package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.nari.slsd.hms.hdu.common.comtrade.ComtradeData;
import com.nari.slsd.hms.hdu.common.util.FileUtil;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线分拣数据分拣线程 根据数据和开始结束参数，对数据进行分拣并且生成文件
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class SelectHandleThread extends Thread
{
	// protected static ResourceBundle res = ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);
	Vector<SelectSection> sections;
	String destpath;
	String sourpath;

	/**
	 * 
	 * @param sections
	 *            区间
	 * @param Dpath
	 *            目的目录（工作空间最外层）
	 * @param Spath
	 *            源目录（到XX实验）
	 */
	public SelectHandleThread(Vector<SelectSection> sections, String Destpath,
			String Sourpath)
	{
		this.sections = sections;
		this.destpath = Destpath;
		this.sourpath = Sourpath;
	}


	@Override
	public void run()
	{
		super.run();
		File rootFile = new File(sourpath);
		String[] names = sourpath.split("\\\\");

		File destFile;
		
		String[] deStrings = destpath.split("\\\\");

		if (deStrings.length >= 3)// 这里是判断是否已经有相同的目录，3是判断下后面三级是否相同
		{
			if (deStrings[deStrings.length - 2].equals(names[names.length - 2]))
			{
				destFile = new File(destpath);
			} else if (deStrings[deStrings.length - 1]
					.equals(names[names.length - 2]))
			{
				destFile = new File(destpath + "\\" + names[names.length - 1]);
			} else
			{
				destFile = new File(destpath + "\\" + names[names.length - 2]
						+ "\\" + names[names.length - 1]);
			}
		} else
		{
			destFile = new File(destpath + "\\" + names[names.length - 2]
					+ "\\" + names[names.length - 1]);
		}

		if (!destFile.exists())
			destFile.mkdirs();// 如果目录不存在就创建目录结构

		for (int i = 0; i < sections.size(); i++)
		{
			File createDir = new File(destFile.getAbsolutePath() + "\\"
					+ (i + 1));// 第几区段, 这里加1是为了从1开始
			if (!createDir.exists())
				createDir.mkdir();

//			String[] fileName = { "\\定时\\wave\\VIB-1024.0Hz", "\\定时\\wave\\VIB-4096.0Hz",
//					"\\定时\\ain", "\\定时\\eig", "\\整周期\\wave\\VIB", "\\整周期\\wave\\键相" };
			
			Vector<String> filename = new Vector<String>();
			filename.add("\\定时\\ain");
			filename.add("\\定时\\eig");
			
			File file = new File(sourpath+ "\\定时\\wave");
			File[] files = file.listFiles();
			if(null != files)
			{
				for(File f:files)
				{
					filename.add("\\定时\\wave\\" + f.getName());
				}
			}
			file = new File(sourpath+ "\\整周期\\wave");
			files = file.listFiles();
			if(null != files)
			{
				for(File f:files)
				{
					filename.add("\\整周期\\wave\\" + f.getName());
				}
			}
			
			
			for (int j = 0; j < filename.size(); j++)
			{
				File newFile = new File(createDir.getAbsolutePath()
						+ filename.get(j));
				File waitFile = new File(sourpath + filename.get(j));// 等待被分拣的文件夹
				newFile.mkdirs();

				selectHandle(waitFile, newFile, sections.get(i));

			}

		}

		JOptionPane.showMessageDialog(null,
				HduChartUtil.getResource("OfflineDataSelect_CompleteMsg"));
	}

	/**
	 * 分拣数据
	 * 
	 * @param sourFile
	 *            源数据
	 * @param deFile
	 *            目的数据
	 * @param section
	 *            区间信息
	 */
	void selectHandle(File sourFile, File deFile, SelectSection section)
	{

		// 分离出cfg文件
		FilenameFilter filenameFilter = new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				// TODO Auto-generated method stub
				return name.matches(".*[.][c][f][g]");
			}
		};
		ComtradeData CmtrContent = null;
		String[] nameStrings = sourFile.list(filenameFilter);
		if (null == nameStrings)
		{
			return;
		}
		for (String t : nameStrings)// 获取
		{
			String[] date = t.split("_");// change by lqj 2_1
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH-mm-ss");
			try
			{
				Date startDate = format.parse(date[1]);
				Date endDate = format.parse(date[2]);
				long soureStartTime = startDate.getTime() / 1000;
				long soureEndTime = endDate.getTime() / 1000;
				long detStartTime = section.startTime.getTime() / 1000;
				long detEndTime = section.endTime.getTime() / 1000;

				// 需要覆盖的
				if (!(soureEndTime <= detStartTime || soureStartTime >= detEndTime))
				{
					String[] getNameStrings = t.split("[.]");
					String namepath = sourFile.getAbsolutePath() + "\\"
							+ getNameStrings[0];

					File newCFGFile = new File(deFile + "\\"
							+ getNameStrings[0] + ".cfg");

					if (soureStartTime >= detStartTime
							&& soureEndTime <= detEndTime)// 区间包含直接添加
					{
						File newDATFile = new File(deFile + "\\"
								+ getNameStrings[0] + ".DAT");
						newDATFile.createNewFile();
						FileUtil.Copy(namepath + ".cfg",
								newCFGFile.getAbsolutePath());
						FileUtil.Copy(namepath + ".DAT",
								newDATFile.getAbsolutePath());
					} else
					// 需要进数据截取的
					{
						ComtradeData.copySelectFile(namepath, deFile + "\\"
								+ getNameStrings[0], section.startTime,
								section.endTime);
					}
				}

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

};
