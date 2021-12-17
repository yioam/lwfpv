package com.lewei.lib63;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.klh.lwsample.service.Services;


public class Device63 {
	private final static String TAG = "device63";

	private Thread connThread;
	private boolean isThreadStop = false;
	private boolean isDevConnected = false;
	/**
	 * 只是表示远程是否正在录像，为了仅在从非录像到录像或者录像到非录像 切换时才会产生回调 onRecordStateChanged
	 */
	private boolean isRemoteRecording = false;

	/**
	 * Activity中点击了远程录像按钮之后，以此标志位来控制线程中开启录像
	 */
	private boolean isNeedRemoteRecord = false;

	private int sleepInterval = 500; // ms

	private ConnectState mConnectState;
	private Services mServices;

	/* 1：挂载成功；0：未挂载 */
	private int mSDCardState = 0;
	private boolean isNeedSDCardFormat = false;

	private SDCardInfo info = new SDCardInfo();
	// 冠旭
	private String USERNAME1 = "guanxukeji";
	private String PASSWORD1 = "gxrdw60";
	// 新（乐为）
	private String USERNAME2 = "leweiadmin";
	private String PASSWORD2 = "leweiadmin";
	// 辉科
	private String USERNAME3 = "leweiadmin";
	private String PASSWORD3 = "lewei12";
	// 旧（乐为）
	private String USERNAME4 = "leweiadmin";
	private String PASSWORD4 = "leweiadmin";

	// 优迪的，连上这个就把翻转的按钮隐藏掉
	private String USERNAME5 = "udircadmin";
	private String PASSWORD5 = "udircadmin";

