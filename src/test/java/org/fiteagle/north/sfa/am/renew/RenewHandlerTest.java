package org.fiteagle.north.sfa.am.renew;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Omn;

import org.easymock.EasyMock;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;
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

public class RenewHandlerTest extends CommonTestMethods{

    RenewHandler handler;
    String expirationDate;
 
    @Before
    public void setUp() throws Exception {

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date date = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        expirationDate = dateFormat.format(date);

    }

    @Test(expected = BadArgumentsException.class)
    public void testRenewEmptyList() throws Exception {
        parameter.add(urns);
        parameter.add(credentials);
        parameter.add(expirationDate);
        handler = new RenewHandler(parameter);
        handler.parseURNList();
        Model result =  handler.renew();
    }
    
    @Test(expected = BadArgumentsException.class)
    public void testRenewNullList() throws Exception {
        parameter.add(urns);
        handler = new RenewHandler(parameter);
        handler.parseURNList();
        Model result = handler.renew();
    }
    
    @Test(expected = BadArgumentsException.class)
    public void testRenewNullDate() throws UnsupportedEncodingException {
        prepareParameters();
        expirationDate = null;
        parameter.add(expirationDate);
        handler = new RenewHandler(parameter);
        handler.parseExpirationTime();
        Model result = handler.renew();
    }

//    private List<URN> getSingleSliverList() {
//        List<URN> urnList =  new LinkedList<>();
//        urnList.add(sliver1);
//        return urnList;
//    }

    @Test(expected = BadArgumentsException.class)
    public void testRenewEmptyDate() throws UnsupportedEncodingException {
        prepareParameters();
        expirationDate = "";
        parameter.add(expirationDate);
        handler = new RenewHandler(parameter);
        handler.parseExpirationTime();
        Model result = handler.renew();
    }
    
    
    @Test(expected = BadArgumentsException.class)
    public void testRenewBadDate() throws UnsupportedEncodingException {
      prepareParameters();
      expirationDate = "fafcafcmafhac";
      parameter.add(expirationDate);
      handler = new RenewHandler(parameter);
      handler.parseExpirationTime();
      Model result = handler.renew();
      
    }

    @Test(expected = BadArgumentsException.class)
    public void testRenewOldDate() throws UnsupportedEncodingException {
        prepareParameters();
        expirationDate = "2015-03-26T18:45:06Z";
        parameter.add(expirationDate);
        handler = new RenewHandler(parameter);
        handler.parseExpirationTime();
        Model result = handler.renew();
    }

    @Test(expected = SearchFailedException.class)
    public void testSliverNotFound() throws UnsupportedEncodingException {
        prepareParameters();
        parameter.add(expirationDate);
        Model model = ModelFactory.createDefaultModel();
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(model);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(sender);
        
        handler = new RenewHandler(parameter);
        handler.setSender(sender);
        handler.parseURNList();
        handler.parseExpirationTime();
        Model result = handler.renew();
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

        prepareParameters();
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource(test_urn);
        resource.addProperty(RDF.type, Omn.Resource);
        Map<String, Object> optionsMap = new HashMap<>();
        optionsMap.put("geni_best_effort",false);
        parameter.add(expirationDate);
        parameter.add(optionsMap);
        EasyMock.expect(sender.sendRDFRequest(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(model);
        EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(sender);
        handler = new RenewHandler(parameter);
        handler.setSender(sender);
        handler.parseURNList();
        handler.parseExpirationTime();
        Model result = handler.renew();
        Assert.assertTrue(!result.isEmpty());

    }
}