package org.fiteagle.north.sfa.am;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.north.sfa.am.allocate.ProcessAllocate;
import org.fiteagle.north.sfa.am.common.AbstractMethodProcessor;
import org.fiteagle.north.sfa.am.delete.ProcessDelete;
import org.fiteagle.north.sfa.am.describe.DescribeProcessor;
import org.fiteagle.north.sfa.am.dm.SFA_AM_Delegate_Default;
import org.fiteagle.north.sfa.am.dm.SFA_AM_MDBSender;
import org.fiteagle.north.sfa.am.getVersion.ProcessGetVersion;
import org.fiteagle.north.sfa.am.listResources.ListResourcesProcessor;
import org.fiteagle.north.sfa.am.performOperationalAction.PerformOperationalActionHandler;
import org.fiteagle.north.sfa.am.provision.ProcessProvision;
import org.fiteagle.north.sfa.am.renew.RenewHandler;
import org.fiteagle.north.sfa.am.status.StatusProcessor;
import org.fiteagle.north.sfa.exceptions.BadArgumentsException;
import org.fiteagle.north.sfa.exceptions.BadVersionException;
import org.fiteagle.north.sfa.exceptions.EmptyReplyException;
import org.fiteagle.north.sfa.exceptions.ForbiddenException;
import org.fiteagle.north.sfa.exceptions.SearchFailedException;
import org.fiteagle.north.sfa.exceptions.URNParsingException;

import com.hp.hpl.jena.rdf.model.Model;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.InvalidRspecValueException;
import info.openmultinet.ontology.exceptions.MissingRspecElementException;

public class SFA_AM implements ISFA_AM {
	private final static Logger LOGGER = Logger.getLogger(SFA_AM.class
			.getName());
	private ISFA_AM_Delegate delegate;

	public SFA_AM() {

	}

	private String query = "";

	@Override
	public Object handle(final String methodName, final List<?> parameter,
			final String path, final X509Certificate cert) {
		Object result;

		this.delegate = new SFA_AM_Delegate_Default();
		SFA_AM.LOGGER.log(
				Level.INFO,
				System.getProperty("jboss.server.config.dir")
						+ System.getProperty("file.separator")
						+ "jetty-ssl.keystore");
		SFA_AM.LOGGER.log(Level.INFO, "START: Handling " + methodName);
		try {
			switch (methodName.toUpperCase()) {
			case ISFA_AM.METHOD_GET_VERSION:
				result = this.getVersion(parameter);
				break;
			case ISFA_AM.METHOD_LIST_RESOURCES:
				result = this.listResources(parameter);
				break;
			case ISFA_AM.METHOD_ALLOCATE:
				result = this.allocate(parameter);
				break;
			case ISFA_AM.METHOD_DESCRIBE:
				result = this.describe(parameter);
				break;
			case ISFA_AM.METHOD_RENEW:
				result = this.renew(parameter);
				break;
			case ISFA_AM.METHOD_PROVISION:
				result = this.provision(parameter);
				break;
			case ISFA_AM.METHOD_STATUS:
				result = this.status(parameter);
				break;
			case ISFA_AM.METHOD_PERFORMOPERATIONALACTION:
				result = this.performOperationalAction(parameter);
				break;
			case ISFA_AM.METHOD_DELETE:
				result = this.delete(parameter);
				break;
			case ISFA_AM.METHOD_SHUTDOWN:
				result = this.shutdown(parameter);
				break;
			default:
				result = "Unimplemented method '" + methodName + "'";
				break;
			}
		} catch (BadArgumentsException e) {
			result = handleException(e, GENI_CodeEnum.BADARGS);

		} catch (URNParsingException e) {
			result = handleException(e, GENI_CodeEnum.BADARGS);

		} catch (JMSException e) {
			result = handleException(e, GENI_CodeEnum.SERVERERROR);

		} catch (EmptyReplyException e) {
			result = handleException(e, GENI_CodeEnum.SEARCHFAILED);

		} catch (MessageUtil.TimeoutException e) {
			result = handleException(e, GENI_CodeEnum.TIMEDOUT);

		} catch (ForbiddenException e) {
			result = handleException(e, GENI_CodeEnum.FORBIDDEN);

		} catch (SearchFailedException e) {
			result = handleException(e, GENI_CodeEnum.SEARCHFAILED);

		} catch (BadVersionException e) {
			result = handleException(e, GENI_CodeEnum.BADVERSION);

		} catch (RuntimeException e) {
			result = handleException(e, GENI_CodeEnum.ERROR);

		} catch (UnsupportedEncodingException e) {
			result = handleException(e, GENI_CodeEnum.ERROR);

		} catch (JAXBException e) {
			result = handleException(e, GENI_CodeEnum.ERROR);

		} catch (InvalidModelException e) {
			result = handleException(e, GENI_CodeEnum.ERROR);
		} catch (MissingRspecElementException | InvalidRspecValueException e) {
			result = handleException(e, GENI_CodeEnum.BADARGS);
		}

		SFA_AM.LOGGER.log(Level.INFO, "END: Handling " + methodName);
		return result;
	}

