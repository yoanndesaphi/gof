package com.bryde.gofserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.ListIterator;

public class Game implements Runnable {
	
	private static final int PLAYER_COUNT = 4;
	private static int mGameNumber = 0;
	private int mID;
	private ArrayList<Player> mPlayerList;
	
	private HashMap<Player, Integer> mScore;
	private HashMap<Player, Boolean> mPassed;
	
	private Deck mDeck;
	
	private int mTurn;
	
	private Player mCurrentPlayer;
	private Player mPreviousTurnWinner;
	
	private boolean mClockwise;
	
	private GoFcombination mLastPlayedCombination;
	
	private boolean mPlay1M;

	private class Deck {
		private static final int NB_CARDS = 64;
		
		private ArrayList<GoFcard> mCards;
		
		public Deck() {
			mCards = new ArrayList<GoFcard>(NB_CARDS);
			for(int value = GoFcard.VALUE_MIN; value <= GoFcard.VALUE_MAX; value++) {
				for(int color = GoFcard.COLOR_GREEN; color <= GoFcard.COLOR_RED; color++) {
					mCards.add(new GoFcard(value, color));
					mCards.add(new GoFcard(value, color));
				}
			}
			mCards.add(new GoFcard(1, GoFcard.COLOR_MULTI));
			mCards.add(new GoFcard(GoFcard.VALUE_PHOENIX, GoFcard.COLOR_GREEN));
			mCards.add(new GoFcard(GoFcard.VALUE_PHOENIX, GoFcard.COLOR_YELLOW));
			mCards.add(new GoFcard(GoFcard.VALUE_DRAGON, GoFcard.COLOR_RED));
		}
		
		public void shuffle() {
			Collections.shuffle(mCards);
		}
		
		public HashMap<Player, ArrayList<GoFcard>> distribute(ArrayList<Player> players) {
			HashMap<Player, ArrayList<GoFcard>> deck = new HashMap<Player, ArrayList<GoFcard>>(players.size());
			for(Player p : players) {
				deck.put(p, new ArrayList<GoFcard>(NB_CARDS/players.size()));
			}
			
			for(int i = 0; i < NB_CARDS;) {
				for(Player p : players) {
					deck.get(p).add(mCards.get(i++));
				}
			}
			
			return deck;
		}
		
		public String toString() {
			StringBuffer sbuf = new StringBuffer();
			
			for(GoFcard card : mCards) {
				sbuf.append(card.toString());
				sbuf.append(" ");
			}
			return sbuf.toString();
		}
	}
	
	@Override
	public void run() {
		int playerNb = 1;
		System.out.println("Game #" + mID + " starts!!!");
		for(Player p : mPlayerList) {
			p.sendStart();
		}
		
		sendPlayers();
		
		while(!isOver()) {
			mTurn++;
			
			mDeck.shuffle();
			
			sendDistribution(mDeck.distribute(mPlayerList));
			
			while(!turnIsOver()) {
				while(!playIsOver()) {
					boolean validPlay = false;
					Player currentPlayer = determineCurrentPlayer();
					
					while(!validPlay) {
						sendPlay(currentPlayer.getLogin());
					
						ArrayList<GoFcard> play = currentPlayer.receivePlay();
						
						GoFcombination currentCombination = new GoFcombination(play);
						
						if(checkPlay(currentCombination)) {
							validPlay = true;
							currentPlayer.updateHand(play);
							currentPlayer.sendAccept();
							sendPlayerHasPlayed(currentPlayer.getLogin(), play);
							mLastPlayedCombination = currentCombination;
							if(mPlay1M) mPlay1M = false;
						} else {
							currentPlayer.sendError();
						}
					}
				}
				mLastPlayedCombination = null;
				mPreviousTurnWinner = determinePlayWinner();
				resetPassed();
			}
			
			updateScore();
			
			invertDirection();
		}
		
		for(Player p : mPlayerList) {
			p.sendGameOver(determineGameWinner());
		}
	}
	
	public Game() {
		setID(++mGameNumber);
		mPlayerList = new ArrayList<Player>(PLAYER_COUNT);
		mScore = new HashMap<Player, Integer>(PLAYER_COUNT);
		mPassed = new HashMap<Player, Boolean>(PLAYER_COUNT);
		mDeck = new Deck();
		mTurn = 0;
		mCurrentPlayer = null;
		mPreviousTurnWinner = null;
		mClockwise = false; 
		mLastPlayedCombination = null;
		mPlay1M = true;
	}
	
	public void addPlayer(Player p) {
		if(mPlayerList.size() < PLAYER_COUNT) {
			mPlayerList.add(p);
			mScore.put(p, Integer.valueOf(0));
			mPassed.put(p, Boolean.valueOf(false));
			p.sendOK();
		}
	}

	public boolean isWaiting() {
		return mPlayerList.size() < PLAYER_COUNT;
	}
	
