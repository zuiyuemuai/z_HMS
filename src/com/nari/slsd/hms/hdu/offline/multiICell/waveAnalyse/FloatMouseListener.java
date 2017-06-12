package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Map;

import org.jfree.chart.HduChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.BaseChartJpanel;
import com.nari.slsd.hms.hdu.common.iCell.HduCharPanelInteface;

/**
 * 浮点检测的监听类
 * 
 * @author Administrator
 * 
 */
public class FloatMouseListener implements MouseListener, MouseMotionListener
{

	public FloatMouseListener()
	{

	}

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

	public static final int FLOAT_POINT = 1000;// 浮点检测
	public static final int NEAR_FLOAT_POINT = 1001;// 临近浮点检测
	public static final int NOTHING = 999;// 什么都不做
	public static final int SHOW_JIANXIANG = 1007;// 键像点整周期fft
	// private org.jfree.data.Range nowRange;

	HduChartPanel chartPanel;
	PlaneXY planeXY;
	JFreeChart chart;
	HduCharPanelInteface charPanelInteface;

	public FloatMouseListener(HduChartPanel chartPanel, JFreeChart chart,
			PlaneXY planeXY, HduCharPanelInteface charPanelInteface)
	{
		this.chartPanel = chartPanel;
		this.planeXY = planeXY;
		this.chart = chart;
		this.charPanelInteface = charPanelInteface;
	}

