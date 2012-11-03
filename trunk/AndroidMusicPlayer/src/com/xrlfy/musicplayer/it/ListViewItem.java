package com.xrlfy.musicplayer.it;

import android.graphics.Bitmap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewItem {
	
	public ImageView icon;
	public TextView title;
	public TextView label;
	public CheckBox check;
	
	public void setIcon(Bitmap bitmap){
		if(null!=icon){
			icon.setImageBitmap(bitmap);
		}
	}
	
	public void setIcon(int resId){
		icon.setImageResource(resId);
	}
	
	public void setTitle(String str){
		title.setText(str);
	}
}
