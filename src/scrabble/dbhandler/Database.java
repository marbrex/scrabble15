package scrabble.dbhandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.*;

public class Database {

	static Connection connection = null;
	private static Statement stmt = null;
	private static PreparedStatement pstmt = null;

	// Connecting to DB and creating DB File if it doesn't exist (SQLite .jar file
	// needed!)
	public void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(
					"jdbc:sqlite:" + System.getProperty("user.dir") + "\\resources\\" + "scrabble\\db\\Scrabble15.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Connection failed!");
			System.exit(0);
		}
		System.out.println("Successfully connected to DB!");
	}

	// Disconnecting from DB
	public void disconnectDB() {
		try {
			stmt = null;
			pstmt = null;
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Disconnected from DB");
	}

	// Necessary to fill them with player statistics
	public void createTables() {
		List<String> sqlstatements = new ArrayList<String>();
		sqlstatements.add("CREATE TABLE IF NOT EXISTS Players (Id INT PRIMARY KEY, Name VARCHAR(25) NOT NULL, "
				+ " GamesWon INT NOT NULL, GamesLost INT NOT NULL, Winrate DOUBLE NOT NULL);");
		sqlstatements.add("CREATE TABLE IF NOT EXISTS Rooms (RoomId INT PRIMARY KEY, Name VARCHAR(25) NOT NULL);");
		sqlstatements
				.add("CREATE TABLE IF NOT EXISTS PlayerinRoom (Id INT PRIMARY KEY, Player_Id references Players (Id), "
						+ "Room_Id references Rooms (RoomId));");
		try {
			stmt = connection.createStatement();
			for (String sql : sqlstatements) {
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Deleting all tables which are present in our Database
	public void dropAllTables() {
		dropPlayerTable();
		dropRoomTable();
		dropPlayerinRoomTable();
		System.out.println("All tables dropped.");
	}

	// Deleting Room Table
	private void dropRoomTable() {
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE Rooms;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Deleting Player table
	private void dropPlayerTable() {
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE Players;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Deleting Player in Room table
	private void dropPlayerinRoomTable() {
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE PlayerinRoom;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Necessary for general game statistics
	public void fillPlayerTable(String[][] playerData) {
		try {
			pstmt = connection
					.prepareStatement("INSERT INTO Players (Id,Name,GamesWon,GamesLost,Winrate) VALUES (?,?,?,?,?);");
			for (int i = 0; i < playerData.length; i++) {
				pstmt.setInt(1, Integer.parseInt(playerData[i][0]));
				pstmt.setString(2, playerData[i][1]);
				pstmt.setInt(3, Integer.parseInt(playerData[i][2]));
				pstmt.setInt(4, Integer.parseInt(playerData[i][3]));
				pstmt.setDouble(5, Double.parseDouble(playerData[i][4]));
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Returns a List, which contains all Player names saved in the Database
	public List<String> getPlayerNames() {
		try {
			List<String> playernames = new ArrayList<String>();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Name FROM Players;");
			while (rs.next()) {
				playernames.add(rs.getString("Name"));
			}
			return playernames;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	// Updating the amount of games won in your Database and in the player's object
	public void updateGamesWon(HumanPlayer player) {
		try {
			pstmt = connection
					.prepareStatement("UPDATE Players SET GamesWon = ? WHERE Name = '" + player.getName() + "';");
			pstmt.setInt(1, player.getGamesWon() + 1);
			pstmt.executeUpdate();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE Name = '" + player.getName() + "';");
			while (rs.next()) {
				player.setGamesWon(rs.getInt("GamesWon"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Necessary to check if someone in the Network game has already the name
	public boolean checkDoubleNames(String name) {
		List<String> playerNames = getPlayerNames();
		if (playerNames.contains(name)) {
			return true;
		} else {
			return false;
		}

	}

}
