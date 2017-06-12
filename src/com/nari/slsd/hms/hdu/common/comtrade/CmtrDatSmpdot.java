package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.Serializable;

public class CmtrDatSmpdot implements Serializable
{
	private int index;
	private int time;
	private int[] analogs;
	private int[] digits;

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}

	public int[] getAnalogs()
	{
		return analogs;
	}

	public void setAnalogs(int[] analogs)
	{
		this.analogs = analogs;
	}

	public int[] getDigits()
	{
		return digits;
	}

	public void setDigits(int[] digits)
	{
		this.digits = digits;
	}

}
