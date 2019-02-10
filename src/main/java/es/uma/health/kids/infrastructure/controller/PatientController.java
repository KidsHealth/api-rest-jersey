package es.uma.health.kids.infrastructure.controller;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uma.health.kids.application.dto.diseasecontraction.DetailedDiseaseContractionMapper;
import es.uma.health.kids.application.dto.medication.MedicationMapper;
import es.uma.health.kids.application.dto.patient.DetailedPatientDTO;
import es.uma.health.kids.application.dto.patient.DetailedPatientMapper;
import es.uma.health.kids.application.dto.patient.PatientDTO;
import es.uma.health.kids.application.dto.patient.PatientMapper;
import es.uma.health.kids.application.service.patient.ViewDetailedPatient;
import es.uma.health.kids.application.service.patient.ViewDetailedPatientRequest;
import es.uma.health.kids.application.service.user.UserViewTheirPatients;
import es.uma.health.kids.application.service.user.UserViewTheirPatientsRequest;
import es.uma.health.kids.application.service.user.doctor.DoctorAddTreatment;
import es.uma.health.kids.application.service.user.doctor.DoctorAddTreatmentRequest;
import es.uma.health.kids.application.service.user.doctor.DoctorAssignNewPatient;
import es.uma.health.kids.application.service.user.doctor.DoctorAssignNewPatientRequest;
import es.uma.health.kids.application.service.user.doctor.DoctorDiagnosePatient;
import es.uma.health.kids.application.service.user.doctor.DoctorDiagnosePatientRequest;
import es.uma.health.kids.application.service.user.doctor.DoctorUnassignAPatient;
import es.uma.health.kids.application.service.user.doctor.DoctorUnassignAPatientRequest;
import es.uma.health.kids.application.service.user.patientresponsible.ResponsibleAddPatient;
import es.uma.health.kids.application.service.user.patientresponsible.ResponsibleAddPatientRequest;
import es.uma.health.kids.application.service.user.patientresponsible.ResponsibleDeletePatient;
import es.uma.health.kids.application.service.user.patientresponsible.ResponsibleDeletePatientRequest;
import es.uma.health.kids.application.service.user.patientresponsible.ResponsibleUpdatePatient;
import es.uma.health.kids.application.service.user.patientresponsible.ResponsibleUpdatePatientRequest;
import es.uma.health.kids.domain.model.diseasecontraction.DiseaseContractionRepository;
import es.uma.health.kids.domain.model.medication.MedicationRepository;
import es.uma.health.kids.domain.model.patient.PatientRepository;
import es.uma.health.kids.domain.model.user.UserRepository;

@Path("")
public class PatientController {

	private UserRepository userRepo;
	private PatientRepository patientRepo;
	private MedicationRepository medicationRepo;
	private DiseaseContractionRepository contractionRepo;
	private DetailedPatientMapper detailedPatientMapper;
	private MedicationMapper medicationMapper;
	private DetailedDiseaseContractionMapper detailedContractionMapper;
	private PatientMapper patientMapper;
	
	@Inject
	public PatientController(UserRepository userRepo, PatientRepository patientRepo,
			MedicationRepository medicationRepo, DiseaseContractionRepository contractionRepo,
			DetailedPatientMapper detailedPatientMapper, MedicationMapper medicationMapper,
			DetailedDiseaseContractionMapper detailedContractionMapper, PatientMapper patientMapper) {
		this.userRepo = userRepo;
		this.patientRepo = patientRepo;
		this.medicationRepo = medicationRepo;
		this.contractionRepo = contractionRepo;
		this.detailedPatientMapper = detailedPatientMapper;
		this.medicationMapper = medicationMapper;
		this.detailedContractionMapper = detailedContractionMapper;
		this.patientMapper = patientMapper;
	}
	
	@GET
	@Path("/user/{userId}/patient/{patientId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public DetailedPatientDTO viewDetailedPatient(
			@PathParam("patientId") final int patientId,
			@PathParam("userId") final int userId) {
		return new ViewDetailedPatient(userRepo, patientRepo, contractionRepo, medicationRepo, 
				detailedPatientMapper, detailedContractionMapper, medicationMapper)
				.execute(new ViewDetailedPatientRequest(userId, patientId));
	}

	@GET
	@Path("/user/{userId}/patient")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Collection<PatientDTO> viewPatients(
			@PathParam("userId") final int userId) {
		return new UserViewTheirPatients(userRepo, patientRepo, patientMapper)
				.execute(new UserViewTheirPatientsRequest(userId));
	}
	
	@POST
	@Path("/user/{userId}/patient")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response addPatient(
			final ResponsibleAddPatientRequest request,
			@PathParam("userId") final int userId) {
		request.userId = userId;
		new ResponsibleAddPatient(userRepo, patientRepo).execute(request);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/user/{userId}/patient/{patientId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response updatePatient(
			final ResponsibleUpdatePatientRequest request,
			@PathParam("userId") final int userId,
			@PathParam("patientId") final int patientId) {
		request.userId = userId;
		request.patientId = patientId;
		new ResponsibleUpdatePatient(userRepo, patientRepo)
			.execute(request);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/user/{userId}/patient/{patientId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response deletePatient(
			@PathParam("userId") final int userId,
			@PathParam("patientId") final int patientId) {
		new ResponsibleDeletePatient(userRepo, patientRepo)
			.execute(new ResponsibleDeletePatientRequest(userId, patientId));
		return Response.ok().build();
	}
	
	@POST
	@Path("/doctor/{userId}/patient/{patientId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doctorAssignNewPatient(
			@PathParam("userId") final int doctorId,
			@PathParam("patientId") final int patientId) {
		new DoctorAssignNewPatient(userRepo, patientRepo)
			.execute(new DoctorAssignNewPatientRequest(doctorId, patientId));
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/doctor/{userId}/patient/{patientId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doctorUnassignAPatient(
			@PathParam("userId") final int doctorId,
			@PathParam("patientId") final int patientId) {
		new DoctorUnassignAPatient(userRepo, patientRepo)
			.execute(new DoctorUnassignAPatientRequest(doctorId, patientId));
		return Response.ok().build();
	}
	
	@POST
	@Path("/doctor/{userId}/patient/{patientId}/diagnosis")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doctorDiagnosePatient(
			final DoctorDiagnosePatientRequest request,
			@PathParam("userId") final int doctorId,
			@PathParam("patientId") final int patientId) {
		request.doctorId = doctorId;
		request.patientId = patientId;
		new DoctorDiagnosePatient(userRepo, patientRepo, contractionRepo)
			.execute(request);
		return Response.ok().build();
	}
	
	@POST
	@Path("/doctor/{userId}/diagnosis/{diseaseContractionId}/medication")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doctorDiagnosePatient(
			final DoctorAddTreatmentRequest request,
			@PathParam("diseaseContractionId") final int diseaseContractionId,
			@PathParam("userId") final int doctorId) {
		request.diseaseContractionId = diseaseContractionId;
		request.doctorId = doctorId;
		new DoctorAddTreatment(userRepo, patientRepo, contractionRepo, medicationRepo)
			.execute(request);
		return Response.ok().build();
	}
	
}
