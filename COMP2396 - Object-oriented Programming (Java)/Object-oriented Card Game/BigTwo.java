import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * The BigTwo class implements the CardGame interface and is used to model a Big Two card game.
 */
public class BigTwo {
	// Instance variables
	/** an int specifying the number of players.*/
	private int numOfPlayers;
	/** a deck of cards.*/
	private Deck deck;
	/** a list of players*/
	private ArrayList<CardGamePlayer> playerList;
	/** a list of hands played on the table.*/
	private ArrayList<Hand> handsOnTable;
	/** an integer specifying the index of the current player.*/
	private int currentPlayerIdx;
	/** a BigTwoGUI object for providing the user interface.*/
	private BigTwoGUI ui;
	/** a BigTwoCard object containing diamond of three*/
	private static Card diamondThree = new BigTwoCard(0,2);
	/** an int specifying the index of the last player*/
	private CardGamePlayer lastPlayer;
	/** a BigTwoClient object communicating with the Big Two game server.*/
	private BigTwoClient client;
	// Constructors
	/**
	 * a constructor for creating a Big Two card game. 
	 */
	public BigTwo(){
		setNumOfPlayers(0);
		setPlayerList(new String[4]);
		reset();
		/*
		for (int i=0;i<numOfPlayers;i++) {
			this.playerList.add(new CardGamePlayer());
		}*/
		//this.handsOnTable.add(null);
		
		/*
		//this.ui.printMsg("New game\n");
		CardList cards = new CardList();
		cards.addCard(diamondThree);
		cards.addCard(diamondThree);
		handsOnTable.add(composeHand(playerList.get(currentPlayerIdx),cards));
		*/
		
		this.ui = new BigTwoGUI(this);	
		this.client = new BigTwoClient(this,ui);
		ui.setClient(client);
		}
	