	@Override
	public Object allocate(final List<?> parameter) throws JAXBException,
			InvalidModelException, UnsupportedEncodingException,
			MissingRspecElementException, InvalidRspecValueException {
		SFA_AM.LOGGER.log(Level.INFO, "allocate...");
		final HashMap<String, Object> result = new HashMap<>();
		ProcessAllocate processAllocate = new ProcessAllocate(parameter);
		processAllocate.parseAllocateParameter();
		processAllocate.handleCredentials(1, ISFA_AM.METHOD_ALLOCATE);
		processAllocate.setSender(SFA_AM_MDBSender.getInstance());
		Model allocateResponse = processAllocate.reserveInstances();
		processAllocate.createResponse(result, allocateResponse);
		return result;
	}

	@Override
	public Object renew(final List<?> parameter)
			throws UnsupportedEncodingException {
		SFA_AM.LOGGER.log(Level.INFO, "renew...");
		final HashMap<String, Object> result = new HashMap<>();
		RenewHandler renewHandler = new RenewHandler(parameter);
		renewHandler.parseURNList();
		renewHandler.handleCredentials(1, ISFA_AM.METHOD_RENEW);
		renewHandler.parseExpirationTime();
		renewHandler.setSender(SFA_AM_MDBSender.getInstance());
		Model resultModel = renewHandler.renew();
		renewHandler.createResponse(result, resultModel);
		return result;
	}

	@Override
	public Object provision(final List<?> parameter)
			throws UnsupportedEncodingException {
		SFA_AM.LOGGER.log(Level.INFO, "provision...");
		final HashMap<String, Object> result = new HashMap<>();
		ProcessProvision processProvision = new ProcessProvision(parameter);
		processProvision.parseURNList();
		processProvision.handleCredentials(1, ISFA_AM.METHOD_PROVISION);
		processProvision.handleOptions();
		SFA_AM.LOGGER.log(Level.INFO, "provision parameters have been parsed");
		processProvision.setSender(SFA_AM_MDBSender.getInstance());
		Model provisionResponse = processProvision.provisionInstances();
		processProvision.createResponse(result, provisionResponse);
		return result;
	}

	@Override
	public Object status(final List<?> parameter)
			throws UnsupportedEncodingException {
		SFA_AM.LOGGER.log(Level.INFO, "status...");
		final HashMap<String, Object> result = new HashMap<>();
		StatusProcessor statusProcessor = new StatusProcessor(parameter);
		statusProcessor.parseURNList();
		statusProcessor.handleCredentials(1, ISFA_AM.METHOD_STATUS);
		statusProcessor.setSender(SFA_AM_MDBSender.getInstance());
		Model statusResponse = statusProcessor.getStates();
		statusProcessor.createResponse(result, statusResponse);
		return result;
	}

	@Override
	public Object performOperationalAction(final List<?> parameter)
			throws UnsupportedEncodingException {
		SFA_AM.LOGGER.log(Level.INFO, "performOperationalAction...");
		final HashMap<String, Object> result = new HashMap<>();
		PerformOperationalActionHandler performOperationalActionHandler = new PerformOperationalActionHandler(
				parameter);
		performOperationalActionHandler.parseURNList();
		performOperationalActionHandler.handleCredentials(1,
				ISFA_AM.METHOD_PERFORMOPERATIONALACTION);
		performOperationalActionHandler.parseAction();
		// TODO ignore options for now
		performOperationalActionHandler.setSender(SFA_AM_MDBSender
				.getInstance());
		Model performResponse = performOperationalActionHandler.performAction();
		performOperationalActionHandler.createResponse(result, performResponse);
		return result;
	}

