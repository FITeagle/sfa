package org.fiteagle.north.sfa.am.delete;

import info.openmultinet.ontology.vocabulary.Omn;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessDeleteTest {
  
  ProcessDelete processDelete;
  List<URN> urns;
  URN test_urn;
  SFA_AM_MDBSender sender;
  
  @Before
  public void initialize() {
    urns = new LinkedList<>();
    test_urn = new URN("urn:publicid:IDN+localhost+sliver+test");
    sender = EasyMock.createMock(SFA_AM_MDBSender.class);
  }
  
  @Test
  public void testProvisionInstances() throws UnsupportedEncodingException{
    Model returnModel = ModelFactory.createDefaultModel();
    Resource resource = returnModel.createResource("http://test");
    resource.addProperty(RDF.type, Omn.Resource);
    
    EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(returnModel);
    EasyMock.replay(sender);
    urns.add(test_urn);
    processDelete = new ProcessDelete(urns);
    processDelete.setSender(sender);
    Model model = processDelete.deleteInstances();
    assertFalse(model.isEmpty());
  }
}
