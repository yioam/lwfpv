package com.klh.lwsample.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.klh.lwsample.R;


public class Media
{	
	private SoundPool mSoundPool;

	public Media(Context context)
	{	
		mSoundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
		//微调两边加减的声音
		mSoundPool.load(context, R.raw.tweet_sent, 1);
		//微调中间的声音
		mSoundPool.load(context, R.raw.short_double_high, 2);
		//拍照的声音
		mSoundPool.load(context, R.raw.photo_shutter, 3);
		
		
	}

	public void playShutter()
	{
		mSoundPool.play(3, 1, 1, 3, 0, 1);
	}

	
	public void playBtnTurn()
	{
		mSoundPool.play(1, 1, 1, 1, 0, 1);
	}
	
	public void playBtnMiddle()
	{
		mSoundPool.play(2, 1, 1, 2, 0, 1);
	}
	
	
}
