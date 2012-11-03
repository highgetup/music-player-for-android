package com.xrlfy.musicplayer.it;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;

import com.xrlfy.musicplayer.util.DBHelper;

public class PlayList {
	
	private static HashMap<String, ArrayList<MusicFile>> playLists=new HashMap<String, ArrayList<MusicFile>>();
	
	private static PlayList instance;
	
	private PlayList(){
		super();
	}
	
	public static synchronized PlayList getInstance(){
		if(null==instance){
			instance=new PlayList();
		}
		if(null==playLists){
			playLists=new HashMap<String, ArrayList<MusicFile>>();
		}
		return instance;
	}
	
	public ArrayList<MusicFile> getPlayList(String dbName){
		if(null!=dbName){
			if(playLists.containsKey(dbName)){
				return playLists.get(dbName);
			}else{
				return read(dbName);
			}
		}
		return null;
	}
	
	public boolean save(String dbName,MusicFile musicFile){
		DBHelper dbh = new DBHelper(dbName);
		SQLiteDatabase db=dbh.getWritableDatabase();
		long result=db.insert("MUSIC", null, musicFile.putToContentValues());
		db.close();
		dbh.close();
		return result>-1;
	}
	
	public void save(String dbName,MusicFile[] mfs){
		DBHelper dbh = new DBHelper(dbName);
		SQLiteDatabase db=dbh.getWritableDatabase();
		for (int i = 0; i < mfs.length; i++) {
			db.insert("MUSIC", null, mfs[i].putToContentValues());
		}
		db.close();
		dbh.close();
	}
	
	public void delete(String dbName,MusicFile musicFile){
		DBHelper dbh = new DBHelper(dbName);
		SQLiteDatabase db=dbh.getWritableDatabase();
		db.delete("MUSIC", "path = ?", new String[]{musicFile.absolutePath});
		db.close();
		dbh.close();
	}
	
	public ArrayList<MusicFile> read(String dbName){
		ArrayList<MusicFile> list=new ArrayList<MusicFile>();
		DBHelper dbh = new DBHelper(dbName);
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			db=dbh.getReadableDatabase();
			cursor=db.rawQuery(MusicFile.getQuery(), null);
		} catch (Exception e) {
			e.printStackTrace();
			dbh.close();
			if(null!=db){
				db.close();
			}
			return list;
		}
		
		MusicFile mf;
		while (cursor.moveToNext()) {
			mf=new MusicFile();
			mf.LoadFromCursor(cursor);
			list.add(mf);
		}
		db.close();
		dbh.close();
		playLists.put(dbName, list);
		return list;
	}
	
	public void update(String dbName,MusicFile[] mfs){
		if(null==mfs){
			return;
		}
		DBHelper dbh =new DBHelper(dbName);
		SQLiteDatabase db=dbh.getWritableDatabase();
		db.execSQL("DELETE FROM MUSIC");
		for (int i = 0; i < mfs.length; i++) {
			db.insert("MUSIC", null, mfs[i].putToContentValues());
		}
		db.close();
		dbh.close();
	}
	
	public void removeRepeat(String dbName){
		DBHelper dbh =new DBHelper(dbName);
		SQLiteDatabase db=dbh.getWritableDatabase();
		db.execSQL("delete from MUSIC m where path not in(select path from MUSIC where path = m.path);");
		db.close();
		dbh.close();
	}
	
	public static void clearData(){
		if(null!=playLists){
			playLists.clear();
			playLists=null;
		}
	}
	
	public MusicFile[] getAllMusic(Context context){
		if(playLists.containsKey("jobernowl_music_play_all_music")){
			ArrayList<MusicFile> mfs=playLists.get("jobernowl_music_play_all_music");
			return mfs.toArray(new MusicFile[mfs.size()]);
		}
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
						MediaStore.Audio.Media.TITLE,
	                    MediaStore.Audio.Media.ARTIST,
	                    MediaStore.Audio.Media.ALBUM,
	                    MediaStore.Audio.Media.YEAR,
	                    MediaStore.Audio.Media.DURATION,
	                    MediaStore.Audio.Media.DATA }, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(null==cursor){
			return null;
		}
		int count = cursor.getCount();
		ArrayList<MusicFile> list=new ArrayList<MusicFile>(count);
		while (cursor.moveToNext()) {
			list.add(new MusicFile(cursor));
		}
		playLists.put("jobernowl_music_play_all_music", list);
		return list.toArray(new MusicFile[count]);
	}
}
