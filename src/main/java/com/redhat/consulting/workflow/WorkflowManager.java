package com.redhat.consulting.workflow;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;

public interface WorkflowManager {

	public void setAuthorization(String authorization);

	public void setRuntimeUri(String runtime_uri);

	public void createStartProcessCommand(String workflowId,
			Map<String, Object> params);

	public void createDelegateTaskCommand(long taskId, String userId,
			String targetUserId);
	
	public JaxbCommandsRequest getBpmsRequest(String deploymentId);

	public void sendBpmsCommands(String deploymentId) throws Exception;

	public void sendStartProcessCommand(String deploymentId, String workflowId,
			Map<String, String> parameters);

	public void processJaxbCommandResponse(Response response) throws Exception;
	
	public void clearCommandList();

}
