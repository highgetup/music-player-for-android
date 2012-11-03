package com.xrlfy.musicplayer.service;

import com.xrlfy.musicplayer.MusicPlayerActivity;
import com.xrlfy.musicplayer.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

public class MusicPlayerWidgetProvider extends AppWidgetProvider{
	
	private static RemoteViews rvs;
	private static String title;
	private static String artist;
	private static boolean isPlaying;
	private static int[] ids;
	private static AppWidgetManager widgetManager;
	
	private static MusicPlayerWidgetProvider widgetProvider;
	static final ComponentName THIS_APPWIDGET = new ComponentName("com.xrlfy.service", "com.xrlfy.service.MusicPlayerWidgetProvider");
	
	static synchronized MusicPlayerWidgetProvider getInstance(){
		if(null==widgetProvider){
			widgetProvider=new MusicPlayerWidgetProvider();
		}
		return widgetProvider;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		ids=appWidgetIds;
		widgetManager=appWidgetManager;
		defaultWidget(context);
		Intent intent=new Intent("com.xrlfy.musicplayer.widget");
		intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		context.sendBroadcast(intent);
	}
	
	private void defaultWidget(Context context){
		initSharedPreferences(context);
		pushUpdate(context);
		linkClick(context);
	}

	private void linkClick(Context context){
		Intent service1=new Intent(context, MusicPlayerService.class);
		service1.setAction("next");
		service1.putExtra("cmd", 598);
		PendingIntent pi1=PendingIntent.getService(context, 0, service1, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Intent service2=new Intent(context, MusicPlayerService.class);
		service2.setAction("state");
		service2.putExtra("cmd", 589);
		PendingIntent pi2=PendingIntent.getService(context, 1, service2, PendingIntent.FLAG_UPDATE_CURRENT);
		rvs.setOnClickPendingIntent(R.id.next, pi1);
		rvs.setOnClickPendingIntent(R.id.playOrPause, pi2);
		
		Intent intent=new Intent(context, MusicPlayerActivity.class);
		PendingIntent p=PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rvs.setOnClickPendingIntent(R.id.layout, p);
	}
	
	private void pushUpdate(Context context) {
		if(null==rvs){
			rvs=new RemoteViews(context.getPackageName(), R.layout.widget);
			linkClick(context);
		}
		if(null==title){
			rvs.setTextViewText(R.id.title, context.getString(R.string.null_playlist));
			rvs.setViewVisibility(R.id.artist, View.GONE);
		}else{
			rvs.setViewVisibility(R.id.artist, View.VISIBLE);
			rvs.setTextViewText(R.id.title, title);
			rvs.setTextViewText(R.id.artist, artist);
		}
		String text;
		if(isPlaying){
			text=context.getString(R.string.pause);
		}else{
			text=context.getString(R.string.play);
		}
		rvs.setCharSequence(R.id.playOrPause, "setText", text);
        if (ids != null) {
        	widgetManager.updateAppWidget(ids, rvs);
        } else {
        	if(null==widgetManager){
    			widgetManager=AppWidgetManager.getInstance(context);
    		}
    		widgetManager.updateAppWidget(new ComponentName(context, getClass()), rvs);
        }
    }
	
	private void initSharedPreferences(Context context){
		SharedPreferences sps=context.getSharedPreferences("music_player", Context.MODE_PRIVATE);
		title=sps.getString("musicTitle", null);
		artist=sps.getString("artist", null);
	}
	
	private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MusicPlayerWidgetProvider.class));
        return (null != appWidgetIds && appWidgetIds.length > 0);
    }
	
	void notifyChange(MusicPlayerService service) {
		if(hasInstances(service)){
			if(service.isPlaying()){
	        	isPlaying=true;
	        }else{
	        	isPlaying=false;
	        }
	        String[] info=service.getCurentPlayingMusicInfo();
	        title=info[0];
	        artist=info[1];
	        pushUpdate(service);
		}
    }
	
}
