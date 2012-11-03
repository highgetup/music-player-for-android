package com.xrlfy.musicplayer.it;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.xrlfy.musicplayer.R;
import com.xrlfy.musicplayer.util.FileUtil;

public class MyFile implements Parcelable{
	
	private final static int FALSE = 0;
	private final static int TRUE = 1;
	
	public String name;
	public String typeName="";
	public Integer iconId;
	public long length;
	public String lengthString;
	public long lastModified;
	public String lastModifiedString;
	public boolean isDirectory;
	public String absolutePath;
	public boolean canRead;
	public String info;
	public boolean isChecked;
	private SimpleDateFormat sdf;
	
	public MyFile(){
		super();
		this.name="..";
		this.isDirectory=true;
		this.info="";
	}
	
	public MyFile(String file){
		this(new File(file));
	}
	public MyFile(File file){
		init(file);
	}
	
	protected void init(File file){
		sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		name=file.getName();
		typeName=name.substring(name.lastIndexOf(".")+1).toLowerCase();
		iconId=R.drawable.zaoan;
		if(null==iconId){
			iconId=R.drawable.zaoan;
		}
		length=file.length();
		lastModified=file.lastModified();
		isDirectory=file.isDirectory();
		absolutePath=file.getAbsolutePath();
		canRead=file.canRead();
		lastModifiedString=sdf.format(lastModified);
		info=lastModifiedString;
		if(!isDirectory){
			lengthString=FileUtil.getSizeString(length);
			info+="   "+lengthString;
		}
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(typeName);
		dest.writeInt(iconId);
		dest.writeLong(length);
		dest.writeString(lengthString);
		dest.writeLong(lastModified);
		dest.writeString(lastModifiedString);
		dest.writeInt(isDirectory?TRUE:FALSE);
		dest.writeString(absolutePath);
		dest.writeInt(canRead?TRUE:FALSE);
		dest.writeString(info);
	}
	
	public static final Parcelable.Creator<MyFile> CREATOR = new Creator<MyFile>(){
		@Override
		public MyFile createFromParcel(Parcel source) {
			MyFile mf=new MyFile();
			mf.name=source.readString();
			mf.typeName=source.readString();
			mf.iconId=source.readInt();
			mf.length=source.readLong();
			mf.lengthString=source.readString();
			mf.lastModified=source.readLong();
			mf.lastModifiedString=source.readString();
			mf.isDirectory=(source.readInt()==TRUE);
			mf.absolutePath=source.readString();
			mf.canRead=(source.readInt()==TRUE);
			mf.info=source.readString();
			return mf;
		}

		@Override
		public MyFile[] newArray(int size) {
			return null;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static ArrayList<MyFile> getMyFilestByTypeFromList(ArrayList<MyFile> mfs, int type) {
		ArrayList<MyFile> list = new ArrayList<MyFile>();
		if (null == mfs) {
			return list;
		}
		MyFile mf;
		int size = mfs.size();
		int i = 0;
		for (; i < size; i++) {
			mf = mfs.get(i);
			if (null!=mf.iconId && mf.iconId == type) {
				list.add(mf);
			}
		}
		return list;
	}
	
	public String getParent(){
		return absolutePath.substring(0, absolutePath.lastIndexOf('/'));
	}
}
