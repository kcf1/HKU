/**
 * The Flush class is a subclass of the Hand class and is used to model a straight in a Big Two Card game.
 */
public class Flush extends Hand{
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	public boolean isValid() {
		if (this.size() != 5) return false;
		else {
			// all cards must have same suit
			// if any of two doesn't, return false
			for (int i=1;i<this.size();i++) {
				Card card0 = this.getCard(i-1);
				Card card1 = this.getCard(i);
				if (card1.getSuit()!=card0.getSuit())
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
		else if (this.getTopCard().getSuit()>hand.getTopCard().getSuit())
			return true;
		else
			return (this.getTopCard().getRank()>hand.getTopCard().getRank());
	}
	
	public String getType() {
		return "Flush";
	}
}
