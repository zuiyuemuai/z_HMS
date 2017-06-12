package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.DrawArrow;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Created :2014-11-30 下午8:50:44 
 * Describe :原波形图
 * Class : OriginalChartPanel.java
 * Created by：YXQ
 * modified by：Lynn
 * 
 * 
 */
public class OriginalChartPanel extends LineChartPanel 
		
{

	/** 原波形图中浮点检测命令 */
	private final String CHECK_POINT_COMMAND = "CHECK_POINT";
	/** 原波形图中临近浮点检测命令 */
	private final String CHECKO_LINE_POINT_COMMAND = "CHECKO_LINE_POINT";
	/** 原波形图中标记最值命令 */
	private final String TIP_COMMAND = "TIP";
	private final String SET_RANGE_COMMAND = "SET_RANGE_COMMOND";

	/* 这两个标志主要用于区分标注最大值、最小值的区分 */
	private final int MAXTIP = 110;// 标出最大值
	private final int MINTIP = 111;// 标出最小值

	/** 浮点检测标志位 */
	private Boolean flagFudian = false;
	/** 临近浮点检测标志位 */
	private Boolean flagNearfudian = false;
	/** The resourceBundle for the localization. */
//	protected ResourceBundle localizationResources = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);

	JMenu customizeMenu;
	FloatMouseListener mouseListener;
	PlaneXY planeXY;
	
	public OriginalChartPanel(String chartName, PlaneXY planeXY)
	{
		super(chartName, Color.gray);
		this.planeXY = planeXY;
		CustomizePopupMenu(true, true, true , true);
		mouseListener = new FloatMouseListener(chartPanel, chart,this.planeXY, this);
		chartPanel.addMouseMotionListener(mouseListener);// 添加浮点检测的监听器
		chartPanel.addMouseListener(mouseListener);
		upAutSeriesData(chartName, planeXY, Color.blue);
		setTitle(null);
	}

