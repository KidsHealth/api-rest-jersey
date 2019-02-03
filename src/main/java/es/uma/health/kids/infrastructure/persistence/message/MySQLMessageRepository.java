package es.uma.health.kids.infrastructure.persistence.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import es.uma.health.kids.domain.model.message.Message;
import es.uma.health.kids.domain.model.message.MessageBody;
import es.uma.health.kids.domain.model.message.MessageId;
import es.uma.health.kids.domain.model.message.MessageRepository;
import es.uma.health.kids.domain.model.patient.PatientId;
import es.uma.health.kids.domain.model.user.UserId;

public class MySQLMessageRepository implements MessageRepository {
	
	protected final static String MESSAGE_TABLE = "Message";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", MESSAGE_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?,?,?,?,?,?);", MESSAGE_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT * FROM %s WHERE id = ?;", MESSAGE_TABLE);
	protected final static String SELECT_OF_PATIENTID = String.format("SELECT * FROM %s WHERE Patient_id = ?;", MESSAGE_TABLE);

	private Connection connection;
	
	public MySQLMessageRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public MessageId nextIdentity() {
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
		return new MessageId(nextIdentity);
	}

	@Override
	public void add(Message aMessage) {
		try (
				PreparedStatement st = connection.prepareStatement(INSERT);
		) {
			st.setInt(1, aMessage.id().value());
			st.setString(2, aMessage.body().value());
			st.setTimestamp(3, java.sql.Timestamp.valueOf(aMessage.sendedAt()));
			st.setBoolean(4, aMessage.isDoctorTheSender());
			st.setInt(5, aMessage.patientId().value());
			st.setInt(6, aMessage.doctorId().value());
			st.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Message aMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Message aMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Message> all() {
		// TODO Auto-generated method stub
		return null;
	}

	private Message buildMessage(ResultSet result) throws SQLException {
		MessageId i = new MessageId(result.getInt(1));
		MessageBody t = new MessageBody(result.getString(2));
		LocalDateTime send = result.getTimestamp(3).toLocalDateTime();
		boolean doctor = result.getBoolean(4);
		UserId doctorId = new UserId(result.getInt(5));
		PatientId patientId = new PatientId(result.getInt(6));
		return new Message(i, t, send, doctor, doctorId, patientId);
	}
	
	@Override
	public Message ofId(MessageId anId) {
		Message message;
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, anId.value());
			ResultSet result = statement.executeQuery();
			result.next();
			message = buildMessage(result);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return message;
	}

	@Override
	public Collection<Message> ofPatient(PatientId patientId) {
		Collection<Message> messages = new ArrayList<>();
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_PATIENTID);
		) {
			statement.setInt(1, patientId.value());
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				messages.add(buildMessage(result));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return messages;
	}

}
