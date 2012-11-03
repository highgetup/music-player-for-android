package com.xrlfy.musicplayer;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.xrlfy.musicplayer.it.MusicFile;
import com.xrlfy.musicplayer.it.PlayList;
import com.xrlfy.musicplayer.service.MusicPlayerService;
import com.xrlfy.musicplayer.util.MusicUtil;

public class MusicExplorerActivity extends TabActivity implements TabContentFactory,OnTabChangeListener{
	
	private Integer[] tabsResources={R.string.artist,R.string.albums,R.string.all_music,R.string.play_list};
	private String[] tags={"artist","album","all_music","play_list"};
	private static LayoutInflater li;
	private String artist_key = "";
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.music_explorer);
		li=getLayoutInflater();
		Intent intent=getIntent();
		if(intent.hasExtra("artist_key")){
			artist_key=intent.getStringExtra("artist_key");
			tabsResources=new Integer[]{R.string.albums,R.string.all_music};
			tags=new String[]{"album","all_music"};
		}
		initTabs();
	}
	
	private void initTabs(){
		TabHost th=getTabHost();
		th.setOnTabChangedListener(this);
		TabSpec ts = null;
		int count = 4;
		if(artist_key.length()>0){
			count = 2;
		}
		for(int i=0;i<count;i++){
			ts=th.newTabSpec(tags[i]);
			View view=li.inflate(R.layout.indicator, null);
			TextView tv=(TextView) view.findViewById(R.id.textView);
			tv.setText(getString(tabsResources[i]));
			ts.setIndicator(view);
			ts.setContent(this);
			th.addTab(ts);
		}
	}

	@Override
	public View createTabContent(String tag) {
		boolean flag=artist_key.length()>0;
		if(tags[0].equals(tag)){
			if(flag){
				return createAlbumView();
			}
			return createArtistView();
		} else if(tags[1].equals(tag)){
			if(flag){
				return createAllMusicView();
			}
			return createAlbumView();
		} else if(tags[2].equals(tag)){
			return createAllMusicView();
		} else if(tags[3].equals(tag)){
			return createPlayListView();
		}
		return null;
	}
	
	private View createAllMusicView(){
		View view=li.inflate(R.layout.listview_layout, null);
		ListView lv=(ListView) view.findViewById(R.id.listview);
		final MusicFile[] mfs;
		if(artist_key.length()>0){
			mfs=MusicUtil.readMusicsFromArtist(MusicExplorerActivity.this, artist_key);
		}else{
			mfs=MusicUtil.readAllMusic(this);
		}
		lv.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Item item;
				MusicFile mf=mfs[position];
				if(null==convertView){
					convertView=li.inflate(R.layout.music_item, null);
					item=new Item();
					item.icon=(ImageView) convertView.findViewById(R.id.icon);
					item.title=(TextView) convertView.findViewById(R.id.title);
					item.info=(TextView) convertView.findViewById(R.id.info);
					convertView.setTag(item);
				}else{
					item=(Item) convertView.getTag();
				}
				item.icon.setImageResource(R.drawable.icon_audio);
				item.title.setText(mf.getTitle());
				item.info.setText(mf.getArtist());
				return convertView;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			@Override
			public Object getItem(int position) {
				return getCount()>position?mfs[position]:null;
			}
			@Override
			public int getCount() {
				return null!=mfs?mfs.length:0;
			}
		});
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent service=new Intent(MusicExplorerActivity.this, MusicPlayerService.class);
				if(artist_key.length()>0){
					service.putExtra("cmd", 86);
					service.putExtra("artist_key", artist_key);
				}else{
					service.putExtra("cmd", 58);
					service.putExtra("playListName", "");
				}
				service.putExtra("pos", position);
				startService(service);
				finish();
			}
		});
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, View view,
					final int position, long id) {
				String[] array=getResources().getStringArray(R.array.music_array);
				final MusicFile mf=(MusicFile)parent.getItemAtPosition(position);
				new AlertDialog.Builder(MusicExplorerActivity.this)
				.setItems(array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:{
							readPlayLists();
							ArrayList<String> list=new ArrayList<String>();
							for (int i = 0; i < playLists.length; i++) {
								list.add(playLists[i]);
							}
							list.add(getString(R.string.new_playlist));
							final String[] items=list.toArray(new String[list.size()]);
							new AlertDialog.Builder(MusicExplorerActivity.this)
							.setItems(items, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog,int which) {
									if(which==items.length-1){
										final EditText et=new EditText(MusicExplorerActivity.this);
										new AlertDialog.Builder(MusicExplorerActivity.this).setView(et)
										.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												String name=et.getText().toString();
												if(null==name || name.length()==0){
													return;
												}
												if(PlayList.getInstance().save(name, mf)){
													Toast.makeText(getApplicationContext(),
															String.format(getString(R.string.add_to_playlist_success),name),Toast.LENGTH_LONG).show();
												}else{
													Toast.makeText(getApplicationContext(),
															String.format(getString(R.string.add_to_playlist_fail),name),Toast.LENGTH_LONG).show();
												}
											}
										}).setNegativeButton(android.R.string.no, null).create().show();
									}else{
										PlayList.getInstance().save(items[which], mf);
									}
								}
							}).create().show();
							break;
						}
						case 1:{
							new AlertDialog.Builder(MusicExplorerActivity.this).setMessage(R.string.delete_music_confirm)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new File(mf.absolutePath).delete();
								}
							}).setNegativeButton(android.R.string.no, null).create().show();
							break;
						}
						case 2:{
							MusicUtil.showPropertyDialog(MusicExplorerActivity.this,mf);
							break;
						}
						}
					}
				}).create().show();
				return false;
			}
		});
		return view;
	}
	
	private View createArtistView(){
		View view=li.inflate(R.layout.listview_layout, null);
		ListView lv=(ListView) view.findViewById(R.id.listview);
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{
				MediaStore.Audio.Artists.ARTIST,
				MediaStore.Audio.Artists.ARTIST_KEY}, null, null,MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
		int count=cursor.getCount();
		final String[] artist=new String[count];
		final String[] artist_key=new String[count];
		int i=0;
		while (cursor.moveToNext()) {
			artist[i]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
			artist_key[i]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST_KEY));
			i++;
		}
		lv.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Item item;
				if(null==convertView){
					convertView=li.inflate(R.layout.music_item, null);
					item=new Item();
					item.icon=(ImageView) convertView.findViewById(R.id.icon);
					item.title=(TextView) convertView.findViewById(R.id.title);
					item.info=(TextView) convertView.findViewById(R.id.info);
					convertView.setTag(item);
				}else{
					item=(Item) convertView.getTag();
				}
				item.icon.setVisibility(View.GONE);
				item.title.setText(artist[position]);
				item.info.setVisibility(View.GONE);
				return convertView;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			@Override
			public Object getItem(int position) {
				return getCount()>position?artist[position]:null;
			}
			@Override
			public int getCount() {
				return null!=artist?artist.length:0;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String key=artist_key[position];
				Intent intent=new Intent(MusicExplorerActivity.this, MusicExplorerActivity.class);
				intent.putExtra("artist_key", key);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		return view;
	}
	
	private View createAlbumView(){
		View view=li.inflate(R.layout.listview_layout, null);
		ListView lv=(ListView) view.findViewById(R.id.listview);
		String where=artist_key.length()>0?MediaStore.Audio.Artists.ARTIST_KEY+"=?":null;
		String[] arg=artist_key.length()>0?new String[]{artist_key}:null;
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				new String[]{MediaStore.Audio.Albums.ALBUM,MediaStore.Audio.Albums.ALBUM_KEY,MediaStore.Audio.Albums._ID}, 
				where,arg,MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
		int count=cursor.getCount();
		final String[] albums=new String[count];
		final String[] album_keys=new String[count];
		final int[] album_ids=new int[count];
		int i=0;
		while (cursor.moveToNext()) {
			albums[i]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
			album_keys[i]=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_KEY));
			album_ids[i]=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
			i++;
		}
		final Bitmap icon=BitmapFactory.decodeResource(getResources(), R.drawable.zaoan);
		lv.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Item item;
				if(null==convertView){
					convertView=li.inflate(R.layout.music_item, null);
					item=new Item();
					item.icon=(ImageView) convertView.findViewById(R.id.icon);
					item.title=(TextView) convertView.findViewById(R.id.title);
					item.info=(TextView) convertView.findViewById(R.id.info);
					convertView.setTag(item);
				}else{
					item=(Item) convertView.getTag();
				}
				Bitmap bitmap=MusicUtil.getAlbumImage(MusicExplorerActivity.this, album_ids[position]);
				if(null!=bitmap){
					item.icon.setImageBitmap(bitmap);
				}else{ 
					item.icon.setImageBitmap(icon);
				}
				item.title.setText(albums[position]);
				item.info.setVisibility(View.GONE);
				return convertView;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			@Override
			public Object getItem(int position) {
				return getCount()>position?albums[position]:null;
			}
			@Override
			public int getCount() {
				return null!=albums?albums.length:0;
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(MusicExplorerActivity.this, MusicSubExplorerActivity.class);
				intent.putExtra("album_key", album_keys[position]);
				startActivity(intent);
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final String[] items=getResources().getStringArray(R.array.album_array);
				new AlertDialog.Builder(MusicExplorerActivity.this).setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:{
							Intent intent=new Intent(MusicExplorerActivity.this, MusicPlayerService.class);
							intent.putExtra("cmd", 88);
							intent.putExtra("album_key", album_keys[position]);
							intent.putExtra("pos", position);
							startService(intent);
							break;
						}
						case 1:{
							readPlayLists();
							ArrayList<String> list=new ArrayList<String>();
							for (int j = 0; j < playLists.length; j++) {
								list.add(playLists[j]);
							}
							list.add(getString(R.string.new_playlist));
							final String[] data=list.toArray(new String[list.size()]);
							new AlertDialog.Builder(MusicExplorerActivity.this).setItems(data, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(which==data.length-1){
										final EditText et=new EditText(MusicExplorerActivity.this);
										new AlertDialog.Builder(MusicExplorerActivity.this).setView(et)
										.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												String name=et.getText().toString();
												if(null==name || name.length()==0){
													return;
												}
												MusicFile[] mfs=MusicUtil.readMusicsFromAlbum(MusicExplorerActivity.this, album_keys[position]);
												PlayList.getInstance().save(name, mfs);
											}
										}).setNegativeButton(android.R.string.no, null).create().show();
									}else{
										MusicFile[] mfs=MusicUtil.readMusicsFromAlbum(MusicExplorerActivity.this, album_keys[position]);
										PlayList.getInstance().save(data[which], mfs);
									}
								}
							}).create().show();
							break;
						}
						case 2:{
							new AlertDialog.Builder(MusicExplorerActivity.this).setMessage(R.string.delete_album_confirm)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									MusicFile[] mfs=MusicUtil.readMusicsFromAlbum(MusicExplorerActivity.this, album_keys[position]);
									for (int j = 0; j < mfs.length; j++) {
										new File(mfs[j].absolutePath).delete();
									}
								}
							}).setNegativeButton(android.R.string.no, null).create().show();
							break;
						}
						}
					}
				}).create().show();
				return false;
			}
		});
		return view;
	}
	
	private String[] playLists=new String[0];
	
	private View createPlayListView(){
		View view=li.inflate(R.layout.listview_layout, null);
		listView=(ListView) view.findViewById(R.id.listview);
		readPlayLists();
		listView.setAdapter(new BaseAdapter() {
			@Override
			public void notifyDataSetChanged() {
				readPlayLists();
				super.notifyDataSetChanged();
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Item item;
				if(null==convertView){
					convertView=li.inflate(R.layout.music_item, null);
					item=new Item();
					item.icon=(ImageView) convertView.findViewById(R.id.icon);
					item.title=(TextView) convertView.findViewById(R.id.title);
					item.info=(TextView) convertView.findViewById(R.id.info);
					convertView.setTag(item);
				}else{
					item=(Item) convertView.getTag();
				}
				item.icon.setVisibility(View.GONE);
				item.info.setVisibility(View.GONE);
				item.title.setText(playLists[position]);
				return convertView;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}
			@Override
			public Object getItem(int position) {
				return getCount()>position?playLists[position]:null;
			}
			@Override
			public int getCount() {
				return null!=playLists?playLists.length:0;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name=(String)parent.getItemAtPosition(position);
				Intent intent=new Intent(MusicExplorerActivity.this,MusicSubExplorerActivity.class);
				intent.putExtra("playListName", name);
				startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, View view,final int position, long id) {
				String[] items=getResources().getStringArray(R.array.playlist_array);
				new AlertDialog.Builder(MusicExplorerActivity.this).setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:{
							final EditText et=new EditText(MusicExplorerActivity.this);
							new AlertDialog.Builder(MusicExplorerActivity.this).setView(et)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String name=et.getText().toString();
									if(null!= name && name.indexOf('/')<0 && name.length()>0){
										String folder=Environment.getExternalStorageDirectory().getPath()+"/.AndroidTool/DB/";
										new File(folder+(String)parent.getItemAtPosition(position)).renameTo(new File(folder+name));
										((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
									}
								}
							}).setNegativeButton(android.R.string.no, null).create().show();
							break;
						}
						case 1:{
							new AlertDialog.Builder(MusicExplorerActivity.this).setMessage(R.string.delete_playlist_confirm)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new File(Environment.getExternalStorageDirectory().getPath()+"/.AndroidTool/DB/"+(String)parent.getItemAtPosition(position)).delete();
									((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
								}
							}).setNegativeButton(android.R.string.no, null).create().show();
							break;
						}
						}
					}
				}).create().show();;
				return false;
			}
		});
		return view;
	}
	
	private void readPlayLists(){
		playLists=MusicUtil.readPlayLists();
		if(null==playLists){
			playLists=new String[0];
		}
	}
	
	private class Item{
		ImageView icon;
		TextView title;
		TextView info;
	}

	@Override
	public void onTabChanged(String tabId) {
		if(tags.length>2 && tags[3].equals(tabId)){
			((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
		}
	}
}
