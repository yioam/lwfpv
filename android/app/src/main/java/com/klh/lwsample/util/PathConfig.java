package com.klh.lwsample.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.klh.lwsample.bean.Photo;
import com.klh.lwsample.bean.Video;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PathConfig
{
	public static SdcardSelector sdcardItem = SdcardSelector.BUILT_IN;

	public static final String PHOTOS_PATH = "/LW_FPV/Photos";
	public static final String VIDEOS_PATH = "/LW_FPV/Videos";
	private final static String PARENTFOLDER = "LW_FPV";
	private final static String PHOTOS = "Photos";
	private final static String VIDEOS = "Videos";

	public static  List<Video> videoList = new ArrayList<Video>();
	

	public static enum SdcardSelector
	{
		BUILT_IN, EXTERNAL
	}

	public void setSdcardItem(SdcardSelector item)
	{
		sdcardItem = item;
	}

	public static String getPhotoPath()
	{
		String absolutePath = null;
		try
		{
			String sdCardDir;
			if (sdcardItem == SdcardSelector.BUILT_IN)
			{
				sdCardDir = SdCardUtils.getFirstExternPath();
			} else
			{
				sdCardDir = SdCardUtils.getSecondExternPath();
				if (sdCardDir == null)
					return null;
			}
			String photoPath = sdCardDir + "/" + PARENTFOLDER + "/" + PHOTOS + "/";
			File folder = new File(photoPath);
			if (!folder.exists())
			{
				folder.mkdirs();
			}

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
			// SimpleDateFormat format = new
			// SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
			long time = System.currentTimeMillis();
			Date curDate = new Date(time);
			String timeString = format.format(curDate);

			File savePhoto = new File(photoPath + timeString + ".jpg");

			absolutePath = savePhoto.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return absolutePath;
	}

	/**
	 * return video path, if the video is not exist, then create it
	 * 
	 * @param parentFolder
	 *            like:DCIM/VIDEO
	 * @param videoName
	 *            like:VIDEO1.AVI
	 * @return
	 */
	public String getVideoPath(String parentFolder, String videoName)
	{
		String absolutePath = null;
		try
		{
			String sdCardDir;
			if (sdcardItem == SdcardSelector.BUILT_IN)
			{
				sdCardDir = SdCardUtils.getFirstExternPath();
			} else
			{
				sdCardDir = SdCardUtils.getSecondExternPath();
				if (sdCardDir == null)
					return null;
			}
			String videoPath = sdCardDir + "/" + parentFolder + "/";
			File folder = new File(videoPath);
			if (!folder.exists())
			{
				folder.mkdirs();
			}
			File saveVideo = new File(videoPath + videoName);
			if (!saveVideo.exists())
			{
				saveVideo.createNewFile();
			}
			absolutePath = saveVideo.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return absolutePath;
	}

	/**
	 * get video name without any params
	 * 
	 * @return video path
	 */
	public static String getVideoPath()
	{
		String absolutePath = null;
		try
		{
			String sdCardDir;
			if (sdcardItem == SdcardSelector.BUILT_IN)
			{
				sdCardDir = SdCardUtils.getFirstExternPath();
			} else
			{
				sdCardDir = SdCardUtils.getSecondExternPath();
				if (sdCardDir == null)
					return null;
			}
			String videoPath = sdCardDir + "/" + PARENTFOLDER + "/" + VIDEOS + "/";
			File folder = new File(videoPath);
			if (!folder.exists())
			{
				folder.mkdirs();
			}

			// SimpleDateFormat format = new
			// SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
			long time = System.currentTimeMillis();
			Date curDate = new Date(time);
			String timeString = format.format(curDate);

			File saveVideo = new File(videoPath + timeString + ".mp4");
			// if (!saveVideo.exists())
			// {
			// saveVideo.createNewFile();
			// }
			absolutePath = saveVideo.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return absolutePath;
	}
	/**
	 * get video name without any params
	 * 
	 * @return avi video path
	 */
	public static String getAVIPath()
	{
		String absolutePath = null;
		try
		{
			String sdCardDir;
			if (sdcardItem == SdcardSelector.BUILT_IN)
			{
				sdCardDir = SdCardUtils.getFirstExternPath();
			} else
			{
				sdCardDir = SdCardUtils.getSecondExternPath();
				if (sdCardDir == null)
					return null;
			}
			String videoPath = sdCardDir + "/" + PARENTFOLDER + "/" + VIDEOS + "/";
			File folder = new File(videoPath);
			if (!folder.exists())
			{
				folder.mkdirs();
			}

			// SimpleDateFormat format = new
			// SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
			long time = System.currentTimeMillis();
			Date curDate = new Date(time);
			String timeString = format.format(curDate);

			File saveVideo = new File(videoPath + timeString + ".avi");
			absolutePath = saveVideo.getAbsolutePath();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return absolutePath;
	}

	/**
	 * return the sdcard path
	 * 
	 * @return
	 */
	public static String getRootPath()
	{
		String sdCardDir;
		if (sdcardItem == SdcardSelector.BUILT_IN)
		{
			sdCardDir = SdCardUtils.getFirstExternPath();
		} else
		{
			sdCardDir = SdCardUtils.getSecondExternPath();
			if (sdCardDir == null)
				return null;
		}

		return sdCardDir;
	}

	/**
	 * save photos use bytes stream
	 * 
	 * @param parentFolder
	 *            like:Photo
	 * @param photoName
	 *            like:IMAGE1.JPG
	 * @param imagedata
	 *            image bytes stream data
	 */

	public static void savePhoto(Context context, String parentFolder, String photoName, byte[] imagedata)
	{
		String sdCardDir = getRootPath();
		if (sdCardDir != null)
		{
			try
			{
				String photoPath = sdCardDir + "/" + parentFolder + "/";
				File folder = new File(photoPath);
				if (!folder.exists())
				{
					folder.mkdirs();
				}
				File savePhoto = new File(photoPath, photoName);
				if (!savePhoto.exists())
				{
					savePhoto.createNewFile();
				}
				String absolutePath = savePhoto.getAbsolutePath();
				Log.e("PathConfig","path="+ absolutePath);

				FileOutputStream fout;

				fout = new FileOutputStream(absolutePath);

				fout.write(imagedata, 0, imagedata.length);
				fout.close();

				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				//Uri uri = path2uri(context, Uri.fromFile(new File(photoPath + photoName)));
				Uri uri = path2uri(context, Uri.fromFile(new File(absolutePath)));
				Log.e("Display Activity", "uri  " + uri.toString());
				intent.setData(uri);
				context.sendBroadcast(intent);
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * save photos use bitmap
	 * 
	 * @param parentFolder
	 * @param photoName
	 * @param bmp
	 *            bitmap data
	 */
	public static void savePhoto(Context context, Bitmap bmp)
	{
		String sdCardDir = getRootPath();
		if (sdCardDir != null)
		{
			try
			{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());
				long time = System.currentTimeMillis();
				Date curDate = new Date(time);
				String timeString = format.format(curDate);

				String photoPath = sdCardDir + "/" + PARENTFOLDER + "/" + PHOTOS;
				File folder = new File(photoPath);
				if (!folder.exists())
				{
					folder.mkdirs();
				}
				String photoName = timeString + ".jpg";
				File savePhoto = new File(photoPath, photoName);
				if (!savePhoto.exists())
				{
					savePhoto.createNewFile();
				}
				String absolutePath = savePhoto.getAbsolutePath();
				Log.e("path", absolutePath);

				FileOutputStream fout;

				fout = new FileOutputStream(absolutePath);

				bmp.compress(CompressFormat.JPEG, 80, fout);

				fout.close();

				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				//Uri uri = path2uri(context, Uri.fromFile(new File(photoPath + photoName)));
				Uri uri = path2uri(context, Uri.fromFile(new File(absolutePath)));
				Log.e("Display Activity", "uri  " + uri.toString());
				intent.setData(uri);
				context.sendBroadcast(intent);
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("DefaultLocale")
	public static List<Photo> getImagesList(final File photoPath) {
		List<Photo> photoList = new ArrayList<Photo>();

		FileFilter filter = new FileFilter()

		{
			@Override
			public boolean accept(File file) {
				if (file.isFile()
						&& (file.getAbsolutePath().toLowerCase()
								.endsWith(".bmp")
								|| file.getAbsolutePath().toLowerCase()
										.endsWith(".jpg") || file
								.getAbsolutePath().toLowerCase()
								.endsWith(".png"))) {
					return true;
				} else
					return false;
			}
		};

		File[] filterFiles = photoPath.listFiles(filter);
		/******* 这些代码是排序 *********/

		// 按文件名
		// List fileList = Arrays.asList(filterFiles);
		// Collections.sort(fileList, new Comparator<File>() {
		// @Override
		// public int compare(File o1, File o2) {
		// if (o1.isDirectory() && o2.isFile()) {
		// return -1;
		// }
		// if (o1.isFile() && o2.isDirectory()) {
		// return 1;
		// }
		// return o1.getName().compareTo(o2.getName());
		// }
		// });

		if (filterFiles != null) {
			// 按时间日期
			Arrays.sort(filterFiles, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff > 0)
						return -1;
					else if (diff == 0)
						return 0;
					else
						return 1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
				}

				public boolean equals(Object obj) {
					return true;
				}

			});
		}

		/**********************/
		if (null != filterFiles && filterFiles.length > 0) {

			for (File file : filterFiles) {
				// 这边对文件进行过滤
				if (photoList.indexOf(file.getAbsolutePath()) == -1) {
					// Log.e(Tag, file.getAbsolutePath());
					Photo photo = new Photo(file.getAbsolutePath());
					photoList.add(photo);
				}

			}
		}
		return photoList;
	}

	public static List<Video> getVideosList(final File videoPath)
	{
		videoList.clear();
		// getVideoList(videoPath);
		getVideoListNew(videoPath);
		return videoList;
	}

	/**
	 * New Method:新方法不需要再录像前先拍照作为缩略图
	 * 
	 * @param videoPath
	 */
	@SuppressLint("DefaultLocale")
	private static void getVideoListNew(final File videoPath) {
		List<String> temp = new ArrayList<String>();
		File[] files = videoPath.listFiles();
		// //按文件名
		// List fileList = Arrays.asList(files);
		// Collections.sort(fileList, new Comparator<File>() {
		// @Override
		// public int compare(File o1, File o2) {
		// if (o1.isDirectory() && o2.isFile()) {
		// return -1;
		// }
		// if (o1.isFile() && o2.isDirectory()) {
		// return 1;
		// }
		// return o1.getName().compareTo(o2.getName());
		// }
		// });

		if (files != null) {
			// 按时间日期
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff > 0)
						return -1;
					else if (diff == 0)
						return 0;
					else
						return 1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
				}

				public boolean equals(Object obj) {
					return true;
				}

			});
		}
		/**********************/

		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					if (files[i].getAbsolutePath().toLowerCase()
							.endsWith(".avi")
							|| files[i].getAbsolutePath().toLowerCase()
									.endsWith(".3gp")
							|| files[i].getAbsolutePath().toLowerCase()
									.endsWith(".mp4")) {
						String absPath = files[i].getAbsolutePath();

						File videoFile = new File(absPath);
						if (videoFile.exists()) {
							if (temp.indexOf(videoFile.getAbsolutePath()) == -1) {
								temp.add(videoFile.getAbsolutePath());
								// videoList.add(videoFile.toString());
								Video video = new Video(
										videoFile.getAbsolutePath());
								videoList.add(video);
							}
						}
					}

				} else {
					if (files[i].isDirectory()
							&& files[i].getPath().indexOf("/.") == -1) {
						getVideoListNew(files[i]);
					}
				}
			}
		}
	}

	/**
	 * Old Method:老方法是在录像前拍照作为视频缩略图，这样的话要将视频地址替换为其照片地址来显示
	 * 
	 * @param videoPath
	 */
	@SuppressLint("DefaultLocale")
	private static void getVideoList(final File videoPath)
	{
		List<String> temp = new ArrayList<String>();
		File[] files = videoPath.listFiles();
		if (files != null && files.length > 0)
		{
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isFile())
				{
					if (files[i].getAbsolutePath().toLowerCase().endsWith(".avi")
							|| files[i].getAbsolutePath().toLowerCase().endsWith(".3gp")
							|| files[i].getAbsolutePath().toLowerCase().endsWith(".mp4"))
					{
						String lcPath = files[i].getAbsolutePath().toLowerCase();
						String absPath = files[i].getAbsolutePath();
						String photopath = null;
						if (lcPath.contains(".avi"))
						{
							photopath = absPath.replace(".avi", ".jpg");
						} else if (lcPath.contains(".mp4"))
						{
							photopath = absPath.replace(".mp4", ".jpg");
						} else if (lcPath.contains(".3gp"))
						{
							photopath = absPath.replace(".3gp", ".jpg");
						}
						File photofile = new File(photopath);
						if (photofile.exists())
						{
							if (temp.indexOf(photofile.getAbsolutePath()) == -1)
							{
								temp.add(photofile.getAbsolutePath());
								//videoList.add(photofile.toString());
								Video video = new Video(
										photofile.getAbsolutePath());
								videoList.add(video);
							}
						}
					}

				} else
				{
					if (files[i].isDirectory() && files[i].getPath().indexOf("/.") == -1)
					{
						getVideoList(files[i]);
					}
				}
			}
		}
	}
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
	{
		Cursor cursor = null;
		String column = Images.Media.DATA;
		String[] projection = { column };
		try
		{
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst())
			{
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri)
	{
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri)
	{
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri)
	{
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri)
	{
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	@SuppressLint("NewApi")
	public static String uri2path(Context context, Uri uri)
	{
		if (context == null || uri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, uri))
		{
			if (isExternalStorageDocument(uri))
			{
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type))
				{
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(uri))
			{
				String id = DocumentsContract.getDocumentId(uri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri))
			{
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type))
				{
					contentUri = Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type))
				{
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type))
				{
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme()))
		{
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme()))
		{
			return uri.getPath();
		}
		return null;
	}
	public static Uri path2uri(Context context, Uri uri)
	{
		if (uri.getScheme().equals("file"))
		{
			String path = uri.getEncodedPath();
			// Log.d("", "path1 is " + path);
			if (path != null)
			{
				path = Uri.decode(path);
				// Log.d("", "path2 is " + path);
				ContentResolver cr = context.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID },
						buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext())
				{
					index = cur.getColumnIndex(Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0)
				{
					// do nothing
				} else
				{
					Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
					Log.d("", "uri_temp is " + uri_temp);
					if (uri_temp != null)
					{
						uri = uri_temp;
					}
				}
			}
		}
		return uri;
	}

	public int getSdcardAvilibleSize()
	{
		String sdCardDir = getRootPath();
		StatFs stat = new StatFs(new File(sdCardDir).getPath());
		/* 获取block的SIZE */
		long blockSize = stat.getBlockSize();
		/* 空闲的Block的数量 */
		long availableBlocks = stat.getAvailableBlocks();
		/* 返回bit大小值 */
		return (int) (availableBlocks * blockSize / 1024 / 1024);
	}

	public int getSdcardTotalSize()
	{
		String sdCardDir = getRootPath();
		StatFs stat = new StatFs(new File(sdCardDir).getPath());
		/* 获取block的SIZE */
		long blockSize = stat.getBlockSize();
		/* 空闲的Block的数量 */
		long blockCount = stat.getBlockCount();
		/* 返回bit大小值 */
		return (int) (blockCount * blockSize / 1024 / 1024);
	}

	/** 把录像按修改时间的先后排序 */
	public List<File> sortVideoList(List<File> photoList)
	{
		Collections.sort(photoList, new Comparator<File>()
		{

			@Override
			public int compare(File curFile, File nextFile)
			{
				// TODO Auto-generated method stub
				long firstDate = curFile.lastModified();
				long nextDate = nextFile.lastModified();
				return (firstDate > nextDate) ? 1 : -1; // 若大于，即后修改，返回1，即按修改时间顺序
			}
		});
		return photoList;
	}

	/** delete all the files in the folder and it's sub folders */
	public static void deleteFiles(File file)
	{
		if (file.exists())
		{
			if (file.isFile())
			{
				file.delete();
			} else if (file.isDirectory())
			{
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					deleteFiles(files[i]);
				}
			}
			file.delete();
		}
	}
}
