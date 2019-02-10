package es.uma.health.kids.infrastructure.persistence.patient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import es.uma.health.kids.domain.model.patient.Patient;
import es.uma.health.kids.domain.model.patient.PatientFullName;
import es.uma.health.kids.domain.model.patient.PatientId;
import es.uma.health.kids.domain.model.patient.PatientRepository;
import es.uma.health.kids.domain.model.user.UserId;

public class MySQLPatientRepository implements PatientRepository {

	protected final static String PATIENT_TABLE = "Patient";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", PATIENT_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?);", PATIENT_TABLE);
	protected final static String UPDATE = String.format("UPDATE %s SET name = ?, surname = ?, "
			+ "birthdate = ?, Doctor_User_id = ? WHERE id = ?;", PATIENT_TABLE);
	protected final static String DELETE = String.format("DELETE FROM %s WHERE id = ?;", PATIENT_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT * FROM %s WHERE id = ?;", PATIENT_TABLE);
	protected final static String SELECT_OF_RESPONSIBLEID = String.format("SELECT * FROM %s WHERE PatientResponsible_User_id = ?;", PATIENT_TABLE);
	protected final static String SELECT_OF_DOCTORID = String.format("SELECT * FROM %s WHERE Doctor_User_id = ?;", PATIENT_TABLE);
	protected final static String SELECT_RELATED_WITH = String.format("SELECT * FROM %s WHERE PatientResponsible_User_id = ? OR Doctor_User_id = ?;", PATIENT_TABLE);

	private Connection connection;
	
	public MySQLPatientRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public PatientId nextIdentity() {
		Integer nextIdentity = null;
		try (
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(NEXT_ID);
		){
			rs.next();
			nextIdentity = rs.getInt("NEXT_ID");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return new PatientId(nextIdentity);
	}

	@Override
	public void add(Patient aPatient) {
		try (
				PreparedStatement st = connection.prepareStatement(INSERT);
		) {
			st.setInt(1, aPatient.id().value());
			st.setString(2, aPatient.fullName().name());
			st.setString(3, aPatient.fullName().surname());			
			st.setDate(4, Date.valueOf(aPatient.birthdate()));
			st.setInt(5, aPatient.patientResponsibleId().value());
			st.setObject(6, Optional.ofNullable(
					aPatient.doctorId()).map(i -> i.value()).orElse(null), java.sql.Types.INTEGER);
			st.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Patient aPatient) {
		try (
				PreparedStatement statement = connection.prepareStatement(UPDATE);
		) {
			statement.setString(1, aPatient.fullName().name());
			statement.setString(2, aPatient.fullName().surname());
			statement.setDate(3, Date.valueOf(aPatient.birthdate()));
			if (aPatient.doctorId() == null) {
				statement.setNull(4, java.sql.Types.NULL);
			} else {
				statement.setInt(4, aPatient.doctorId().value());
			}
			statement.setInt(5, aPatient.id().value());
			
			statement.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Patient aPatient) {
		try (
			PreparedStatement statement = connection.prepareStatement(DELETE);
		) {
			statement.setInt(1, aPatient.id().value());
			statement.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<Patient> all() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Patient buildPatient(ResultSet result) throws SQLException {
		PatientId i = new PatientId(result.getInt(1));
		PatientFullName f = new PatientFullName(result.getString(2), result.getString(3));
		LocalDate birth = result.getDate(4).toLocalDate();
		UserId respon = new UserId(result.getInt(5));
		UserId doctor;
		result.getInt(6);
		
		if (result.wasNull()) {
			doctor = null;
		} else {			
			doctor = new UserId(result.getInt(6));
		}
		
		return new Patient(i, f, birth, respon, doctor);
	}

	@Override
	public Patient ofId(PatientId anId) {
		Patient patient;
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, anId.value());
			ResultSet result = statement.executeQuery();
			if(!result.next()) {
				return null;
			}
			patient = buildPatient(result);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return patient;
	}

	@Override
	public Collection<Patient> ofResponsible(UserId responsibleId) {
		Collection<Patient> patients = new ArrayList<>();
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_RESPONSIBLEID);
		) {
			statement.setInt(1, responsibleId.value());
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				patients.add(buildPatient(result));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return patients;
	}

	@Override
	public Collection<Patient> ofDoctor(UserId doctorId) {
		Collection<Patient> patients = new ArrayList<>();
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_DOCTORID);
		) {
			statement.setInt(1, doctorId.value());
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				patients.add(buildPatient(result));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return patients;
	}

	@Override
	public Collection<Patient> relatedWith(UserId userId) {
		Collection<Patient> patients = new ArrayList<>();
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_RELATED_WITH);
		) {
			statement.setInt(1, userId.value());
			statement.setInt(2, userId.value());
			ResultSet result = statement.executeQuery();
			while(result.next()) {
				patients.add(buildPatient(result));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return patients;
	}

}
