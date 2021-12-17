package com.lewei.lib;

public interface OnTcpListener
{
	void TcpConnected();
	
	void TcpDisconnected();

	void TcpReceive(byte[] data, int size);
		void TcpRemoteKeyListener(int key_value, int key_index,int key_status);
	/*
	CameraOk   摄像头适口准备可以了，
	cameraCurFps   摄像头当前的帧率
	*/
	void  OnCameraOk(int CameraOk,int cameraCurFps);
	
	void  OnTakePhotoOk(int CameraOk,String path);
}
