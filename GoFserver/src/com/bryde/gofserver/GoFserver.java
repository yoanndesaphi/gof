package com.bryde.gofserver;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;

public class GoFserver implements Runnable {
	private Socket mConnection;
	private static ArrayList<Game> mGameList;
	private ObjectOutputStream mOos;
	private ObjectInputStream mOis;
	  
	@Override
	public void run() {
		GoFmessage message = readMessage();
		
		switch(message.getCommand()) {
			case GoFmessage.REGISTER:
				//to be implemented
				break;
			case GoFmessage.LOGIN:
				//to be implemented
				break;
			case GoFmessage.JOIN_NEW_GAME:
				if(message.getArgs().containsKey(GoFmessage.LOGNAME)) {
					joinGame((String)message.getArgs().get(GoFmessage.LOGNAME));
				} else {
					responseError();
				}
				break;
		}
	}
	
	private GoFmessage readMessage() {
		GoFmessage message = new GoFmessage();
		
		try {
			message.readObject(mOis);
		}
		catch (Exception e) {
			System.out.println("readMessage Exception: " + e);
		}
		
		return message;
	}
	
	private void joinGame(String login) {
		Player p = new Player(login, mConnection, mOis, mOos);
		
		for (Game game : mGameList) {
			if(game.isWaiting()) {
				System.out.println("Adding player " + login + " to game " + game.getID());
				game.addPlayer(p);
				
				if(game.isReady()) {
					Thread thread = new Thread(game);
		        	thread.start();
				}
				
				return;
			}
		}
		
		//if no waiting game has been found
		Game game = new Game();
		game.addPlayer(p);
		mGameList.add(game);
	}
	
	private void responseError() {
		try {
			GoFmessage errorMessage = new GoFmessage(GoFmessage.ERROR, null);
			errorMessage.writeObject(mOos);
		}
		catch (Exception e) {
			System.out.println("responseError Exception: " + e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int port = 19999;
		
		mGameList = new ArrayList<Game>();
		
		try{
			ServerSocket serverSocket = new ServerSocket(port);
		    System.out.println("Gang of Four Server Initialized");
		    while (true) {
		    	System.out.println("Waiting for players...");
		    	Socket connection = serverSocket.accept();
		        Runnable runnable = new GoFserver(connection);
		        Thread thread = new Thread(runnable);
		        thread.start();
		    }
		}
		catch (Exception e) {}
	}

	GoFserver(Socket s) {
	  this.mConnection = s;
	  
	  try {
		mOos = new ObjectOutputStream(mConnection.getOutputStream());
		mOis = new ObjectInputStream(mConnection.getInputStream());
	  } catch(IOException e) {
		  System.out.println(e);
	  }
	}
	
}
