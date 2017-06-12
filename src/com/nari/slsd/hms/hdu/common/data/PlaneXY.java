package com.nari.slsd.hms.hdu.common.data;



/**
 * 平面 x y
 * @author Administrator
 *
 */
public class PlaneXY
{

	private float[] x;
	private float[] y;
	/**
	 * 获取一个通达的数量
	 * @return
	 */
	public int getNum()
	{
		return x.length<y.length?x.length:y.length;//取两个通道中数量最小的，防止数组越界
	}
	
	public PlaneXY()
	{
		x = null;
		y = null;
	}
	
	public PlaneXY(float[] x, float[] y)
	{
		this.x = x.clone();
		this.y = y.clone();
	}

	public float[] getX()
	{
		return x;
	}

	public void setX(float[] x)
	{
		this.x = x.clone();
	}

	public float[] getY()
	{
		return y;
	}

	public void setY(float[] y)
	{
		this.y = y.clone();
	}

	public PlaneXY clone()
	{
		PlaneXY m = new PlaneXY(x.clone(), y.clone());
		return m;
	}

}
