package org.fiteagle.north.sfa.am.performOperationalAction;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.apache.jena.riot.RiotException;
import org.bouncycastle.crypto.engines.ISAACEngine;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.ISFA;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.GENI_Credential;
import org.fiteagle.north.sfa.util.URN;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dne on 12.03.15.
 */
public class PerformOperationalActionHandler extends AbstractMethodProcessor {
  
  String action;
  
  public PerformOperationalActionHandler(final List<?> parameter) {
      this.parameter = parameter;
    }


    public Model performAction() throws UnsupportedEncodingException {
        Model model = ModelFactory.createDefaultModel();

        if (this.urns == null || this.action == null) {
            throw new IllegalArgumentException("illegal arguments");
        }
        for(URN urn : urns) {
            if (ISFA_AM.Sliver.equals(urn.getType())) {
                Resource resource = model.createResource(URLDecoder.decode(urn.getSubject(), ISFA_AM.UTF_8));
                resource.addProperty(RDF.type, Omn.Resource);
                switch (action) {
                    case IGeni.GENI_STATRT:
                        System.out.println("start");
                        resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
                        break;
                    case IGeni.GENI_RESTART:
                        System.out.println("restart");
                        resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
                        break;
                    case IGeni.GENI_STOP:
                        System.out.println("stop");
                        resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Stopping);
                        break;
                    default:
                        try {
                            InputStream is = new ByteArrayInputStream(action.getBytes());
                            model.read(is, null, IMessageBus.SERIALIZATION_TURTLE);
                            model.add(resource.listProperties());
                        } catch (RiotException e) {
                            throw new IllegalArgumentException("illegal action");
                        }
                        break;
                }


            }else if(ISFA_AM.SLICE.equals(urn.getType())){
                Resource topology = model.createResource("http://" + urn.getDomain() + "/topology/" + urn.getSubject());
                topology.addProperty(RDF.type, Omn.Topology);
                switch (action) {
                    case IGeni.GENI_STATRT:
                        System.out.println("start");
                        topology.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
                        break;
                    case IGeni.GENI_RESTART:
                        System.out.println("restart");
                        topology.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
                        break;
                    case IGeni.GENI_STOP:
                        System.out.println("stop");
                        topology.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Stopping);
                        break;
                    default:
                        try {
                            InputStream is = new ByteArrayInputStream(action.getBytes());
                            model.read(is, null, IMessageBus.SERIALIZATION_TURTLE);
                            model.add(topology.listProperties());
                        } catch (RiotException e) {
                            throw new IllegalArgumentException("illegal action");
                        }
                        break;
                }
                model.add(topology.getModel());

            }
        }
        String serializedModel = MessageUtil.serializeModel(model,
                IMessageBus.SERIALIZATION_TURTLE);

        Model performOpActionResponse =this.getSender().sendRDFRequest(serializedModel, IMessageBus.TYPE_CONFIGURE,
                        IMessageBus.TARGET_ORCHESTRATOR);

        return performOpActionResponse;
    }

    public void createResponse(final HashMap<String, Object> result, Model performResponse) {
      HashMap<String, Object> value = new HashMap<>();
      addSliverInformation(value, performResponse);
      result.put(ISFA_AM.VALUE, value);
      this.addCode(result);
      this.addOutput(result);
    }
    
    public void parseAction(){
      if(parameter.get(2) == null){
        throw new IllegalArgumentException("action parameter shouldn't be null");
      }
      this.action = (String) parameter.get(2);
    }

}
