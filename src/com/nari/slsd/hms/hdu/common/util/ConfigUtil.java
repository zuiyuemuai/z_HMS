package com.nari.slsd.hms.hdu.common.util;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//配置文件
public class ConfigUtil
{
	// 配置文件的位置
	public static final String PROPPATH = "conf/HMS_ClientChartCfg/SystemCfg.properties";

	public static final String KEY_TEXT_FONT = "textfont";
	public static final String KEY_TEXT_FONT_STYLE = "textfontstyle";
	public static final String KEY_TEXT_FONT_SIZE = "textfontsize";
	
	public static final String KEY_TITLE_FONT = "titlefont";
	public static final String KEY_TITLE_FONT_STYLE = "titlefontstyle";
	public static final String KEY_TITLE_FONT_SIZE = "titlefontsize";

	public static final String KEY_MENU_FONT = "menufont";
	public static final String KEY_MENU_FONT_STYLE = "menufontstyle";
	public static final String KEY_MENU_FONT_SIZE = "menufontsize";
	
	public static final String KEY_WORKSPACE_SELECTPATH = "selectworkspacepath";
	public static final String KEY_WORKSPACE_ORIGINPATH = "originworkspacepath";

	public static final String KEY_SELECT_PATH = "SELECT_PATH";// 在分拣中选择路径保存
	public static final String KEY_SELECT_SAVEPATH = "SELECT_SAVEPATH";// 在分拣中保存路径

	public static final String KEY_ServerNode_PATH = "ServerNode";
	public static final String KEY_ServerName_PATH = "ServerName";

	// 在线工况图的数据来源
	public static final String KEY_ONLINEPOWEREGGGRAHP_DATASOUCE = "OnlinePowerEggGrahp_DataSource";
	
	//语言后缀
	public static final String KEY_LANGUAGE_SUFFIX = "LangageSuffix";
	
	public static final String KEY_WORKTYPES = "workTypes";

	public static final String KEY_LINECOLOR = "lineColor";
	public static final String KEY_CONNECTCOLOR = "connectColor";
	public static final String KEY_CASCADCOLOR = "connectColor";

	public static final String KEY_READFROMLOCAL = "readFromLocal";

	public static final String KEY_ONLINE_SWITCH = "onLineSwitch";
	public static final String KEY_SERVERNODE_SWITCH = "serverNodeSwitch";

	public static final String KEY_MULTI_COLOR = "multiColor";//多种颜色
	
	public static final String KEY_SELECTDIV = "selectDIV";//分拣时显示数量的分频数，如果为3则表示 每3个点取一个数据
	
	
	
	
	public static void main(String[] args)
	{
		//clearProperies(PROPPATH);
		System.out.println("清除所有配置");
		HashMap<String, String> keyValueMap = new HashMap<String, String>();
		keyValueMap.put(KEY_TEXT_FONT, "微软雅黑");// 普通文本的字体
		keyValueMap.put(KEY_TEXT_FONT_STYLE, "" + Font.BOLD);// 普通文本的风格
		keyValueMap.put(KEY_TEXT_FONT_SIZE, "12");// 普通文本的字体大小
		
		keyValueMap.put(KEY_TITLE_FONT, "微软雅黑"); // 标题的字体
		keyValueMap.put(KEY_TITLE_FONT_STYLE, "" + Font.BOLD);// 普通文本的风格
		keyValueMap.put(KEY_TITLE_FONT_SIZE, "18");// 标题的字体大小
		
		keyValueMap.put(KEY_MENU_FONT, "微软雅黑"); // 标题的字体
		keyValueMap.put(KEY_MENU_FONT_STYLE, "" + Font.BOLD);// 普通文本的风格
		keyValueMap.put(KEY_MENU_FONT_SIZE, "14");// 标题的字体大小
		
		
		
		keyValueMap.put(KEY_ONLINEPOWEREGGGRAHP_DATASOUCE, "0");
		keyValueMap.put(KEY_LANGUAGE_SUFFIX, "zh_CN");
		keyValueMap.put(KEY_WORKTYPES, "未知工况,黑匣子,解列运行,自动开机过程,手动开机过程,空转过程,空载过程," +
				"自动停机过程,紧急停机过程,定时试验工况,并网运行,负载稳定运行,有功增负荷,有功减负荷,无功加负荷(增磁),无功减负荷(减磁)," +
				"调相,整周期试验工况,停机备用," +
				"开机准备,检修态");
		
		keyValueMap.put(KEY_LINECOLOR, "0,0,139");
		keyValueMap.put(KEY_CONNECTCOLOR, "64,224,208");

		keyValueMap.put(KEY_CASCADCOLOR, "186,85,211");
		keyValueMap.put(KEY_READFROMLOCAL, "1");
		keyValueMap.put(KEY_SELECTDIV, "3");

		keyValueMap.put(KEY_ONLINE_SWITCH, "1");
		keyValueMap.put(KEY_SERVERNODE_SWITCH, "1");

		keyValueMap.put(KEY_MULTI_COLOR, "100,211,222_100,211,222_100,211,222_100,211,222");
		
		
		ConfigUtil.updateProperties(PROPPATH, keyValueMap);
		System.out.println("配置完成");
	}

	// 获取数据,使用默认路径
	// 没有则返回null
	public static String getPropertiesValue(String key)
	{
		return (String) getProperties(PROPPATH).get(key);
	}

	// 清除所有配置信息
	private static void clearProperies(String filePath)
	{
		Properties props = new Properties();
		BufferedWriter bw = null;
		try
		{
			filePath = URLDecoder.decode(filePath, "utf-8");
			// 写入属性文件
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "UTF-8"));
			props.clear();
			props.store(bw, "");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// 获取配置资源
	public static Properties getProperties(String path)
	{
		Properties props = new Properties();

		try
		{
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					path), "UTF-8");
			BufferedReader reader = new BufferedReader(read);

			props.load(reader);
			return props;
		} catch (IOException e)
		{
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static void updateProperties(String key, String value)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		updateProperties(map);
	}

	public static void updateProperties(Map<String, String> keyValueMap)
	{
		updateProperties(PROPPATH, keyValueMap);
	}

	/**
	 * 传递键值对的Map，更新properties文件
	 * 
	 * @param fileName
	 *            文件名(放在resource源包目录下)，需要后缀
	 * @param keyValueMapx
	 *            键值对Map
	 */
	public static void updateProperties(String filePath,
			Map<String, String> keyValueMap)
	{
		Properties props = null;
		BufferedWriter bw = null;

		try
		{
			filePath = URLDecoder.decode(filePath, "utf-8");
			props = getProperties(filePath);

			// 写入属性文件
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "utf-8"));

			// props.clear();// 清空旧的文件

			for (String key : keyValueMap.keySet())
				// 直接更改,如果有相同的key则覆盖
				props.setProperty(key, keyValueMap.get(key));

			props.store(bw, "");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
