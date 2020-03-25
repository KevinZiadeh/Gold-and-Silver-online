import java.net.Socket;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.util.Scanner;
//import java.net.*;  
//import java.io.*;  
//import java.net.SocketException;

public class Client{
	private static ServerConnection con;
	private static User user;
	
	//listens and sends input to server for userSelection code
	private static void createUser(){
		String temps;
		try {
			do {
				temps = con.receiveMessage();
				System.out.println(temps);
				if (temps.equals("Successful")) {
					break;
				}
				con.sendMessage(con.readInput());
			} while (!temps.equals("Successful"));
//			System.out.println(con.receiveMessage());
			user = (User) con.receiveUser();
			System.out.println(user.getString());
			} 
		catch (Exception e) {
			System.out.println("Goodbye");
//			System.out.println(e);
		}
	}
	
	//sends responses and awaits requests from server regardeless what gamemode
	public static boolean play() throws Exception {
		String temps;
		try {
			do {
				temps = con.receiveMessage();
				if (temps.contains("Congrats!") || temps.contains("Sorry") || temps.contains("tie") || temps.contains("Waiting")) {
					break;
				}
				System.out.println(temps);
				String msg = con.readInput();
				con.sendMessage(msg);
			} while (!temps.contains("Congrats!") && !temps.contains("Sorry") && !temps.contains("tie") && !temps.contains("Waiting"));
			if (temps.contains("Waiting")) {
				temps = con.receiveMessage();
			}
			System.out.print(temps);
			if (temps.contains("Congrats!")) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
//				System.out.println(e);
				throw new Exception(e);
			}
	}
	
	//listens and sends input to server for modeSelection and startGame code
	private static void startGame(int comboCounter) throws Exception{
		System.out.println(con.receiveMessage());
		String temps;
		String receiver;
		while (true) {
			try {	
				temps = con.readInput();
				if ((!temps.equals("0")) && (!temps.equals("1")) && (!temps.equals("2"))){
					throw new Exception("Invalid entry, please enter 0 or 1 \nSelect game mode: 0 for singleplayer, 1 for multiplayer");
				}else if(temps.equals("0")) {
					con.sendMessage(temps);
					if (play()) {
						comboCounter++;
						if (comboCounter % 5 == 0) {
							System.out.println("You have been rewarded additional gold coins for winning a number of matches in a row!");
						}
					}
				}else if(temps.equals("1")){
					con.sendMessage(temps);
					receiver = con.receiveMessage();
					if (receiver.contains("online")) {
						throw new Exception(receiver);
					}
					System.out.println(receiver);
					System.out.println(con.receiveMessage());
					if (play()) {
						comboCounter++;
						if (comboCounter % 5 == 0) {
							System.out.println("You have been rewarded additional gold coins for winning a number of matches in a row!");
						}
					}
				}
					break;
			}	
			catch (Exception e) {
//				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		System.out.println(con.receiveMessage());
		temps = con.readInput();
		con.sendMessage(temps);
		if (temps.equals("0")) {
			startGame(comboCounter);
		}else {
			return;
		}
	} 

	
	public static void main(String[] args) throws Exception {
		//check for conditions to connect 
		if (args.length <= 1) {
			System.err.println("Pass the server IP and Port as command line argument");
			return;
		}
		
		try (var socket = new Socket(args[0], Integer.parseInt(args[1]))) {
			con = new ServerConnection(socket);
			System.out.println("Enter lines of text then Ctrl+C to quit");
			createUser();
			con.sendUser(user);
			startGame(user.getWinCombo());
			System.out.println("Goodbye");
			con.s.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fuck");
		}
	}
}
