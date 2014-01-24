package com.bryde.gofserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class Player {
	private String mLogin;
	private Socket mSocket;
	private ObjectInputStream mOis;
	private ObjectOutputStream mOos;
	
	private ArrayList<GoFcard> mHand;
	
	public Player(String login, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
		setLogin(login);
		mSocket = socket;
		mOis = ois;
		mOos = oos;
	}

	public String getLogin() {
		return mLogin;
	}

	private void setLogin(String login) {
		this.mLogin = login;
	}
	
	public void sendOK() {
        GoFmessage okMessage = new GoFmessage(GoFmessage.OK, null);
		try {
			okMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendOK Exception: " + e);
	    }
	}
	
	public void sendError() {
        GoFmessage errorMessage = new GoFmessage(GoFmessage.ERROR, null);
		try {
			errorMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendError Exception: " + e);
	    }
	}
	
	public void sendStart() {
        GoFmessage startMessage = new GoFmessage(GoFmessage.GAME_START, null);
		try {
			startMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendStart Exception: " + e);
	    }
	}
	
	public void sendPlay(String logname) {
        HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.LOGNAME, logname);
        GoFmessage playMessage = new GoFmessage(GoFmessage.PLAY, msgArgs);
		try {
			playMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendPlay Exception: " + e);
	    }
	}
	
	public void sendPlayerHasPlayed(String logname, ArrayList<GoFcard> cards) {
        HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.LOGNAME, logname);
        msgArgs.put(GoFmessage.CARDS, cards);
        GoFmessage playMessage = new GoFmessage(GoFmessage.PLAYER_HAS_PLAYED, msgArgs);
		try {
			playMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendPlayerHasPlayed Exception: " + e);
	    }
	}
	
	public ArrayList<GoFcard> receivePlay() {
		ArrayList<GoFcard> cards = null;
		GoFmessage cardsMessage = new GoFmessage();
		try {
			cardsMessage.readObject(mOis);
        }
        catch (Exception e) {
          System.out.println("receivePlay Exception: " + e);
        }
		
		if(cardsMessage.getCommand() == GoFmessage.CARDS_PLAYED) {
        	cards = (ArrayList<GoFcard>)cardsMessage.getArgs().get(GoFmessage.CARDS);
        }
		
		return cards;
	}
	
	public GoFcard receiveWorstCard() {
		GoFcard worstCard = null;
		GoFmessage worstCardMessage = new GoFmessage();
		try {
			worstCardMessage.readObject(mOis);
        }
        catch (Exception e) {
          System.out.println("receivePlay Exception: " + e);
        }
		
		if(worstCardMessage.getCommand() == GoFmessage.WORST_CARD) {
        	worstCard = (GoFcard)worstCardMessage.getArgs().get(GoFmessage.CARD);
        }
		
		return worstCard;
	}
	
	public void sendDistribution(ArrayList<GoFcard> hand) {
		mHand = hand;
        HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.HAND, mHand);
        GoFmessage distributionMessage = new GoFmessage(GoFmessage.DISTRIBUTION, msgArgs);
		try {
			distributionMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendDistribution Exception: " + e);
	    }
	}
	
	public void sendAccept() {
        HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.CARDS, mHand);
        GoFmessage distributionMessage = new GoFmessage(GoFmessage.PLAY_ACCEPTED, msgArgs);
		try {
			distributionMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendAccept Exception: " + e);
	    }
	}
	
	public void sendPlayers(ArrayList<String> players) {
        HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.PLAYERS, players);
        GoFmessage playersMessage = new GoFmessage(GoFmessage.GAME_PLAYERS, msgArgs);
		try {
			playersMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendDistribution Exception: " + e);
	    }
	}
	
	public void sendGameStatus(int state, HashMap<Player, Integer> scores) {
		HashMap<String, Object> msgArgs = new HashMap<String, Object>();
		msgArgs.put(GoFmessage.STATE, Integer.valueOf(state));
        msgArgs.put(GoFmessage.SCORES, scores);
        GoFmessage statusMessage = new GoFmessage(GoFmessage.GAME_STATUS, msgArgs);
		try {
			statusMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendGameStatus Exception: " + e);
	    }
	}
	
	public void sendGiveBest(String sender, String receiver, GoFcard bestCard) {
		HashMap<String, Object> msgArgs = new HashMap<String, Object>();
		msgArgs.put(GoFmessage.SENDER, sender);
        msgArgs.put(GoFmessage.RECEIVER, receiver);
        msgArgs.put(GoFmessage.CARD, bestCard);
        GoFmessage giveBestMessage = new GoFmessage(GoFmessage.GIVE_BEST, msgArgs);
		try {
			giveBestMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendGiveBest Exception: " + e);
	    }
	}
	
	public void sendGiveWorst(String sender, String receiver, GoFcard worstCard) {
		HashMap<String, Object> msgArgs = new HashMap<String, Object>();
		msgArgs.put(GoFmessage.SENDER, sender);
        msgArgs.put(GoFmessage.RECEIVER, receiver);
        msgArgs.put(GoFmessage.CARD, worstCard);
        GoFmessage giveWorstMessage = new GoFmessage(GoFmessage.GIVE_WORST, msgArgs);
		try {
			giveWorstMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendGiveWorst Exception: " + e);
	    }
	}
	
	public void sendChooseWorst() {
        GoFmessage chooseWorstMessage = new GoFmessage(GoFmessage.CHOOSE_WORST, null);
		try {
			chooseWorstMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendChooseWorst Exception: " + e);
	    }
	}
	
	public ArrayList<GoFcard> getHand() {
		return mHand;
	}
	
	public boolean has1M() {
		for(GoFcard card : mHand) {
			if(card.getColor() == GoFcard.COLOR_MULTI)
				return true;
		}
		return false;
	}
	
	public int own(GoFcard card) {
		int i;
		for(i = 0; i < mHand.size(); i++) {
			if(mHand.get(i).equals(card))
				return i;
		}
		return -1;
	}
	
	public void updateHand(ArrayList<GoFcard> cards) {
		for(GoFcard c : cards) {
			int i = this.own(c); 
			if(i != -1)
				mHand.remove(i);
		}
	}
	
	public GoFcard getBestCard() {
		GoFcard bestCard = new GoFcard(1, GoFcard.COLOR_GREEN);
		for(GoFcard card : mHand)
			if(card.isStronger(bestCard))
				bestCard = card;
		return bestCard;
	}
	
	public void removeCard(GoFcard card) {
		for(GoFcard c : mHand) {
			if(c.equals(card)) {
				mHand.remove(c);
				return;
			}
		}
	}
	
	public void addCard(GoFcard card) {
		mHand.add(card);
	}
}
