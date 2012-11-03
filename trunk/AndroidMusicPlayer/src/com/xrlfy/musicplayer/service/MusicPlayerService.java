package com.xrlfy.musicplayer.service;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.xrlfy.musicplayer.IMusicPlayer;
import com.xrlfy.musicplayer.MusicPlayerActivity;
import com.xrlfy.musicplayer.R;
import com.xrlfy.musicplayer.it.MusicFile;
import com.xrlfy.musicplayer.it.PlayList;
import com.xrlfy.musicplayer.util.MusicUtil;

public class MusicPlayerService extends Service {

	private final int NOTIFICATION_ID = 59438;
	public static final int CMD_PLAY_PAUSE = 0;
	public static final int CMD_NEXT = 3;
	public static final int CMD_PREVIOUS = 4;
	public static final int CMD_RANDOM = 5;
	public static final int CMD_REPEAT = 6;
	public static final int CMD_SEEK = 7;
	public static final int MODE_PLAY_LIST = 8;
	public static final int MODE_FOLDER_LIST = 9;
	private int repeatIndex = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	private MediaPlayer mediaPlayer;
	private MusicFile[] playList;
	private int pos = 0;
	private boolean isRandom = false;
	private boolean isPause;
	private boolean isCreate = false;
	private boolean isPlugged = true;
	private boolean isCalling = false;
	private String playListName;
	private String albumKey;
	private String artistKey;
	private int position;
	private Bitmap icon;
	private int playListMode;
	private String folder;
	private Notification n;
	