	public void setPlanexy(PlaneXY planeXY)
	{
		this.planeXY = planeXY;
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
		float y[] = { (float) (map.get(BaseChartJpanel.YAxisLower)*0.95f),
				(float) (map.get(BaseChartJpanel.YAxisUpper))*0.95f };
		xy.setX(x);
		xy.setY(y);
		charPanelInteface.upAutSeriesData(FLOAT_RANGE, xy, Color.green);
		
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

	// 当整周期FFT时，选择的范围将一直在键相点之间，或者不变换
	private void jianxiang()
	{
		int i, j;
		boolean rangeFlag = false;
		float[] jianxiangX = OfflineWaveAnalysePanel.jianxiangX.clone();
		float[] jianxiangY = OfflineWaveAnalysePanel.jianxiangY.clone();
		// Log.println("jiangxiang -----"+pressPointX+","+pressPointY);
		// Point pressPoint = new Point((int) pressPointX, (int) pressPointY);
		// double[] first = translateScreenToValue(pressPoint);
		// Log.println("jiangxiang translateScreenToValue-----"+first[0]+","+first[1]);
		double lowRange = 0;
		double upRange = 0;
		if (jianxiangX == null || jianxiangY == null)
		{
			rangeFlag = false;
		}
		for (i = 0; i < jianxiangX.length; i++)
		{
			if (jianxiangX[i] > chartPanel.pressPoint[0]
					&& jianxiangX[i] < chartPanel.releasePoint[0])
			{
				lowRange = jianxiangX[i];
				rangeFlag = true;
				break;
			}
		}

		if (i == jianxiangX.length)
		{
			rangeFlag = false;
		}

		for (j = 0; j < jianxiangX.length - 1; j++)
		{
			if (jianxiangX[j] < chartPanel.releasePoint[0]
					&& jianxiangX[j + 1] > chartPanel.releasePoint[0])
			{
				upRange = jianxiangX[j];
				rangeFlag = true;
				break;
			}

		}

		if (j + 1 == jianxiangX.length)
		{
			if (jianxiangX[j] < chartPanel.releasePoint[0])
			{
				rangeFlag = true;
				upRange = jianxiangX[j];
			} else
			{
				rangeFlag = false;
			}

		}

		if (rangeFlag)
		{

			XYPlot xyPlot = chart.getXYPlot();
			NumberAxis domainAxis = (NumberAxis) xyPlot.getDomainAxis();
			if (chartPanel.pressPoint[0] > chartPanel.releasePoint[0])
			{
				return;
			}
			if (lowRange == upRange || lowRange == 0 || upRange == 0)
			{
				domainAxis.setRange(chartPanel.nowRange);
				// XYPlot xyPlot = super.getChart().getXYPlot();
				// NumberAxis domainAxis = (NumberAxis) xyPlot.getDomainAxis();
				// domainAxis.setRange(lowRange, upRange);
			} else
			{
				domainAxis.setRange(lowRange, upRange);
			}
		}
	}

	/*
	 * 单击事件使用原来的会造成画的线消失
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	private float[] firstPoint;

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		firstpointX = e.getX();
		firstpointY = e.getY();
		firstPoint = BaseChartJpanel.translateScreenToValue(chartPanel,
				new Point((int) firstpointX, (int) firstpointY));
		Point2D point = BaseChartJpanel.translateValueToScreen(chartPanel,
				xline, yline);

		if (FLOAT_POINT == mouseDragOperation
				|| NEAR_FLOAT_POINT == mouseDragOperation)
		{
			if (Math.abs(firstpointX - point.getX()) < 5)
			{
				xread = true;
			} else
			{
				xread = false;
			}

			if (Math.abs(firstpointY - point.getY()) < 5)
			{
				yread = true;
			} else
			{
				yread = false;
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		int button = e.getButton();
		lastpointX = e.getX();
		lastpointY = e.getY();

		float[] mousePoint = new float[2];
		mousePoint = BaseChartJpanel.translateScreenToValue(chartPanel,
				new Point(e.getX(), e.getY()));
		Map<String, Float> map = charPanelInteface.getXYAxisRange();
		if (mousePoint[0] < map.get(BaseChartJpanel.XAxisLower))
		{
			mousePoint[0] = map.get(BaseChartJpanel.XAxisLower);
		}
		if (mousePoint[0] > map.get(BaseChartJpanel.XAxisUpper))
		{
			mousePoint[0] = map.get(BaseChartJpanel.XAxisUpper);
		}

		if (mousePoint[1] < map.get(BaseChartJpanel.YAxisLower))
		{
			mousePoint[1] = map.get(BaseChartJpanel.YAxisLower);
		}
		if (mousePoint[1] > map.get(BaseChartJpanel.YAxisUpper))
		{
			mousePoint[1] = map.get(BaseChartJpanel.YAxisUpper);
		}
		if (button == 1)
		{
			switch (mouseDragOperation)
			{
			case FLOAT_POINT:
			{
				if (xread)
				{
					setXline(mousePoint[0]);
					xline = mousePoint[0];
					CharacterPanel.setCheckPointX(xline);
				} else
				{
					setXline(xline);
					CharacterPanel.setCheckPointX(xline);
				}

				if (yread)
				{
					setYline(mousePoint[1]);
					yline = mousePoint[1];
					CharacterPanel.setCheckPointY(yline);
				} else
				{
					setYline(yline);
					CharacterPanel.setCheckPointY(yline);
				}
			}

				break;
			case NEAR_FLOAT_POINT:
			{

				if (xread)
				{
					int nearPointX = Calculate.getNearPosition(planeXY.getX(),
							mousePoint[0]);
					setXYline(mousePoint[0], planeXY.getY()[nearPointX]);
					xline = mousePoint[0];
					yline = planeXY.getY()[nearPointX];
					CharacterPanel.setCheckPoint(xline, yline);
				}

				if (yread)
				{
					int nearPointY = Calculate.getNearPosition(planeXY.getY(),
							mousePoint[1]);

					setXYline(planeXY.getX()[nearPointY], mousePoint[1]);
					xline = planeXY.getX()[nearPointY];
					yline = mousePoint[1];
					CharacterPanel.setCheckPoint(xline, yline);

				}

			}
				break;
			case SHOW_JIANXIANG:
				jianxiang();
			default:
				break;
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub

		// XYPlot plot = chart.getXYPlot();
		// ValueAxis d = plot.getDomainAxis();// 获取横坐标上的数据值
		// ValueAxis r = plot.getRangeAxis();
		float[] mousePoint = new float[2];
		// Graphics2D graphics2D = (Graphics2D) chartPanel.getGraphics();
		Map<String, Float> map = charPanelInteface.getXYAxisRange();


		mousePoint = BaseChartJpanel.translateScreenToValue(chartPanel,
				new Point(e.getX(), e.getY()));
		System.out.println("upper"+map.get(BaseChartJpanel.YAxisUpper));
		System.out.println("y:"+mousePoint[1]);
		if (mousePoint[0] < map.get(BaseChartJpanel.XAxisLower))
		{
			mousePoint[0] = map.get(BaseChartJpanel.XAxisLower);
			chart.getXYPlot().getDomainAxis().setLowerBound(map.get(BaseChartJpanel.XAxisLower));
		}
		if (mousePoint[0] > map.get(BaseChartJpanel.XAxisUpper))
		{
			mousePoint[0] = map.get(BaseChartJpanel.XAxisUpper);
			chart.getXYPlot().getDomainAxis().setUpperBound(map.get(BaseChartJpanel.XAxisUpper));
		}
		if (mousePoint[1] < map.get(BaseChartJpanel.YAxisLower))
		{
			mousePoint[1] = map.get(BaseChartJpanel.YAxisLower);
			chart.getXYPlot().getRangeAxis().setLowerBound(map.get(BaseChartJpanel.YAxisLower));
		}
		if (mousePoint[1] > map.get(BaseChartJpanel.YAxisUpper))
		{
			mousePoint[1] = map.get(BaseChartJpanel.YAxisUpper);
			chart.getXYPlot().getRangeAxis().setUpperBound(map.get(BaseChartJpanel.YAxisUpper));
		}
		switch (mouseDragOperation)
		{
		// case 0，代表浮点检测
		case FLOAT_POINT:
		{
			chartPanel.setMouseZoomable(false);
			if (xread)
			{
				setXline(mousePoint[0]);
				CharacterPanel.setCheckPointX(mousePoint[0]);
			} else
			{
				setXline(xline);
				CharacterPanel.setCheckPointX(xline);
			}

			if (yread)
			{
				setYline(mousePoint[1]);
				CharacterPanel.setCheckPointY(mousePoint[1]);
			} else
			{
				setYline(yline);
				CharacterPanel.setCheckPointY(yline);
			}

		}
			break;

		// case 1，表示附近浮点检测
		case NEAR_FLOAT_POINT:
		{
			chartPanel.setMouseZoomable(false);

			if (xread)
			{
				int nearPointX = Calculate.getNearPosition(planeXY.getX(),
						mousePoint[0]);
				setXYline(mousePoint[0], planeXY.getY()[nearPointX]);
				CharacterPanel.setCheckPoint(mousePoint[0],
						planeXY.getY()[nearPointX]);
			}

			if (yread)
			{
				int nearPointY = Calculate.getNearPosition(planeXY.getY(),
						mousePoint[1]);

				setXYline(planeXY.getX()[nearPointY], mousePoint[1]);
				CharacterPanel.setCheckPoint(planeXY.getX()[nearPointY],
						mousePoint[1]);
			}

		}
		}

	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	// private int last
	// private int getNearPosition(float shuzu[], float value)
	// {
	// float temp = Math.abs(value - shuzu[0]);
	// int j = 0;
	// for (int i = 1; i < shuzu.length; i++)
	// {
	// if (Math.abs(value - shuzu[i]) - temp < 0.00001)
	// {
	// temp = value - shuzu[i];
	// j = i;
	// }
	// if (value < shuzu[i])
	// break;
	// }
	// return j;
	// }

}
