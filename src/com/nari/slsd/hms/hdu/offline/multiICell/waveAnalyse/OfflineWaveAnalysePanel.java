/**
 * ****************************************************************************
 * Class name:  OfflineWaveAnalysePanel.java	  Created  2014  2014�?�?1�? 下午10:18:24
 * Description:  离线分析时，显示波形图和频谱图
 * Department:  HDU
 * @author      YXQ and Lynn
 * @version     1.0
 * ************************************************************************** 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.undo.UndoManager;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.XYPlot;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.common.comtrade.ComtradeData;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 波形显示界面
 * 
 * @author Administrator
 * 
 */
public class OfflineWaveAnalysePanel extends JPanel implements ActionListener
{
	// 文件路径
	private final String OpenPath = "电站I-1号机组#2014-10-17 15-46-06#2014-10-17 15-46-14#wave";

	/** 保存图片命令 */
	private final String SAVE_COMMAND = "SAVE";
	/** 增加通道曲线命令 */
	private final String ADD_LINE_COMMAND = "ADD_LINE";
	/** 显示全图命令 */
	private final String SHOW_ALL_COMMAND = "SHOW_ALL";
	/** 显示原波形图命令 */
	private final String SHOW_ORIGINAL_COMMAND = "SHOW_ORIGINAL";
	/** 显示频谱图命�? */
	private final String SHOW_FRIER_COMMAND = "SHOW_FRIER";
	/** 保存数据命令 */
	private final String SAVE_DATA_COMMAND = "SAVE_DATA";
	/** 波形滤波命令 */
	private final String FILTER_COMMAND = "FILTER";
	/** FIR波形滤波命令 */
	private final String FIR_FILTER_COMMAND = "FIR_FILTER";
	/** IIR波形滤波命令 */
	private final String IIR_FILTER_COMMAND = "IIR_FILTER";
	/** 显示键相点 */
	private final String SHOW_JIANXIANG_COMMAND = "SHOW_JIANXIANG";
	/** 整周期fft */
	private final String ZHENG_FFT_COMMAND = "ZHENG_FFT";
	/** 恢复滤波前数据 */
	private final String RETURN_ORIGINAL_COMMAND = "RETURN_ORIGINAL";
	/** 增加趋势线 */
	private final String ADD_TENDENCY_COMMAND = "ADD_TENDENCY";
	/** 输出word文档 */
	private final String CREAT_WORD_COMMAND = "CREAT_WORD";

	/** 是否进行频谱分析 */
	private final String ISFFT_COMMAND = "ISFFT";

	/** The resourceBundle for the localization. */
	// protected ResourceBundle res = ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);

	/** 声明用于存放原波形图的panel */
	public OriginalChartPanel originalChartPanel;
	/** 声明用于存放频谱的panel */
	public FrierChartPanel frierChartPanel;

	/** 声明用于存放波形和频谱图特征值的panel */
	private JPanel showValuePanle;

	/** 声明用于存放原始波形特征值的panel */
	private CharacterPanel characterPanel;

	/** 声明用于存放频谱主频特征值的panel */
	private BasicFrePanel basicalPanel;

	/** 声明用于存放波形图、频谱图的panel */
	public JPanel xyImagePanel;
	// public static Box xyImagePanel;
	/** 声明当浮点检测时，画出第一条跟踪轨迹的点坐标 */

	// 创建两个label，用于当浮点�?��或�?临近浮点�?��时，实时显示坐标值，�?��设置成public static类型
	public static JLabel xvalue;
	public static JLabel yvalue;
	private int start;

	/** 声明撤销操作所用到的类 */
	public static UndoManager undoManager = new UndoManager();

	/** 声明右键菜单 */
	private JPopupMenu jPopupMenu;

	/** Cmtr的信息 **/
	PlaneXY planeXY;// 数据
	CmtrCfgInfo cmtrCfgInfo;// 配置信息

	// 存放打开的工作空间，可以多个
	private Vector<WorkSpaceProp> workSpaceProps;
	private Vector<String> soucePaths;

	public static float[] jianxiangX;
	public static float[] jianxiangY;
	private float[] jianXiangChannel;
	private Vector<Integer> keyData;
	private String jianXiangLab = HduChartUtil
			.getResource("OfflineAnalyse_JianXiang");
	private HduCreatWord hduCreatWord;

	private JCheckBox isFFTBox = new JCheckBox();

