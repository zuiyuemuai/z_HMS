package com.nari.slsd.hms.hdu.common.util;

import java.awt.Font;
import java.util.logging.Level;

public class ConfigHelp
{
	private static Font titleFont;

	public static Font textfont;

	public static Font menuFont;

	public static Font getTitileFont()
	{
		if (null == titleFont)
		{
			String font = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_TITLE_FONT);
			if (null == font)
			{
				font = "微软雅黑";
			}
			String style = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_TITLE_FONT_STYLE);
			if (null == style)
			{
				style = "" + Font.BOLD;
			}
			String size = ConfigUtil
					.getPropertiesValue(ConfigUtil.KEY_TITLE_FONT_SIZE);
			if (null == size)
			{
				size = "14";
			}
			LoggerUtil.log(Level.INFO, "title using " + font + style + size);
			titleFont = new Font(font, Integer.parseInt(style),
					Integer.parseInt(size));

		}

		return titleFont;
	}

	public static Font getTextFont()
	{
		if (null == textfont)
		{
			textfont = new Font(
					ConfigUtil.getPropertiesValue(ConfigUtil.KEY_TEXT_FONT),
					Integer.parseInt(ConfigUtil
							.getPropertiesValue(ConfigUtil.KEY_TEXT_FONT_STYLE)),
					Integer.parseInt(ConfigUtil
							.getPropertiesValue(ConfigUtil.KEY_TEXT_FONT_SIZE)));

		}

		return textfont;
	}

	public static Font getMenuFont()
	{
		if (null == menuFont)
		{
			menuFont = new Font(
					ConfigUtil.getPropertiesValue(ConfigUtil.KEY_MENU_FONT),
					Integer.parseInt(ConfigUtil
							.getPropertiesValue(ConfigUtil.KEY_MENU_FONT_STYLE)),
					Integer.parseInt(ConfigUtil
							.getPropertiesValue(ConfigUtil.KEY_MENU_FONT_SIZE)));

		}

		return menuFont;
	}

	public static boolean getOnlineSwitch()
	{
		String value = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_ONLINE_SWITCH);
		if (null == value)
		{
			return true;
		}

		if (value.equals("1"))
		{
			return true;
		} else
		{
			return false;
		}

	}
	public static boolean getServerNodeSwitch()
	{
		String value = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_SERVERNODE_SWITCH);
		if (null == value)
		{
			return true;
		}
		
		if (value.equals("1"))
		{
			return true;
		} else
		{
			return false;
		}
	}
}
