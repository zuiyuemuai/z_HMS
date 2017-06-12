package com.nari.slsd.hms.hdu.common.iCell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.HduChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.HduXYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleEdge;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.PropDialog;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Jfreechart的基本图元
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class BaseChartJpanel extends JPanel
{
	public HduChartPanel chartPanel;
	public JFreeChart chart;

	public String chartName;
	protected Color frameColor;// 网格的颜色
	protected XYPlot plot; // 当前坐标区域

	protected boolean Rangesuit = false;// x轴自适应标志
	protected boolean Domainsuit = false;// y轴自适应标志

	// 创建chart
	protected abstract JFreeChart createJFreeChart();

	public class SeriesProperties
	{
		public static final String NATURE_LINE = "line";
		public static final String NATURE_POINT = "point";
		public static final String NATURE_BAR = "bar";

		public int id;// 在集合中的下标
		public Color color;// 颜色

		public String nature;// 是什么 线还是点还是柱状图
		public XYItemRenderer renderer;// 绘图器
		public double wide;//线点的宽度

		public SeriesProperties()
		{
			this(0, Color.black);
		}

		public SeriesProperties(int id, Color c)
		{
			this(id, c, NATURE_LINE, null);
		}

		public SeriesProperties(int id, Color c, String nature,
				XYItemRenderer renderer)
		{
			this(id, c, nature, renderer, 1);
		}
		public SeriesProperties(int id, Color c, String nature,
				XYItemRenderer renderer, double wide)
		{
			this.id = id;
			this.color = c;
			this.nature = nature;
			this.renderer = renderer;
			this.wide = wide;
		}

	}

	private class SetSectionDialog extends PropDialog
	{

		public SetSectionDialog()
		{
			super(344, 146);
		
			ValueAxis axis = plot.getDomainAxis();
			
			table.setValueAt(axis.getUpperBound() + "", 0, 1);
			table.setValueAt(axis.getLowerBound() + "", 1, 1);
			
			axis = plot.getRangeAxis();
			
			table.setValueAt(axis.getUpperBound() + "", 2, 1);
			table.setValueAt(axis.getLowerBound() + "", 3, 1);
		
		}

		@Override
		protected void JcomBoxsInit()
		{
			// TODO Auto-generated method stub
		}

		@Override
		protected void JcomBoxsSelectHandle(JComboBox choise)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void CommitHandle(Vector<JComboBox> boxs)
		{
			// TODO Auto-generated method stub

		}

		@Override
		protected void IntemInit()
		{
			// TODO Auto-generated method stub
			item = new String[] { 
					
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MaxX"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MinX"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MaxY"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MinY")
					};
		}

		@Override
		public void CommitHandle(JTable table)
		{
			// TODO Auto-generated method stub
			setXaxis(Float.parseFloat((String) table
					.getValueAt(1, 1)), Float.parseFloat((String) table
					.getValueAt(0, 1))
					);
			setYaxis(Float.parseFloat((String) table
					.getValueAt(3, 1)), Float.parseFloat((String) table
							.getValueAt(2, 1))
					);

		}

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
	
	//横向和纵向跟随鼠标线
	public void setHorizontalAxisTrace(boolean flag)
	{
		chartPanel.setHorizontalAxisTrace(flag);
	}
	public void setVerticalAxisTrace(boolean flag)
	{
		chartPanel.setVerticalAxisTrace(flag);
	}
	
	public String getChartName()
	{
		return chartName;
	}
	public BaseChartJpanel()
	{	
		this("Default", Color.gray);
	}

	public BaseChartJpanel(String chartName, Color frameColor)
	{
		this.chartName = chartName;
		this.frameColor = frameColor;
		// chart的参数通过上面的全局变量传入
		chart = createJFreeChart();

		chartPanel = new HduChartPanel(chart);
		this.setLayout(new BorderLayout());
		this.add(chartPanel, BorderLayout.CENTER);
		
		JMenuItem recover = new JMenuItem(HduChartUtil.getResource("Properties..."));
		recover.setActionCommand("Properties");
		recover.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				if(e.getActionCommand().equals("Properties"))
				{
					new SetSectionDialog().setVisible(true);
				}
			}
		});
		chartPanel.getPopupMenu().add(recover);
		
		
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
	 * 坐标轴上的X和Y标签不显示
	 */
	public void setCloseLableXandY()
	{
		// 修改绘图区域中的内容，需要通过plot对象进行设置
		ValueAxis valueAxis = plot.getRangeAxis();
		valueAxis.setLabel(null);
		valueAxis = plot.getDomainAxis();
		valueAxis.setLabel(null);
	}
	/**
	 * 让x上的标签显示未name
	 * @param name
	 */
	public void setYLable(String name)
	{
		ValueAxis valueAxis = plot.getRangeAxis();
		valueAxis.setLabel(name);
		valueAxis.setLabelAngle(2*Math.PI);
	}
	public void setXLable(String name)
	{
		ValueAxis valueAxis = plot.getDomainAxis();
		valueAxis.setLabel(name);
	}

	/**
	 * 设置表格名称 设置为null就能取消显示title
	 * 
	 * @param name
	 */
	public void setTitle(String name)
	{
		chart.setTitle(name);
	}

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
	public void setYaxis(double lx, double hx)
	{
		plot.getRangeAxis().setRange(lx, hx);
	}

	/**
	 * 判断是否相同的X轴坐标范围
	 * 
	 * @param float1
	 * @param float2
	 */
	public boolean isSameXAxis(float float1, float float2)
	{
		return ((int) plot.getDomainAxis().getLowerBound() == (int) float1 && (int) plot
				.getDomainAxis().getUpperBound() == (int) float2);
	}

	/**
	 * 设置鼠标拖动时是x向操作
	 */
	public void setMouseDragOperation_X()
	{
		chartPanel.setDomainZoomable(true);
		chartPanel.setRangeZoomable(false);
		chartPanel.mouseDragOperation = chartPanel.LINE_SCALE_X;
	}

	/**
	 * 设置鼠标拖动时是y向操作
	 */
	public void setMouseDragOperation_Y()
	{
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(true);
		chartPanel.mouseDragOperation = chartPanel.LINE_SCALE_Y;
	}

	/**
	 * 设置鼠标拖动时是xy向操作
	 */
	public void setMouseDragOperation_Both()
	{
		chartPanel.setMouseZoomable(true);
		chartPanel.mouseDragOperation = chartPanel.LINE_SCALE;
	}

	/**
	 * 外部添加鼠标控制的监听器
	 */
	public void addMouseListener(MouseListener listener)
	{
		chartPanel.addMouseListener(listener);
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
	 * 设置某个系列的颜色
	 * 
	 * @param renderer
	 *            绘图器
	 * @param seriesname
	 *            系列的名称
	 * @param c
	 *            颜色
	 */
	public void setSeriesColor(int id, XYItemRenderer renderer, Color c)
	{
		renderer.setSeriesPaint((Integer) id, c);
	}

	/**
	 * 设置系列名称显示
	 * 
	 * @param seriesname
	 *            系列的名称
	 */
	public void setOpenSeriesVisibleInLegend(
			HashMap<String, SeriesProperties> map, XYItemRenderer renderer,
			String seriesname)
	{
		Object id = getSeriesID(map, seriesname);
		if (id != null)
		{
			renderer.setSeriesVisibleInLegend((Integer) id, true);
		}
	}

	/**
	 * 设置系列名称不显示
	 * 
	 * @param seriesname
	 *            系列的名称
	 */
	public void setCloseSeriesVisibleInLegend(
			HashMap<String, SeriesProperties> map, XYItemRenderer renderer,
			String seriesname)
	{
		Object id = getSeriesID(map, seriesname);
		if (id != null)
		{
			renderer.setSeriesVisibleInLegend((Integer) id, false);
		}
	}

	/**
	 * 设置系列名称不显示
	 * 
	 * @param id
	 *            系列的id 默认第一个为0
	 */
	public void setCloseSeriesVisibleInLegend(XYItemRenderer renderer, int id)
	{
		renderer.setSeriesVisibleInLegend(id, false);
	}

	/**
	 * 设置系列名称显示
	 * 
	 * @param id
	 *            系列的id 默认第一个为0
	 */
	public void setOpenSeriesVisibleInLegend(XYItemRenderer renderer, int id)
	{
		renderer.setSeriesVisibleInLegend(id, true);
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
	 * 新增一个系列
	 * 
	 * @param name
	 *            系列名称
	 * @param PlaneXY
	 *            一个平面信息数据
	 * @param c
	 *            这个系列的显示颜色
	 */
	protected void addSeries(HduXYSeriesCollection collection, String name,
			PlaneXY xy, Color c)
	{

		XYSeries xyseries = new XYSeries(name);
		float[] Xvalue = xy.getX();
		float[] Yvalue = xy.getY();
		int num = xy.getNum();

		for (int i = 0; i < num; i++)
		{
			xyseries.add(Xvalue[i], Yvalue[i]);
		}

		collection.addSeries(xyseries);

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
	 * 更新数据
	 * 
	 * @param planeXY
	 *            数据
	 * @param names
	 *            系列的名称id
	 */

	protected void upSeriesData(HashMap<String, SeriesProperties> nameProp,
			HduXYSeriesCollection collection, String names, PlaneXY planeXY)
	{
		int id = getSeriesID(nameProp, names);

		XYSeries xyseries = new XYSeries(names);
		float[] Xvalue = planeXY.getX();
		float[] Yvalue = planeXY.getY();
		int num = planeXY.getNum();

		for (int i = 0; i < num; i++)
		{
			xyseries.add(Xvalue[i], Yvalue[i]);
		}
		collection.setSeries(id, xyseries);

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

	protected void setRenderer(SeriesProperties properties)
	{
		setSeriesColor(properties.id, properties.renderer, properties.color);
	}

	/**
	 * 删除一个系列
	 * 
	 * @param name
	 */
	public SeriesProperties deleteSeries(HashMap<String, SeriesProperties> nameProp,
			HduXYSeriesCollection collection, String name)
	{
		if (!nameProp.containsKey(name))
		{
			return null;
		}
		SeriesProperties properties = nameProp.remove(name);

		// 由于要删除list中的一个数，那么list后面的id都需要减一
		Set<String> key = nameProp.keySet();
		for (String s : key)
		{
			SeriesProperties kProperties = nameProp.get(s);
			if (kProperties.id > properties.id)
			{
				nameProp.put(s, new SeriesProperties(--kProperties.id,
						kProperties.color, kProperties.nature,
						kProperties.renderer, kProperties.wide));
				// 每个系列的外观参数改变了，所以要修改
				setRenderer(kProperties);
			}
		}

		collection.removeSeries(properties.id);
		return properties;
	}

	// 将实际坐标转换化为屏幕坐标
	public static Point2D translateValueToScreen(HduChartPanel chartPanel,float xcoordinate, float ycoordinate)
	{

		float[] java2D = new float[2];
		Point ScreenPoint = new Point();
		ChartRenderingInfo chartRenderingInfo = chartPanel
				.getChartRenderingInfo();
		Rectangle2D rectangle2D = chartRenderingInfo.getPlotInfo()
				.getDataArea();
		XYPlot plot = chartPanel.getChart().getXYPlot();
		ValueAxis d = plot.getDomainAxis();// 获取横坐标上的数据值
		RectangleEdge rectangleEdge1 = plot.getDomainAxisEdge();
		ValueAxis r = plot.getRangeAxis();
		RectangleEdge rectangleEdge2 = plot.getRangeAxisEdge();
		// 将实际坐标值转化为java2D个格式
		java2D[0] = (float) d.valueToJava2D(xcoordinate, rectangle2D,
				rectangleEdge1);
		java2D[1] = (float) r.valueToJava2D(ycoordinate, rectangle2D,
				rectangleEdge2);
		Point2D point2D = new Point((int) java2D[0], (int) java2D[1]);
		// 再将java2D格式转化为屏幕坐标值
		ScreenPoint = chartPanel.translateJava2DToScreen(point2D);
		return ScreenPoint;

	}

	// 坐标转换函数，将屏幕上的坐标转换为实际自定义坐标值
	public static float[] translateScreenToValue(HduChartPanel chartPanel,Point point)
	{
		float[] valuePoint = new float[2];
		Point2D point2D = chartPanel.translateScreenToJava2D(point);
		ChartRenderingInfo chartRenderingInfo = chartPanel
				.getChartRenderingInfo();
		Rectangle2D rectangle2D = chartRenderingInfo.getPlotInfo()
				.getDataArea();
		XYPlot plot = chartPanel.getChart().getXYPlot();
		ValueAxis d = plot.getDomainAxis();// 获取横坐标上的数据值
		RectangleEdge rectangleEdge1 = plot.getDomainAxisEdge();
		ValueAxis r = plot.getRangeAxis();
		RectangleEdge rectangleEdge2 = plot.getRangeAxisEdge();
		// 将java2D的格式转换为实际坐标轴坐标
		double d1 = d
				.java2DToValue(point2D.getX(), rectangle2D, rectangleEdge1);
		double d2 = r
				.java2DToValue(point2D.getY(), rectangle2D, rectangleEdge2);
		valuePoint[0] = (float) d1;
		valuePoint[1] = (float) d2;
		return valuePoint;
	}
	
	public JFreeChart getChart(){
		return chart;
	}

}
