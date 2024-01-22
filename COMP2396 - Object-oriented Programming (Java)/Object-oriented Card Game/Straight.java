/**
 * The Straight class is a subclass of the Hand class and is used to model a straight in a Big Two Card game.
 */
public class Straight extends Hand{
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	public boolean isValid() {
		if (this.size() != 5) return false;
		else {
			this.sort();
			// loop through sorted hand
			// rank of i-th card - rank of (i-1)th card must equal 1
			for (int i=1;i<this.size();i++) {
				Card card0 = this.getCard(i-1);
				Card card1 = this.getCard(i);
				if (card1.getRank()-card0.getRank() != 1)
					return false;
			}
			return true;
		}
	}
	
	public boolean beats(Hand hand) {
		/**
		 * a method for checking if this hand beats a specified hand.
		 */
		if (hand == null) return false;
		if (!this.isValid() || !hand.isValid()) return false;
		if (this.size() != hand.size()) return false;
		
		if (this.compareCombo(hand)==1) 
			return true;
		else if (this.compareCombo(hand)==-1)
			return false;
		else return (this.getTopCard().compareTo(hand.getTopCard())>0);
	}
	
	public String getType() {
		return "Straight";
	}
}
