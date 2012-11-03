package com.xrlfy.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
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
import android.widget.TextView;

import com.xrlfy.musicplayer.it.ListViewItem;
import com.xrlfy.musicplayer.it.MusicFile;
import com.xrlfy.musicplayer.it.MyFile;
import com.xrlfy.musicplayer.it.PlayList;
import com.xrlfy.musicplayer.service.MusicPlayerService;
import com.xrlfy.musicplayer.util.MusicUtil;

public class MusicLocalExplorerActivity extends Activity implements OnItemClickListener,OnItemLongClickListener{
	
	private ListView listView;
	private LayoutInflater li;
	private MyAdapter adapter;
	private ArrayList<MyFile> files;
	private ArrayList<MyFile> folders;
	private String currentPath;
	private boolean ifCanBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_layout);
		li=getLayoutInflater();
		listView=(ListView) findViewById(R.id.listview);
		adapter=new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		files=new ArrayList<MyFile>();
		folders=new ArrayList<MyFile>();
		new SearchMusicFoldersTask().execute();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MyFile mf=files.get(position);
		if(mf.isDirectory){
			currentPath=mf.absolutePath;
			searchMusicInFolder();
		}else{
			playLocalMusic(position);
		}
	}
	
	private void playLocalMusic(int pos){
		Intent service=new Intent(MusicLocalExplorerActivity.this, MusicPlayerService.class);
		service.putExtra("cmd", 586);
		service.putExtra("folder", currentPath);
		service.putExtra("pos", pos);
		startService(service);
		finish();
	}
	
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return null!=files?files.size():0;
		}
		@Override
		public Object getItem(int position) {
			return getCount()>position?files.get(position):null;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyFile mf=files.get(position);
			ListViewItem item;
			if(null==convertView){
				convertView=li.inflate(R.layout.music_item, null);
				item=new ListViewItem();
				item.icon=(ImageView) convertView.findViewById(R.id.icon);
				item.title=(TextView) convertView.findViewById(R.id.title);
				item.label=(TextView) convertView.findViewById(R.id.info);
				convertView.setTag(item);
			}else{
				item=(ListViewItem) convertView.getTag();
			}
			if(mf.isDirectory){
				item.setIcon(R.drawable.icon_folder);
				item.label.setText(mf.absolutePath);
			}else{
				item.setIcon(R.drawable.icon_audio);
				item.label.setText(mf.info);
			}
			item.setTitle(mf.name);
			
			return convertView;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		MyFile mf=files.get(position);
		if(mf.isDirectory){
			currentPath = files.get(position).absolutePath;
			String[] array=getResources().getStringArray(R.array.album_array);
			new AlertDialog.Builder(MusicLocalExplorerActivity.this).setItems(array, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which){
					case 0:{
						playLocalMusic(0);
						break;
					}
					case 1:{
						String[] array=MusicUtil.readPlayLists();
						final ArrayList<String> list=new ArrayList<String>();
						list.addAll(Arrays.asList(array));
						list.add(getString(R.string.new_playlist));
						new AlertDialog.Builder(MusicLocalExplorerActivity.this)
						.setItems(list.toArray(new String[list.size()]), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final MusicFile[] mfs=MusicUtil.getMusicFilesFromFolder(MusicLocalExplorerActivity.this, currentPath);
								if(which==(list.size()-1)){
									final EditText et=new EditText(MusicLocalExplorerActivity.this);
									new AlertDialog.Builder(MusicLocalExplorerActivity.this).setView(et)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String name=et.getText().toString();
											if(null==name || name.length()==0){
												return;
											}
											PlayList.getInstance().save(name, mfs);
										}
									}).setNegativeButton(android.R.string.no, null).create().show();
								}else{
									PlayList.getInstance().save(list.get(which), mfs);
								}
							}
						}).create().show();
						break;
					}
					case 2:{
						new AlertDialog.Builder(MusicLocalExplorerActivity.this)
						.setMessage(R.string.delete_folder_confirm).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MusicFile[] mfs=MusicUtil.getMusicFilesFromFolder(MusicLocalExplorerActivity.this, currentPath);
								for (int i = 0; i < mfs.length; i++) {
									new File(mfs[i].absolutePath).delete();
								}
								new SearchMusicFoldersTask().execute();
							}
							
						}).setNegativeButton(android.R.string.no, null).create().show();
						break;
					}
					}
				}
			}).create().show();
		}else{
			String[] array=getResources().getStringArray(R.array.music_array);
			new AlertDialog.Builder(MusicLocalExplorerActivity.this)
			.setItems(array, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final MusicFile mf=(MusicFile) files.get(position);
					switch(which){
					case 0:{
						String[] array=MusicUtil.readPlayLists();
						final ArrayList<String> list=new ArrayList<String>();
						list.addAll(Arrays.asList(array));
						list.add(getString(R.string.new_playlist));
						new AlertDialog.Builder(MusicLocalExplorerActivity.this)
						.setItems(list.toArray(new String[list.size()]), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(which==(list.size()-1)){
									final EditText et=new EditText(MusicLocalExplorerActivity.this);
									new AlertDialog.Builder(MusicLocalExplorerActivity.this).setView(et)
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String name=et.getText().toString();
											if(null==name || name.length()==0){
												return;
											}
											PlayList.getInstance().save(name, mf);
										}
									}).setNegativeButton(android.R.string.no, null).create().show();
								}else{
									PlayList.getInstance().save(list.get(which), mf);
								}
							}
						}).create().show();
						break;
					}
					case 1:{
						new AlertDialog.Builder(MusicLocalExplorerActivity.this)
						.setMessage(R.string.delete_music_confirm).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new File(mf.absolutePath).delete();
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										files.remove(position);
										adapter.notifyDataSetChanged();
									}
								});
							}
						}).setNegativeButton(android.R.string.no, null).create().show();
						break;
					}
					case 2:{
						MusicUtil.showPropertyDialog(MusicLocalExplorerActivity.this, mf);
						break;
					}
					}
				}
			}).create().show();
		}
		return false;
	}
	
	private class SearchMusicFoldersTask extends AsyncTask<Void, Void, Void>{
		private HashMap<String, Object> foldersMap;
		@Override
		protected void onPreExecute() {
			foldersMap=new HashMap<String, Object>();
		}
		@Override
		protected void onPostExecute(Void result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
		@Override
		protected Void doInBackground(Void... params) {
			MusicFile[] mfs=PlayList.getInstance().getAllMusic(MusicLocalExplorerActivity.this);
			for (int i = 0; i < mfs.length; i++) {
				foldersMap.put(mfs[i].getParent(), null);
			}
			Iterator<String> it=foldersMap.keySet().iterator();
			MyFile mf;
			files.clear();
			folders.clear();
			while(it.hasNext()){
				mf=new MyFile(it.next());
				files.add(mf);
				folders.add(mf);
			}
			return null;
		}
	}
	
	private void searchMusicInFolder(){
		if(null==currentPath){
			return;
		}
		File file=new File(currentPath);
		if(file.exists()){
			files.clear();
			MyFile[] mfs=MusicUtil.getMusicFilesFromFolder(MusicLocalExplorerActivity.this, currentPath);
			if(null==mfs){
				return;
			}
			ifCanBack = true;
			for (int i = 0; i < mfs.length; i++) {
				files.add(mfs[i]);
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:{
			if(ifCanBack){
				back();
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}
		default:{
			return super.onKeyDown(keyCode, event);
		}
		}
	}

	@Override
	public void onBackPressed() {
		if(ifCanBack){
			back();
		}else{
			super.onBackPressed();
		}
	}
	
	private void back(){
		ifCanBack = false;
		files.clear();
		files.addAll(folders);
		adapter.notifyDataSetChanged();
	}

}
