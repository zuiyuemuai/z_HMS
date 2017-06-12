package com.nari.slsd.hms.hdu.common.iCell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;

import org.jfree.chart.HduChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.HduXYSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;


/**
 * 南瑞水电站监护系统基本图元
 * 柱状图图元
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class BarChartPanel extends BaseChartJpanel implements
		HduCharPanelInteface
{
	private XYBarRenderer xybarrenderer;
	private XYLineAndShapeRenderer xylineandshaperenderer;// 对于系列点的设置

	/* 系列数据集合 */
	private HduXYSeriesCollection lineSeriesCollection;// 专门用于画浮点检测的线
	private HduXYSeriesCollection barSeriesCollection;
	/** 记录系列名字与其配置的对应关系 **/
	private HashMap<String, SeriesProperties> lineNameProp;
	private HashMap<String, SeriesProperties> barNameProp;

	public BarChartPanel()
	{
		this(" ");
	}

	public BarChartPanel(String chartName)
	{
		this(chartName, Color.gray);
	}

	public BarChartPanel(String chartName, Color frameColor)
	{
		super(chartName, frameColor);
	}

	@Override
	protected JFreeChart createJFreeChart()
	{
		// TODO Auto-generated method stub
		lineSeriesCollection = new HduXYSeriesCollection();
		barSeriesCollection = new HduXYSeriesCollection();
		lineNameProp = new HashMap<String, BaseChartJpanel.SeriesProperties>();
		barNameProp = new HashMap<String, BaseChartJpanel.SeriesProperties>();

		return createChart(lineSeriesCollection, barSeriesCollection, chartName);
	}

	public JFreeChart createChart(XYDataset linedataset, XYDataset bardataset,
			String title)
	{
		NumberAxis xnumberaxis = new NumberAxis();
		NumberAxis ynumberaxis = new NumberAxis();

		xnumberaxis.setLowerMargin(0.0D);
		xnumberaxis.setUpperMargin(0.0D);
		ynumberaxis.setLowerMargin(0.00D);
		ynumberaxis.setUpperMargin(0.05D);

		xybarrenderer = new XYBarRenderer();

		// 消去水晶和阴影效果
		xybarrenderer.setBarPainter(new StandardXYBarPainter());
		xybarrenderer.setShadowVisible(false);

		// xybarrenderer.setSeriesItemLabelPaint(0, Color.green);

		xybarrenderer.setMargin(0.5F);

		XYBarDataset barDataset = new XYBarDataset(bardataset, 0.10D);

		plot = new XYPlot(barDataset, xnumberaxis, ynumberaxis, xybarrenderer);
		plot.setDomainPannable(true);// 沿着y轴平移
		plot.setRangePannable(true);// 沿着x轴平移
		plot.setNoDataMessage("No data to display");// 此名可用来做当没有数据的时候jfreechart面板上的显示

		JFreeChart jfreechart = new JFreeChart(title,
				JFreeChart.DEFAULT_TITLE_FONT, plot, true);

		jfreechart.setBackgroundPaint(Color.white);

		// 配置浮点检测的线
		xylineandshaperenderer = new XYLineAndShapeRenderer();
		plot.setRenderer(1, xylineandshaperenderer);
		plot.setDataset(1, linedataset);

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

		return jfreechart;
	}

	/**
	 * 设置一个系列的颜色，和是否相连
	 * 
	 * @param index
	 * @param c
	 * @param isPoint
	 */
	private void setRenderer(int index, Color c)
	{
		// 设置系列点颜色
		xybarrenderer.setSeriesPaint(index, c);

	}

	@Override
	public void upAutSeriesData(String names, PlaneXY planeXY, Color c)
	{
		// TODO Auto-generated method stub
		this.upAutSeriesData(names, planeXY, c, true);
	}
	
	
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
	
	@Override
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
	 * @param names
	 *            系列名称数组
	 * @param planeXY
	 *            数据集向量
	 * @param c
	 *            颜色数组
	 * @param isBar
	 *            是否为柱状图
	 */
	public void upAutSeriesData(String[] names, Vector<PlaneXY> planeXY,
			Color[] c, boolean isBar)
	{
		for (int i = 0; i < names.length; i++)
		{
			upAutSeriesData(names[i], planeXY.get(i), c[i], isBar);
		}
	}

	/**
	 * 删除全部曲线
	 */
	public void deleteAllSeries()
	{
		Set<String> set = barNameProp.keySet();
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

	/**
	 * @param names
	 *            系列名称数组
	 * @param planeXY
	 *            数据集向量
	 * @param c
	 *            颜色
	 * @param isBar
	 *            是否为柱状图
	 */
	public void upAutSeriesData(String[] names, Vector<PlaneXY> planeXY,
			Color c, boolean isBar)
	{
		for (int i = 0; i < names.length; i++)
		{
			upAutSeriesData(names[i], planeXY.get(i), c, isBar);
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
	 * @param isBar
	 *            是的话将用柱状图显示，如果不是的话将用线显示
	 */
	public void upAutSeriesData(String names, PlaneXY planeXY, Color c,
			boolean isBar)
	{
		HashMap<String, SeriesProperties> hashMap;
		if (isBar)
		{
			hashMap = barNameProp;
		} else
		{
			hashMap = lineNameProp;
		}
		/* 如果是新的系列则添加 */
		if (isNewSeries(hashMap, names))
		{
			addSeries(names, planeXY, c, isBar);
			return;
		} else
		/* 如果不是新的系列则更新 */
		{
			upSeriesData(names, planeXY, isBar);
		}

	}

	protected void upSeriesData(String names, PlaneXY planeXY, boolean isBar)
	{
		HashMap<String, SeriesProperties> nameProp;
		HduXYSeriesCollection collection;
		if (isBar)
		{
			nameProp = barNameProp;
			collection = barSeriesCollection;
		} else
		{
			nameProp = lineNameProp;
			collection = lineSeriesCollection;
		}
		super.upSeriesData(nameProp, collection, names, planeXY);

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
	private void addSeries(String name, PlaneXY xy, Color c, boolean isBar)
	{
		// 第一步将新的数据加入Map中
		HashMap<String, SeriesProperties> nameProp;
		HduXYSeriesCollection collection;
		if (isBar)
		{
			nameProp = barNameProp;
			collection = barSeriesCollection;
			// 将名字和id对应起来
			SeriesProperties properties = new SeriesProperties(
					collection.getSeriesCount(), c,
					SeriesProperties.NATURE_BAR, xybarrenderer);
			nameProp.put(name, properties);

		} else
		{
			nameProp = lineNameProp;
			collection = lineSeriesCollection;
			// 将名字和id对应起来
			SeriesProperties properties = new SeriesProperties(
					collection.getSeriesCount(), c,
					SeriesProperties.NATURE_LINE, xylineandshaperenderer);
			nameProp.put(name, properties);
		}

		// 第二步设置这个系列的属性
		setRenderer(nameProp.get(name));

		// 第三步调用父类方法将数据添加进去
		super.addSeries(collection, name, xy, c);

	}

	// 直接中两个系列中删除，这样导致默认两个系列不能重名
	@Override
	public SeriesProperties deleteSeries(String name)
	{
		// TODO Auto-generated method stub
		super.deleteSeries(lineNameProp, lineSeriesCollection, name);
		return super.deleteSeries(barNameProp, barSeriesCollection, name);
	}

	/**
	 * 设置某个系列的颜色
	 * 
	 * @param seriesname
	 * @param c
	 */
	public void setSeriesColor(String seriesname, Color c)
	{
		Object id = getSeriesID(barNameProp, seriesname);
		if (id != null)
		{
			xybarrenderer.setSeriesPaint((Integer) id, c);
		}
	}

	/**
	 * 设置系列名称显示
	 * 
	 * @param seriesname
	 *            系列的名称
	 */
	public void setOpenSeriesVisibleInLegend(String seriesname)
	{
		Object id = getSeriesID(barNameProp, seriesname);
		if (id != null)
		{
			xybarrenderer.setSeriesVisibleInLegend((Integer) id, true);
		}
	}

	/**
	 * 设置系列名称不显示
	 * 
	 * @param id
	 *            系列的id 默认第一个为0
	 */
	public void setCloseSeriesVisibleInLegend(int id)
	{
		xybarrenderer.setSeriesVisibleInLegend(id, false);
	}

	/**
	 * 设置系列名称显示
	 * 
	 * @param id
	 *            系列的id 默认第一个为0
	 */
	public void setOpenSeriesVisibleInLegend(int id)
	{
		xybarrenderer.setSeriesVisibleInLegend(id, true);
	}

	/**
	 * 设置系列名称不显示
	 * 
	 * @param seriesname
	 *            系列的名称
	 */
	public void setCloseSeriesVisibleInLegend(String seriesname)
	{
		Object id = getSeriesID(barNameProp, seriesname);
		if (id != null)
		{
			xybarrenderer.setSeriesVisibleInLegend((Integer) id, false);
		}
	}

	/**
	 * 设置系列的间距
	 * 
	 * @param seriesname
	 *            系列的名称
	 * @param margin
	 * 			      间距值
	 */
	public void setSeriesMargin(String seriesname,double margin)
	{
		Object id = getSeriesID(barNameProp, seriesname);
		if (id != null)
		{
			xybarrenderer.setMargin(margin);
		}
	}
	
	
	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());
		float x[] = { 0, 1, 2, 3, 4, 5 };
		float y[] = { 1, 2, 4, 2, 4, 1 };
		PlaneXY planeXY = new PlaneXY(x, y);
		BarChartPanel lineChartPanel = new BarChartPanel("OK");
		lineChartPanel.upAutSeriesData("1", planeXY, Color.green);
		for (int i = 0; i < y.length; i++)
		{
			y[i] = i;
		}
		planeXY = new PlaneXY(x, y);
		// lineChartPanel.upAutSeriesData("2", planeXY, Color.red, false);

		jFrame.add(lineChartPanel, BorderLayout.CENTER);
		jFrame.setVisible(true);

	}

	@Override
	public HduChartPanel getChartPanel()
	{
		// TODO Auto-generated method stub
		return chartPanel;
	}

	@Override
	public void clearTextAnnotation()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public HduXYSeriesCollection getCollection()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
