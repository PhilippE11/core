package de.btu.openinfra.backend.exception;

import javax.ws.rs.core.Response.Status;

public class OpenInfraSolrException extends OpenInfraWebException {

	private static final long serialVersionUID = 1L;

	public OpenInfraSolrException(OpenInfraExceptionTypes type) {
		super(Status.INTERNAL_SERVER_ERROR,
		        OpenInfraSolrException.class.getName(),
		        type);
	}



}
