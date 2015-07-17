package org.fiteagle.north.sfa;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn_resource;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.api.core.TimerHelper;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Startup
@Singleton
public class StartUp {

	Model defaultModel;

	@PostConstruct
	public void addSfaApi() {
		setDefaultModel();
		TimerHelper timer = new TimerHelper(new SfaAPI());
	}
	
//	@PreDestroy
//	public void deleteNativeApi() {
//		TimerHelper timer = new TimerHelper(new DeleteSfaAPI());
//	}

	private Model setDefaultModel() {
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model
				.createResource("http://localhost/resource/SFA");
		resource.addProperty(Omn_resource.hasInterface, "/sfa/api/am/v3");
		resource.addProperty(Omn_resource.hasInterface, "/sfa/api/sa/v1");
		defaultModel = model;

		return model;
	}

	final class DeleteSfaAPI implements Callable<Void> {

		@Override
		public Void call() throws ResourceRepositoryException, InvalidModelException {
			if (defaultModel == null) {
				TripletStoreAccessor.deleteModel(setDefaultModel());
			} else {
				TripletStoreAccessor.deleteModel(defaultModel);
			}
			return null;
		}
	}
	
	class SfaAPI implements Callable<Void> {

		@Override
		public Void call() throws ResourceRepositoryException {
			if (defaultModel == null) {
				TripletStoreAccessor.addResource(setDefaultModel().getResource(
						"http://localhost/resource/SFA"));
			} else {
				TripletStoreAccessor.addResource(defaultModel
						.getResource("http://localhost/resource/SFA"));
			}
			return null;
		}
}
}
