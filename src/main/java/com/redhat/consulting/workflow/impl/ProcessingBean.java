package com.redhat.consulting.workflow.impl;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.common.message.CxfConstants;

import com.redhat.consulting.workflow.WorkflowManager;

public class ProcessingBean {

	JAXBContext jaxbContext = null;

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

		LinkedHashMap<String, Object> params = null;

		Object obj = exchange.getIn().getBody();

		if (obj instanceof LinkedHashMap) {
			params = (LinkedHashMap<String, Object>) exchange.getIn().getBody();
		} else {
			throw new RuntimeException("unsupported body type: "
					+ obj.getClass().getName());
		}

		try {
			WorkflowManager workflowManager = new WorkflowManagerImpl();
			workflowManager
					.setRuntimeUri("http://localhost:8080/business-central/rest/runtime");
			// Map<String, Object> params = new HashMap<>();
			//params.put("user", "foobar");
			//params.put("email", "baz");
			workflowManager.createStartProcessCommand("Workflows.TestProcess",
					params);
			workflowManager
					.sendBpmsCommands("com.redhat.consulting:Workflows:1.0");
		} catch (Exception e) {
			throw new RuntimeException("Error sending request to BPMS. ", e);
		}

		// exchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_QUERY_MAP, obj);

		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		/*
		 * exchange.getIn().setHeader(Exchange.CONTENT_TYPE,
		 * "application/x-www-form-urlencoded");
		 * exchange.getIn().setHeader(Exchange.ACCEPT_CONTENT_TYPE,
		 * "application/json");
		 */
		
/*		exchange.getIn().setHeader(Exchange.HTTP_QUERY,
				"map_user=foo&map_email=bar");
*/
		return params;

		/*
		 * Marshaller marshaller = null; StringWriter stringWriter = null;
		 * 
		 * try { marshaller = jaxbContext.createMarshaller();
		 * 
		 * stringWriter = new StringWriter();
		 * 
		 * marshaller.marshal(body, stringWriter); } catch (JAXBException je) {
		 * throw new RuntimeException("Error marshalling to XML.", je); }
		 * 
		 * return stringWriter.toString();
		 */}

}
