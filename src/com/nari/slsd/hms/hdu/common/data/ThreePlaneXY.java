package com.nari.slsd.hms.hdu.common.data;


/**
 * 三个平面 6个通道数据
 * */
public class ThreePlaneXY
{
	public static int UpTtl_X = 0; 
	public static int UpTtl_Y = 1; 
	public static int DownTtl_X = 2; 
	public static int DownTtl_Y = 3; 
	public static int WaterTtl_X = 4; 
	public static int WaterTtl_Y = 5; 
	
	public int[] index;//对应的建向信号
	public float[][] data = new float[6][];//对应的数据    0是上导的x 1是上导的y  2是法兰的x 3是法兰的y 4是水导的x 5是水导的y
	
	public void setIndex(int[] index)
	{
		this.index = index.clone();
	}
	public void setData(float[][] array)
	{
		data = array.clone();
	}
	
	/**
	 * 修改其中一个通道的参数
	 * @param array  数据
	 * @param id     通道的id 0-5
	 * @param start  从哪个下标开始
	 * @param len    长度
	 */
	public void setData(float[] array, int id, int start, int len)
	{
		data[id] = new float[len];
		System.arraycopy(array, start, data[id], 0, len);
	}
	
	/**
	 * 一次性传入6个通道信息
	 * @param array  数据
	 * @param start  从缓冲中那个开始
	 * @param len    长度
	 */
	public void setData(float[][] array, int start, int len)
	{
		for(int i=0; i<6; i++)
		{
			data[i] = new float[len];
			System.arraycopy(array[i], start, data[i], 0, len);
		}
		
	}
	
	public ThreePlaneXY()
	{

	}
	
}
