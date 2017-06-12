package com.nari.slsd.hms.hdu.common.iCell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.HDUXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.HduXYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;

/**
 * 南瑞水电站监护系统基本图元
 * 可以画区域类型的chart（其中还可以画线和点）
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class AreaChartPanel extends JPanel
{

	private HDUXYAreaRenderer areaRenderer;
	private XYLineAndShapeRenderer lineRenderer;
	private XYLineAndShapeRenderer pointRenderer;

	/* 系列数据集合 */
	private HduXYSeriesCollection areaSeriesCollection;
	private HduXYSeriesCollection lineSeriesCollection;
	private HduXYSeriesCollection pointSeriesCollection;

	/** 记录系列名字与其配置的对应关系 **/
	private HashMap<String, SeriesProperties> areanameProp;
	private HashMap<String, SeriesProperties> linenameProp;
	private HashMap<String, SeriesProperties> pointnameProp;

	public ChartPanel chartPanel;
	public JFreeChart chart;

	public String chartName;
	protected Color frameColor;// 网格的颜色
	protected XYPlot plot; // 当前坐标区域

	protected boolean Rangesuit = false;// x轴自适应标志
	protected boolean Domainsuit = false;// y轴自适应标志

	public class SeriesProperties
	{
		public static final int NATURE_LINE = 1;
		public static final int NATURE_POINT = 2;
		public static final int NATURE_AREAR = 0;

		public int id;// 在集合中的下标
		public Color color;// 颜色

		public int nature;// 是什么 线还是点还是柱状图
		public AbstractXYItemRenderer renderer;// 绘图器

		public SeriesProperties()
		{
			this(0, Color.black);
		}

		public SeriesProperties(int id, Color c)
		{
			this(id, c, NATURE_LINE, null);
		}

		public SeriesProperties(int id, Color c, int nature,
				AbstractXYItemRenderer renderer)
		{
			this.id = id;
			this.color = c;
			this.nature = nature;
			this.renderer = renderer;
		}

	}
	
	public static final String XAxisLower = "XLower";
	public static final String XAxisUpper = "XUpper";
	public static final String YAxisLower = "YLower";
	public static final String YAxisUpper = "YUpper";

	/**
	 * 获取X轴的范围
	 * 
	 * @return
	 */
	public Map<String, Float> getXAxisRange()
	{
		ValueAxis axis = plot.getDomainAxis();
		Map<String, Float> retMap = new HashMap<String, Float>();
		retMap.put(XAxisLower, (float) axis.getLowerBound());
		retMap.put(XAxisUpper, (float) axis.getUpperBound());

		return retMap;
	}

	/**
	 * 获取Y轴的范围
	 * 
	 * @return
	 */
	public Map<String, Float> getYAxisRange()
	{
		ValueAxis axis = plot.getRangeAxis();
		Map<String, Float> retMap = new HashMap<String, Float>();
		retMap.put(YAxisLower, (float) axis.getLowerBound());
		retMap.put(YAxisUpper, (float) axis.getUpperBound());

		return retMap;
	}

	public Map<String, Float> getXYAxisRange()
	{
		ValueAxis axis = plot.getRangeAxis();
		Map<String, Float> retMap = new HashMap<String, Float>();
		retMap.put(YAxisLower, (float) axis.getLowerBound());
		retMap.put(YAxisUpper, (float) axis.getUpperBound());
		axis = chart.getXYPlot().getDomainAxis();
		retMap.put(XAxisLower, (float) axis.getLowerBound());
		retMap.put(XAxisUpper, (float) axis.getUpperBound());
		return retMap;
	}
	/**
	 * 对于坐标系变化的监听
	 * 
	 * @param listener
	 *            AxisChangeListener监听
	 */
	public void addAxisChangedListener(AxisChangeListener listener)
	{
		XYPlot plot = chart.getXYPlot(); // 获得坐标区域
		Axis domainAxis = plot.getDomainAxis();// 获取横坐标
		Axis RangeAxis = plot.getRangeAxis();// 获取纵坐标
		domainAxis.addChangeListener(listener);
		RangeAxis.addChangeListener(listener);
	}

	public AreaChartPanel()
	{
		this(" ");
	}

	public AreaChartPanel(String chartName)
	{
		// super(chartName, frameColor);
		this.chartName = chartName;
		// chart的参数通过上面的全局变量传入
		chart = createJFreeChart();

		plot = chart.getXYPlot();

		chartPanel = new ChartPanel(chart);

		this.setLayout(new BorderLayout());
		this.add(chartPanel, BorderLayout.CENTER);

	}

	protected JFreeChart createJFreeChart()
	{
		areaSeriesCollection = new HduXYSeriesCollection();
		pointSeriesCollection = new HduXYSeriesCollection();
		lineSeriesCollection = new HduXYSeriesCollection();

		areanameProp = new HashMap<String, SeriesProperties>();
		linenameProp = new HashMap<String, AreaChartPanel.SeriesProperties>();
		pointnameProp = new HashMap<String, AreaChartPanel.SeriesProperties>();

		return creat_PolarChart(areaSeriesCollection, chartName,
				JFreeChart.DEFAULT_TITLE_FONT, frameColor);
	}

	NumberAxis xnumberaxis;
	NumberAxis ynumberaxis;

	/**
	 * 设置x轴方向的坐标范围
	 * 
	 * @param lx
	 * @param hx
	 */
	public void setXaxis(double lx, double hx)
	{
		plot.getDomainAxis().setRange(lx, hx);
	}
	/**
	 * 设置y轴方向的坐标范围
	 * 
	 * @param lx
	 * @param hx
	 */
	public void setYaxis(double lx, double hx)
	{
		plot.getRangeAxis().setRange(lx, hx);
	}
	
	
	/**
	 * 设置注释
	 * @param s
	 * @param x
	 * @param y
	 */
	public void setTextAnnotation(String s, float x, float y)
	{
		plot.addAnnotation((XYAnnotation) new XYTextAnnotation(s, x, y));  
	}
	
	
	private JFreeChart creat_PolarChart(XYDataset dataset, String title,
			Font font, Color frameColor)
	{
		// TODO Auto-generated method stub
		xnumberaxis = new NumberAxis();
		xnumberaxis.setLowerMargin(0.0D);// 上边边距是最大值的百分之五
		xnumberaxis.setUpperMargin(0.0D);
		ynumberaxis = new NumberAxis();
		ynumberaxis.setLowerMargin(0.00D);// 上边边距是最大值的百分之五
		ynumberaxis.setUpperMargin(0.05D);

		areaRenderer = new HDUXYAreaRenderer(areaSeriesCollection);
		lineRenderer = new XYLineAndShapeRenderer();
		pointRenderer = new XYLineAndShapeRenderer();
		pointRenderer.setSeriesShape(1, new Ellipse2D.Double(1, // 坐标
				0, // 坐标
				4.0D, // 宽
				4.0D));// 高
		
		lineRenderer.setBaseShapesVisible(false);// 关闭显示点
		
		XYPlot plot = new XYPlot(dataset, xnumberaxis, ynumberaxis,
				lineRenderer);
		plot.setForegroundAlpha(0.5f);// 设置透明度
		plot.setDomainGridlinePaint(Color.black);// 设置横网格线颜色
		plot.setRangeGridlinePaint(Color.black);// 设置纵网格线颜色

		JFreeChart jfreechart = new JFreeChart(title,
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);

		jfreechart.setBackgroundPaint(Color.white);
		plot.setBackgroundPaint(Color.white);
		// plot.setBackgroundImageAlignment(PolarPlot.MINIMUM_HEIGHT_TO_DRAW);

		plot.setRenderer(2, areaRenderer);
		plot.setRenderer(1, lineRenderer);
		plot.setRenderer(0, pointRenderer);

		plot.setDataset(2, areaSeriesCollection);
		plot.setDataset(1, lineSeriesCollection);
		plot.setDataset(0, pointSeriesCollection);

		xnumberaxis.setAutoRange(true);
		ynumberaxis.setAutoRange(true);
		// xnumberaxis.setAutoTickUnitSelection(true);

		return jfreechart;// efficiency
	}
	
	public void setCloseSeriesVisibleInLegend(int type, int id)
	{
		XYItemRenderer renderer;
		if (type == SeriesProperties.NATURE_AREAR )
			
			renderer = areaRenderer;
		else if(type == SeriesProperties.NATURE_LINE)
		{
			renderer = lineRenderer;
		}else {
			renderer = pointRenderer;
		}
		
		renderer.setSeriesVisibleInLegend(id, false);
	}
	

	public boolean isNewSeries(String name)
	{
		return !(areanameProp.containsKey(name)
				|| linenameProp.containsKey(name) || pointnameProp
					.containsKey(name));
	}

	/**
	 * 设置鼠标拖动时是x向操作
	 */
	public void setCloseMouseDragOperation_XY()
	{
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
	}

	/**
	 * 设置Y自动适应
	 * 
	 * @param is
	 */
	public void setRangeSuit(boolean is)
	{
		Rangesuit = is;
		if (!is)
		{
			chartPanel.restoreAutoRangeBounds();
		}

	}

	/**
	 * 设置X自动适应
	 * 
	 * @param is
	 */
	public void setDomainSuit(boolean is)
	{
		Domainsuit = is;
		if (!is)
		{
			chartPanel.restoreAutoDomainBounds();
		}
	}

	public void setBothSuit(boolean is)
	{
		this.setDomainSuit(is);
		this.setRangeSuit(is);
	}

	/**
	 * 由系列名字获取系列的ID值
	 * 
	 * @param name
	 * @return null为未找到
	 */
	public int getSeriesID(HashMap<String, SeriesProperties> nameProp,
			String name)
	{
		return nameProp.get(name).id;
	}

	protected boolean isNewSeries(HashMap<String, SeriesProperties> map,
			String name)
	{
		return !map.containsKey(name);
	}

	/**
	 * 设置某个系列的颜色
	 * 
	 * @param renderer
	 *            绘图器
	 * @param seriesname
	 *            系列的名称
	 * @param c
	 *            颜色
	 */
	public void setSeriesColor(int id, AbstractXYItemRenderer renderer, Color c)
	{
		renderer.setSeriesPaint((Integer) id, c);
	}

	protected void setRenderer(SeriesProperties properties)
	{
		setSeriesColor(properties.id, properties.renderer, properties.color);
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
	private void addSeries(String name, PlaneXY xy, Color c, int type)
	{
		XYSeries xyseries;
		if (type == SeriesProperties.NATURE_AREAR || type == SeriesProperties.NATURE_LINE)
			xyseries = new XYSeries(name, false);// 不自动排序
		else
		{
			xyseries = new XYSeries(name);// 自动排序
		}

		float[] Xvalue = xy.getX();
		float[] Yvalue = xy.getY();
		int num = xy.getNum();

		for (int i = 0; i < num; i++)
		{
			xyseries.add(Xvalue[i], Yvalue[i]);
		}

		if (SeriesProperties.NATURE_AREAR == type)
		{
			areaSeriesCollection.addSeries(xyseries);
			// 将名字和id对应起来
			areanameProp.put(name,
					new SeriesProperties(
							areaSeriesCollection.getSeriesCount() - 1, c, type,
							areaRenderer));
			// 设置点颜色和是否相连
			setRenderer(areanameProp.get(name));
		} else if (SeriesProperties.NATURE_LINE == type)
		{
			lineSeriesCollection.addSeries(xyseries);
			// 将名字和id对应起来
			linenameProp.put(name,
					new SeriesProperties(
							lineSeriesCollection.getSeriesCount() - 1, c, type,
							lineRenderer));
			// 设置点颜色和是否相连
			setRenderer(linenameProp.get(name));
		} else
		{
			pointSeriesCollection.addSeries(xyseries);
			// 将名字和id对应起来
			pointnameProp.put(name,
					new SeriesProperties(
							pointSeriesCollection.getSeriesCount() - 1, c,
							type, pointRenderer));
			// 设置点颜色和是否相连
			setRenderer(pointnameProp.get(name));
		}

		// 自动适应相关
		if (Domainsuit)
		{
			chartPanel.restoreAutoDomainBounds();
		}
		if (Rangesuit)
		{
			chartPanel.restoreAutoRangeBounds();
		}

		xnumberaxis.setRange(150, 370);
		ynumberaxis.setRange(82, 112);

	}

	/**
	 * 更新数据
	 * 
	 * @param planeXY
	 *            数据
	 * @param names
	 *            系列的名称id
	 */

	protected void upSeriesData(String names, PlaneXY planeXY, int type)
	{
		int id = 0;
		XYSeries xyseries = null;

		if (0 == type)
		{
			xyseries = new XYSeries(names, false);// 不自动排序
			id = getSeriesID(areanameProp, names);
		} else if (1 == type)
		{
			xyseries = new XYSeries(names);// 自动排序
			id = getSeriesID(linenameProp, names);
		} else if (2 == type)
		{
			xyseries = new XYSeries(names);// 自动排序
			id = getSeriesID(pointnameProp, names);
		}

		float[] Xvalue = planeXY.getX();
		float[] Yvalue = planeXY.getY();
		int num = planeXY.getNum();

		for (int i = 0; i < num; i++)
		{
			xyseries.add(Xvalue[i], Yvalue[i]);
		}

		if (0 == type)
		{
			areaSeriesCollection.setSeries(id, xyseries);
		} else if (1 == type)
		{
			lineSeriesCollection.setSeries(id, xyseries);
		} else
		{
			pointSeriesCollection.setSeries(id, xyseries);
		}

		// 自动适应相关
		if (Domainsuit)
		{
			chartPanel.restoreAutoDomainBounds();
		}
		if (Rangesuit)
		{
			chartPanel.restoreAutoRangeBounds();
		}
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
	 * 
	 *            type 0为area 1为line 2为point
	 */
	public void upAutSeriesData(String names, PlaneXY planeXY, Color c, int type)
	{
		/* 如果是新的系列则添加 */
		if (isNewSeries(names))
		{
			addSeries(names, planeXY, c, type);
			return;
		} else
		/* 如果不是新的系列则更新 */
		{
			upSeriesData(names, planeXY, type);
		}
	}

	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());
		float x[] = { 0, 90, 100, 180, 270, 320 };
		float y[] = { 0.8f, 1, 0.5f, 0.7f, 0.1f, 0.6f };
		PlaneXY planeXY = new PlaneXY(x, y);
		AreaChartPanel lineChartPanel = new AreaChartPanel("中文");
		lineChartPanel.upAutSeriesData("OK", planeXY, Color.blue, 0);
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

}
