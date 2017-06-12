package com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.comtrade.ComtradeData;
import com.nari.slsd.hms.hdu.common.data.Complex;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.iCell.PolarChartPanel;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelect.MouseHandle;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 动平衡分析模块 做一些动平衡试验
 * 
 * @author YXQ
 * @version 1.0,14/12/25
 * @since JDK1.625
 */
public class BalanceAnalyse extends JPanel {
	// protected static ResourceBundle res = ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);
	/** 加载试重前数据命令 */
	private final String BEFORE_DATA_COMMAND = "BEFORE_DATA";

	/** 加载试重后数据命令 */
	private final String AFTER_DATA_COMMAND = "AFTER_DATA";

	/** 取消加载试重前数据周期选择命令 */
	private final String CANCEL_BEFORE_DATA_COMMAND = "CANCEL_BEFORE_DATA";

	/** 取消加载试重后数据周期选择命令 */
	private final String CANCEL_AFTER_DATA_COMMAND = "CANCEL_AFTER_DATA";

	/** 图片另存为 */
	private final String SAVE_IMAGE_COMMAND = "SAVE_IMAGE";

	/** 输出word文档 */
	private final String CREAT_WORD_COMMAND = "CREAT_WORD";

	private PolarChartPanel polarChartPanel;
	private Vector<WorkSpaceProp> workSpaceProps;
	private JPanel twinLineChartPanel;
	private DataTable dataTable;
	private JButton calculateBtn;
	private JButton creatWordBtn;
	private MouseHandle mouseHandle;
	private BalanceMouseHandle beforeMouseHandle;
	private BalanceMouseHandle afterMouseHandle;
	private JPanel showChartPanel;
	private PlaneXY BejianxiangData;
	private PlaneXY AfjianxiangData;

	private LineChartPanel beforeHeavyChart;
	private LineChartPanel afterHeavyChart;
	private Complex[] beValueFFT;
	private Complex[] afValueFFT;
	private float frequency1;
	private float frequency2;
	private HduCreatWord hduCreatWord;
	private JLabel zhouqiNum1;
	private JLabel zhouqiNum2;

	protected String beforeDate = HduChartUtil
			.getResource("OfflineBalance_BeforeDate");
	protected String afterData = HduChartUtil
			.getResource("OfflineBalance_AfterData");
	protected String beforeNum = HduChartUtil
			.getResource("OfflineBalance_BeforeNum");
	protected String afterNum = HduChartUtil
			.getResource("OfflineBalance_AfterNum");
	protected String jianXiangLab = HduChartUtil
			.getResource("OfflineAnalyse_JianXiang");

	public BalanceAnalyse() {

	}

	public BalanceAnalyse(Vector<WorkSpaceProp> workSpaceProp) {
		// setWorkSpace(workSpaceProp);
		this.workSpaceProps = workSpaceProp;
		init();
		guiDesign();
	}

	protected void setWorkSpace(Vector<WorkSpaceProp> workSpaceProp) {
		this.workSpaceProps = workSpaceProp;
	}

	public JPanel getPanelSelf() {
		return this;
	}

	private void init() {
		// setSize(300,500);
		setLayout(new BorderLayout());
		beforeHeavyChart = new LineChartPanel();
		afterHeavyChart = new LineChartPanel();
		polarChartPanel = new PolarChartPanel();
		dataTable = new DataTable();
		calculateBtn = new JButton(
				HduChartUtil.getResource("OfflineBalance_Calculate"));
		creatWordBtn = new JButton(HduChartUtil.getResource("Common_CreatWord"));
		creatWordBtn.setActionCommand(CREAT_WORD_COMMAND);
		showChartPanel = new JPanel(new GridLayout(1, 2));
		twinLineChartPanel = new JPanel(new GridLayout(2, 1));
		zhouqiNum1 = new JLabel(beforeNum);
		zhouqiNum2 = new JLabel(afterNum);
	}

