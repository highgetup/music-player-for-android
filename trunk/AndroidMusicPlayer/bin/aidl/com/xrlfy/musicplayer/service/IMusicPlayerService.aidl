package com.xrlfy.musicplayer.service;

import com.xrlfy.musicplayer.IMusicPlayer;

interface IMusicPlayerService{
	
	void playSpecifiedMusicByPLayListIndex(int index,String playList);
	
	void playSpecifiedMusicByPath(String fileName);
	
	void playPrevious();
	
	void playNext();
	
	void playOrPause();
	
	void seek(int pos);
	
	boolean isPlaying();
	
	boolean isPause();
	
	int getgetCurrentPosition();
	
	int getDuration();
	
	void setRandom();
	
	void setRepeat();
	
	int getCurrentPlayIndex();
	
	String getCurrentMusicFile();
	
	int getPlayMusicCount();
	
	int getRpeat();
	
	boolean isRandom();
	
	void getInform();
	
	String[] getCurrentPlayList();
	
	String[] getCurentPlayingMusicInfo();
	
	void register(com.xrlfy.musicplayer.IMusicPlayer musicPlayer);
	
	void unRegister(com.xrlfy.musicplayer.IMusicPlayer musicPlayer);
	
	void save();
	
	void addCurrentToPlaylist(String playListName);
	
	void exit();
}