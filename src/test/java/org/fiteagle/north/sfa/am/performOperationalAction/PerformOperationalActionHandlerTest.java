package org.fiteagle.north.sfa.am.performOperationalAction;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_resource;
import org.easymock.EasyMock;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.util.URN;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PerformOperationalActionHandlerTest {

    PerformOperationalActionHandler handler;
    List<URN> urnList;
    URN sliver1;
    URN sliver2;
    SFA_AM_MDBSender sender;
    @Before
    public void init(){
        urnList = new LinkedList<>();
        sliver1 = new URN("urn:publicid:IDN+localhost+sliver+sliver1");
        sliver2 = new URN("urn:publicid:IDN+localhost+sliver+sliver2");
        sender = EasyMock.createMock(SFA_AM_MDBSender.class);


    }

    @Test
    public void testEmptyList() throws UnsupportedEncodingException {
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(ModelFactory.createDefaultModel());
        EasyMock.replay(sender);

        handler = new PerformOperationalActionHandler(urnList);
        handler.setSender(sender);
        Model model = handler.performAction("start");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAction() throws UnsupportedEncodingException {
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(ModelFactory.createDefaultModel());
        EasyMock.replay(sender);

        handler = new PerformOperationalActionHandler(urnList);
        handler.setSender(sender);
        Model model = handler.performAction(null);

    }

    @Test
    public void testEmptyAction() throws UnsupportedEncodingException {
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(ModelFactory.createDefaultModel());
        EasyMock.replay(sender);

        handler = new PerformOperationalActionHandler(urnList);
        handler.setSender(sender);
        Model model = handler.performAction("");
        assertTrue(model.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPerformNonTTLencodedActionOnSingleSliver() throws UnsupportedEncodingException {
        Model returnModel = createSingleSliverReturnModel();
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(returnModel);
        EasyMock.replay(sender);
        urnList.add(sliver1);
        handler = new PerformOperationalActionHandler(urnList);
        handler.setSender(sender);
        Model model = handler.performAction("fsddsf");
        assertFalse(model.isEmpty());
    }

    @Test
    public void testPerformSingleSliverAction() throws UnsupportedEncodingException {
        Model returnModel = createSingleSliverReturnModel();
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(returnModel);
        EasyMock.replay(sender);
        urnList.add(sliver1);
        handler = new PerformOperationalActionHandler(urnList);
        handler.setSender(sender);
        Model model = handler.performAction("<http://localhost/resource/motor-1> <http://open-multinet.info/ontology/omn#Status> <http://open-multinet.info/ontology/omn#Started>.");
        assertFalse(model.isEmpty());
    }

    @Test
    public void testPerformSingleSliverActionGENISTART() throws UnsupportedEncodingException {
        Model returnModel = createSingleSliverReturnModel();
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(returnModel);
        EasyMock.replay(sender);
        urnList.add(sliver1);
        handler = new PerformOperationalActionHandler(urnList);
        handler.setSender(sender);
        Model model = handler.performAction("geni_start");
        assertFalse(model.isEmpty());
    }

    private Model createSingleSliverReturnModel() {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://foo#bar");
        resource.addProperty(RDF.type, Omn.Resource);
        return model;
    }
}