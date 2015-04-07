package org.fiteagle.north.sfa.am.describe;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import org.apache.commons.codec.binary.Base64;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.vocabulary.Omn;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import javax.xml.bind.JAXBException;

/**
 * Created by dne on 12.01.15.
 */
public class DescribeProcessor extends AbstractMethodProcessor {
    private final static Logger LOGGER = Logger.getLogger(DescribeProcessor.class.getName());

    public DescribeProcessor(final List<?> parameter) {
    this.parameter = parameter;
    }
    
    public Model getDescriptions() throws UnsupportedEncodingException {
        Model requestModel = ModelFactory.createDefaultModel();
        for(URN u : this.urns){

            if(ISFA_AM.SLICE.equals(u.getType())){
                Resource resource = requestModel.createResource(IConfig.TOPOLOGY_NAMESPACE_VALUE+ u.getSubject());
                resource.addProperty(RDF.type, Omn.Topology);
            }
            if(ISFA_AM.Sliver.equals(u.getType())){
                Resource resource =requestModel.createResource(URLDecoder.decode(u.getSubject(), ISFA_AM.UTF_8));
                resource.addProperty(RDF.type, Omn.Resource);

            }
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send getValue request ...");
        Model resultModel = getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);

        return resultModel;
    }

    public void parseDescribeOptions() {
      HashMap<String, Object> optionsMap = (HashMap<String, Object>) this.parameter.get(2);

      boolean compressed = (boolean) optionsMap.get(IGeni.GENI_COMPRESSED);
      if (compressed)
          this.delegate.setCompressed(true);

      HashMap<String, Object> geni_rspec_version = (HashMap<String, Object>) optionsMap.get(IGeni.GENI_RSPEC_VERSION);
      String geni_rspec_version_type = (String) geni_rspec_version.get(ISFA_AM.TYPE);
      String geni_rspec_version_version = (String) geni_rspec_version.get(ISFA_AM.VERSION);

  }
    public void createResponse(final HashMap<String, Object> result, Model descriptions) throws JAXBException, InvalidModelException, UnsupportedEncodingException{
      HashMap<String, Object> value = new HashMap<>();
      StmtIterator stmtIterator = descriptions.listStatements(null, RDF.type, Omn.Reservation);
      if(stmtIterator.hasNext() || !(this.urns.size() ==1 && ISFA_AM.SLICE.equalsIgnoreCase(this.urns.get(0).getType()) ))
        addSliverInformation(value,descriptions);

      value.put(IGeni.GENI_RSPEC, ManifestConverter.getRSpec(descriptions, IConfig.DEFAULT_HOSTNAME));
      if (this.delegate.getCompressed())
          value.put(IGeni.GENI_RSPEC, compress((String) value.get(IGeni.GENI_RSPEC)));
      result.put(ISFA_AM.VALUE, value);
      this.addCode(result);
      this.addOutput(result);
    }

}
