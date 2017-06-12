package com.jogamp.opengl.util;

import javax.media.opengl.GLCapabilitiesImmutable;

import org.jzy3d.chart.Chart;
import org.jzy3d.global.Settings;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class HduJzyChart extends Chart
{
	
	public HduJzyChart()
	{
		super();
	}
	Animator animator;
	
	protected ICanvas initializeCanvas(Scene scene, Quality quality, String chartType)
	{
		HduCanvasAWT awt = new HduCanvasAWT(scene, quality, capabilities);
		animator = awt.getAnimator();
		return awt;
	}
	
	// 刷新一次画布
	public void hduDisplay()
	{
		animator.display();
		System.out.println("HduJzyChart.hduDisplay()");
	}
	
	class HduCanvasAWT extends CanvasAWT
	{
		public Animator getAnimator()
		{
			return animator;
		}
		public HduCanvasAWT(Scene scene, Quality quality)
		{
			this(scene, quality, ((GLCapabilitiesImmutable) (Settings.getInstance().getGLCapabilities())));
		}

		public HduCanvasAWT(Scene scene, Quality quality, GLCapabilitiesImmutable glci)
		{
			this(scene, quality, glci, false, false);
		}

		public HduCanvasAWT(Scene scene, Quality quality, GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL)
		{
			super(scene, quality, glci, traceGL, debugGL);
		}
		
	}

}
