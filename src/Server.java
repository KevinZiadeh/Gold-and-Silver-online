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
	public static Game[] gamemult_2;
	public static ServerConnection[] conmult_2;

	//use it to connect to database
	private static void connect() {
		try{  
			//method to register driver class. links stuff to make it work
			Class.forName("com.mysql.cj.jdbc.Driver");  
			
			//establish connection with the database, we pass username and password
			//here demo is database name, root is username and "" password  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://db4free.net:3306/eece350project","eece350","livelove350"); 

			
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
	
	//set up MySQL connection and creates server
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Server may take some time to establish connection to database");
			connect();
			System.out.println("Connection to MySQL was successful");
		}
		catch (Exception e) {
			System.out.println(e);
		}
		try (var listener = new ServerSocket(12345)) {
			System.out.println("The capitalization server is running...");
			System.out.println(InetAddress.getLocalHost());
			var pool = Executors.newFixedThreadPool(16);
			gamemult_2 =  new Game[2];
			conmult_2 =  new ServerConnection[2];
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

	//empty constructor for extends to work
	Capitalizer(){}
	
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
		    				con.sendMessage("Successful \nThank you for signing up. Enjoy");
//		    				con.sendMessage("Thank you for signing up. We'll give you 50 gold coins. Enjoy");
		    				con.sendUser(new User(usern, pass, nick));
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
		//check minimum requirements to play
		if (g.user.getGold() < 5 && (g.user.getSilver()/10)+g.user.getGold() >= 5) {
			if(!tradeChoice(g)) {
				  return;
			  }
		}
		con.sendMessage("Select game mode: 0 for singleplayer, 1 for multiplayer(500 gold coins to enter)");
		while (true) {
			try {
				selectMode = Integer.parseInt(con.receiveMessage());
				if ((selectMode != 0) && (selectMode != 1) && (selectMode != 2)){
					throw new Exception();
				}else if(selectMode == 0) {
					//Single player mode. Generate number to guess then check if he guessed correctly
					int number = g.generateNumber();
					int guessed = startGame(g, number, con, false);
					if (guessed > 0) {
						//means that if you won, we update the number of wins and other stats accordingly
						con.sendMessage("Congrats! You won with " + guessed + " number of guesses. \n");
						g.user.setGold(g.user.getGold()+50);
						g.user.setWin();
						g.user.setWinCombo(1);
						winCombo(g);
					}else {
						g.user.setLoss();
						g.user.setWinCombo(0);
					}
					break;
				}else if(selectMode == 1){
					if (g.user.getGold()<500) {
						throw new Exception("You don't have enough to play online \nSelect game mode: 0 for singleplayer, 1 for multiplayer");
					}
					if (gamemult_2[0] == null) {
						gamemult_2[1] = null;
						gamemult_2[0] = g;	
						conmult_2[0] = con;	
						con.sendMessage("Waiting for another player");
						g.kill=true;
						//Waiting until game starts
						while (g.kill) {
							try {
								con.sendMessage("");
							}catch (Exception e){
								gamemult_2[0] = null;	
								conmult_2[0] = null;	
								throw new Exception(e.getMessage());	
							}
							Thread.sleep(1000);
						}
						//waiting until game is finished
						while (!g.kill) {
							Thread.sleep(1000);
						}
						break;
					  }else if (gamemult_2[1] == null) {
						  System.out.println(gamemult_2[0]+" "+gamemult_2[0]);
						  Game user1_game = gamemult_2[0];
						  ServerConnection user1_con = conmult_2[0];
						  gamemult_2[0] = null;
						  conmult_2[0] = null;
						  int number = g.generateNumber();
						  user1_game.kill = false;	//to start user1 game
						  con.sendMessage("Another player has been waiting for you");
						  new Multiplayer(user1_game, g, user1_con, con, number).main();
						  user1_game.kill = true;	//to kill user1 thread for it to exit the loop
						  break;
					  }
				  }
			  }
			  catch (Exception e) {
				  if (e.getCause() == null){
//					  e.printStackTrace();
					  con.sendMessage(e.getMessage());
				  }else {
					  throw new Exception(e.getMessage());	
				  }
			  }
		}
		//check if user wants to play again. Calls mode selection 
		playAgain(g);
	}
	
	//check if user wants to play again. Calls mode selection 
	public void playAgain(Game g) throws Exception {
		int selectMode;
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
	public int startGame(Game g, int number, ServerConnection con, boolean Mult) throws Exception {
		if (Mult) {
			g.user.setGold(g.user.getGold()-500);			
		}
		g.number = number;
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
						con.sendMessage("Wrong input, number must be between  1000 and 9999 and not contain any duplicates \nEnter guess:");
				    }else {
						g.user.setLoss();
						g.user.setWinCombo(0);
				    	throw new Exception("User disconnected");	
				    }
				}
			}
			g.user.setGold(g.user.getGold()-5);
			g.nbGuesses++;
			guessed = g.checkGuess(entered);
			if (guessed.contains("Congrats!")) {
				return g.nbGuesses;
			}
			if (g.user.getGold() < 5 && (g.user.getSilver()/10)+g.user.getGold() >= 5) {
				if(!tradeChoice(g)) {
					return -1;
				}
			}
			con.sendMessage(guessed);
		}
		con.sendMessage("Sorry, you don't have any gold left. Come back in 24 hours");
		return -1;
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