	public boolean isReady() {
		return mPlayerList.size() == PLAYER_COUNT;
	}
	
	public boolean isOver() {
		boolean over = false;
		for(Integer score : mScore.values()) {
			if(score.intValue() >= 100)
				over = true;
		}
		return over;
	}

	public int getID() {
		return mID;
	}

	private void setID(int mID) {
		this.mID = mID;
	}
	
	private void sendDistribution(HashMap<Player, ArrayList<GoFcard>> distribution) {
		for(Player player : distribution.keySet()) {
			StringBuffer sbuf = new StringBuffer();
			
			for(GoFcard card : distribution.get(player)) {
				sbuf.append(card.toString());
				sbuf.append(" ");
			}
			
			player.sendDistribution(distribution.get(player));
		}
	}
	
	private void sendPlay(String logname) {
		for(Player p : mPlayerList) {
			p.sendPlay(logname);
		}
	}
	
	private void sendPlayerHasPlayed(String logname, ArrayList<GoFcard> cards) {
		for(Player p : mPlayerList) {
			p.sendPlayerHasPlayed(logname, cards);
		}
	}
	
	private Player determineCurrentPlayer() {
		if(mCurrentPlayer == null) {
			if(mTurn == 1) {
				for(Player p : mPlayerList) {
					if(p.has1M())
						mCurrentPlayer = p;
				}
			} else {
				mCurrentPlayer = mPreviousTurnWinner;
			}
		} else {
			do {
				int index = mPlayerList.indexOf(mCurrentPlayer);
				
				if(!mClockwise) {
					if(index == 0)
						mCurrentPlayer = mPlayerList.get(PLAYER_COUNT-1);
					else
						mCurrentPlayer = mPlayerList.get(index-1);
					
				} else {
					if(index == PLAYER_COUNT-1)
						mCurrentPlayer = mPlayerList.get(index+1);
					else
						mCurrentPlayer = mPlayerList.get(0);
				}
			} while(mPassed.get(mCurrentPlayer).booleanValue());
		}
		
		return mCurrentPlayer;
	}
	
	private boolean turnIsOver() {
		for(Player p : mPlayerList) {
			if(p.getHand().isEmpty())
				return true;
		}
		return false;
	}
	
	private boolean playIsOver() {
		int nb_passed = 0;
		for(Player p : mPlayerList) {
			if(mPassed.get(p).booleanValue())
				nb_passed++;
		}
		
		return nb_passed == 3;
	}
	
	private Player determinePlayWinner() {
		Player winner = null;
		for(Player p : mPlayerList) {
			if(!mPassed.get(p).booleanValue())
				winner = p;
		}
		return winner;
	}
	
	private Player determineGameWinner() {
		int bestScore = 100;
		Player bestPlayer = mPlayerList.get(0);
		
		for(Player p : mPlayerList) {
			if(mScore.get(p).intValue() < bestScore) {
				bestPlayer = p;
				bestScore = mScore.get(p).intValue();
			}
		}
		
		return bestPlayer;
	}
	
	private void invertDirection() {
		mClockwise = !mClockwise;
	}
	
	private boolean checkPlay(GoFcombination c) {
		if(c.isPass()) {
			setPassed(mCurrentPlayer);
			return true;
		}
		
		for(GoFcard card : c.getCards())
			if(mCurrentPlayer.own(card) == -1)
				return false;
		
		if(!c.isValid()) return false;
		
		if(mPlay1M) {
			if(!c.contains1M())
				return false;
		}
		
		return c.isStronger(mLastPlayedCombination);
	}
	
	private void resetPassed() {
		for(Player p : mPlayerList) {
			mPassed.put(p, Boolean.valueOf(false));
		}
	}
	
	private void setPassed(Player p) {
		mPassed.put(p, Boolean.valueOf(true));
	}
	
	private void updateScore() {
		for(Player p : mPlayerList) {
			int remainingCards = p.getHand().size();
			
			if(remainingCards < 8) {
				// x1
				mScore.put(p, Integer.valueOf(mScore.get(p).intValue()+remainingCards));
			} else if(remainingCards < 11) {
				// x2
				mScore.put(p, Integer.valueOf(mScore.get(p).intValue()+remainingCards)*2);
			} else if(remainingCards < 14) {
				// x3
				mScore.put(p, Integer.valueOf(mScore.get(p).intValue()+remainingCards)*3);
			} else if(remainingCards < 16) {
				// x4
				mScore.put(p, Integer.valueOf(mScore.get(p).intValue()+remainingCards)*4);
			} else {
				// +80
				mScore.put(p, Integer.valueOf(mScore.get(p).intValue()+80));
			}
		}
	}
	
	private void sendPlayers() {
		ArrayList<String> players = new ArrayList<String>(this.PLAYER_COUNT);
		for(Player p : mPlayerList)
			players.add(p.getLogin());
		
		for(Player p : mPlayerList)
			p.sendPlayers(players);
	}
}
