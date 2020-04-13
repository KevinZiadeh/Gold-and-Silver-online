import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException; 
import java.net.Socket;
import java.net.SocketException;

public class ServerConnection {
	Socket s;
	DataInputStream din;  
	DataOutputStream dout;
	BufferedReader br;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	
	//constructor to set up variables
	ServerConnection(Socket s){
		this.s = s;
		try {
			this.din=new DataInputStream(s.getInputStream());
			this.dout=new DataOutputStream(s.getOutputStream());
			this.oout=new ObjectOutputStream(s.getOutputStream());
			this.oin=new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}  
		this.br=new BufferedReader(new InputStreamReader(System.in)); 
	}
	
	//to send a message as string
	public void sendMessage(String string) throws Exception {
		try {
			dout.writeUTF(string);
			dout.flush();  
		} catch (SocketException e) {
			e.printStackTrace();
//			throw new Exception("User Disconnected", new Throwable("User"));
		}catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	//wait until you receive a message. Return the message or throw exception
	public String receiveMessage() throws Exception {
		try {
			return din.readUTF();   
		} catch (SocketException e) {
			throw new Exception("User Disconnected", new Throwable("User"));
		} catch (EOFException e) {
			e.printStackTrace();
//			throw new Exception("User Disconnected", new Throwable("User"));
		}
		catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return null;  
	}
	
	//Read input from terminal. Return read input or throw exception
	public String readInput() throws Exception {
		try {
			return br.readLine();
		} catch (IOException e) {
			throw new Exception("Error in reading input");
//			e.printStackTrace();
		}
	}
	
	//to receive a user. Return user
	public Object receiveUser() throws Exception {
		try {
			return oin.readObject(); 
		}catch (SocketException e) {
			throw new Exception("User Disconnected", new Throwable("User"));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return "";
		}  
	}
	
	//to send a user
	public void sendUser(User user) throws Exception {
		try {
			oout.writeObject(user);
			oout.flush();  
		} catch (SocketException e) {
			throw new Exception("User Disconnected", new Throwable("User"));
		}catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
}
