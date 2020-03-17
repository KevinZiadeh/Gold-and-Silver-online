//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
import java.net.Socket;
//import java.util.Scanner;
//import java.net.*;  
//import java.io.*;  
import java.net.SocketException;

public class Client{
	public static ServerConnection con;
	private static User user;
	
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
			System.out.println(con.receiveMessage());
			user = (User) con.receiveUser();
			System.out.println(user.getString());
			} 
		catch (Exception e) {
			System.out.println("Goodbye");
//			System.out.println(e);
		}
	}
	
	private static void startGame(){
		String temps;
		try {
			do {
				temps = con.receiveMessage();
				System.out.println(temps);
				if (temps.contains("Congrats!") || temps.contains("Sorry")) {
					break;
				}
				con.sendMessage(con.readInput());
			} while (!temps.contains("Congrats!") && !temps.contains("Sorry"));
			System.out.println(con.receiveMessage());
			temps = con.readInput();
			con.sendMessage(temps);
			if (temps.equals("0")) {
				startGame();
			}else {
				return;
			}
		} 
		catch (Exception e) {
//			System.out.println(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length <= 1) {
			System.err.println("Pass the server IP and Port as command line argument");
			return;
		}
		try (var socket = new Socket(args[0], Integer.parseInt(args[1]))) {
			con = new ServerConnection(socket);
			System.out.println("Enter lines of text then Ctrl+D or Ctrl+C to quit");
			createUser();
			con.sendUser(user);
			startGame();
			System.out.println("Goodbye");
			con.s.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fuck");
		}
	}
}
