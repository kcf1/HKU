import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Hand class is a subclass of the CardList class and is used to model a hand of cards. 
 */
abstract class Hand extends CardList {
	// Instance variables
	private CardGamePlayer player;
	/** the player who plays this hand.*/
	private static ArrayList<String> comboRank = 
			new ArrayList<>(Arrays.asList(
					"Straight",
					"Flush",
					"FullHouse",
					"Quad",
					"StraightFlush"
					));	
	/** the rank of five-card combo*/
	
	// Constructors
	public Hand(CardGamePlayer player, CardList cards) {
		/**
		 * a constructor for building a hand with the specified player and list of cards.
		 */
		this.player = player;
		
		for (int i=0;i<cards.size();i++) {
			this.addCard(cards.getCard(i));
		}
		this.sort();
	}
	
	// Methods
	public CardGamePlayer getPlayer() {
		/**
		 * a method for retrieving the player of this hand.
		 */
		return this.player;
	}
	public Card getTopCard() {
		/**
		 * a method for retrieving the top card of this hand.
		 */
		Card topCard;
		
		this.sort();
		topCard = this.getCard(this.size()-1);
				
		return topCard;
	}
	public boolean beats(Hand hand) {
		/**
		 * a method for checking if this hand beats a specified hand.
		 */
		if (hand == null) return false;
		if (!this.isValid() || !hand.isValid()) return false;
		if (this.getType() != hand.getType()) return false;
		
		return (this.getTopCard().compareTo(hand.getTopCard())>0);
	}
	public abstract boolean isValid();
	/** a method for checking if this is a valid hand.*/
	public abstract String getType();
	/** a method for returning a string specifying the type of this hand.*/
	
	public int compareCombo(Hand hand) {
		/**
		 * a method for comparing the order of this five-card combo with the specified combo. 
		 */
		// get index of the declared ranking
		int thisRank = comboRank.indexOf(this.getType());
		int handRank = comboRank.indexOf(hand.getType());
		if (thisRank > handRank) {
			return 1;
		} else if (thisRank < handRank) {
			return -1;
		} else {
			return 0;
		}
	}
}
