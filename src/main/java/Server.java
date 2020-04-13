import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Server {
	private static Statement db;
	protected ServerConnection con;
	public static Game[] gamemult_2;
	public static ServerConnection[] conmult_2;

	//use it to connect to database
	private static void connect() throws Exception {
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
			throw new Exception(e);
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
	public User user;

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
	
	
	public boolean authenticateUser() throws Exception {
		//get info for user
		ResultSet r;
		while (true) {
			String selection = con.receiveMessage();
			if (selection.equals("leave")) return false; 		//client exited completly
			else if (selection.equals("login")) {
				String username = con.receiveMessage();
				String password_input = con.receiveMessage();
				r = executeQuery("SELECT * FROM Users WHERE username LIKE '" +username+"';");
				r.next();
				if (!r.first()) {
					con.sendMessage("ERROR: Username does not exist.");
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
					if (password_input.equals(password)) {
						con.sendMessage("SUCCESS");
						user = new User(username, password, nickname, goldCoins, silverCoins, numLosses, numWins, numWinsCombo, datetimeLastLogin);
						con.sendUser(user);
						break;
					}else {
						con.sendMessage("ERROR: Wrong password");
					}
				}
			}else if(selection.equals("signup")){
                                String username = con.receiveMessage();
                                String password = con.receiveMessage();
                                String nickname = con.receiveMessage();
                                r = executeQuery("SELECT * FROM Users WHERE username LIKE '" +username+"';");
                                r.next();
                                if (r.first()) {
                                        r.close();
                                        con.sendMessage("ERROR: Username already exist");
                                }else {
                                        con.sendMessage("SUCCESS");
                                        user = new User(username, password, nickname);
                                        con.sendUser(user);
                                        break;
                                }	
			}
		}
		return true;
	}
	
	
	public boolean mainMenu() throws Exception {
		g = new Game(user);
		String msg;
		while (true) {
			msg = con.receiveMessage();
			if (msg.equals("leave")) return false; 		//client exited complety
			if (msg.equals("trade")) {
				try {
					user.Trade(1); //throws an exception if not possible
					con.sendMessage("ok");
				}catch (Exception e) {
					con.sendMessage("ERROR: " + e.getMessage());
				}
				continue;
			}else if (msg.equals("single")) {
				if (user.getGold() < 5) {
					con.sendMessage("ERROR: You don't have enough gold to play.");
					continue;
				}
				con.sendMessage("ok");
				int number = g.generateNumber();
				int guessed = startGame(g, number, con, false);
				if (guessed == -666) return false;		//client exited complety
				if (guessed == 0) {
					continue;
				}
				else if (guessed > 0) {
					//means that if you won, we update the number of wins and other stats accordingly
					con.sendMessage("SUCCESS! You won with " + guessed + " guesses.\n");
					user.setGold(user.getGold()+50);
					user.setWin();
					user.setWinCombo(1);
					winCombo(g, con);
				}else {
					user.setLoss();
					user.setWinCombo(0);
				}
			}
			else if(msg.equals("multi")) {
/*
 * We use gamemult_2 array to keep track of the state of players
 * gamemult_2[0]:	if it is NOT null => 	player 1 is in the waiting area
 * gamemult_2[0]:	if it IS null =>		player 2 is in waiting area and set it to null to make it possible for others to play
 * 											OR there is no player 1 yet
 * gamemult_2[1]:	if it is NOT null =>	player 1 left the waiting area
 * gamemult_2[1]:	if it IS null =>		player 1 is inside waiting area AND pressed ready
 * */
				if (user.getGold()<500) {
					con.sendMessage("ERROR: You don't have enough to play online");
					continue;
				}
				if (gamemult_2[0] == null) {
					gamemult_2[0] = g;	
					conmult_2[0] = con;	
					gamemult_2[1] = null;
					con.sendMessage("You are Player 1. Waiting for Player 2.");
					g.kill=true;				//to stall player 2 if ready before player 1
					msg = con.receiveMessage();
					if (msg.equals("leave")) {
						gamemult_2[0] = null;	
						conmult_2[0] = null;	
						gamemult_2[1] = g;
						return false; 		//client exited complety
					}
					if (msg.equals("exit")) {
						gamemult_2[0] = null;	
						conmult_2[0] = null;	
						gamemult_2[1] = g;
						continue;
					}
					g.kill = false;
					//waiting until game is finished
					while (!g.kill) {
						Thread.sleep(1000);
					}
				  }else if (gamemult_2[0] != null) {
					  Game user1_game = gamemult_2[0];
					  ServerConnection user1_con = conmult_2[0];
					  gamemult_2[0] = null;
					  conmult_2[0] = null;
					  con.sendMessage("You are Player 2.");
					  msg = con.receiveMessage();
					  if (msg.equals("leave")) return false; 		//client exited complety
					  if (msg.equals("exit")) {	
						  if (gamemult_2[1] == null) {
							  gamemult_2[0] = user1_game;
							  conmult_2[0] = user1_con;
						  }
						  continue;
					  }
					  if (gamemult_2[1] != null) {
						  gamemult_2[1] = null;
						  con.sendMessage("Player 1 left");
						  continue;
					  }
					  while (user1_game.kill) {
						  Thread.sleep(1000);
					  }
					  int number = g.generateNumber();
					  new Multiplayer(user1_game, g, user1_con, con, number).main();
					  user1_game.kill = true;	//to kill user1 thread for it to exit the loop
				  }
			  }
		}
	}
	
	
	public int startGame(Game g, int number, ServerConnection con, boolean Mult) throws Exception {
		if (Mult) {
			g.user.setGold(g.user.getGold()-500);			
		}
		g.number = number;
		g.nbGuesses = 0;
		int entered;
		String guessed = "";
		String msg;
		while (true){
			while (true) {
				try {
					msg = con.receiveMessage();
					if (msg.equals("leave")) return -666; 		//client exited complety
					if (msg.equals("trade")) {
						try {
							g.user.Trade(1); //throws an exception if not possible
							con.sendMessage("ok");
						}catch (Exception e) {
							e.printStackTrace();
							con.sendMessage("ERROR: " + e.getMessage());
						}
						continue;
					}
					if (msg.equals("trade")) {
						try {
							g.user.Trade(1); //throws an exception if not possible
							con.sendMessage("ok");
						}catch (Exception e) {
							e.printStackTrace();
							con.sendMessage("ERROR: " + e.getMessage());
						}
						continue;
					}else if (msg.equals("exit")) {
						if (g.nbGuesses == 0 && !Mult) {
							return 0;
						}
						return -1;
					}
					entered = Integer.parseInt(msg);	
					if (g.hasDuplicates(entered) || entered < 1000 || entered > 9999) {
						con.sendMessage("ERROR: Number must be between  1000 and 9999 and not contain any duplicates");
						continue;
					}
					break;
				}
				catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
			}
			if (g.user.getGold() < 5) {
				con.sendMessage("ERROR: You don't have enough gold to continue");
				continue;
			}
			g.user.setGold(g.user.getGold()-5);
			g.nbGuesses++;
			guessed = g.checkGuess(entered);
			if (guessed.contains("Congrats!")) {
				return g.nbGuesses;
			}
			con.sendMessage(guessed);
		}
	}
	
	//Handles reward for winning games in a row
	public boolean winCombo(Game g, ServerConnection con) throws Exception {
			int combo = g.user.getWinCombo();
			if (combo % 5 == 0) {
				con.sendMessage("combo " + combo);
				g.user.setGold(g.user.getGold() + combo*50);
				return true;
			}
			con.sendMessage("ok");
			return false;
		}
	
	@Override
	public void run() {
		try {
			System.out.println("Connected: " + socket);
			if (!authenticateUser()) {
				close();
				return;
			}
			if (!mainMenu()) {
				close();
				return;
			}		
		} catch(EOFException e) {
			close();
		} catch (Exception e) {
			System.out.println("Error:" + socket);
			e.printStackTrace();
		} 
	}
}

