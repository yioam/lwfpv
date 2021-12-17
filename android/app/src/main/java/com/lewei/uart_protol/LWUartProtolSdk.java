package com.lewei.uart_protol;

import com.lewei.uart_protol.ControlPara.Uart_Protocol;


public class LWUartProtolSdk {
	static {
		try {
				System.loadLibrary("lewei_uartprotol");
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("loadLibrary(lewei_uartprotol)," + ule.getMessage());
		}
	}
	

	
	
	public native byte[] LWUartProtolGetControlData(
			Uart_Protocol mUart_Protocol, LWUartProtolBean mLWUartProtolBean);
	

	public native int LWUartProtolFlyInfoParseData(byte[] data,int len,
			Uart_Protocol mUart_Protocol, LWUartProtolBean mLWUartProtolBean);

	public native int LWDroneVersionSync(byte[] data,int[] version,int size,LWUartProtolBean mLWUartProtolBean,int verCRC);


	public native int LWParseTestInfoData(byte[] data,int len,LWUartProtolBean mLWUartProtolBean);

	public native String LWGetObTrackData();
	public native String LWGetHandTrackData();


//	public void LWUartProtolGetControlData(String data, String len,
//			Uart_Protocol protocolBtgps, FlyInfo mFlyInfo) {
//		// TODO Auto-generated method stub
//		
//	}


	
}
