
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
				e.printStackTrace();
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
			user2_con.sendMessage("Waiting for other player to finish");
		}
		else if (user2_guesses == 0 && user1_guesses > 0) {
			user1_con.sendMessage("Waiting for other player to finish");
		}
		while (user1_guesses == 0 || user2_guesses == 0) {
			Thread.sleep(1000);
		}
		if (user1_guesses < 0 && user2_guesses >= 0) {
			user2_con.sendMessage("Player 1 left");
		}
		else if (user2_guesses < 0 && user1_guesses >= 0) {
			user1_con.sendMessage("Player 2 left");
		}
                else if (user2_guesses < 0 && user1_guesses < 0){ //both left
                        return;
                }
		//handles winning logic
		if (user1_guesses == user2_guesses) {
			user1_con.sendMessage("It was a tie. You won 500 gold!");
			user1_game.user.setGold(user1_game.user.getGold()+500);
			user2_con.sendMessage("It was a tie. You won 500 gold!");
			user2_game.user.setGold(user2_game.user.getGold()+500);
		}
		else if (user1_guesses > 0 && (user1_guesses < user2_guesses || user2_guesses < 0)) {
			user1_con.sendMessage("SUCCESS! You won 1000 gold!");
			user1_game.user.setGold(user1_game.user.getGold()+1000);
			user1_game.user.setWin();
			user1_game.user.setWinCombo(1);
			winCombo(user1_game, user1_con);
			if (user2_guesses < 0) return;
			user2_con.sendMessage("FAILED: You lost");
			user2_game.user.setLoss();
			user2_game.user.setWinCombo(0);
		}
		else {
			user2_con.sendMessage("SUCCESS! You won 1000 gold!");
			user2_game.user.setGold(user2_game.user.getGold()+1000);
			user2_game.user.setWin();
			user2_game.user.setWinCombo(1);
			winCombo(user2_game, user2_con);
			if (user1_guesses < 0) return;
			user1_con.sendMessage("FAILED: You lost");
			user1_game.user.setLoss();
			user1_game.user.setWinCombo(0);
		}
	}
        
        
        public int getWinner() throws Exception {
            user1_con.sendMessage("Winner is the one who gets the answer in the fewest guesses possible");
            user2_con.sendMessage("Winner is the one who gets the answer in the fewest guesses possible");
            //Run user1 game in their own thread
            Thread user1_thread = new Thread(() -> {
                    try {
                            user1_guesses = startGame(user1_game, number, user1_con, true);
                    } catch (Exception e) {
                            user1_guesses = -1;
                            e.printStackTrace();
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
                    user2_con.sendMessage("Waiting for other player to finish");
            }
            else if (user2_guesses == 0 && user1_guesses > 0) {
                    user1_con.sendMessage("Waiting for other player to finish");
            }
            while (user1_guesses == 0 || user2_guesses == 0) {
                    Thread.sleep(1000);
            }
            if (user1_guesses < 0) {
                    user2_con.sendMessage("Player 1 left");
            }
            else if (user2_guesses < 0) {
                    user1_con.sendMessage("Player 2 left");
            }
            //handles winning logic
            if (user1_guesses == user2_guesses) {
                    if (Math.random()>0.5){
                        return 1;
                    }else{
                        return 2;
                    }
            }
            else if (user1_guesses > 0 && (user1_guesses < user2_guesses || user2_guesses < 0)) {
                    return 1;
            }
            else {
                    return 2;
            }
    }
        
        
        
}