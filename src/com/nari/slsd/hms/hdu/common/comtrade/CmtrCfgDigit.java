package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.Serializable;

public class CmtrCfgDigit implements Serializable
{
	private int index;
	private String name;
	private String ph;
	private String ccbm;
	private int state;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPh() {
		return ph;
	}
	public void setPh(String ph) {
		this.ph = ph;
	}
	public String getCcbm() {
		return ccbm;
	}
	public void setCcbm(String ccbm) {
		this.ccbm = ccbm;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	
}
