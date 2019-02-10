package es.uma.health.kids.infrastructure.controller;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uma.health.kids.application.dto.message.ConversationDTO;
import es.uma.health.kids.application.dto.message.MessageMapper;
import es.uma.health.kids.application.dto.patient.PatientMapper;
import es.uma.health.kids.application.service.message.ProposeAppointmentRequest;
import es.uma.health.kids.application.service.message.SendMessageRequest;
import es.uma.health.kids.application.service.message.UpdateProposedAppointment;
import es.uma.health.kids.application.service.message.UpdateProposedAppointmentRequest;
import es.uma.health.kids.application.service.message.UserProposeAppointment;
import es.uma.health.kids.application.service.message.UserSendMessage;
import es.uma.health.kids.application.service.message.UserViewMessages;
import es.uma.health.kids.application.service.message.UserViewMessagesRequest;
import es.uma.health.kids.domain.event.DomainEventPublisher;
import es.uma.health.kids.domain.model.event.EventRepository;
import es.uma.health.kids.domain.model.message.AppointmentAcceptedSubscriber;
import es.uma.health.kids.domain.model.message.AppointmentRejectedSubscriber;
import es.uma.health.kids.domain.model.message.MessageRepository;
import es.uma.health.kids.domain.model.patient.PatientRepository;
import es.uma.health.kids.domain.model.user.UserRepository;

@Path("")
public class MessageController {

	private UserRepository userRepo;
	private PatientRepository patientRepo;
	private MessageRepository messageRepo;
	private MessageMapper messageMapper;
	private PatientMapper patientMapper;
	
	@Inject
	public MessageController(UserRepository userRepo, PatientRepository patientRepo, MessageRepository messageRepo,
			EventRepository eventRepo, MessageMapper messageMapper, PatientMapper patientMapper) {
		this.userRepo = userRepo;
		this.patientRepo = patientRepo;
		this.messageRepo = messageRepo;
			this.messageMapper = messageMapper;
		this.patientMapper = patientMapper;
		
		DomainEventPublisher.instance().subscribe(new AppointmentAcceptedSubscriber(eventRepo));
		DomainEventPublisher.instance().subscribe(new AppointmentRejectedSubscriber(eventRepo));
	}

	@GET
	@Path("/user/{userId}/message")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ConversationDTO> viewAllMessages(@PathParam("userId") final int userId) {
		return new UserViewMessages(userRepo, patientRepo, messageRepo, messageMapper, patientMapper)
				.execute(new UserViewMessagesRequest(userId));
	}
	
	@POST
	@Path("/user/{userId}/patient/{patientId}/message")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postOrdinaryMessage(
			final SendMessageRequest request, 
			@PathParam("patientId") final int patientId, 
			@PathParam("userId") final int userId) {
		request.patientId = patientId;
		request.userId = userId;
		new UserSendMessage(userRepo, patientRepo, messageRepo)
			.execute(request);
		return Response.ok().build();
	}
	
	@POST
	@Path("/user/{userId}/patient/{patientId}/appointmentRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postAppointmentRequest(
			final ProposeAppointmentRequest request,
			@PathParam("patientId") final int patientId,
			@PathParam("userId") final int userId) {
		request.patientId = patientId;
		request.userId = userId;
		new UserProposeAppointment(userRepo, patientRepo, messageRepo)
			.execute(request);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/user/{userId}/appointmentRequest/{messageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAppointmentRequest(
			final UpdateProposedAppointmentRequest request,
			@PathParam("messageId") final int messageId,
			@PathParam("userId") final int userId) {
		request.messageId = messageId;
		request.userId = userId;
		new UpdateProposedAppointment(userRepo, messageRepo)
			.execute(request);
		return Response.ok().build();
	}
	
}
