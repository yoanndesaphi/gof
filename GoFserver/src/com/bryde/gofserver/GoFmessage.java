package com.bryde.gofserver;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import java.util.ArrayList;
import java.util.HashMap;

public class GoFmessage implements Serializable {
	private static final long serialVersionUID = 5056572645027074636L;
	private int mCommand;
	private HashMap<String, Object> mArgs;
	
	/*
	 * List of commands
	 */
	public static final int ERROR 				= -1;
	public static final int NONE 				= 0;
	public static final int OK 					= 1;
	public static final int REGISTER 			= 2;
	public static final int LOGIN	 			= 3;
	public static final int JOIN_NEW_GAME 		= 4;
	public static final int GAME_START	 		= 5;
	public static final int DISTRIBUTION 		= 6;
	public static final int PLAY		 		= 7;
	public static final int CARDS_PLAYED 		= 8;
	public static final int PLAY_ACCEPTED 		= 9;
	public static final int GAME_OVER			= 10;
	public static final int GAME_STATUS			= 11; // TODO
	public static final int GAME_PLAYERS		= 12;
	public static final int GIVE_WORST			= 13; // TODO
	public static final int GIVE_BEST			= 14;
	public static final int PLAYER_HAS_PLAYED	= 15;
	
	/*
	 * List of argument IDs 
	 */
	public static final String LOGNAME 	= "LOGNAME";
	public static final String HAND 	= "HAND";
	public static final String CARDS 	= "CARDS";
	public static final String WINNER	= "WINNER";
	public static final String STATE	= "STATE"; // TODO
	public static final String SCORES	= "SCORES"; // TODO
	public static final String PLAYERS	= "PLAYERS";
	
	public static final int STATE_NEW_PLAY	= 0;
	public static final int STATE_NEW_TURN	= 1;
	public static final int STATE_GAME_OVER	= 2;
	
	public GoFmessage() {
		setCommand(NONE);
		mArgs = new HashMap<String, Object>();
	}
	
	public GoFmessage(int command, HashMap<String, Object> args) {
		setCommand(command);
		setArgs(args);
	}

	public int getCommand() {
		return mCommand;
	}

	public void setCommand(int command) {
		this.mCommand = command;
	}

	public void setArgs(HashMap<String, Object> args) {
		this.mArgs = args;
	}
	
	public HashMap<String, Object> getArgs() {
		return this.mArgs;
	}
	
	public void writeObject(ObjectOutputStream o) throws IOException {
		o.writeInt(mCommand);
		switch(mCommand) {
			case JOIN_NEW_GAME:
			case PLAY:
				o.writeObject(mArgs.get(LOGNAME));
				break;
			case GAME_PLAYERS:
				for(String p : (ArrayList<String>)mArgs.get(PLAYERS)) {
					o.writeObject(p);
				}
				break;
			case DISTRIBUTION:
				for(GoFcard card : (ArrayList<GoFcard>)mArgs.get(HAND)) {
					card.writeObject(o);
				}
				break;
			case CARDS_PLAYED:
			case PLAY_ACCEPTED:
				o.writeInt(((ArrayList<GoFcard>)mArgs.get(CARDS)).size());
				for(GoFcard card : (ArrayList<GoFcard>)mArgs.get(CARDS)) {
					card.writeObject(o);
				}
				break;
			case PLAYER_HAS_PLAYED:
				o.writeObject(mArgs.get(LOGNAME));
				o.writeInt(((ArrayList<GoFcard>)mArgs.get(CARDS)).size());
				for(GoFcard card : (ArrayList<GoFcard>)mArgs.get(CARDS)) {
					card.writeObject(o);
				}
				break;
			case GAME_OVER:
				o.writeObject(mArgs.get(WINNER));
				break;
		}
	}
		  
	public void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException { 
		mCommand = o.readInt();
		switch(mCommand) {
			case JOIN_NEW_GAME:
			case PLAY:
				mArgs.put(LOGNAME, o.readObject());
				break;
			case GAME_PLAYERS:
				ArrayList<String> players = new ArrayList<String>(4);
				for(int i = 0; i < 4; i++)
					players.add((String)o.readObject());
				mArgs.put(PLAYERS, players);
				break;
			case DISTRIBUTION:
				ArrayList<GoFcard> hand = new ArrayList<GoFcard>(16);
				for(int i = 0; i < 16; i++) {
					GoFcard card = new GoFcard();
					card.readObject(o);
					hand.add(card);
				}
				mArgs.put(HAND, hand);
				break;
			case CARDS_PLAYED:
			case PLAY_ACCEPTED:
				int nb_cards = o.readInt();
				ArrayList<GoFcard> cards = new ArrayList<GoFcard>(nb_cards);
				for(int i = 0; i < nb_cards; i++) {
					GoFcard card = new GoFcard();
					card.readObject(o);
					cards.add(card);
				}
				mArgs.put(CARDS, cards);
				break;
			case PLAYER_HAS_PLAYED:
				mArgs.put(LOGNAME, o.readObject());
				int nb_cards2 = o.readInt();
				ArrayList<GoFcard> cards2 = new ArrayList<GoFcard>(nb_cards2);
				for(int i = 0; i < nb_cards2; i++) {
					GoFcard card = new GoFcard();
					card.readObject(o);
					cards2.add(card);
				}
				mArgs.put(CARDS, cards2);
				break;
			case GAME_OVER:
				mArgs.put(WINNER, o.readObject());
				break;
		}
	}
}
