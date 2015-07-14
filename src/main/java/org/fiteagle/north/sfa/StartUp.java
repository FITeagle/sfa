package org.fiteagle.north.sfa;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn_component;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_resource;
import info.openmultinet.ontology.vocabulary.Omn_service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;
import org.fiteagle.core.tripletStoreAccessor.TripletStoreAccessor;
import org.fiteagle.core.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;


@Startup
@Singleton
public class StartUp {

private Model defaultModel;	
	
	@PostConstruct
    public void addSfaApi(){
    	Model model = ModelFactory.createDefaultModel();
    Resource resource = model.createResource("http://localhost/resource/SFA");
    resource.addProperty(Omn_resource.hasInterface, "/sfa/api/am/v3");
    resource.addProperty(Omn_resource.hasInterface, "/sfa/api/sa/v1");
    try {
		TripletStoreAccessor.addResource(resource);
		defaultModel = model;
	} catch (ResourceRepositoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
	
	
	@PreDestroy
	public void deleteSfaApi() {
		try{	
	    TripletStoreAccessor.deleteModel(defaultModel);
		} catch (ResourceRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
