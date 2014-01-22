package com.bryde.gofserver;

import java.util.ArrayList;

public class GoFcombination {
	private static final int INVALID			= 0;
	private static final int PASS				= 1;
	private static final int UNITARY			= 2;
	private static final int PAIR				= 3;
	private static final int THREE_OF_A_KIND	= 4;
	private static final int HAND				= 5;
	private static final int COLOR				= 6;
	private static final int FULL				= 7;
	private static final int COLOR_HAND			= 8;
	private static final int GANG_OF_FOUR		= 9;
	private static final int GANG_OF_FIVE		= 10;
	private static final int GANG_OF_SIX		= 11;
	private static final int GANG_OF_SEVEN		= 12;
	
	private int mType;
	private ArrayList<GoFcard> mCards;
	
	public GoFcombination(ArrayList<GoFcard> cards) {
		mCards = new ArrayList<GoFcard>(cards); 
		
		sortCards(mCards);
		
		checkValidity();
	}
	
	public int getType() {
		return mType;
	}
	
	public ArrayList<GoFcard> getCards() {
		return mCards;
	}
	
	public static ArrayList<GoFcard> sortCards(ArrayList<GoFcard> cards) {
		ArrayList<GoFcard> cardsToSort = new ArrayList<GoFcard>(cards);
		ArrayList<GoFcard> sortedList = new ArrayList<GoFcard>(cardsToSort.size());
		
		while(!cardsToSort.isEmpty()) {
			int weakestCardIndex = 0;
			for(int i = 0; i < cardsToSort.size(); i++) {
				if(!cardsToSort.get(i).isStronger(cardsToSort.get(weakestCardIndex)))
					weakestCardIndex = i;
			}
			sortedList.add(cardsToSort.get(weakestCardIndex));
			
			cardsToSort.remove(weakestCardIndex);
		}
		return sortedList;
	}
	
	public static void displayCardList(ArrayList<GoFcard> cards) {
		StringBuffer sbuf = new StringBuffer("Hand: ");
		
		for(GoFcard card : cards) {
			sbuf.append(card.toString());
			sbuf.append(" ");
		}
		System.out.println(sbuf.toString());
	}
	
	private boolean isUnitary() {
		return mCards.size() == 1;
	}
	
	private boolean isPair() {
		if(mCards.size() == 2) {
			return mCards.get(0).getValue() == mCards.get(1).getValue();
		}
		return false;
	}
	
	private boolean isThreeOfAKind() {
		if(mCards.size() == 3) {
			return mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() == mCards.get(2).getValue();
		}
		return false;
	}
	
	private boolean isHand() {
		if(mCards.size() == 5) {
			return mCards.get(0).getValue() <= 6 && mCards.get(1).getValue() == mCards.get(0).getValue()+1
					&& mCards.get(2).getValue() == mCards.get(0).getValue()+2 && mCards.get(3).getValue() == mCards.get(0).getValue()+3
					&& mCards.get(4).getValue() == mCards.get(0).getValue()+4;
		}
		return false;
	}
	
	private boolean isColor() {
		if(mCards.size() == 5) {
			return mCards.get(0).getValue() <= GoFcard.VALUE_MAX &&
					mCards.get(1).getValue() <= GoFcard.VALUE_MAX && mCards.get(0).getColor() == mCards.get(1).getColor()
					&& mCards.get(2).getValue() <= GoFcard.VALUE_MAX && mCards.get(0).getColor() == mCards.get(2).getColor()
					&& mCards.get(3).getValue() <= GoFcard.VALUE_MAX && mCards.get(0).getColor() == mCards.get(3).getColor()
					&& mCards.get(4).getValue() <= GoFcard.VALUE_MAX && mCards.get(0).getColor() == mCards.get(4).getColor();
		}
		return false;
	}
	
	private boolean isFull() {
		if(mCards.size() == 5) {
			return (mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() == mCards.get(2).getValue()
					&& mCards.get(0).getValue() != mCards.get(3).getValue() && mCards.get(3).getValue() == mCards.get(4).getValue())
					|| (mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() != mCards.get(2).getValue()
					&& mCards.get(2).getValue() == mCards.get(3).getValue() && mCards.get(3).getValue() == mCards.get(4).getValue());
		}
		return false;
	}
	
	private boolean isColorHand() {
		if(mCards.size() == 5) {
			return mCards.get(0).getValue() <= 6 && mCards.get(1).getValue() == mCards.get(0).getValue()+1
					&& mCards.get(2).getValue() == mCards.get(0).getValue()+2 && mCards.get(3).getValue() == mCards.get(0).getValue()+3
					&& mCards.get(4).getValue() == mCards.get(0).getValue()+4
					&& mCards.get(0).getColor() == mCards.get(1).getColor() && mCards.get(0).getColor() == mCards.get(2).getColor()
					&& mCards.get(0).getColor() == mCards.get(3).getColor() && mCards.get(0).getColor() == mCards.get(4).getColor();
		}
		return false;
	}
	
