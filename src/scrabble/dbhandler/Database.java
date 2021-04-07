package scrabble.dbhandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.*;

/**
 * scrabble.dbhandler.Database class to connect to the Database, creating and
 * being able to delete tables and updating player statistics
 * 
 * @author Sergen Keskincelik
 * @author Moritz Raucher
 */

public class Database {

	protected static Connection connection = null;
	protected static Statement stmt = null;
	protected static PreparedStatement pstmt = null;

	/**
	 * Connecting to DB and creating DB File if it doesn't exist (SQLite .jar file
	 * needed!)
	 */
	public static void connectToDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "\\resources\\" + "scrabble\\db\\Scrabble15.db");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Connection failed!");
			System.exit(0);
		}
		System.out.println("Successfully connected to DB!");
	}

	/** Disconnecting from DB */
	public static void disconnectDB() {
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

	/** Creating tables for the Database */
	public static void createTables() {
		List<String> sqlstatements = new ArrayList<String>();
		sqlstatements.add("CREATE TABLE IF NOT EXISTS Players (Id INT PRIMARY KEY, Name VARCHAR(25) NOT NULL, "
				+ " GamesWon INT NOT NULL, GamesLost INT NOT NULL, Winrate DOUBLE NOT NULL);");
		sqlstatements.add("CREATE TABLE IF NOT EXISTS Statistics (Statistic_Id INT PRIMARY KEY, Player_Id references Players (ID));");

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

	/** Deleting all tables which are present in our Database */
	public static void dropAllTables() {
		dropPlayerTable();
		dropStatisticsTable();
		System.out.println("All tables dropped.");
	}

	/** Deleting Statistics Table */
	private static void dropStatisticsTable() {
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE Statistics;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Deleting Player table */
	private static void dropPlayerTable() {
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE Players;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Fills in player table with Player Data */
	public static void fillPlayerTable(List<String> playerData) {
		try {
			pstmt = connection.prepareStatement("INSERT INTO Players (Id,Name,GamesWon,GamesLost,Winrate) VALUES (?,?,?,?,?);");
			pstmt.setInt(1, Integer.parseInt(playerData.get(0)));
			pstmt.setString(2, playerData.get(1));
			pstmt.setInt(3, Integer.parseInt(playerData.get(2)));
			pstmt.setInt(4, Integer.parseInt(playerData.get(3)));
			pstmt.setDouble(5, Double.parseDouble(playerData.get(4)));
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Fills in statistics table with Statistic Data
	 * 
	 * @throws SQLException
	 */
	public static void fillStatisticTable(String[][] statisticData) throws SQLException {
		pstmt = connection.prepareStatement("INSERT INTO Statistic_Id (Statistic_Id, Player_Id) VALUES (?,?);");
		for (int i = 0; i < statisticData.length; i++) {
			pstmt.setInt(1, Integer.parseInt(statisticData[i][0]));
			pstmt.setInt(2, Integer.parseInt(statisticData[i][1]));
			pstmt.executeUpdate();
		}
	}

	/**
	 * Updating the amount of games won in your Database and in the player's object
	 */
	public static void updateGamesWon(HumanPlayer player) {
		try {
			pstmt = connection.prepareStatement("UPDATE Players SET GamesWon = ? WHERE Name = '" + player.getName() + "';");
			pstmt.setInt(1, player.getGamesWon() + 1);
			pstmt.executeUpdate();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE Name = '" + player.getName() + "';");
			while (rs.next()) {
				player.setGamesWon(rs.getInt("GamesWon"));
			}
			updateWinRate(player);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updating the amount of games lost in your Database and in the player's object
	 */
	public static void updateGamesLost(HumanPlayer player) {
		try {
			pstmt = connection.prepareStatement("UPDATE Players SET GamesLost = ? WHERE Name = '" + player.getName() + "';");
			pstmt.setInt(1, player.getGamesLost() + 1);
			pstmt.executeUpdate();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Players WHERE Name = '" + player.getName() + "';");
			while (rs.next()) {
				player.setGamesLost(rs.getInt("GamesLost"));
			}
			updateWinRate(player);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Updating the Win-Rate in the Database */
	public static void updateWinRate(HumanPlayer player) {
		try {
			pstmt = connection.prepareStatement("UPDATE Players SET Winrate = ? WHERE Name = '" + player.getName() + "';");
			pstmt.setDouble(1, player.getWinRate());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Updating the Name if Player wants to change it */
	public static void updatePlayerName(HumanPlayer player, String newName) {
		if (!DBInformation.containsName(newName)) {
			try {
				stmt = connection.createStatement();
				stmt.executeUpdate("UPDATE Players SET Name = '" + newName + "' WHERE Name = '" + player.getName() + "';");
				player.setName(newName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}

}
