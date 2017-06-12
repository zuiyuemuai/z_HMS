/**
 * 
 */
package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.crypto.Data;

import com.nari.slsd.hms.hdu.common.util.LoggerUtil;

//import com.nari.slsd.hd.control.date.f;

/**
 * Created :2014-10-13 ����7:38:08 Describe :��ȡ��� Class : GetCmtrContent.java
 * love you
 * 
 * 
 */
public class ComtradeData implements Serializable
{
	public String cmtrFilePath;// 路径
	public CmtrCfgInfo cmtrCfgInfo = new CmtrCfgInfo();

	public float[] factorA;// 这是char值的倍数
	public float[] factorB;// 这个是char值的偏移 将dat中数据转换成float型

	public int smprateCount;// 一个通道的数据量
	public int smprateRate;// 频率
	public int channelCount;// 通道数
	public int digitsCount;// 数字量数
	public String[] channelName;// 通道名称
	public float[][] realAnalogs;// 真实数据
	public String beginTime;// 开始时间
	public String endTime;// 结束时间

	public ComtradeData(String cmtrFilePath)
	{
		this.cmtrFilePath = cmtrFilePath;
		getChannelImformation(cmtrFilePath);
		getChannelData(cmtrFilePath);
	}

	/**
	 * 从一个文件中截取数据 注意文件中的时间必须包含截取时间一部分
	 * 
	 * @param cmtrFilePath
	 *            文件路径名，不含.cfg和.dat
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 */
	public ComtradeData(String cmtrFilePath, Date selectstart, Date selectend)
	{
		this.cmtrFilePath = cmtrFilePath;
		getChannelImformation(cmtrFilePath);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			Date startData = format.parse(beginTime);
			Date endData = format.parse(endTime);

			float startIndex = 0;
			float endIndex = 0;
			long alltime = endData.getTime() - startData.getTime() + 1000;

			// 来计算截取部分的开始百分比和结束百分比，分别存放在startIndex和endIndex中
			long st = selectstart.getTime() - startData.getTime();// 截取部分
			long et = selectend.getTime() - startData.getTime();// 截取部分
			if (st > 0)
			{
				startIndex = st / (float) alltime;
			} else
			{
				startIndex = 0;// 从0开始
			}
			if (et < alltime)// 超过了总长说明是最后
			{
				endIndex = et / (float) alltime;
			} else
			{
				endIndex = 1;// 最后
			}

			// 截取数据
			smprateCount *= (endIndex - startIndex);
			beginTime = format.format(selectstart);
			endTime = format.format(selectend);

			// 获取数据
			getChannelData(cmtrFilePath, startIndex, endIndex);
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取通道信息
	 */
	public void getChannelImformation(String cmtrFilePath)
	{
		FileInputStream fileInputStream1;
		try
		{
			fileInputStream1 = new FileInputStream(new File(cmtrFilePath
					+ ".cfg"));
			CmtrUtil.readCmtrCfgInfo(fileInputStream1, cmtrCfgInfo);
			fileInputStream1.close();
			channelCount = cmtrCfgInfo.getAnalogCount();
			channelName = new String[channelCount];
			factorA = new float[channelCount];
			factorB = new float[channelCount];
			smprateCount = cmtrCfgInfo.getSmprates().get(0).getPoint();
			smprateRate = (int) cmtrCfgInfo.getSmprates().get(0).getRate();
			digitsCount = cmtrCfgInfo.getDigitCount();
			beginTime = cmtrCfgInfo.getBeginTime();
			endTime = cmtrCfgInfo.getEndTime();
			for (int i = 0; i < channelCount; i++)
			{
				channelName[i] = cmtrCfgInfo.getAnalogs().get(i).getName();
				factorA[i] = cmtrCfgInfo.getAnalogs().get(i).getFactorA();
				factorB[i] = cmtrCfgInfo.getAnalogs().get(i).getFactorB();
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 获取通达数据
	 */
	public void getChannelData(String cmtrFilePath)
	{
		FileInputStream fileInputStream2;
		CmtrDatSmpdot cmtrDatSmpdot = new CmtrDatSmpdot();
		int[][] analogs;
		try
		{

			fileInputStream2 = new FileInputStream(new File(cmtrFilePath
					+ ".DAT"));
			analogs = new int[channelCount][smprateCount];
			int[] temp;
			for (int k = 0; k < smprateCount; k++)
			{
				CmtrUtil.readCmtrDatSmpdotBinary(fileInputStream2,
						channelCount, digitsCount, cmtrDatSmpdot);
				temp = cmtrDatSmpdot.getAnalogs();
				for (int i = 0; i < channelCount; i++)
				{

					analogs[i][k] = temp[i];
					// System.out.println(String.valueOf(temp[i]));
				}
			}
			fileInputStream2.close();
			realAnalogs = new float[channelCount][smprateCount];
			for (int i = 0; i < channelCount; i++)
			{
				for (int j = 0; j < smprateCount; j++)
				{
					realAnalogs[i][j] = analogs[i][j] * factorA[i] + factorB[i];
				}
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 获取通达数据
	 * 
	 * @param cmtrFilePath
	 * @param start
	 *            开始的比分比，0——1
	 * @param end
	 *            结束的百分比，0-1
	 */
	public void getChannelData(String cmtrFilePath, float start, float end)
	{
		FileInputStream fileInputStream2;
		CmtrDatSmpdot cmtrDatSmpdot = new CmtrDatSmpdot();
		int[][] analogs;
		try
		{

			fileInputStream2 = new FileInputStream(new File(cmtrFilePath
					+ ".DAT"));
			analogs = new int[channelCount][smprateCount];
			int[] temp;
			for (int k = 0; k < smprateCount; k++)
			{
				CmtrUtil.readCmtrDatSmpdotBinary(fileInputStream2,
						channelCount, digitsCount, cmtrDatSmpdot);
				temp = cmtrDatSmpdot.getAnalogs();
				for (int i = 0; i < channelCount; i++)
				{

					analogs[i][k] = temp[i];
				}
			}
			fileInputStream2.close();

			realAnalogs = new float[channelCount][smprateCount];
			for (int i = 0; i < channelCount; i++)
			{
				int k = 0;// 进行截取
				for (int j = (int) (smprateCount * start); j < (int) smprateCount
						* end; j++, k++)
				{
					realAnalogs[i][k] = analogs[i][j] * factorA[i] + factorB[i];
				}
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 追加信息，将两个cfg和dat合并
	 * 
	 * @param cmtrFilePath
	 *            要追加的路径
	 * @return 是否成功
	 */
	public boolean merge(String cmtrFilePath)
	{
		FileInputStream fileInputStream1;
		try
		{
			fileInputStream1 = new FileInputStream(new File(cmtrFilePath
					+ ".cfg"));
			CmtrCfgInfo newcmtrCfgInfo = new CmtrCfgInfo();
			CmtrUtil.readCmtrCfgInfo(fileInputStream1, newcmtrCfgInfo);
			fileInputStream1.close();

			// 增加1s
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date data = format.parse(newcmtrCfgInfo.getBeginTime());
			data.setSeconds(data.getSeconds() - 1);
			String newtime = format.format(data);

			if (endTime.equals(newtime))
			{
				endTime = newcmtrCfgInfo.getEndTime();
				smprateCount += newcmtrCfgInfo.getSmprates().get(0).getPoint();

				mergeChannelData(cmtrFilePath, newcmtrCfgInfo);
				return true;
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 追加获取通达数据
	 */
	public Boolean mergeChannelData(String cmtrFilePath,
			CmtrCfgInfo newcmtrCfgInfo)
	{
		int newsmprateCount = newcmtrCfgInfo.getSmprates().get(0).getPoint();
		FileInputStream fileInputStream2;
		CmtrDatSmpdot cmtrDatSmpdot = new CmtrDatSmpdot();
		int[][] analogs;
		try
		{
			fileInputStream2 = new FileInputStream(new File(cmtrFilePath
					+ ".DAT"));
			analogs = new int[channelCount][newsmprateCount];
			int[] temp;
			for (int k = 0; k < newsmprateCount; k++)
			{
				CmtrUtil.readCmtrDatSmpdotBinary(fileInputStream2,
						channelCount, digitsCount, cmtrDatSmpdot);
				temp = cmtrDatSmpdot.getAnalogs();
				for (int i = 0; i < channelCount; i++)
				{
					analogs[i][k] = temp[i];
				}
			}
			fileInputStream2.close();
			float[][] buf = realAnalogs;
			realAnalogs = new float[channelCount][smprateCount];
			for (int i = 0; i < channelCount; i++)
			{
				for (int j = 0; j < buf[i].length; j++)
				{
					realAnalogs[i][j] = buf[i][j];
				}

				for (int j = buf[i].length; j < smprateCount; j++)
				{
					realAnalogs[i][j] = analogs[i][j - buf[i].length]
							* factorA[i] + factorB[i];
				}
			}

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 追加一个comtrade数据 没有安全检查
	 * 
	 * @param data
	 * @return
	 */
	public boolean merge(ComtradeData data)
	{

		this.smprateCount += data.smprateCount;
		this.digitsCount += data.digitsCount;

		float[][] t = realAnalogs;
		realAnalogs = new float[channelCount][smprateCount];
		for (int i = 0; i < channelCount; i++)
		{
			System.arraycopy(t[i], 0, realAnalogs[i], 0, t[i].length);
			System.arraycopy(data.realAnalogs[i], 0, realAnalogs[i],
					t[i].length, data.realAnalogs[i].length);
		}

		endTime = data.endTime;

		return true;

	}

	/**
	 * 获取一个通道的数据
	 * 
	 * @param cmtrFilePath
	 * @param cmtrCfgInfo
	 * @param id
	 * @return float[]
	 */
	public static float[] getOneChannelData(String cmtrFilePath,
			CmtrCfgInfo cmtrCfgInfo, int id)
	{
		FileInputStream fileInputStream;
		int pointCount = cmtrCfgInfo.getSmprates().get(0).getPoint();
		CmtrDatSmpdot smpdot = null;
		try
		{
			fileInputStream = new FileInputStream(new File(cmtrFilePath
					+ ".DAT"));
			smpdot = CmtrUtil.readCmtrDatOneChannel(fileInputStream, id,
					cmtrCfgInfo.getAnalogCount(), cmtrCfgInfo.getDigitCount(),
					pointCount);
			fileInputStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		float[] realAnalogs = new float[pointCount];
		int[] analogs = smpdot.getAnalogs();

		float factorA = cmtrCfgInfo.getAnalogs().get(id).getFactorA();
		float factorB = cmtrCfgInfo.getAnalogs().get(id).getFactorB();
		for (int i = 0; i < pointCount; i++)
		{
			realAnalogs[i] = analogs[i] * factorA + factorB;
		}
		return realAnalogs;
	}

	/**
	 * 获取cmtr信息
	 * 
	 * @param cmtrFilePath
	 * @return CmtrCfgInfo
	 */
	public static CmtrCfgInfo getCmtrCfgInfo(String cmtrFilePath)
	{
		FileInputStream fileInputStream;
		CmtrCfgInfo cmtrCfgInfo = new CmtrCfgInfo();
		try
		{
			fileInputStream = new FileInputStream(new File(cmtrFilePath
					+ ".cfg"));
			CmtrUtil.readCmtrCfgInfo(fileInputStream, cmtrCfgInfo);
			fileInputStream.close();

		} catch (Exception e)
		{
			LoggerUtil.log(Level.SEVERE, "Worng Cfginfo path:"+cmtrFilePath);
			
			e.printStackTrace();
		}

		cmtrCfgInfo.channelCount = cmtrCfgInfo.getAnalogCount();
		cmtrCfgInfo.channelName = new String[cmtrCfgInfo.channelCount];
		cmtrCfgInfo.factorA = new float[cmtrCfgInfo.channelCount];
		cmtrCfgInfo.factorB = new float[cmtrCfgInfo.channelCount];
		cmtrCfgInfo.pointCount = cmtrCfgInfo.getSmprates().get(0).getPoint();
		cmtrCfgInfo.smprateRate = (int) cmtrCfgInfo.getSmprates().get(0)
				.getRate();

		for (int i = 0; i < cmtrCfgInfo.channelCount; i++)
		{
			cmtrCfgInfo.channelName[i] = cmtrCfgInfo.getAnalogs().get(i)
					.getName();
			cmtrCfgInfo.factorA[i] = cmtrCfgInfo.getAnalogs().get(i)
					.getFactorA();
			cmtrCfgInfo.factorB[i] = cmtrCfgInfo.getAnalogs().get(i)
					.getFactorB();
		}

		return cmtrCfgInfo;
	}

	/**
	 * 从多个文件中获取一个通道的数据
	 * 
	 * @param soucePaths
	 * @param allcmtrCfgInfo
	 * @param id
	 * @return
	 */
	public static float[] getOneChannelDataFromFiles(Vector<String> soucePaths,
			CmtrCfgInfo allcmtrCfgInfo, int id)
	{
		float[] all = new float[allcmtrCfgInfo.pointCount];
		int index = 0;
		for (int i = 0; i < soucePaths.size(); i++)
		{
			CmtrCfgInfo info = getCmtrCfgInfo(soucePaths.get(i));
			float[] data = getOneChannelData(soucePaths.get(i), info, id);
			System.arraycopy(data, 0, all, index, info.pointCount);
			index += info.pointCount;
		}
		return all;
	}

	/**
	 * 从多个文件中读取配置信息，并进行合并，这个文件夹时间上必须是顺序的
	 * 
	 * @return
	 */
	public static CmtrCfgInfo getCmtrCfgInfoFromFiles(Vector<String> soucePaths)
	{
		CmtrCfgInfo allcmtrCfgInfo = null;
		if (soucePaths.isEmpty())
		{
			return allcmtrCfgInfo;
		}
		allcmtrCfgInfo = ComtradeData.getCmtrCfgInfo(soucePaths.get(0));
		for (int i = 1; i < soucePaths.size(); i++)
		{
			CmtrCfgInfo newcmtrCfgInfo = ComtradeData.getCmtrCfgInfo(soucePaths
					.get(i));

			// 增加1s
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date data;
			try
			{
				data = format.parse(newcmtrCfgInfo.getBeginTime());
				data.setSeconds(data.getSeconds() - 1);
				String newtime = format.format(data);

				if (allcmtrCfgInfo.getEndTime().equals(newtime))
				{
					allcmtrCfgInfo.setEndTime(newcmtrCfgInfo.getEndTime());
					allcmtrCfgInfo.pointCount += newcmtrCfgInfo.getSmprates()
							.get(0).getPoint();
				}

			} catch (ParseException e)
			{
				e.printStackTrace();
			}

		}

		return allcmtrCfgInfo;

	}

	/**
	 * 对于一个文件进行分拣复制
	 * 
	 * @param sourePath
	 *            源路径 要求是 文件名不带.cfg或者.dat
	 * @param detPath
	 *            目的路径 要求是 文件名不带.cfg或者.dat
	 * @param selectstart
	 *            开始时间
	 * @param selectend
	 *            结束时间
	 */
	public static void copySelectFile(String sourePath, String detPath,
			Date selectstart, Date selectend)
	{
		CmtrCfgInfo info = getCmtrCfgInfo(sourePath);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			Date startData = format.parse(info.getBeginTime());
			Date endData = format.parse(info.getEndTime());

			int startIndex = 0;
			int endIndex = 0;
			long alltime = (endData.getTime() - startData.getTime() + 1000)/1000;

			// 文件名字的处理
			String[] temp = detPath.split("\\\\");
			if(null == temp)
			{
				LoggerUtil.log(Level.WARNING, "select detPath err");
				return;
			}
			String[] names = temp[temp.length-1].split("_");//change by lqj 2_1
			if(null == names)
			{
				LoggerUtil.log(Level.WARNING, "select detPath err");
				return;
			}
			

			SimpleDateFormat formatfile = new SimpleDateFormat(
					"yyyy-MM-dd HH-mm-ss");

			// 来计算截取部分的开始下标和结束下标，分别存放在startIndex和endIndex中
			long st = selectstart.getTime()/1000 - startData.getTime()/1000;// 截取部分
			long et = selectend.getTime()/1000 - startData.getTime()/1000;// 截取部分
			if (st > 0)
			{
				info.setBeginTime(format.format(selectstart));
				startIndex = (int) (st / (float) alltime * info.pointCount);
				names[1] = formatfile.format(selectstart);
			} else
			{
				startIndex = 0;// 从0开始
			}
			if (et < alltime)// 超过了总长说明是最后
			{
				endIndex = (int) (et / (float) alltime * info.pointCount);
				info.setEndTime(format.format(selectend));
				names[2] = formatfile.format(selectend);
			} else
			{
				endIndex = info.pointCount;// 最后
			}

			// 截取数据
			info.pointCount = (endIndex - startIndex);
			info.getSmprates().get(0).setPoint(info.pointCount);

			// 写配置信息
			String pathString = "";
			for(int i=0; i<temp.length-1; i++)
			{
				pathString += temp[i];
				pathString += "\\\\";
			}
			File cfgoutFile = new File(pathString + names[0]+"_"+names[1]+"_"+names[2]+"_"+ names[3] + ".cfg");
			if(!cfgoutFile.exists())
				cfgoutFile.createNewFile();
			
			FileOutputStream cfgFileOutputStream = new FileOutputStream(
					cfgoutFile);
			CmtrUtil.writeCmtrCfgInfo(cfgFileOutputStream, info);

			// 写DAT信息
			CmtrUtil.copyData(sourePath + ".DAT", pathString+names[0]+"_"+names[1]+"_"+names[2]+"_"+ names[3] + ".DAT", startIndex,
					endIndex, info.getAnalogCount(), info.getDigitCount());

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		String pathString = "电站I-1号机组#2014-10-17 15-46-06#2014-10-17 15-46-14#wave";

		ComtradeData cData1 = new ComtradeData(pathString);
		CmtrCfgInfo cfgInfo = ComtradeData.getCmtrCfgInfo(pathString);
		float[] data = ComtradeData.getOneChannelData(pathString, cfgInfo, 0);
		while (true)
			;
	}
}
