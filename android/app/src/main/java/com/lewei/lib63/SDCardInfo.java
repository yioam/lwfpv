package com.lewei.lib63;

public class SDCardInfo
{
	public static final byte FHNPEN_SDCardState_FOUND = (1 << 0);
	public static final byte FHNPEN_SDCardState_LOADED = (1 << 1);
	public static final byte FHNPEN_SDCardState_NORMAL = (1 << 2);
	public static final byte FHNPEN_SDCardState_FORMATING = (1 << 3);

	public byte state;
	public long totalSize;
	public long usedSize;
	public int formatState;
	public int formatProgress;

}
