package org.fiteagle.north.sfa.am.status;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.fiteagle.api.core.*;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 11.02.15.
 */
public class StatusProcessor extends AbstractMethodProcessor {
  
  
  public StatusProcessor(final List<?> parameter) {
    this.parameter = parameter;
  }
  
    private final static Logger LOGGER = Logger.getLogger(StatusProcessor.class.getName());
    
    public Model getStates() throws UnsupportedEncodingException {
        LOGGER.log(Level.INFO, "create status model ");
        Model requestModel = ModelFactory.createDefaultModel();
        for (URN urn : this.urns) {
            if(ISFA_AM.Sliver.equals(urn.getType())){
                Resource resource = requestModel.createResource(URLDecoder.decode(urn.getSubject(), ISFA_AM.UTF_8));
                resource.addProperty(RDF.type,Omn.Resource);
                requestModel.add(resource.listProperties());
            }else {
                Resource topology = requestModel.createResource("http://" + urn.getDomain() + "/topology/" + urn.getSubject());
                topology.addProperty(RDF.type, Omn.Topology);
                requestModel.add(topology.listProperties());
            }
        }

        String serializedModel = MessageUtil.serializeModel(requestModel,
                IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send status request ...");
        Model statusResponse = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET,IMessageBus.TARGET_ORCHESTRATOR);
        LOGGER.log(Level.INFO,"status reply received.");
        return statusResponse;
    }
    
  @Override
  public void addSliverInformation(Map<String, Object> value, Model response) {
    
    final List<Map<String, Object>> geniSlivers;
    if (response.isEmpty()) {
      throw new SearchFailedException("Resource not found");
    } else {
      StmtIterator stmtIterator = response.listStatements(null, RDF.type, Omn.Reservation);
      if (stmtIterator.hasNext()) {
        geniSlivers = getSlivers(response);
        value.put(IGeni.GENI_URN, this.getSliceURN(response));
      } else {
        geniSlivers = createEmptySliversList();
        value.put(IGeni.GENI_URN, this.getURN(response));
      }
      
      value.put(IGeni.GENI_SLIVERS, geniSlivers);
    }
    
  }
    
    private String getURN(Model response){
      StmtIterator stmtIterator = response.listStatements(new SimpleSelector(null,RDF.type, (Object)null));
      URN urn = null;
      if(stmtIterator.hasNext()){
      Resource topology = stmtIterator.nextStatement().getSubject();

      String uri = topology.getURI();
      String localname = "";
      String hostname = "";
      try {
          URL url = new URL(uri);
          localname = url.getPath().substring( url.getPath().lastIndexOf('/') + 1 );
          hostname = url.getHost();

      } catch (MalformedURLException e) {
          e.printStackTrace();
      }
      
      urn = new URN("urn:publicid:IDN+"+hostname+"+slice+"+localname);
      }
      return urn.toString();
      
    }
    
    
    
    private List<Map<String, Object>> createEmptySliversList(){
      
      final List<Map<String, Object>> geniSlivers = new LinkedList<>();

      return geniSlivers;
    }
  

    public void createResponse(final HashMap<String, Object> result, Model statusResponse) {
      HashMap<String, Object> value = new HashMap<>();
      addSliverInformation(value, statusResponse);
      result.put(ISFA_AM.VALUE, value);
      this.addCode(result);
      this.addOutput(result);
    }
    
}
