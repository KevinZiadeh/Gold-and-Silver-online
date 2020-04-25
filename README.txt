PROJECT TITLE: Gold-and-Silver game

PURPOSE OF PROJECT: 	Apply networking knowledge to create a multi threaded server 
			to be able to play the Gold-and-Silver game

DESCRIPTION: 	Game like mastermind. The player has to guess a secret number (4 distincts digits) with 
		as few guesses as possible. Each guess costs 5 gold coins. If the player guesses the correct 
		number in the wrong position, he gets 1 silver coin. If the player guesses the correct number 
		in the correct position, he gets 1 gold coin. 10 silver coins can be exchanged for 1 gold coin.
		The player gets 50 gold coins when he/she guesses the secret number. At the first login of each 
		day, every user gets 50 gold coins for free. For the two-player game, each player pays 500 gold 
		coins to enter the game, and the winner (guessed the number with fewer guests) takes 1000 gold 
		coins. The tournament competition is held with 8, 16 or 32 players. Also, each player pays 500 
		gold coins to enter the tournament, and the winner takes all.

HOW TO START THIS PROJECT:
		Create a MySQL database and change its parameters to connect to it in the Server class (connect function).
		Select the port you want to run the server, then start the server.
		Add mysql-connector-java-8.0.19.jar in the lib folder to the java Build Path.
		Run the server.
		Create a friendly user interface with GUI (Apache-NetBeans)
		
USER INSTRUCTIONS:
	YOU NEED TO HAVE JAVA INSTALLED ON YOUR MACHINE. (https://www.oracle.com/java/technologies/javase-jdk13-downloads.html)
	YOU NEED TO SET ENVIRONMENT VARIABLES. (https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html)
		
		Start the server:
				Either by putting the Server.java file in eclipse, setting up 
					Build Path for it to be able to connect to the database
			OR
				Using the terminal: browse to the directory target/classes/ then type
					java -cp ";../../lib/mysql-connector-java-8.0.19.jar" Server	
	
		Start the client:
			Using the terminal: browse to the directory target/ then type
					java -jar Client.jar 192.168.1.104 12354
		where the IP address (here 192.168.1.104) will be outputed in the console after running the server, 
		and 12345 being the port number
				
		Client may be slow at first, since your computer is not a proper server and the 
		database is online. If you don't see any errors on the server console and the client 
		console, just wait a bit. You can always press CTRL+C on the client console to 
		force exit.
				
