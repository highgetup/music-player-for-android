package com.xrlfy.musicplayer.util;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xrlfy.musicplayer.R;
import com.xrlfy.musicplayer.it.MusicFile;
import com.xrlfy.musicplayer.it.PlayList;

import entagged.audioformats.AudioFileIO;
import entagged.audioformats.Tag;

public class MusicUtil {
	
	public static MusicFile[] readAllMusic(Context context){
		return PlayList.getInstance().getAllMusic(context);
	}
	
	public static MusicFile[] readMusicsFromArtist(Context context,String artistKey){
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
						MediaStore.Audio.Media.TITLE,
	                    MediaStore.Audio.Media.ARTIST,
	                    MediaStore.Audio.Media.ALBUM,
	                    MediaStore.Audio.Media.YEAR,
	                    MediaStore.Audio.Media.DURATION,
	                    MediaStore.Audio.Media.DATA }, MediaStore.Audio.Media.ARTIST_KEY+"=?", new String[]{artistKey},
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(null==cursor){
			return new MusicFile[0];
		}
		MusicFile[] mfs=new MusicFile[cursor.getCount()];
		int i=0;
		while (cursor.moveToNext()) {
			mfs[i]=new MusicFile(cursor);
			i++;
		}
		return mfs;
	}
	
	public static MusicFile[] readMusicsFromAlbum(Context context,String albumKey){
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
						MediaStore.Audio.Media.TITLE,
	                    MediaStore.Audio.Media.ARTIST,
	                    MediaStore.Audio.Media.ALBUM,
	                    MediaStore.Audio.Media.YEAR,
	                    MediaStore.Audio.Media.DURATION,
	                    MediaStore.Audio.Media.DATA },MediaStore.Audio.Media.ALBUM_KEY+"=?",new String[]{albumKey},
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(null==cursor){
			return new MusicFile[0];
		}
		MusicFile[] mfs=new MusicFile[cursor.getCount()];
		int i=0;
		while (cursor.moveToNext()) {
			mfs[i]=new MusicFile(cursor);
			i++;
		}
		return mfs;
	}
	
	public static String[] readPlayLists(){
		String folder=Environment.getExternalStorageDirectory().getPath()+"/.AndroidTool/DB";
		File f = new File(folder);
		if (f.exists() && f.isDirectory()) {
			return f.list();
		}
		return new String[0];
	}
	
	public static ArrayList<MusicFile> readPlayList(String playListName){
		if(new File(Environment.getExternalStorageDirectory().getPath()+"/.AndroidTool/DB/"+playListName).exists()){
			PlayList pl=PlayList.getInstance();
			return pl.read(playListName);
		}
		return new ArrayList<MusicFile>();
	}

	public static Bitmap getAlbumImage(Context context,String file) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
				new String[]{MediaStore.Audio.Media.ALBUM_ID}, 
				MediaStore.Audio.Media.DATA+"=?", new String[]{file},
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		
		if(null!=cursor && cursor.getCount()>0){
			cursor.moveToNext();
			int album_id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
			cursor.close();
			cursor = null;
			return getAlbumImage(context, album_id);
		}
		return null;
	}
	
	public static Bitmap getAlbumImage(Context context,int album_id) {
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				new String[] { MediaStore.Audio.Media.ALBUM_ART }, 
				MediaStore.Audio.Albums._ID+"=?",new String[]{""+album_id},
				MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		
		String album_art = null;
		if (null!=cursor && cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			cursor.moveToNext();
			album_art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ART));
		}
		cursor.close();
		cursor = null;
		if(null==album_art){
			return null;
		}
		BitmapFactory.Options opts=new BitmapFactory.Options();
		int density=context.getResources().getDisplayMetrics().densityDpi;
		opts.inDensity=density;
		opts.inTargetDensity=density;
		return BitmapFactory.decodeFile(album_art, opts);
	}
	
	public static String getTimeLongString(long time){
		int t=(int) (time/1000);
		int seconds=t%60;
		if(seconds>9){
			return t/60+":"+seconds;
		}else{
			return t/60+":0"+seconds;
		}
	}
	
	public static MusicFile[] getMusicFilesFromFolder(Context context,String folder){
		MusicFile[] mfs=PlayList.getInstance().getAllMusic(context);
		ArrayList<MusicFile> list=new ArrayList<MusicFile>();
		String parent;
		for (int i = 0; i < mfs.length; i++) {
			parent=mfs[i].getParent();
			if(folder.equals(parent)||("/mnt"+folder).equals(parent)){
				list.add(mfs[i]);
			}
		}
		return list.toArray(new MusicFile[list.size()]);
	}
	
	public static Tag readMusicTag(String file){
		try {
			return AudioFileIO.read(new File(file)).getTag();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void showPropertyDialog(Context context,MusicFile mf){
		mf.readStatic();
		LayoutInflater li=LayoutInflater.from(context);
		View view=li.inflate(R.layout.music_property, null);
		
		TextView length=(TextView) view.findViewById(R.id.musicLength);
		length.setText(MusicUtil.getTimeLongString(mf.getMusicLength()));
		
		TextView title=(TextView) view.findViewById(R.id.musicTitle);
		title.setText(mf.getTitle());
		
		TextView artist=(TextView) view.findViewById(R.id.musicArtist);
		artist.setText(mf.getArtist());
		
		TextView album=(TextView) view.findViewById(R.id.musicAlbum);
		album.setText(mf.getAlbum());
		
		TextView year=(TextView) view.findViewById(R.id.musicYear);
		int y=mf.getMusicYear();
		String strY = "";
		if(y<=0){
			strY = context.getString(R.string.unknow);
		}else {
			strY= ""+y;
		}
		year.setText(strY);
		
		TextView location=(TextView) view.findViewById(R.id.musicFileLocation);
		location.setText(mf.absolutePath);
		
		TextView size=(TextView) view.findViewById(R.id.musicFileSize);
		size.setText(FileUtil.getSizeString(mf.length));
		
		TextView encode=(TextView) view.findViewById(R.id.musicEncode);
		encode.setText(mf.getEncodingType());
		
		TextView chanel=(TextView) view.findViewById(R.id.musicChanel);
		chanel.setText(mf.getChannelNumber()+"");
		
		TextView frequency=(TextView) view.findViewById(R.id.musicFrequency);
		frequency.setText(mf.getSamplingRate()+" Hz");
		
		TextView bitrate=(TextView) view.findViewById(R.id.musicBitrate);
		String br=mf.getBitrate()+" Kbps";
		if(mf.isVbr()){
			br+=" (vbr)";
		}
		bitrate.setText(br);
		
		AlertDialog dialog=new AlertDialog.Builder(context).setPositiveButton(android.R.string.ok, null).create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	
	public static Bitmap createReflectedImage(Bitmap originalImage) {
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		final int reflectionGap = 4;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0, 0, paint);

		canvas.drawRect(0, height, width, height + reflectionGap, paint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, paint);

		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}
}
