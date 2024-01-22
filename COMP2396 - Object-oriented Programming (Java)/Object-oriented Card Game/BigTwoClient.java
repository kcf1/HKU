import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
/**
 * The BigTwoClient class implements the NetworkGame interface. It is used to model a Big
Two game client that is responsible for establishing a connection and communicating with
the Big Two game server. 
 */
public class BigTwoClient implements NetworkGame {
	// Instance vars
	/** a BigTwo object for the Big Two card game.*/
	private BigTwo game;
	/** a BigTwoGUI object for the Big Two card game.*/
	private BigTwoGUI gui;
	/** a socket connection to the game server*/
	private Socket sock;
	/** an ObjectOutputStream for sending messages to the server.*/
	private ObjectOutputStream oos;
	/** an integer specifying the playerID (i.e., index) of the local player*/
	private int playerID;
	/** a string specifying the name of the local player.*/
	private String playerName;
	/** a string specifying the IP address of the game server.*/
	private String serverIP;
	/** an integer specifying the TCP port of the game server.*/
	private int serverPort;
	/** a boolean indicating the game state.*/
	private Boolean gameStarted;
	// Constructor
	/**
	 * a constructor for creating a Big Two client.
	 * @param game a reference to a BigTwo object associated with this client
	 * @param gui a reference to a BigTwoGUI object associated the BigTwo object
	 */
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {
		this.game = game;
		this.gui = gui;
		promptPlayerName();
		connect();
	}
	// Inner class
	/**
	 * an inner class that implements the Runnable interface to handle server socket input.
	 */
	public class ServerHandler implements Runnable {
		/** ObjectInputStream of the server socket*/
		private ObjectInputStream ois;
		// constructor
		/**
		 * Creates and returns an instance of the ServerHandler class.
		 * @param serverSocket the socket connection to the server
		 */
		public ServerHandler(Socket serverSocket) {
			try {
				// creates an ObjectInputStream of the server socket
				ois = new ObjectInputStream(sock.getInputStream());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		/**
		 * implementation of method from the Runnable interface
		 */
		@Override
		public void run() {
			CardGameMessage message;
			try {
				// waits for messages from the client
				while ((message = (CardGameMessage) ois.readObject()) != null) {
					parseMessage(message);
					gui.repaint();
				} // close while
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	// Methods
	/**
	 * a method to prompt player to input his/her name.
	 */
	public void promptPlayerName() {
		String name = JOptionPane.showInputDialog("Please enter name");
		// random 4 digit player name is assigned for empty input
		if (name.length()==0) name = String.format("#%04d",(int) (Math.random() * 1000));
		setPlayerName(name);
	}
	/**
	 * a method to prompt player to input server IP.
	 */
	public void promptServerIP() {
		String IP = JOptionPane.showInputDialog("Please enter server IP","127.0.0.1");
		setServerIP(IP);
	}
	/**
	 * a method to prompt player to input server TCP port.
	 */
	public void promptServerPort() {
		int port = Integer.parseInt(JOptionPane.showInputDialog("Please enter server port","2396"));
		setServerPort(port);
	}
	/**
	 * a method for getting the playerID (i.e., index) of the local player.
	 */
	@Override
	public int getPlayerID() {
		return playerID;
	}
	/**
	 * a method for setting the playerID (i.e., index) of the local player. 
	 */
	@Override
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
		gui.setLocalPlayer(playerID);
	}
	/**
	 * a method for getting the name of the local player.
	 */
	@Override
	public String getPlayerName() {
		return playerName;
	}
	/**
	 * a method for setting the name of the local player.
	 */
	@Override
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	/**
	 * a method for getting the IP address of the game server.
	 */
	@Override
	public String getServerIP() {
		return serverIP;
	}
	/**
	 * a method for setting the IP address of the game server.
	 */
	@Override
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	/**
	 * a method for getting the TCP port of the game server.
	 */
	@Override
	public int getServerPort() {
		return serverPort;
	}
	/**
	 * a method for setting the TCP port of the game server.
	 */
	@Override
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	/**
	 * a method for making a socket connection with the game server. 
	 * Upon successful connection, 
	 * (i) create an ObjectOutputStream for sending messages to the game server; 
	 * (ii) create a new thread for receiving messages from the game server.
	 */
	@Override
	public void connect() {
		promptServerIP();
		promptServerPort();
		try {
			sock = new Socket(serverIP,serverPort);
			//(i) create an ObjectOutputStream for sending messages to the game server; 
			oos = new ObjectOutputStream(sock.getOutputStream());
			//(ii) create a new thread for receiving messages from the game server.
			Thread t = new Thread(new ServerHandler(sock));
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * a method for parsing the messages received from the game server.
	 */
	@Override
	public void parseMessage(GameMessage message) {
		int playerID = message.getPlayerID();
		Object data = message.getData();
		// parses the message based on its type
		switch (message.getType()) {
		case CardGameMessage.PLAYER_LIST:
			// set playerID as assigned by server
			setPlayerID(playerID);
			// set playerList received from server
			setPlayerList((String[]) data);
			// send JOIN to server
			sendJoin();
			break;
		case CardGameMessage.JOIN:
			// add player to the player list
			addPlayer(playerID, (String) data);
			// if local player successfully joined,
			// send READY to server
			if (playerID == getPlayerID()) 
				sendReady();
			break;
		case CardGameMessage.READY:
			// print ready on GUI
			printReady(playerID);
			break;
		case CardGameMessage.FULL:
			// print full on GUI
			printFull();
			break;
		case CardGameMessage.QUIT:
			// remove player from playerList 
			removePlayer(playerID);
			// if game already started,
			// reset the game
			// send READY to server
			if (gameStarted) {
				game.reset();
				sendReady();
			}
			break;
		case CardGameMessage.START:
			// start game with the shuffled deck from server
			startGame((Deck) data);
			break;
		case CardGameMessage.MOVE:
			// check player's move received from server
			checkMove(playerID, (int[]) data);
			break;
		case CardGameMessage.MSG:
			// print player's message on chat room
			printChat(playerID, (String) data);
			break;
		default:
			System.out.println("Wrong message type: " + message.getType());
			// invalid message
			break;
		}
	}
	/**
	 * a method for sending the specified message to the game server. 
	 */
	@Override
	public void sendMessage(GameMessage message) {
		// TODO Auto-generated method stub
		try {
			oos.writeObject(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * a method for sending JOIN message to the server
	 */
	public synchronized void sendJoin() {
		sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, getPlayerName()));
	}
	/**
	 * a method for sending READY message to the server
	 */
	public synchronized void sendReady() {
		sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}
	/**
	 * a method for sending CHAT message to the server
	 * @param text a String storing the chat message
	 */
	public synchronized void sendChat(String text) {
		sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, text));
	}
	/**
	 * a method for sending MOVE message to the server
	 * @param cardIdx an array of indices of the cards selected, or null if pass
	 */
	public synchronized void sendMove(int[] cardIdx) {
		sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
	}
	/**
	 * a method for printing chat message on client
	 * @param playerID 
	 * @param text String storing the chat message
	 */
	private synchronized void printChat(int playerID, String text) {
		gui.printChat(text);
	}
	/**
	 * a method for checking player's move on client
	 */
	private synchronized void checkMove(int playerID, int[] cardIdx) {
		game.checkMove(playerID,cardIdx);
	}
	/**
	 * a method for removing player from player list on client
	 */
	private synchronized void removePlayer(int playerID) {
		String playerName = game.getPlayerList().get(playerID).getName();
		gui.printMsg(String.format("%s leaves the game...\n", playerName));
		game.removePlayer(playerID);
	}
	/**
	 * a method for starting game on client
	 */
	private synchronized void startGame(Deck deck) {
		gui.printMsg("Game start!!!\n");
		gameStarted = true;
		game.start(deck);
	}
	/**
	 * a method for printing "server is full" message on client
	 */
	private synchronized void printFull() {
		gui.printMsg("Server is full. Cannot join the game.\n");
	}
	/**
	 * a method for printing ready players on client
	 */
	private synchronized void printReady(int playerID) {
		String playerName = game.getPlayerList().get(playerID).getName();
		if (playerName!=null)
			gui.printMsg(String.format("%s is ready! (%d/4)\n", playerName, game.getNumOfPlayers()));
	}
	/**
	 * a method for adding player to player list on client
	 */
	private synchronized void addPlayer(int playerID, String playerName) {
		game.addPlayer(playerID,playerName);
		gui.printMsg(String.format("%s joins the game...\n", playerName));
	}
	/**
	 * a method for setting player list on client
	 */
	private synchronized void setPlayerList(String[] playerNames) {
		game.setPlayerList(playerNames);
		for (int i=0;i<game.getPlayerList().size();i++) 
			printReady(i);
	}

}
