package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.Serializable;
import java.util.ArrayList;

public class CmtrCfgInfo implements Serializable
{
	public String[] channelName;//每个通道的名称
	public int pointCount;//一个通道的点数
	public int channelCount;//通道数
	public float[] factorA ;
	public float[] factorB ;
	public float smprateRate;
	
	private String stationName;
	private String kymographId;
	private String revyear;
	private int analogCount;
	private int digitCount;
	private ArrayList<CmtrCfgAnalog> analogs;
	private ArrayList<CmtrCfgDigit> digits;
	private float frequency;
	private int smprateCount;
	private ArrayList<CmtrCfgSmprate> smprates;
	private String beginTime;
	private String endTime;
	private String fileType;
	

	public String getStationName()
	{
		return stationName;
	}

	public void setStationName(String stationName)
	{
		this.stationName = stationName;
	}
	public float getSmprateRate()
	{
		return smprateRate;
	}

	public String getKymographId()
	{
		return kymographId;
	}

	public void setKymographId(String kymographId)
	{
		this.kymographId = kymographId;
	}

	public String getRevyear()
	{
		return revyear;
	}

	public void setRevyear(String revyear)
	{
		this.revyear = revyear;
	}

	public int getAnalogCount()
	{
		return analogCount;
	}

	public void setAnalogCount(int analogCount)
	{
		this.analogCount = analogCount;
	}

	public int getDigitCount()
	{
		return digitCount;
	}

	public void setDigitCount(int digitCount)
	{
		this.digitCount = digitCount;
	}

	public ArrayList<CmtrCfgAnalog> getAnalogs()
	{
		return analogs;
	}

	public void setAnalogs(ArrayList<CmtrCfgAnalog> analogs)
	{
		this.analogs = analogs;
	}

	public ArrayList<CmtrCfgDigit> getDigits()
	{
		return digits;
	}

	public void setDigits(ArrayList<CmtrCfgDigit> digits)
	{
		this.digits = digits;
	}

	public float getFrequency()
	{
		return frequency;
	}

	public void setFrequency(float frequency)
	{
		this.frequency = frequency;
	}

	public int getSmprateCount()
	{
		return smprateCount;
	}

	public void setSmprateCount(int smprateCount)
	{
		this.smprateCount = smprateCount;
	}

	public ArrayList<CmtrCfgSmprate> getSmprates()
	{
		return smprates;
	}

	public void setSmprates(ArrayList<CmtrCfgSmprate> smprates)
	{
		this.smprates = smprates;
	}

	public String getBeginTime()
	{
		return beginTime;
	}

	public void setBeginTime(String beginTime)
	{
		this.beginTime = beginTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}
	

}
