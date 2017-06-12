package com.nari.slsd.hms.hdu.common.data;

import java.awt.Color;

public class Floatxy 
{

	public float x;
	public float y;
	public Color color;
	
	public Floatxy(float x, float y, Color c)
	{
		this.x = x;
		this.y = y;
		color = c;
	}
	
	public static void copy(Floatxy from, Floatxy to)
	{
		to.x = from.x;
		to.y = from.y;
		to.color = from.color;
	}

}
