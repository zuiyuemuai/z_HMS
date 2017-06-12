package com.nari.slsd.hms.hdu.offline.multiICell.spatialAxis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.common.algorithm.CalResult;
import com.nari.slsd.hms.hdu.common.data.NetSwingBean;
import com.nari.slsd.hms.hdu.common.data.ThreePlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.AxesOrbitChart;
import com.nari.slsd.hms.hdu.common.iCell.SpatialAxis3D;
import com.nari.slsd.hms.hdu.common.iCell.TextDataPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;

/**
 * 南瑞水电站监护系统基本图元 包含一个三维姿态图和3个截面显示和3个文字显示
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class SpatialAxis_AxesOrbitJpanel extends JPanel
{

	// 数据集
	protected ThreePlaneXY dataIn = new ThreePlaneXY();// 这个就是输入的数据包
	protected LinkedHashMap<String, Float>[] textDataMap = new LinkedHashMap[3];// 用于在text中顯示

	protected SpatialAxis3D posture3d;// 3D显示
	protected AxesOrbitChart[] sectionCharts = new AxesOrbitChart[3];
	protected TextDataPanel[] textPanels = new TextDataPanel[3];
	

	// 三个面的名称和颜色
	protected String[] name = new String[3];// 三个面的名称，从上之下分别为0-2
	protected Color[] frameColor = new Color[3];// 显示时外框显示颜色
	protected Color drawColor = null;// 显示内部点的显示颜色
	protected Color lineColor = null;// 显示时两个面之间线的显示颜色
	protected Color testColor = Color.black;

	// 用于坐标轴调整的参数
	protected float Max[] = { 0, 0, 0 };// 坐标轴的最大值
	protected float Min[] = { 0, 0, 0 };// 坐标轴的最小值
	protected float normalization = 1;// 归一化参数

	/**
	 * 
	 * @param name
	 *            三个面的名称
	 * @param frameColor
	 *            三个面的颜色
	 * @param drawColor
	 *            绘制点的颜色
	 * @param lineColor
	 *            三个面之间连接的线颜色
	 */
	public SpatialAxis_AxesOrbitJpanel(String[] name, Color[] frameColor,
			Color drawColor, Color lineColor)
	{
		try
		{
			for (int i = 0; i < 3; i++)
			{
				this.name[i] = name[i];
				this.frameColor[i] = frameColor[i];
			}
			this.drawColor = drawColor;
			this.lineColor = lineColor;
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();

		}
		init();
	}

	public SpatialAxis_AxesOrbitJpanel(String name1, String name2, String name3)
	{
		this(new String[] { name1, name2, name3 }, new Color[] { Color.gray,
				Color.gray, Color.gray }, ExtColor.getLineColor(), ExtColor.getConnectColor());
	}

	public SpatialAxis_AxesOrbitJpanel()
	{
		this(new String[] { "上导", "法兰", "水导" }, new Color[] { Color.gray,
				Color.gray, Color.gray }, ExtColor.getLineColor(), ExtColor.getConnectColor());
	}

	/**
	 * 重新建立坐標
	 */
	public void reinitCoordinate()
	{
		init();
	}

	public void init()
	{
		// TODO Auto-generated method stub
		textdatainit();
		// 姿态图初始化
		posture3d = new SpatialAxis3D("姿态图", name, frameColor, drawColor,
				lineColor);

		this.setLayout(new BorderLayout());// 主panel

		this.add(BorderLayout.CENTER, posture3d);
		this.add(BorderLayout.EAST, getRightPanel());

	}

	private JPanel getRightPanel()
	{
		JPanel rightJPanel = new JPanel();

		rightJPanel.setLayout(new GridBagLayout());
		// this.setBorder(new LineBorder(new Color(192, 192, 192), 1, true))
		JPanel facepanel = new JPanel(new GridLayout(3, 1));// 显示截面
		JPanel textpanel = new JPanel(new GridLayout(3, 1));// 显示数据

		// 数据显示的面板
		for (int i = 0; i < 3; i++)
		{
			textPanels[i] = new TextDataPanel(name[i], textDataMap[i],
					testColor);
		}

		// 获取截面
		for (int i = 0; i < 3; i++)
		{
			sectionCharts[i] = new AxesOrbitChart("     " + name[i],
					frameColor[i], drawColor, 1);
			sectionCharts[i].setSizeAutAdujst();
			sectionCharts[i].setCloseLableXandY();

			facepanel.add(sectionCharts[i].getPanel());
			textpanel.add(textPanels[i]);
		}

		rightJPanel.setLayout(new GridLayout(1, 2));
		rightJPanel.add(facepanel);
		rightJPanel.add(textpanel);

		return rightJPanel;
	}

	// 文本显示数据 这里要注意第一次是数据的初始化
	private void textdatainit()
	{

		for (int i = 0; i < 3; i++)
		{
			textDataMap[i] = new LinkedHashMap<String, Float>();

			for (int j = 0; j < 8; j++)
			{
				textDataMap[i].put(nameStrings[j], new Float(1.0));
			}
		}

		textDataMap[1].put(nameStrings[8], new Float(1.0));
		textDataMap[1].put(nameStrings[9], new Float(1.0));

		textDataMap[2].put(nameStrings[8], new Float(1.0));
		textDataMap[2].put(nameStrings[9], new Float(1.0));
	}

	// 显示弯曲角和弯曲量
	// @param id 是第几个test 0 - 3
	protected void display_NetSwing(LinkedHashMap<String, Float> DataMap,
			NetSwingBean netSwing, int id)
	{
		DataMap.put(nameStrings[8], netSwing.getPhase());
		DataMap.put(nameStrings[9], netSwing.getAmptitude());
		textPanels[id].updata(DataMap);
	}

	String[] nameStrings = new String[] { "X向峰峰值: ", "X向1X幅值: ", "X向1X相位: ",
			"Y向峰峰值: ", "Y向1X幅值: ", "Y向1X相位: ", "  超重角 : ", "  超重量 : ",
			"  弯曲角 : ", "  弯曲量 : " };

	// 显示相位和峰峰值
	/**
	 * 
	 * @param DataMap
	 * @param resultx
	 * @param resulty
	 * @param id
	 *            是第几个test 0 - 3
	 */
	protected void display_PeakAndFFTANDTotal(
			LinkedHashMap<String, Float> DataMap, CalResult resultx,
			CalResult resulty, int id)
	{
		DataMap.put(nameStrings[0], resultx.getPeak());
		DataMap.put(nameStrings[3], resulty.getPeak());

		DataMap.put(nameStrings[1], resultx.getAmplitude1X());
		DataMap.put(nameStrings[4], resulty.getAmplitude1X());

		DataMap.put(nameStrings[2], resultx.getphase1X());
		DataMap.put(nameStrings[5], resulty.getphase1X());

		DataMap.put(nameStrings[6], resultx.getOverAngle());// x轴的
		DataMap.put(nameStrings[7], resultx.getTotalSwing());

		textPanels[id].updata(DataMap);
	}

	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());

		SpatialAxis_AxesOrbitJpanel orbitJpanel = new SpatialAxis_AxesOrbitJpanel();

		jFrame.add(orbitJpanel, BorderLayout.CENTER);
		jFrame.setVisible(true);

	}

}
