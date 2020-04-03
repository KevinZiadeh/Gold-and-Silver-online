import java.time.*;
//import java.sql.*;

public class User implements java.io.Serializable{
	/**
	 * serialVersionUID is auto generated code to remove warning. 
	 * Possible to remove the variable as it won't cause an error.
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String nickname;
	private int goldCoins;
	private int silverCoins;
	private int numLosses;
	private int numWins;
	private int numWinsCombo;
	private long datetimeLastLogin;
	          
            	
	//User constructor. Set initial values to 0. Set time in a way to be able to compare. 
	///You can google Epoch time
	User(String username, String password, String nickname){
		this.username = username;
		this.setPassword(password);
		this.nickname = nickname;
		this.goldCoins = 50;
		this.silverCoins = 0;
		this.numLosses = 0;
		this.numWins = 0;
		this.numWinsCombo = 0;
		LocalDateTime date = LocalDateTime.now();
		ZoneOffset  zone = ZoneOffset.UTC;
		this.datetimeLastLogin = date.toEpochSecond(zone);
		String query = "INSERT INTO `eece350project`.`Users` (`username`, `password`, `nickname`, `goldCoins` ,`datetimeLastLogin`) VALUES ('"+username+"', '"+password+"', '"+nickname+"', '"+this.goldCoins+"', '"+this.datetimeLastLogin+"');";
		Server.executeUpdate(query);
	}
	
	//User constructor if user already exists in database. r contains all the info we need.
	//Compare current time with last time to reward 50 gold accordingly. Store the time once a day
//	User(ResultSet r){
//		try {
//			this.username = r.getString("username");
//			this.password = r.getString("password");
//			this.nickname = r.getString("nickname");
//			this.silverCoins = r.getInt("silverCoins");
//			this.numLosses = r.getInt("numLosses");
//			this.numWins = r.getInt("numWins");
//			this.numWinsCombo = r.getInt("numWinsCombo");
//			LocalDateTime date = LocalDateTime.now();
//			ZoneOffset  zone = ZoneOffset.UTC;
//			long previousTime = r.getLong("datetimeLastLogin");
//			this.datetimeLastLogin = date.toEpochSecond(zone);
//			if (this.datetimeLastLogin-previousTime >= 86400) {
//				this.goldCoins = r.getInt("goldCoins")+50;
//			}else {
//				this.datetimeLastLogin = previousTime;
//				this.goldCoins = r.getInt("goldCoins");
//			}
//			String query = "UPDATE `eece350project`.`Users` SET `goldCoins` = '"+this.goldCoins+"', `datetimeLastLogin` = '"+this.datetimeLastLogin+"' WHERE (`username` = '"+this.username+"');";
//			Server.executeUpdate(query);
//			r.close();
//		}
//		catch (Exception e) {
//			System.out.println(e);
//		}
//	}
	
	//User constructor if user already exists in database. we passs all the info we need to fix ResultSet error
	//Compare current time with last time to reward 50 gold accordingly. Store the time once a day
	public User(String username, String password, String nickname, int goldCoins, int silverCoins, int numLosses,
			int numWins, int numWinsCombo, long datetimeLastLogin) {
		try {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.silverCoins = silverCoins;
		this.numLosses = numLosses;
		this.numWins = numWins;
		this.numWinsCombo = numWinsCombo;
		LocalDateTime date = LocalDateTime.now();
		ZoneOffset  zone = ZoneOffset.UTC;
		long previousTime = datetimeLastLogin;
		this.datetimeLastLogin = date.toEpochSecond(zone);
		if (this.datetimeLastLogin-previousTime >= 86400) {
			this.goldCoins = goldCoins+50;
		}else {
			this.datetimeLastLogin = previousTime;
			this.goldCoins = goldCoins;
		}
		String query = "UPDATE `eece350project`.`Users` SET `goldCoins` = '"+this.goldCoins+"', `datetimeLastLogin` = '"+this.datetimeLastLogin+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	catch (Exception e) {
		System.out.println(e);
	}
	}

	//return user as a string
	public String getString() {
		return ("Welcome " + nickname + ". You have " + goldCoins + " gold coins and " + silverCoins + " silver coins."); 
	}
	
	//Trade 10 silver coins for 1 gold. Throws error if not enough silver, return true otherwise
	public boolean Trade(int n) throws Exception {
		if (this.silverCoins < n*10) {
			throw new Exception("You don't have enough silver coins \nPlease enter 0 or 1");
		}else {
			String query;
			this.goldCoins = this.goldCoins + n;
			this.silverCoins = this.silverCoins - 10*n;
			query = "UPDATE `eece350project`.`Users` SET `goldCoins` = '"+this.goldCoins+"', `silverCoins` = '"+this.silverCoins+"' WHERE (`username` = '"+this.username+"');";
			Server.executeUpdate(query);
			return true;
		}
	}
	
	//Getter
	public String getPassword() {
		return password;
	}
	
	//Setter
	public void setPassword(String password) {
		this.password = password;
	}
	
	//Getter
	public int getGold() {
		return this.goldCoins;
	}
	
	//Setter
	public void setGold(int x) {
		this.goldCoins = x;
		String query = "UPDATE `eece350project`.`Users` SET `goldCoins` = '"+this.goldCoins+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	
	//Getter
	public int getSilver() {
		return this.silverCoins;
	}
	
	//Setter
	public void setSilver(int x) {
		this.silverCoins = x;
		String query = "UPDATE `eece350project`.`Users` SET `silverCoins` = '"+this.silverCoins+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	
	//Getter
	public int getLoss() {
		return this.numLosses;
	}
	
	//Setter
	public void setLoss() {
		this.numLosses++;
		String query = "UPDATE `eece350project`.`Users` SET `numLosses` = '"+this.numLosses+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	
	//Getter
	public int getWins() {
		return this.numWins;
	}
	
	//Setter
	public void setWin() {
		this.numWins++;
		String query = "UPDATE `eece350project`.`Users` SET `numWins` = '"+this.numWins+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	
	//Getter
	public int getWinCombo() {
		return this.numWinsCombo;
	}
	
	//Setter to x if x > 0, or to 0 if x < 0
	public void setWinCombo(int x) {
		if (x > 0) {
			this.numWinsCombo++;			
		}else{
			this.numWinsCombo = 0;
		}
		String query = "UPDATE `eece350project`.`Users` SET `numWinsCombo` = '"+this.numWinsCombo+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	
	//cheat that we can use to get 500 gold.
	public void generate() {
		this.goldCoins = this.goldCoins + 500;
		String query = "UPDATE `eece350project`.`Users` SET `goldCoins` = '"+this.goldCoins+"' WHERE (`username` = '"+this.username+"');";
		Server.executeUpdate(query);
	}
	
}  
