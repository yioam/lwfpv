package com.klh.lwsample.bean;

import java.io.File;
import java.util.Date;

public class Video {
	// 文件列表的时候用到的
	public String path = "";// 路径
	public long s_start_time = 0;// 时间
	public Date m_video_date = null;// 日期
	public File f_photo_file = null;// 缩略图的时候对应的photo file
	private boolean isDelete = false;
	public boolean selectVideo = false;

	public boolean isSelectVideo() {
		return selectVideo;
	}

	public void setSelectVideo(boolean selectVideo) {
		this.selectVideo = selectVideo;
	}

	public static boolean isVideoSelectAll = false;

	public static boolean isVideoSelectAll() {
		return isVideoSelectAll;
	}

	public static void setVideoSelectAll(boolean isVideoSelectAll) {
		Video.isVideoSelectAll = isVideoSelectAll;
	}

	public Video(String path) {
		this.path = path;
	}

	public void setDeleteFlag() {
		if (isDelete) {
			isDelete = false;
		} else {
			isDelete = true;
		}
	}

	public boolean getDelteFlag() {
		return isDelete;
	}

	public void setPath(String path) {

	}

	public void setFile(File photofile) {
		// TODO Auto-generated method stub
		this.f_photo_file = photofile;
	}
}
