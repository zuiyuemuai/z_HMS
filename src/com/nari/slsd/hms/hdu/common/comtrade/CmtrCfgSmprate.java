package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.Serializable;

public class CmtrCfgSmprate implements Serializable {
	private float rate;
	private int point;
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
}
