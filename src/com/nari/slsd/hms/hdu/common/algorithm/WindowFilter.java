/**
 * 
 */
package com.nari.slsd.hms.hdu.common.algorithm;

/**
 * Created :2014-10-31 ����7:24:33 Describe : Class : WindowFilter.java love you
 * 
 * 
 */
public class WindowFilter {

	/**
	 * 平均值滤波
	 * @param input 
	 * @param step 窗口步长
	 * @param width 窗口宽度
	 * @return 结果
	 */
	public static float[] averageFilter(float input[], int step, int width) {
		float sum;
		float[] output=new float[input.length/step];
		int count = 0;
		for (int i = 0; i < input.length - width; i += step) {
			sum = 0;
			for (int j = 0; j < width; j++) {
				sum += input[i + j];
			}
			output[count] = sum / width;
			count++;
		}
		return output;
	}

	/**
	 * 中值滤波
	 * @param input 
	 * @param step 窗口步长
	 * @param width 窗口宽度
	 * @return 结果
	 */
	public static float[] middleFilter(float input[], int step, int width) {
		float[] temp = new float[width];
		float[] output=new float[input.length/step];
		int count = 0;
		for (int i = 0; i < input.length - width; i += step) {
			for (int j = 0; j < width; j++) {
				temp[j] = input[j + i];
			}
			orderMinToMax(temp);
			if (width / 2 == 0) {
				output[count] = (temp[width / 2] + temp[width / 2 + 1]) / 2;
			} else {
				output[count] = temp[(width+1) / 2];
			}
			count++;
		}
		return output;
	}
	
	/**
	 * 峰峰值滤波
	 * @param input 
	 * @param step 窗口步长
	 * @param width 窗口宽度
	 * @return 结果
	 */
	public static float[] peakFilter(float input[], int step, int width)
	{
		float[] temp = new float[width];
//		float[] output = new float[input.length - width];
		float[] output=new float[input.length/step];
		int count = 0;
		float peak;
//		for (int i = 0; i < output.length; i++) {
//			output[i] = input[i];
//		}
		for(int i=0;i<input.length - width;i+=step)
		{
			for (int j = 0; j < width; j++) {
				temp[j] = input[j + i];
			}
			peak=CalWavePeak(temp);
			output[count]=peak;
			count++;
		}
		return output;
	}
	
	
	/**
	 * 有效值滤波
	 * @param input 
	 * @param step 窗口步长
	 * @param width 窗口宽度
	 * @return 结果
	 */
	public static float[] usefulValueFilter(float input[], int step, int width)
	{
//		float[] output = new float[input.length - width];
		float[] output=new float[input.length/step];
		int count = 0;
		float sum=0;
//		for (int i = 0; i < output.length; i++) {
//			output[i] = input[i];
//		}
		for(int i=0;i<input.length - width;i+=step)
		{
			sum=0;
			for (int j = 0; j < width; j++) {
				sum+=(input[j + i])*(input[j + i]);
//				temp[j] = input[j + i];
			}
			output[count]=(float) Math.sqrt(sum/width);
			count++;
		}
		return output;
	}
	

	public static void orderMinToMax(float datas[]) {
		float mid;
		for (int m = 0; m < datas.length; m++) {
			for (int n = 0; n < datas.length - m - 1; n++) {
				if (datas[n] > datas[n + 1]) {
					mid = datas[n + 1];
					datas[n + 1] = datas[n];
					datas[n] = mid;
				}
			}
		}
	}

	public static float CalWavePeak(float[] datas) {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		float ret;

		for (int i = 0; i < datas.length; i++) {
			max = (float) Math.max(max, datas[i]);
			min = (float) Math.min(min, datas[i]);
		}

		ret = max - min;
		return (float)ret;
	}
}
