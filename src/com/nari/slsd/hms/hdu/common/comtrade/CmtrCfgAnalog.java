package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.Serializable;

public class CmtrCfgAnalog implements Serializable
{

	private int index;
	private String name;
	private String phase;
	private String element;
	private String unit;
	private float factorA;
	private float factorB;
	private int offsetTime;
	private int smpMin;
	private int smpMax;
	private float primary;
	private float secondary;
	private String ps;

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhase()
	{
		return phase;
	}

	public void setPhase(String phase)
	{
		this.phase = phase;
	}

	public String getElement()
	{
		return element;
	}

	public void setElement(String element)
	{
		this.element = element;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public float getFactorA()
	{
		return factorA;
	}

	public void setFactorA(float factorA)
	{
		this.factorA = factorA;
	}

	public float getFactorB()
	{
		return factorB;
	}

	public void setFactorB(float factorB)
	{
		this.factorB = factorB;
	}

	public int getOffsetTime()
	{
		return offsetTime;
	}

	public void setOffsetTime(int offsetTime)
	{
		this.offsetTime = offsetTime;
	}

	public int getSmpMin()
	{
		return smpMin;
	}

	public void setSmpMin(int smpMin)
	{
		this.smpMin = smpMin;
	}

	public int getSmpMax()
	{
		return smpMax;
	}

	public void setSmpMax(int smpMax)
	{
		this.smpMax = smpMax;
	}

	public float getPrimary()
	{
		return primary;
	}

	public void setPrimary(float primary)
	{
		this.primary = primary;
	}

	public float getSecondary()
	{
		return secondary;
	}

	public void setSecondary(float secondary)
	{
		this.secondary = secondary;
	}

	public String getPs()
	{
		return ps;
	}

	public void setPs(String ps)
	{
		this.ps = ps;
	}
}
