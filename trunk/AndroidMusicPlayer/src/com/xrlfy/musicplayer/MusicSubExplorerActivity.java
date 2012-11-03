package com.xrlfy.musicplayer;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xrlfy.musicplayer.it.MusicFile;
import com.xrlfy.musicplayer.it.PlayList;
import com.xrlfy.musicplayer.service.MusicPlayerService;
import com.xrlfy.musicplayer.util.MusicUtil;

public class MusicSubExplorerActivity extends Activity implements OnItemClickListener,OnItemLongClickListener{
	
	private LayoutInflater li;
	private MusicFile[] playList;
	private String playListName;
	private String albumKey;
	private String artistKey;
	private String folder;
	private int pos;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_layout);
		li=getLayoutInflater();
		Intent intent=getIntent();
		if(intent.hasExtra("album_key")){
			albumKey=intent.getStringExtra("album_key");
			playList=MusicUtil.readMusicsFromAlbum(this, albumKey);
		}
		else if(intent.hasExtra("artist_key")){
			artistKey=intent.getStringExtra("artist_key");
			playList=MusicUtil.readMusicsFromArtist(this, artistKey);
		}else if(intent.hasExtra("folder")){
			folder = intent.getStringExtra("folder");
			playList=MusicUtil.getMusicFilesFromFolder(this, folder);
		}else{
			playListName = intent.getStringExtra("playListName");
			readPlayList();
		}
		pos=intent.getIntExtra("pos", -1);
		
		listView=(ListView) findViewById(R.id.listview);
		listView.setFastScrollEnabled(true);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(null!=playList && playList.length>pos && pos>0){
			listView.post(new Runnable() {
				@Override
				public void run() {
					listView.setSelection(pos);
				}
			});
		}
	}

	private BaseAdapter adapter=new BaseAdapter() {
		@Override
		public void notifyDataSetChanged() {
			readPlayList();
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
				item.triangle=convertView.findViewById(R.id.triangle);
				convertView.setTag(item);
			}else{
				item=(Item) convertView.getTag();
			}
			item.icon.setImageResource(R.drawable.icon_audio);
			item.title.setText(playList[position].getTitle());
			item.info.setText(playList[position].getArtist());
			if(position==pos){
				item.triangle.setVisibility(View.VISIBLE);
			}else{
				item.triangle.setVisibility(View.GONE);
			}
			return convertView;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public Object getItem(int position) {
			return getCount()>position?playList[position]:null;
		}
		@Override
		public int getCount() {
			return null!=playList?playList.length:0;
		}
	};
	
	private class Item{
		private ImageView icon;
		private TextView title;
		private TextView info;
		private View triangle;
	}
	
	private void readPlayList(){
		if("".equals(playListName)){
			playList=MusicUtil.readAllMusic(this);
		}else{
			ArrayList<MusicFile> list=MusicUtil.readPlayList(playListName);
			if(null!=list){
				playList=list.toArray(new MusicFile[list.size()]);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final MusicFile mf=(MusicFile) parent.getItemAtPosition(position);
		String[] items=getResources().getStringArray(R.array.music_array);
		new AlertDialog.Builder(MusicSubExplorerActivity.this).setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0:{
					String[] array=MusicUtil.readPlayLists();
					ArrayList<String> list=new ArrayList<String>();
					for (int i = 0; i < array.length; i++) {
						if(!array[i].equals(playListName)){
							list.add(array[i]);
						}
					}
					list.add(getString(R.string.new_playlist));
					final String[] data=list.toArray(new String[list.size()]);
					new AlertDialog.Builder(MusicSubExplorerActivity.this).setItems(data, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which==data.length-1){
								final EditText et=new EditText(MusicSubExplorerActivity.this);
								new AlertDialog.Builder(MusicSubExplorerActivity.this).setView(et)
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
								PlayList.getInstance().save(data[which], mf);
							}
						}
					}).create().show();
					break;
				}
				case 1:{
					LinearLayout layout=new LinearLayout(MusicSubExplorerActivity.this);
					layout.setOrientation(LinearLayout.VERTICAL);
					TextView tv=new TextView(MusicSubExplorerActivity.this);
					tv.setTextAppearance(MusicSubExplorerActivity.this, android.R.style.TextAppearance_Medium);
					tv.setTextColor(0xFFFFFFFF);
					final CheckBox cbo=new CheckBox(MusicSubExplorerActivity.this);
					if(null==playListName){
						tv.setText(R.string.delete_music_confirm);
						layout.addView(tv);
					}else{
						cbo.setChecked(false);
						cbo.setText(R.string.delete_physical_file);
						tv.setText(R.string.delete_music_from_playlst_confirm);
						layout.addView(tv);
						layout.addView(cbo);
					}
					new AlertDialog.Builder(MusicSubExplorerActivity.this).setView(layout)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(null!=playListName){
								try {
									if(cbo.isChecked()){
										new File(mf.absolutePath).delete();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}finally{
									PlayList.getInstance().delete(playListName, mf);
									adapter.notifyDataSetChanged();
								}
							}else{
								new File(mf.absolutePath).delete();
							}
						}
					}).setNegativeButton(android.R.string.no, null).create().show();
					break;
				}
				case 2:{
					MusicUtil.showPropertyDialog(MusicSubExplorerActivity.this, mf);
					break;
				}
				}
			}
		}).create().show();
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent service=new Intent(MusicSubExplorerActivity.this, MusicPlayerService.class);
		if(null!=playListName){
			service.putExtra("cmd", 58);
			service.putExtra("playListName", playListName);
			
		}else if(null!=albumKey){
			service.putExtra("cmd", 88);
			service.putExtra("album_key", albumKey);
		}else if(null!=artistKey){
			service.putExtra("cmd", 86);
			service.putExtra("artist_key", artistKey);
		}else if(null!=folder){
			service.putExtra("cmd", 586);
			service.putExtra("folder", folder);
			service.putExtra("pos", position);
		}
		service.putExtra("pos", position);
		startService(service);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(null!=playList && playList.length>pos && pos>0){
			listView.post(new Runnable() {
				@Override
				public void run() {
					listView.setSelection(pos);
				}
			});
		}
	}

	@Override
	public void finish() {
		Intent service=new Intent(this, MusicPlayerService.class);
		service.putExtra("cmd", 568);
		service.putExtra("playListName", playListName);
		startService(service);
		super.finish();
	}
}
