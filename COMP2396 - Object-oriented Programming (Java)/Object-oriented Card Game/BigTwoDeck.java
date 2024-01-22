/**
 * The BigTwoDeck class is a subclass of the Deck class and is used to model a deck of cards used in a Big Two card game.
 */
public class BigTwoDeck extends Deck {
	public void initialize() {
		/**
		 * a method for initializing a deck of Big Two cards.
		 */
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				// add card from 3-2 according to the Big Two order
				Card card = new BigTwoCard(i, (j+2)%13);
				addCard(card);
			}
		}
	}
}
