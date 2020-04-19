/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kevin
 */
public class Tournament extends Capitalizer{
    public static boolean isfull;
    public static Game[] gameList;    
    public static ServerConnection[] conList;
    public int size;
    public int numPlayers;
    public int winner1;
    public int winner2;
    public int winner3;
    public int winner4;
    public int winner5;
    public int winner6;
    public int winner7;
    
//    Tournament (){}
    
    Tournament(int size){
        this.size = size;
        numPlayers = 0;
        isfull = false;
        gameList = new Game[size];
        conList = new ServerConnection[size];
    }
    
    public void addPlayer(Game g, ServerConnection con){
        gameList[numPlayers] = g;
        conList[numPlayers] = con;
        numPlayers++;
        if (numPlayers == size){
            isfull = true;
            numPlayers = 0;
        }
    }
    
    public void removePlayer(Game g, ServerConnection con){
        boolean found = false;
        for(int i =0;i<size;i++){
            if (gameList[i]==g){
                found = true;
                numPlayers--;
            }
            if (i==size){
                return;
            }
            if (found){
                gameList[i]=gameList[i+1];
                conList[i]=conList[i+1];
            }
        }
    }
    
    public boolean awaitingReady(){
        for(int i =0;i<size;i++){
                try{
                    if (conList[i].receiveMessage().equals("exit")){
                        return false;
                    }
                }catch (Exception e){}
        }
        return true;
    }
    
    public void sendAll(String msg){
        for(int i =0;i<size;i++){
                try{
                    conList[i].sendMessage(msg);
                }catch (Exception e){}
        }
    }
        
