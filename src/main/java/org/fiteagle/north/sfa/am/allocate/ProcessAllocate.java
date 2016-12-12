package org.fiteagle.north.sfa.am.allocate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;

import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IGeni;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.OntologyModelUtil;
import org.fiteagle.api.core.TimeHelperMethods;
import org.fiteagle.north.sfa.am.ISFA_AM;
import org.fiteagle.north.sfa.am.ReservationStateEnum;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.util.URN;

import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.exceptions.DeprecatedRspecVersionException;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.InvalidRspecValueException;
import info.openmultinet.ontology.exceptions.MissingRspecElementException;
import info.openmultinet.ontology.translators.dm.DeliveryMechanism;
import info.openmultinet.ontology.translators.geni.ManifestConverter;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN.UnsupportedException;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

public class ProcessAllocate extends AbstractMethodProcessor {


	private Map<String, Object> allocateOptions = new HashMap<>();

	private URN urn;
	private String request;

	public ProcessAllocate(List<?> parameter) {
		this.parameter = parameter;
	}

	public void parseAllocateParameter() throws JAXBException,
			InvalidModelException {


		if (parameter.get(0) == null || parameter.get(2) == null) {
			throw new BadArgumentsException(
					"sliceUrn and rspec fileds must not be null");
		}
		String slice_urn = (String) parameter.get(0);
		this.urn = new URN(slice_urn);

		this.request = (String) parameter.get(2);


		@SuppressWarnings("unchecked")
		final Map<String, ?> param2 = (Map<String, ?>) parameter.get(3);
		if (!param2.isEmpty()) {
			for (Map.Entry<String, ?> parameters : param2.entrySet()) {
				if (parameters.getKey().toString().equals(IGeni.GENI_END_TIME)) {
					allocateOptions.put(ISFA_AM.EndTime, parameters.getValue()
							.toString());

				}
				if (parameters.getKey().toString()
						.equals(IGeni.GENI_START_TIME)) {
					allocateOptions.put(ISFA_AM.StartTime, parameters
							.getValue().toString());
				}
			}
		}

	}

	public Model reserveInstances() throws JAXBException,
			InvalidModelException, MissingRspecElementException,
			InvalidRspecValueException {

		Model incoming = parseRSpec(request);
		Model requestModel = ModelFactory.createDefaultModel();

		Resource topology = requestModel.createResource("http://"
				+ this.urn.getDomain() + "/topology/" + this.urn.getSubject());
		topology.addProperty(RDF.type, Omn.Topology);

		if (!this.urn.getProject().isEmpty()) {
			topology.addProperty(Omn_lifecycle.project, this.urn.getProject());
		}

		Model leaseInfo = getLeaseInfo(topology, incoming);
		requestModel.add(leaseInfo);

		addDateInformation(topology);
		Model requestedResources = getRequestedResources(topology, incoming);
		requestModel.add(requestedResources);

		String serializedModel = MessageUtil.serializeModel(requestModel,
				IMessageBus.SERIALIZATION_TURTLE);

		Model resultModel = getSender().sendRDFRequest(serializedModel,
				IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESERVATION);

		return resultModel;
	}

