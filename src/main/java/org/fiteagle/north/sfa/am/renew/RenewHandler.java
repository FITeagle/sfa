package org.fiteagle.north.sfa.am.renew;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import info.openmultinet.ontology.vocabulary.Omn;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
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
import java.util.List;
import java.util.Map;

/**
 * Created by dne on 27.03.15.
 */
public class RenewHandler extends AbstractMethodProcessor {


    public Model renew(List<URN> urnList, String expirationTime, Map<String, Object> options) throws UnsupportedEncodingException {
        if(urnList == null || urnList.isEmpty()){
            throw  new BadArgumentsException("No URN provided");
        }
        if(expirationTime == null){
            throw  new BadArgumentsException("No expiration date provided");
        }
        ParsePosition parsePos = new ParsePosition(0);
        Date expirationDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(expirationTime,parsePos);
        if(expirationDate == null || new Date().after(expirationDate)){
            throw new BadArgumentsException("Wrong Expiration Date");
        }

        Model result = getExistingSlivers(urnList);

        if(result.isEmpty()){
            throw new SearchFailedException("No resources found for provided sliver URNS");
        }
        Model finalResult = renewSlivers(expirationTime, result);
        return finalResult;

    }

    private Model renewSlivers(String expirationTime, Model result) {
        Model requestModel2 = ModelFactory.createDefaultModel();

        ResIterator resIterator = result.listSubjectsWithProperty(Omn.isReservationOf);

        while(resIterator.hasNext()){
            Resource reservation = resIterator.nextResource();

            Statement endTimeStatement = reservation.getProperty(MessageBusOntologyModel.endTime);
            Property endTime = endTimeStatement.getPredicate();

            Statement newEndTime = endTimeStatement.changeObject(expirationTime);

            requestModel2.add(newEndTime);
            requestModel2.add(reservation,RDF.type,Omn.Reservation);
            requestModel2.add(endTime, RDF.type, OWL.FunctionalProperty);


        }

        String serializedModel2 = MessageUtil.serializeModel(requestModel2, IMessageBus.SERIALIZATION_TURTLE);
        return getSender().sendRDFRequest(serializedModel2,IMessageBus.TYPE_CONFIGURE,IMessageBus.TARGET_RESERVATION);
    }

    private Model getExistingSlivers(List<URN> urnList) throws UnsupportedEncodingException {
        Model requestModel = ModelFactory.createDefaultModel();

        for(URN urn:urnList){
            if(urn.getType().equalsIgnoreCase("sliver")){
                Resource resource = requestModel.createResource(URLDecoder.decode(urn.getSubject(), "UTF-8"));
                resource.addProperty(RDF.type, Omn.Resource);

            }else if(urn.getType().equalsIgnoreCase("slice")){
                Resource resource = requestModel.createResource(IConfig.TOPOLOGY_NAMESPACE_VALUE + urn.getSubject());
                resource.addProperty(RDF.type, Omn.Topology);
            }
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        return getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);
    }
}
