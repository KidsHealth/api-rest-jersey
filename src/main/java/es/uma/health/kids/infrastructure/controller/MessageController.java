package es.uma.health.kids.infrastructure.controller;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import es.uma.health.kids.application.dto.message.ConversationDTO;
import es.uma.health.kids.application.dto.message.MessageMapper;
import es.uma.health.kids.application.dto.patient.PatientMapper;
import es.uma.health.kids.application.service.message.UserViewMessages;
import es.uma.health.kids.application.service.message.UserViewMessagesRequest;
import es.uma.health.kids.domain.model.message.MessageRepository;
import es.uma.health.kids.domain.model.patient.PatientRepository;
import es.uma.health.kids.domain.model.user.UserRepository;

@Path("/message")
public class MessageController {

	private UserRepository userRepo;
	private PatientRepository patientRepo;
	private MessageRepository messageRepo;
	private MessageMapper messageMapper;
	private PatientMapper patientMapper;
	
	@Inject
	public MessageController(UserRepository userRepo, PatientRepository patientRepo, MessageRepository messageRepo,
			MessageMapper messageMapper, PatientMapper patientMapper) {
		this.userRepo = userRepo;
		this.patientRepo = patientRepo;
		this.messageRepo = messageRepo;
		this.messageMapper = messageMapper;
		this.patientMapper = patientMapper;
	}

	@GET
	@Path("/user/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ConversationDTO> viewAllMessages(@PathParam("userId") final int userId) {
		return new UserViewMessages(userRepo, patientRepo, messageRepo, messageMapper, patientMapper)
				.execute(new UserViewMessagesRequest(userId));
	}
}
