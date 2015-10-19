package org.fiteagle.north.sfa;

import info.openmultinet.ontology.vocabulary.Omn_resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.jena.atlas.web.HttpException;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Startup
@Singleton
public class StartUp {

    private static final Logger LOGGER = Logger.getLogger(StartUp.class
            .getName());

    @javax.annotation.Resource
    private TimerService timerService;

    Model defaultModel;
    private int failureCounter = 0;
    private static String resourceUri = "http://localhost/resource/SFA";

    @PostConstruct
    public void addSfaApi() {
	LOGGER.info("START");
        setDefaultModel();
        timerService.createIntervalTimer(0, 5000, new TimerConfig());
    }

    @PreDestroy
    public void deleteSfaApi() {
	LOGGER.info("STOP");
    }

    @Timeout
    public void timerMethod(Timer timer) {
        if(failureCounter < 100){
            try {
                if (defaultModel == null) {
                    TripletStoreAccessor.addResource(setDefaultModel().getResource(
                            resourceUri));
                    timer.cancel();
                } else {
                    TripletStoreAccessor.addResource(defaultModel
                            .getResource(resourceUri));
                    timer.cancel();
                }
            } catch (ResourceRepositoryException e) {
                LOGGER.log(Level.INFO,
                        "Errored while adding something to Database - will try again");
                failureCounter++;
            } catch (HttpException e) {
                LOGGER.log(Level.INFO,
                        "Couldn't find RDF Database - will try again");
                failureCounter++;
            }
        }else{
            LOGGER.log(Level.SEVERE,
                    "Tried to add something to Database several times, but failed. Please check the OpenRDF-Database");
            timer.cancel();
        }

    }

    private Model setDefaultModel() {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model
                .createResource(resourceUri);
        resource.addProperty(Omn_resource.hasInterface, "/sfa/api/am/v3");
        resource.addProperty(Omn_resource.hasInterface, "/sfa/api/sa/v1");
        defaultModel = model;

        return model;
    }
}
