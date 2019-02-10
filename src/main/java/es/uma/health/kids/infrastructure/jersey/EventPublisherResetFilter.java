package es.uma.health.kids.infrastructure.jersey;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import es.uma.health.kids.domain.event.DomainEventPublisher;

@Provider
@PreMatching
public class EventPublisherResetFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		DomainEventPublisher.instance().reset();
	}

}
