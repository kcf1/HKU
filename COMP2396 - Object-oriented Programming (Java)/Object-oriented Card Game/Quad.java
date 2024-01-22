/**
 * The Quad class is a subclass of the Hand class and is used to model a straight in a Big Two Card game.
 */
public class Quad extends Hand{
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	public boolean isValid() {
		if (this.size() != 5) return false;
		else {
			this.sort();
			int count0 = 0;
			int count4 = 0;
			Card card0 = this.getCard(0);
			Card card4 = this.getCard(4);
			
			// sort the hand, separate the hand into quad/single or single/quad
			// count the number of cards for each set
			// if the numbers are (1,4) or (4,1), it is valid
			
			for (int i=0;i<this.size();i++) {
				Card cardI = this.getCard(i);
				if (cardI.getRank()==card0.getRank()) 
					count0++;
				else if (cardI.getRank()==card4.getRank()) 
					count4++;
			}
			if (count0==1 && count4==4)
				return true;
			else if (count0==4 && count4==1)
				return true;
			else 
				return false;
		}
	}
	public Card getTopCard() {
		/**
		 * a method for retrieving the top card of this hand.
		 */
		Card topCard;
		
		// sort the hand
		// if second last card == last card, last card is in the quad
		//     hence last card is top card
		// else last card is not in the quad
		//     hence second last card is top card
		
		this.sort();

		Card card3 = this.getCard(3);
		Card card4 = this.getCard(4);
		
		if (card3==card4)
			topCard = card4;
		else
			topCard = card3;
		return topCard;
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
		return "Quad";
	}
}
