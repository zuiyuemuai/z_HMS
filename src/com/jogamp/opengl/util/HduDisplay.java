package com.jogamp.opengl.util;

import javax.media.opengl.GLAutoDrawable;

public class HduDisplay extends FPSAnimator
{

	
	public HduDisplay(GLAutoDrawable arg0)
	{
		super(arg0, 30, true);
		// TODO Auto-generated constructor stub
	}

	//刷新一次画布
	public void hduDisplay()
	{
		display();
	}

}
