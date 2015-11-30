package de.btu.openinfra.backend.exception;

import javax.ws.rs.core.Response.Status;

public class OpenInfraSubjectException extends OpenInfraWebException {
	private static final long serialVersionUID = 1L;

	protected OpenInfraSubjectException(Status status, String name,
			OpenInfraExceptionTypes type) {
		super(Status.CONFLICT, OpenInfraSubjectException.class.getName(),
				OpenInfraExceptionTypes.PASSWORD_IN_BLACKLIST);
	}

}
