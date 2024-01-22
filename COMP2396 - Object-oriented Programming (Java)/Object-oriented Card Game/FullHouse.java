/**
 * The FullHouse class is a subclass of the Hand class and is used to model a straight in a Big Two Card game.
 */
public class FullHouse extends Hand{
	public FullHouse(CardGamePlayer player, CardList cards) {
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
			
			// sort the hand, separate the hand into triple/pair or pair/triple
			// count the number of cards for each set
			// if the numbers are (2,3) or (3,2), it is invalid
			
			for (int i=0;i<this.size();i++) {
				Card cardI = this.getCard(i);
				if (cardI.getRank()==card0.getRank()) 
					count0++;
				else if (cardI.getRank()==card4.getRank()) 
					count4++;
			}
			if (count0==2 && count4==3)
				return true;
			else if (count0==3 && count4==2)
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
		// if middle card == last card, last card is in the triple
		//     hence last card is top card
		// else last card is not in the triple
		//     hence middle card is top card
		
		this.sort();

		Card card2 = this.getCard(2);
		Card card4 = this.getCard(4);
		
		if (card2==card4)
			topCard = card4;
		else
			topCard = card2;
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
		return "FullHouse";
	}
}
