/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\WorkSpace1\\AndroidMusicPlayer\\src\\com\\xrlfy\\musicplayer\\IMusicPlayer.aidl
 */
package com.xrlfy.musicplayer;
public interface IMusicPlayer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.xrlfy.musicplayer.IMusicPlayer
{
private static final java.lang.String DESCRIPTOR = "com.xrlfy.musicplayer.IMusicPlayer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.xrlfy.musicplayer.IMusicPlayer interface,
 * generating a proxy if needed.
 */
public static com.xrlfy.musicplayer.IMusicPlayer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.xrlfy.musicplayer.IMusicPlayer))) {
return ((com.xrlfy.musicplayer.IMusicPlayer)iin);
}
return new com.xrlfy.musicplayer.IMusicPlayer.Stub.Proxy(obj);
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
case TRANSACTION_onMusicInfoChanged:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
this.onMusicInfoChanged(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_exitMusicPlayer:
{
data.enforceInterface(DESCRIPTOR);
this.exitMusicPlayer();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.xrlfy.musicplayer.IMusicPlayer
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
public void onMusicInfoChanged(java.lang.String title, java.lang.String artist, java.lang.String album, java.lang.String fileName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(title);
_data.writeString(artist);
_data.writeString(album);
_data.writeString(fileName);
mRemote.transact(Stub.TRANSACTION_onMusicInfoChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void exitMusicPlayer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_exitMusicPlayer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onMusicInfoChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_exitMusicPlayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void onMusicInfoChanged(java.lang.String title, java.lang.String artist, java.lang.String album, java.lang.String fileName) throws android.os.RemoteException;
public void exitMusicPlayer() throws android.os.RemoteException;
}