	// Methods
	/**
	 * a method for setting the number of players
	 * @param n number of players
	 */
	public void setNumOfPlayers(int n) {
		numOfPlayers = n;
	}
	/**
	 * a method for getting the number of players.
	 * @return number of players
	 */
	public int getNumOfPlayers() {
		return this.numOfPlayers;
	}
	/**
	 * a method for retrieving the deck of cards being used.
	 * @return the Deck object for this BigTwoGame
	 */
	public Deck getDeck() {
		return this.deck;
	}
	/**
	 * a method for setting playerList
	 * @param playerNames an array storing the names of players
	 */
	public void setPlayerList(String[] playerNames) {
		playerList = new ArrayList<CardGamePlayer>();
		int n = 0;
		for (int i=0; i < playerNames.length; i++) {
			String playerName = playerNames[i];
			if (playerName!=null) n++;
			playerList.add(new CardGamePlayer(playerName));
		}
		setNumOfPlayers(n);
	}
	/**
	 * a method for retrieving the list of players.
	 * @return an ArrayList storing the CardGamePlayer objects.
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return this.playerList;
	}
	/**
	 * a method for adding player to the playerList
	 * @param playerID player's index in the playerList
	 * @param playerName player's name
	 */
	public void addPlayer(int playerID, String playerName) {
		getPlayerList().get(playerID).setName(playerName);
		setNumOfPlayers(getNumOfPlayers()+1);
	}
	/**
	 * a method for removing player from the playerList
	 * @param playerID player's index in the playerList
	 */
	public void removePlayer(int playerID) {
		getPlayerList().get(playerID).setName(null);
		setNumOfPlayers(getNumOfPlayers()-1);
	}
	/**
	 * a method for retrieving the list of hands played on the table.
	 * @return an ArrayList storing the hand objects on the table
	 */
	public ArrayList<Hand> getHandsOnTable(){
		return this.handsOnTable;
	}
	/**
	 * a method for retrieving the index of the current player.
	 * @return current player's index in the playerList
	 */
	public int getCurrentPlayerIdx() {
		return this.currentPlayerIdx;
	}
	/**
	 * a method for resetting the game.
	 */
	public void reset() {
		// (i) remove cards from players and table
		for (int i=0;i<4;i++) {
			this.playerList.get(i).removeAllCards();
		}
		this.handsOnTable = new ArrayList<Hand>();
	}
	/**
	 * a method for starting/restarting the game with a given shuffled deck of cards.
	 * @param deck a given shuffled Deck object.
	 */
	public void start(Deck deck) {
		reset();
		ui.reset();
		this.handsOnTable = new ArrayList<Hand>();
		// (ii) distribute cards
		Card card;
		int diamondThreePlayerIdx = 0;
		Card diamondThree = new BigTwoCard(0,2);
		for (int j=0;j<13;j++) {
			for (int i=0;i<4;i++) {
				card = deck.getCard(0);
				deck.removeCard(0);
				this.playerList.get(i).addCard(card);

				// (iii) identify the player who holds diamond 3
				if (card.compareTo(diamondThree)==0)
					diamondThreePlayerIdx = i;
			}
		}
		for (int i=0;i<4;i++) {
			this.playerList.get(i).sortCardsInHand();
		}
		// (iv) set currentPlayerIdx and activePlayer
		this.currentPlayerIdx = diamondThreePlayerIdx;
		this.ui.setActivePlayer(currentPlayerIdx);
		
		// (v) call repaint
		this.ui.repaint();
		// (vi) call promptActivePlayer
		//this.ui.promptActivePlayer();
	}
	/**
	 * a method for the local player making his move
	 * @param playerIdx player's ID in the playerList
	 * @param cardIdx an array of indices of the cards selected, or null if pass
	 */
	public synchronized void makeMove(int playerIdx, int[] cardIdx) {
		// send MOVE message to server
		client.sendMove(cardIdx);
	}
	/**
	 * a method for checking whether the player's move is legal or not
	 * @param playerIdx player's ID in the playerList
	 * @param cardIdx an array of indices of the cards selected, or null if pass
	 */
	public synchronized void checkMove(int playerIdx,int[] cardIdx) {
		
		Hand hand = null;
		
		CardGamePlayer player = this.playerList.get(playerIdx);
		CardList cards = player.play(cardIdx);
		String playerName = player.getName();
		
		//System.out.print(player);
		//System.out.print(" plays ");
		//System.out.println(cards);
		
		boolean legal = true;
		
		Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
		if (lastHandOnTable==null) {
			if (cards==null) {
				//System.out.println("Cannot pass on the first round");
				//this.ui.printMsg("Not a legal move!!!\n");
				legal = false;
			}
			else if (!cards.contains(diamondThree)) {
				//System.out.println("Cannot play without diamond 3 on the first round");
				//this.ui.printMsg("Not a legal move!!!\n");
				legal = false;
			}
			else {
				hand = composeHand(player,cards);
				if (hand==null) {
					//System.out.println("First round invalid hand");
					//this.ui.printMsg("Not a legal move!!!\n");
					legal = false;
				}
			}
		}
		else {
			if (player==lastPlayer) {
				if (cards==null) {
					//System.out.println("Biggest player cannot pass");
					//this.ui.printMsg("Not a legal move!!!\n");
					legal = false;
				}
				else {
					hand = composeHand(player,cards);
					if (hand==null) {
						//System.out.println("Biggest player invalid hand");
						//System.out.println("Invalid hand");
						//this.ui.printMsg("Not a legal move!!!\n");
						legal = false;
					}
				}
			}
			else {
				if (cards!=null) {
					hand = composeHand(player,cards);
					//System.out.println(player.getName());
					//System.out.println(cards);
					if (hand==null) {
						//System.out.println("Invalid hand");
						//this.ui.printMsg("Not a legal move!!!\n");
						legal = false;
					}
					else if (!hand.beats(lastHandOnTable)) {
						//System.out.println("Fail to beat last hand");
						//this.ui.printMsg("Not a legal move!!!\n");
						legal = false;
					}
				}
			}
			
		}
		//System.out.println(legal);
		
		if (legal==true) {
			if (cards==null) {
				this.ui.printMsg(playerName+": "+"{Pass} \n");
				//this.ui.printMsg("\n");
			}
			else {
				player.removeCards(cards);
				this.handsOnTable.add(hand);
				this.lastPlayer = player;
				this.ui.printMsg(String.format("%s: {%s} %s \n",playerName,hand.getType(),hand.toString()));
				
				// checking whether the game has ended
				if (endOfGame()) {
					// disable gui, prompt end message, and reset the game
					ui.repaint();
					ui.promptEnd(currentPlayerIdx);
					ui.disable();
					reset();
					ui.reset();
				}
			}
			this.currentPlayerIdx = (this.currentPlayerIdx+1)%4;
			this.ui.setActivePlayer(this.currentPlayerIdx);
		}
		this.ui.repaint();
	}
	/**
	 * a method for checking if the game ends.
	 * @return true if the game has ended, false otherwise
	 */
	public boolean endOfGame() {
		CardGamePlayer currentPlayer = this.playerList.get(currentPlayerIdx);
		Boolean isEnd = currentPlayer.getCardsInHand().isEmpty();
		return isEnd;
	}
	/**
	 * a method for starting a Big Two card game.
	 */
	public static void main(String[] args) {
		BigTwo game = new BigTwo();
		/*
		BigTwoDeck deck = new BigTwoDeck();
		deck.shuffle();
		game.start(deck);
		*/
	}
	/**
	 * a method for returning a valid hand from the specified list of cards of the player. 
	 * @param player the player of the hand
	 * @param cards cards to compose the Hand
	 * @return the Hand played by the specified player
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand hand;
		if (cards.size() == 1) {
			hand = new Single(player,cards);
			if (hand.isValid()) {
				return hand;
			}
		}
		else if (cards.size() == 2) {
			hand = new Pair(player,cards);
			if (hand.isValid()) {
				return hand;
			}
		}
		else if (cards.size() == 3) {
			hand = new Triple(player,cards);
			if (hand.isValid()) {
				return hand;
			}
		}
		else if (cards.size() == 5){
			hand = new StraightFlush(player,cards);
			if (hand.isValid()) return hand;
			
			hand = new Quad(player,cards);
			if (hand.isValid()) return hand;

			hand = new FullHouse(player,cards);
			if (hand.isValid()) return hand;
			
			hand = new Flush(player,cards);
			if (hand.isValid()) return hand;
			
			hand = new Straight(player,cards);
			if (hand.isValid()) return hand;
		}
		return null;
	}
}
