package es.uma.health.kids.infrastructure.jersey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import es.uma.health.kids.application.dto.diseasecontraction.DetailedDiseaseContractionMapper;
import es.uma.health.kids.application.dto.diseasecontraction.DiseaseContractionMapper;
import es.uma.health.kids.application.dto.medication.MedicationMapper;
import es.uma.health.kids.application.dto.message.MessageMapper;
import es.uma.health.kids.application.dto.patient.DetailedPatientMapper;
import es.uma.health.kids.application.dto.patient.PatientMapper;
import es.uma.health.kids.domain.model.diseasecontraction.DiseaseContractionRepository;
import es.uma.health.kids.domain.model.event.EventRepository;
import es.uma.health.kids.domain.model.medication.MedicationRepository;
import es.uma.health.kids.domain.model.message.MessageRepository;
import es.uma.health.kids.domain.model.patient.PatientRepository;
import es.uma.health.kids.domain.model.user.UserRepository;
import es.uma.health.kids.infrastructure.persistence.diseasecontraction.MySQLDiseaseContractionRepository;
import es.uma.health.kids.infrastructure.persistence.event.MySQLEventRepository;
import es.uma.health.kids.infrastructure.persistence.medication.MySQLMedicationRepository;
import es.uma.health.kids.infrastructure.persistence.message.MySQLMessageRepository;
import es.uma.health.kids.infrastructure.persistence.patient.MySQLPatientRepository;
import es.uma.health.kids.infrastructure.persistence.user.MySQLUserRepository;

public class DependencyBinder extends AbstractBinder {

	@Override
	protected void configure() {
		System.err.println("Loading binder...");
		
		System.err.println(System.getenv("KIDSHEALTH_DB_URL"));
		System.err.println(System.getenv("KIDSHEALTH_DB_USER"));
		System.err.println(System.getenv("KIDSHEALTH_DB_PASS"));
		
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
				System.getenv("KIDSHEALTH_DB_URL"),
				System.getenv("KIDSHEALTH_DB_USER"),
				System.getenv("KIDSHEALTH_DB_PASS")
			);
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		UserRepository userRepository = new MySQLUserRepository(connection);
		PatientRepository patientRepository = new MySQLPatientRepository(connection);
		MessageRepository messageRepository = new MySQLMessageRepository(connection);
		MedicationRepository medicationRepository = new MySQLMedicationRepository(connection);
		EventRepository eventRepository = new MySQLEventRepository(connection);
		DiseaseContractionRepository contractionRepository = new MySQLDiseaseContractionRepository(connection);
		
		bind(userRepository).to(UserRepository.class);
		bind(patientRepository).to(PatientRepository.class);
		bind(messageRepository).to(MessageRepository.class);
		bind(medicationRepository).to(MedicationRepository.class);
		bind(eventRepository).to(EventRepository.class);
		bind(contractionRepository).to(DiseaseContractionRepository.class);
		
		bindAsContract(DiseaseContractionMapper.class);
		bindAsContract(MedicationMapper.class);
		bindAsContract(MessageMapper.class);
		bindAsContract(PatientMapper.class);
		bindAsContract(DetailedDiseaseContractionMapper.class);
		bindAsContract(DetailedPatientMapper.class);

	}

}