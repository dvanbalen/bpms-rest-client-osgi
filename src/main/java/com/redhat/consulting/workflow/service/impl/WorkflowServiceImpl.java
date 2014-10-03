package com.redhat.consulting.workflow.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.redhat.consulting.workflow.WorkflowManager;
import com.redhat.consulting.workflow.impl.WorkflowManagerImpl;
import com.redhat.consulting.workflow.service.WorkflowService;

public class WorkflowServiceImpl implements WorkflowService {

	private static Logger LOG = Logger.getLogger(WorkflowServiceImpl.class
			.getName());

	private WorkflowManager workflowManager;

	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public void setWorkflowManager(WorkflowManager workflowManager) {
		this.workflowManager = workflowManager;
	}

	/*
	 * @Override public Response batch(List<Workflow> commands) { // TODO
	 * Auto-generated method stub return null; }
	 */

	@Override
	public Response startWorkflow(Map<String, Object> parameters,
			String deploymentId, String workflowId) {
		try {
			/*
			 * bpmsRestHelper = new BpmsRestCommandHelper();
			 * bpmsRestHelper.setRuntimeUri
			 * ("http://ad0480blyn.genworth.net:8080/business-central/rest/runtime/"
			 * );
			 */
			System.out.println("In startWorkflow()");
			LOG.info("Going to start process: '" + workflowId
					+ "' from deployment: '" + deploymentId + "'");
			
			/*
			 * Map<String, String> params = new HashMap<>(); params.put("user",
			 * "foobar"); params.put("email", "baz");
			 * workflowManager.sendStartProcessCommand(deploymentId, workflowId,
			 * params);
			 */// workflowManager.createStartProcessCommand(workflowId,
				// parameters);
				// workflowManager.sendBpmsCommands(deploymentId);
			return Response.ok().build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Response delegateWorkflowTask(String deploymentId, long taskId,
			String fromActor, String toActor) {
		// TODO Auto-generated method stub
		return null;
	}

}
