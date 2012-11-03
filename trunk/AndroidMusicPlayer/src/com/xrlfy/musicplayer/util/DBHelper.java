package com.xrlfy.musicplayer.util;

import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends MySQLiteOpenHelper{

	public DBHelper(String name) {
		super(name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE MUSIC (_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,artist TEXT,album TEXT,musicLength INTEGER,year INTEGER,path TEXT UNIQUE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