	private MusicPlayerWidgetProvider widgetProvider = MusicPlayerWidgetProvider.getInstance();

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_HEADSET_PLUG);
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction(Intent.ACTION_SHUTDOWN);
		filter.addAction("com.xrlfy.musicplayer.widget");
		filter.addAction("com.xrlfy.musicplayer.mediabutton");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(br, filter);
		registerReceiver();
		icon = BitmapFactory.decodeResource(getResources(), R.drawable.zaoan);
		mediaPlayer = new MediaPlayer();
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if (isRandom) {
							pos = (int) (Math.random() * playList.length);
							updatePlayMusic();
						} else {
							if (repeatIndex != 2) {
								pos++;
							}
							if (pos == playList.length && repeatIndex == 0) {
								mediaPlayer.stop();
								pos = playList.length - 1;
								handler.post(new Runnable() {
									@Override
									public void run() {
										inform();
									}
								});
								return;
							}
							updatePlayMusic();
						}
					}
				});
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				isPause = false;
				mediaPlayer.start();
				if (isCreate) {
					mediaPlayer.pause();
					isPause = true;
					isCreate = false;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						inform();
					}
				});
			}
		});
		isCreate = true;
		initSharedPreferences();
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		String text = "";
		if (hour >= 3 && hour < 12) {
			text = getString(R.string.good_morning);
		} else if (hour >= 12 && hour < 13) {
			text = getString(R.string.good_noon);
		} else if (hour >= 13 && hour < 19) {
			text = getString(R.string.good_afternoon);
		} else if (hour >= 19 && hour < 24) {
			text = getString(R.string.good_evening);
		} else {
			text = getString(R.string.good_night);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 1);
		n = new Notification(R.drawable.zaoan, text, calendar.getTimeInMillis());
		notification();
	}

	private void updatePlayState() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		} else {
			if (isPause) {
				mediaPlayer.start();
				isPause = false;
			} else {
				updatePlayMusic();
				return;
			}
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				inform();
				saveSharedPreferences();
			}
		});
	}
	
	private void updatePlayMusic() {
		if (mediaPlayer.isPlaying() || isPause) {
			mediaPlayer.stop();
		}
		mediaPlayer.reset();
		if (pos >= playList.length) {
			pos = 0;
		}
		try {
			String file = playList[pos].absolutePath;
			if (!new File(file).exists()) {
				if (playListMode == MODE_PLAY_LIST || null == folder
						|| !new File(folder).exists()) {
					PlayList.getInstance().delete(playListName, playList[pos]);
					readPlayList();
					folder = null;
				} else {
					playList = MusicUtil.getMusicFilesFromFolder(
							MusicPlayerService.this, folder);
					playListName = null;
				}
				updatePlayMusic();
				return;
			}
			mediaPlayer.setDataSource(file);
			mediaPlayer.prepare();
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}

	private void inform() {
		try {
			int n = mMusicPlayer.beginBroadcast();
			MusicFile mf = playList[pos];
			String path = mf.absolutePath.substring(mf.absolutePath
					.lastIndexOf('/') + 1);
			for (int i = 0; i < n; i++) {
				mMusicPlayer.getBroadcastItem(i).onMusicInfoChanged(
						mf.getTitle(), mf.getArtist(), mf.getAlbum(), path);
			}
			mMusicPlayer.finishBroadcast();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			updateWidget();
			saveSharedPreferences();
		}
	}

	private void updateWidget() {
		widgetProvider.notifyChange(this);
		updateNotification();
	}

	private void next() {
		if (isCalling) {
			return;
		}
		if (isRandom) {
			pos = (int) (Math.random() * playList.length);
		} else if (repeatIndex != 2) {
			pos++;
			if (pos == playList.length) {
				pos = 0;
			}
		}
		updatePlayMusic();
	}
	
	boolean isPlaying(){
		if (null == mediaPlayer) {
			return false;
		}
		return mediaPlayer.isPlaying();
	}

	private final IMusicPlayerService.Stub binder = new IMusicPlayerService.Stub() {
		@Override
		public void playPrevious() throws RemoteException {
			if (isCalling) {
				return;
			}
			if (isRandom) {
				pos = (int) (Math.random() * playList.length);
			} else if (repeatIndex != 2) {
				pos--;
				if (pos == -1) {
					pos = playList.length - 1;
				}
			}
			updatePlayMusic();
		}

		@Override
		public void playNext() throws RemoteException {
			next();
		}

		@Override
		public void playOrPause() throws RemoteException {
			updatePlayState();
		}

		@Override
		public void seek(int pos) throws RemoteException {
			if (null == mediaPlayer) {
				return;
			}
			mediaPlayer.seekTo(pos);
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return MusicPlayerService.this.isPlaying();
		}

		@Override
		public boolean isPause() throws RemoteException {
			if (null == mediaPlayer) {
				return false;
			}
			return isPause && !mediaPlayer.isPlaying();
		}

		@Override
		public int getgetCurrentPosition() throws RemoteException {
			if (null == mediaPlayer) {
				return 0;
			}
			return mediaPlayer.getCurrentPosition();
		}

		@Override
		public int getDuration() throws RemoteException {
			if (null == mediaPlayer) {
				return 0;
			}
			return mediaPlayer.getDuration();
		}

		@Override
		public void setRandom() throws RemoteException {
			isRandom = !isRandom;
			repeatIndex = 1;
		}

		@Override
		public void setRepeat() throws RemoteException {
			repeatIndex++;
			if (repeatIndex == 3) {
				repeatIndex = 0;
			}
			if (repeatIndex != 1) {
				isRandom = false;
			}
		}

		@Override
		public int getCurrentPlayIndex() throws RemoteException {
			return pos + 1;
		}

		@Override
		public int getPlayMusicCount() throws RemoteException {
			return null != playList ? playList.length : 0;
		}

		@Override
		public void playSpecifiedMusicByPLayListIndex(final int index,
				final String playList) throws RemoteException {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (!playList.equals(playListName)) {
						playListName = playList;
						readPlayList();
					}
					pos = index;
					updatePlayMusic();
				}
			});
		}

		@Override
		public void playSpecifiedMusicByPath(final String fileName)
				throws RemoteException {
			handler.post(new Runnable() {
				@Override
				public void run() {
					playListName = "";
					pos = 0;
					String file = fileName;
					folder = new File(file).getParent();
					playList = MusicUtil.getMusicFilesFromFolder(
							MusicPlayerService.this, folder);
					for (int i = 0; i < playList.length; i++) {
						if (file.equals(playList[i].absolutePath)
								|| ("/mnt" + file)
										.equals(playList[i].absolutePath)) {
							pos = i;
							break;
						}
					}
					playListMode = MODE_FOLDER_LIST;
					updatePlayMusic();
				}
			});
		}

		@Override
		public int getRpeat() throws RemoteException {
			return repeatIndex;
		}

		@Override
		public boolean isRandom() throws RemoteException {
			return isRandom;
		}

		@Override
		public void register(IMusicPlayer musicPlayer) throws RemoteException {
			if (null != musicPlayer) {
				mMusicPlayer.register(musicPlayer);
				registerReceiver();
			}
		}

		@Override
		public void unRegister(IMusicPlayer musicPlayer) throws RemoteException {
			mMusicPlayer.unregister(musicPlayer);
		}

		@Override
		public void getInform() throws RemoteException {
			handler.post(new Runnable() {
				@Override
				public void run() {
					inform();
				}
			});
		}

		@Override
		public void save() throws RemoteException {
			saveSharedPreferences();
		}

		@Override
		public String[] getCurrentPlayList() throws RemoteException {
			String[] pl = new String[6];
			pl[0] = playListName;
			pl[1] = albumKey;
			pl[2] = artistKey;
			pl[3] = "" + pos;
			pl[4] = "" + playListMode;
			if (playListMode == MODE_FOLDER_LIST) {
				pl[0] = folder;
			}
			pl[5] = folder;
			return pl;
		}

		@Override
		public void exit() throws RemoteException {
			exitMusicPlayer();
		}

		@Override
		public String getCurrentMusicFile() throws RemoteException {
			return playList[pos].absolutePath;
		}

		@Override
		public void addCurrentToPlaylist(String playListName)
				throws RemoteException {
			PlayList.getInstance().save(playListName, playList[pos]);
		}

		@Override
		public String[] getCurentPlayingMusicInfo() throws RemoteException {
			return MusicPlayerService.this.getCurentPlayingMusicInfo();
		}
	};
	
	String[] getCurentPlayingMusicInfo(){
		String[] info = new String[2];
		try {
			info[0] = playList[pos].getTitle();
			info[1] = playList[pos].getArtist();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	private void exitMusicPlayer() {
		saveSharedPreferences();
		try {
			getSharedPreferences("music_player", Context.MODE_PRIVATE).edit().remove("time_up").commit();
			int n = mMusicPlayer.beginBroadcast();
			for (int i = 0; i < n; i++) {
				mMusicPlayer.getBroadcastItem(i).exitMusicPlayer();
			}
			mMusicPlayer.finishBroadcast();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopSelf();
	}

	private final RemoteCallbackList<IMusicPlayer> mMusicPlayer = new RemoteCallbackList<IMusicPlayer>();

	@Override
	public void onDestroy() {
		unRegisterReceiver();
		try {
			if (null != mediaPlayer) {
				mediaPlayer.stop();
				updateWidget();
				mediaPlayer.release();
			}
			unregisterReceiver(br);

			stopForeground(true);
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent service = new Intent(this, MusicPlayerService.class);
			service.putExtra("cmd", 59438);
			PendingIntent pi = PendingIntent.getService(this, 0, service,
					PendingIntent.FLAG_CANCEL_CURRENT);
			am.cancel(pi);
			PlayList.clearData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	private void saveSharedPreferences() {
		Editor editor = getSharedPreferences("music_player", Context.MODE_PRIVATE).edit();
		try {
			editor.putInt("index", pos);
			editor.putBoolean("isRandom", isRandom);
			editor.putInt("repeat", repeatIndex);
			editor.putInt("position", mediaPlayer.getCurrentPosition());
			editor.putString("playList", playListName);
			editor.putString("albumKey", albumKey);
			editor.putString("artistKey", artistKey);
			editor.putInt("playListMode", playListMode);
			editor.putString("folder", folder);
			editor.putString("musicTitle", playList[pos].getTitle());
			editor.putString("artist", playList[pos].getArtist());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			editor.commit();
			widgetProvider.notifyChange(MusicPlayerService.this);
		}
	}

	private void initSharedPreferences() {
		SharedPreferences sps = getSharedPreferences("music_player", Context.MODE_PRIVATE);
		playListMode = sps.getInt("playListMode", MODE_PLAY_LIST);
		folder = sps.getString("folder", null);
		pos = sps.getInt("index", 0);
		position = sps.getInt("position", 0);
		if (playListMode == MODE_PLAY_LIST || folder == null
				|| !new File(folder).exists()) {
			playListName = sps.getString("playList", null);
			albumKey = sps.getString("albumKey", null);
			artistKey = sps.getString("artistKey", null);
			if (playListName == null) {
				if (null != albumKey) {
					playList = MusicUtil.readMusicsFromAlbum(this, albumKey);
				} else if (null != artistKey) {
					playList = MusicUtil.readMusicsFromArtist(this, artistKey);
				} else {
					playListName = "";
					playList = MusicUtil.readAllMusic(this);
					pos = 0;
					position = 0;
				}
			} else if (playListName.length() == 0) {
				playList = MusicUtil.readAllMusic(this);
			} else {
				readPlayList();
			}
		} else {
			playList = MusicUtil.getMusicFilesFromFolder(
					MusicPlayerService.this, folder);
		}

		isRandom = sps.getBoolean("isRandom", false);
		repeatIndex = sps.getInt("repeat", 0);

		if (pos >= playList.length || pos < 0) {
			pos = 0;
			position = 0;
		}
		try {
			mediaPlayer.setDataSource(playList[pos].absolutePath);
			mediaPlayer.prepare();
			if (position > 0) {
				mediaPlayer.seekTo(position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readPlayList() {
		playListMode = MODE_PLAY_LIST;
		ArrayList<MusicFile> list = MusicUtil.readPlayList(playListName);
		if (null != list) {
			playList = list.toArray(new MusicFile[list.size()]);
		}
		if (null == playList || playList.length == 0) {
			playList = MusicUtil.readAllMusic(this);
			playListName = "";
			artistKey = null;
			albumKey = null;
			pos = 0;
			position = 0;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int cmd = -1;
		try {
			cmd = intent.getIntExtra("cmd", -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (cmd) {
		case 59438: {
			handler.post(new Runnable() {
				@Override
				public void run() {
					try {
						exitMusicPlayer();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			break;
		}
		case 888: {
			addAlarm(intent.getLongExtra("future", 0));
			break;
		}
		case 58: {
			pos = intent.getIntExtra("pos", 0);
			String pl = intent.getStringExtra("playListName");
			if (pl.length() == 0) {
				if (null != albumKey || null != artistKey
						|| !pl.equals(playListName)) {
					playListName = pl;
					playList = MusicUtil.readAllMusic(this);
					playListName = "";
				}
			} else if (!pl.equals(playListName)) {
				playListName = pl;
				readPlayList();
			}
			playListMode = MODE_PLAY_LIST;
			updatePlayMusic();
			albumKey = null;
			artistKey = null;
			break;
		}
		case 88: {
			pos = intent.getIntExtra("pos", 0);
			albumKey = intent.getStringExtra("album_key");
			playList = MusicUtil.readMusicsFromAlbum(this, albumKey);
			playListMode = MODE_PLAY_LIST;
			updatePlayMusic();
			playListName = null;
			artistKey = null;
			break;
		}
		case 86: {
			pos = intent.getIntExtra("pos", 0);
			artistKey = intent.getStringExtra("artist_key");
			playList = MusicUtil.readMusicsFromArtist(this, artistKey);
			playListMode = MODE_PLAY_LIST;
			updatePlayMusic();
			playListName = null;
			albumKey = null;
			break;
		}
		case 568: {
			String pln = intent.getStringExtra("playListName");
			if (null == pln) {
				break;
			}
			if (pln.length() > 0 && pln.equals(playListName)) {
				ArrayList<MusicFile> pl = PlayList.getInstance().getPlayList(
						playListName);
				this.playList = pl.toArray(new MusicFile[pl.size()]);
				inform();
			}
			break;
		}
		case 589: {
			handler.post(new Runnable() {
				@Override
				public void run() {
					updatePlayState();
				}
			});
			break;
		}
		case 598: {
			handler.post(new Runnable() {
				@Override
				public void run() {
					next();
				}
			});
			break;
		}
		case 586: {
			folder = intent.getStringExtra("folder");
			pos = intent.getIntExtra("pos", 0);
			playListMode = MODE_FOLDER_LIST;
			handler.post(new Runnable() {
				@Override
				public void run() {
					playList = MusicUtil.getMusicFilesFromFolder(MusicPlayerService.this, folder);
					updatePlayMusic();
				}
			});
			break;
		}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler handler = new Handler();

	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if("com.xrlfy.musicplayer.mediabutton".equals(action)){
				next();
			}else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
				if (intent.hasExtra("state")) {
					int state = intent.getIntExtra("state", 0);
					if (state == 0) {
						if (mediaPlayer.isPlaying() && isPlugged) {
							isPlugged = false;
							mediaPlayer.pause();
							isPause = true;
							inform();
							saveSharedPreferences();
						}
					} else {
						if (!isPlugged) {
							isPlugged = true;
							mediaPlayer.start();
							isPause = false;
							inform();
						}
					}
				}
			}else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
				TelephonyManager telephony = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				int state = telephony.getCallState();
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					// wating
					if (mediaPlayer.isPlaying()) {
						isCalling = true;
						mediaPlayer.pause();
						isPause = true;
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					//
					if (isCalling) {
						isCalling = false;
						mediaPlayer.start();
						isPause = false;
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					// speaking
					if (mediaPlayer.isPlaying()) {
						isCalling = true;
						isPause = true;
						mediaPlayer.pause();
					}
					break;
				}
			}else if("com.xrlfy.musicplayer.widget".equals(action)){
				widgetProvider.notifyChange(MusicPlayerService.this);
			}else if(Intent.ACTION_SHUTDOWN.equals(action)){
				exitMusicPlayer();
			}
		}
	};

	private void registerReceiver() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		try {
			Method method = AudioManager.class.getMethod("registerMediaButtonEventReceiver", ComponentName.class);
			method.setAccessible(true);
			ComponentName cm = new ComponentName(this,MusicBroadcastReceiver.class);
			method.invoke(am, cm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unRegisterReceiver() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		try {
			Method method = AudioManager.class.getMethod("unregisterMediaButtonEventReceiver", ComponentName.class);
			method.setAccessible(true);
			ComponentName cm = new ComponentName(this,MusicBroadcastReceiver.class);
			method.invoke(am, cm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void notification() {
		n.contentView = new RemoteViews(getPackageName(), R.layout.notification);
		n.contentIntent = PendingIntent.getActivity(this, 2, new Intent(this,
				MusicPlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		n.flags |= Notification.FLAG_ONGOING_EVENT;
		updateNotification();
	}

	private void updateNotification() {
		if (null == playList || playList.length < pos || playList.length == 0) {
			return;
		}
		MusicFile mf = playList[pos];
		RemoteViews rvs = new RemoteViews(getPackageName(),
				R.layout.notification);
		rvs.setTextViewText(R.id.title, mf.getTitle());
		rvs.setTextViewText(R.id.artist, mf.getArtist());
		Bitmap bitmap = MusicUtil.getAlbumImage(this,
				playList[pos].absolutePath);
		if (null != bitmap) {
			rvs.setImageViewBitmap(R.id.lable, bitmap);
		} else {
			rvs.setImageViewBitmap(R.id.lable, icon);
		}
		n.contentView = rvs;
		startForeground(NOTIFICATION_ID, n);
	}

	private void addAlarm(long future) {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent service = new Intent(this, MusicPlayerService.class);
		service.putExtra("cmd", 59438);
		PendingIntent pi = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_CANCEL_CURRENT);
		long time = System.currentTimeMillis() + future;
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
		getSharedPreferences("music_player", Context.MODE_PRIVATE).edit().putLong("time_up", time).commit();
	}
}
