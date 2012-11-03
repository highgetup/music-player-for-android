package com.xrlfy.musicplayer.it;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.provider.MediaStore;
import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;

public class MusicFile extends MyFile{

	private String title;
	private String album;
	private String artist;
	private int musicLength;
	private int musicYear;
	
	private int bitrate;
	private int channelNumber;
	private String encodingType;
	private int samplingRate;
	private boolean isLossless;
	private boolean isVbr;
	
	public MusicFile(Cursor cursor){
		super(getTrueString(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))));
		title = getTrueString(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
		album = getTrueString(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
		artist = getTrueString(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
		musicLength = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
		musicYear = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
	}
	
	public MusicFile() {
		super();
	}

	public MusicFile(File file) {
		super(file);
	}

	public MusicFile(String file) {
		super(file);
	}
	
	public void readStatic(){
		try {
			AudioFile af=AudioFileIO.read(new File(absolutePath));
			bitrate=af.getBitrate();
			channelNumber=af.getChannelNumber();
			encodingType=af.getEncodingType();
			samplingRate=af.getSamplingRate();
			isLossless=af.isLossless();
			isVbr=af.isVbr();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}

	@Override
	public int describeContents() {
		return super.describeContents();
	}

	@Override
	public String toString() {
		return "bitrate:"+bitrate+"\nchannelNumber:"+channelNumber+"\nsamplingRate:"+samplingRate+"\nisVbr:"+isVbr+"\nisLossless:"+isLossless+"\nencodingType:"+encodingType+"\n";
	}
	
	public static String getTrueString(String content){
		return content;
	}

	public String getTitle() {
		return title;
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}
	public int getMusicLength() {
		return musicLength;
	}

	public int getMusicYear() {
		return musicYear;
	}

	public int getBitrate() {
		return bitrate;
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	public boolean isLossless() {
		return isLossless;
	}

	public boolean isVbr() {
		return isVbr;
	}

	public void LoadFromCursor(Cursor cursor) {
		init(new File(cursor.getString(cursor.getColumnIndex("path"))));
		title = cursor.getString(cursor.getColumnIndex("title"));
		artist = cursor.getString(cursor.getColumnIndex("artist"));
		album = cursor.getString(cursor.getColumnIndex("album"));
		musicLength = cursor.getInt(cursor.getColumnIndex("musicLength"));
		musicYear = cursor.getInt(cursor.getColumnIndex("year"));
	}
	
	public ContentValues putToContentValues() {
		ContentValues values = new ContentValues();		
		values.put("title", title);
		values.put("artist", artist);
		values.put("album", album);
		values.put("path", absolutePath);
		values.put("musicLength", musicLength);
		values.put("year", musicYear);
		return values;
	}
	
	public static String getQuery() {
		return "SELECT title, artist, album, musicLength, year, path FROM MUSIC ORDER BY _id DESC";
	}

}
