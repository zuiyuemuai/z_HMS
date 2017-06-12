package com.nari.slsd.hms.hdu.common.algorithm;

import com.nari.slsd.hms.hdu.common.data.Complex;

/**
 * 一个通道的计算结果
 * @author Administrator
 *
 */
public class CalResult
{
	protected int size = 0;// 一个数据通道的长度
	protected float[] datas;
	protected int[] indexs;
	
	protected Complex[] complexs;//fft后结果
	
	protected float[] FFTAbs; //fft后的幅值
	protected float[] FFTPhase; //fft后的相位
	
	protected float dataMax = Float.MIN_VALUE;// 数据中最大的值
	protected float dataMin = Float.MAX_VALUE;
	protected float moduleMAX = Float.MIN_VALUE;// 最大最小数值中模最大，用于归一化在姿态图上的显示
	
//	protected float curveAmptitude;// 弯曲量
//	protected float curvePhase;// 弯曲角
	
	protected float peak;//峰峰值
	protected float amplitude1X;//1X幅值
	protected float phase1X;//1X相位

	protected int first_index;//主频的下标
	protected int second_index;//次频的下标
	
	protected float TotalSwing;// 超重量
	protected float OverAngle;// 超重角
	protected float LossAngle;// 失重角
	
	//获取平均幅值
	public float getAvgAbs()
	{
		return Calculate.getAve(FFTAbs);
	}
	//获取平均相位
	public float getAvgPhase()
	{
		return Calculate.getAve(FFTPhase);
	}
	
	public int getFirstIndex()
	{
		return first_index;
	}
	public int getSecondIndex()
	{
		return second_index;
	}
	
	public float[] getFFTAbs()
	{
		return FFTAbs;
	}
	public float[] getFFTPhase()
	{
		return FFTPhase;
	}
	public float getTotalSwing()
	{
		return TotalSwing;
	}
	public float getOverAngle()
	{
		return OverAngle;
	}
	public float getModuleMAX()
	{
		return moduleMAX;
	}

	public float getDataMax()
	{
		return dataMax;
	}

	public float getDataMin()
	{
		return dataMin;
	}
	
	public float getPeak()
	{
		return peak;
	}
	
	public float getAmplitude1X()
	{
		return amplitude1X;
	}
	
	public float getphase1X()
	{
		return phase1X;
	}
	public float[] getFloats()
	{
		return datas;
	}
	public int[] getIntegers()
	{
		return indexs;
	}
	
	
	/**
	 * float数组转换成Interger数组
	 * 
	 * @param in
	 * @return
	 */
	public static Integer[] floatToInteger(float[] in)
	{
		Integer[] indexsIntegers = new Integer[in.length];

		for (int i = 0; i < in.length; i++)
		{
			indexsIntegers[i] = new Integer((int) in[i]);
		}
		return indexsIntegers;
	}
	/**
	 * float数组转换成Interger数组
	 * 
	 * @param in
	 * @return
	 */
	public static Integer[] FloatToInteger(Float[] in)
	{
	
		Integer[] indexsIntegers = new Integer[in.length];

		for (int i = 0; i < in.length; i++)
		{
			if(null == in[i]) return indexsIntegers;
			indexsIntegers[i] = (int)(float) in[i];
		}
		return indexsIntegers;
	}

	
	public static Float[] float1ToFloat(float[] in)
	{
		Float[] dataFloats = new Float[in.length];

		for (int i = 0; i < in.length; i++)
		{
			dataFloats[i] = in[i];
		}

		return dataFloats;
	}
	
	public static float[] FloatTOfloat(Float[] in)
	{
		float[] dataFloats = new float[in.length];

		for (int i = 0; i < in.length; i++)
		{
			dataFloats[i] = in[i];
		}

		return dataFloats;
	}
	public static int[] floatToint(float[] in)
	{
		// TODO Auto-generated method stub
		int[] dataout = new int[in.length];

		for (int i = 0; i < in.length; i++)
		{
			dataout[i] = (int) in[i];
		}

		return dataout;
	}
}
