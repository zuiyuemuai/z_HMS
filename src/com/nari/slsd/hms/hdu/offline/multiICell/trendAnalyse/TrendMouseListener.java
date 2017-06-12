package com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.internal.win32.HIGHCONTRAST;
import org.jfree.data.xy.HduXYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import com.nari.slsd.hms.hdu.common.data.Floatxy;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.BaseChartJpanel;
import com.nari.slsd.hms.hdu.common.iCell.HduCharPanelInteface;

/**
 * 浮点检测的监听类
 * 
 * @author Administrator
 * 
 */
public class TrendMouseListener implements MouseMotionListener
{

	/*
	 * 0:浮点检测 1:固定浮点 2:平移 3:x轴放大 4：y轴放大
	 */
	public int mouseDragOperation = 5;

	public static float firstpointX;
	public static float firstpointY;
	public static float lastpointX;
	public static float lastpointY;

	public float xline;
	public float yline;
	private Boolean xread = false;
	private Boolean yread = false;

	HduCharPanelInteface charPanelInteface;

	public TrendMouseListener(HduCharPanelInteface charPanelInteface)
	{
		this.charPanelInteface = charPanelInteface;
		this.setXline(0);
	}

	public void startFloatCheck()
	{
		setXYline(xline, yline);
	}

	private void setXYline(float pointx, float pointy)
	{
		this.setXline(pointx);
		this.setYline(pointy);
	}

	// 设置竖向
	private void setXline(float pointx)
	{

		PlaneXY xy = new PlaneXY();
		Map<String, Float> map = charPanelInteface.getXYAxisRange();

		float x[] = { pointx, pointx };
		
		double low = map.get(BaseChartJpanel.YAxisLower);//modified by lqj bug 1174 2015/6/4
		double high = map.get(BaseChartJpanel.YAxisUpper);
		
		float[] y = new float[2];
		y[0] = (float) (low + (high-low)*0.05);
		y[1] = (float) (high - (high-low)*0.05);
	
		xy.setX(x);
		xy.setY(y);
		charPanelInteface.upAutSeriesData(FLOAT_RANGE, xy, Color.green);
		setAnnotation(pointx);
	}

	/**
	 * 设置游标值
	 * 
	 * @param pointx
	 */
	private void setAnnotation(float pointx)
	{
		charPanelInteface.clearTextAnnotation();

		HduXYSeriesCollection collection = charPanelInteface.getCollection();

		List<XYSeries> list = collection.getSeries();

		for (XYSeries pXy : list)
		{

			int i = 0;

			for (; i < pXy.getItemCount(); i++)
			{
				if ((Double) pXy.getX(i) > pointx)
					break;
			}

			if (0 == i || pXy.getItemCount() == i)
			{
				break;
			}
			Floatxy f1 = new Floatxy(new Float((Double) pXy.getX(i - 1)),
					new Float((Double) pXy.getY(i - 1)), Color.black);
			Floatxy f2 = new Floatxy(new Float((Double) pXy.getX(i)),
					new Float((Double) pXy.getY(i)), Color.black);

			float linek = 0, lineb = 0;
			linek = (f2.y - f1.y) / (f2.x - f1.x);
			lineb = f2.y - linek * f2.x;
			float pointy = pointx * linek + lineb;
			charPanelInteface.setTextAnnotation(
					new String().format("(%.2f,%.2f)", pointx, pointy), pointx,
					pointy);

		}
	}

	// 设置横向
	private void setYline(float pointy)
	{
		PlaneXY xy = new PlaneXY();
		Map<String, Float> map = charPanelInteface.getXYAxisRange();

		float x[] = { map.get(BaseChartJpanel.XAxisLower),
				map.get(BaseChartJpanel.XAxisUpper) };
		float y[] = { pointy, pointy };
		xy.setX(x);
		xy.setY(y);
		charPanelInteface.upAutSeriesData(FLOAT_DOMAIN, xy, Color.black);
	}

	public static final String FLOAT_RANGE = "Range";
	public static final String FLOAT_DOMAIN = "Domain";

	public void stopFloatCheck()
	{
		charPanelInteface.deleteSeries(FLOAT_RANGE);
		charPanelInteface.deleteSeries(FLOAT_DOMAIN);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		setXline(BaseChartJpanel.translateScreenToValue(
				charPanelInteface.getChartPanel(),
				new Point(e.getX(), e.getY()))[0]);

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

}
