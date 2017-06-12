package com.nari.slsd.hms.hdu.common.comtrade;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;

import com.nari.slsd.hms.hdu.common.util.LoggerUtil;

/**
 * 
 * 在获取第二行的时候有问题，已经解决 每次循环获取数据时，没有生成一个新的类
 * 
 * @author Administrator
 * 
 */
public class CmtrUtil
{

	final String file = "file";

	public static void writeCmtrCfgInfo(FileOutputStream fileOutputStream,
			CmtrCfgInfo cfg)
	{
		OutputStreamWriter osw = null;
		try
		{
			osw = new OutputStreamWriter(fileOutputStream, "gbk");

			osw.write(cfg.getStationName() + "," + cfg.getKymographId() + ","
					+ cfg.getRevyear() + "\r\n");

			osw.write(cfg.getAnalogCount() + cfg.getDigitCount() + ","
					+ cfg.getAnalogCount() + "A," + cfg.getDigitCount()
					+ "D\r\n");
			osw.flush();
			for (int i = 0; i < cfg.getAnalogCount(); i++)
			{
				osw.write(cfg.getAnalogs().get(i).getIndex() + ","
						+ cfg.getAnalogs().get(i).getName() + ","
						+ cfg.getAnalogs().get(i).getPhase() + ","
						+ cfg.getAnalogs().get(i).getElement() + ","
						+ cfg.getAnalogs().get(i).getUnit() + ","
						+ cfg.getAnalogs().get(i).getFactorA() + ","
						+ cfg.getAnalogs().get(i).getFactorB() + ","
						+ cfg.getAnalogs().get(i).getOffsetTime() + ","
						+ cfg.getAnalogs().get(i).getSmpMin() + ","
						+ cfg.getAnalogs().get(i).getSmpMax() + ","
						+ cfg.getAnalogs().get(i).getPrimary() + ","
						+ cfg.getAnalogs().get(i).getSecondary() + ","
						+ cfg.getAnalogs().get(i).getPs() + "\r\n");
				osw.flush();
			}

			for (int i = 0; i < cfg.getDigitCount(); i++)
			{
				osw.write(cfg.getDigits().get(i).getIndex() + ","
						+ cfg.getDigits().get(i).getName() + ","
						+ cfg.getDigits().get(i).getPh() + ","
						+ cfg.getDigits().get(i).getCcbm() + ","
						+ cfg.getDigits().get(i).getState() + "\r\n");
				osw.flush();
			}

			osw.write(cfg.getFrequency() + "\r\n");

			osw.write(cfg.getSmprateCount() + "\r\n");

			for (int i = 0; i < cfg.getSmprateCount(); i++)
			{
				osw.write(cfg.getSmprates().get(i).getRate() + ","
						+ cfg.getSmprates().get(i).getPoint() + "\r\n");

			}

			osw.write("'" + cfg.getBeginTime() + "'\r\n");

			osw.write("'" + cfg.getEndTime() + "'\r\n");

			osw.write(cfg.getFileType() + "\r\n");

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				osw.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static byte[] intToByte(int number)
	{

		int temp = number;

		byte[] b = new byte[4];

		for (int i = 0; i < b.length; i++)
		{

			b[i] = new Integer(temp & 0xff).byteValue();// �����λ���������λ

			temp = temp >> 8; // ������8λ

		}

		return b;

	}

	public static int ByteToInt(byte[] b, int offset, int byteOrchar)
	{
		int firstByte = 0;
		int secondByte = 0;
		int thirdByte = 0;
		int fourthByte = 0;
		int index = offset;
		int value = 0;

		if (byteOrchar == 4)
		{
			firstByte = (0x000000FF & ((int) b[index]));
			secondByte = (0x000000FF & ((int) b[index + 1]));
			thirdByte = (0x000000FF & ((int) b[index + 2]));
			fourthByte = (0x000000FF & ((int) b[index + 3]));
			value = (int) (((long) (firstByte | secondByte << 8
					| thirdByte << 16 | fourthByte << 24)) & 0xFFFFFFFFL);
		} else if (byteOrchar == 2)
		{
			firstByte = (0x000000FF & ((int) b[index]));
			secondByte = (0x000000FF & ((int) b[index + 1]));
			value = (int) (((long) (firstByte | secondByte << 8)) & 0xFFFFFFFFL);
		}
		return value;
	}

	public static void writeCmtrDatSmpdotBinary(
			FileOutputStream fileOutputStream, int analogCount, int digitCount,
			CmtrDatSmpdot dot) throws IOException
	{
		int bufferLength = 0;

		byte[] converInput = new byte[256];
		int[] analogs = new int[analogCount];

		int[] digits = new int[digitCount];
		System.arraycopy(intToByte(dot.getIndex()), 0, converInput,
				bufferLength, 4);
		bufferLength += 4;
		System.arraycopy(intToByte(dot.getTime()), 0, converInput,
				bufferLength, 4);
		bufferLength += 4;
		analogs = dot.getAnalogs();
		for (int i = 0; i < analogCount; i++)
		{
			System.arraycopy(intToByte(analogs[i]), 0, converInput,
					bufferLength, 2);
			bufferLength += 2;
			if (bufferLength > 100)
			{
				fileOutputStream.write(converInput, 0, bufferLength);
				bufferLength = 0;
			}
		}
		fileOutputStream.write(converInput, 0, bufferLength);
		bufferLength = 0;
		digits = dot.getDigits();
		short temp16 = 0x0000;
		int j = 0;
		for (j = 0; j < digitCount; j++)
		{
			if ((j % 16 == 0) && j != 0)
			{
				System.arraycopy(intToByte(temp16), 0, converInput,
						bufferLength, 2);
				bufferLength += 2;
				temp16 = 0x0000;
				if (bufferLength > 100)
				{
					fileOutputStream.write(converInput, 0, bufferLength);
					bufferLength = 0;
				}

			}

			if (digits[j] == 1)
			{
				temp16 |= (0x0001 << (j % 16));
			}

		}
		if (((j % 16) != 1) && (j != 0))
		{
			System.arraycopy(intToByte(temp16), 0, converInput, bufferLength, 2);
			bufferLength += 2;
			fileOutputStream.write(converInput, 0, bufferLength);
		}

	}

	public static void readCmtrCfgInfo(FileInputStream fileInputStream,
			CmtrCfgInfo cmtrCfgInfo) throws Exception
	{

		StringBuffer strLine = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(fileInputStream, "gbk");
		BufferedReader br = new BufferedReader(isr);
		ArrayList<CmtrCfgAnalog> analogs = new ArrayList<CmtrCfgAnalog>();
		ArrayList<CmtrCfgDigit> digits = new ArrayList<CmtrCfgDigit>();
		ArrayList<CmtrCfgSmprate> smprates = new ArrayList<CmtrCfgSmprate>();
		CmtrCfgAnalog cmtrCfgAnalog = new CmtrCfgAnalog();
		CmtrCfgDigit cmtrCfgDigit = new CmtrCfgDigit();
		CmtrCfgSmprate cmtrCfgSmprate = new CmtrCfgSmprate();
		strLine.append(br.readLine());
		// br.read(arg0, arg1, arg2)
		String strLine1 = strLine.toString();
		String[] strLine2 = strLine1.split(",");
		cmtrCfgInfo.setStationName(strLine2[0]);
		cmtrCfgInfo.setKymographId(strLine2[1]);
		cmtrCfgInfo.setRevyear(strLine2[2]);

		// br = new BufferedReader(isr);
		strLine1 = br.readLine();
		strLine2 = strLine1.split(",");
		String[] strings = strLine2[1].split("A");
		cmtrCfgInfo.setAnalogCount(Integer.parseInt(strings[0]));
		strings = strLine2[2].split("D");
		cmtrCfgInfo.setDigitCount(Integer.parseInt(strings[0]));

		for (int i = 0; i < cmtrCfgInfo.getAnalogCount(); i++)
		{
			strLine1 = br.readLine();
			strLine2 = strLine1.split(",");

			cmtrCfgAnalog = new CmtrCfgAnalog();
			cmtrCfgAnalog.setIndex(Integer.parseInt(strLine2[0]));
			cmtrCfgAnalog.setName(strLine2[1]);
			cmtrCfgAnalog.setPhase(strLine2[2]);
			cmtrCfgAnalog.setElement(strLine2[3]);
			cmtrCfgAnalog.setUnit(strLine2[4]);
			cmtrCfgAnalog.setFactorA(Float.parseFloat(strLine2[5]));
			cmtrCfgAnalog.setFactorB(Float.parseFloat(strLine2[6]));
			cmtrCfgAnalog.setOffsetTime(Integer.parseInt(strLine2[7]));
			cmtrCfgAnalog.setSmpMin(Integer.parseInt(strLine2[8]));

			cmtrCfgAnalog.setSmpMax(Integer.parseInt(strLine2[9]));

			cmtrCfgAnalog.setPrimary(Float.parseFloat(strLine2[10]));
			cmtrCfgAnalog.setSecondary(Float.parseFloat(strLine2[11]));
			cmtrCfgAnalog.setPs(strLine2[12]);
			analogs.add(cmtrCfgAnalog);

		}
		cmtrCfgInfo.setAnalogs(analogs);// 可以放外边吧

		for (int j = 0; j < cmtrCfgInfo.getDigitCount(); j++)
		{
			strLine1 = br.readLine();
			strLine2 = strLine1.split(",");
			cmtrCfgDigit = new CmtrCfgDigit();// 每次循环需要重新生成一个
			cmtrCfgDigit.setIndex(Integer.parseInt(strLine2[0]));
			cmtrCfgDigit.setName(strLine2[1]);
			cmtrCfgDigit.setPh(strLine2[2]);
			cmtrCfgDigit.setCcbm(strLine2[3]);
			cmtrCfgDigit.setState(Integer.parseInt(strLine2[4]));
			digits.add(cmtrCfgDigit);
		}
		cmtrCfgInfo.setDigits(digits);// 可以放外边吧

		strLine1 = br.readLine();
		cmtrCfgInfo.setFrequency(Float.parseFloat(strLine1));
		strLine1 = br.readLine();
		cmtrCfgInfo.setSmprateCount(Integer.parseInt(strLine1));

		for (int k = 0; k < cmtrCfgInfo.getSmprateCount(); k++)
		{
			strLine1 = br.readLine();
			strLine2 = strLine1.split(",");

			cmtrCfgSmprate.setRate(Float.parseFloat(strLine2[0]));
			cmtrCfgSmprate.setPoint(Integer.parseInt(strLine2[1]));
			smprates.add(cmtrCfgSmprate);
		}
		cmtrCfgInfo.setSmprates(smprates);// 可以放外边吧

		strLine1 = br.readLine();
		strLine2 = strLine1.split("'");
		cmtrCfgInfo.setBeginTime(strLine2[1]);
		strLine1 = br.readLine();
		strLine2 = strLine1.split("'");
		cmtrCfgInfo.setEndTime(strLine2[1]);
		strLine1 = br.readLine();
		cmtrCfgInfo.setFileType(strLine1);

	}

	public static void readCmtrDatSmpdotBinary(FileInputStream fileInputStream,
			int analogCount, int digitCount, CmtrDatSmpdot dot)
	{
		DataInputStream dis = null;
		dis = new DataInputStream(fileInputStream);
		try
		{
			int dataLength = analogCount * 2
					+ (int) Math.ceil((double) digitCount / 16.0) + 8;
			int bufferLength = 0;
			int[] analogs = new int[analogCount];
			int[] digits = new int[digitCount];
			byte[] converInput = new byte[dataLength];
			dis.read(converInput, 0, dataLength);

			dot.setIndex(ByteToInt(converInput, bufferLength, 4));
			bufferLength += 4;
			dot.setTime(ByteToInt(converInput, bufferLength, 4));
			bufferLength += 4;

			for (int i = 0; i < analogCount; i++)
			{

				analogs[i] = ByteToInt(converInput, bufferLength, 2);
				if (analogs[i] > 32768)
				{
					analogs[i] = analogs[i] - 65536;
				}
				// System.out.println( analogs[i]);
				bufferLength += 2;

			}
			dot.setAnalogs(analogs);
			int chlIndex = 0;
			int j = 0;
			int temp16 = 0;
			for (j = 0; j < Math.ceil((double) digitCount / 16.0); j++)
			{
				temp16 = ByteToInt(converInput, bufferLength, 2);
				bufferLength += 2;
				for (int k = 0; k < 16; k++)
				{
					digits[chlIndex] = (temp16 >> k) & (0x0001);

					chlIndex++;
					if (chlIndex >= digitCount)
						break;
				}
			}
			dot.setDigits(digits);

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 获取一个通道的数据,对于数字量没有进行处理
	 * 
	 * @param fileInputStream
	 *            文件数据流
	 * @param id
	 *            第几个通道 从0开始计算
	 * @param analogCount
	 *            模拟量通道数
	 * @param digitCount
	 *            数字量通道数
	 * @param pointCount
	 *            一个通道的数据量
	 * @return
	 */
	public static CmtrDatSmpdot readCmtrDatOneChannel(
			FileInputStream fileInputStream, int id, int analogCount,
			int digitCount, int pointCount) throws Exception
	{
		DataInputStream dis = null;
		CmtrDatSmpdot cmtrDatSmpdot = new CmtrDatSmpdot();

		dis = new DataInputStream(fileInputStream);

		int dataLength = 8;
		int[] analogs = new int[pointCount];
		// int[] digits = new int[digitCount];
		byte[] converInput = new byte[dataLength];
		dis.read(converInput, 0, dataLength);

		cmtrDatSmpdot.setIndex(ByteToInt(converInput, 0, 4));
		cmtrDatSmpdot.setTime(ByteToInt(converInput, 4, 4));

		converInput = new byte[2];
		dis.skip(id * 2);// 跳到要获取的数据位置
		// 读取数据
		for (int i = 0; i < pointCount; i++)
		{
			dis.read(converInput, 0, 2);
			analogs[i] = ByteToInt(converInput, 0, 2);
			if (analogs[i] > 32768)
			{
				analogs[i] = analogs[i] - 65536;
			}

			dis.skip(analogCount * 2 + 6);// 字节流的数据格式应该是4个字节的下标信息+4个字节的时间信息+n个通道的*2的信息量
											// 这里加6实际上是+8-2,跳过8个字节的下标和时间信息，-2是自己的对应通道信息要保留
		}

		cmtrDatSmpdot.setAnalogs(analogs);

		return cmtrDatSmpdot;
	}

	/**
	 * 赋值一个DAT的一部分到另外一个文件中去
	 * 
	 * @param sourePath
	 *            源DAT文件，这里的文件名是XX.DAT
	 * @param detPath
	 *            目的DAT文件，这里的文件名是XX.DAT
	 * @param start
	 *            开始的下标
	 * @param end
	 *            结束的下标
	 * @param analogCount
	 *            模拟量数
	 * @param digitCount
	 *            数字量数
	 */

	public static void copyData(String sourePath, String detPath, int start,
			int end, int analogCount, int digitCount)
	{
		try
		{
			File inFile = new File(sourePath);
			if (!inFile.exists())
			{
				LoggerUtil.log(Level.WARNING, "infile is not exist");
				return;
			}
			File outFile = new File(detPath);
			if (!outFile.exists())
			{
				outFile.createNewFile();
			}
			FileInputStream fileInputStream = new FileInputStream(inFile);
			FileOutputStream fileOutputStream = new FileOutputStream(outFile);

			int dataLength = analogCount * 2
					+ (int) Math.ceil((double) digitCount / 16.0) + 8;
			byte[] converInput = new byte[dataLength];

			fileInputStream.skip(start * dataLength);// 直接跳到起始地址

			for (int i = 0; i < end - start + 1; i++)
			{
				fileInputStream.read(converInput, 0, dataLength);
				fileOutputStream.write(converInput, 0, dataLength);
			}

			fileInputStream.close();
			fileOutputStream.close();

		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
