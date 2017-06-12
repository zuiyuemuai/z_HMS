package com.nari.slsd.hms.hdu.common.util;

import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GridBag布局的工具类
 * 
 * @author Administrator
 * 
 */
public class GridBagUtil
{

	/**
	 * 添加一个空白的控件
	 * @param component
	 *            控件
	 * @param jPanel
	 *            要添加到的Jpanel
	 * @param gridex
	 *            位置x
	 * @param gridey
	 *            位置y
	 * @param weightx
	 *            在x方向的比重
	 * @param weighty
	 *            在y方向的比重 gridwidth 组件显示所占的行列数
	 */
	public static void addBlankJLabel(JPanel jPanel, int gridex, int gridey,
			double weightx, double weighty)
	{

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		JLabel tagLbl = new JLabel(" ");
		c.gridx = gridex; // x grid position
		c.gridy = gridey; // y grid position
		c.weightx = weightx;
		c.weighty = weighty;
		jPanel.add(tagLbl, c);

	}

	/**
	 * 设置一个控件的位置
	 * 
	 * @param component
	 *            控件
	 * @param jPanel
	 *            要添加到的Jpanel
	 * @param gridex
	 *            位置x
	 * @param gridey
	 *            位置y
	 * @param weightx
	 *            在x方向的比重
	 * @param weighty
	 *            在y方向的比重 gridwidth 组件显示所占的行列数
	 * 
	 */
	public static void setLocation(JPanel jPanel, Component component,
			int gridex, int gridey, double weightx, double weighty,
			int gridwidth, int gridheight, boolean isboth)
	{
		GridBagConstraints c = new GridBagConstraints();

		if (isboth)
			c.fill = GridBagConstraints.BOTH;
		c.gridx = gridex;
		c.gridy = gridey;
		c.weightx = weightx;
		c.weighty = weighty;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		jPanel.add(component, c);
	}

	// 默认 1 1
	public static void setLocation(JPanel jPanel, Component component,
			int gridex, int gridey, double weightx, double weighty,
			boolean isboth)
	{
		GridBagUtil.setLocation(jPanel, component, gridex, gridey, weightx,
				weighty, 1, 1, isboth);
	}

}
