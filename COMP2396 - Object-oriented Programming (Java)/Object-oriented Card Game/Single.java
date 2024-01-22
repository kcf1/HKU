/**
 * The Single class is a subclass of the Hand class and is used to model a single in a Big Two Card game.
 */
public class Single extends Hand{
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	public boolean isValid() {
		if (this.size() != 1) return false;
		return true;
	}
	
	public String getType() {
		return "Single";
	}
}
