package com.nari.slsd.hms.hdu.common.data;

import org.jfree.chart.axis.NumberAxis;

/**
 * 记录坐标轴范围
 * 
 * @author Administrator
 *
 */
public class XYaxis {
	private double LowestX;
	private double HighestX;
	private double LowestY;
	private double HighestY;

	public XYaxis(NumberAxis xnumberaxis, NumberAxis ynumberaxis) {
		// 获取最小坐标的数值
		LowestX = xnumberaxis.getRange().getLowerBound();
		// 获取最大坐标的数值
		HighestX = xnumberaxis.getRange().getUpperBound();
		LowestY = ynumberaxis.getRange().getLowerBound();
		HighestY = ynumberaxis.getRange().getUpperBound();
	}

	public double getLowestX() {
		return LowestX;
	}

	public void setLowestX(double lowestX) {
		LowestX = lowestX;
	}

	public double getLowestY() {
		return LowestY;
	}

	public void setLowestY(double lowestY) {
		LowestY = lowestY;
	}

	public double getHighestX() {
		return HighestX;
	}

	public void setHighestX(double highestX) {
		HighestX = highestX;
	}

	public double getHighestY() {
		return HighestY;
	}

	public void setHighestY(double highestY) {
		HighestY = highestY;
	}
}