	private void CustomizePopupMenu(boolean ifcheckpoint, boolean ifcheckline,
			boolean iftip,boolean ifSetRange)
	{
		// 定制弹出菜单栏
		// boolean properties, boolean copy, boolean save,
		// boolean print, boolean zoom, boolean ifmove, boolean ifback,
		// boolean ifrepain, boolean isadjustboolean properties, boolean
		// save,boolean print, boolean zoom, boolean
		// ifmove,boolean ifback, boolean ifrepain, boolean isadjust
		JPopupMenu popup = new JPopupMenu();

		customizeMenu = new JMenu(HduChartUtil.getResource("Image_Ori"));

		chartPanel.addMenuItem_Back(customizeMenu);
		chartPanel.addMenuItem_Zoom(customizeMenu);
		chartPanel.addMenuItem_Move(customizeMenu);

		// 新建一个"浮点检测"的在菜单项目
		final JCheckBoxMenuItem checkPoint = new JCheckBoxMenuItem(
				HduChartUtil.getResource("Check_Point"));
		if (ifcheckpoint)
		{
			checkPoint.setActionCommand(CHECK_POINT_COMMAND);
//			checkPoint.addActionListener(originalMneuLisitenr);
			customizeMenu.add(checkPoint);
		}
		
		// 新建一个"临近浮点检测"的在菜单项目
		final JCheckBoxMenuItem checkLinePoint = new JCheckBoxMenuItem(
				HduChartUtil.getResource("Check_Line_Point"));
		if (ifcheckline)
		{
			checkLinePoint.setActionCommand(CHECKO_LINE_POINT_COMMAND);
//			checkLinePoint.addActionListener(originalMneuLisitenr);
			customizeMenu.add(checkLinePoint);
			customizeMenu.addSeparator();
		}
		
		checkPoint.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(checkPoint.getState()){
					checkLinePoint.setSelected(false);
					doCheckPoint();
					setCloseSeriesVisibleInLegend(FloatMouseListener.FLOAT_RANGE);
					setCloseSeriesVisibleInLegend(FloatMouseListener.FLOAT_DOMAIN);
				}
				else{
					chartPanel.setMouseZoomable(true);
					CharacterPanel.positionX="";
					CharacterPanel.positionY="";
//					CharacterPanel.positionX.setVisible(false);
//					CharacterPanel.positionY.setVisible(false);
					mouseListener.stopFloatCheck();
					mouseListener.mouseDragOperation = FloatMouseListener.NOTHING;
				}
				
			}
		});
		
		checkLinePoint.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(checkLinePoint.getState()){
					checkPoint.setSelected(false);
					doCheckLinePoint();
					setCloseSeriesVisibleInLegend(FloatMouseListener.FLOAT_RANGE);
					setCloseSeriesVisibleInLegend(FloatMouseListener.FLOAT_DOMAIN);
				}else {
					chartPanel.setMouseZoomable(true);
					CharacterPanel.positionX="";
					CharacterPanel.positionY="";
//					CharacterPanel.positionX.setVisible(false);
//					CharacterPanel.positionY.setVisible(false);
					mouseListener.stopFloatCheck();
					mouseListener.mouseDragOperation = FloatMouseListener.NOTHING;
				}
			}
		});

		if (iftip)
		{
			// 新建一个"标记"的在菜单项目
			JMenuItem tip = new JMenuItem(
					HduChartUtil.getResource("Tip"));
			tip.setActionCommand(TIP_COMMAND);
			tip.addActionListener(originalMneuLisitenr);
			customizeMenu.add(tip);
			customizeMenu.addSeparator();
		}
		
		if(ifSetRange){
			JMenuItem setRange = new JMenuItem(HduChartUtil.getResource("setRange"));
			setRange.setActionCommand(SET_RANGE_COMMAND);
			setRange.addActionListener(originalMneuLisitenr);
			customizeMenu.add(setRange);
		}

		chartPanel.addMenuItem_Recover(customizeMenu);
		popup.add(customizeMenu);
		chartPanel.setPopupMenu(popup);
	}

	public JMenu getJMenu()
	{
		return customizeMenu;
	}

	/**
	 * Describe :获取浮点检测轨迹线的一个点，在该点的基础上画出横纵轨迹，该点最好存在与曲线上
	 * 所以获取的方法，便是在这段曲线数据中随机产生一个数 入口参数： 返回值：Point，返回一个屏幕坐标点
	 */
	private float[] getFirstTracePoint()
	{
		float[] point = new float[2];
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis d = plot.getDomainAxis();
		float lowRange = (float) d.getLowerBound();
		float upRange = (float) d.getUpperBound();
		point[0] =  (float) (Math.random() * ((upRange - lowRange) + 1) + lowRange);
		point[1] =  planeXY.getY()[(int) point[0]];

		return point;
	}

	/**
	 * Describe :浮点检测函数 入口参数： 返回值：
	 */
	private void doCheckPoint()
	{
			chartPanel.setMouseZoomable(false);
			mouseListener.mouseDragOperation = FloatMouseListener.FLOAT_POINT;
			float[] point = getFirstTracePoint();
//			CharacterPanel.positionX.setVisible(true);
//			CharacterPanel.positionY.setVisible(true);
			CharacterPanel.setCheckPoint(point[0], point[1]);
			mouseListener.xline = point[0];
			mouseListener.yline = point[1];
			mouseListener.startFloatCheck();
	}

	/**
	 * Describe :临近浮点检测函数 入口参数： 返回值：
	 */
	private void doCheckLinePoint()
	{
			mouseListener.mouseDragOperation = FloatMouseListener.NEAR_FLOAT_POINT;
			float[] point = getFirstTracePoint();
//			CharacterPanel.positionX.setVisible(true);
//			CharacterPanel.positionY.setVisible(true);
			CharacterPanel.setCheckPoint(point[0], point[1]);
			mouseListener.xline = point[0];
			mouseListener.yline = point[1];
			mouseListener.startFloatCheck();
	}

	/**
	 * Describe :箭头标注最值，在图表中用箭头标出最值 入口参数：int MaxOrMin用于区分需要标注的是最大值还是最小值 返回值：无
	 */
	private void drawMax_MinArrow(int MaxOrMin)
	{
		Point StartPoint = new Point();
		int ValueLocation = MAXTIP;
		String tipMean = "max";
		if (MaxOrMin == MAXTIP)
		{
			ValueLocation = Get_MaxValue(planeXY.getY());
			tipMean = "max";
		} else if (MaxOrMin == MINTIP)
		{
			ValueLocation = Get_MinValue(planeXY.getY());
			tipMean = "min";
		}

		StartPoint = (Point) this.translateValueToScreen(this.chartPanel,
				planeXY.getX()[ValueLocation], planeXY.getY()[ValueLocation]);
		int StartX = (int) StartPoint.getX();
		int StartY = (int) StartPoint.getY();
		int EndX = StartX + 100;
		int EndY; 
		if(MaxOrMin == MAXTIP)
		{
			EndY = StartY - 5;
		}
		else
		{
			EndY = StartY + 5;
		}
		Graphics TipGraphic = (Graphics2D) this.getGraphics();
		DrawArrow drawArrow = new DrawArrow(StartX, StartY, EndX, EndY, tipMean);
		drawArrow.paintComponent(TipGraphic);

	}

	public ActionListener originalMneuLisitenr = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			String command = e.getActionCommand();
			if (command.equals(CHECK_POINT_COMMAND))
			{
//				doCheckPoint();

			} else if (command.equals(CHECKO_LINE_POINT_COMMAND))
			{
//				doCheckLinePoint();

			} else if (command.equals(TIP_COMMAND))
			{
				drawMax_MinArrow(MAXTIP);
				drawMax_MinArrow(MINTIP);
			} else if(command.equals(SET_RANGE_COMMAND)){
				setRange();
			}
		}
	};
	
	
	//设置范围
	private void setRange(){
		DialogSetRange setRangeFm = new DialogSetRange() {
			@Override
			public void sureEvent(float lowRange, float upRange) {
				// TODO Auto-generated method stub
				if(lowRange - upRange > 0){
					JOptionPane.showMessageDialog(null, "Error!");
					return;
				}
				XYPlot xyPlot = chart.getXYPlot();
				NumberAxis domainAxis = (NumberAxis) xyPlot.getDomainAxis();
				domainAxis.setRange(lowRange, upRange);
				setVisible(false);
			}
		};
		setRangeFm.pack();
		setRangeFm.setVisible(true);
	}

	/**
	 * 
	 * @param yvalue 输入序列
	 * @return 返回最大值的位置
	 */
	public int Get_MaxValue(float yvalue[])
	{
		float max = yvalue[0];
		int j = 0;
		for (int i = 0; i < yvalue.length; i++)
		{
			if (yvalue[i] > max)
			{
				max = yvalue[i];
				j = i;
			}
		}

		return j;
	}

	/**
	 * 
	 * @param yvalue 输入序列
	 * @return 返回最小值的位置
	 */
	public int Get_MinValue(float yvalue[])
	{
		float min = yvalue[0];
		int j = 0;
		for (int i = 0; i < yvalue.length; i++)
		{
			if (yvalue[i] < min)
			{
				min = yvalue[i];
				j = i;
			}
		}

		return j;
	}



	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());
		float x[] = { 0, 1, 2, 3, 4, 5 };
		float y[] = { 1, 3, 1, 0, 2, 0 };
		PlaneXY planeXY = new PlaneXY(x, y);
		OriginalChartPanel lineChartPanel = new OriginalChartPanel("OK",
				planeXY);
		lineChartPanel.upAutSeriesData("1", planeXY, Color.green);
		
//		y[0] = 2;
//		y[2] = 2;
//		y[4] = 2;
//		planeXY = new PlaneXY(x, y);
//		lineChartPanel.upAutSeriesData("2", planeXY, Color.red, true);// 新增一个系列
//
//		y[0] = 3;
//		y[2] = 3;
//		y[4] = 3;
//		planeXY = new PlaneXY(x, y);
//		lineChartPanel.upAutSeriesData("3", planeXY, Color.blue);// 更新一个系列
//
//		lineChartPanel.deleteSeries("1");

		lineChartPanel.setCloseLableXandY();
		lineChartPanel.setTitle(null);// 取消Title
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