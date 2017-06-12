package com.nari.slsd.hms.hdu.common.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 日志输出到登陆系统用户所在的根目录的aaa的文件夹下， 并且根据系统日期命名日志文件
 * 
 */
public class LoggerUtil
{

	/** 存放的文件夹 **/
	private static String file_name = "logs/HMS_ClientChartLog";
	static Logger logger = Logger.getLogger("log");

	/**
	 * 得到要记录的日志的路径及文件名称
	 * 
	 * @return
	 */
	private static String getLogName()
	{
		StringBuffer logPath = new StringBuffer();
		// logPath.append(System.getProperty("user.home"));
		logPath.append(file_name);
		File file = new File(logPath.toString());
		if (!file.exists())
			file.mkdir();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		logPath.append("\\" + sdf.format(new Date()) + ".log");

		return logPath.toString();
	}

	/**
	 * 配置Logger对象输出日志文件路径
	 * 
	 * @param logger
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void setLogingProperties(Logger logger)
			throws SecurityException, IOException
	{
		setLogingProperties(logger, Level.ALL);
	}

	/**
	 * 配置Logger对象输出日志文件路径
	 * 
	 * @param logger
	 * @param level
	 *            在日志文件中输出level级别以上的信息
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void setLogingProperties(Logger logger, Level level)
	{
		FileHandler fh;
		try
		{
			fh = new FileHandler(getLogName(), true);
			logger.addHandler(fh);// 日志输出文件
			// logger.setLevel(level);
			fh.setFormatter(new Formatter()
			{
				@Override
				public String format(LogRecord record)
				{
					// TODO Auto-generated method stub
					Date date = new Date();
					String sDate = date.toString();
					return "[" + sDate + "]" + "[" + record.getLevel() + "]"
							+ record.getMessage() + "\n";

				}
			});// 输出格式
				// logger.addHandler(new ConsoleHandler());//输出到控制台
		} catch (SecurityException e)
		{
			logger.log(Level.SEVERE, "安全性错误", e);
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, "读取文件日志错误", e);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param level
	 *            日志严重程度
	 * @param msg
	 *            日志信息
	 * @param classname
	 *            打日子的类
	 */
	public static void log(Level level, String msg)
	{
		try
		{
			LoggerUtil.setLogingProperties(logger);

			logger.log(level, "[class]"
					+ Thread.currentThread().getStackTrace()[2].getFileName() + ":"
					+ Thread.currentThread().getStackTrace()[2].getLineNumber()
					+ "  [msg]" + msg + "\n");

		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args)
	{

		LoggerUtil.log(Level.INFO, "adfasdfw");
		// LoggerUtil.log(Level.INFO, "eeeeee");
		// LoggerUtil.log(Level.INFO, "ffffff");
		// LoggerUtil.log(Level.INFO, "gggggg");
		// LoggerUtil.log(Level.INFO, "hhhhhh");

	}
}
