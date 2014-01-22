package com.bryde.gofserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GoFcard implements Serializable{
	private static final long serialVersionUID = -5138548232820147477L;
	public static final int COLOR_GREEN = 0;
	public static final int COLOR_YELLOW = 1;
	public static final int COLOR_RED = 2;
	public static final int COLOR_MULTI = 3;
	
	public static final int VALUE_MIN = 1;
	public static final int VALUE_MAX = 10;
	public static final int VALUE_PHOENIX = 11;
	public static final int VALUE_DRAGON = 12;
	
	private int mValue;
	private int mColor;
	
	public GoFcard() {}
	
	public GoFcard(int value, int color) {
		mValue = value;
		mColor = color;
	}
	
	public GoFcard(String value, String color) {
		mValue = Integer.valueOf(value).intValue();
		if(color.contentEquals("Y"))
			mColor = COLOR_YELLOW;
		else if(color.contentEquals("R"))
			mColor = COLOR_RED;
		else if(color.contentEquals("M"))
			mColor = COLOR_MULTI;
		else
			mColor = COLOR_GREEN;
	}
	
	public boolean isStronger(GoFcard card) {
		if(mValue > card.getValue()) {
			return true;
		} else if(mValue == card.getValue()) {
			if(mColor > card.getColor()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public int getValue() {
		return mValue;
	}
	
	public int getColor() {
		return mColor;
	}
	
	private final String getColorString() {
		switch(mColor) {
			case COLOR_GREEN:
				return "G";
			case COLOR_YELLOW:
				return "Y";
			case COLOR_RED:
				return "R";
			case COLOR_MULTI:
				return "M";
		}
		return "";
	}
	
	public String toString() {
		return String.valueOf(mValue) + getColorString();
	}
	
	public void writeObject(ObjectOutputStream o) throws IOException {  
		o.writeInt(mValue);
		o.writeInt(mColor);
	}
		  
	public void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {  
		mValue = o.readInt();
		mColor = o.readInt();
	}
	
	public boolean equals(GoFcard c) {
		return c.getValue() == mValue && c.getColor() == mColor;
	}
}
