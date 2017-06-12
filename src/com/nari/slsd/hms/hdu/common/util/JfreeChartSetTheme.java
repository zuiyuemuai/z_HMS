package com.nari.slsd.hms.hdu.common.util;

import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

//设置Chart的主题
public class JfreeChartSetTheme
{
	public static void set()
	{
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 18));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋体", Font.BOLD, 16));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
	}
}
