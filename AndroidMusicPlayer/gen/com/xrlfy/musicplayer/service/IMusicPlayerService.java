/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\WorkSpace1\\AndroidMusicPlayer\\src\\com\\xrlfy\\musicplayer\\service\\IMusicPlayerService.aidl
 */
package com.xrlfy.musicplayer.service;
public interface IMusicPlayerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.xrlfy.musicplayer.service.IMusicPlayerService
{
private static final java.lang.String DESCRIPTOR = "com.xrlfy.musicplayer.service.IMusicPlayerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.xrlfy.musicplayer.service.IMusicPlayerService interface,
 * generating a proxy if needed.
 */
public static com.xrlfy.musicplayer.service.IMusicPlayerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.xrlfy.musicplayer.service.IMusicPlayerService))) {
return ((com.xrlfy.musicplayer.service.IMusicPlayerService)iin);
}
return new com.xrlfy.musicplayer.service.IMusicPlayerService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_playSpecifiedMusicByPLayListIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.playSpecifiedMusicByPLayListIndex(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_playSpecifiedMusicByPath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.playSpecifiedMusicByPath(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playPrevious:
{
data.enforceInterface(DESCRIPTOR);
this.playPrevious();
reply.writeNoException();
return true;
}
case TRANSACTION_playNext:
{
data.enforceInterface(DESCRIPTOR);
this.playNext();
reply.writeNoException();
return true;
}
case TRANSACTION_playOrPause:
{
data.enforceInterface(DESCRIPTOR);
this.playOrPause();
reply.writeNoException();
return true;
}
case TRANSACTION_seek:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seek(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isPause:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPause();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getgetCurrentPosition:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getgetCurrentPosition();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDuration:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDuration();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setRandom:
{
data.enforceInterface(DESCRIPTOR);
this.setRandom();
reply.writeNoException();
return true;
}
case TRANSACTION_setRepeat:
{
data.enforceInterface(DESCRIPTOR);
this.setRepeat();
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentPlayIndex:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentPlayIndex();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurrentMusicFile:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurrentMusicFile();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getPlayMusicCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPlayMusicCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getRpeat:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getRpeat();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isRandom:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isRandom();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getInform:
{
data.enforceInterface(DESCRIPTOR);
this.getInform();
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentPlayList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getCurrentPlayList();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_getCurentPlayingMusicInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getCurentPlayingMusicInfo();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_register:
{
data.enforceInterface(DESCRIPTOR);
com.xrlfy.musicplayer.IMusicPlayer _arg0;
_arg0 = com.xrlfy.musicplayer.IMusicPlayer.Stub.asInterface(data.readStrongBinder());
this.register(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unRegister:
{
data.enforceInterface(DESCRIPTOR);
com.xrlfy.musicplayer.IMusicPlayer _arg0;
_arg0 = com.xrlfy.musicplayer.IMusicPlayer.Stub.asInterface(data.readStrongBinder());
this.unRegister(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_save:
{
data.enforceInterface(DESCRIPTOR);
this.save();
reply.writeNoException();
return true;
}
case TRANSACTION_addCurrentToPlaylist:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addCurrentToPlaylist(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_exit:
{
data.enforceInterface(DESCRIPTOR);
this.exit();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.xrlfy.musicplayer.service.IMusicPlayerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void playSpecifiedMusicByPLayListIndex(int index, java.lang.String playList) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeString(playList);
mRemote.transact(Stub.TRANSACTION_playSpecifiedMusicByPLayListIndex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playSpecifiedMusicByPath(java.lang.String fileName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(fileName);
mRemote.transact(Stub.TRANSACTION_playSpecifiedMusicByPath, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playPrevious() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playPrevious, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playNext() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playNext, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void playOrPause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playOrPause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void seek(int pos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(pos);
mRemote.transact(Stub.TRANSACTION_seek, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isPause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPause, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getgetCurrentPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getgetCurrentPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDuration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDuration, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setRandom() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setRandom, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setRepeat() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setRepeat, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getCurrentPlayIndex() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentPlayIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCurrentMusicFile() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentMusicFile, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getPlayMusicCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPlayMusicCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getRpeat() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRpeat, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isRandom() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isRandom, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void getInform() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getInform, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String[] getCurrentPlayList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentPlayList, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String[] getCurentPlayingMusicInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurentPlayingMusicInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void register(com.xrlfy.musicplayer.IMusicPlayer musicPlayer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((musicPlayer!=null))?(musicPlayer.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unRegister(com.xrlfy.musicplayer.IMusicPlayer musicPlayer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((musicPlayer!=null))?(musicPlayer.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unRegister, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void save() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_save, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void addCurrentToPlaylist(java.lang.String playListName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(playListName);
mRemote.transact(Stub.TRANSACTION_addCurrentToPlaylist, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void exit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_exit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_playSpecifiedMusicByPLayListIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_playSpecifiedMusicByPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_playPrevious = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_playNext = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_playOrPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_seek = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_isPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getgetCurrentPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_setRandom = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_setRepeat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getCurrentPlayIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getCurrentMusicFile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getPlayMusicCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getRpeat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_isRandom = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getInform = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_getCurrentPlayList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getCurentPlayingMusicInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_register = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_unRegister = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_save = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_addCurrentToPlaylist = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_exit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
}
public void playSpecifiedMusicByPLayListIndex(int index, java.lang.String playList) throws android.os.RemoteException;
public void playSpecifiedMusicByPath(java.lang.String fileName) throws android.os.RemoteException;
public void playPrevious() throws android.os.RemoteException;
public void playNext() throws android.os.RemoteException;
public void playOrPause() throws android.os.RemoteException;
public void seek(int pos) throws android.os.RemoteException;
public boolean isPlaying() throws android.os.RemoteException;
public boolean isPause() throws android.os.RemoteException;
public int getgetCurrentPosition() throws android.os.RemoteException;
public int getDuration() throws android.os.RemoteException;
public void setRandom() throws android.os.RemoteException;
public void setRepeat() throws android.os.RemoteException;
public int getCurrentPlayIndex() throws android.os.RemoteException;
public java.lang.String getCurrentMusicFile() throws android.os.RemoteException;
public int getPlayMusicCount() throws android.os.RemoteException;
public int getRpeat() throws android.os.RemoteException;
public boolean isRandom() throws android.os.RemoteException;
public void getInform() throws android.os.RemoteException;
public java.lang.String[] getCurrentPlayList() throws android.os.RemoteException;
public java.lang.String[] getCurentPlayingMusicInfo() throws android.os.RemoteException;
public void register(com.xrlfy.musicplayer.IMusicPlayer musicPlayer) throws android.os.RemoteException;
public void unRegister(com.xrlfy.musicplayer.IMusicPlayer musicPlayer) throws android.os.RemoteException;
public void save() throws android.os.RemoteException;
public void addCurrentToPlaylist(java.lang.String playListName) throws android.os.RemoteException;
public void exit() throws android.os.RemoteException;
}