	private boolean isGangOfFour() {
		if(mCards.size() == 4) {
			return mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() == mCards.get(2).getValue() && mCards.get(0).getValue() == mCards.get(3).getValue();
		}
		return false;
	}
	
	private boolean isGangOfFive() {
		if(mCards.size() == 5) {
			return mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() == mCards.get(2).getValue() && mCards.get(0).getValue() == mCards.get(3).getValue() && mCards.get(0).getValue() == mCards.get(4).getValue();
		}
		return false;
	}
	
	private boolean isGangOfSix() {
		if(mCards.size() == 6) {
			return mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() == mCards.get(2).getValue() && mCards.get(0).getValue() == mCards.get(3).getValue() && mCards.get(0).getValue() == mCards.get(4).getValue() && mCards.get(0).getValue() == mCards.get(5).getValue();
		}
		return false;
	}
	
	private boolean isGangOfSeven() {
		if(mCards.size() == 7) {
			return mCards.get(0).getValue() == mCards.get(1).getValue() && mCards.get(0).getValue() == mCards.get(2).getValue() && mCards.get(0).getValue() == mCards.get(3).getValue() && mCards.get(0).getValue() == mCards.get(4).getValue() && mCards.get(0).getValue() == mCards.get(5).getValue() && mCards.get(0).getValue() == mCards.get(6).getValue();
		}
		return false;
	}
	
	private void checkValidity() {
		mType = INVALID;
		if(isPass()) {
			mType = PASS;
		} else if(isUnitary()) {
			mType = UNITARY;
		} else if(isPair()) {
			mType = PAIR;
		} else if(isThreeOfAKind()) {
			mType = THREE_OF_A_KIND;
		} else if(isColorHand()) {
			mType = COLOR_HAND;
		} else if (isHand()) {
			mType = HAND;
		} else if(isColor()) {
			mType = COLOR;
		} else if(isFull()) {
			mType = FULL;
		} else if(isGangOfFour()) {
			mType = GANG_OF_FOUR;
		} else if(isGangOfFive()) {
			mType = GANG_OF_FIVE;
		} else if(isGangOfSix()) {
			mType = GANG_OF_SIX;
		} else if(isGangOfSeven()) {
			mType = GANG_OF_SEVEN;
		} 
	}
	
	public boolean isValid() {
		return mType != INVALID;
	}
	
	public boolean isPass() {
		return mCards.size() == 0;
	}
	
	public boolean contains1M() {
		for(GoFcard c : mCards)
			if(c.getValue() == 1 && c.getColor() == GoFcard.COLOR_MULTI)
				return true;
		return false;
	}
	
	public boolean isStronger(GoFcombination c) {
		boolean stronger = false;
		
		if(c == null) return true;
		
		switch(mType) {
			case UNITARY:
				stronger = isUnitaryStronger(c);
				break;
			case PAIR:
				stronger = isPairStronger(c);
				break;
			case THREE_OF_A_KIND:
				stronger = isThreeOfAKindStronger(c);
				break;
			case HAND:
				stronger = isHandStronger(c);
				break;
			case COLOR:
				stronger = isColorStronger(c);
				break;
			case FULL:
				stronger = isFullStronger(c);
				break;
			case COLOR_HAND:
				stronger = isColorHandStronger(c);
				break;
			case GANG_OF_FOUR:
				stronger = isGangOfFourStronger(c);
				break;
			case GANG_OF_FIVE:
				stronger = isGangOfFiveStronger(c);
				break;
			case GANG_OF_SIX:
				stronger = isGangOfSixStronger(c);
				break;
			case GANG_OF_SEVEN:
				stronger = true;
				break;
		}
		
		return stronger;
	}
	
