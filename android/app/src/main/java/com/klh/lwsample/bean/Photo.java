package com.klh.lwsample.bean;

import android.util.Log;

import java.io.File;
import java.util.Date;

public class Photo {
	private String Tag="Photo";
	// 文件列表的时候用到的
	public int  photoID;
	public String path = "";// 路径
	public long s_start_time = 0;// 时间
	public Date m_photo_date = null;// 日期
	public File f_photo_file = null;// 缩略图的时候对应的photo file
	private   boolean isDelete = false;
	public boolean select=false;
	public static boolean isSelectAll=false;
	
	public Photo(int id, String path){
		photoID = id;
		select=false;
		this.path=path;
	}
	public Photo(int id, boolean flag) {
		photoID = id;
		select = flag;
	}

	public int getPhotoID() {
		return photoID;
	}
	public void setPhotoID(int photoID) {
		this.photoID = photoID;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public static boolean isSelectAll() {
		//Log.e("", "isSelectAll==="+isSelectAll);
		return isSelectAll;
	}

	public static void setSelectAll(boolean isSelectAll) {
		Photo.isSelectAll = isSelectAll;
	}

	public Photo(String path) {
		this.path = path;
	}

//	public  void setDeleteFlag() {
//		if (isDelete) {
//			isDelete = false;
//		} else {
//			isDelete = true;
//		}
//	}
//	public boolean getDelteFlag() {
//		return isDelete;
//	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
		Log.e(Tag, "------又执行了一次这里------");;
	}
	public   boolean getDelteFlag() {
		return isDelete;
	}

	public void setPath(String path) {

	}



	public void setFile(File photofile) {
		// TODO Auto-generated method stub
		this.f_photo_file = photofile;
	}

	public Object get(Photo photo) {
		// TODO Auto-generated method stub
		return photo;
	}
}
