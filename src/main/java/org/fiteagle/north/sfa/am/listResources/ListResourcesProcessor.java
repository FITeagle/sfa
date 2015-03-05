package org.fiteagle.north.sfa.am.listResources;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;

import javax.jms.JMSException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dne on 03.03.15.
 */
public class ListResourcesProcessor {
    private final List<?> parameter;

    public ListResourcesProcessor(List<?> parameter) {
        this.parameter = parameter;
    }

    public Model listResources() throws UnsupportedEncodingException, JMSException {
        Model resourcesResult = getResources();
        Model topologyModel = ModelFactory.createDefaultModel();
        topologyModel.setNsPrefixes(resourcesResult.getNsPrefixMap());
        
        Resource topology = topologyModel.createResource(AnonId.create());
        topology.addProperty(RDF.type, Omn_lifecycle.Offering);
        topology.addProperty(RDF.type, Omn.Topology);
        ResIterator resIterator = resourcesResult.listSubjects();
        while(resIterator.hasNext()){
            Resource resource = resIterator.nextResource();
            topology.addProperty(Omn.hasResource, resource);
            resource.addProperty(Omn.isResourceOf, topology);
            topologyModel.add(resource.getModel());
        }

        return topologyModel;


    }

    private Model getResources() throws JMSException, UnsupportedEncodingException {
        Model requestModel = ModelFactory.createDefaultModel();
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        Model model = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
       return  model;


    }



}
