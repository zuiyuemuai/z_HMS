package com.nari.slsd.hms.hdu.common.iCell;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.HduChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.HduDefaultXYDataset;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.util.JfreeChartSetTheme;
import com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse.TrendMouseListener;

/**
 * 南瑞水电站监护系统基本图元 轴心轨迹图元
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class AxesOrbitChart extends JPanel
{
	protected HduChartPanel chartPanel;
	protected JFreeChart jfreechart;
	protected HduDefaultXYDataset DXY;

	public int height;
	private double size = 1;// 整个的大小系数

	protected String nameString;
	protected XYPlot xyplot;
	protected java.util.List<double[][]> list = new ArrayList<double[][]>();

	public JFreeChart getJFreeChart()
	{
		return jfreechart;
	}

	public HduDefaultXYDataset getDataset()
	{
		return DXY;
	}

	public AxesOrbitChart(String nameString, Color frameColor, Color drawColor,
			double size)
	{
		this(nameString, frameColor, drawColor, size, true);
	}

	/***
	 * 截面图
	 * 
	 * @param nameString
	 *            截面的名称
	 */
	public AxesOrbitChart(String nameString, Color frameColor, Color drawColor,
			double size, boolean isshowBorder)
	{
		this.size = size;
		this.nameString = nameString;
		double[][] datas = new double[2][2];

		JfreeChartSetTheme.set();// 设置主题，否则中文无法显示
		DXY = new HduDefaultXYDataset();// 获取数据集

		// list.add(datas);
		DXY.setSeries(list);

		jfreechart = ChartFactory.createScatterPlot(null, "X", "Y", DXY,
				PlotOrientation.VERTICAL, true, false, true);// 获取chart
		chartPanel = new HduChartPanel(jfreechart);// 获取chart的jpanel

		jfreechart.setBackgroundPaint(Color.white);// 背景颜色

		jfreechart.setBorderStroke(new BasicStroke(10f));// 边框粗细

		// 修改绘图区域中的内容，需要通过plot对象进行设置
		xyplot = (XYPlot) jfreechart.getPlot();

		// 设置没有所需要数据时显示提示信息
		xyplot.setNoDataMessage("no data");
		xyplot.setNoDataMessageFont(new Font("", Font.BOLD, 14));
		xyplot.setNoDataMessagePaint(new Color(87, 149, 117));
		xyplot.setBackgroundPaint(Color.white); // 设置背景颜色
		xyplot.setOutlinePaint(frameColor);

		// 网格线设置
		// xyplot.setDomainGridlineStroke(new BasicStroke(20f));
		xyplot.setDomainGridlinePaint(frameColor);// 网格横线的颜色
		xyplot.setRangeGridlinePaint(frameColor);// 网格横线的颜色

		// 修改坐标轴信息
		ValueAxis valueAxis = xyplot.getDomainAxis();

		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setLabelPaint(frameColor);
		valueAxis.setTickMarkPaint(frameColor);
		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setTickLabelPaint(frameColor);
		valueAxis.setAxisLineVisible(false);

		// NumberAxis numberAxis = (NumberAxis) valueAxis;
		// DecimalFormat dFormat = new DecimalFormat("#0.00");
		// numberAxis.setNumberFormatOverride(dFormat);

		valueAxis = xyplot.getRangeAxis();
		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setLabelPaint(frameColor);
		valueAxis.setTickMarkPaint(frameColor);
		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setTickLabelPaint(frameColor);
		valueAxis.setAxisLineVisible(false);

		// numberAxis = (NumberAxis) valueAxis;
		// numberAxis.setNumberFormatOverride(dFormat);

		// 修改绘图区域中的点和线，可以通过renderer
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		xylineandshaperenderer.setSeriesVisibleInLegend(0, false);// 设置系列名称不显示
		xylineandshaperenderer.setSeriesPaint(0, drawColor);// 设置点的颜色
		// 设置点的形状粗细
		xylineandshaperenderer.setSeriesShape(0, new Ellipse2D.Double(0, // 坐标
				0, // 坐标
				2.0D, // 宽
				2.0D));// 高
		xylineandshaperenderer.setSeriesLinesVisible(0, true);

		xylineandshaperenderer.setSeriesVisibleInLegend(1, false);// 设置系列名称不显示
		xylineandshaperenderer.setSeriesPaint(1, Color.red);// 设置点的颜色
		// 设置点的形状粗细
		xylineandshaperenderer.setSeriesShape(1, new Ellipse2D.Double(0, // 坐标
				0, // 坐标
				8.0D, // 宽
				8.0D));// 高
		xylineandshaperenderer.setSeriesLinesVisible(1, true);

		adujstSize();

		this.setLayout(new BorderLayout());
		this.add(chartPanel, BorderLayout.CENTER);
		this.setBackground(Color.white);
		if (isshowBorder)
		{
			this.setBorder(new TitledBorder(nameString));
		}

	}

	// 设置边框上的名称
	public void setBorderTitle(String nameString)
	{
		this.setBorder(new TitledBorder(nameString));
	}

	/**
	 * 坐标轴上的X和Y标签不显示
	 */
	public void setCloseLableXandY()
	{
		ValueAxis valueAxis = xyplot.getRangeAxis();
		valueAxis.setLabel(null);
		valueAxis = xyplot.getDomainAxis();
		valueAxis.setLabel(null);
	}

	/**
	 * 设置自动调整大小 可能导致闪烁
	 */
	public void setSizeAutAdujst()
	{
		chartPanel.addComponentListener(new SizeListener());
	}

	/**
	 * 设置表格名称
	 * 
	 * @param name
	 */
	public void setTitle(String name)
	{
		jfreechart.setTitle(name);
	}

	// 如果上下边界非常近，导致小数位非常多，图像变形，这里进行处理
	//如果非常近的话小数位直接取0个
	void outSetFormatOverride()
	{
		DecimalFormat dFormat = new DecimalFormat("#0");
		NumberAxis xnumberAxis = (NumberAxis) xyplot.getDomainAxis();
		NumberAxis ynumberAxis = (NumberAxis) xyplot.getRangeAxis();
		
		if(xnumberAxis.getUpperBound() - xnumberAxis.getLowerBound() < 0.01)
		{
			xnumberAxis.setNumberFormatOverride(dFormat);
		}else {
			xnumberAxis.setNumberFormatOverride(null);			
		}
		if(ynumberAxis.getUpperBound() - ynumberAxis.getLowerBound() < 0.01)
		{
			ynumberAxis.setNumberFormatOverride(dFormat);
		}else {
			ynumberAxis.setNumberFormatOverride(null);			
		}
	}

	/**
	 * 输入一个planeset进行显示
	 * 
	 * @param xList
	 * @param yList
	 * @param Max
	 *            两个轴的最大界限
	 * @param Min
	 *            最小界限
	 */
	public void display(float[] xList, float[] yList, float Max, float Min)
	{

		int size = xList.length;
		int ysize = yList.length;
		if (size <= 0 || ysize <= 0)
		{
			return;// 如果两个通道中一个没有数据则退出
		}
		size = Math.min(size, ysize);// 如果两个通道数据量不一致，则取最小的数量

		double[][] datas = new double[2][size];

		for (int i = 0; i < size; i++)
		{
			datas[0][i] = xList[i] + 0.05;
			datas[1][i] = yList[i] + 0.05;
		}
		list.clear();

		double[][] firstPoint = new double[2][1];
		firstPoint[0][0] = xList[0] + 0.05;
		firstPoint[1][0] = yList[0] + 0.05;

		list.add(datas);
		list.add(firstPoint);

		DXY.setSeries(list);

		ValueAxis valueAxis = xyplot.getDomainAxis();
		valueAxis.setRange(Min, Max);
		valueAxis = xyplot.getRangeAxis();
		valueAxis.setRange(Min, Max);
		outSetFormatOverride();
	}

	/**
	 * 输入一个planeset进行显示 自动适应
	 * 
	 * @param realAnalogs
	 * @param realAnalogs2
	 * @param issuit是否自动适应
	 * 
	 */
	public void display(float[] realAnalogs, float[] realAnalogs2,
			boolean issuit)
	{

		int size = realAnalogs.length;
		int ysize = realAnalogs2.length;
		float Max = Float.MIN_VALUE;
		float Min = Float.MAX_VALUE;

		if (size <= 0 || ysize <= 0)
		{
			return;// 如果两个通道中一个没有数据则退出
		}
		size = Math.min(size, ysize);// 如果两个通道数据量不一致，则取最小的数量

		double[][] datas = new double[2][size];

		for (int i = 0; i < size; i++)
		{
			datas[0][i] = realAnalogs[i] + 0.05;
			datas[1][i] = realAnalogs2[i] + 0.05;
			Max = Math.max(Max, realAnalogs[i]);
			Max = Math.max(Max, realAnalogs2[i]);
			Min = Math.min(Min, realAnalogs[i]);
			Min = Math.min(Min, realAnalogs2[i]);
		}

		list.clear();
		double[][] firstPoint = new double[2][1];
		firstPoint[0][0] = realAnalogs[0] + 0.05;
		firstPoint[1][0] = realAnalogs2[0] + 0.05;

		list.add(datas);
		list.add(firstPoint);

		DXY.setSeries(list);

		if (issuit)
		{

			ValueAxis valueAxis = xyplot.getDomainAxis();
			valueAxis.setRange(Min, Max);
			valueAxis = xyplot.getRangeAxis();
			valueAxis.setRange(Min, Max);
		} else
		{
			ValueAxis valueAxis = xyplot.getDomainAxis();
			valueAxis.setRange(valueAxis.getLowerBound(),
					valueAxis.getUpperBound());
			valueAxis = xyplot.getRangeAxis();
			valueAxis.setRange(valueAxis.getLowerBound(),
					valueAxis.getUpperBound());
		}
		outSetFormatOverride();
	}

	public JPanel getPanel()
	{
		return this;
	}

	/**
	 * 调整大小，使之保持正方形
	 */
	private void adujstSize()
	{
		height = chartPanel.getHeight();

		// if (nameString != null)
		// {
		// TextTitle textTitle = new TextTitle(nameString, new Font("宋体",
		// Font.PLAIN, height / 12));
		// jfreechart.setTitle(textTitle);
		// }

		chartPanel.setPreferredSize(new Dimension((int) (height / size),
				(int) (height / size)));// 设置大小

		chartPanel.updateUI();// 更新界面

	}
	
	/**
	 * 显示单位
	 * @param name 单位
	 */
	public void setXLabel(String name){
		ValueAxis valueAxis = xyplot.getDomainAxis();
		valueAxis.setLabel(name);
		valueAxis.setLabelAngle(2*Math.PI);
	}
	
	
	public void setYLabel(String name){
		ValueAxis valueAxis = xyplot.getRangeAxis();
		valueAxis.setLabel(name);
		valueAxis.setLabelAngle(2*Math.PI);
	}

	/**
	 * 外部获取大小自动适应的监听类
	 * 
	 * @return
	 */
	public ComponentListener getSizeListener()
	{
		return new SizeListener();
	}

	/**
	 * 对于窗体大小改变的监听
	 * */
	class SizeListener implements ComponentListener
	{

		@Override
		public void componentResized(ComponentEvent e)
		{
			// TODO Auto-generated method stub
			adujstSize();
		}

		@Override
		public void componentMoved(ComponentEvent e)
		{
			// TODO Auto-generated method stub
			adujstSize();
		}

		@Override
		public void componentShown(ComponentEvent e)
		{
			// TODO Auto-generated method stub
			adujstSize();
		}

		@Override
		public void componentHidden(ComponentEvent e)
		{
			// TODO Auto-generated method stub
			adujstSize();
		}

	}
	
	
	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());
		
		

		AxesOrbitChart lineChartPanel = new AxesOrbitChart("中文", Color.cyan, Color.BLACK, 50);
		

		jFrame.add(lineChartPanel, BorderLayout.CENTER);
		jFrame.setVisible(true);

	}
	
}
