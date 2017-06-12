package com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressDialog
{
	private JProgressBar aJProgressBar = null;
	private JDialog jd = null;

	public ProgressDialog()
	{
		aJProgressBar = new JProgressBar(0, 100);
		aJProgressBar.setStringPainted(true); // 显示百分比字符
		aJProgressBar.setIndeterminate(false); // 不确定的进度条
		aJProgressBar.setValue(0);

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		jd = new JDialog();
		jd.setTitle("正在导出");
		jd.setLocation(width / 2 - 100, height / 2 - 100);
		jd.setSize(200, 70);
		jd.setVisible(true);
		jd.add(aJProgressBar);
	}

	public void setValue(int val)
	{
		final int i = val;
		Dimension d = aJProgressBar.getSize();
		Rectangle rect = new Rectangle(0,0, d.width, d.height);
		aJProgressBar.setValue(i);
		aJProgressBar.paintImmediately(rect);
	}

	public void Finish()
	{
		jd.dispose();
	}

	public static void main(String[] args)
	{
		ProgressDialog progressDialog = new ProgressDialog();
	}
}
