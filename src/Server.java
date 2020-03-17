import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
//import java.util.*;
//import java.util.Scanner;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;

public class Server {
	private static Statement db;
	protected ServerConnection con;

	//use it to connect to database
	private static void connect() {
		try{  
			//method to register driver class. links stuff to make it work
			Class.forName("com.mysql.cj.jdbc.Driver");  
			
			//establish connection with the database, we pass username and password
			//here demo is database name, root is username and "" password  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost:3306/demo","root","26-11Zkevin"); 
			
			//the object of statement is responsible to execute queries
			db = con.createStatement();  
		}
		catch(Exception e){ 
			System.out.println(e);
		}  
	}  
	
	//use it to execute SELECT database commands
	public static ResultSet executeQuery(String exc) {
		ResultSet rs = null;
		try {
			rs=db.executeQuery(exc);  
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return rs;
	}
	
	//use it to execute OTHER database commands
	public static int executeUpdate(String exc) {
		int rs = 0;
		try {
			rs=db.executeUpdate(exc);  
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return rs;
	}
	
	//use it to set user
	public void userSelection() throws Exception {
		String selectUser;
		ResultSet r;
		con.sendMessage("Enter 0 for login, 1 for sign up");
		//general loop to catch errors. Make user able to re select login ro signup
		while (true) {
		    try {
		    	selectUser = con.receiveMessage();
		    	if ((!selectUser.equals("0")) && (!selectUser.equals("1"))){
		    		throw new Exception("\nInvalid entry, please enter 0 or 1");
		    	}else if(selectUser.equals("0")) {
		    		//get info for user
		    		String username;
		    		String password_input;
	    			con.sendMessage("LOGIN: Enter username:");
		    		username = con.receiveMessage();
		    		r = executeQuery("SELECT * FROM Users WHERE username LIKE '" +username+"';");
		    		r.next();
		    		if (!r.first()) {
		    			throw new Exception("\nUsername does not exist \nEnter 0 for login, 1 for sign up");
		    		}else {
			    		username = r.getString("username");
						String password = r.getString("password");
						String nickname = r.getString("nickname");
						int goldCoins = r.getInt("goldCoins");
						int silverCoins = r.getInt("silverCoins");
						int numLosses = r.getInt("numLosses");
						int numWins = r.getInt("numWins");
						int numWinsCombo = r.getInt("numWinsCombo");
						long datetimeLastLogin = r.getLong("datetimeLastLogin");
			    		r.close();
		    			con.sendMessage("Enter password:");
		    			password_input = con.receiveMessage();
		    			if (password_input.equals(password)) {
		    				con.sendMessage("Successful");
		    				con.sendUser(new User(username, password, nickname, goldCoins, silverCoins, numLosses, numWins, numWinsCombo, datetimeLastLogin));
		    			}else {
		    				throw new Exception("\nWrong password  \nEnter 0 for login, 1 for sign up");
		    			}
		    		}
		    	}else if(selectUser.equals("1")) {
		    		//loop for getting a unique username
		    		String usern;
		    		con.sendMessage("Enter username:");
		    		while (true) {
		    			try {
				    		usern = con.receiveMessage();
				    		r = executeQuery("SELECT * FROM Users WHERE username LIKE '" +usern+"';");
				    		r.next();
				    		if (r.first()) {
				    			r.close();
				    			throw new Exception("\nUsername already exist \nEnter username:");
				    		}
				    		break;	
		    			}
		    			catch (Exception e) {
			    			if (e.getCause() == null){
					    		con.sendMessage(e.getMessage());
					    	}else {
					    		throw new Exception("User disconnected");
					    	}
					    }
		    		}
		    		//loop to set up password that satisfies given criteria
		    		String pass = "";
		    		con.sendMessage("Enter password:");
		    		while (true) {
			    		try {
				    		pass = con.receiveMessage();
				    		if (pass.length() < 6) {
				    			throw new Exception("\nPassword must be at least six characters long \nEnter password");
				    		}
				    		break;
			    		}
			    		catch (Exception e) {
			    			if (e.getCause() == null){
					    		con.sendMessage(e.getMessage());
					    	}else {
					    		throw new Exception("User disconnected");
					    	}
					    }
		    		}
		    		while (true) {
		    			try {
		    				con.sendMessage("Enter nickname:");
		    				String nick = con.receiveMessage();
		    				con.sendMessage("Successful");
		    				con.sendMessage("Thank you for signing up. We'll give you 50 gold coins. Enjoy");
		    				con.sendUser(new User(usern, pass, nick));		    				
		    			}
		    			catch (Exception e) {
			    			if (e.getCause() == null){
					    		con.sendMessage(e.getMessage());
					    	}else {
					    		throw new Exception("User disconnected");
					    	}
		    			}
		    		}
		    	}
		    break;
		    }
		    catch (Exception e) {
		    	if (e.getCause() == null){
		    		con.sendMessage(e.getMessage());
		    	}else {
		    		throw new Exception("User disconnected");	
		    	}
		    }
	    }   
	}

	//select game mode to play. runs the game
	public void modeSelection(Game g) throws Exception {
		int selectMode;
		boolean guessed;
		//check minimum requirements to play
		if (g.user.getGold() < 5 && (g.user.getSilver()/10)+g.user.getGold() >= 5) {
			if(!tradeChoice(g)) {
				  return;
			  }
		}
		con.sendMessage("Select game mode: 0 for singleplayer, 1 for multiplayer");
		while (true) {
			try {
				selectMode = Integer.parseInt(con.receiveMessage());
				if ((selectMode != 0) && (selectMode != 1)){
					  throw new Exception("Invalid entry, please enter 0 or 1 \nSelect game mode: 0 for singleplayer, 1 for multiplayer");
				  }else if(selectMode == 0) {
			    		guessed = startGame(g);
			    		break;
				  }else {
					  //still need o implement multiplayer
					  System.out.println("online");
					  return;
				  }
			  }
			  catch (Exception e) {
				  if (e.getCause() == null){
			    		con.sendMessage(e.getMessage());
				  }else {
			    	throw new Exception("User disconnected");	
				  }
			  }
		}
		if (guessed) {
			g.user.setWin();
			g.user.setWinCombo(1);
			winCombo(g);
		}else {
			g.user.setLoss();
			g.user.setWinCombo(0);
		}
		con.sendMessage("Do you want to play again? 0 for yes, 1 for no");
		while (true) {
			try {
				selectMode = Integer.parseInt(con.receiveMessage());
				if ((selectMode != 0) && (selectMode != 1)){
					  throw new Exception("Invalid entry, please enter 0 or 1 \nDo you want to play again? 0 for yes, 1 for no");
				  }else if(selectMode == 0) {
			    		modeSelection(g);
			    		break;
				  }else {
					  return;
				  }
			  }
			 catch (Exception e) {
				 if (e.getCause() == null){
			    		con.sendMessage(e.getMessage());
				 }else {
			    	throw new Exception("User disconnected");	
			    }
			 }
		}
	}
	
	//Game main code
	public boolean startGame(Game g) throws Exception {
		  g.generateNumber();
		  g.nbGuesses = 0;
		  int entered;
		  String guessed = "";
		  con.sendMessage("Enter guess: ");
		  while (g.user.getGold() >= 5){
			  while (true) {
				  try {
					  entered = Integer.parseInt(con.receiveMessage());	
					  if (g.hasDuplicates(entered) || entered < 1000 || entered > 9999) {
						  throw new Exception("");
					  }
					  break;
				  }
				  catch (Exception e) {
					  if (e.getCause() == null){
						  con.sendMessage("Wrong input, number must be between  1000 and 9999 and not contain any duplicates \\nEnter guess:");
				    	}else {
				    		throw new Exception("User disconnected");	
				    	}
				  }
			  }
			  g.user.setGold(g.user.getGold()-5);
			  g.nbGuesses++;
			  guessed = g.checkGuess(entered);
			  if (guessed.contains("Congrats!")) {
				  con.sendMessage(guessed);
				  return true;
			  }
			  if (g.user.getGold() < 5 && (g.user.getSilver()/10)+g.user.getGold() >= 5) {
				  if(!tradeChoice(g)) {
					  return false;
				  }
			  }
			  con.sendMessage(guessed);
		  }
		  return false;
	  }
	  
	//Handles reward for winning games in a row
	public boolean winCombo(Game g) {
		int combo = g.user.getWinCombo();
		if (combo % 5 == 0) {
			g.user.setGold(g.user.getGold() + combo*50);
			return true;
		}
		return false;
	}
	
	//prompt usr to select if they want to trade or not
	public boolean tradeChoice(Game g) throws Exception {
		  int choice;
		  con.sendMessage("You don't have enough gold coins to continue \nYou can trade 10 silver for 1 gold: number of silver you have is " + g.user.getSilver() + "\nEnter 0 to finish the game, 1 to trade");
		  while (true) {
			  try {
				  choice = Integer.parseInt(con.receiveMessage());
				  if ((choice != 0) && (choice != 1)){
					  throw new Exception("Invalid entry, please enter 0 or 1");
				  }else if(choice== 0) {
					  con.sendMessage("Sorry. Come back in 24h hours");
					  return false;
				  }else {
					   if (trade(g)) {
						   return true;
					   }
				  }
			  }
			  catch (Exception e) {
				  if (e.getCause() == null){
			    		con.sendMessage(e.getMessage());
				  }else {
			    		throw new Exception("User disconnected");	
				  }
			  }
		  }
	  }
	  
	//trade code that calls user trade
	public boolean trade(Game g) throws NumberFormatException, Exception {
		  int silver = g.user.getSilver();
		  con.sendMessage("You have "+silver+" silver. You can trade and get a maximum of "+silver/10+" more gold. \nHow many gold coins do you want?");
		  g.user.Trade(Integer.parseInt(con.receiveMessage()));
		  return true;
	  }
	
	//set up MySQL connection and creates server
	public static void main(String[] args) throws Exception {
		try {
			connect();
			System.out.println("Connection to MySQL was successful");
		}
		catch (Exception e) {
			System.out.println(e);
		}
		try (var listener = new ServerSocket(12345)) {
			System.out.println("The capitalization server is running...");
			System.out.println(InetAddress.getLocalHost());
			var pool = Executors.newFixedThreadPool(4);
			while (true) {
				pool.execute(new Capitalizer(pool, listener.accept()));
			}
		}
	}
}

class Capitalizer extends Server implements Runnable {
	private Socket socket;
	public ExecutorService pool;
	public Game g;

	//Constructor to set up variable
	Capitalizer(ExecutorService pool, Socket socket) {
		this.pool = pool;
		this.socket = socket;
		this.con = new ServerConnection(socket);
	}
	
	//close the socket
	private void close() {
		try { 
			socket.close(); 
		} 
		catch (IOException e) {
			System.out.println(e);
		}
		System.out.println("Closed: " + socket);
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Connected: " + socket);
			userSelection();
			g = new Game((User)con.receiveUser());
			modeSelection(g);
		} 
		catch (Exception e) {
			System.out.println("Error:" + socket);
			System.out.println(e.getMessage());
		} 
		close();

	}
}