	private byte[] key1 = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'1', '2', '3', '4', '5', '6' };
	private byte[] key2 = { '1', '2', '3', 'l', 'e', 'w', 'e', 'i', 'm', 'a',
			'r', 'k', '1', '2', '3', '4' };
	private byte[] key3 = { '1', '2', '3', 'l', 'e', 'w', 'e', 'i', 'm', 'a',
			'r', 'k', '1', '2', '3', '4' };
	private byte[] key4 = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'1', '2', '3', '4', '5', '6' };

	private byte[] key5 = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'1', '2', '3', '4', '5', '6' };

	private LeweiLib63 mLeweiLib63 = new LeweiLib63();

	private OnRemoteKeyListener onRemoteKeyListener = new OnRemoteKeyListener() {
		// type与LeweiLib63
		@Override
		public void OnRemoteKeyValue(int type, int value) {
			// TODO Auto-generated method stub
			Log.e(TAG, "message type " + type + " value " + value);
			mConnectState.onRemoteKey(type,value);
		}
	};

	public Device63(Context context) {
		setWifiEnable(context);

		Intent intent = new Intent(context, Services.class);
		context.bindService(intent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
		mLeweiLib63.setRemoteKeyListener(onRemoteKeyListener);
	}

	private void setWifiEnable(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}

	public void startLoginThread() {
		isThreadStop=false;
		if (connThread == null || !connThread.isAlive()) {
			connThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {

						while (!isThreadStop) {

							if (LeweiLib63.LW63GetLogined()) {
								if (!isDevConnected) {
									// callback
									if (mConnectState != null) {
										mConnectState.onDeviceConnect();
										isDevConnected = true;
									}
								}

								if (isNeedSDCardFormat) {

									formatRemoteSDCard(info);
								} else {

									checkSDCardInfo(info);
								}

								// Log.d(TAG, "loop");
								checkRemoteRecordState();

								if (isNeedRemoteRecord) {
									takeRemoteRecord();
								}

							} else {
								// if (!isDevConnected)
								// {
								// LeweiLib63.LW63Login();
								// }

								if (isDevConnected) {
									dealDevStatus(LeweiLib63.FHNPEN_SYS_OffLine);

								}

								if(!mLeweiLib63.LW63LoginNew(USERNAME2, PASSWORD2, key2))
								{
									
									Thread.sleep(200);
									if(mLeweiLib63.LW63LoginNew(USERNAME4, PASSWORD4, key4))
									{
										Log.d(TAG, "login type new");
									}
								}
								else 
								{
									Log.d(TAG, "login type old");
								}
							}

							// dealDevStatus(LeweiLib63.LW63GetDevStatus());

							Thread.sleep(sleepInterval);
						}

						mLeweiLib63.LW63Logout();

					} catch (InterruptedException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			});
			connThread.start();
		}
	}

	public void setOnConnectState(ConnectState mConnectState) {
		isDevConnected = false;
		this.mConnectState = mConnectState;
	}

	public void stopLoginThread() {
		isThreadStop = true;

	}

	/**
	 * 将在线程中开始判断SD卡是否需要格式化，需要则直接格式化，否则无操作
	 */
	public void startFormatSDCard() {
		isNeedSDCardFormat = true;
	}

	private void dealDevStatus(int status) {
		switch (status) {
		case LeweiLib63.FHNPEN_SYS_OffLine: {
			Log.e(TAG, "device off line now");
			if (mConnectState != null) {
				mConnectState.onDevOffLine();
				isDevConnected = false;
			}
		}
			break;
		case LeweiLib63.FHNPEN_SYS_ReConnect: {

		}
			break;
		case LeweiLib63.FHNPEN_SYS_ShotBtnPush: {

		}
		case LeweiLib63.FHNPEN_ALM_SDError: {

		}
			break;
		case LeweiLib63.FHNPEN_ALM_SDFull: {

		}
			break;
		case LeweiLib63.FHNPEN_OPT_RecStart: {

		}
			break;
		case LeweiLib63.FHNPEN_OPT_RecFinish: {

		}
			break;
		}
	}

	private void formatRemoteSDCard(SDCardInfo info) {
		if (LeweiLib63.LW63GetSDCardInfo(info)) {
			if (((info.state & SDCardInfo.FHNPEN_SDCardState_NORMAL) > 0)
					&& ((info.state & SDCardInfo.FHNPEN_SDCardState_FOUND) > 0)
					&& ((info.state & SDCardInfo.FHNPEN_SDCardState_LOADED) > 0)) {
				sleepInterval = 500;
				isNeedSDCardFormat = false; // do not need to format
				return;
			}

			if ((info.state & SDCardInfo.FHNPEN_SDCardState_FOUND) <= 0) {
				sleepInterval = 500;
				isNeedSDCardFormat = false;
				// 在格式化卡的过程中异常拔出
				if (mConnectState != null)
					mConnectState.onSDPushOut(true);

				return;
			}

			if ((info.state & SDCardInfo.FHNPEN_SDCardState_FORMATING) <= 0) {
				LeweiLib63.LW63StartSDCardFormat(0);
			} else {
				LeweiLib63.LW63GetSDCardFormatState(info);
				int progress = info.formatProgress;
				if (progress < 0) {
					progress = 100;
				}

				if (mConnectState != null)
					mConnectState.onFormatState(progress);

				if (progress >= 100) {
					isNeedSDCardFormat = false;// format success
					sleepInterval = 500;
				}
			}
		}
	}

	private void checkSDCardInfo(SDCardInfo sdInfo) {
		if (LeweiLib63.LW63GetSDCardInfo(sdInfo)) {
			if (((sdInfo.state & SDCardInfo.FHNPEN_SDCardState_NORMAL) > 0)
					&& ((sdInfo.state & SDCardInfo.FHNPEN_SDCardState_FOUND) > 0)
					&& ((sdInfo.state & SDCardInfo.FHNPEN_SDCardState_LOADED) > 0)) {
				mSDCardState = 1;
				if (mSDCardState == 0 && mConnectState != null) {
					mConnectState.onSDPushOut(false);
				}
				return;
			}
			if ((sdInfo.state & SDCardInfo.FHNPEN_SDCardState_FOUND) <= 0) {
				// 在格式化卡的过程中异常拔出
				mSDCardState = 0;
				if (mSDCardState == 1 && mConnectState != null) {
					mConnectState.onSDPushOut(true);
				}
				return;
			}
		}
	}

	/**
	 * 远程录像操作，开始和停止都由此操作，开始录像前需要 先判断卡状态，不正常则先格式化
	 */
	public void startRemoteRecord() {
		isNeedRemoteRecord = true;
	}

	private void takeRemoteRecord() {
		if (isRemoteRecording) {
			if (LeweiLib63.LW63StopRemoteRecord()) {
				isRemoteRecording = false;
				if (mConnectState != null)
					mConnectState.onRecordStateChanged(false);
			}
		} else {
			formatRemoteSDCard(info);
			sleepInterval = 200;

			if (LeweiLib63.LW63TakeRemoteRecord()) {
				isRemoteRecording = true;
				if (mConnectState != null)
					mConnectState.onRecordStateChanged(true);
			}
		}
		isNeedRemoteRecord = false;
	}

	private void checkRemoteRecordState() {
		if (LeweiLib63.LW63GetRemoteRecordState()) {
			if (!isRemoteRecording) {
				isRemoteRecording = true;
				if (mConnectState != null)
					mConnectState.onRecordStateChanged(true);
			}
		} else {
			if (isRemoteRecording) {
				isRemoteRecording = false;
				if (mConnectState != null)
					mConnectState.onRecordStateChanged(false);
			}
		}
	}

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			Log.i(TAG, "sevice initialized connected");
			mServices = ((Services.LocalBinder) service).getService();
			mServices.init();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "sevice initialized disconnected");
			mServices.exit();
			mServices = null;
		}
	};
}