	@Override
	public Object delete(final List<?> parameter)
			throws UnsupportedEncodingException {
		SFA_AM.LOGGER.log(Level.INFO, "delete...");
		final HashMap<String, Object> result = new HashMap<>();
		ProcessDelete processDelete = new ProcessDelete(parameter);
		processDelete.parseURNList();
		processDelete.handleCredentials(1, ISFA_AM.METHOD_DELETE);
		SFA_AM.LOGGER.log(Level.INFO, "delete parameters are parsed");
		processDelete.setSender(SFA_AM_MDBSender.getInstance());
		Model deleteResponse = processDelete.deleteInstances();
		processDelete.createResponse(result, deleteResponse);
		return result;
	}

	@Override
	public Object shutdown(final List<?> parameter) {
		final HashMap<String, Object> result = new HashMap<>();
		return result;
	}

	@Override
	public Object listResources(final List<?> parameter) throws JMSException,
			UnsupportedEncodingException, JAXBException, InvalidModelException {
		SFA_AM.LOGGER.log(Level.INFO, "listResources...");
		HashMap<String, Object> result = new HashMap<>();
		ListResourcesProcessor listResourcesProcessor = new ListResourcesProcessor(
				parameter);
		listResourcesProcessor.handleCredentials(0,
				ISFA_AM.METHOD_LIST_RESOURCES);
		listResourcesProcessor.parseOptionsParameters();
		if (listResourcesProcessor.checkSupportedVersions()) {
			listResourcesProcessor.setSender(SFA_AM_MDBSender.getInstance());
			Model topologyModel = listResourcesProcessor.listResources();
			listResourcesProcessor.createResponse(result, topologyModel);
			return result;
		} else {
			throw new BadVersionException(
					GENI_CodeEnum.BADVERSION.getDescription());
		}
	}

	@Override
	public Object getVersion(final List<?> parameter) {
		final HashMap<String, Object> result = new HashMap<>();
		ProcessGetVersion processGetVersion = new ProcessGetVersion(parameter);
		processGetVersion.setSender(SFA_AM_MDBSender.getInstance());
		Model testbedDescriptionModel = processGetVersion
				.getTestbedDescription();
		String testbedDescription = processGetVersion
				.parseTestbedDescription(testbedDescriptionModel);
		processGetVersion.createResponse(result, testbedDescription);
		return result;
	}

	@Override
	public Object describe(List<?> parameter)
			throws UnsupportedEncodingException, JAXBException,
			InvalidModelException {
		LOGGER.log(Level.INFO, "Describe... ");
		final HashMap<String, Object> result = new HashMap<>();
		DescribeProcessor describeProcessor = new DescribeProcessor(parameter);
		describeProcessor.parseURNList();
		describeProcessor.handleCredentials(1, ISFA_AM.METHOD_DESCRIBE);
		describeProcessor.parseDescribeOptions();
		describeProcessor.setSender(SFA_AM_MDBSender.getInstance());

		Model descriptions = describeProcessor.getDescriptions();
		describeProcessor.createResponse(result, descriptions);
		return result;
	}

	private HashMap<String, Object> handleException(Exception e,
			GENI_CodeEnum errorCode) {
		LOGGER.log(Level.WARNING, e.getMessage(), e);
		HashMap<String, Object> result = new HashMap<>();
		AbstractMethodProcessor abstractMethodProcessor = new AbstractMethodProcessor();
		abstractMethodProcessor.delegate.setGeniCode(errorCode.getValue());
		abstractMethodProcessor.delegate.setOutput(e.getMessage());
		abstractMethodProcessor.addCode(result);
		abstractMethodProcessor.addOutput(result);
		result.put(ISFA_AM.VALUE, new HashMap<String, Object>());
		return result;
	}

}
