package org.fiteagle.north.sfa.am.renew;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import info.openmultinet.ontology.vocabulary.Omn;
import org.easymock.EasyMock;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.util.URN;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Message;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class RenewHandlerTest {
    URN sliver1;
    URN sliver2;
    SFA_AM_MDBSender sender;
    RenewHandler handler;
    String expirationDate;
    @Before
    public void setUp() throws Exception {

        handler = new RenewHandler();
        sender = EasyMock.createMock(SFA_AM_MDBSender.class);
        handler.setSender(sender);
        sliver1 = new URN("urn:publicid:IDN+localhost+sliver+http%3A%2F%2Flocalhost%2Fresource%2Fsliver1");
        sliver2 = new URN("urn:publicid:IDN+localhost+sliver+http%3A%2F%2Flocalhost%2Fresource%2Fsliver1");
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date date = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        expirationDate = dateFormat.format(date);

    }

    @Test(expected = BadArgumentsException.class)
    public void testRenewEmptyList() throws Exception {
        List<URN> urnList = new LinkedList<>();

       Model result =  handler.renew(urnList, expirationDate, null);
    }
    @Test(expected = BadArgumentsException.class)
    public void testRenewNullList() throws Exception {
        Model result = handler.renew(null, expirationDate, null);
    }
    @Test(expected = BadArgumentsException.class)
    public void testRenewNullDate() throws UnsupportedEncodingException {
        List<URN> urnList = getSingleSliverList();
        Model result = handler.renew(urnList,null, null);
    }

    private List<URN> getSingleSliverList() {
        List<URN> urnList =  new LinkedList<>();
        urnList.add(sliver1);
        return urnList;
    }

    @Test(expected = BadArgumentsException.class)
    public void testRenewBadDate() throws UnsupportedEncodingException {
        List<URN> urnList = getSingleSliverList();
        expirationDate = "fafcafcmafhac";
        Model result = handler.renew(urnList,expirationDate, null);
    }

    @Test(expected = BadArgumentsException.class)
    public void testRenewOldDate() throws UnsupportedEncodingException {
        List<URN> urnList = getSingleSliverList();
        expirationDate = "2015-03-26T18:45:06Z";
        Model result = handler.renew(urnList,expirationDate, null);
    }

    @Test(expected = SearchFailedException.class)
    public void testSliverNotFound() throws UnsupportedEncodingException {
        List<URN> urnList = getSingleSliverList();
        Model model = ModelFactory.createDefaultModel();
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(model);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(sender);
        Model result = handler.renew(urnList,expirationDate, null);
    }

  /*  @Test(expected = SearchFailedException.class)
    public void testOneSliverNotFoundBestEffortFalse() throws UnsupportedEncodingException {
        List<URN> urnList = getSingleSliverList();
        urnList.add(sliver2);
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(sliver1.toString());
        resource.addProperty(RDF.type, Omn.Resource);
        Resource topo1 = model.createResource("http://localhost/resources/topology/topo1");
        topo1.addProperty(Omn.hasResource, resource);
        Map<String, Object> optionsMap = new HashMap<>();
        optionsMap.put("geni_best_effort",false);
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(model);
        EasyMock.replay(sender);
        Model result = handler.renew(urnList,expirationDate, optionsMap);
    }*/

    @Test
    public void testSingleExistingSliver() throws UnsupportedEncodingException {
        List<URN> urnList = getSingleSliverList();
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(sliver1.toString());
        resource.addProperty(RDF.type, Omn.Resource);
        Map<String, Object> optionsMap = new HashMap<>();
        optionsMap.put("geni_best_effort",false);
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(model);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(sender);
        Model result = handler.renew(urnList,expirationDate,optionsMap);
        Assert.assertTrue(!result.isEmpty());

    }
}