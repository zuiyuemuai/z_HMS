package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.Complex;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.BarChartPanel;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Created :2014-11-30 下午8:50:44 
 * Describe :频谱图
 * Class : FrierChartPanel.java
 * Created by：YXQ
 * modified by：Lynn
 * 
 * 
 */
public class FrierChartPanel extends BarChartPanel
{

	/** 频谱图中临近浮点检测命令 */
	private final String CHECKF_LINE_POINT_COMMAND = "CHECKF_LINE_POINT";

//	protected static ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);

	public int smprateRate;

	private Boolean flagNearfudian = false;
	JMenu customizeMenu;
	FloatMouseListener mouseListener;
	PlaneXY planeXY = new PlaneXY();
	Complex[] frierChange;
	
	static String frierImg = HduChartUtil.getResource("Image_Frier");
	
	public FrierChartPanel(float samValues[], int smprateCount, int smprateRate)
	{
		super(frierImg, Color.GRAY);
		chartPanel.setName(frierImg);
//		this.samValues = new double[samValues.length];
//		for (int i = 0; i < samValues.length; i++)
//		{
//			this.samValues[i] = samValues[i];
//		}
		this.smprateRate = smprateRate;
		
		frierChange = Calculate.rfft_xyaxis(samValues); 
		float[] Frier_value = new float[frierChange.length/2];
		for (int j = 0; j < frierChange.length / 2; j++) {
			Frier_value[j] = (float) ((Math.sqrt(frierChange[j].getReal()
					* frierChange[j].getReal() + frierChange[j].getImage() * frierChange[j].getImage()))
					/ frierChange.length * 2);
		}
		
		float[] F_value = new float[Frier_value.length];

		for (int j = 0; j < Frier_value.length; j++)
		{
			F_value[j] = (float) (j * ((double) smprateRate / 2 / (double) Frier_value.length));
		}

		planeXY.setX(F_value);
		planeXY.setY(Frier_value);
		
		upAutSeriesData(frierImg, planeXY, Color.blue);
		setCloseSeriesVisibleInLegend(frierImg);
		CustomizePopupMenu(true);
		mouseListener = new FloatMouseListener(chartPanel, chart, this.planeXY, this);
		chartPanel.addMouseMotionListener(mouseListener);// 添加浮点检测的监听器
		chartPanel.addMouseListener(mouseListener);
		
		this.setTitle(frierImg);// 取消Title
	}

	/**
	 * 将输入数据进行FFT变化，并且显示在图上
	 * @param samValues 输入的数据
	 * 
	 */
	public void creatFrierChart(float samValues[])
	{
		frierChange = Calculate.rfft_xyaxis(samValues); 
		float[] Frier_value = new float[frierChange.length/2];
		for (int j = 0; j < frierChange.length / 2; j++) {
			Frier_value[j] = (float) ((Math.sqrt(frierChange[j].getReal()
					* frierChange[j].getReal() + frierChange[j].getImage() * frierChange[j].getImage()))
					/ frierChange.length * 2);
		}
		float[] F_value = new float[Frier_value.length];

		for (int j = 0; j < Frier_value.length; j++)
		{
			F_value[j] = (float) (j * ((double) smprateRate / 2 / (double) Frier_value.length));
		}

		planeXY.setX(F_value);
		planeXY.setY(Frier_value);
		
		upAutSeriesData(frierImg, planeXY, Color.blue);
		setXLable("Hz");
	}
	
	/**
	 * 右键菜单创建
	 * @param ifcheckline 是否要浮点检测
	 *  
	 */
	
	private void CustomizePopupMenu(boolean ifcheckline)
	{
		// 定制弹出菜单栏
		// boolean properties, boolean copy, boolean save,
		// boolean print, boolean zoom, boolean ifmove, boolean ifback,
		// boolean ifrepain, boolean isadjustboolean properties, boolean
		// save,boolean print, boolean zoom, boolean
		// ifmove,boolean ifback, boolean ifrepain, boolean isadjust
		JPopupMenu popup = new JPopupMenu();

		customizeMenu = new JMenu(frierImg);

		chartPanel.addMenuItem_Back(customizeMenu);
		chartPanel.addMenuItem_Zoom(customizeMenu);
		chartPanel.addMenuItem_Move(customizeMenu);

		if (ifcheckline)
		{
			// 新建一个"临近浮点检测"的在菜单项目
			JCheckBoxMenuItem checkLinePoint = new JCheckBoxMenuItem(
					HduChartUtil.getResource("Check_Line_Point"));
			checkLinePoint.setActionCommand(CHECKF_LINE_POINT_COMMAND);
			checkLinePoint.addActionListener(frierMenuLisitener);
			customizeMenu.add(checkLinePoint);
			customizeMenu.addSeparator();
		}

		chartPanel.addMenuItem_Recover(customizeMenu);
		popup.add(customizeMenu);
		chartPanel.setPopupMenu(popup);
	}

	public JMenu getJMenu()
	{
		return customizeMenu;
	}

	public void upAutSeriesData(String names, PlaneXY planeXY, Color c)
	{
		// TODO Auto-generated method stub
		if (names == FloatMouseListener.FLOAT_DOMAIN || names == FloatMouseListener.FLOAT_RANGE)
		{
			this.upAutSeriesData(names, planeXY, c, false);
		}else {
			this.upAutSeriesData(names, planeXY, c, true);
		}
		
	}
	
	
	/**
	 * 获取浮点检测轨迹线的一个点，在该点的基础上画出横纵轨迹，该点最好存在与曲线上
	 * 		所以获取的方法，便是在这段曲线数据中随机产生一个数 
	 * @return Point，返回一个屏幕坐标点
	 *  
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

	//线上浮点检测
	private void doCheckLinePoint()
	{
		if (!flagNearfudian)
		{
			flagNearfudian = true;
			mouseListener.mouseDragOperation = FloatMouseListener.NEAR_FLOAT_POINT;
			float[] point = getFirstTracePoint();
			mouseListener.xline = point[0];
			mouseListener.yline = point[1];
			mouseListener.startFloatCheck();
		} else
		{
			flagNearfudian = false;
			chartPanel.setMouseZoomable(true);
			mouseListener.stopFloatCheck();
			mouseListener.mouseDragOperation = FloatMouseListener.NOTHING;
		}
	}

	public ActionListener frierMenuLisitener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			String command = e.getActionCommand();
			if (command.equals(CHECKF_LINE_POINT_COMMAND))
			{
				doCheckLinePoint();
//				setCloseSeriesVisibleInLegend(FloatMouseListener.FLOAT_RANGE);
//				setCloseSeriesVisibleInLegend(FloatMouseListener.FLOAT_DOMAIN);
			}
		}
	};
	
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
		FrierChartPanel lineChartPanel = new FrierChartPanel(x,6,10);
		
	//	lineChartPanel.upAutSeriesData("1", planeXY, Color.green);

		jFrame.add(lineChartPanel, BorderLayout.CENTER);
		jFrame.setVisible(true);

	}
	

}
