package com.xrlfy.musicplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MusicBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		
		if(Intent.ACTION_MEDIA_BUTTON.equals(action)){
			KeyEvent keyEvent = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			int keyCode = keyEvent.getKeyCode();
			int keyAction = keyEvent.getAction();
			switch(keyCode){
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			case KeyEvent.KEYCODE_HEADSETHOOK:{
				if(keyAction==KeyEvent.ACTION_UP){
					context.sendBroadcast(new Intent("com.xrlfy.musicplayer.mediabutton"));
				}
				break;
			}
			}
		}
	}

}
