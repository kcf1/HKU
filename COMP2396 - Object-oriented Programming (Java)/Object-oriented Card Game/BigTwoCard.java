/**
 * The BigTwoCard class is a subclass of the Card class and is used to model a card used in a Big Two card game. 
 */
public class BigTwoCard extends Card {
	public BigTwoCard(int suit, int rank) {
		/**
		 * a constructor for building a card with the specified suit and rank. 
		 */
		super(suit,rank);
	}
	public int getRank() {
		/**
		 * a to get the rank adjusted by the Big Two rule
		 */
		// reorder the rank without changing the instance variable
		return (this.rank+11)%13;
	}
	public int compareTo(Card card) {
		/**
		 * a method for comparing the order of this card with the specified card. 
		 */
		if (this.getRank() > card.getRank()) {
			return 1;
		} else if (this.getRank() < card.getRank()) {
			return -1;
		} else if (this.getSuit() > card.getSuit()) {
			return 1;
		} else if (this.getSuit() < card.getSuit()) {
			return -1;
		} else {
			return 0;
		}
	}
	public boolean equals(Object card) {
		return (this.getRank() == ((BigTwoCard) card).getRank() && this.getSuit() == ((BigTwoCard) card).getSuit());
	}
}
