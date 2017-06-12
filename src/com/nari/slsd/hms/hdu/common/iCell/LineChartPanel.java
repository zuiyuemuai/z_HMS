package com.nari.slsd.hms.hdu.common.iCell;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;

import org.jfree.chart.HduChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.HduXYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse.TrendMouseListener;

/**
 * 南瑞水电站监护系统基本图元 直线和点的chart
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class LineChartPanel extends BaseChartJpanel implements
		HduCharPanelInteface
{

	private XYLineAndShapeRenderer xylineandshaperenderer;// 对于系列点的设置
	/* 系列数据集合 */
	private HduXYSeriesCollection lineSeriesCollection;
	/** 记录系列名字与其配置的对应关系 **/
	private HashMap<String, SeriesProperties> nameProp;

	public PlaneXY planeXY;

	/**
	 * 
	 * @param chartName
	 *            chart的名称 seriesName系列的名称
	 * @param planeXY
	 *            数据
	 * @param frameColor
	 *            网格的颜色，背景默认白色
	 */
	public LineChartPanel()
	{
		this(" ");
	}

	public LineChartPanel(Color frameColor)
	{
		this(" ", frameColor);
	}

	public LineChartPanel(String chartName)
	{
		this(chartName, Color.gray);
	}

	public LineChartPanel(String chartName, Color frameColor)
	{
		super(chartName, frameColor);
	}

	@Override
	protected JFreeChart createJFreeChart()
	{
		// TODO Auto-generated method stub
		lineSeriesCollection = new HduXYSeriesCollection();// 一定要放这里
		nameProp = new HashMap<String, SeriesProperties>();
		return creat_LineChart(lineSeriesCollection, chartName, "X", "Y",
				JFreeChart.DEFAULT_TITLE_FONT, frameColor);
	}

	/**
	 * Describe :创建折现图表，设置图表基本属性 入口参数：XYDataset dataset表示传入图表的数据集，String
	 * title表示图表的名字， String xTitle yTitle 分别表示横坐标、纵坐标的名字，Font font 表示图表中文字的字体
	 * 返回值：JFreeChart chart 返回一个图表
	 */
	private JFreeChart creat_LineChart(XYDataset dataset, String title,
			String xTitle, String yTitle, Font font, Color frameColor)
	{

		NumberAxis xnumberaxis = new NumberAxis();
		NumberAxis ynumberaxis = new NumberAxis();

		xnumberaxis.setLowerMargin(0.0D);
		xnumberaxis.setUpperMargin(0.0D);
		ynumberaxis.setLowerMargin(0.0D);// 上边边距是最大值的百分之五
		ynumberaxis.setUpperMargin(0.0D);
		// 自适应时候取消包括0点 chl 2015.5.23
		ynumberaxis.setAutoRangeIncludesZero(false);

		XYLineAndShapeRenderer xyLineAndShapeRenderer = new XYLineAndShapeRenderer();
		xyLineAndShapeRenderer.setBaseShapesVisible(false);// 关闭显示点

		plot = new XYPlot(dataset, xnumberaxis, ynumberaxis,
				xyLineAndShapeRenderer);
		plot.setDomainPannable(true);// 沿着y轴平移
		plot.setRangePannable(true);// 沿着x轴平移
		plot.setNoDataMessage("No data to display");// 此名可用来做当没有数据的时候jfreechart面板上的显示

		chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
		chart.setBackgroundPaint(Color.white);

		// 修改绘图区域中的点和线，可以通过renderer
		xylineandshaperenderer = (XYLineAndShapeRenderer) plot.getRenderer();

		TextTitle t = chart.getTitle();
		t.setFont(font);// 设置标题字体

		plot.setDomainGridlinePaint(frameColor);// 设置横网格线颜色
		plot.setRangeGridlinePaint(frameColor);// 设置纵网格线颜色

		// 修改坐标轴信息
		ValueAxis valueAxis = plot.getDomainAxis();

		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setLabelPaint(frameColor);
		valueAxis.setTickMarkPaint(frameColor);
		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setTickLabelPaint(frameColor);
		valueAxis.setAxisLineVisible(false);

		valueAxis = plot.getRangeAxis();
		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setLabelPaint(frameColor);
		valueAxis.setTickMarkPaint(frameColor);
		valueAxis.setAxisLinePaint(frameColor);
		valueAxis.setTickLabelPaint(frameColor);
		valueAxis.setAxisLineVisible(false);

		return chart;

	}

	// 波形是否设置有百分之5的预留
	public void setMargin5()
	{
		ValueAxis valueAxis = plot.getRangeAxis();
		valueAxis.setLowerMargin(0.05D);// 上边边距是最大值的百分之五
		valueAxis.setUpperMargin(0.05D);
	}

	// 设置横向网格是否显示
	public void setRangeGridlinesVisible(boolean visible)
	{
		plot.setRangeGridlinesVisible(visible);
	}

	public HduXYSeriesCollection getCollection()
	{
		return lineSeriesCollection;
	}
	
	
	/**
	 * 设置系列名称显示
	 * 
	 * @param seriesname
	 *            系列的名称
	 */
	public void setOpenSeriesVisibleInLegend(String seriesname)
	{
		Object id = getSeriesID(seriesname);
		if (id != null)
		{
			xylineandshaperenderer.setSeriesVisibleInLegend((Integer) id, true);
		}
	}
	

	/**
	 * 设置系列名称是否显示
	 * 
	 * @param seriesname
	 *            系列的名称
	 */
	public void setOpenSeriesVisibleInLegend(String seriesname, boolean on)
	{
		Object id = getSeriesID(seriesname);
		if (id != null)
		{
			xylineandshaperenderer.setSeriesVisibleInLegend((Integer) id, on);
		}
	}

	public void setOpenSeriesVisibleInLegend(int id)
	{
		xylineandshaperenderer.setSeriesVisibleInLegend((Integer) id, true);

	}

	/**
	 * 重写方法 设置一个系列的颜色，和是否相连
	 * 
	 * @param index
	 * @param c
	 * @param isPoint
	 */
	protected void setRenderer(SeriesProperties properties)
	{
		int index = properties.id;
		Color c = properties.color;
		boolean isPoint = false;
		if (properties.nature == SeriesProperties.NATURE_POINT)
		{
			isPoint = true;
		}

		// 设置系列点颜色
		xylineandshaperenderer.setSeriesPaint(index, c);

		// 设置点，默认是线
		xylineandshaperenderer.setSeriesLinesVisible(index, !isPoint);
		xylineandshaperenderer.setSeriesShapesVisible(index, isPoint);
		
		if (isPoint)
		{
			// 设置点的形状粗细
			xylineandshaperenderer.setSeriesShape(index, new Ellipse2D.Double(
					-1.0D,// 坐标
					-1.0D, // 坐标
					2.0D, // 宽
					2.0D));// 高
		} else
		{
			xylineandshaperenderer.setSeriesStroke(index, new BasicStroke(
					(float) properties.wide));

		}
	}

	/**
	 * 新增一个系列
	 * 
	 * @param name
	 *            系列名称
	 * @param PlaneXY
	 *            一个平面信息数据
	 * @param c
	 *            这个系列的显示颜色
	 * @param isPoint
	 *            该系列点是否相连接，如果是点的话不连接
	 */
	private void addSeries(String name, PlaneXY xy, Color c, double wide,
			boolean isPoint)
	{
		super.addSeries(lineSeriesCollection, name, xy, c);

		// 将名字和id对应起来
		if (isPoint)
		{
			nameProp.put(name,
					new SeriesProperties(
							lineSeriesCollection.getSeriesCount() - 1, c,
							SeriesProperties.NATURE_POINT,
							xylineandshaperenderer, wide));
		} else
		{
			nameProp.put(name,
					new SeriesProperties(
							lineSeriesCollection.getSeriesCount() - 1, c,
							SeriesProperties.NATURE_LINE,
							xylineandshaperenderer, wide));
		}

		// 设置点颜色和是否相连
		setRenderer(nameProp.get(name));

	}

	/**
	 * 由系列名字获取系列的ID值
	 * 
	 * @param name
	 * @return null为未找到
	 */
	public int getSeriesID(String name)
	{
		return nameProp.get(name).id;
	}

	public boolean isNewSeries(String name)
	{
		return !nameProp.containsKey(name);
	}

	public void setCloseSeriesVisibleInLegend(String seriesname)
	{
		super.setCloseSeriesVisibleInLegend(nameProp, xylineandshaperenderer,
				seriesname);
	}

	/**
	 * 设置系列名称不显示
	 * 
	 * @param id
	 *            系列的id 默认第一个为0
	 */
	public void setCloseSeriesVisibleInLegend(int id)
	{
		xylineandshaperenderer.setSeriesVisibleInLegend(id, false);
	}

	/**
	 * 更新数据
	 * 
	 * @param planeXY
	 *            数据
	 * @param names
	 *            系列的名称id
	 */

	protected void upSeriesData(String names, PlaneXY planeXY)
	{
		super.upSeriesData(nameProp, lineSeriesCollection, names, planeXY);
		// setPlanexy(planeXY);
	}

	public void setPlanexy(PlaneXY planeXY)
	{
		this.planeXY = planeXY;
	}

	/**
	 * 删除一个系列
	 * 
	 * @param name
	 */
	public SeriesProperties deleteSeries(String name)
	{
		return super.deleteSeries(nameProp, lineSeriesCollection, name);
	}

	/**
	 * 删除全部曲线
	 */
	public void deleteAllSeries()
	{
		Set<String> set = nameProp.keySet();
		String[] arry = new String[set.size()];
		int i = 0;
		for (String name : set)
		{
			arry[i++] = name;
		}
		for (String name : arry)
		{
			deleteSeries(name);
		}
	}

	// 默认是线
	public void upAutSeriesData(String names, PlaneXY planeXY, Color c)
	{
		this.upAutSeriesData(names, planeXY, c, false);
	}

	public void upAutSeriesData(String names, PlaneXY planeXY, Color c,
			double wide)
	{
		this.upAutSeriesData(names, planeXY, c, wide, false);
	}

	public void upAutSeriesData(String names, PlaneXY planeXY, Color c,
			boolean isPoint)
	{
		this.upAutSeriesData(names, planeXY, c, 1D, isPoint);
	}

	/**
	 * 智能更新，如果是新的系列则添加，如果是旧的系列则更新
	 * 
	 * @param names
	 *            系列名称
	 * @param planeXY
	 *            数据
	 * @param c
	 *            显示颜色
	 */
	public void upAutSeriesData(String names, PlaneXY planeXY, Color c,
			double wide, boolean isPoint)
	{
		/* 如果是新的系列则添加 */
		if (isNewSeries(names))
		{
			addSeries(names, planeXY, c, wide, isPoint);
			return;
		} else
		/* 如果不是新的系列则更新 */
		{
			upSeriesData(names, planeXY);
		}
	}

	/**
	 * @param names
	 *            系列名称数组
	 * @param planeXY
	 *            数据集向量
	 * @param c
	 *            颜色
	 * @param isPoint
	 *            是否为点
	 */
	public void upAutSeriesData(String[] names, Vector<PlaneXY> planeXY,
			Color[] c, boolean isPoint)
	{
		for (int i = 0; i < names.length; i++)
		{
			if(c.length > i)//如果颜色数量不够，则用灰色代替
			{
				upAutSeriesData(names[i], planeXY.get(i), c[i], isPoint);
			}
			else 
			{
				upAutSeriesData(names[i], planeXY.get(i), Color.gray, isPoint);
			}
		}
	}

	/**
	 * @param names
	 *            系列名称数组
	 * @param planeXY
	 *            数据集向量
	 * @param c
	 *            颜色
	 * @param isPoint
	 *            是否为点
	 */
	public void upAutSeriesData(String[] names, Vector<PlaneXY> planeXY,
			Color c, boolean isPoint)
	{
		for (int i = 0; i < names.length; i++)
		{
			upAutSeriesData(names[i], planeXY.get(i), c, isPoint);
		}
	}

	/**
	 * 默认黑色
	 * 
	 * @param names
	 * @param planeXYs
	 * @param num
	 */
	public void upAutSeriesData(String[] names, Vector<PlaneXY> planeXYs,
			int num)
	{
		this.upAutSeriesData(names, planeXYs, Color.black, num);
	}

	/**
	 * 智能更新，如果是新的系列则添加，如果是旧的系列则更新 更新num一批
	 * 
	 * @param names
	 *            系列名称
	 * @param planeXYs
	 *            数据
	 * @param c
	 *            显示颜色
	 * @param num
	 *            数量
	 */
	public void upAutSeriesData(String[] names, Vector<PlaneXY> planeXYs,
			Color c, int num)
	{

		if (names.length <= num || planeXYs.size() <= num)
			return;// 边界检测

		for (int i = 0; i < num; i++)
		{
			upAutSeriesData(names[i], planeXYs.get(i), c);
		}
	}

	//下面的使用string来区分，所有整个图上不能有相同的text,不然出现清除不掉的现象
	HashMap<String, XYTextAnnotation> textAnnotationMap = new HashMap<String, XYTextAnnotation>();

	public void removeTextAnnotation(String s)
	{
		if (textAnnotationMap.containsKey(s))
		{
			plot.removeAnnotation(textAnnotationMap.get(s));
			textAnnotationMap.remove(s);
		}
	}

	/**
	 * 设置注释
	 * 
	 * @param s
	 * @param x
	 * @param y
	 */
	public void setTextAnnotation(String s, float x, float y)
	{
		
		XYTextAnnotation xyTextAnnotation = new XYTextAnnotation(s, x, y);

		plot.addAnnotation((XYAnnotation) xyTextAnnotation);

		textAnnotationMap.put(s, xyTextAnnotation);
	}
	public void clearTextAnnotation()
	{
		plot.clearAnnotations();
	}

	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());
		
		float[] x = new float[35000];
		float[] y = new float[35000];
		
		for (int i = 0; i < y.length; i++)
		{
			x[0] = i; 
		}
		
		PlaneXY planeXY = new PlaneXY(x, y);
		ColorMapper colormapper = new ColorMapper(new ColorMapRainbow(), 0, 10);

		org.jzy3d.colors.Color c = colormapper.getColor(3);

		Color color = new Color(c.r, c.g, c.b);

		LineChartPanel lineChartPanel = new LineChartPanel("中文", color);
		lineChartPanel.upAutSeriesData("中p", planeXY, color, 5D, true);
		//lineChartPanel.upAutSeriesData("中l", planeXY, color, 5D);

		lineChartPanel.setXLable("us");
		lineChartPanel.setYLable("us");

		lineChartPanel.chartPanel
				.addMouseMotionListener(new TrendMouseListener(lineChartPanel));
		lineChartPanel.setTextAnnotation("OK", 2f, 0.5f);
		lineChartPanel.removeTextAnnotation("OK");
