package org.fiteagle.north.sfa.am.describe;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import info.openmultinet.ontology.vocabulary.Omn;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 12.01.15.
 */
public class DescribeProcessor extends AbstractMethodProcessor {
    private final static Logger LOGGER = Logger.getLogger(DescribeProcessor.class.getName());

    public Model getDescriptions(List<URN> urns) throws UnsupportedEncodingException {
        Model requestModel = ModelFactory.createDefaultModel();
        for(URN u : urns){

            if(ISFA_AM.SLICE.equals(u.getType())){
                Resource resource = requestModel.createResource(IConfig.TOPOLOGY_NAMESPACE_VALUE+ u.getSubject());
                resource.addProperty(RDF.type, Omn.Topology);
            }
            if(ISFA_AM.Sliver.equals(u.getType())){
                Resource resource =requestModel.createResource(URLDecoder.decode(u.getSubject(), "UTF-8"));
                resource.addProperty(RDF.type, Omn.Resource);

            }
        }
        String serializedModel = MessageUtil.serializeModel(requestModel, IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.INFO, "send getValue request ...");
        Model resultModel = SFA_AM_MDBSender.getInstance().sendRDFRequest(serializedModel, IMessageBus.TYPE_GET, IMessageBus.TARGET_RESERVATION);

        return resultModel;
    }


}
