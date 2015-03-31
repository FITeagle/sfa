package org.fiteagle.north.sfa.am.performOperationalAction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_resource;

import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.util.URN;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PerformOperationalActionHandlerTest extends CommonTestMethods{

    PerformOperationalActionHandler handler;

  @Test(expected = BadArgumentsException.class)
  public void testEmptyList() throws UnsupportedEncodingException {
    
    parameter.add(urns);
    parameter.add(credentials);
    parameter.add("start");
    handler = new PerformOperationalActionHandler(parameter);
    handler.parseURNList();
    handler.parseAction();
    Model model = handler.performAction();
    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testNullAction() throws UnsupportedEncodingException {
    
    this.prepareParameters();
    parameter.add(null);
    handler = new PerformOperationalActionHandler(parameter);
    handler.parseAction();
    Model model = handler.performAction();
    
  }
  
  @Test
  public void testEmptyAction() throws UnsupportedEncodingException {
    EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),EasyMock.anyObject(String.class))).andReturn(ModelFactory.createDefaultModel());
    EasyMock.replay(sender);
    this.prepareParameters();
    parameter.add("");
    handler = new PerformOperationalActionHandler(parameter);
    handler.parseURNList();
    handler.parseAction();
    handler.setSender(sender);
    Model model = handler.performAction();
    assertTrue(model.isEmpty());
  }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformNonTTLencodedActionOnSingleSliver() throws UnsupportedEncodingException {
        this.prepareTest();
        this.prepareParameters();
        parameter.add("fsddsf"); // action
        handler = new PerformOperationalActionHandler(parameter);
        handler.setSender(sender);
        handler.parseURNList();
        handler.parseAction();
        Model model = handler.performAction();
        assertFalse(model.isEmpty());
    }

    @Test
    public void testPerformSingleSliverAction() throws UnsupportedEncodingException {
        this.prepareTest();
        this.prepareParameters();
        //action
        parameter.add("<http://localhost/resource/motor-1> <http://open-multinet.info/ontology/omn#Status> <http://open-multinet.info/ontology/omn#Started>.");
        handler = new PerformOperationalActionHandler(parameter);
        handler.setSender(sender);
        handler.parseURNList();
        handler.parseAction();
        Model model = handler.performAction();
        assertFalse(model.isEmpty());
    }
    
    @Test (expected = SearchFailedException.class)
    public void testAddSliverInformation() throws UnsupportedEncodingException {
        Model returnModel = createSingleSliverReturnModel();
        this.prepareParameters();
        handler = new PerformOperationalActionHandler(parameter);
        HashMap<String, Object> value = new HashMap<>();
        handler.addSliverInformation(value, returnModel);
    }

    @Test
    public void testPerformSingleSliverActionGENISTART() throws UnsupportedEncodingException {
        this.prepareTest();
        this.prepareParameters();
        parameter.add("geni_start");
        handler = new PerformOperationalActionHandler(parameter);
        handler.setSender(sender);
        handler.parseURNList();
        handler.parseAction();
        Model model = handler.performAction();
        assertFalse(model.isEmpty());
    }

    private Model createSingleSliverReturnModel() {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://foo#bar");
        resource.addProperty(RDF.type, Omn.Resource);
        return model;
    }
}