class Multiplayer extends Capitalizer{
	public Game user1_game;
	public Game user2_game;
	public ServerConnection user1_con;
	public ServerConnection user2_con;
	public int number;
	public int user1_guesses;
	public int user2_guesses;
	
	Multiplayer(Game user1_game, Game user2_game, ServerConnection user1_con, ServerConnection user2_con, int number){
		this.user1_game= user1_game;
		this.user2_game= user2_game;
		this.user1_con= user1_con;
		this.user2_con= user2_con;	
		this.number = number;
		this.user1_guesses = 0;
		this.user2_guesses = 0;
	}
	
	public void main() throws Exception {
		user1_con.sendMessage("Winner is the one who gets the answer in the fewest guesses possible");
		user2_con.sendMessage("Winner is the one who gets the answer in the fewest guesses possible");
		//Run user1 game in their own thread
		Thread user1_thread = new Thread(() -> {
			try {
				user1_guesses = startGame(user1_game, number, user1_con, true);
			} catch (Exception e) {
				user1_guesses = -1;
//				e.printStackTrace();
			}
		});
		user1_thread.start();
		//Run user2 game in their own thread
		Thread user2_thread = new Thread(() -> {
			try {
				user2_guesses = startGame(user2_game, number, user2_con, true);
			} catch (Exception e) {
				user2_guesses = -1;
//				e.printStackTrace();
			}
		});
		user2_thread.start();
		//wait until both players finish the game
		while (user1_guesses == 0 && user2_guesses == 0) {
			Thread.sleep(1000);
		}
		if (user1_guesses == 0 && user2_guesses > 0) {
			user2_con.sendMessage("Waiting for other player to finish \n");
		}
		else if (user2_guesses == 0 && user1_guesses > 0) {
			user1_con.sendMessage("Waiting for other player to finish \n");
		}
		while (user1_guesses == 0 || user2_guesses == 0) {
			Thread.sleep(1000);
		}
		if (user1_guesses < 0) {
			user2_con.sendMessage("Player 1 left \n");
		}
		else if (user2_guesses < 0) {
			user1_con.sendMessage("Player 2 left \n");
		}
		
		//handles winning logic
		if (user1_guesses == user2_guesses) {
			user1_con.sendMessage("It was a tie. You won 500 gold! \n");
			user1_game.user.setGold(user1_game.user.getGold()+500);
			user2_con.sendMessage("It was a tie. You won 500 gold! \n");
			user2_game.user.setGold(user2_game.user.getGold()+500);
		}
		else if (user1_guesses > 0 && (user1_guesses < user2_guesses || user2_guesses < 0)) {
			user1_con.sendMessage("Congrats! You won 1000 gold! \n");
			user1_game.user.setGold(user1_game.user.getGold()+1000);
			user1_game.user.setWin();
			user1_game.user.setWinCombo(1);
			winCombo(user1_game);
			if (user2_guesses < 0) return;
			user2_con.sendMessage("Sorry, you lost \n");
			user2_game.user.setLoss();
			user2_game.user.setWinCombo(0);
		}
		else {
			user2_con.sendMessage("Congrats! You won 1000 gold! \n");
			user2_game.user.setGold(user2_game.user.getGold()+1000);
			user2_game.user.setWin();
			user2_game.user.setWinCombo(1);
			winCombo(user2_game);
			if (user1_guesses < 0) return;
			user1_con.sendMessage("Sorry, you lost \n");
			user1_game.user.setLoss();
			user1_game.user.setWinCombo(0);
		}
	}
}
