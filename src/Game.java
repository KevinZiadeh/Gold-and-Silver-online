import java.util.Random;
//import java.util.Scanner;
//import javax.swing.JOptionPane;

public class Game extends Thread{
	public Random RANDOM = new Random();
	public int number;
	public int nbGuesses;
	public boolean guessed;
	public boolean kill;
	public User user;
	
	//Initial Constructor
	Game(){
		
	}
	
	//Set initial values
	Game(User user){
		this.guessed = false;
		this.user = user;
		this.kill = false;
	}

	// generate the number to guess
	public int generateNumber() {
		do {
			number = RANDOM.nextInt(9000) + 1000;
		} while (hasDuplicates(number));
	    	System.out.println("Number is " + number + "\n");
	    	return number;
	}
	  
	//check if numbers are unique
	public boolean hasDuplicates(int number) {
		boolean[] digits = new boolean[10];
		while (number > 0) {
			int last = number % 10;
			if (digits[last]) return true;
			digits[last] = true;
			number = number / 10;
		}
	    return false;
	}
	
	// return the number of gold and silver for the entered guess comparing to the number.
	//Return the string to send to client
	public String checkGuess(int entered) {
		int silver = 0, gold = 0;
		String enteredStr = String.valueOf(entered);
		String numberStr = String.valueOf(number);

		for (int i = 0; i < numberStr.length(); i++) {
			char c = enteredStr.charAt(i);

			if (c == numberStr.charAt(i)) {
				gold++;
			} else if (numberStr.contains(String.valueOf(c))) {
				silver++;
			}
		}
		if (gold == 4) {
//			user.setGold(user.getGold()+50);
			return ("Congrats! You won with "+nbGuesses+" guesses \n");
		}else {
			user.setGold(user.getGold()+gold);
			user.setSilver(user.getSilver()+silver);  
			return ("You got "+ gold + " gold coins and "+ silver + " silver coins. \t GOLD Remaining: "+user.getGold()+". SILVER Remaining: "+user.getSilver());
		}
	}
	  
	//used for testing
	public void run(){
		while (!kill) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
	


