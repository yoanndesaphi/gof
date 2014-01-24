package com.bryde.gofplayer;

import com.bryde.gofserver.GoFcard;
import com.bryde.gofserver.GoFmessage;
import com.bryde.gofserver.GoFcombination;

import java.net.InetAddress;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoFplayer {
	private static final String GOF_HOST = "localhost";
	private final static int GOF_PORT = 19999;
	private Socket mConnection;
	private ObjectOutputStream mOos;
	private ObjectInputStream mOis;
	private String mLogin;
	private ArrayList<GoFcard> mHand;
	private boolean mGameOver;
	private String mWinner;
	private ArrayList<String> mPlayers;
	private HashMap<String, Integer> mScores;
	private String mCurrentPlayer;
	
	public GoFplayer(String login) {
		this.mLogin = login;
		this.mGameOver = false;
		this.mScores = new HashMap<String, Integer>(4);
		this.mCurrentPlayer = "";
		
		try {
			/** Obtain an address object of the server */
	        InetAddress address = InetAddress.getByName(GOF_HOST);
	        /** Establish a socket connection */
	        mConnection = new Socket(address, GOF_PORT);
		    mOis = new ObjectInputStream(mConnection.getInputStream());
		    mOos = new ObjectOutputStream(mConnection.getOutputStream());
		}
		catch (Exception e) {
	        System.out.println("Exception: " + e);
	    }
	}
	
	public boolean isGameOver() {
		return mGameOver;
	}
	
	public void closeConnection() {
		try {
			mOos.close();
			mOis.close();
			mConnection.close();
		} catch(IOException e) {
			System.out.println("Exception: " + e);
		}
	}
	
	public void handleMessage() {
		GoFmessage message = new GoFmessage();
		try {
	        /* wait for response */
			message.readObject(mOis);
        }
		catch (EOFException eofe) {
			System.out.println("handleMessage ERROR : Server disconnected!!!");
			mGameOver = true;
		}
        catch (Exception e) {
          System.out.println("handleMessage Exception: " + e);
        }
		
		switch(message.getCommand()) {
			case GoFmessage.DISTRIBUTION:
				mHand = (ArrayList<GoFcard>)message.getArgs().get(GoFmessage.HAND);
				mHand = GoFcombination.sortCards(mHand);
				GoFcombination.displayCardList(mHand);
				break;
			case GoFmessage.PLAY:
				mCurrentPlayer = (String)message.getArgs().get(GoFmessage.LOGNAME);
				if(this.mLogin.equals(mCurrentPlayer))
					while(!play());
				break;
			case GoFmessage.PLAYER_HAS_PLAYED:
				String player = (String)message.getArgs().get(GoFmessage.LOGNAME);
				ArrayList<GoFcard> cards = (ArrayList<GoFcard>)message.getArgs().get(GoFmessage.CARDS);
				System.out.println(player + " has played :");
				GoFcombination.displayCardList(cards);
				break;
			case GoFmessage.GAME_PLAYERS:
				mPlayers = new ArrayList<String>((ArrayList<String>)message.getArgs().get(GoFmessage.PLAYERS));
				for(String str : mPlayers)
					mScores.put(str, Integer.valueOf(0));
				break;
			case GoFmessage.GAME_STATUS:
				Integer state = (Integer)message.getArgs().get(GoFmessage.STATE);
				switch(state.intValue()) {
					case GoFmessage.STATE_NEW_PLAY:
						System.out.println("New Play");
						break;
					case GoFmessage.STATE_GAME_OVER:
						mGameOver = true;
						System.out.println("Game over!");
						System.out.println("Scores:");
						for(Entry<String, Integer> score : ((HashMap<String, Integer>)message.getArgs().get(GoFmessage.SCORES)).entrySet())
							System.out.println(score.getKey() + " : " + score.getValue());
						break;
					case GoFmessage.STATE_NEW_TURN:
						System.out.println("Scores:");
						for(Entry<String, Integer> score : ((HashMap<String, Integer>)message.getArgs().get(GoFmessage.SCORES)).entrySet())
							System.out.println(score.getKey() + " : " + score.getValue());
						System.out.println("New turn starts");
						break;
				}
				break;
			case GoFmessage.CHOOSE_WORST:
				sendWorst();
				break;
			case GoFmessage.GIVE_BEST:
				GoFcard bestCard = (GoFcard)message.getArgs().get(GoFmessage.CARD);
				String sender = (String)message.getArgs().get(GoFmessage.SENDER);
				String receiver = (String)message.getArgs().get(GoFmessage.RECEIVER);
				System.out.println(sender + " gives " + bestCard.toString() + " to " + receiver);
				break;
			case GoFmessage.GIVE_WORST:
				GoFcard worstCard = (GoFcard)message.getArgs().get(GoFmessage.CARD);
				String sender1 = (String)message.getArgs().get(GoFmessage.SENDER);
				String receiver1 = (String)message.getArgs().get(GoFmessage.RECEIVER);
				System.out.println(sender1 + " gives " + worstCard.toString() + " to " + receiver1);
				break;
		}
	}
	
	public void sendJoinMessage() {
		/* send request to join a new game */
        HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.LOGNAME, mLogin);
        GoFmessage joinMessage = new GoFmessage(GoFmessage.JOIN_NEW_GAME, msgArgs);
		try {
	        joinMessage.writeObject(mOos);
	        mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendJoinMessage Exception: " + e);
	    }
	}
	
	public boolean waitOK() {
		GoFmessage response = new GoFmessage();
		boolean okReceived = false;
		try {
            response.readObject(mOis);
        }
        catch (Exception e) {
          System.out.println("waitOK Exception: " + e);
        }
		
		if(response.getCommand() == GoFmessage.OK) {
        	okReceived = true;
        } else {
        	System.out.println("waitOK ERROR!");
        }
		
		return okReceived;
	}
	
	public boolean waitPlayAccepted() {
		GoFmessage response = new GoFmessage();
		boolean okReceived = false;
		try {
	        /* wait for response */
            response.readObject(mOis);
        }
        catch (Exception e) {
          System.out.println("waitPlayAccepted Exception: " + e);
        }
		
		if(response.getCommand() == GoFmessage.PLAY_ACCEPTED) {
			mHand = (ArrayList<GoFcard>)response.getArgs().get(GoFmessage.CARDS);
			mHand = GoFcombination.sortCards(mHand);
			GoFcombination.displayCardList(mHand);
        	okReceived = true;
        } else {
        	System.out.println("Wrong play!");
        }
		
		return okReceived;
	}
	
	public boolean waitForGameStarting() {
		GoFmessage gameStartMessage = new GoFmessage();
		boolean ok = false;
		try {
	        /* wait for response */
			gameStartMessage.readObject(mOis);
        }
        catch (Exception e) {
          System.out.println("waitForGameStarting Exception: " + e);
        }
		
		if(gameStartMessage.getCommand() == GoFmessage.GAME_START) {
        	ok = true;
        } else {
        	System.out.println("GAME_START not received !!!!!!!");
        }
		
		return ok;
	}
	
	private void sendWorst() {
		GoFcard worstCard = null;
		//flush System input
		try {
			System.in.skip(System.in.available());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		System.out.println("Enter your worst card : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			System.out.println("sendWorst Exception: " + e);
		}
		
		if(line.length() > 0) {
			Pattern p = Pattern.compile("(\\d{1,2})([GYRM])");
			Matcher m = p.matcher(line);
			if (m.find()) {
				worstCard = new GoFcard(m.group(1), m.group(2));
				System.out.println("Read card :" + worstCard.toString());
			}
		}
		
		HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.CARD, worstCard);
        GoFmessage playMessage = new GoFmessage(GoFmessage.WORST_CARD, msgArgs);
		try {
			playMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("sendWorst Exception: " + e);
	    }
	}
	
	private boolean play() {	
		ArrayList<GoFcard> cards = readCards();
		
		HashMap<String, Object> msgArgs = new HashMap<String, Object>();
        msgArgs.put(GoFmessage.CARDS, cards);
        GoFmessage playMessage = new GoFmessage(GoFmessage.CARDS_PLAYED, msgArgs);
		try {
			playMessage.writeObject(mOos);
			mOos.flush();
		}
		catch (Exception e) {
	        System.out.println("play Exception: " + e);
	    }
		
		return waitPlayAccepted();
	}
	
	private ArrayList<GoFcard> readCards() {
		//flush System input
		try {
			System.in.skip(System.in.available());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		System.out.println("Enter cards (blank to pass) : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			System.out.println("readCards Exception: " + e);
		}
		
		ArrayList<GoFcard> cards = new ArrayList<GoFcard>();
		
		if(line.length() > 0) {
			String[] strCards = line.split(" ");
			for(String strc : strCards) {
				Pattern p = Pattern.compile("(\\d{1,2})([GYRM])");
				Matcher m = p.matcher(strc);
				if (m.find()) {
					GoFcard c = new GoFcard(m.group(1), m.group(2));
					cards.add(c);
				}
			}
		}
		return cards;
	}
	
	public static void main(String[] args) {
	    String login;
	    
	    if(args.length > 0) {
	    	login = args[0];
	    } else {
	    	System.out.println("Missing argument : login");
	    	return;
	    }
	    
	    GoFplayer player = new GoFplayer(login);
	    player.sendJoinMessage();
	    if(player.waitOK()) {
	    	System.out.println("Player " + login + " logged.");
	    	
		    if(player.waitForGameStarting()){
		    	System.out.println("Game starts!!!");
		    
			    while(!player.isGameOver()) {
			    	player.handleMessage();
			    }
		    }
	    }
	    
	    player.closeConnection();
	}
}
