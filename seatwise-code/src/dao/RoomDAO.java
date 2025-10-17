package dao;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class RoomDAO {
	public void insertRoom(Room room) throws SQLException {
		String sql = "INSERT INTO Room (room_id, capacity, is_backup) VALUES (?, ?, ?) " +
					 "ON DUPLICATE KEY UPDATE capacity = VALUES(capacity), is_backup = VALUES(is_backup)";
		try (Connection con = DatabaseConnection.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, room.getRoomId());
			ps.setInt(2, room.getCapacity());
			ps.setBoolean(3, room.isBackup());
			ps.executeUpdate();
		}
	}
	public List<Room> getAllRooms() throws SQLException {
		List<Room> rooms = new ArrayList<>();
		String sql = "SELECT room_id, capacity, is_backup FROM Room";
		try (Connection con = DatabaseConnection.getConnection();
			 Statement st = con.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				rooms.add(new Room(rs.getString("room_id"), rs.getInt("capacity"), rs.getBoolean("is_backup")));
			}
		}
		return rooms;
	}
}
