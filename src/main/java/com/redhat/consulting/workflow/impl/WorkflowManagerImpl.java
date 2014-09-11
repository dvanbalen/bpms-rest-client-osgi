package com.redhat.consulting.workflow.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.WebClient;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.jbpm.services.task.commands.DelegateTaskCommand;
import org.kie.api.command.Command;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsResponse;
import org.kie.services.client.serialization.jaxb.rest.JaxbExceptionResponse;

import com.redhat.consulting.workflow.WorkflowManager;

public class WorkflowManagerImpl implements WorkflowManager {

	private static Logger LOG = Logger.getLogger(WorkflowManagerImpl.class
			.getName());

	private Set<Class<?>> extraJaxbClassList = new HashSet<>();
	@SuppressWarnings("rawtypes")
	private List<Command> cmds = new ArrayList<>();

	private String runtimeUri = "http://localhost:8080/business-central/rest/runtime/";
	private String authorization = null;

	@Override
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	@Override
	public void setRuntimeUri(String runtime_uri) {
		this.runtimeUri = runtime_uri;
	}

	@Override
	public void createStartProcessCommand(String workflowId,
			Map<String, Object> params) {

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Creating a start process command.");
		}

		// Get list of parameter classes to add to JAXB context
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> e : params.entrySet()) {
				extraJaxbClassList.add(e.getValue().getClass());
			}
		}
		extraJaxbClassList.add(params.getClass());

		StartProcessCommand cmd = new StartProcessCommand(workflowId, params);

		cmds.add(cmd);

	}

	@Override
	public void createDelegateTaskCommand(long taskId, String userId,
			String targetUserId) {

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Creating task delegation command.");
		}

		DelegateTaskCommand cmd = new DelegateTaskCommand(taskId, userId,
				targetUserId);

		cmds.add(cmd);
	}

	@Override
	public void sendBpmsCommands(String deploymentId) throws Exception {

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Sending commands to BPMS server.");
		}

		Response response = null;

		String uri = runtimeUri + deploymentId + "/execute";
		String username = "david";
		String password = "david123!";
		/*
		 * String b64enc = Base64.encodeBytes((username + ":" + password)
		 * .getBytes());
		 */
		String b64enc = Base64Utility.encode((username + ":" + password)
				.getBytes());
		String auth = "Basic " + b64enc;
		String jaxbRequestString = null;

		try {
			if (authorization != null) {
				auth = authorization;
			}
			System.out.println("Sending " + cmds.size() + " commands to BPMS.");
			System.out.println("Auth: " + auth);

			JaxbSerializationProvider jaxbProvider = new JaxbSerializationProvider(
					extraJaxbClassList);

			JaxbCommandsRequest req = new JaxbCommandsRequest(deploymentId, cmds);

			jaxbRequestString = jaxbProvider.serialize(req);
			
			LOG.info("Serialized request: "+jaxbRequestString);

			/*
			 * Client client = ClientBuilder.newClient(); WebTarget target =
			 * client.target(uri);
			 * 
			 * response = target .request() .header("Authorization", auth)
			 * .post(Entity.entity(jaxbRequestString,
			 * MediaType.APPLICATION_XML));
			 */

			WebClient client = WebClient.create(uri);
			client = client.header("Authorization", auth).type(
					MediaType.APPLICATION_XML);
			response = client.post(jaxbRequestString);
			this.processJaxbCommandResponse(response);
		} finally {
			if (cmds != null) {
				cmds.clear();
			}
			if (extraJaxbClassList != null) {
				extraJaxbClassList.clear();
			}
			try {
				if (response != null) {
					response.close();
				}
			} catch (Throwable t) {
				// ignore
			}
		}

	}

	@Override
	public void sendStartProcessCommand(String deploymentId, String workflowId,
			Map<String, String> parameters) {
		String uri = runtimeUri + deploymentId + "/process/" + workflowId + "/start";
		String username = "david";
		String password = "david123!";
		String b64enc = Base64Utility.encode((username + ":" + password)
				.getBytes());
		String auth = "Basic " + b64enc;

		Response response = null;
		
		WebClient client = WebClient.create(uri);
		client = client.header("Authorization", auth).type(MediaType.APPLICATION_XML);
		response = client.post(parameters);
		
		LOG.info("Got response: "+response.getStatus());
		
	}

	@Override
	public void processJaxbCommandResponse(Response response) throws Exception {
		JaxbCommandsResponse commandResponse = null;
		JaxbCommandResponse<?> responseObject = null;

		System.out.println("Status: " + response.getStatus() + " class: "
				+ response.getClass().getName());

		commandResponse = response.readEntity(JaxbCommandsResponse.class);
		if (commandResponse == null) {
			System.out.println("command response is null");
		}
		List<JaxbCommandResponse<?>> responses = commandResponse.getResponses();
		JaxbExceptionResponse exceptionResponse;
		if (responses.size() == 0) {
			System.out.println("Responses size is zero.");
			return;
		} else if (responses.size() == 1) {
			System.out.println("Responses size is one.");
			responseObject = responses.get(0);
			if (responseObject instanceof JaxbExceptionResponse) {
				System.out.println("Resepose is exception.");
				exceptionResponse = (JaxbExceptionResponse) responseObject;
				System.err.println("Exception response from command: "
						+ exceptionResponse.getCommandName() + " message: "
						+ exceptionResponse.getMessage() + " result: "
						+ exceptionResponse.getResult());
			} else {
				System.out.println("Response isn't exception.");
				System.out.println("Response Object: "
						+ responseObject.getResult().toString());
			}
		} else {
			throw new Exception(
					"Unexpected number of results from single command");
		}

	}

}
