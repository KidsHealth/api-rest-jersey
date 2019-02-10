package es.uma.health.kids.infrastructure.jersey;

import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {

	public ApplicationConfig() {
		packages(
				"es.uma.health.kids.infrastructure.controller", 
				"es.uma.health.kids.infrastructure.jersey", 
				"io.swagger.jaxrs.listing");
        
		register(new DependencyBinder());
	}
}