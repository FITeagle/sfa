package org.fiteagle.north.sfa.am.describe;

import static org.junit.Assert.assertFalse;
import info.openmultinet.ontology.exceptions.InvalidModelException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.fiteagle.north.sfa.am.common.CommonTestMethods;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.ForbiddenException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class DescribeProcessorTest extends CommonTestMethods{
  
  DescribeProcessor describeProcessor;
  
  @Test(expected = BadArgumentsException.class)
  public void testDescribeEmptyURNList() throws Exception {
      parameter.add(urns);
      parameter.add(credentials);
      describeProcessor = new DescribeProcessor(parameter);
      describeProcessor.parseURNList();
  }
  
  @Test( expected = BadArgumentsException.class)
  public void testDescribeEmptyCredentials(){
    prepareParameters();
    describeProcessor = new DescribeProcessor(parameter);
    describeProcessor.handleCredentials(1);
  }
  
  @Test( expected = ForbiddenException.class)
  public void testDescribeBadCredentials(){
    urns.add(test_urn);
    parameter.add(urns);
    Map<String, Object> map = new HashMap<>();
    credentials.add(map);
    parameter.add(credentials);
    describeProcessor = new DescribeProcessor(parameter);
    describeProcessor.handleCredentials(1);
  }
  
  @Test
  public void testSliverNotFound() throws UnsupportedEncodingException {
      prepareParameters();
      prepareTest();
      describeProcessor = new DescribeProcessor(parameter);
      describeProcessor.setSender(sender);
      describeProcessor.parseURNList();
      Model result = describeProcessor.getDescriptions();
      assertFalse(result.isEmpty());
  }
  
  @Test (expected = SearchFailedException.class)
  public void testCreateResponse() throws UnsupportedEncodingException, JAXBException, InvalidModelException{
    Model returnModel = createTestModel();
    prepareParameters();
    describeProcessor = new DescribeProcessor(parameter);
    describeProcessor.parseURNList();
    HashMap<String, Object> result = new HashMap<>();
    describeProcessor.createResponse(result, returnModel);
  }
}
