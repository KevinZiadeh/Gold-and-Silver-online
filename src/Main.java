import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Main {
	private static Statement db;
	private static User user;
	
	//use it to connect to db
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
	
	//use it to execute select db commands
	public ResultSet executeQuery(String exc) {
		ResultSet rs = null;
		try {
			rs=db.executeQuery(exc);  
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return rs;
	}
	
	//use it to execute other db commands
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
	public void userSelection() {
		int selectUser;
		Scanner in = new Scanner(System.in);   
	    System.out.println("Enter 0 for login, 1 for sign up");  
	    while (true) {
		    try {
		    	selectUser = Integer.parseInt(in.nextLine());
		    	if ((selectUser != 0) && (selectUser != 1)){
		    		System.out.println("Invalid entry, please enter 0 or 1");
		    		continue;
		    	}else if(selectUser == 0) {
		    		//get info for user
		    		String username;
		    		String password;
		    		while (true) {
			    		System.out.println("LOGIN: Enter username:");
			    		username = in.nextLine();		
			    		ResultSet r = this.executeQuery("SELECT * FROM Users WHERE username LIKE '" +username+"';");
			    		r.next();
			    		if (!r.first()) {
			    			System.out.println("Username does not exist");
			    			continue;
			    		}else {
			    			System.out.println("Enter password:");
			    			password = in.nextLine();
			    			if (password.equals(r.getString("password"))) {
					    		user = new User(r);
					    		break;
			    			}else {
				    			System.out.println("Wrong password");
				    			continue;
			    			}
			    		}
		    		}
		    	}else if(selectUser == 1) {
		    		//loop to check for uniqueness of username
		    		String usern;
		    		while (true) {
			    		System.out.println("Enter username:");
			    		usern = in.nextLine();		
			    		ResultSet r = this.executeQuery("SELECT * FROM Users WHERE username LIKE '" +usern+"';");
			    		r.next();
			    		if (r.first()) {
			    			System.out.println("Username already exists");
			    			continue;
			    		}
			    		break;	
		    		}
		    		System.out.println("Enter password:");
		    		String pass = in.nextLine();
		    		System.out.println("Enter nickname:");
		    		String nick = in.nextLine();
		    		user = new User(usern, pass, nick);
		    		break;
		    	}
		    break;
		    }
		    catch (Exception e) {
		    	System.out.println("Invalid entry, please enter 0 or 1");
		    }
	    }   
	}
	
	
	public static void main(String args[]){  
//		Main s = new Main();
//		connect();
//		s.userSelection();
//    	Game g = new Game(user);
//    	g.start();
	}
}