    // use fin to detect if 8 player tournament or not => false means 8 player tournament
    public int startTournament(boolean fin, Game[] gameList, ServerConnection[] conList) throws Exception{
        //first round
        int number;
        number = gameList[0].generateNumber();
        Multiplayer mult1 = new Multiplayer(gameList[0], gameList[1], conList[0], conList[1], number);
        number = gameList[2].generateNumber();
        Multiplayer mult2 = new Multiplayer(gameList[2], gameList[3], conList[2], conList[3], number);
        number = gameList[4].generateNumber();
        Multiplayer mult3 = new Multiplayer(gameList[4], gameList[5], conList[4], conList[5], number);
        number = gameList[6].generateNumber();
        Multiplayer mult4 = new Multiplayer(gameList[6], gameList[7], conList[6], conList[7], number);
        Thread winner1Thread = new Thread(() -> {
            try {
                winner1 = -1+mult1.getWinner();
                int loser = (winner1 == 0) ? 1 : 0;
                sendResults(winner1, loser);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        Thread winner2Thread = new Thread(() -> {
            try {
                winner2 = 1+mult2.getWinner();
                int loser = (winner2 == 2) ? 3 : 2;
                sendResults(winner2, loser);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        Thread winner3Thread = new Thread(() -> {
            try {
                winner3 = 3+mult3.getWinner();
                int loser = (winner3 == 4) ? 5 : 4;
                sendResults(winner3, loser);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        Thread winner4Thread = new Thread(() -> {
            try {
                winner4 = 5+mult4.getWinner();
                int loser = (winner4 == 6) ? 7 : 6;
                sendResults(winner4, loser);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        winner1Thread.start();
        winner2Thread.start();
        winner3Thread.start();
        winner4Thread.start();
        while (winner1 == 0 || winner2 == 0 || winner3 == 0 || winner4 == 0){
            Thread.sleep(1000);
        }
        conList[winner1].sendMessage("full");
        conList[winner2].sendMessage("full");
        conList[winner3].sendMessage("full");
        conList[winner4].sendMessage("full");
        if (!conList[winner1].receiveMessage().equals("ready")){
            conList[winner2].sendMessage("ERROR: Tournament Cancelled");
            conList[winner3].sendMessage("ERROR: Tournament Cancelled");
            conList[winner4].sendMessage("ERROR: Tournament Cancelled");
            gameList[winner1].kill = true;
            gameList[winner2].kill = true;
            gameList[winner3].kill = true;
            gameList[winner4].kill = true;
            return -1;
        }
        if (!conList[winner4].receiveMessage().equals("ready")){
            conList[winner2].sendMessage("ERROR: Tournament Cancelled");
            conList[winner3].sendMessage("ERROR: Tournament Cancelled");
            conList[winner1].sendMessage("ERROR: Tournament Cancelled"); 
            gameList[winner1].kill = true;
            gameList[winner2].kill = true;
            gameList[winner3].kill = true;
            gameList[winner4].kill = true;
            return -1;
        }
        if (!conList[winner2].receiveMessage().equals("ready")){
            conList[winner1].sendMessage("ERROR: Tournament Cancelled");
            conList[winner3].sendMessage("ERROR: Tournament Cancelled");
            conList[winner4].sendMessage("ERROR: Tournament Cancelled");  
            gameList[winner1].kill = true;
            gameList[winner2].kill = true;
            gameList[winner3].kill = true;
            gameList[winner4].kill = true;
            return -1;
        }
        if (!conList[winner3].receiveMessage().equals("ready")){
            conList[winner2].sendMessage("ERROR: Tournament Cancelled");
            conList[winner1].sendMessage("ERROR: Tournament Cancelled");
            conList[winner4].sendMessage("ERROR: Tournament Cancelled");  
            gameList[winner1].kill = true;
            gameList[winner2].kill = true;
            gameList[winner3].kill = true;
            gameList[winner4].kill = true;
            return -1;
        }
        //Second round
        number = gameList[winner1].generateNumber();
        Multiplayer mult5 = new Multiplayer(gameList[winner1], gameList[winner2], conList[winner1], conList[winner2], number);
        number = gameList[winner3].generateNumber();
        Multiplayer mult6 = new Multiplayer(gameList[winner3], gameList[winner4], conList[winner3], conList[winner4], number);
        Thread winner5Thread = new Thread(() -> {
            try {
                winner5 = (mult5.getWinner() == 1) ? winner1 : winner2;
                int loser = (winner5 == winner1) ? winner2 : winner1;
                sendResults(winner5, loser);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        Thread winner6Thread = new Thread(() -> {
            try {
                winner6 = (mult6.getWinner() == 1) ? winner3 : winner4;
                int loser = (winner6 == winner3) ? winner4 : winner3;
                sendResults(winner6, loser);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        winner5Thread.start();
        winner6Thread.start();
        while (winner5 == 0  || winner6 == 0){
            Thread.sleep(1000);
        }
        conList[winner5].sendMessage("full");
        conList[winner6].sendMessage("full");
        if (!conList[winner5].receiveMessage().equals("ready")){
            conList[winner6].sendMessage("ERROR: Tournament Cancelled");
            gameList[winner5].kill = true;
            gameList[winner6].kill = true;
            return -1;
        }
        if (!conList[winner6].receiveMessage().equals("ready")){
            conList[winner5].sendMessage("ERROR: Tournament Cancelled");
            gameList[winner5].kill = true;
            gameList[winner6].kill = true;
            return -1;
        }
        //final round
        number = gameList[winner5].generateNumber();
        Multiplayer mult7 = new Multiplayer(gameList[winner5], gameList[winner6], conList[winner6], conList[winner6], number);
        Thread winner7Thread = new Thread(() -> {
                try {
                    winner7 = (mult7.getWinner() == 1) ? winner5 : winner6;
                    int loser = (winner7 == winner5) ? winner6 : winner5;
                    if (!fin){
                        conList[winner7].sendMessage("CONGRATS! You won "+ 500*size +" gold! |"+500*size);
                        gameList[winner7].user.setGold(gameList[winner7].user.getGold()+500*size);
                        gameList[winner7].user.setWin();
                        gameList[winner7].user.setWinCombo(1);
                        winCombo(gameList[winner7], conList[winner7]);
                        conList[loser].sendMessage("FAILED: You lost");
                        gameList[loser].user.setLoss();
                        gameList[loser].user.setWinCombo(0);
                        gameList[loser].kill = true;
                        gameList[winner7].kill = true;
                    }else{
                        sendResults(winner7, loser);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        winner7Thread.start();
        if (fin){
            return winner7;
        }
        return -1;
    }
    
    //use fin to detect if this is a 8 player tournament or more to return accordingly
    public void sendResults(int winner, int loser) throws Exception{
        conList[winner].sendMessage("SUCCESS! You proceed to the next stage");
        conList[loser].sendMessage("FAILED: You lost");
        gameList[loser].user.setLoss();
        gameList[loser].user.setWinCombo(0);
        gameList[loser].kill = true;
    }
    
    public void run(){
        sendAll("full");
        if (!awaitingReady()){
            sendAll("ERROR: A player left.");
            for(int i =0;i<size;i++){
                gameList[i].kill = true;
            }
            return;
        }
/*
        Possible to divide each round in a function and make the code easier to read
*/
        try{
            if (size == 8){
                startTournament(false, gameList, conList);
            }else if (size == 16){
                Game[] glist1 = new Game[8];
                ServerConnection[] clist1 = new ServerConnection[8];
                for (int i = 0; i < 8; i++){
                    glist1[i]=gameList[i];
                    clist1[i]=conList[i];
                } 
                Game[] glist2 = new Game[8];
                ServerConnection[] clist2 = new ServerConnection[8];
                for (int i = 8; i < 16; i++){
                    glist2[i]=gameList[i];
                    clist2[i]=conList[i];
                } 
                Thread winner1Thread = new Thread(() -> {
                    try{
                        winner1 = startTournament(true, glist1, clist1);
                    }catch (Exception e){e.printStackTrace();}
                });
                Thread winner2Thread = new Thread(() -> {
                    try{
                        winner2 = 8+startTournament(true, glist2, clist2);
                    }catch (Exception e){e.printStackTrace();}
                });
                winner1Thread.start();
                winner2Thread.start();
                while (winner1 == 0 || winner2 == 0){
                    Thread.sleep(1000);
                }
                conList[winner1].sendMessage("full");
                conList[winner2].sendMessage("full");
                if (!conList[winner1].receiveMessage().equals("ready")){
                    conList[winner2].sendMessage("ERROR: Tournament Cancelled");
                    gameList[winner1].kill = true;
                    gameList[winner2].kill = true;
                    return;
                }
                if (!conList[winner2].receiveMessage().equals("ready")){
                    conList[winner1].sendMessage("ERROR: Tournament Cancelled");
                    gameList[winner2].kill = true;
                    gameList[winner1].kill = true;
                    return;
                }
                int number = gameList[winner1].generateNumber();
                int winner3 = new Multiplayer(gameList[winner1], gameList[winner2], conList[winner1], conList[winner2], number).getWinner();
                int loser = (winner3 == winner1) ? winner2 : winner1;
                conList[winner3].sendMessage("CONGRATS! You won "+ 500*size +" gold! |"+500*size);
                gameList[winner3].user.setGold(gameList[winner3].user.getGold()+500*size);
                gameList[winner3].user.setWin();
                gameList[winner3].user.setWinCombo(1);
                winCombo(gameList[winner3], conList[winner3]);
                conList[loser].sendMessage("FAILED: You lost");
                gameList[loser].user.setLoss();
                gameList[loser].user.setWinCombo(0);
                gameList[loser].kill = true;
                gameList[winner3].kill = true;
            }else if (size == 32){
                //implements manually stuff from startTournamaent
                Game[] glist1 = new Game[8];
                ServerConnection[] clist1 = new ServerConnection[8];
                for (int i = 0; i < 8; i++){
                    glist1[i]=gameList[i];
                    clist1[i]=conList[i];
                } 
                Game[] glist2 = new Game[8];
                ServerConnection[] clist2 = new ServerConnection[8];
                for (int i = 8; i < 16; i++){
                    glist2[i]=gameList[i];
                    clist2[i]=conList[i];
                } 
                Game[] glist3 = new Game[8];
                ServerConnection[] clist3 = new ServerConnection[8];
                for (int i = 16; i < 24; i++){
                    glist3[i]=gameList[i];
                    clist3[i]=conList[i];
                } 
                Game[] glist4 = new Game[8];
                ServerConnection[] clist4 = new ServerConnection[8];
                for (int i = 24; i < 32; i++){
                    glist4[i]=gameList[i];
                    clist4[i]=conList[i];
                } 
                Thread winner1Thread = new Thread(() -> {
                    try{
                        winner1 = startTournament(true, glist1, clist1);
                    }catch (Exception e){e.printStackTrace();}
                });
                Thread winner2Thread = new Thread(() -> {
                    try{
                        winner2 = 8+startTournament(true, glist2, clist2);
                    }catch (Exception e){e.printStackTrace();}
                });
                Thread winner3Thread = new Thread(() -> {
                    try{
                        winner3 = startTournament(true, glist3, clist3);
                    }catch (Exception e){e.printStackTrace();}
                });
                Thread winner4Thread = new Thread(() -> {
                    try{
                        winner4 = 8+startTournament(true, glist4, clist4);
                    }catch (Exception e){e.printStackTrace();}
                });
                winner1Thread.start();
                winner2Thread.start();
                winner3Thread.start();
                winner4Thread.start();
                while (winner1 == 0 || winner2 == 0 || winner3 == 0 || winner4 == 0){
                    Thread.sleep(1000);
                }
                conList[winner1].sendMessage("full");
                conList[winner2].sendMessage("full");
                conList[winner3].sendMessage("full");
                conList[winner4].sendMessage("full");
                if (!conList[winner1].receiveMessage().equals("ready")){
                    conList[winner2].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner3].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner4].sendMessage("ERROR: Tournament Cancelled");
                    gameList[winner1].kill = true;
                    gameList[winner2].kill = true;
                    gameList[winner3].kill = true;
                    gameList[winner4].kill = true;
                    return;
                }
                if (!conList[winner4].receiveMessage().equals("ready")){
                    conList[winner2].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner3].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner1].sendMessage("ERROR: Tournament Cancelled"); 
                    gameList[winner1].kill = true;
                    gameList[winner2].kill = true;
                    gameList[winner3].kill = true;
                    gameList[winner4].kill = true;
                    return;
                }
                if (!conList[winner2].receiveMessage().equals("ready")){
                    conList[winner1].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner3].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner4].sendMessage("ERROR: Tournament Cancelled");  
                    gameList[winner1].kill = true;
                    gameList[winner2].kill = true;
                    gameList[winner3].kill = true;
                    gameList[winner4].kill = true;
                    return;
                }
                if (!conList[winner3].receiveMessage().equals("ready")){
                    conList[winner2].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner1].sendMessage("ERROR: Tournament Cancelled");
                    conList[winner4].sendMessage("ERROR: Tournament Cancelled");  
                    gameList[winner1].kill = true;
                    gameList[winner2].kill = true;
                    gameList[winner3].kill = true;
                    gameList[winner4].kill = true;
                    return;
                }
                //Second round
                int number;
                number = gameList[winner1].generateNumber();
                Multiplayer mult5 = new Multiplayer(gameList[winner1], gameList[winner2], conList[winner1], conList[winner2], number);
                number = gameList[winner3].generateNumber();
                Multiplayer mult6 = new Multiplayer(gameList[winner3], gameList[winner4], conList[winner3], conList[winner4], number);
                Thread winner5Thread = new Thread(() -> {
                    try {
                        winner5 = (mult5.getWinner() == 1) ? winner1 : winner2;
                        int loser = (winner5 == winner1) ? winner2 : winner1;
                        sendResults(winner5, loser);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
                Thread winner6Thread = new Thread(() -> {
                    try {
                        winner6 = (mult6.getWinner() == 1) ? winner3 : winner4;
                        int loser = (winner6 == winner3) ? winner4 : winner3;
                        sendResults(winner6, loser);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
                winner5Thread.start();
                winner6Thread.start();
                while (winner5 == 0  || winner6 == 0){
                    Thread.sleep(1000);
                }
                conList[winner5].sendMessage("full");
                conList[winner6].sendMessage("full");
                if (!conList[winner5].receiveMessage().equals("ready")){
                    conList[winner6].sendMessage("ERROR: Tournament Cancelled");
                    gameList[winner5].kill = true;
                    gameList[winner6].kill = true;
                    return;
                }
                if (!conList[winner6].receiveMessage().equals("ready")){
                    conList[winner5].sendMessage("ERROR: Tournament Cancelled");
                    gameList[winner5].kill = true;
                    gameList[winner6].kill = true;
                    return;
                }
                //final round
                number = gameList[winner5].generateNumber();
                int winner7 = new Multiplayer(gameList[winner5], gameList[winner6], conList[winner6], conList[winner6], number).getWinner();
                int loser = (winner7 == winner5) ? winner6 : winner5;
                conList[winner7].sendMessage("CONGRATS! You won "+ 500*size +" gold! |"+500*size);
                gameList[winner7].user.setGold(gameList[winner3].user.getGold()+500*size);
                gameList[winner7].user.setWin();
                gameList[winner7].user.setWinCombo(1);
                winCombo(gameList[winner7], conList[winner7]);
                conList[loser].sendMessage("FAILED: You lost");
                gameList[loser].user.setLoss();
                gameList[loser].user.setWinCombo(0);
                gameList[loser].kill = true;
                gameList[winner7].kill = true;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Over");
    }
    
//    public Game getWinnerGame(){
//    
//    }
//    
}
