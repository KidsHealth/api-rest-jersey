package es.uma.health.kids.infrastructure.jersey;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import es.uma.health.kids.domain.model.shared.ResourceDoesNotExistException;

@Provider
public class ResourceDoesNotExistExceptionMapper implements ExceptionMapper<ResourceDoesNotExistException> {

	@Override
	public Response toResponse(ResourceDoesNotExistException exception) {
		return Response.status(404).entity(exception.getMessage()).type(MediaType.APPLICATION_JSON).build();
	}

}