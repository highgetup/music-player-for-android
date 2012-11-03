package com.xrlfy.musicplayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xrlfy.musicplayer.service.IMusicPlayerService;
import com.xrlfy.musicplayer.service.MusicPlayerService;
import com.xrlfy.musicplayer.util.MusicUtil;

public class MusicPlayerActivity extends Activity implements OnClickListener{
	
	private final int MENU_ID_SLEEP = 0;
	private final int MENU_ID_MUSIC_STORE = 1;
	private final int MENU_ID_ADD_PLAYLIST = 2;
	private final int MENU_ID_CURRENT_PLAYLIST = 3;
	private final int MENU_ID_EXIT = 4;
	private final int MENU_ID_FOLDER = 5;
	
	private Button previous;
	private Button playOrPause;
	private Button next;
	private Button repeat;
	private Button random;
	private ImageView imageLabel;
	private SeekBar sb;
	private TextView infoNum;
	private TextView position;
	private TextView length;
	private TextView tvFileName;
	private TextView tvTitle;
	private TextView tvArtist;
	private TextView tvAlbum;
	
	private String mf;
	private boolean isExit = false;
	private Timer timer;
	private IMusicPlayerService musicPlayerService;
	private ServiceConnection connection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicPlayerService = IMusicPlayerService.Stub.asInterface(service);
			if(null != musicPlayerService){
				try {
					musicPlayerService.register(musicPlayer);
					timer.schedule(task, 0, 1000);
					if(null!=mf){
						musicPlayerService.playSpecifiedMusicByPath(mf);
						mf=null;
					}
					musicPlayerService.getInform();
					service.linkToDeath(bdeath,0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			if(null!=timer){
				timer.cancel();
				timer.purge();
				timer=null;
			}
		}
	};
	private void bindService(){
        Intent intent=new Intent(MusicPlayerActivity.this,MusicPlayerService.class);
        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    } 
	private IBinder.DeathRecipient bdeath = new IBinder.DeathRecipient() {
		public void binderDied() {
			if(!isExit){
				bindService();
			}
		}
	};
	private TimerTask task=new TimerTask() {
		@Override
		public void run() {
			try {
				if(null != musicPlayerService && musicPlayerService.isPlaying()){
					final int currentPosition = musicPlayerService.getgetCurrentPosition();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							sb.setProgress(currentPosition);
							position.setText(MusicUtil.getTimeLongString(currentPosition));
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private String getRepeatString(){
		try {
			switch(musicPlayerService.getRpeat()){
			case 1:{
				return getString(R.string.repeat_all);
			}
			case 2:{
				return getString(R.string.repeat_one);
			}
			default:{
				return getString(R.string.repeat_none);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return getString(R.string.repeat_one);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.music_player);
		
		previous=(Button) findViewById(R.id.previous);
		previous.setOnClickListener(this);
		
		playOrPause=(Button) findViewById(R.id.playOrPause);
		playOrPause.setOnClickListener(this);
		
		next=(Button) findViewById(R.id.next);
		next.setOnClickListener(this);
		
		repeat=(Button) findViewById(R.id.repeat);
		repeat.setOnClickListener(this);
		
		random=(Button) findViewById(R.id.random);
		random.setOnClickListener(this);
		
		imageLabel=(ImageView) findViewById(R.id.imageLabel);
		
		sb=(SeekBar) findViewById(R.id.seekBar);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser){
					if(null!=musicPlayerService){
						try {
							musicPlayerService.seek(progress);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		infoNum=(TextView) findViewById(R.id.infoNum);
		position=(TextView) findViewById(R.id.position);
		length=(TextView) findViewById(R.id.length);
		tvFileName=(TextView) findViewById(R.id.fileName);
		tvTitle=(TextView) findViewById(R.id.title);
		tvArtist=(TextView) findViewById(R.id.artist);
		tvAlbum=(TextView) findViewById(R.id.album);
		
		Intent intent=getIntent();
		mf=intent.getParcelableExtra("musicFile");
		if(null!=intent.getData()){
			mf=intent.getData().getPath();
		}
		intent.removeExtra("musicFile");
		intent.setData(null);
		timer = new Timer();
		bindService();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.random:{
			try {
				musicPlayerService.setRandom();
				repeat.setText(getRepeatString());
				random.setText(musicPlayerService.isRandom()?getString(R.string.random_open):getString(R.string.random_close));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.repeat:{
			try {
				musicPlayerService.setRepeat();
				repeat.setText(getRepeatString());
				random.setText(musicPlayerService.isRandom()?getString(R.string.random_open):getString(R.string.random_close));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.previous:{
			try {
				musicPlayerService.playPrevious();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.playOrPause:{
			try {
				musicPlayerService.playOrPause();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.next:{
			try {
				musicPlayerService.playNext();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		}
	}

	private void exit(){
		getSharedPreferences("music_player", Context.MODE_PRIVATE).edit().remove("time_up").commit();
		isExit=true;
		if(null!=timer){
			timer.cancel();
			timer.purge();
			timer=null;
		}
		if(null!=task){
			task.cancel();
			task=null;
		}
		try {
			unbindService(connection);
			connection = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent service=new Intent(this, MusicPlayerService.class);
		stopService(service);
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
//		try {
//			ActivityManager manager=(ActivityManager)getSystemService(ACTIVITY_SERVICE); 
//			Method method=ActivityManager.class.getMethod("killBackgroundProcesses", String.class);
//			method.setAccessible(true);
//			method.invoke(manager, getPackageName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ID_MUSIC_STORE, 0, R.string.music_store);
		menu.add(0, MENU_ID_ADD_PLAYLIST, 0, R.string.add_to_playlist);
		menu.add(0, MENU_ID_FOLDER, 0, R.string.local_folder);
		menu.add(0, MENU_ID_CURRENT_PLAYLIST, 0, R.string.current_palylist);
		menu.add(0, MENU_ID_SLEEP, 0, R.string.sleep_mode);
		menu.add(0, MENU_ID_EXIT, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case MENU_ID_EXIT:{
			exit();
			break;
		}
		case MENU_ID_MUSIC_STORE:{
			startActivity(new Intent(MusicPlayerActivity.this, MusicExplorerActivity.class));
			break;
		}
		case MENU_ID_CURRENT_PLAYLIST:{
			try {
				
				String[] playList=musicPlayerService.getCurrentPlayList();
				int playListMode = Integer.parseInt(playList[4]);
				Intent i=new Intent(MusicPlayerActivity.this, MusicSubExplorerActivity.class);
				if(playListMode == MusicPlayerService.MODE_FOLDER_LIST){
					i.putExtra("folder", playList[5]);
				}else{
					if(null!=playList[0]){
						i.putExtra("playListName", playList[0]);
					}else if(null!=playList[1]){
						i.putExtra("album_key", playList[1]);
					}else if(null!=playList[2]){
						i.putExtra("artist_key", playList[2]);
					}
				}
				i.putExtra("pos", Integer.parseInt(playList[3]));
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		case MENU_ID_FOLDER:{
			startActivity(new Intent(MusicPlayerActivity.this, MusicLocalExplorerActivity.class));
			break;
		}
		case MENU_ID_SLEEP:{
			long time=getSharedPreferences("music_player", Context.MODE_PRIVATE).getLong("time_up",0);
			if(time>System.currentTimeMillis()){
				getSharedPreferences("music_player", Context.MODE_PRIVATE).edit().remove("time_up").commit();
				AlarmManager am=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent service=new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
				service.putExtra("cmd", 59438);
				PendingIntent pi=PendingIntent.getService(MusicPlayerActivity.this, 0, service, PendingIntent.FLAG_CANCEL_CURRENT);
				am.cancel(pi);
				Toast.makeText(getApplicationContext(), R.string.cancle_alarm_success, Toast.LENGTH_LONG).show();
				return super.onOptionsItemSelected(item);
			}
			LinearLayout layout=new LinearLayout(MusicPlayerActivity.this);
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			layout.setOrientation(LinearLayout.HORIZONTAL);
			final EditText et=new EditText(MusicPlayerActivity.this);
			LayoutParams lp=new LayoutParams(0, LayoutParams.WRAP_CONTENT);
			lp.weight=2f;
			et.setLayoutParams(lp);
			et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			layout.addView(et);
			TextView tv=new TextView(MusicPlayerActivity.this);
			tv.setTextSize(16);
			tv.setTextColor(0xFFFFFFFF);
			tv.setText("("+getString(R.string.minute)+")");
			lp=new LayoutParams(0, LayoutParams.WRAP_CONTENT);
			lp.weight=1f;
			tv.setLayoutParams(lp);
			layout.addView(tv);
			new AlertDialog.Builder(MusicPlayerActivity.this).setView(layout)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						String text=et.getText().toString();
						long time=Integer.parseInt(text.trim())*60*1000;
						Intent service=new Intent(MusicPlayerActivity.this, MusicPlayerService.class);
						service.putExtra("cmd", 888);
						service.putExtra("future", time);
						startService(service);
						SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
						Toast.makeText(MusicPlayerActivity.this, String.format(getString(R.string.time_up_msg), sdf.format(System.currentTimeMillis()+time)), Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).setNegativeButton(android.R.string.no, null).show();
			break;
		}
		case MENU_ID_ADD_PLAYLIST:{
			String[] pls=MusicUtil.readPlayLists();
			String currentPlayList = null;
			try {
				currentPlayList=musicPlayerService.getCurrentPlayList()[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<String> list=new ArrayList<String>();
			for(int i=0;i<pls.length;i++){
				if(!pls[i].equals(currentPlayList)){
					list.add(pls[i]);
				}
			}
			list.add(getString(R.string.new_playlist));
			final String[] items=list.toArray(new String[list.size()]);
			new AlertDialog.Builder(this).setItems(items, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which==items.length-1){
						final EditText et=new EditText(MusicPlayerActivity.this);
						new AlertDialog.Builder(MusicPlayerActivity.this).setView(et)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String name=et.getText().toString();
								try {
									musicPlayerService.addCurrentToPlaylist(name);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).setNegativeButton(android.R.string.no, null).create().show();
					}else{
						try {
							musicPlayerService.addCurrentToPlaylist(items[which]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).create().show();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setAlbumPic(String file){
		Bitmap bitmap=MusicUtil.getAlbumImage(this, file);
		if(null!=bitmap){
			imageLabel.setBackgroundDrawable(null);
		}else{
			imageLabel.setBackgroundResource(R.drawable.music_lable);
		}
		
		imageLabel.setImageBitmap(bitmap);
	}
	
	private final IMusicPlayer musicPlayer=new IMusicPlayer.Stub(){
		@Override
		public void onMusicInfoChanged(final String title, final String artist,
				final String album, final String fileName) throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvFileName.setText(fileName);
					tvTitle.setText(title);
					tvArtist.setText(artist);
					tvAlbum.setText(album);
					try {
						repeat.setText(getRepeatString());
						random.setText(musicPlayerService.isRandom()?getString(R.string.random_open):getString(R.string.random_close));
						sb.setMax(musicPlayerService.getDuration());
						int currentPosition = musicPlayerService.getgetCurrentPosition();
						sb.setProgress(currentPosition);
						position.setText(MusicUtil.getTimeLongString(currentPosition));
						length.setText(MusicUtil.getTimeLongString(musicPlayerService.getDuration()));
						infoNum.setText(musicPlayerService.getCurrentPlayIndex()+"/"+musicPlayerService.getPlayMusicCount());
						setAlbumPic(musicPlayerService.getCurrentMusicFile());
						if(musicPlayerService.isPlaying()){
							playOrPause.setText(R.string.pause);
						}else{
							playOrPause.setText(R.string.play);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		@Override
		public void exitMusicPlayer() throws RemoteException {
			exit();
		}
	};
	@Override
	protected void onDestroy() {
		try {
			if(null!=connection){
				unbindService(connection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			super.onDestroy();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			musicPlayerService.getInform();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		try {
			musicPlayerService.save();
			musicPlayerService.unRegister(musicPlayer);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		isExit=true;
		if(null!=timer){
			timer.cancel();
			timer.purge();
			timer=null;
		}
		if(null!=task){
			task.cancel();
			task=null;
		}
		try {
			unbindService(connection);
			connection = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
/*		case KeyEvent.KEYCODE_DPAD_DOWN:{
			int current=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			current--;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, AudioManager.FLAG_SHOW_UI);
			return true;
		}
		case KeyEvent.KEYCODE_DPAD_UP:{
			int current=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			current++;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, AudioManager.FLAG_SHOW_UI);
			return true;
		}*/
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:{
			try {
				musicPlayerService.playPrevious();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		case KeyEvent.KEYCODE_MEDIA_NEXT:{
			try {
				musicPlayerService.playNext();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		case KeyEvent.KEYCODE_SPACE:{
			try {
				musicPlayerService.playOrPause();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		}
		return super.onKeyDown(keyCode, event);
	}

}
