package com.nari.slsd.hms.hdu.common.util;

import java.awt.Color;
import java.util.logging.Level;

public class ExtColor
{
	public static Color LightPink = new Color(255, 182, 193);// 浅粉色
	public static Color Crimson = new Color(220, 20, 60);// 猩红
	public static Color MediumOrchid = new Color(186, 85, 211);// 适中的兰花紫
	public static Color LightSkyBlue = new Color(135, 206, 250);// 淡蓝色

	public static Color DarkBlue = new Color(0, 0, 139);// 深蓝色
	public static Color LightSteelBlue = new Color(176, 196, 222);// 淡钢蓝
	public static Color Turquoise = new Color(64, 224, 208);// 绿宝石
	public static Color LemonChiffon = new Color(255, 250, 205);// 柠檬薄纱
	public static Color LawnGreen = new Color(124, 252, 0);// 草坪绿

	public static Color LineColor = null;// 图元曲线显示的颜色
	public static Color connectColor = null;// 主要用于三维姿态图平面之间连接线的颜色
	public static Color CascadColor = null;// 主要用于三维姿态图平面之间连接线的颜色

	// 获取级联线颜色
	public static Color getCascadColor()
	{
		if (null == CascadColor)
		{
			String value = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_CASCADCOLOR);
			if (null == value)
			{
				LoggerUtil.log(Level.WARNING, ConfigUtil.KEY_CASCADCOLOR
						+ " not exit, using LightSteelBlue");
				CascadColor = LightSteelBlue;
			} else
			{
				String[] line = value.split(",");
				CascadColor = new Color(new Integer(line[0]), new Integer(
						line[1]), new Integer(line[2]));
			}

		}
		return LineColor;

	}

	public static Color[] getColors()
	{

		if (null == colors)
		{
			String value = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_MULTI_COLOR);
			if (null == value)
			{
				LoggerUtil.log(Level.WARNING, ConfigUtil.KEY_MULTI_COLOR
						+ " not exit, using LightSteelBlue");

				colors = new Color[] { ExtColor.getLineColor(), Color.blue,
						Color.darkGray, Color.magenta, new Color(0, 0, 255),
						new Color(125, 0, 50), new Color(56, 45, 60),
						new Color(89, 79, 70), new Color(46, 98, 80),
						new Color(98, 45, 90), new Color(20, 45, 60) };

			} else
			{
				String[] line = value.split("_");
				colors = new Color[line.length];

				for (int i = 0; i < line.length; i++)
				{
					String[] colorStrings = line[i].split(",");
					colors[i] = new Color(new Integer(colorStrings[0]),
							new Integer(colorStrings[1]), new Integer(
									colorStrings[2]));
				}

			}

		}
		return colors;
	}

	public static Color getLineColor()
	{
		if (null == LineColor)
		{
			String value = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_LINECOLOR);
			if (null == value)
			{
				LoggerUtil.log(Level.WARNING, ConfigUtil.KEY_LINECOLOR
						+ " not exit, using LightSteelBlue");
				LineColor = LightSteelBlue;
			} else
			{
				String[] line = value.split(",");
				LineColor = new Color(new Integer(line[0]),
						new Integer(line[1]), new Integer(line[2]));
			}

		}
		return LineColor;

	}

	public static Color getConnectColor()
	{
		if (null == connectColor)
		{
			String value = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_CONNECTCOLOR);
			if (null == value)
			{
				LoggerUtil.log(Level.WARNING, ConfigUtil.KEY_CONNECTCOLOR
						+ " not exit, using LightSteelBlue");
				connectColor = LightSteelBlue;
			} else
			{
				String[] line = value.split(",");
				connectColor = new Color(new Integer(line[0]), new Integer(
						line[1]), new Integer(line[2]));
			}

		}
		return connectColor;

	}

	// 颜色集合
	public static Color[] colors;

}
