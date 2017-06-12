package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

import java.awt.Color;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 分拣区间参数的定义
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class SelectSection 
{
	public static int NothingMove = 1;
	public static int StartMove = 2;//表示start线在移动
	public static int EndMove = 3;//表示end线在移动
	
	public boolean isOk = false;// 判断是否完成
	public String name;
	public float startindex = 0;
	public float endindex = 0;
	public Color color;
	public Date startTime = null;
	public Date endTime = null;
	public int ismove = NothingMove;//是否在移动中
	
	//获取起始时间的字符串格式
	public String getStartTimeString()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(startTime);
	}
	public static String getDataString(Date d)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(d);
	}
	public String getEndTimeString()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(endTime);
	}
	
	public void setStartIndex(float index)
	{
		startindex = index;
	}
	
	
}
