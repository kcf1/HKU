/**
 * The Pair class is a subclass of the Hand class and is used to model a pair in a Big Two Card game.
 */
public class Pair extends Hand{
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	public boolean isValid() {
		if (this.size() != 2) return false;
		else {
			Card card0 = this.getCard(0);
			Card card1 = this.getCard(1);
			return (card0.getRank() == card1.getRank());
		}
	}
	
	public String getType() {
		return "Pair";
	}
}
