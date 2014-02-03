package com.bryde.gofserver;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
	public static final int GAME_STATUS			= 10;
	public static final int GAME_PLAYERS		= 11;
	public static final int CHOOSE_WORST		= 12;
	public static final int WORST_CARD			= 13;
	public static final int GIVE_WORST			= 14;
	public static final int GIVE_BEST			= 15;
	public static final int PLAYER_HAS_PLAYED	= 16;
	
	/*
	 * List of argument IDs 
	 */
	public static final String LOGNAME 	= "LOGNAME";
	public static final String HAND 	= "HAND";
	public static final String CARDS 	= "CARDS";
	public static final String STATE	= "STATE";
	public static final String SCORES	= "SCORES";
	public static final String PLAYERS	= "PLAYERS";
	public static final String SENDER	= "SENDER";
	public static final String RECEIVER	= "RECEIVER";
	public static final String CARD		= "CARD";
	
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
			case GAME_STATUS:
				o.writeInt(((Integer)mArgs.get(STATE)).intValue());
				for(Entry<Player, Integer> p : ((HashMap<Player, Integer>)mArgs.get(SCORES)).entrySet()) {
					o.writeObject(p.getKey().getLogin());
					o.writeObject(p.getValue());
				}
				break;
			case GIVE_BEST:
			case GIVE_WORST:
				o.writeObject(mArgs.get(SENDER));
				o.writeObject(mArgs.get(RECEIVER));
				((GoFcard)mArgs.get(CARD)).writeObject(o);
				break;
			case WORST_CARD:
				((GoFcard)mArgs.get(CARD)).writeObject(o);
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
			case GAME_STATUS:
				mArgs.put(STATE, Integer.valueOf(o.readInt()));
				HashMap<String, Integer> scores = new HashMap<String, Integer>(4);
				for(int i = 0; i < 4; i++)
					scores.put((String)o.readObject(), (Integer)o.readObject());
				mArgs.put(SCORES, scores);
				break;
			case GIVE_BEST:
			case GIVE_WORST:
				mArgs.put(SENDER, o.readObject());
				mArgs.put(RECEIVER, o.readObject());
				GoFcard card = new GoFcard();
				card.readObject(o);
				mArgs.put(CARD, card);
				break;
			case WORST_CARD:
				GoFcard card1 = new GoFcard();
				card1.readObject(o);
				mArgs.put(CARD, card1);
				break;
		}
	}
}
