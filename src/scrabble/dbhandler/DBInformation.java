package scrabble.dbhandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import scrabble.model.HumanPlayer;

public class DBInformation extends Database {

	/** Player profiles to choose from in UI */
	public List<HumanPlayer> getPlayerProfiles() {
		try {
			List<HumanPlayer> playerProfiles = new ArrayList<HumanPlayer>();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Players;");
			while (rs.next()) {
				HumanPlayer player = new HumanPlayer();
				player.setName(rs.getString("Name"));
				player.setGamesWon(rs.getInt("GamesWon"));
				player.setGamesLost(rs.getInt("GamesLost"));
				playerProfiles.add(player);
			}
			return playerProfiles;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/** Loading player profile by selecting profile in the UI */
	public HumanPlayer loadPlayerProfile(HumanPlayer hp) {
		List<HumanPlayer> playerProfiles = getPlayerProfiles();
		for (HumanPlayer player : playerProfiles) {
			if (player.getName().equals(hp.getName())) {
				return player;
			}
		}
		return null;
	}

	/** Checks, if Database contains already the name (e.g. for Player Profile) */
	public boolean containsName(String name) {
		List<String> playerNames = getPlayerNames();
		if (playerNames.contains(name)) {
			return true;
		} else {
			return false;
		}

	}

	/** Returns a List, which contains all Player names saved in the Database */
	private List<String> getPlayerNames() {
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

	/** Returns one specific Identification number from Statistics table */
	public boolean containsIdentification(int id) {
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Statistic_Id FROM Statistics;");
			while (rs.next()) {
				if (id == rs.getInt("Statistic_Id")) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	
	/** Returns all statistics of an Player in a List */
	public List<String> getPlayerStatistic(HumanPlayer player) {
		List<String> playerStatistic = new ArrayList<String>();
		HumanPlayer hp = loadPlayerProfile(player);
		playerStatistic.add(hp.getName());
		playerStatistic.add(Integer.toString(hp.getGamesWon()));
		playerStatistic.add(Integer.toString(hp.getGamesLost()));
		playerStatistic.add(Double.toString(hp.getWinRate()));
		return playerStatistic;
	}
	/** Returns the amount of the games won in total from all player */
	public int getMostGamesWon() {
		int max = 0;
		List<HumanPlayer> players = getPlayerProfiles();
		for(HumanPlayer player : players) {
			if(max < player.getGamesWon()) {
				max = player.getGamesWon();
			}
		}
		return max;
	}
	/** Returns the highest Win-Rate in total from a player */
	public double getHighestWinrate() {
		double max = 0;
		List<HumanPlayer> players = getPlayerProfiles();
		for(HumanPlayer player : players) {
			if(max < player.getWinRate()) {
				max = player.getWinRate();
			}
		}
		return max;
	}		

}
