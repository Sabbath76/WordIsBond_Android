/*___Generated_by_IDEA___*/

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Tom\\git\\gitrepro\\WordIsBond\\src\\com\\tomapp\\wordisbond\\RemoteService.aidl
 */
package com.tomapp.wordisbond;
public interface RemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tomapp.wordisbond.RemoteService
{
private static final java.lang.String DESCRIPTOR = "com.tomapp.wordisbond.RemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tomapp.wordisbond.RemoteService interface,
 * generating a proxy if needed.
 */
public static com.tomapp.wordisbond.RemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tomapp.wordisbond.RemoteService))) {
return ((com.tomapp.wordisbond.RemoteService)iin);
}
return new com.tomapp.wordisbond.RemoteService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
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
case TRANSACTION_TriggerParse:
{
data.enforceInterface(DESCRIPTOR);
this.TriggerParse();
reply.writeNoException();
return true;
}
case TRANSACTION_GetLatestResult:
{
data.enforceInterface(DESCRIPTOR);
com.tomapp.wordisbond.RSSFeedData _result = this.GetLatestResult();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_RegisterListener:
{
data.enforceInterface(DESCRIPTOR);
com.tomapp.wordisbond.RSSServiceListener _arg0;
_arg0 = com.tomapp.wordisbond.RSSServiceListener.Stub.asInterface(data.readStrongBinder());
this.RegisterListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_UnregisterListener:
{
data.enforceInterface(DESCRIPTOR);
com.tomapp.wordisbond.RSSServiceListener _arg0;
_arg0 = com.tomapp.wordisbond.RSSServiceListener.Stub.asInterface(data.readStrongBinder());
this.UnregisterListener(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tomapp.wordisbond.RemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void TriggerParse() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_TriggerParse, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public com.tomapp.wordisbond.RSSFeedData GetLatestResult() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.tomapp.wordisbond.RSSFeedData _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_GetLatestResult, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.tomapp.wordisbond.RSSFeedData.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void RegisterListener(com.tomapp.wordisbond.RSSServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_RegisterListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void UnregisterListener(com.tomapp.wordisbond.RSSServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_UnregisterListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_TriggerParse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_GetLatestResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_RegisterListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_UnregisterListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void TriggerParse() throws android.os.RemoteException;
public com.tomapp.wordisbond.RSSFeedData GetLatestResult() throws android.os.RemoteException;
public void RegisterListener(com.tomapp.wordisbond.RSSServiceListener listener) throws android.os.RemoteException;
public void UnregisterListener(com.tomapp.wordisbond.RSSServiceListener listener) throws android.os.RemoteException;
}