	/** 构造函数 */
	public OfflineWaveAnalysePanel(Vector<WorkSpaceProp> workSpaceProps,
			String name, CmtrCfgInfo cmtrCfgInfo, Vector<String> soucePaths,
			DefaultMutableTreeNode node)
	{
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		this.workSpaceProps = workSpaceProps;
		this.soucePaths = soucePaths;
		guiDesign();
		OfflinetreeSelectionHandle(name, cmtrCfgInfo, soucePaths, node);
	}

	public JPanel getPanelSelf()
	{
		return this;
	}

	// 实现同步
	private void together()
	{
		XYPlot xyPlot = originalChartPanel.chart.getXYPlot();
		ValueAxis domian = xyPlot.getDomainAxis();
		domian.addChangeListener(originalAxisChanged);
	}

	/**
	 * Describe :保存数据函数 入口参数�?返回值：
	 */
	private void doSaveData()
	{
		String data = HduChartUtil
				.getResource("Common_data");
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser(chartName + data);
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setSelectedFile(new File(chartName + data));
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)
		{
			savePath = null;
			return;
		}

		else
		{
			savePath = jFileChooser.getSelectedFile().getPath() + ".DAT";
		}
		File fout = new File(savePath);
		FileWriter fw;
		BufferedWriter bw;
		String sb = new String();
		try
		{
			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);// 初始化输出字符流
			for (int i = 0; i < originalChartPanel.planeXY.getY().length; i++)
			{
				sb += String.valueOf(originalChartPanel.planeXY.getY()[i])
						+ "\r\n";
			}
			// sb.append(yvalues.toString());
			bw.write(sb);
			// bw.write(sb);// 写文�?
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 初始化输出流

	}

	// 输出word
	private void doCreatWord()
	{
		String report = HduChartUtil
				.getResource("Common_Report");
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File(chartName + report));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else
		{
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"waveAnalyseModel.ftl")
		{
			@Override
			public void getData(Map<String, Object> dataMap)
			{
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				dataMap.put("channel", chartName);
				dataMap.put("image", ImageChange.getImageEncode(getPanelSelf()));
				inputData2Word(dataMap);
				dataMap.put("fileName", chartName);
				dataMap.put("channelName", "");
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}

	// 将数据存入word中
	private void inputData2Word(Map<String, Object> dataMap)
	{
		DecimalFormat df = new DecimalFormat("######0.00");// 保留两位有效数字
		dataMap.put("dianshu",
				df.format(characterPanel.endPoint - characterPanel.startPoint));
		dataMap.put("time", characterPanel.endTime);
		dataMap.put("fre", cmtrCfgInfo.smprateRate);
		dataMap.put("max", df.format(characterPanel.maxAndMinAndPeak[0]));
		dataMap.put("min", characterPanel.maxAndMinAndPeak[1]);
		dataMap.put("mean", characterPanel.meanValue);
		dataMap.put("middle", characterPanel.midValue);
		dataMap.put("valid", characterPanel.validValue);
		dataMap.put("peak", df.format(characterPanel.maxAndMinAndPeak[2]));

		dataMap.put("fre1",basicalPanel.table.getValueAt(0, 1));
		dataMap.put("fre2",basicalPanel.table.getValueAt(1, 1));
		dataMap.put("fre3",basicalPanel.table.getValueAt(2, 1));
		dataMap.put("fre4",basicalPanel.table.getValueAt(3, 1));
		dataMap.put("fre5",basicalPanel.table.getValueAt(4, 1));
		dataMap.put("fre6",basicalPanel.table.getValueAt(5, 1));
		dataMap.put("fre7",basicalPanel.table.getValueAt(6, 1));
		
		dataMap.put("amp1",basicalPanel.table.getValueAt(0, 2));
		dataMap.put("amp2",basicalPanel.table.getValueAt(1, 2));
		dataMap.put("amp3",basicalPanel.table.getValueAt(2, 2));
		dataMap.put("amp4",basicalPanel.table.getValueAt(3, 2));
		dataMap.put("amp5",basicalPanel.table.getValueAt(4, 2));
		dataMap.put("amp6",basicalPanel.table.getValueAt(5, 2));
		dataMap.put("amp7",basicalPanel.table.getValueAt(6, 2));

		dataMap.put("pha1",basicalPanel.table.getValueAt(0, 3));
		dataMap.put("pha2",basicalPanel.table.getValueAt(1, 3));
		dataMap.put("pha3",basicalPanel.table.getValueAt(2, 3));
		dataMap.put("pha4",basicalPanel.table.getValueAt(3, 3));
		dataMap.put("pha5",basicalPanel.table.getValueAt(4, 3));
		dataMap.put("pha6",basicalPanel.table.getValueAt(5, 3));
		dataMap.put("pha7",basicalPanel.table.getValueAt(6, 3));
		
		
//		dataMap.put("fre1",
//				df.format(Double.parseDouble(basicalPanel.firFreTxt.getText())));
//		dataMap.put("fre2",
//				df.format(Double.parseDouble(basicalPanel.secFreTxt.getText())));
//		dataMap.put("fre3",
//				df.format(Double.parseDouble(basicalPanel.thiFreTxt.getText())));
//		dataMap.put("fre4",
//				df.format(Double.parseDouble(basicalPanel.forFreTxt.getText())));
//		dataMap.put("fre5",
//				df.format(Double.parseDouble(basicalPanel.fifFreTxt.getText())));
//		dataMap.put("fre6",
//				df.format(Double.parseDouble(basicalPanel.sixFreTxt.getText())));
//		dataMap.put("fre7",
//				df.format(Double.parseDouble(basicalPanel.sevFreTxt.getText())));

//		dataMap.put("amp1",
//				df.format(Double.parseDouble(basicalPanel.firAmpTxt.getText())));
//		dataMap.put("amp2",
//				df.format(Double.parseDouble(basicalPanel.secAmpTxt.getText())));
//		dataMap.put("amp3",
//				df.format(Double.parseDouble(basicalPanel.thiAmpTxt.getText())));
//		dataMap.put("amp4",
//				df.format(Double.parseDouble(basicalPanel.forAmpTxt.getText())));
//		dataMap.put("amp5",
//				df.format(Double.parseDouble(basicalPanel.fifAmpTxt.getText())));
//		dataMap.put("amp6",
//				df.format(Double.parseDouble(basicalPanel.sixAmpTxt.getText())));
//		dataMap.put("amp7",
//				df.format(Double.parseDouble(basicalPanel.sevAmpTxt.getText())));
//
//		dataMap.put("pha1", df.format(Double
//				.parseDouble(basicalPanel.firPhaseTxt.getText())));
//		dataMap.put("pha2", df.format(Double
//				.parseDouble(basicalPanel.secPhaseTxt.getText())));
//		dataMap.put("pha3", df.format(Double
//				.parseDouble(basicalPanel.thiPhaseTxt.getText())));
//		dataMap.put("pha4", df.format(Double
//				.parseDouble(basicalPanel.forPhaseTxt.getText())));
//		dataMap.put("pha5", df.format(Double
//				.parseDouble(basicalPanel.fifPhaseTxt.getText())));
//		dataMap.put("pha6", df.format(Double
//				.parseDouble(basicalPanel.sixPhaseTxt.getText())));
//		dataMap.put("pha7", df.format(Double
//				.parseDouble(basicalPanel.sevPhaseTxt.getText())));

	}

	// 显示键相点，yvalue为通道数据
	private void showJianxiang(float yvalue[])
	{
		String[] name = chartName.split(",");
		// WorkSpaceProp prop = workSpaceProps.get(0);
		WorkSpaceProp prop = WorkSpaceProp.getWorkSpace(workSpaceProps,
				name[0], name[1]);
		keyData = prop.getKeyIndexData(name[2]);
		if (keyData.size() == 0)
		{
			JOptionPane.showMessageDialog(null,
					HduChartUtil.getResource("OfflineAnalyse_JianxiangError"));
			return;
		}
		PlaneXY planeXY = new PlaneXY();
		jianxiangX = new float[keyData.size()];
		jianxiangY = new float[keyData.size()];
		for (int j = 0; j < keyData.size(); j++)
		{
			jianxiangX[j] = keyData.get(j);
			if (jianxiangX[j] < yvalue.length)
			{
				jianxiangY[j] = yvalue[keyData.get(j)];
			}
		}
		planeXY.setX(jianxiangX);
		planeXY.setY(jianxiangY);
		originalChartPanel.upAutSeriesData(jianXiangLab, planeXY, Color.red,
				true);
	}

	// 整周期fft
	private void zhengFFT(float yvalue[])
	{
		int length;
		float[] zhengDataX;
		float[] zhengDataY;
		float[] outPut;
		PlaneXY planeXY = new PlaneXY();
		if (jianxiangX == null)
		{
			return;
		}
		originalChartPanel.mouseListener.mouseDragOperation = originalChartPanel.mouseListener.SHOW_JIANXIANG;
		length = (int) (jianxiangX[jianxiangX.length - 1] - jianxiangX[0]);
		zhengDataX = new float[length];
		zhengDataY = new float[length];
		for (int i = 0, j = (int) jianxiangX[0]; i < length; i++, j++)
		{
			zhengDataY[i] = yvalue[j];
		}

		outPut = Calculate.CNiWaveFraqView(zhengDataY,
				(int) cmtrCfgInfo.smprateRate);
		zhengDataX = new float[outPut.length];
		for (int i = 0; i < outPut.length; i++)
		{
			zhengDataX[i] = i;
		}
		planeXY.setX(zhengDataX);
		planeXY.setY(outPut);
		// originalChartPanel.creatOriginalCharts(zhengDataX, outPut);
		frierChartPanel.creatFrierChart(outPut);
	}

	/**
	 * Describe :界面设计函数 入口参数：无 返回值：�?
	 */
	public void guiDesign()
	{
		setBackground(Color.white);
		jPopupMenu = new JPopupMenu();
		// xyImagePanel=Box.createVerticalBox();
		xyImagePanel = new JPanel(new GridLayout(2, 1));
		xyImagePanel.setBackground(Color.WHITE);
		showValuePanle = new JPanel(new GridLayout(2, 1));
		showValuePanle.setBackground(Color.white);
		characterPanel = new CharacterPanel();
		characterPanel.setBackground(Color.white);
		basicalPanel = new BasicFrePanel();
		basicalPanel.setBackground(Color.white);
		//this.setLayout(new BorderLayout());
		add(xyImagePanel);
		xyImagePanel.setComponentPopupMenu(jPopupMenu);

	//	this.add(getTopPanel(), BorderLayout.NORTH)

	}

	private JPanel getTopPanel()
	{
		JPanel jPanel = new JPanel(new GridBagLayout());
		
		isFFTBox = new JCheckBox(
				HduChartUtil.getResource("OnlineWaterfall_AutoSuit"));
		isFFTBox.addActionListener(this);
		isFFTBox.setActionCommand(ISFFT_COMMAND);
		isFFTBox.setSelected(true);
		isFFTBox.setBackground(java.awt.Color.white);
		GridBagUtil.addBlankJLabel(jPanel, 0, 0, 10, 1);
		GridBagUtil.setLocation(jPanel, isFFTBox, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(jPanel, 2, 0, 1, 1);
		
		return jPanel;
		
	}

	// 获取当前的数据范围，将此范围中的数据返回
	public PlaneXY getRange()
	{
		PlaneXY planeXY = new PlaneXY();
		float[] originalX = originalChartPanel.planeXY.getX();
		float[] originalY = originalChartPanel.planeXY.getY();
		float[] ScaleRangeX;
		float[] ScaleRangeY;
		XYPlot plot = originalChartPanel.chart.getXYPlot();
		ValueAxis d = plot.getDomainAxis();// 获取横坐标上的数据值
		int first = (int) d.getLowerBound();
		start = first;
		int last = (int) d.getUpperBound();
		int range = last - first;
		if (range < 0)
		{
			ScaleRangeY = originalY;
			ScaleRangeX = originalX;
		} else
		{
			ScaleRangeY = new float[range];
			ScaleRangeX = new float[range];
			for (int i = 0, j = first; i < range; i++, j++)
			{
				if ((first >= originalX.length) || j >= originalX.length)
				{
					planeXY.setX(ScaleRangeX);
					planeXY.setY(ScaleRangeY);
					return planeXY;
				}
				ScaleRangeX[i] = j;
				ScaleRangeY[i] = originalY[j];
			}
		}
		planeXY.setX(ScaleRangeX);
		planeXY.setY(ScaleRangeY);
		return planeXY;
	}

	/**
	 * Describe :创建右键菜单，电机右键时跳出此菜�? 入口参数：无 返回值：JPopupMenu是一个右键菜单类�?
	 */
	private JPopupMenu createPopupMenu()
	{
		JPopupMenu jpopupMenu = new JPopupMenu();
		/** 创建波形图的菜单 */
		JMenu originalMenu = originalChartPanel.getJMenu();
		/** 将波形图菜单嵌入总菜单 */
		jpopupMenu.add(originalMenu);

		/** 创建频谱图的菜单 */
		JMenu frierMenu = frierChartPanel.getJMenu();

		/** 将频谱菜单嵌入总菜单 */
		jpopupMenu.add(frierMenu);
		jpopupMenu.addSeparator();

		JMenu showMenu = new JMenu(HduChartUtil.getResource("Show_Select"));

		JMenuItem ImageAll = new JMenuItem(
				HduChartUtil.getResource("Image_all"));
		ImageAll.setActionCommand(SHOW_ALL_COMMAND);
		ImageAll.addActionListener(this);
		showMenu.add(ImageAll);

		JMenuItem ImageOriginal = new JMenuItem(
				HduChartUtil.getResource("Image_Ori"));
		ImageOriginal.setActionCommand(SHOW_ORIGINAL_COMMAND);
		ImageOriginal.addActionListener(this);
		showMenu.add(ImageOriginal);

		JMenuItem ImageFrier = new JMenuItem(
				HduChartUtil.getResource("Image_Frier"));
		ImageFrier.setActionCommand(SHOW_FRIER_COMMAND);
		ImageFrier.addActionListener(this);
		showMenu.add(ImageFrier);
		jpopupMenu.add(showMenu);
		jpopupMenu.addSeparator();

		JMenuItem addLine = new JMenuItem(HduChartUtil.getResource("Add_Line"));
		addLine.setActionCommand(ADD_LINE_COMMAND);
		addLine.addActionListener(this);
		jpopupMenu.add(addLine);

		JMenuItem addTendency = new JMenuItem(
				HduChartUtil.getResource("Add_Tendency"));
		addTendency.setActionCommand(ADD_TENDENCY_COMMAND);
		addTendency.addActionListener(this);
		jpopupMenu.add(addTendency);
		jpopupMenu.addSeparator();

		final JCheckBoxMenuItem zhengFft = new JCheckBoxMenuItem(
				HduChartUtil.getResource("Zheng_fft"));
		zhengFft.setActionCommand(ZHENG_FFT_COMMAND);
		zhengFft.addActionListener(this);
		jpopupMenu.add(zhengFft);

		final JCheckBoxMenuItem showJianxiang = new JCheckBoxMenuItem(
				HduChartUtil.getResource("Show_Jianxiang"));
		showJianxiang.setActionCommand(SHOW_JIANXIANG_COMMAND);
		showJianxiang.addActionListener(this);
		jpopupMenu.add(showJianxiang);
		jpopupMenu.addSeparator();

		zhengFft.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				// TODO Auto-generated method stub
				if (zhengFft.getState())
				{
					showJianxiang.setSelected(true);
					zhengFFT(originalChartPanel.planeXY.getY());
				} else
				{
					frierChartPanel.creatFrierChart(originalChartPanel.planeXY
							.getY());
					originalChartPanel.mouseListener.mouseDragOperation = originalChartPanel.mouseListener.NOTHING;
				}
			}
		});

		showJianxiang.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				// TODO Auto-generated method stub
				if (showJianxiang.getState())
				{
					showJianxiang(originalChartPanel.planeXY.getY());
				} else
				{
					originalChartPanel.deleteSeries(jianXiangLab);
				}
			}
		});

		JMenu filter = new JMenu(HduChartUtil.getResource("Filter"));

		JMenuItem firFilter = new JMenuItem(
				HduChartUtil.getResource("FIR_Filter"));
		firFilter.setActionCommand(FIR_FILTER_COMMAND);
		firFilter.addActionListener(this);
		filter.add(firFilter);
		JMenuItem iirFilter = new JMenuItem(
				HduChartUtil.getResource("IIR_Filter"));
		iirFilter.setActionCommand(IIR_FILTER_COMMAND);
		iirFilter.addActionListener(this);
		filter.add(iirFilter);
		jpopupMenu.add(filter);

		JMenuItem returnOriginal = new JMenuItem(
				HduChartUtil.getResource("Return_Original"));
		returnOriginal.setActionCommand(RETURN_ORIGINAL_COMMAND);
		returnOriginal.addActionListener(this);
		jpopupMenu.add(returnOriginal);
		jpopupMenu.addSeparator();

		JMenuItem save = new JMenuItem(HduChartUtil.getResource("Save_as..."));
		save.setActionCommand(SAVE_COMMAND);
		save.addActionListener(this);
		jpopupMenu.add(save);

		JMenuItem saveData = new JMenuItem(
				HduChartUtil.getResource("Save_Data"));
		saveData.setActionCommand(SAVE_DATA_COMMAND);
		saveData.addActionListener(this);
		jpopupMenu.add(saveData);

		jpopupMenu.addSeparator();

		JMenuItem creatWord = new JMenuItem(
				HduChartUtil.getResource("Common_CreatWord"));
		creatWord.setActionCommand(CREAT_WORD_COMMAND);
		creatWord.addActionListener(this);
		jpopupMenu.add(creatWord);

		return jpopupMenu;

	}

	// 原波形名称
	private String chartName;

	/**
	 * 外部树的处理
	 * 
	 * @param e
	 *            树的事件
	 * @param cmtrContent
	 *            cmtr的内容
	 */
	public void OfflinetreeSelectionHandle(String name,
			CmtrCfgInfo cmtrCfgInfo, Vector<String> soucePaths,
			DefaultMutableTreeNode node)
	{
		// this.cmtrContent = cmtrContent;
		this.cmtrCfgInfo = cmtrCfgInfo;

		xyImagePanel.removeAll();

		float[] tempX = new float[cmtrCfgInfo.pointCount];
		float[] tempY = new float[cmtrCfgInfo.pointCount];

		PlaneXY planeXY = new PlaneXY();
		int index = 0;
		for (int i = 0; i < cmtrCfgInfo.channelCount; i++)
		{

			if (name.equals(cmtrCfgInfo.channelName[i]))
			{

				tempY = ComtradeData.getOneChannelDataFromFiles(soucePaths,
						cmtrCfgInfo, i);// i是第几个通达+特征量的偏移
				// jianXiangChannel = ComtradeData.getOneChannelDataFromFiles(
				// soucePaths, cmtrCfgInfo, 23);
				index = i;
				for (int j = 0; j < cmtrCfgInfo.pointCount; j++)
				{
					tempX[j] = j;
				}
				planeXY.setX(tempX);
				planeXY.setY(tempY);

				break;
			}
		}
		chartName = node.getParent().getParent().getParent().toString() + ","
				+ node.getParent().getParent().toString() + ","
				+ node.getParent().toString() + "," + name;
		originalChartPanel = new OriginalChartPanel(chartName, planeXY);
		originalChartPanel.setYLable(cmtrCfgInfo.getAnalogs().get(index).getUnit());//显示单位
		together();
		characterPanel.getValue(0, planeXY.getY(), cmtrCfgInfo);
		xyImagePanel.add(originalChartPanel, BorderLayout.NORTH);

		frierChartPanel = new FrierChartPanel(tempY,
				cmtrCfgInfo.getSmprateCount(), (int) cmtrCfgInfo.smprateRate);
		frierChartPanel.setYLable(cmtrCfgInfo.getAnalogs().get(index).getUnit());
		frierChartPanel.setXLable("Hz");
		basicalPanel.setValue(frierChartPanel.planeXY,
				frierChartPanel.frierChange);
		xyImagePanel.add(frierChartPanel, BorderLayout.SOUTH);

		showValuePanle.add(characterPanel);
		showValuePanle.add(basicalPanel);
		add(showValuePanle, BorderLayout.EAST);
		jPopupMenu = createPopupMenu();

		originalChartPanel.chartPanel.setPopupMenu(jPopupMenu);
		frierChartPanel.chartPanel.setPopupMenu(jPopupMenu);

		xyImagePanel.setComponentPopupMenu(jPopupMenu);
		updateUI();
		this.planeXY = planeXY;
	}

	/**
	 * Describe :这个是实现actionlistener接口的函数，实现对右键菜单的监听，完成不同操�?
	 * 入口参数:传入触发的事件，就是右键菜单中被选中的项目事�?返回值：�?
	 */
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		if (command.equals(SHOW_ALL_COMMAND))
		{
			xyImagePanel.setLayout(new GridLayout(2, 1));
			originalChartPanel.setVisible(true);
			frierChartPanel.setVisible(true);
			xyImagePanel.add(originalChartPanel);
			xyImagePanel.add(frierChartPanel);

		} else if (command.equals(SHOW_ORIGINAL_COMMAND))
		{
			xyImagePanel.setLayout(new BorderLayout());
			originalChartPanel.setVisible(true);
			frierChartPanel.setVisible(false);
			xyImagePanel.add(originalChartPanel, BorderLayout.CENTER);
			xyImagePanel.add(frierChartPanel, BorderLayout.NORTH);

		} else if (command.equals(SHOW_FRIER_COMMAND))
		{
			xyImagePanel.setLayout(new BorderLayout());
			originalChartPanel.setVisible(false);
			frierChartPanel.setVisible(true);
			xyImagePanel.add(originalChartPanel, BorderLayout.NORTH);
			xyImagePanel.add(frierChartPanel, BorderLayout.CENTER);

		} else if (command.equals(SAVE_DATA_COMMAND))
		{
			doSaveData();
		} else if (command.equals(ADD_LINE_COMMAND))
		{
			AddLineProDialog addLineProDialog = new AddLineProDialog(
					workSpaceProps, originalChartPanel)
			{
			};
			addLineProDialog.setVisible(true);

		} else if (command.equals(SAVE_COMMAND))
		{
			DialogSetSave savefm = new DialogSetSave(xyImagePanel,
					originalChartPanel, frierChartPanel);
			// savefm.pack();
			savefm.setVisible(true);
		} else if (command.equals(ADD_TENDENCY_COMMAND))
		{
			DialogAddTendency addTendencyFrame = new DialogAddTendency(
					originalChartPanel);
			addTendencyFrame.setVisible(true);

		} else if (command.equals(ZHENG_FFT_COMMAND))
		{
			// doShowJianxiang(originalChartPanel.planeXY.getY());
			// zhengFft(originalChartPanel.planeXY.getY());
			// // originalChartPanel.backFlag =originalChartPanel. ORIGINAL;
			//
			// originalChartPanel.mouseListener.mouseDragOperation =
			// originalChartPanel.mouseListener.SHOW_JIANXIANG;
			// originalChartPanel.panelOriginal.mouseDragOperation =
			// originalChartPanel.SHOW_JIANXIANG;

		} else if (command.equals(SHOW_JIANXIANG_COMMAND))
		{
			// jianxiangFlag++;
			// doShowJianxiang(originalChartPanel.planeXY.getY());

		} else if (command.equals(FIR_FILTER_COMMAND))
		{
			DialogFirFilter firFilterFrame = new DialogFirFilter(
					originalChartPanel, frierChartPanel);
			// filterFrame.pack();
			firFilterFrame.setVisible(true);
		} else if (command.equals(IIR_FILTER_COMMAND))
		{
			DialogIIRFilter iirFilterFrame = new DialogIIRFilter(
					originalChartPanel, frierChartPanel);
			// filterFrame.pack();
			iirFilterFrame.setVisible(true);
		} else if (command.equals(RETURN_ORIGINAL_COMMAND))
		{
			originalChartPanel.upAutSeriesData(chartName,
					originalChartPanel.planeXY, Color.blue);
			frierChartPanel.creatFrierChart(originalChartPanel.planeXY.getY());
		} else if (command.equals(CREAT_WORD_COMMAND))
		{
			doCreatWord();
		}
	}

	public AxisChangeListener originalAxisChanged = new AxisChangeListener()
	{

		@Override
		public void axisChanged(AxisChangeEvent arg0)
		{
			// TODO Auto-generated method stub
			if (originalChartPanel.chartPanel.mouseDragOperation != originalChartPanel.chartPanel.LINE_MOVE)
			{
				PlaneXY planeXY = getRange();
				if (originalChartPanel.chartPanel.mouseDragOperation == originalChartPanel.chartPanel.SHOW_JIANXIANG)
				{
					float[] outPut = Calculate.CNiWaveFraqView(planeXY.getY(),
							(int) cmtrCfgInfo.smprateRate);
					frierChartPanel.creatFrierChart(outPut);
					characterPanel.getValue(start, planeXY.getY(), cmtrCfgInfo);
					basicalPanel.setValue(frierChartPanel.planeXY,
							frierChartPanel.frierChange);
				} else
				{
					frierChartPanel.creatFrierChart(planeXY.getY());
					characterPanel.getValue(start, planeXY.getY(), cmtrCfgInfo);
					basicalPanel.setValue(frierChartPanel.planeXY,
							frierChartPanel.frierChange);
				}
			}

		}

	};
	// 上面的显示
	// AnalyseWavePanel.xvalue.setText(String.valueOf(String
	// .valueOf(mousePoint[0])));
	// AnalyseWavePanel.yvalue.setText(String.valueOf(String
	// .valueOf(mousePoint[1])));

}