	private Model getLeaseInfo(Resource topologyResource, Model incoming) {

		ResIterator topologies = incoming.listSubjectsWithProperty(RDF.type,
				Omn.Topology);
		if (!topologies.hasNext()) {
			return null;
		}

		Resource incomingTopology = topologies.next();


		String serializedModel = MessageUtil.serializeModel(incoming,
				IMessageBus.SERIALIZATION_TURTLE);


		Model leaseInfo = ModelFactory.createDefaultModel();
		Resource topology = leaseInfo.createResource(topologyResource.getURI());

		if (incomingTopology.hasProperty(Omn_lifecycle.hasLease)) {


			Resource lease = incomingTopology
					.getProperty(Omn_lifecycle.hasLease).getObject()
					.asResource();

			if (lease.hasProperty(Omn_lifecycle.startTime)) {

				XSDDateTime time = null;
				Object startTime = ((Object) lease
						.getProperty(Omn_lifecycle.startTime).getObject()
						.asLiteral().getValue());
				if (startTime instanceof XSDDateTime) {
					time = (XSDDateTime) startTime;
				}
				Date date = TimeHelperMethods.getDateFromXSD(time);

				Property property = leaseInfo.createProperty(
						MessageBusOntologyModel.startTime.getNameSpace(),
						MessageBusOntologyModel.startTime.getLocalName());
				property.addProperty(RDF.type, OWL.FunctionalProperty);
				topology.addProperty(property, new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ssXXX").format(date));
			}

			if (lease.hasProperty(Omn_lifecycle.expirationTime)) {
				XSDDateTime time = null;
				Object startTime = ((Object) lease
						.getProperty(Omn_lifecycle.expirationTime).getObject()
						.asLiteral().getValue());
				if (startTime instanceof XSDDateTime) {
					time = (XSDDateTime) startTime;
				}

				Date date = TimeHelperMethods.getDateFromXSD(time);
				Property property = leaseInfo.createProperty(
						MessageBusOntologyModel.endTime.getNameSpace(),
						MessageBusOntologyModel.endTime.getLocalName());
				property.addProperty(RDF.type, OWL.FunctionalProperty);
				topology.addProperty(property, new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ssXXX").format(date));
			}
		}
		return leaseInfo;
	}

	private void addDateInformation(Resource topology) {

		if (allocateOptions.get(ISFA_AM.EndTime) != null) {
			String endTime = (String) allocateOptions.get(ISFA_AM.EndTime);
			Property property = topology.getModel().createProperty(
					MessageBusOntologyModel.endTime.getNameSpace(),
					MessageBusOntologyModel.endTime.getLocalName());
			property.addProperty(RDF.type, OWL.FunctionalProperty);
			topology.addProperty(property, endTime);

		}
		if (allocateOptions.get(ISFA_AM.StartTime) != null) {
			String startTime = (String) allocateOptions.get(ISFA_AM.StartTime);
			Property property = topology.getModel().createProperty(
					MessageBusOntologyModel.startTime.getNameSpace(),
					MessageBusOntologyModel.startTime.getLocalName());
			property.addProperty(RDF.type, OWL.FunctionalProperty);
			topology.addProperty(property, startTime);
		}
	}

	private Model getRequestedResources(Resource topology, Model requestedModel) {
		ResIterator resIterator = requestedModel.listSubjects();

		// ResIterator resIterator =
		// requestedModel.listResourcesWithProperty(Omn.isResourceOf);
		Model requestedResourcesModel = ModelFactory.createDefaultModel();

		Map<String, Resource> originalResourceNames = new HashMap<String, Resource>();

		while (resIterator.hasNext()) {

			Resource oldResource = resIterator.nextResource();

			if (oldResource.hasProperty(Omn.isResourceOf)) {

				Resource newResource = null;
				if (oldResource.hasProperty(Omn_lifecycle.implementedBy)) {
					Resource oldBase = oldResource
							.getProperty(Omn_lifecycle.implementedBy)
							.getObject().asResource();

					newResource = requestedResourcesModel
							.createResource(oldBase.getURI() + "/"
									+ oldResource.getLocalName());
				} else {
					newResource = requestedResourcesModel
							.createResource(oldResource.getURI());
				}

				originalResourceNames.put(oldResource.getURI(), newResource);

				StmtIterator stmtIterator = oldResource.listProperties();
				while (stmtIterator.hasNext()) {
					Statement statement = stmtIterator.nextStatement();
					if (statement.getPredicate().equals(Omn.isResourceOf)) {
						newResource.addProperty(statement.getPredicate(),
								topology);
					} else {

						newResource.addProperty(statement.getPredicate(),
								statement.getObject());

					}
					if (statement.getPredicate().equals(
							Omn_lifecycle.usesService)) {
						Resource service = requestedModel.getResource(statement
								.getObject().asResource().getURI());
						StmtIterator serviceProperties = service
								.listProperties();
						while (serviceProperties.hasNext()) {
							requestedResourcesModel.add(serviceProperties
									.nextStatement());
						}

					}

				}
				topology.addProperty(Omn.hasResource, newResource);
				requestedResourcesModel.add(topology
						.getProperty(Omn.hasResource));
			} else if (oldResource.hasProperty(Omn.hasResource)) {

			} else {
				// if(!oldResource.hasProperty(Omn.isResourceOf) ||
				// !oldResource.hasProperty(Omn.hasResource)){
				StmtIterator stmtIterator = oldResource.listProperties();
				while (stmtIterator.hasNext()) {
					Statement statement = stmtIterator.nextStatement();
					requestedResourcesModel.add(statement);
				}

			}
		}

		Model newRequestedResourcesModel = ModelFactory.createDefaultModel();

		ResIterator resIter = requestedResourcesModel.listSubjects();
		while (resIter.hasNext()) {
			Resource res = resIter.nextResource();
			StmtIterator stmtIter = res.listProperties();
			while (stmtIter.hasNext()) {
				Statement stmt = stmtIter.nextStatement();
				// if("deployedOn".equals(stmt.getPredicate().getLocalName()) ||
				// "requires".equals(stmt.getPredicate().getLocalName())){
				// Statement newStatement = new StatementImpl(stmt.getSubject(),
				// stmt.getPredicate(),
				// originalResourceNames.get(stmt.getObject().toString()));
				// newRequestedResourcesModel.add(newStatement);
				// }
				// else{
				newRequestedResourcesModel.add(stmt);
				// }
			}
		}

		// check whether replaced resources were also used as objects, replace
		// old uri with new
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toDelete = new ArrayList<Statement>();
		Model model = ModelFactory.createDefaultModel();
		for (Map.Entry<String, Resource> entry : originalResourceNames
				.entrySet()) {
			String oldUri = entry.getKey();
			Resource newResource = entry.getValue();
			StmtIterator statements = newRequestedResourcesModel
					.listStatements();
			while (statements.hasNext()) {
				Statement stmt = statements.nextStatement();
				if (stmt.getObject().isURIResource()
						&& stmt.getObject().asResource().getURI()
								.equals(oldUri)) {
					toDelete.add(stmt);
					Statement statementToAdd = model
							.createStatement(stmt.getSubject(),
									stmt.getPredicate(), newResource);
					toAdd.add(statementToAdd);
				}
			}
		}
		newRequestedResourcesModel.add(toAdd);
		newRequestedResourcesModel.remove(toDelete);

		return newRequestedResourcesModel;
	}

	private Model parseRSpec(String request) throws JAXBException,
			InvalidModelException, MissingRspecElementException,
			InvalidRspecValueException {
		Model model = null;
		InputStream is = new ByteArrayInputStream(request.getBytes(Charset
				.defaultCharset()));
		if (request.contains(RDF.getURI())) {
			model = ModelFactory.createDefaultModel();
			model.read(is, null, IMessageBus.SERIALIZATION_RDFXML);
		} else {
			// model = RequestConverter.getModel(is);
			try {
				model = DeliveryMechanism.getModelFromUnkownInput(request);
			} catch (UnsupportedException | XMLStreamException
					| DeprecatedRspecVersionException e) {

			}
		}

		return model;
	}

	public void createResponse(final HashMap<String, Object> result,
			Model allocateResponse) throws UnsupportedEncodingException {

		final Map<String, Object> value = new HashMap<>();

		try {

			Config config = new Config();
			value.put(
					IGeni.GENI_RSPEC,
					ManifestConverter.getRSpec(allocateResponse,
							config.getProperty(IConfig.KEY_HOSTNAME)));
		} catch (JAXBException | InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final List<Map<String, Object>> geniSlivers = new LinkedList<>();

		ResIterator iterator = allocateResponse.listResourcesWithProperty(
				RDF.type, Omn.Reservation);
		while (iterator.hasNext()) {

			final Map<String, Object> sliverMap = new HashMap<>();

			Resource reservation = iterator.nextResource();
			Config config = new Config();
			sliverMap.put(IGeni.GENI_SLIVER_URN, ManifestConverter
					.generateSliverID(config.getProperty(IConfig.KEY_HOSTNAME),
							reservation.getProperty(Omn.isReservationOf)
									.getResource().getURI()));
			sliverMap.put(IGeni.GENI_EXPIRES,
					reservation.getProperty(MessageBusOntologyModel.endTime)
							.getLiteral().getString());
			sliverMap.put(
					IGeni.GENI_ALLOCATION_STATUS,
					ReservationStateEnum.valueOf(
							reservation
									.getProperty(
											Omn_lifecycle.hasReservationState)
									.getResource().getLocalName())
							.getGeniState());
			geniSlivers.add(sliverMap);
		}

		value.put(IGeni.GENI_SLIVERS, geniSlivers);
		result.put(ISFA_AM.VALUE, value);
		this.addCode(result);
		this.addOutput(result);
		// }
	}
}
