package org.fiteagle.north.sfa.am.renew;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Omn;

import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.ISFA;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.URN;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dne on 27.03.15.
 */
public class RenewHandler extends AbstractMethodProcessor {

  private String expirationTime;

    public RenewHandler(List<?> parameter) {
      this.parameter = parameter;
    }
    
    public Model renew() throws UnsupportedEncodingException {
        Map<String, Object> options = new HashMap<>();
        if(this.urns == null || this.urns.isEmpty()){
            throw  new BadArgumentsException("No URN provided");
        }
        
        ParsePosition parsePos = new ParsePosition(0);
        Date expirationDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(this.expirationTime,parsePos);
        if(expirationDate == null || new Date().after(expirationDate)){
            throw new BadArgumentsException("Wrong Expiration Date");
        }

        Model result = getExistingSlivers();

        if(result.isEmpty()){
            throw new SearchFailedException("No resources found for provided sliver URNS");
        }
        Model finalResult = renewSlivers(result);
        return finalResult;

    }

    private Model renewSlivers(Model result) {
        Model requestModel2 = ModelFactory.createDefaultModel();

        ResIterator resIterator = result.listSubjectsWithProperty(Omn.isReservationOf);

        while(resIterator.hasNext()){
            Resource reservation = resIterator.nextResource();

            Statement endTimeStatement = reservation.getProperty(MessageBusOntologyModel.endTime);
            Property endTime = endTimeStatement.getPredicate();

            Statement newEndTime = endTimeStatement.changeObject(this.expirationTime);

            requestModel2.add(newEndTime);
            requestModel2.add(reservation,RDF.type,Omn.Reservation);
            requestModel2.add(endTime, RDF.type, OWL.FunctionalProperty);


        }

        String serializedModel2 = MessageUtil.serializeModel(requestModel2, IMessageBus.SERIALIZATION_TURTLE);
        return getSender().sendRDFRequest(serializedModel2,IMessageBus.TYPE_CONFIGURE,IMessageBus.TARGET_RESERVATION);
    }

    private Model getExistingSlivers() throws UnsupportedEncodingException {
        Model requestModel = ModelFactory.createDefaultModel();

        for(URN urn:this.urns){
            if(ISFA_AM.Sliver.equals(urn.getType())){
                Resource resource = requestModel.createResource(URLDecoder.decode(urn.getSubject(), ISFA_AM.UTF_8));
                resource.addProperty(RDF.type, Omn.Resource);

            }else if(ISFA_AM.SLICE.equals(urn.getType())){
                Individual topology = Omn.Topology.createIndividual("http://" + urn.getDomain() + "/topology/" + urn.getSubject());

               requestModel.add(topology.getModel());
            }
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        return getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);
    }
    
    public void parseExpirationTime(){
//      if(parameter.get(2) == null){
//        throw new BadArgumentsException("No expiration date provided");
//      }
      this.expirationTime = (String) this.parameter.get(2);
      if(expirationTime == null || expirationTime.isEmpty()){
        throw new BadArgumentsException("No expiration date provided");
      }
    }
    
    public void createResponse(final HashMap<String, Object> result, Model renewResponse) {
      List<Map<String, Object>> slivers = getSlivers(renewResponse);
      result.put(ISFA_AM.VALUE,slivers );
      this.addCode(result);
      this.addOutput(result);
    }
}