	private boolean isUnitaryStronger(GoFcombination c) {
		if(c.getType() == UNITARY) {
			if(mCards.get(0).getValue() > c.getCards().get(0).getValue()) {
				return true;
			} else if(mCards.get(0).getValue() == c.getCards().get(0).getValue()
					&& mCards.get(0).getColor() > c.getCards().get(0).getColor()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isPairStronger(GoFcombination c) {
		if(c.getType() == PAIR) {
			if(mCards.get(0).getValue() > c.getCards().get(0).getValue()) {
				return true;
			} else if(mCards.get(0).getValue() == c.getCards().get(0).getValue()
					&& mCards.get(1).getColor() > c.getCards().get(1).getColor()) {
				return true;
			} else if(mCards.get(0).getValue() == c.getCards().get(0).getValue()
					&& mCards.get(1).getColor() == c.getCards().get(1).getColor()
					&& mCards.get(0).getColor() > c.getCards().get(0).getColor()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isThreeOfAKindStronger(GoFcombination c) {
		if(c.getType() == THREE_OF_A_KIND) {
			if(mCards.get(0).getValue() > c.getCards().get(0).getValue()) {
				return true;
			} else if(mCards.get(0).getValue() == c.getCards().get(0).getValue()
					&& mCards.get(2).getColor() > c.getCards().get(2).getColor()) {
				return true;
			} else if(mCards.get(0).getValue() == c.getCards().get(0).getValue()
					&& mCards.get(2).getColor() == c.getCards().get(2).getColor()
					&& mCards.get(1).getColor() > c.getCards().get(1).getColor()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isHandStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == HAND) {
			if(mCards.get(4).getValue() > c.getCards().get(4).getValue()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(4).getColor() > c.getCards().get(4).getColor()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(4).getColor() == c.getCards().get(4).getColor()
					&& mCards.get(3).getColor() > c.getCards().get(3).getColor()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(4).getColor() == c.getCards().get(4).getColor()
					&& mCards.get(3).getColor() == c.getCards().get(3).getColor()
					&& mCards.get(2).getColor() > c.getCards().get(2).getColor()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(4).getColor() == c.getCards().get(4).getColor()
					&& mCards.get(3).getColor() == c.getCards().get(3).getColor()
					&& mCards.get(2).getColor() == c.getCards().get(2).getColor()
					&& mCards.get(1).getColor() > c.getCards().get(1).getColor()) {
				return true;
			}  else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(4).getColor() == c.getCards().get(4).getColor()
					&& mCards.get(3).getColor() == c.getCards().get(3).getColor()
					&& mCards.get(2).getColor() == c.getCards().get(2).getColor()
					&& mCards.get(1).getColor() == c.getCards().get(1).getColor()
					&& mCards.get(0).getColor() > c.getCards().get(0).getColor()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isColorStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == COLOR) {
			if(mCards.get(4).getValue() > c.getCards().get(4).getValue()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(3).getValue() > c.getCards().get(3).getValue()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(3).getValue() == c.getCards().get(3).getValue()
					&& mCards.get(2).getValue() > c.getCards().get(2).getValue()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(3).getValue() == c.getCards().get(3).getValue()
					&& mCards.get(2).getValue() == c.getCards().get(2).getValue()
					&& mCards.get(1).getValue() > c.getCards().get(1).getValue()) {
				return true;
			} else if(mCards.get(4).getValue() == c.getCards().get(4).getValue()
					&& mCards.get(3).getValue() == c.getCards().get(3).getValue()
					&& mCards.get(2).getValue() == c.getCards().get(2).getValue()
					&& mCards.get(1).getValue() == c.getCards().get(1).getValue()
					&& mCards.get(0).getValue() > c.getCards().get(0).getValue()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isFullStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == FULL) {
			if(this.threeOfAKindValueInFull() > c.threeOfAKindValueInFull()) {
				return true;
			} else if (this.threeOfAKindValueInFull() == c.threeOfAKindValueInFull()
					&& this.pairValueInFull() > c.pairValueInFull()) {
				return true;
			}
		}
		
		return false;
	}
	
	private int pairValueInFull() {
		if(mCards.get(0).getValue() == mCards.get(2).getValue())
			return mCards.get(4).getValue();
		else
			return mCards.get(0).getValue();
	}
	
	private int threeOfAKindValueInFull() {
		if(mCards.get(0).getValue() == mCards.get(2).getValue())
			return mCards.get(0).getValue();
		else
			return mCards.get(4).getValue();
	}
	
	private boolean isColorHandStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == COLOR_HAND) {
			if(mCards.get(4).getValue() > c.getCards().get(4).getValue())
				return true;
		}
		
		return false;
	}
	
	private boolean isGangOfFourStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == GANG_OF_FOUR) {
			if(mCards.get(0).getValue() > c.getCards().get(0).getValue())
				return true;
		}
		
		return false;
	}
	
	private boolean isGangOfFiveStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == GANG_OF_FIVE) {
			if(mCards.get(0).getValue() > c.getCards().get(0).getValue())
				return true;
		}
		
		return false;
	}
	
	private boolean isGangOfSixStronger(GoFcombination c) {
		if(mType > c.getType())
			return true;
		
		if(c.getType() == GANG_OF_SIX) {
			if(mCards.get(0).getValue() > c.getCards().get(0).getValue())
				return true;
		}
		
		return false;
	}
}
