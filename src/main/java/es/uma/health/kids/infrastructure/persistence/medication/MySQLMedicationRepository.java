package es.uma.health.kids.infrastructure.persistence.medication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import es.uma.health.kids.domain.model.diseasecontraction.DiseaseContractionId;
import es.uma.health.kids.domain.model.medication.Medication;
import es.uma.health.kids.domain.model.medication.MedicationId;
import es.uma.health.kids.domain.model.medication.MedicationRepository;
import es.uma.health.kids.domain.model.medication.MedicineCommercialName;
import es.uma.health.kids.domain.model.medication.MedicineName;
import es.uma.health.kids.domain.model.medication.Posology;
import es.uma.health.kids.domain.model.medication.Timing;

public class MySQLMedicationRepository implements MedicationRepository {
	
	protected final static String MEDICATION_TABLE = "Medication";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", MEDICATION_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?,?,?,?,?,?,?,?);", MEDICATION_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT * FROM %s WHERE id = ?;", MEDICATION_TABLE);
	protected final static String SELECT_OF_DIAGNOSISID = String.format("SELECT * FROM %s WHERE DiseaseContraction_id = ?;", MEDICATION_TABLE);

	private Connection connection;
	
	public MySQLMedicationRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public MedicationId nextIdentity() {
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
		return new MedicationId(nextIdentity);
	}

	@Override
	public void add(Medication aMedication) {
		try (
				PreparedStatement st = connection.prepareStatement(INSERT);
		) {
			st.setInt(1, aMedication.id().value());
			st.setString(2, aMedication.medicineName().value());
			st.setString(3, aMedication.medicineCommercialName().value());
			st.setInt(4, aMedication.timing().value());
			st.setDouble(5, aMedication.posology().value());
			st.setDate(6, java.sql.Date.valueOf(aMedication.startedAt()));
			st.setDate(7, java.sql.Date.valueOf(aMedication.endedAt()));
			st.setInt(8, aMedication.diseaseContractionId().value());
			st.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Medication aMedication) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Medication aMedication) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Medication> all() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Medication ofId(MedicationId anId) {
		Medication medication;
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, anId.value());
			ResultSet result = statement.executeQuery();
			result.next();
			
			medication = buildMedication(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return medication;
	}

	private Medication buildMedication(ResultSet result) throws SQLException {
		MedicationId mi = new MedicationId(result.getInt(1));
		MedicineName name = new MedicineName(result.getString(2));
		MedicineCommercialName comn = new MedicineCommercialName(result.getString(3));
		Timing t = new Timing(result.getInt(4));
		Posology p = new Posology(result.getInt(5));
		LocalDate start = result.getDate(6).toLocalDate();
		LocalDate end = result.getDate(7).toLocalDate();
		DiseaseContractionId d = new DiseaseContractionId(result.getInt(8));
		return new Medication(mi, d, name, comn, t, p, start, end);
	}

	@Override
	public Collection<Medication> ofDiagnosis(DiseaseContractionId id) {
		Collection<Medication> medications = new ArrayList<>();
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_DIAGNOSISID);
		) {
			statement.setInt(1, id.value());
			ResultSet result = statement.executeQuery();
			result.next();
			
			while (result.next()) {
				medications.add(buildMedication(result));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return medications;
	}

}
