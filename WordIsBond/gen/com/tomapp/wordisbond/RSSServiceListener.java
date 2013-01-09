/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Tom\\Documents\\Projects\\eclipse\\WordIsBond\\src\\com\\tomapp\\wordisbond\\RSSServiceListener.aidl
 */
package com.tomapp.wordisbond;
public interface RSSServiceListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tomapp.wordisbond.RSSServiceListener
{
private static final java.lang.String DESCRIPTOR = "com.tomapp.wordisbond.RSSServiceListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tomapp.wordisbond.RSSServiceListener interface,
 * generating a proxy if needed.
 */
public static com.tomapp.wordisbond.RSSServiceListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tomapp.wordisbond.RSSServiceListener))) {
return ((com.tomapp.wordisbond.RSSServiceListener)iin);
}
return new com.tomapp.wordisbond.RSSServiceListener.Stub.Proxy(obj);
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
case TRANSACTION_onNewFeed:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onNewFeed(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tomapp.wordisbond.RSSServiceListener
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
public void onNewFeed(java.lang.String pubDate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pubDate);
mRemote.transact(Stub.TRANSACTION_onNewFeed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onNewFeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void onNewFeed(java.lang.String pubDate) throws android.os.RemoteException;
}
