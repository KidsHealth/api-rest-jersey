package es.uma.health.kids.infrastructure.jersey;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import es.uma.health.kids.domain.model.user.NotAuthorizedException;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException>{

	@Override
	public Response toResponse(NotAuthorizedException exception) {
		return Response.status(401).entity(exception.getMessage()).type(MediaType.APPLICATION_JSON).build();
	}

}
