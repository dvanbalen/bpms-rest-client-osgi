package com.redhat.consulting.workflow.impl;

import java.util.LinkedHashMap;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;

import com.redhat.consulting.workflow.WorkflowManager;

public class ProcessingBean {

	JAXBContext jaxbContext = null;
	WorkflowManager workflowManager = new WorkflowManagerImpl();
	String deployment = null;
	String workflow = null;
	StartProcessCommand spc = null;

	public ProcessingBean() {
		try {
			jaxbContext = JAXBContext.newInstance(Container.class);
		} catch (Exception e) {
			throw new RuntimeException("Error creating JAXBContext.", e);
		}
	}

	public Object process(Exchange exchange) {
		System.out.println("########## GOT BODY ###########: "
				+ exchange.getIn().getBody() + " of type: "
				+ exchange.getIn().getBody().getClass().getName());
		
		workflowManager.clearCommandList();

		LinkedHashMap<String, Object> params = null;
		JaxbCommandsRequest request = null;
		
		deployment = (String)exchange.getIn().getHeader("deployment");
		workflow = (String)exchange.getIn().getHeader("workflow");
		
		Object obj = exchange.getIn().getBody();

		if (obj instanceof LinkedHashMap) {
			params = (LinkedHashMap<String, Object>) obj;
		} else {
			throw new RuntimeException("unsupported body type: "
					+ obj.getClass().getName());
		}

		try {
			workflowManager
					.setRuntimeUri("http://localhost:8080/business-central/rest/runtime");
			workflowManager.createStartProcessCommand("Workflows.TestProcess",
					params);
			request = workflowManager
					.getBpmsRequest("com.redhat.consulting:Workflows:1.0");
			
		} catch (Exception e) {
			throw new RuntimeException("Error sending request to BPMS. ", e);
		}

		// exchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_QUERY_MAP, obj);
		exchange.getIn().setHeader("CamelHttpPath", "runtime/"+deployment+"/execute");
		exchange.getIn().setHeader("Content-Type", "application/xml");

		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		
		System.out.println("########## RETURNING REQUEST ##########");
		
		return request;

	}

}
