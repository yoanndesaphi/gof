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
	
	public void sendGameOver(Player p) {
		GoFmessage gameOverMessage = new GoFmessage(GoFmessage.GAME_OVER, null);
		HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.WINNER, p.getLogin());
		try {
			gameOverMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendGameOver Exception: " + e);
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
}
