package com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

public class ProgressThread
{

	static int value = 0;

	static class BarThread extends Thread
	{
		private static int DELAY = 500;
		JProgressBar progressBar;

		public BarThread(JProgressBar bar)
		{
			progressBar = bar;
		}

		public void run()
		{
			int minimum = progressBar.getMinimum();
			int maximum = progressBar.getMaximum();
			Runnable runner = new Runnable()
			{
				public void run()
				{

					progressBar.setValue(value);
				}
			};
			for (int i = minimum; i < maximum; i++)
			{
				try
				{
					SwingUtilities.invokeAndWait(runner);
					// Our task for each step is to just sleep
					Thread.sleep(DELAY);
				} catch (InterruptedException ignoredException)
				{
				} catch (InvocationTargetException ignoredException)
				{
				}
			}
		}
	}
	
	public void setValue(int val){
		value = val;
	}
	
	public void Finish(){
		jd.dispose();
	}

	JDialog jd;
	
	public void Initialize()
	{
		// Initialize
		final JProgressBar aJProgressBar = new JProgressBar(0, 100);
		final JButton aJButton = new JButton("test");

		aJProgressBar.setStringPainted(true); // 显示百分比字符
		aJProgressBar.setIndeterminate(false); // 不确定的进度条
		Thread stepper = new BarThread(aJProgressBar);
		stepper.start();
		jd = new JDialog();
		Container contentPane = jd.getContentPane();
		contentPane.setLayout(new GridLayout(2, 1));
		contentPane.add(aJProgressBar);
		contentPane.add(aJButton);
		jd.setSize(300, 100);
		jd.setVisible(true);
	    int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	    int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	    jd.setTitle("正在导出");
	    jd.setLocation(width/2-100, height/2-100);

	}

	public static void main(String[] args)
	{
		ProgressThread pg = new ProgressThread();
		pg.Initialize();
		for (int i = 0; i < 100; i++)
		{
			pg.setValue(i);
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}