	private void guiDesign() {
		twinLineChartPanel.add(beforeHeavyChart);
		twinLineChartPanel.add(afterHeavyChart);
		beforeHeavyChart.chartPanel.setMouseZoomable(false);
		afterHeavyChart.chartPanel.setMouseZoomable(false);

		JPanel polarAndTablePanel = new JPanel(new BorderLayout());
		polarAndTablePanel.add(polarChartPanel);
		polarAndTablePanel.add(dataTable, BorderLayout.SOUTH);

		showChartPanel.add(twinLineChartPanel);
		showChartPanel.add(polarAndTablePanel);

		calculateBtn.addActionListener(calculateListener);
		creatWordBtn.addActionListener(popupMenuListener);
		// dataTable.add(calculateBtn);
		add(showChartPanel);
		// add(dataTable, BorderLayout.SOUTH);

		JPanel zhouqiNumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
				80, 10));
		zhouqiNumPanel.setBackground(Color.white);
		zhouqiNumPanel.add(zhouqiNum1);
		zhouqiNumPanel.add(zhouqiNum2);
		zhouqiNumPanel.add(creatWordBtn);
		zhouqiNumPanel.add(calculateBtn);
		add(zhouqiNumPanel, BorderLayout.NORTH);
		JPopupMenu jPopupMenu = createPopupMenu();
		setComponentPopupMenu(jPopupMenu);

	}

	/**
	 * 创建鼠标监听器，数据选择
	 * 
	 * @param lineChartPanel
	 *            输入需要操作的曲线图
	 * @param chartNum
	 *            两个曲线图中的哪一个
	 * @return 返回一个鼠标操作类
	 * 
	 */
	private BalanceMouseHandle selectData(final LineChartPanel lineChartPanel,
			final int chartNum) {
		BalanceMouseHandle mouseHandle;
		mouseHandle = new BalanceMouseHandle() {

			@Override
			protected LineChartPanel getLineChartPanel() {
				// TODO Auto-generated method stub
				return lineChartPanel;
			}

			@Override
			protected void operationSelectRange(PlaneXY planeXY) {
				// TODO Auto-generated method stub
				// 获得选择的数据，待处理
				float speedFre = 0;

				inputDataToTable(chartNum - 1, 2,
						Calculate.findMaxMin(planeXY.getY())[1],
						dataTable.jTable);
				if (chartNum == 1) {
					zhouqiNum1.setText(beforeNum
							+ String.valueOf(planeXY.getX().length));
					speedFre = getSpeed(planeXY, BejianxiangData.getX(),
							frequency1);
					findSignal(speedFre, beValueFFT, chartNum,
							dataTable.jTable, polarChartPanel);
				} else if (chartNum == 2) {
					zhouqiNum2.setText(afterNum
							+ String.valueOf(planeXY.getX().length));
					speedFre = getSpeed(planeXY, AfjianxiangData.getX(),
							frequency2);
					findSignal(speedFre, afValueFFT, chartNum,
							dataTable.jTable, polarChartPanel);
				}

				drawCircle();
			}

		};
		float[] beforePoint = getFirstTracePoint(lineChartPanel);
		mouseHandle.xline = beforePoint[0];
		mouseHandle.firstXline = beforePoint[0];
		// mouseHandle.yline = beforePoint[1];
		mouseHandle.setXline("first", beforePoint[0], Color.red);
		mouseHandle.setYline(beforePoint[0], beforePoint[1], Color.red);
		lineChartPanel.addMouseListener(mouseHandle);
		lineChartPanel.chartPanel.addMouseListener(mouseHandle);
		lineChartPanel.chartPanel.setFocusable(true);
		return mouseHandle;
		// lineChartPanel.addKeyListener(mouseHandle);
		// } catch (Exception e) {
		// TODO: handle exception
		// }

	}

	/**
	 * 周期选择的第一根曲线，随机出现在前半段曲线上
	 * 
	 * @param 输入需要操作的曲线图
	 * @return 返回第一个点的坐标
	 * 
	 */
	protected float[] getFirstTracePoint(LineChartPanel linechart) {
		float[] point = new float[2];
		XYPlot plot = (XYPlot) linechart.chart.getPlot();
		ValueAxis d = plot.getDomainAxis();
		float lowRange = (float) d.getLowerBound();
		float upRange = (float) d.getUpperBound();
		point[0] = (float) (Math.random() * ((upRange - lowRange) / 2 + 1) + lowRange);
		point[1] = linechart.planeXY.getY()[(int) point[0]];
		return point;
	}

	/**
	 * 
	 * @param getRangeValue
	 *            选择的数据段
	 * @param jianxiang
	 *            键相点数据
	 * @param fre
	 *            采样频率
	 * @return 得到转频
	 */
	protected float getSpeed(PlaneXY getRangeValue, float jianxiang[], float fre) {

		if (jianxiang == null) {
			JOptionPane.showMessageDialog(null,
					HduChartUtil.getResource("OfflineAnalyse_JianxiangError"));
			return -1;
		}
		int valueLenth = getRangeValue.getX().length;
		int jianLenth = jianxiang.length;
		int firstJian = -1;
		int lastJian = -1;
		int jianNum = 0;
		// 得到选中的第一个键相点
		for (int i = 0; i < jianLenth; i++) {
			if (getRangeValue.getX()[0] < jianxiang[i]
					&& jianxiang[i] < getRangeValue.getX()[valueLenth - 1]) {
				firstJian = i;
				break;
			}
		}

		// 得到选中的最后一个键相点
		if (firstJian > 0) {
			int j;
			for (j = firstJian; j < jianLenth - 1; j++) {
				if (jianxiang[j] < getRangeValue.getX()[valueLenth - 1]
						&& jianxiang[j + 1] > getRangeValue.getX()[valueLenth - 1]) {
					lastJian = j;
					break;
				}
			}

			if (j + 1 == jianLenth) {
				if (jianxiang[j] < getRangeValue.getX()[valueLenth - 1]) {
					lastJian = j;
				}

			}
		}

		// 计算得到键相点的个数
		if (firstJian == lastJian || firstJian == -1 || lastJian == -1) {
			JOptionPane.showMessageDialog(null,
					"The selected range is wrong,please rechoose!");
			return (Float) null;
		} else {
			jianNum = lastJian - firstJian + 1;
		}

		// 计算得到转频
		float speedFre = (float) (jianNum / (double) (valueLenth / fre));

		return speedFre;
	}

	/**
	 * 
	 * @param chart
	 *            操作的曲线图
	 * @param channelData
	 *            通道数据
	 * @param workSpace
	 *            工作空间
	 * @param section
	 *            第几段数据
	 * @return 得到键相数据
	 */
	public PlaneXY showJianxiang(LineChartPanel chart, float[] channelData,
			WorkSpaceProp workSpace, String section) {

		Vector<Integer> keyData = workSpace.getKeyIndexData(section);
		if (keyData.size() == 0) {
			JOptionPane.showMessageDialog(null,
					HduChartUtil.getResource("OfflineAnalyse_JianxiangError"));
			return null;
		}
		PlaneXY planeXY = new PlaneXY();
		float[] jianxiangX = new float[keyData.size()];
		float[] jianxiangY = new float[keyData.size()];
		for (int j = 0; j < keyData.size(); j++) {
			jianxiangX[j] = keyData.get(j);
			if (jianxiangX[j] < channelData.length) {
				jianxiangY[j] = channelData[keyData.get(j)];
			}
		}
		planeXY.setX(jianxiangX);
		planeXY.setY(jianxiangY);
		chart.upAutSeriesData(jianXiangLab, planeXY, Color.red, true);
		return planeXY;
	}

	// 显示键相点，并且得到键相点集合
	public PlaneXY showJianxiang(LineChartPanel chart, float[] channelData,
			WorkSpaceProp workSpace, int jxT) {
		float jianxiangData[] = null;
		int channelCount = workSpace.allWaveCfgInfos.get(0).getAnalogCount();
		// channelCount = cmtrCfgInfo.channelCount;
		for (int i = 0; i < channelCount; i++) {
			if (workSpace.allWaveCfgInfos.get(0).getAnalogs().get(i)
					.getElement().equals("6")) {
				jianxiangData = ComtradeData.getOneChannelDataFromFiles(
						workSpace.allWaveSoucePaths.get(0),
						workSpace.allWaveCfgInfos.get(0), i);
				break;
			}
		}
		int lenth = jianxiangData.length;
		int group = (int) Math.ceil(lenth / (double) jxT);
		int num = 0;
		int temp = 0;
		int mid = 0;
		int location;
		for (int i = 0; i < group; i++) {
			if (jianxiangData[jxT * i] < 0 || num > lenth) {
				JOptionPane.showMessageDialog(null, HduChartUtil
						.getResource("OfflineAnalyse_JianxiangError"));
				return null;
			}
			num += jianxiangData[jxT * i];

		}
		float[] jianxiangX = new float[num];
		float[] jianxiangY = new float[num];
		for (int j = 0; j < group; j++) {
			mid += temp;
			temp = (int) jianxiangData[jxT * j];
			for (int k = mid, m = 0; m < temp; m++, k++) {
				location = (int) jianxiangData[jxT * j + m + 1];
				if (location < 0) {
					JOptionPane.showMessageDialog(null, HduChartUtil
							.getResource("OfflineAnalyse_JianxiangError"));
					jianxiangX = null;
					jianxiangY = null;
					return null;
				}
				if ((jxT * j + location) > lenth) {
					JOptionPane.showMessageDialog(null, HduChartUtil
							.getResource("OfflineAnalyse_JianxiangOut"));
					break;
				} else {
					jianxiangY[k] = channelData[jxT * j + location];
					jianxiangX[k] = jxT * j + location;
				}
			}
		}
		PlaneXY planeXY = new PlaneXY();
		planeXY.setX(jianxiangX);
		planeXY.setY(jianxiangY);
		chart.upAutSeriesData(jianXiangLab, planeXY, Color.red, true);
		return planeXY;
	}

	/**
	 * 
	 * @param input
	 *            输入数据
	 * @param sampFre
	 *            采样频率
	 * @return 得到整周期FFT变换后的数据
	 */
	protected Complex[] Zhengfft(float input[], float sampFre) {
		if (input == null) {
			return null;
		}
		float[] outPut;
		outPut = Calculate.CNiWaveFraqView(input, (int) sampFre);
		Complex[] frierChange = Calculate.rfft_xyaxis(outPut);
		return frierChange;

	}

	/**
	 * 找到这个信号，将相位和幅值填入表格，并画出极坐标
	 * 
	 * @param frequency
	 *            转频
	 * @param fftResualt
	 *            FFT以后的结果
	 * @param chartNum
	 *            第几个曲线图
	 * @param table
	 *            数据表
	 * @param chart
	 *            曲线图
	 * 
	 */
	protected void findSignal(float frequency, Complex fftResualt[],
			int chartNum, JTable table, PolarChartPanel chart) {
		if (frequency == -1) {
			JOptionPane.showMessageDialog(null, "error!");
			return;
		}
		float[] Frier_value = new float[fftResualt.length / 2];
		for (int j = 0; j < fftResualt.length / 2; j++) {
			Frier_value[j] = (float) ((Math.sqrt(fftResualt[j].getReal()
					* fftResualt[j].getReal() + fftResualt[j].getImage()
					* fftResualt[j].getImage()))
					/ fftResualt.length * 2);
		}

		float[] F_value = new float[Frier_value.length];

		for (int j = 0; j < Frier_value.length; j++) {
			F_value[j] = (float) (j * ((double) 1024 / 2 / (double) Frier_value.length));
		}
		int location = Calculate.getNearPosition(F_value, frequency);
		float ampli = Frier_value[location];
		float phase = Calculate.getPhase(fftResualt[location]);
		inputDataToTable(chartNum - 1, 1, frequency * 60, table);
		inputDataToTable(chartNum - 1, 3, ampli, table);
		inputDataToTable(chartNum - 1, 4, phase, table);
		//
		PlaneXY polar = new PlaneXY();
		float[] angle = { 0, phase };
		float[] amplify = { 0, ampli };
		polar.setX(angle);
		polar.setY(amplify);
		drawPolarChart(chartNum, polar, chart);
	}

	// 导入加重前数据
	private void doImportBeforeData() {
		SelectDataDialog beforeHeavyDialog = new SelectDataDialog(
				workSpaceProps) {

			@Override
			public void CommitHandle(JTable table) {
				// TODO Auto-generated method stub
			}

			@Override
			public void CommitHandle(Vector<JComboBox> boxs) {
				// TODO Auto-generated method stub
				/* 获得工作空间 */
				WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(
						workSpaceProps, (String) boxs.get(0).getSelectedItem(),
						(String) boxs.get(1).getSelectedItem());
				String section = (String) boxs.get(2).getSelectedItem();
				frequency1 = workSpace.allWaveCfgInfos.get(workSpace
						.getIndexFromSections(section)).smprateRate;
				String beforeChannelName = (String) boxs.get(3)
						.getSelectedItem();
				PlaneXY planeXY = new PlaneXY();
				float[] xdata;
				float[] ydata;
				// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
				String unit = workSpace.allWaveCfgInfos
						.get(workSpace.getIndexFromSections(section))
						.getAnalogs()
						.get(workSpace.getChannelIndex(section,
								beforeChannelName)).getUnit();
				ydata = workSpace.getWaveData(section, beforeChannelName);
				xdata = new float[ydata.length];
				for (int i = 0; i < ydata.length; i++) {
					xdata[i] = i;
				}
				planeXY.setX(xdata);
				planeXY.setY(ydata);
				beforeHeavyChart.deleteAllSeries();
				beforeHeavyChart.upAutSeriesData(beforeChannelName, planeXY,
						Color.blue);
				beforeHeavyChart.setTitle((String) boxs.get(1)
						.getSelectedItem()
						+ ","
						+ section
						+ ":"
						+ beforeChannelName);
				beforeHeavyChart.setPlanexy(planeXY);
				beforeHeavyChart.setYLable(unit);
				BejianxiangData = showJianxiang(beforeHeavyChart, ydata,
						workSpace, section);
				// BejianxiangData =
				// showJianxiang(beforeHeavyChart,ydata,workSpace,(int)
				// frequency1);
				beValueFFT = Zhengfft(ydata, frequency1);
				beforeMouseHandle = selectData(beforeHeavyChart, 1);
			}
		};

		beforeHeavyDialog.setVisible(true);

	}

	// 导入加重后数据
	private void doImportAfterData() {
		SelectDataDialog afterHeavyDialog = new SelectDataDialog(workSpaceProps) {

			@Override
			public void CommitHandle(Vector<JComboBox> boxs) {
				// TODO Auto-generated method stub
				WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(
						workSpaceProps, (String) boxs.get(0).getSelectedItem(),
						(String) boxs.get(1).getSelectedItem());
				String section = (String) boxs.get(2).getSelectedItem();
				frequency2 = workSpace.allWaveCfgInfos.get(workSpace
						.getIndexFromSections(section)).smprateRate;
				String afterChannelName = (String) boxs.get(3)
						.getSelectedItem();
				PlaneXY planeXY = new PlaneXY();
				float[] xdata;
				float[] ydata;
				String unit = workSpace.allWaveCfgInfos
						.get(workSpace.getIndexFromSections(section))
						.getAnalogs()
						.get(workSpace.getChannelIndex(section,
								afterChannelName)).getUnit();
				// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
				ydata = workSpace.getWaveData(section, afterChannelName);
				xdata = new float[ydata.length];

				for (int i = 0; i < ydata.length; i++) {
					xdata[i] = i;
				}
				planeXY.setX(xdata);
				planeXY.setY(ydata);
				afterHeavyChart.deleteAllSeries();
				afterHeavyChart.upAutSeriesData(afterChannelName, planeXY,
						Color.blue);
				afterHeavyChart.setTitle((String) boxs.get(1).getSelectedItem()
						+ "," + section + ":" + afterChannelName);
				afterHeavyChart.setPlanexy(planeXY);
				afterHeavyChart.setYLable(unit);
				AfjianxiangData = showJianxiang(afterHeavyChart, ydata,
						workSpace, section);
				// AfjianxiangData =
				// showJianxiang(afterHeavyChart,ydata,workSpace,(int)
				// frequency2);
				afValueFFT = Zhengfft(ydata, frequency2);
				afterMouseHandle = selectData(afterHeavyChart, 2);
			}

			@Override
			public void CommitHandle(JTable table) {
				// TODO Auto-generated method stub
			}
		};
		afterHeavyDialog.setVisible(true);

	}

	// 往表格中输入数据
	protected void inputDataToTable(int row, int colum, Object value,
			JTable table) {
		table.setValueAt(value, row, colum);
		table.updateUI();
	}

	// 画出极坐标
	protected void drawPolarChart(int chartNum, PlaneXY planeXY,
			PolarChartPanel chart) {
		Color c = null;
		if (chartNum == 1) {
			c = Color.PINK;
			chart.upAutSeriesData(beforeDate, planeXY, c);
		} else if (chartNum == 2) {
			c = Color.GREEN;
			chart.upAutSeriesData(afterData, planeXY, c);
		}

	}

	// 画圈
	protected void drawCircle() {
		Object radio1 = dataTable.jTable.getValueAt(0, 3);
		Object radio2 = dataTable.jTable.getValueAt(1, 3);
		String circleName = "";
		float r1 = 0;
		float r2 = 0;
		if (radio1 == "" & radio2 == "") {
			return;
		}
		if (radio1 != "") {
			r1 = Float.parseFloat(radio1.toString());
		}
		if (radio2 != "") {
			r2 = Float.parseFloat(radio2.toString());
		}
		if (r1 - r2 > 0) {
			circleName = "r1";
			polarChartPanel.deleteSeries("r2");
			drawCircle(circleName, r1);
		} else {
			circleName = "r2";
			polarChartPanel.deleteSeries("r1");
			drawCircle(circleName, r2);
		}

	}

	// 画圈
	protected void drawCircle(String name, float radio) {
		PlaneXY planeXY = new PlaneXY();
		float[] angle = new float[(int) (360 / 0.5)];
		float[] amp = new float[(int) (360 / 0.5)];
		for (int i = 0; i < 360 / 0.5; i++) {
			angle[i] = (float) (0.5 * i);
			amp[i] = radio;
		}
		planeXY.setX(angle);
		planeXY.setY(amp);
		polarChartPanel.upAutSeriesData(name, planeXY, Color.RED);
	}

	// 取消周期选择操作
	private void deleteChoice(int lineNum) {
		if (lineNum == 1) {
			beforeHeavyChart.chartPanel.restoreAutoBounds();
			beforeHeavyChart.deleteSeries("second");
			beforeHeavyChart.deleteSeries("up");
			beforeHeavyChart.deleteSeries("down");
			polarChartPanel.deleteSeries(beforeDate);
			polarChartPanel.deleteSeries("r1");
			drawCircle();
			dataTable.clearRowData(0, dataTable.jTable);
			beforeMouseHandle.xline = beforeMouseHandle.firstXline;
			beforeMouseHandle.ifdoubleClick = false;
		} else if (lineNum == 2) {
			afterHeavyChart.chartPanel.restoreAutoBounds();
			// beforeHeavyChart.deleteSeries("first");
			afterHeavyChart.deleteSeries("second");
			afterHeavyChart.deleteSeries("up");
			afterHeavyChart.deleteSeries("down");
			polarChartPanel.deleteSeries(afterData);
			polarChartPanel.deleteSeries("r2");
			drawCircle();
			dataTable.clearRowData(1, dataTable.jTable);
			afterMouseHandle.xline = afterMouseHandle.firstXline;
			afterMouseHandle.ifdoubleClick = false;
		}

	}

	// 计算配重
	private void calculateHeavy() {
		Complex v0 = null;
		Complex v1 = null;
		Complex q1 = null;
		// Object test = dataTable.jTable.getValueAt(0, 3);
		if (dataTable.jTable.getValueAt(0, 3) != ""
				&& dataTable.jTable.getValueAt(0, 4) != "") {
			v0 = Calculate.getComplex(Float.parseFloat(dataTable.jTable
					.getValueAt(0, 3).toString()), Float
					.parseFloat(dataTable.jTable.getValueAt(0, 4).toString()));
		}
		if (dataTable.jTable.getValueAt(1, 3) != ""
				&& dataTable.jTable.getValueAt(1, 4) != "") {
			v1 = Calculate.getComplex(Float.parseFloat(dataTable.jTable
					.getValueAt(1, 3).toString()), Float
					.parseFloat(dataTable.jTable.getValueAt(1, 4).toString()));
		}
		if (dataTable.jTable.getValueAt(1, 5) != ""
				&& dataTable.jTable.getValueAt(1, 6) != "") {
			q1 = Calculate.getComplex(Float.parseFloat(dataTable.jTable
					.getValueAt(1, 5).toString()), Float
					.parseFloat(dataTable.jTable.getValueAt(1, 6).toString()));
		}
		if (v0 != null && v1 != null && q1 != null) {
			Complex m1 = Calculate.dynamicBalance(v0, v1, q1);
			float m1Ampli = (float) Math.sqrt(Calculate.getCompexAmplify(m1));
			float phase = Calculate.getPhase(m1);
			inputDataToTable(2, 5, m1Ampli, dataTable.jTable);
			inputDataToTable(2, 6, phase, dataTable.jTable);

			float[] testAmp = {
					0,
					Float.parseFloat(dataTable.jTable.getValueAt(1, 5)
							.toString()) };
			float[] testPhase = {
					0,
					Float.parseFloat(dataTable.jTable.getValueAt(1, 6)
							.toString()) };
			PlaneXY testPlanexy = new PlaneXY(testPhase, testAmp);

			float[] theoryAmp = { 0, m1Ampli };
			float[] theoryPhase = { 0, phase };
			PlaneXY theoryPlanexy = new PlaneXY(theoryPhase, theoryAmp);

			polarChartPanel.upAutSeriesData(
					HduChartUtil.getResource("OfflineBalance_TestHeavy"),
					testPlanexy, Color.black);
			polarChartPanel.upAutSeriesData(
					HduChartUtil.getResource("OfflineBalance_TheoryHeavy"),
					theoryPlanexy, Color.cyan);
			XYLineAndShapeRenderer render = new XYLineAndShapeRenderer();
			render.setSeriesShapesFilled(3, true);
			render.setSeriesShapesFilled(4, true);
			polarChartPanel.chart.getXYPlot().setRenderer(render);

		} else {
			JOptionPane.showMessageDialog(null,
					HduChartUtil.getResource("OfflineBalance_Calculate_Erro3"));
			return;
		}
	}

	// 保存图片
	protected void saveImage(JPanel panel) {
		String saveName = HduChartUtil.getResource("OfflineBalance_Imag");
		String savePath = saveName+".BMP";
		BufferedImage bi = new BufferedImage(panel.getWidth(),
				panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		panel.paint(g2d);

		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File(savePath));
		jFileChooser.setMultiSelectionEnabled(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"BMP Images", "bmp");
		jFileChooser.setFileFilter(filter);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			savePath = null;
			return;
		} else
			savePath = jFileChooser.getSelectedFile().getPath();

		try {
			ImageIO.write(bi, "BMP", new File(savePath));
			JOptionPane.showMessageDialog(null,
					HduChartUtil.getResource("Common_SaveSuccess"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 创建word报表
	private void creatWord() {
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		String name = HduChartUtil.getResource("OfflineBalance_Word");
		jFileChooser.setSelectedFile(new File(name));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else {
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"dynamicBalanceModel.ftl") {

			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();

				dataMap.put("image", ImageChange.getImageEncode(showChartPanel));
				inputData2Word(dataMap);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));

			}
		};
	}

	// 将数据输入word报表的表格中
	private void inputData2Word(Map<String, Object> dataMap) {
		DecimalFormat df = new DecimalFormat("######0.00");// 保留两位有效数字
		dataMap.put("speedA", dataTable.jTable.getValueAt(0, 1));
		dataMap.put("vibrationA", dataTable.jTable.getValueAt(0, 2));
		dataMap.put("amplA", dataTable.jTable.getValueAt(0, 3));
		dataMap.put("phaseA", dataTable.jTable.getValueAt(0, 4));
		dataMap.put("heavyA", dataTable.jTable.getValueAt(0, 5));
		dataMap.put("heavyPhaseA", dataTable.jTable.getValueAt(0, 6));

		dataMap.put("speedB", dataTable.jTable.getValueAt(1, 1));
		dataMap.put("vibrationB", dataTable.jTable.getValueAt(1, 2));
		dataMap.put("amplB", dataTable.jTable.getValueAt(1, 3));
		dataMap.put("phaseB", dataTable.jTable.getValueAt(1, 4));
		dataMap.put("heavyB", dataTable.jTable.getValueAt(1, 5));
		dataMap.put("heavyPhaseB", dataTable.jTable.getValueAt(1, 6));

		dataMap.put("heavyC", dataTable.jTable.getValueAt(2, 5));
		dataMap.put("heavyPhaseC", dataTable.jTable.getValueAt(2, 6));
	}

	// 创建右键菜单
	public JPopupMenu createPopupMenu() {
		JPopupMenu jPopupMenu = new JPopupMenu();

		JMenuItem beforeDataItem = new JMenuItem(
				HduChartUtil.getResource("OfflineBalance_ImportBeforeData"));
		beforeDataItem.setActionCommand(BEFORE_DATA_COMMAND);
		beforeDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(beforeDataItem);

		JMenuItem afterDataItem = new JMenuItem(
				HduChartUtil.getResource("OfflineBalance_ImportAfterData"));
		afterDataItem.setActionCommand(AFTER_DATA_COMMAND);
		afterDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(afterDataItem);
		jPopupMenu.addSeparator();

		JMenuItem cancelBeforeDataItem = new JMenuItem(
				HduChartUtil.getResource("OfflineBalance_CancelBeforeData"));
		cancelBeforeDataItem.setActionCommand(CANCEL_BEFORE_DATA_COMMAND);
		cancelBeforeDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(cancelBeforeDataItem);

		JMenuItem cancelAfterDataItem = new JMenuItem(
				HduChartUtil.getResource("OfflineBalance_CancelAfterData"));
		cancelAfterDataItem.setActionCommand(CANCEL_AFTER_DATA_COMMAND);
		cancelAfterDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(cancelAfterDataItem);
		jPopupMenu.addSeparator();

		JMenuItem saveImage = new JMenuItem(
				HduChartUtil.getResource("Save_as..."));
		saveImage.setActionCommand(SAVE_IMAGE_COMMAND);
		saveImage.addActionListener(popupMenuListener);
		jPopupMenu.add(saveImage);

		JMenuItem creatWord = new JMenuItem(
				HduChartUtil.getResource("Common_CreatWord"));
		creatWord.setActionCommand(CREAT_WORD_COMMAND);
		creatWord.addActionListener(popupMenuListener);
		jPopupMenu.add(creatWord);
		//
		beforeHeavyChart.chartPanel.setPopupMenu(jPopupMenu);
		afterHeavyChart.chartPanel.setPopupMenu(jPopupMenu);
		setComponentPopupMenu(jPopupMenu);
		return jPopupMenu;

	}

	// 右键菜单响应
	private ActionListener popupMenuListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String command = e.getActionCommand();
			// System.out.println(command);
			if (command.equals(BEFORE_DATA_COMMAND)) {
				doImportBeforeData();
			} else if (command.equals(AFTER_DATA_COMMAND)) {
				doImportAfterData();
			} else if (command.equals(CANCEL_BEFORE_DATA_COMMAND)) {
				deleteChoice(1);
			} else if (command.equals(CANCEL_AFTER_DATA_COMMAND)) {
				deleteChoice(2);
			} else if (command.equals(SAVE_IMAGE_COMMAND)) {
				saveImage(getPanelSelf());
			} else if (command.equals(CREAT_WORD_COMMAND)) {
				creatWord();
			}
		}
	};

	private ActionListener calculateListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			calculateHeavy();
		}
	};

	// 表格类
	private class DataTable extends JPanel {
		public JTable jTable;
		public JScrollPane jScrollPane;
		public String speed = HduChartUtil.getResource("OfflineBalance_Speed");// 转速
		public String vibration = HduChartUtil
				.getResource("OfflineBalance_TotalVibration");// 总振值
		public String amplitude = HduChartUtil
				.getResource("OfflineBalance_TurnFre_Amplitude");// 转频幅值
		public String phase = HduChartUtil
				.getResource("OfflineBalance_TurnFre_Phase");// 转频相位
		public String heavy = HduChartUtil
				.getResource("OfflineBalance_Weight_Weight");// 配重重量
		public String heavyPhase = HduChartUtil
				.getResource("OfflineBalance_Weight_Amplitude"); // 配重幅度
		String originalData = HduChartUtil
				.getResource("OfflineBalance_OriginalData");// 原始数据
		String weightPram = HduChartUtil
				.getResource("OfflineBalance_Weight_Pram");// 配重参数
		String weightPlan = HduChartUtil
				.getResource("OfflineBalance_Weight_Plan");// 配重方案
		String analyse = HduChartUtil.getResource("OfflineBalance_Analysis");// 分解1

		public DataTable() {
			intion();
		}

		private void intion() {
			String[] columnNames = { "", speed, vibration, amplitude, phase,
					heavy, heavyPhase };
			Object[][] cellData = { { originalData, "", "", "", "", "", "" },
					{ weightPram, "", "", "", "", "", "" },
					{ weightPlan, "", "", "", "", "", "" },
					{ analyse, "", "", "", "", "", "" } };

			jTable = new JTable(cellData, columnNames);
			jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			// jTable.setDragEnabled(false);
			FitTableColumns(jTable);
			DefaultTableCellRenderer render = new DefaultTableCellRenderer();
			render.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
			jTable.setDefaultRenderer(Object.class, render);
			// jTable.setVisible(true);
			jScrollPane = new JScrollPane(jTable);
			jScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setPreferredSize(new Dimension(500, 90));
			add(jScrollPane);
			setVisible(true);
		}

		// 清楚表格某一行数据
		public void clearRowData(int row, JTable jTable) {
			int i = 1;
			for (i = 1; i < 7; i++) {
				jTable.setValueAt("", row, i);
			}
		}

		// 列宽自适应
		public void FitTableColumns(JTable myTable) {
			JTableHeader header = myTable.getTableHeader();
			int rowCount = myTable.getRowCount();

			Enumeration columns = myTable.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn column = (TableColumn) columns.nextElement();
				int col = header.getColumnModel().getColumnIndex(
						column.getIdentifier());
				int width = (int) myTable
						.getTableHeader()
						.getDefaultRenderer()
						.getTableCellRendererComponent(myTable,
								column.getIdentifier(), false, false, -1, col)
						.getPreferredSize().getWidth();
				for (int row = 0; row < rowCount; row++) {
					int preferedWidth = (int) myTable
							.getCellRenderer(row, col)
							.getTableCellRendererComponent(myTable,
									myTable.getValueAt(row, col), false, false,
									row, col).getPreferredSize().getWidth();
					width = Math.max(width, preferedWidth);
				}
				header.setResizingColumn(column); // 此行很重要
				column.setWidth(width + myTable.getIntercellSpacing().width
						+ 15);
			}
		}

	}

}
