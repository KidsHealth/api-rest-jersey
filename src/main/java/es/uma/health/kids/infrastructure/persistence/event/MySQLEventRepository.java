package es.uma.health.kids.infrastructure.persistence.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collection;

import es.uma.health.kids.domain.model.event.Event;
import es.uma.health.kids.domain.model.event.EventDescription;
import es.uma.health.kids.domain.model.event.EventId;
import es.uma.health.kids.domain.model.event.EventRepository;
import es.uma.health.kids.domain.model.event.EventTitle;
import es.uma.health.kids.domain.model.event.EventTopic;
import es.uma.health.kids.domain.model.event.EventVenue;
import es.uma.health.kids.domain.model.message.MessageId;
import es.uma.health.kids.domain.model.patient.PatientId;

public class MySQLEventRepository implements EventRepository {

	protected final static String EVENT_TABLE = "Event";
	protected final static String NEXT_ID = String.format("select max(id) + 1 as NEXT_ID from %s;", EVENT_TABLE);
	protected final static String INSERT = String.format("INSERT INTO %s VALUES (?,?,?,?,?,?,?,?);", EVENT_TABLE);
	protected final static String DELETE = String.format("DELETE FROM %s WHERE id = ?;", EVENT_TABLE);
	protected final static String SELECT_OF_ID = String.format("SELECT * FROM %s WHERE id = ?;", EVENT_TABLE);
	protected final static String SELECT_OF_APPOINTMENTID = String
			.format("SELECT * FROM %s WHERE description = ? AND topic = 'Appointments';", EVENT_TABLE);

	private Connection connection;

	public MySQLEventRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public EventId nextIdentity() {
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
		return new EventId(nextIdentity);
	}

	@Override
	public void add(Event anEvent) {
		try (
				PreparedStatement st = connection.prepareStatement(INSERT);
		) {
			st.setInt(1, anEvent.id().value());
			st.setString(2, anEvent.title().value());
			st.setString(3, anEvent.description().value());
			st.setString(4, anEvent.venue().value());
			st.setTimestamp(5, java.sql.Timestamp.valueOf(anEvent.startDatetime()));
			st.setTimestamp(6, java.sql.Timestamp.valueOf(anEvent.endDatetime()));
			st.setString(7, anEvent.topic().value());
			st.setInt(8,anEvent.patientId().value());
			st.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Event anEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Event anEvent) {
		try (
			PreparedStatement statement = connection.prepareStatement(DELETE);
		) {
			statement.setInt(1, anEvent.id().value());
			statement.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<Event> all() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Event ofId(EventId anId) {
		Event event;
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_ID);
		) {
			statement.setInt(1, anId.value());
			ResultSet result = statement.executeQuery();
			if(!result.next()) {
				return null;
			}
			
			event = buildEvent(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return event;
	}

	private Event buildEvent(ResultSet result) throws SQLException {
		Event event;
		EventId i = new EventId(result.getInt(1));
		EventTitle t = new EventTitle(result.getString(2));
		EventDescription d = new EventDescription(result.getString(3));
		EventVenue v = new EventVenue(result.getString(4));
		LocalDateTime start = result.getTimestamp(5).toLocalDateTime();
		LocalDateTime end = result.getTimestamp(6).toLocalDateTime();
		EventTopic c = new EventTopic(result.getString(7));
		PatientId p = new PatientId(result.getInt(8));

		event = new Event(i, t, d, v, c,start,end,p);
		event.patientId(p);
		return event;
	}

	@Override
	public Event ofAppointment(MessageId appointmentId) {
		Event event;
		try (
			PreparedStatement statement = connection.prepareStatement(SELECT_OF_APPOINTMENTID);
		) {
			statement.setInt(1, appointmentId.value());
			ResultSet result = statement.executeQuery();
			if(!result.next()) {
				return null;
			}
			
			event = buildEvent(result);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return event;
	}
	
}
