package com.lewei.lib63;

public interface ConnectState {
	public void onFormatState(int progress);

	/**
	 * pushout: true 拔出；false 插入
	 * 
	 * @param pushout
	 */
	public void onSDPushOut(boolean pushout);

	// public void OnSystemEnvent(String eventString);

	// public void OnAlarmEnvent(String eventString);

	public void onRecordStateChanged(boolean recording);

	public void onDevOffLine();

	public void onDeviceConnect();

	public void onRemoteKey(int type, int value);

}
