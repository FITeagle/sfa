package org.fiteagle.north.sfa.am.delete;


import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertFalse;
import org.fiteagle.north.sfa.am.common.CommonTestMethods;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

public class ProcessDeleteTest extends CommonTestMethods{
  
  ProcessDelete processDelete;
  
  @Test
  public void testDeleteInstances() throws UnsupportedEncodingException{
    this.prepareTest();
    this.prepareParameters();

    processDelete = new ProcessDelete(parameter);
    processDelete.setSender(sender);
    processDelete.parseURNList();
    Model model = processDelete.deleteInstances();
    assertFalse(model.isEmpty());
  }
}
