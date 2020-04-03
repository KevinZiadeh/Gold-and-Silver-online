PROJECT TITLE: Gold and Silver online

PURPOSE OF PROJECT: 	Apply networking knowledge to create a multi threaded server 
			to be able to play a game

VERSION: 1.0.0

DESCRIPTION: 	Game like mastermind. The player has to guess a secret number 
		(4 distincts digits). Each guess costs 5 gold coins. If the player 
		guesses the correct number in the wrong position, he gets 1 silver.
		If the player guesses the correct number in the correct position, he 
		gets 1 gold. 10 silver can be exchanged for 1 gold.
		
AUTHORS:	ARDO Nour
		BAZAZO dANA
		KHALED Paul Karim
		ZIADEH Kevin

HOW TO STRART THIS PROJECT:
		Create a MySQL database and change the parameters to connect to it in
			in the Server class, connect function.
		Select the port you want to run the server, then start the server.
		Add mysql-connector-java-8.0.19.jar in the lib folder to the java Build Path
		Run the server. (using eclipse)
		
USER INSTRUCTIONS:
		Run the Client from the terminal by typing:
			java Client server_ip_address port_number