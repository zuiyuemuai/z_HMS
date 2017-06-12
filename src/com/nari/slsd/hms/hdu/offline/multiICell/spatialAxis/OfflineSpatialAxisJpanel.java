package com.nari.slsd.hms.hdu.offline.multiICell.spatialAxis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

import com.nari.slsd.hms.hdu.common.algorithm.CalResult;
import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.NetSwingBean;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.data.ThreePlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.AxesOrbitChart;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.OffLineICellInterface;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线轴心姿态界面
 * 上面六个数据波形，下面是6个通道的3D姿态图
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class OfflineSpatialAxisJpanel extends SpatialAxis_AxesOrbitJpanel
		implements OffLineICellInterface, ChangeListener
{

	private JTabbedPane waveJTabbedPane;
	private ThreePlaneXY dataIn = new ThreePlaneXY();
	private float[][] dataInArray = new float[6][];// 原始数据
	private LineChartPanel waveJPanel;
	private JPanel[] LinePanels = new JPanel[6];
	private PlaneXY[] lineChartdata = new PlaneXY[6];
	private JPanel mainJPanel = new JPanel(new BorderLayout());
	private JButton creatWordBtn = new JButton(HduChartUtil.getResource("Common_CreatWord"));
	
	CalResult result[];
	NetSwingBean lowbean;
	NetSwingBean waterbean;

	String[] linechartname;
	String[] unit;
	
	public JPanel getJPanel()
	{
		return mainJPanel;
	}

	/**
	 * @param name
	 * 			6个通道数据的名称
	 * @param dataInArray
	 *            6个通道数据
	 */
	public OfflineSpatialAxisJpanel(String[] name,float[][] dataInArray,String[] unit)
	{
		super((name[0]+name[1]), (name[2]+name[3]), (name[4]+name[5]));

		float index[] = new float[dataInArray[0].length];
		for (int i = 0; i < dataInArray[0].length; i++)
		{
			index[i] = (float) i;
		}
		for (int i = 0; i < 6; i++)
		{
			lineChartdata[i] = new PlaneXY(index, dataInArray[i]);
		}
		linechartname = name.clone();
		
		JSplitPane splitPane = new JSplitPane();// 创建一个上下分割容器类
		// 上下
		splitPane.setOneTouchExpandable(true);// 让分割线显示出箭头
		splitPane.setContinuousLayout(true);// 操作箭头，重绘图形
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);// 设置分割线方向
		splitPane.setTopComponent(getTopJPanel());

		splitPane.setBottomComponent(this);

		mainJPanel.add(splitPane, BorderLayout.CENTER);
		creatWordBtn.addActionListener(creatWordListener);
		JPanel buttonJPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(buttonJPanel, 0, 0, 10, 1);
		GridBagUtil.setLocation(buttonJPanel, creatWordBtn, 1, 0, 1, 1, true);
		
		mainJPanel.add(buttonJPanel, BorderLayout.SOUTH);

		update(dataInArray);

		this.dataInArray = dataInArray;
		this.unit = unit;

	}

	private JTabbedPane getTopJPanel()
	{
		
		waveJTabbedPane = new JTabbedPane(); // 创建选项卡面板对象
		waveJTabbedPane.addChangeListener(this);
		// 创建面板
		waveJPanel = new LineChartPanel(linechartname[0], Color.gray);
		waveJPanel.addAxisChangedListener(new LineChartAxisChangeListener());
		waveJPanel.setMouseDragOperation_X();
		waveJPanel.setTitle("");
		waveJPanel.setCloseSeriesVisibleInLegend(0);

		
		for (int i = 0; i < 6; i++)
		{
			LinePanels[i] = new JPanel(new GridBagLayout());
			LinePanels[i].setBackground(Color.white);
			waveJTabbedPane.addTab(linechartname[i], LinePanels[i]);
		}
		
		//LinePanels[0].add(waveJPanel, BorderLayout.CENTER);

		GridBagUtil.addBlankJLabel(LinePanels[0], 0, 0, 2, 1);
		GridBagUtil.setLocation(LinePanels[0], waveJPanel, 1, 0, 10, 1, true);
		GridBagUtil.addBlankJLabel(LinePanels[0], 2, 0, 2, 1);
		
		return waveJTabbedPane;

	}
	
	
	//创建word
	private void creatWord() {
		
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File(HduChartUtil
				.getResource("OfflineSpatia_Word")));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else {
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"postureModel.ftl") {

			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				Point imagePoint = posture3d.getLocationOnScreen();
//				SwingUtilities.convertPointToScreen(posture3d.getLocation(), posture3d);
//				SwingUtilities.convertPointToScreen(Point p,Component c) 
				dataMap.put("image", ImageChange.get3DImageEncode(
						(int)imagePoint.getX(), (int)imagePoint.getY(),
						posture3d.getWidth(), posture3d.getHeight()));
				
				input2wordTable(dataMap);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}
	
	//数据输入word表格
	private void input2wordTable(Map<String, Object> dataMap){
		dataMap.put("Axchannel", linechartname[0]);
		dataMap.put("Axpeak", result[0].getPeak());
		dataMap.put("Axamplify", result[0].getAmplitude1X());
		dataMap.put("Axphase", result[0].getphase1X());
		dataMap.put("Achaojiao", result[0].getOverAngle());
		dataMap.put("Achaoliang",result[0].getTotalSwing());
		dataMap.put("Awanjiao", "");
		dataMap.put("Awanliang", "");
		
		dataMap.put("Aychannel", linechartname[1]);
		dataMap.put("Aypeak", result[1].getPeak());
		dataMap.put("Ayamplify", result[1].getAmplitude1X());
		dataMap.put("Ayphase", result[1].getphase1X());
		
		dataMap.put("Bxchannel", linechartname[2]);
		dataMap.put("Bxpeak", result[2].getPeak());
		dataMap.put("Bxamplify", result[2].getAmplitude1X());
		dataMap.put("Bxphase", result[2].getphase1X());
		dataMap.put("Bchaojiao", result[2].getOverAngle());
		dataMap.put("Bchaoliang",result[2].getTotalSwing());
		dataMap.put("Bwanjiao", lowbean.getPhase());
		dataMap.put("Bwanliang", lowbean.getAmptitude());
		
		dataMap.put("Bychannel", linechartname[3]);
		dataMap.put("Bypeak", result[3].getPeak());
		dataMap.put("Byamplify", result[3].getAmplitude1X());
		dataMap.put("Byphase", result[3].getphase1X());
		
		dataMap.put("Cxchannel", linechartname[4]);
		dataMap.put("Cxpeak", result[4].getPeak());
		dataMap.put("Cxamplify", result[4].getAmplitude1X());
		dataMap.put("Cxphase", result[4].getphase1X());
		dataMap.put("Cchaojiao", result[4].getOverAngle());
		dataMap.put("Cchaoliang",result[4].getTotalSwing());
		dataMap.put("Cwanjiao", waterbean.getPhase());
		dataMap.put("Cwanliang", waterbean.getAmptitude());
		
		dataMap.put("Cychannel", linechartname[5]);
		dataMap.put("Cypeak", result[5].getPeak());
		dataMap.put("Cyamplify", result[5].getAmplitude1X());
		dataMap.put("Cyphase", result[5].getphase1X());
	}

	public ActionListener creatWordListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			creatWord();
		}
	};
	
	/**
	 * 获取数据到datain中
	 * 
	 * @param data
	 */
	private void getdata(float[][] data)
	{
		dataIn = new ThreePlaneXY();
		dataIn.setData(data, 0, data[0].length);
	}

	/**
	 * 输入数据
	 * 
	 * data
	 * 
	 * */
	public void update(float[][] data)
	{
		// TODO Auto-generated method stub
		getdata(data);

		int[] index = new int[2];
		index[0] = 0;
		index[1] = 1023;
		dataIn.setIndex(index);

		float modulemax = Float.MIN_VALUE;
		float max[] = { Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE };
		float min[] = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };

		int[] phaseX = { 0, 1023 };// 无键相信号,采用默认键相信号处理

		result = new CalResult[6];

		for (int i = 0; i < 6; i++)
		{
			result[i] = new Calculate(dataIn.data[i], dataIn.index);
			modulemax = Math.max(modulemax, result[i].getModuleMAX());
			max[i / 2] = Math.max(max[i / 2], result[i].getDataMax());
			min[i / 2] = Math.min(min[i / 2], result[i].getDataMin());
		}

		NetSwingBean lowbean = Calculate.ComputeNetSwing(result[2].getFloats(),
				result[0].getFloats(), phaseX);// 计算得到弯曲量
		NetSwingBean waterbean = Calculate.ComputeNetSwing(
				result[4].getFloats(), result[2].getFloats(), phaseX);
		this.lowbean = lowbean;
		this.waterbean = waterbean;
		// 显示
		display_NetSwing(textDataMap[1], lowbean, 1);
		display_NetSwing(textDataMap[2], waterbean, 2);
		display_PeakAndFFTANDTotal(textDataMap[0], result[0], result[1], 0);
		display_PeakAndFFTANDTotal(textDataMap[1], result[2], result[3], 1);
		display_PeakAndFFTANDTotal(textDataMap[2], result[4], result[5], 2);

		// 计算得到界面大小变化的参数
		normalization = modulemax;
		for (int i = 0; i < 3; i++)
		{
			Max[i] = max[i];
			Min[i] = min[i];
		}

		/**
		 * 更新截面
		 * */
		for (int i = 0; i < 3; i++)
		{
			sectionCharts[i].display(dataIn.data[i * 2],
					dataIn.data[i * 2 + 1], Max[i], Min[i]);
			/***
			 * 主要显示文字显示 在向Map中添加数据后进行调用
			 * */
			textPanels[i].updata(textDataMap[i]);
		}
		posture3d.updata(dataIn, modulemax);

	}

	/**
	 * 同步 这里效率感觉有点低，需要完善
	 * 
	 * @param id
	 *            其他的一个同步id
	 */
	private void syn()
	{
		Map<String, Float> range = waveJPanel.getXAxisRange();

		for (int i = 0; i < 3; i++)
		{
			new SynThread(i, range).start();
		}
		posture3d.updata(dataIn);

	}
	//开三个线程去刷新，增快速度
	class SynThread extends Thread
	{
		int id;
		Map<String, Float> range;
		public SynThread(int id, Map<String, Float> range)
		{
			this.id = id;
			this.range = range;
		}
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			sectionChartDisplay(sectionCharts[id], dataInArray[id * 2],
					dataInArray[id * 2 + 1],
					(int) (float) range.get(LineChartPanel.XAxisLower),
					(int) (float) range.get(LineChartPanel.XAxisUpper));
			posture3dDisplay(id,
				(int) (float) range.get(LineChartPanel.XAxisLower),
				(int) (float) range.get(LineChartPanel.XAxisUpper));
		}
		
	}

	/**
	 * 3D同步
	 * 
	 * @param chartid
	 * @param min
	 * @param max
	 */
	public void posture3dDisplay(int chartid, int min, int max)
	{
		int[] phaseX = { 0, 1023 };// 无键相信号,采用默认键相信号处理
		chartid *= 2;

		for (int j = 0; j < 2; j++)
		{
			dataIn.setData(dataInArray[chartid + j], chartid + j, min, max
					- min);
		}

		int size = dataIn.data[chartid].length;
		int[] index = new int[size];
		for (int i = 0; i < size; i++)
		{
			index[i] = new Integer(i);
		}

		for (int i = 0; i < 2; i++)
		{
			result[chartid + i] = new Calculate(dataIn.data[chartid + i], index);
		}

		/*
		 * 因为键向点一直有问题所以这里不计算 NetSwingBean lowbean =
		 * DataCalculate.ComputeNetSwing( result[2].getFloats(),
		 * result[0].getFloats(), phaseX);// 计算得到弯曲量 NetSwingBean waterbean =
		 * DataCalculate.ComputeNetSwing( result[4].getFloats(),
		 * result[2].getFloats(), phaseX); // 显示
		 * display_NetSwing(textDataMap[1], lowbean);
		 * display_NetSwing(textDataMap[2], waterbean);
		 */

		display_PeakAndFFTANDTotal(textDataMap[0], result[0], result[1], 0);
		display_PeakAndFFTANDTotal(textDataMap[1], result[2], result[3], 1);
		display_PeakAndFFTANDTotal(textDataMap[2], result[4], result[5], 2);

	}

	public void sectionChartDisplay(AxesOrbitChart chart, float[] xList,
			float[] yList, int min, int max)
	{
		float newxList[] = new float[max - min];
		float newyList[] = new float[max - min];

		System.arraycopy(xList, min, newxList, 0, max - min);
		System.arraycopy(yList, min, newyList, 0, max - min);

		chart.display(newxList, newyList, false);
	}

	class LineChartAxisChangeListener implements AxisChangeListener
	{
		public LineChartAxisChangeListener()
		{

		}

		@Override
		public void axisChanged(AxisChangeEvent arg0)
		{
			// TODO Auto-generated method stub
			syn();
		}

	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub

	}

	private int tabbedID = 0;

	@Override
	public void stateChanged(ChangeEvent e)
	{
		// TODO Auto-generated method stub
		tabbedID = waveJTabbedPane.getSelectedIndex();

		//waveJPanel.setTitle(linechartname[tabbedID]);
		waveJPanel.deleteAllSeries();

		waveJPanel.upAutSeriesData("", lineChartdata[tabbedID], Color.green);
		if(null != unit)	waveJPanel.setYLable(unit[tabbedID]);
		//LinePanels[tabbedID].add(waveJPanel, BorderLayout.CENTER);

		waveJPanel.upAutSeriesData("", lineChartdata[tabbedID], ExtColor.getLineColor());
		//LinePanels[tabbedID].add(waveJPanel, BorderLayout.CENTER);
		
		waveJPanel.setTitle("");
		GridBagUtil.addBlankJLabel(LinePanels[tabbedID], 0, 0, 2, 1);
		GridBagUtil.setLocation(LinePanels[tabbedID], waveJPanel, 1, 0, 10, 1, true);
		GridBagUtil.addBlankJLabel(LinePanels[tabbedID], 2, 0, 2, 1);
		


	}

}
