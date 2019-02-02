package es.uma.health.kids.infrastructure.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/default")
public class DefaultController {

	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActorById(@PathParam("name") final String name) {
		return Response.ok("Hello, " + name).build();
	}
}
