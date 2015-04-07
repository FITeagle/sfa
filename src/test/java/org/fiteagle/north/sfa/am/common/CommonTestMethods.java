package org.fiteagle.north.sfa.am.common;

import info.openmultinet.ontology.vocabulary.Omn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.provision.ProcessProvision;
import org.junit.Before;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class CommonTestMethods {
  
  protected List<Object> parameter;
  protected List<String> urns;
  protected String test_urn;
  protected List<Map<String, ?>> credentials;
  protected SFA_AM_MDBSender sender;
  
  @Before
  public void initialize() {
    parameter = new LinkedList<>();
    urns = new ArrayList<>();
    test_urn = "urn:publicid:IDN+localhost+sliver+http%3A%2F%2Flocalhost%2Fresource%2Fsliver1";
    credentials = new LinkedList<>();
    sender = EasyMock.createMock(SFA_AM_MDBSender.class);
  }
  
  protected void prepareTest(){
    Model returnModel = ModelFactory.createDefaultModel();
    Resource resource = returnModel.createResource("http://test");
    resource.addProperty(RDF.type, Omn.Resource);
    
    EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(returnModel);
    EasyMock.replay(sender);
  }
  
  protected void prepareParameters(){
    urns.add(test_urn);
    parameter.add(urns);
    parameter.add(credentials);
  }
  
  public Model createTestModel() {
    Model returnModel = ModelFactory.createDefaultModel();
    Resource resource = returnModel.createResource("http://test");
    resource.addProperty(RDF.type, Omn.Resource);
    return returnModel;
  }
}
