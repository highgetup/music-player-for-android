package com.xrlfy.musicplayer;

interface IMusicPlayer{
	
    void onMusicInfoChanged(String title,String artist,String album,String fileName);
    
    void exitMusicPlayer();
    
}