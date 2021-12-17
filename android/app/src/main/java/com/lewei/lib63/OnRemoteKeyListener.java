package com.lewei.lib63;


/**
 * create an interface for JNI to call
 * @author Tony
 * @Description when the remote key pressed for take photo or video,
 * then this interface will receive the message of the key value
 */
public interface OnRemoteKeyListener
{
	void OnRemoteKeyValue(int type, int value);
}
