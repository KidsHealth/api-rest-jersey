package es.uma.health.kids.infrastructure.persistence.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import es.uma.health.kids.domain.model.message.AppointmentRequest;
import es.uma.health.kids.domain.model.message.AppointmentRequest.Status;
import es.uma.health.kids.domain.model.message.Message;
import es.uma.health.kids.domain.model.message.MessageBody;
import es.uma.health.kids.domain.model.message.MessageId;
import es.uma.health.kids.domain.model.message.MessageRepository;
import es.uma.health.kids.domain.model.patient.PatientId;
import es.uma.health.kids.domain.model.user.UserId;

public class MySQLMessageRepository implements MessageRepository {
	
	protected final static String MESSAGE_TABLE = "Message";
	protected final static String APREQ_TABLE = "AppointmentRequest";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", MESSAGE_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?,?,?,?,?,?);", MESSAGE_TABLE);
	protected final static String UPDATE_APREQ = String.format("UPDATE %s SET updated_at = ?, status = ?;", APREQ_TABLE);
	protected final static String INSERT_APREQ = String.format("INSERT INTO %s VALUES (?,?,?,?);", APREQ_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT * FROM %s AS m " + 
			"LEFT OUTER JOIN %s AS r ON m.id = r.Message_id " + 
			"WHERE m.id = ?;", MESSAGE_TABLE, APREQ_TABLE);
	protected final static String SELECT_OF_PATIENTID = String.format("SELECT * FROM %s AS m " + 
			"LEFT OUTER JOIN %s AS r ON m.id = r.Message_id " + 
			"WHERE m.Patient_id = ?;", MESSAGE_TABLE, APREQ_TABLE);
	
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
			connection.setAutoCommit(false);
			
			st.setInt(1, aMessage.id().value());
			st.setString(2, aMessage.body().value());
			st.setTimestamp(3, java.sql.Timestamp.valueOf(aMessage.sendedAt()));
			st.setBoolean(4, aMessage.isDoctorTheSender());
			st.setInt(5, aMessage.patientId().value());
			st.setInt(6, aMessage.doctorId().value());
			st.execute();
			
			if (aMessage.isAppointmentRequest()) {
				AppointmentRequest apReq = (AppointmentRequest) aMessage;
				try(PreparedStatement st2 = connection.prepareStatement(INSERT_APREQ)) {
					st2.setString(1, apReq.updatedAt()
							.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
					st2.setInt(2, apReq.status().ordinal());
					st2.setInt(3, apReq.id().value());
					st2.setString(4, apReq.datetimeProposed()
							.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
					st2.execute();
				}
			}
			
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Message aMessage) {
		if (!aMessage.isAppointmentRequest()) return;
		AppointmentRequest apReq = (AppointmentRequest) aMessage;
		try (
				PreparedStatement st = connection.prepareStatement(UPDATE_APREQ);
		) {
			
			st.setString(1, apReq.updatedAt()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			st.setInt(2, apReq.status().ordinal());
			st.execute();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
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
			if(!result.next()) {
				return null;
			}
			
			result.getInt("Message_id");
			
			if (result.wasNull()) {
				message = buildMessage(result);
			} else {
				message = buildAppointmentRequest(result);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return message;
	}

	private AppointmentRequest buildAppointmentRequest(ResultSet result) throws SQLException {
		MessageId i = new MessageId(result.getInt(1));
		MessageBody t = new MessageBody(result.getString(2));
		LocalDateTime send = result.getTimestamp(3).toLocalDateTime();
		boolean doctor = result.getBoolean(4);
		UserId doctorId = new UserId(result.getInt(5));
		PatientId patientId = new PatientId(result.getInt(6));
		LocalDateTime proposedAt = result.getTimestamp("datetime_proposed").toLocalDateTime();
		LocalDateTime updatedAt = result.getTimestamp("updated_at").toLocalDateTime();
		Status status = Status.values()[result.getInt("status")];
		return new AppointmentRequest(
				i, t, send, doctor, doctorId, patientId, proposedAt, updatedAt, status);
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
				result.getInt("Message_id");
				
				if (result.wasNull()) {
					messages.add(buildMessage(result));
				} else {
					messages.add(buildAppointmentRequest(result));
				}
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return messages;
	}

}
