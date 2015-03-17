package org.fiteagle.north.sfa.am.performOperationalAction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import org.apache.jena.riot.RiotException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by dne on 12.03.15.
 */
public class PerformOperationalActionHandler extends AbstractMethodProcessor {

    private final List<URN> urns;


    private SFA_AM_MDBSender sender;

    public PerformOperationalActionHandler(List<URN> urns) {
        this.urns = urns;
    }


    public Model performAction(String action) throws UnsupportedEncodingException {
        Model model = ModelFactory.createDefaultModel();

        if (this.urns == null || action == null) {
            throw new IllegalArgumentException("illegal arguments");
        }
        for(URN urn : urns){
            Resource resource = model.createResource(URLDecoder.decode(urn.getSubject(), "UTF-8"));
            resource.addProperty(RDF.type, Omn.Resource);
            switch (action) {
                case "geni_start":
                    System.out.println("start");
                    resource.addProperty(Omn_lifecycle.hasState,Omn_lifecycle.Ready);
                    break;
                case "geni_restart":
                    System.out.println("restart");
                    resource.addProperty(Omn_lifecycle.hasState,Omn_lifecycle.Ready);
                    break;
                case "geni_stop":
                    System.out.println("stop");
                    resource.addProperty(Omn_lifecycle.hasState,Omn_lifecycle.Stopping);
                    break;
                default:
                    try {
                        InputStream is = new ByteArrayInputStream(action.getBytes() );
                        model.read(is,null, IMessageBus.SERIALIZATION_TURTLE);
                        model.add(resource.listProperties());
                    }catch(RiotException e){
                        throw new IllegalArgumentException("illegal action");
                    }
                    break;
            }


        }
        String serializedModel = MessageUtil.serializeModel(model,
                IMessageBus.SERIALIZATION_TURTLE);

        Model performOpActionResponse = getSender()
                .sendRDFRequest(serializedModel, IMessageBus.TYPE_CONFIGURE,
                        IMessageBus.TARGET_ORCHESTRATOR);

        return performOpActionResponse;
    }


    public SFA_AM_MDBSender getSender() {
        return sender;
    }

    public void setSender(SFA_AM_MDBSender sender) {
        this.sender = sender;
    }


}
