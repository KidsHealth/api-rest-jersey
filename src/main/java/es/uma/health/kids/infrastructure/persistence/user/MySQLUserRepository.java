package es.uma.health.kids.infrastructure.persistence.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import es.uma.health.kids.domain.model.user.Address;
import es.uma.health.kids.domain.model.user.Doctor;
import es.uma.health.kids.domain.model.user.Email;
import es.uma.health.kids.domain.model.user.Password;
import es.uma.health.kids.domain.model.user.PatientResponsible;
import es.uma.health.kids.domain.model.user.PhoneNumber;
import es.uma.health.kids.domain.model.user.User;
import es.uma.health.kids.domain.model.user.UserFullName;
import es.uma.health.kids.domain.model.user.UserId;
import es.uma.health.kids.domain.model.user.UserRepository;


public class MySQLUserRepository implements UserRepository {

	protected final static String USER_TABLE = "User";
	protected final static String DOCTOR_TABLE = "Doctor";
	protected final static String PATIENTRESPONSIBLE_TABLE = "PatientResponsible";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", USER_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?, ?, ?, ?);", USER_TABLE);
	protected final static String INSERT_DOCTOR = String.format("INSERT INTO %s VALUES (?);", DOCTOR_TABLE);
	protected final static String INSERT_PATIENTRESPONSIBLE = String.format("INSERT INTO %s VALUES (?, ?, ?);", PATIENTRESPONSIBLE_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT u.*, d.User_id AS doctor_id, pr.User_id AS responsible_id, pr.* " + 
			"FROM %s AS u\n" + 
			"LEFT OUTER JOIN %s AS d ON u.id = d.User_id\n" + 
			"LEFT OUTER JOIN %s AS pr ON u.id = pr.User_id\n" + 
			"WHERE u.id = ?;", USER_TABLE, DOCTOR_TABLE, PATIENTRESPONSIBLE_TABLE);

	private Connection connection;
	
	public MySQLUserRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public UserId nextIdentity() {
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
		return new UserId(nextIdentity);
	}

	@Override
	public User ofId(UserId anId) {
		User user;
		try (
				PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, anId.value());
			ResultSet result = statement.executeQuery();
			
			if(!result.next()) {
				return null;
			}
			
			result.getInt("doctor_id");
			if (result.wasNull()) {
				user = new PatientResponsible(
						new UserId(result.getInt("id")), new UserFullName(result.getString("name"), 
								result.getString("surname")), new Email(result.getString("email")), 
								new Password(result.getString("password")), new Address(result.getString("address")),
								new PhoneNumber(result.getString("phone_number")));
			} else {
				user = new Doctor(new UserId(result.getInt("id")), new UserFullName(result.getString("name"), 
						result.getString("surname")), new Email(result.getString("email")), 
								new Password(result.getString("password")));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return user;
	}

	@Override
	public void add(User user) {
		try (
				PreparedStatement st = connection.prepareStatement(INSERT);
		) {
			connection.setAutoCommit(false);
			
			st.setInt(1, user.id().value());
			st.setString(2, user.fullName().name());
			st.setString(3, user.fullName().surname());
			st.setString(4, user.email().value());
			st.setString(5, user.password().value());
			st.execute();
			
			if (user.isDoctor()) {
				try(PreparedStatement st2 = connection.prepareStatement(INSERT_DOCTOR)) {
					st2.setInt(1, user.id().value());
					st2.execute();
				}
			} else {
				try(PreparedStatement st2 = connection.prepareStatement(INSERT_PATIENTRESPONSIBLE)) {
					PatientResponsible pResp = (PatientResponsible) user;
					st2.setString(1, pResp.phoneNumber().value());
					st2.setString(2, pResp.address().value());
					st2.setInt(3, user.id().value());
					st2.execute();
				}
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
