package com.klh.lwsample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.klh.lwsample.controller.FlyModel;
import com.lewei.lib.LeweiLib;

public class ParamsConfig {

	/**
	 * 从配置中读取是否选择LW93 默认为LW93
	 * 
	 * @return
	 */
	public static int readHDPLAY(Context context) {
		SharedPreferences sp = context.getSharedPreferences("HDPLAY",
				Context.MODE_PRIVATE);
		return sp.getInt("HDPLAY", LeweiLib.HD_flag);
	}


	public static int readleft(Context context) {
		SharedPreferences sp = context.getSharedPreferences("left",
				Context.MODE_PRIVATE);
		return sp.getInt("left", 0);
	}

}
