package com.hw.aizuanshi.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String DB_NAME = "game_info.db";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	public void onCreate(SQLiteDatabase db) {

	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Cursor query() {
		String sql = "select * from game";
		SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery(sql, null);
	}

	public void updateSate(int id) {
		String sql = "update game set exist=? where _id=?";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql, new Object[] { 1, id });
	}

}
