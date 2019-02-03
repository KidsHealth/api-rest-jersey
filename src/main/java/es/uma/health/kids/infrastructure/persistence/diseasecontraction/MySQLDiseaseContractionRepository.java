package es.uma.health.kids.infrastructure.persistence.diseasecontraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import es.uma.health.kids.domain.model.diseasecontraction.DiseaseContraction;
import es.uma.health.kids.domain.model.diseasecontraction.DiseaseContractionId;
import es.uma.health.kids.domain.model.diseasecontraction.DiseaseContractionRepository;
import es.uma.health.kids.domain.model.diseasecontraction.DiseaseName;
import es.uma.health.kids.domain.model.diseasecontraction.DiseaseShortName;
import es.uma.health.kids.domain.model.patient.PatientId;

public class MySQLDiseaseContractionRepository implements DiseaseContractionRepository {
	
	protected final static String DISEASECONTRACTION_TABLE = "DiseaseContraction";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", DISEASECONTRACTION_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?,?,?,?,?);", DISEASECONTRACTION_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT * FROM %s WHERE id = ?;", DISEASECONTRACTION_TABLE);

	private Connection connection;
	
	public MySQLDiseaseContractionRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public DiseaseContractionId nextIdentity() {
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
		return new DiseaseContractionId(nextIdentity);
	}

	@Override
	public void add(DiseaseContraction aDiseaseContraction) {
		try (
				PreparedStatement st = connection.prepareStatement(INSERT);
		) {
			st.setInt(1, aDiseaseContraction.patientId().value());
			st.setInt(2, aDiseaseContraction.id().value());
			st.setString(3, aDiseaseContraction.diseaseName().stringName());
			st.setString(4, aDiseaseContraction.diseaseShortName().value());
			st.setDate(5, java.sql.Date.valueOf(aDiseaseContraction.diagnosedAt().toLocalDate()));
			st.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(DiseaseContraction aDiseaseContraction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(DiseaseContraction aDiseaseContraction) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<DiseaseContraction> all() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiseaseContraction ofId(DiseaseContractionId anId) {
		DiseaseContraction diseaseContraction;
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, anId.value());
			ResultSet result = statement.executeQuery();
			result.next();
			
			diseaseContraction = buildDiseaseContraction(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return diseaseContraction;
	}

	private DiseaseContraction buildDiseaseContraction(ResultSet result) throws SQLException {
		PatientId p = new PatientId(result.getInt(1));
		DiseaseContractionId di = new DiseaseContractionId(result.getInt(2));
		DiseaseName n = new DiseaseName(result.getString(3));
		DiseaseShortName sn = new DiseaseShortName(result.getString(4));
		LocalDateTime ld = result.getTimestamp(5).toLocalDateTime();
		return new DiseaseContraction(di, ld, n, sn, p);
	}

	@Override
	public Collection<DiseaseContraction> ofPatient(PatientId patientId) {
		Collection<DiseaseContraction> diseaseContractions = new ArrayList<>();
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, patientId.value());
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				diseaseContractions.add(buildDiseaseContraction(result));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return diseaseContractions;
	}

}
