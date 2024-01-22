/**
 * The Triple class is a subclass of the Hand class and is used to model a triple in a Big Two Card game.
 */
public class Triple extends Hand{
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	public boolean isValid() {
		if (this.size() != 3) return false;
		else {
			Card card0 = this.getCard(0);
			Card card1 = this.getCard(1);
			Card card2 = this.getCard(2);
			return (card0.getRank() == card1.getRank() && card1.getRank() == card2.getRank());
		}
	}
	
	public String getType() {
		return "Triple";
	}
}