//		lineChartPanel.setSeriesLableFont("中p", new Font("微软雅黑", Font.BOLD, 30));
		// y[0] = 2;
		// y[2] = 2;
		// y[4] = 2;
		// planeXY = new PlaneXY(x, y);
		// lineChartPanel.upAutSeriesData("2", planeXY, Color.red, true);//
		// 新增一个系列

		// y[0] = 3;
		// y[2] = 3;
		// y[4] = 3;
		// planeXY = new PlaneXY(x, y);
		// lineChartPanel.upAutSeriesData("3", planeXY, Color.blue);// 更新一个系列
		//
		// lineChartPanel.deleteSeries("1");
		// Map<String, Float> range = lineChartPanel.getXAxisRange();
		//
		// lineChartPanel.setCloseLableXandY();
		// lineChartPanel.setTitle(null);// 取消Title
		// lineChartPanel.isSameXAxis(0, 2);

		// lineChartPanel.setCloseSeriesVisibleInLegend(1);// 取消
		//
		// lineChartPanel.xylineandshaperenderer.setSeriesLinesVisible(0,
		// false);
		// // 设置点的形状粗细
		// lineChartPanel.xylineandshaperenderer.setSeriesShape(1,
		// new Ellipse2D.Double(-1.0D,// 坐标
		// -1.0D, // 坐标
		// 2.0D, // 宽
		// 2.0D));// 高
		// lineChartPanel.xylineandshaperenderer.setSeriesShapesVisible(0,
		// true);

		jFrame.add(lineChartPanel, BorderLayout.CENTER);
		jFrame.setVisible(true);

	}

	@Override
	public HduChartPanel getChartPanel()
	{
		// TODO Auto-generated method stub
		return chartPanel;
	}

}
