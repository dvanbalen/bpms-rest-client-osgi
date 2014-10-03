package com.redhat.consulting.workflow.service;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import com.genworth.path.dto.Workflow;

@Path("/runtime")
public interface WorkflowService {

/*	@POST
	@Path("/batch")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response batch(List<Workflow> commands);*/

	@POST
	@Path("/{deployment}/process/{workflow}/start")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response startWorkflow(Map<String, Object> parameters, @PathParam("deployment") String deploymentId,
			@PathParam("workflow") String workflowId);

	@POST
	@Path("/{deployment}/{taskInstance}/{fromActor}/delegate/{toActor}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delegateWorkflowTask(
			@PathParam("deployment") String deploymentId,
			@PathParam("taskInstance") long taskId,
			@PathParam("fromActor") String fromActor,
			@PathParam("toActor") String toActor);

}
