/**
 * ****************************************************************************
 * Class name:  DataAnalysePanel.java	  Created  2014  2014�?�?1�? 下午10:18:24
 * Description:  
 * Company:     深圳云览科技有限公司
 * Department:  软件�?��事业�?
 * @author      liyj@yunlauncher.com
 * @version     1.0
 * --------------------------------------------------------------------------
 * 修改历史                                                                                                                                                                                                         
 * 序号  日期  修改�?  修改原因
 * 1
 *---------------------------------------------------------------------------
 * ************************************************************************** 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.OriginWSSearchDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceTreeMenuPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.historySearch.OfflineHistorySearchPanel;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线分拣数据界面 界面左侧是待分拣的特征量的树，中间是特征量的波形图，双击进行分拣，右侧是分拣操作的控制表
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class OfflineDataSelectJPanel extends JPanel implements
		ActionListener
{
	/** The resourceBundle for the localization. */
	// protected static ResourceBundle localizationResources =
	// ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);

	/** 声明用于存放波形图、频谱图的panel */
	public static JPanel xyImagePanel;

	/** 声明用于存放树形菜单的panel */
	private TreeMenuPanel treeMenuPanel;

	private String filePath;
	// 存放四个ain din eig wave的file
	private File[] dataFiles;

	private LineChartPanel linechartPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;
	private JTable table = new JTable();// 右边显示的菜单栏
	private JTable selectJTable = new JTable();// 选中信息菜单栏
	private JLabel selectJLabel = new JLabel();// 当前操作的时间信息显示

	/** 开始生产拣波文具 */
	private static final String START_SELECT_COMMAND = "START_SELECT";
	private MouseHandle mouseHandle;// 对于linechart的操作

	private static final int FeatureMax = 0;
	private static final int FeatureMin = 1;
	private static final int FeatureAvg = 2;
	private static final int FeatureEff = 3;
	private static final int FeatureVar = 4;

	private static final String ModeAut = "自动模式";
	private static final String ModeMan = "手动模式";

	private int Feature;// 特征量
	private String Mode;// 模式选择

	MyEditor editor = null;

	// 关键文件名称
	public final static String KEYNAME = OfflineHistorySearchPanel.filename;

	// 特征值
	String comboitem[] = {
			HduChartUtil.getResource("OfflineDataSelect_FeatureName_Max"),
			HduChartUtil.getResource("OfflineDataSelect_FeatureName_Min"),
			HduChartUtil.getResource("OfflineDataSelect_FeatureName_Avg"),
			HduChartUtil.getResource("OfflineDataSelect_FeatureName_Eff"),
			HduChartUtil.getResource("OfflineDataSelect_FeatureName_Var") };
	String modeString[] = {
			HduChartUtil.getResource("OfflineDataSelect_ModeName_Out"),
			HduChartUtil.getResource("OfflineDataSelect_ModeName_Man") };

	/** 构造函数 */
	public OfflineDataSelectJPanel()
	{
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
	}

	public boolean init()
	{
		filePath = getPath();
		if (null == filePath)
		{
			JOptionPane.showMessageDialog(this, "选择路径有误！");
			return false;
		}

		HashMap<String, String> map = new HashMap<String, String>();

		map.put(ConfigUtil.KEY_SELECT_PATH, filePath);
		ConfigUtil.updateProperties(map);// 更新配置文件

		new SelectDataSearchDialog(filePath)
		{

			@Override
			public void commitHandle(String experPath)
			{
				// TODO Auto-generated method stub
				filePath = experPath;
				/** 基本界面设计 */
				if (!guiDesign())
				{
					JOptionPane.showMessageDialog(this, "选择路径有误！");
					return;
				}
				Complete();
			}

		}.init();

		return true;
	}

	public abstract void Complete();

	// 获取路径
	private String getPath()
	{

		String pathString = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_SELECT_PATH);
		if (null == pathString)// 读取配置文件，如果没有则默认桌面
		{
			pathString = FileSystemView.getFileSystemView().getHomeDirectory()
					.getAbsolutePath();
		}

		JFileChooser fileChooser = new JFileChooser(pathString);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fileChooser.showOpenDialog(fileChooser);
		String filePath = "ERR";

		if (JFileChooser.CANCEL_OPTION == returnVal)// 如果选择取消，则直接返回s
		{
			return null;
		} else if (returnVal == JFileChooser.APPROVE_OPTION)
		{

			if (KEYNAME.equals(fileChooser.getSelectedFile().getName()))
			{

				filePath = fileChooser.getSelectedFile().getAbsolutePath();// 这个就是你选择的文件夹的路径
			} else
			{
				filePath = null;
			}
		}
		System.out.println(filePath);
		return filePath;

	}

	// 判断是否是有数据文件夹
	private boolean checkFilePath(String path)
	{
		File file = new File(path + "\\定时");
		File[] chlidFiles = file.listFiles();
		Vector<String> names = new Vector<String>();
		names.add("ain");
		names.add("din");
		names.add("eig");
		names.add("wave");
		if (null == chlidFiles)
		{
			return false;
		}
		for (File t : chlidFiles)
		{
			if (!names.contains(t.getName()))
			{
				return false;
			}
		}
		dataFiles = chlidFiles;
		return true;
	}

	private LineChartPanel getLineChartPanel(String name, PlaneXY xy, Color c)
	{
		LineChartPanel panel = new LineChartPanel();
		panel.setMouseDragOperation_X();
		panel.upAutSeriesData(name, xy, c);
		panel.setCloseSeriesVisibleInLegend(name);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date startdata;
		java.util.Date enddata;
		linechartPanel = panel;
		try
		{

			startdata = format.parse(treeMenuPanel.getCmtrCfgInfo()
					.getBeginTime());
			enddata = format.parse(treeMenuPanel.getCmtrCfgInfo().getEndTime());

			if (null == mouseHandle)
			{
				mouseHandle = new MouseHandle(startdata, enddata)
				{
					@Override
					protected LineChartPanel getLineChartPanel()
					{
						// TODO Auto-generated method stub
						return linechartPanel;
					}

				};
			} else
			{
				mouseHandle.clearSection();
			}

			if (null != editor)
			{
				mouseHandle.setOutTime(editor.getAutTime());
				mouseHandle.setIsOut(1 == editor.getModeIndex());
			}

			panel.addMouseListener(mouseHandle);
			panel.chartPanel.addMouseMotionListener(mouseHandle);

			panel.chartPanel.setFocusable(true);
			panel.addKeyListener(mouseHandle);
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return panel;
	}

	/**
	 * 对于linechartPanel的配置
	 * 
	 * @return
	 */
	private LineChartPanel getLineChartPanel()
	{
		// 暂时添加数据
		if (null == treeMenuPanel.cmtrCfgInfo)
		{
			float[] x = {};
			float[] y = {};

			PlaneXY xy = new PlaneXY(x, y);

			return getLineChartPanel("test", xy, ExtColor.getLineColor());
		}
		PlaneXY xy = treeMenuPanel.updateLineChartData(
				treeMenuPanel.cmtrCfgInfo.channelName[0], Feature);
		treeMenuPanel.channelName = treeMenuPanel.cmtrCfgInfo.channelName[0];

		return getLineChartPanel("test", xy, ExtColor.getLineColor());
	}

	/**
	 * Describe :界面设计函数 入口参数：无 返回值：�?
	 */
	public boolean guiDesign()
	{

		treeMenuPanel = new TreeMenuPanel(filePath)
		{
			@Override
			public void treeSelectionHandle(String name)
			{
				// TODO Auto-generated method stub
				centerPanel.remove(linechartPanel);
				linechartPanel = updateLineChart(name);

				GridBagUtil.setLocation(centerPanel, linechartPanel, 0, 1, 1,
						10, true);
				mouseHandle.setJTable(selectJTable);
				// selectJTable.getModel()
				mouseHandle.setSelectJlabel(selectJLabel);

				((DefaultTableModel) selectJTable.getModel()).setRowCount(0);

				updateUI();
			}

			public LineChartPanel updateLineChart(String name)
			{
				return getLineChartPanel(" ",
						updateLineChartData(name, Feature),
						ExtColor.getLineColor());
			}

		};

		if (!treeMenuPanel.init())
		{
			return false;
		}
		getCenterPanel();
		getRightPanel();

		setTableValue();// 更新表格中数据

		xyImagePanel = new JPanel(new GridBagLayout());
		xyImagePanel.setBackground(Color.WHITE);

		// 网格布局
		GridBagUtil.setLocation(xyImagePanel, treeMenuPanel, 0, 0, 1, 1, 1, 3,
				true);
		GridBagUtil.setLocation(xyImagePanel, centerPanel, 1, 1, 20, 2, 3, 1,
				true);
		GridBagUtil.setLocation(xyImagePanel, rightPanel, 4, 0, 1, 1, 1, 3,
				true);
		// GridBagUtil.addBlankJLabel(xyImagePanel, 1, 0, 1, 1);
		// GridBagUtil.addBlankJLabel(xyImagePanel, 1, 2, 1, 1);

		this.add(treeMenuPanel, BorderLayout.WEST);
		this.add(xyImagePanel, BorderLayout.CENTER);
		return true;
	}

	private JPanel getRightPanel()
	{
		JScrollPane textTablePane = getJScrollPane(table);
		// 失去焦点后提交数据
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		textTablePane.setBackground(Color.white);

		rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBackground(Color.white);

		rightPanel.add(textTablePane, BorderLayout.CENTER);
		rightPanel.add(textTablePane);

		JButton startSelectButton = new JButton(
				HduChartUtil
						.getResource("OfflineDataSelect_Button_CompleteSelect"));
		startSelectButton.addActionListener(this);
		startSelectButton.setActionCommand(START_SELECT_COMMAND);
		rightPanel.add(startSelectButton, BorderLayout.SOUTH);
		return rightPanel;
	}

	private JPanel getCenterPanel()
	{
		centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setBackground(Color.white);

		linechartPanel = getLineChartPanel();// 获取linechart

		JPanel labelJPanel = new JPanel(new FlowLayout());
		labelJPanel.setBackground(Color.white);
		labelJPanel.add(new JLabel());
		labelJPanel.add(new JLabel());
		labelJPanel.add(selectJLabel);
		selectJLabel.setBackground(Color.white);

		GridBagUtil.setLocation(centerPanel, labelJPanel, 0, 0, 1, 1, true);
		GridBagUtil.setLocation(centerPanel, linechartPanel, 0, 1, 1, 10, true);
		GridBagUtil.setLocation(centerPanel,
				getJScrollPane(selectJTable, mouseHandle.getSections()), 0, 2,
				1, 10, true);
		treeMenuPanel
				.treeSelectionHandle(treeMenuPanel.cmtrCfgInfo.channelName[0]);

		return centerPanel;
	}

	/**
	 * 获取分拣端信息表
	 * 
	 * @param jTable
	 * @param sections
	 * @return
	 */
	private JScrollPane getJScrollPane(JTable jTable,
			Vector<SelectSection> sections)
	{

		Vector<String> title = new Vector<String>();
		Vector<Vector<String>> data = new Vector<Vector<String>>();

		title.add(HduChartUtil
				.getResource("OfflineDataSelect_SelMsg_SelSection"));
		title.add(HduChartUtil
				.getResource("OfflineDataSelect_SelMsg_StartTime"));
		title.add(HduChartUtil.getResource("OfflineDataSelect_SelMsg_EndTime"));

		if (null != sections)
		{
			for (int i = 0; i < sections.size(); i++)
			{
				Vector<String> t = new Vector<String>();
				t.add((i + 1) + "");
				t.add(sections.get(i).getStartTimeString());
				t.add(sections.get(i).getEndTimeString());
				data.add(t);
			}
		}

		DefaultTableModel model = new DefaultTableModel();
		model.setDataVector(data, title);
		jTable.setModel(model);

		JScrollPane jscrollPane = new JScrollPane(jTable);
		jscrollPane.setPreferredSize(new Dimension(100, 100));

		jscrollPane.setBorder(new TitledBorder(HduChartUtil
				.getResource("OfflineDataSelect_SelMsg_Title")));
		jscrollPane.setBackground(Color.white);
		return jscrollPane;
	}

	/**
	 * 获取一个表格界面
	 * 
	 * @param jTable
	 *            需要被封装的表格
	 * @return 表格封装Jpane
	 */
	private JScrollPane getJScrollPane(JTable jTable)
	{

		Vector<String> title = new Vector<String>();
		Vector<Vector<String>> data = new Vector<Vector<String>>();

		title.add(HduChartUtil
				.getResource("OfflineDataSelect_OpetationMsg_Name"));
		title.add(HduChartUtil
				.getResource("OfflineDataSelect_OpetationMsg_Msg"));

		String item[] = {
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_Channel"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_Feature"),
				HduChartUtil.getResource("OfflineDataSelect_OpetationMsg_Mode"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_EachTime"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_StartTime"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_EndTime"),
				HduChartUtil.getResource("OfflineDataSelect_OpetationMsg_Fre"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_GetTime"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_FileNum"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_ProcessTime"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_FileSize"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_StartPoint"),
				HduChartUtil
						.getResource("OfflineDataSelect_OpetationMsg_EndPoint") };

		for (int i = 0; i < 13; i++)
		{
			Vector<String> t = new Vector<String>();
			t.add(item[i]);
			t.add("No Data");
			data.add(t);
		}

		DefaultTableModel model = new DefaultTableModel();
		model.setDataVector(data, title);
		jTable.setModel(model);

		JScrollPane jscrollPane = new JScrollPane(jTable);

		jscrollPane.setPreferredSize(new Dimension(200, 150));

		editor = new MyEditor(mouseHandle);
		jTable.getColumnModel().getColumn(1).setCellEditor(editor);

		return jscrollPane;
	}

	class MyEditor extends AbstractCellEditor implements TableCellEditor,
			ActionListener
	{

		private static final long serialVersionUID = 1L;
		private JComboBox featureBox = new JComboBox();
		private JComboBox modeBox = new JComboBox();
		private JLabel jLabel = new JLabel();
		private JTextField tf = new JTextField();
		private JTextField timetext = new JTextField();

		private Component currentEditorComp = null;
		MouseHandle mouseHandle;

		public MyEditor(MouseHandle mouseHandle)
		{
			this.mouseHandle = mouseHandle;

			for (String s : comboitem)
			{
				featureBox.addItem(s);
			}
			for (String s : modeString)
			{
				modeBox.addItem(s);
			}
			modeBox.setSelectedIndex(1);
			timetext.setText(30 + "");
			featureBox.addActionListener(this);
			modeBox.addActionListener(this);
			timetext.addActionListener(this);

		}

		public int getModeIndex()
		{
			return modeBox.getSelectedIndex();
		}

		public int getAutTime()
		{
			return Integer.parseInt(timetext.getText());
		}

		public boolean isCellEditable(EventObject e)
		{
			if (e instanceof MouseEvent)
			{
				MouseEvent me = (MouseEvent) e;
				if (me.getClickCount() >= 2)
				{
					return true;
				}
				return false;
			}
			return true;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column)
		{
			if (1 == row)
			{
				currentEditorComp = featureBox;
				featureBox.setSelectedItem(value);
			} else if (2 == row)
			{
				currentEditorComp = modeBox;
				modeBox.setSelectedItem(value);
			} else if (3 == row)
			{
				if (modeBox.getSelectedIndex() == 1)
				{
					currentEditorComp = timetext;
					timetext.setText(value == null ? "" : value.toString());
				} else
				{
					currentEditorComp = null;
				}

			} else
			{
				currentEditorComp = null;
			}
			return currentEditorComp;
		}

		public Object getCellEditorValue()
		{
			if (currentEditorComp == tf)
			{
				return tf.getText();
			} else if (currentEditorComp == featureBox)
			{
				return featureBox.getSelectedItem();
			} else if (currentEditorComp == modeBox)
			{
				return modeBox.getSelectedItem();
			} else if (currentEditorComp == timetext)
			{
				mouseHandle.setOutTime(Integer.parseInt(timetext.getText()));
				return timetext.getText();
			} else
			{
				return "No data";
			}
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			if (e.getSource() == featureBox)
			{
				Feature = featureBox.getSelectedIndex();
				centerPanel.remove(linechartPanel);
				linechartPanel = getLineChartPanel(" ",
						treeMenuPanel.updateLineChartData(
								treeMenuPanel.channelName, Feature),
						ExtColor.getLineColor());

				GridBagUtil.setLocation(centerPanel, linechartPanel, 0, 1, 1,
						10, true);
				mouseHandle.setJTable(selectJTable);

				mouseHandle.setSelectJlabel(selectJLabel);
				updateUI();

			} else if (e.getSource() == modeBox)
			{
				mouseHandle.setIsOut(ModeAut.equals((String) modeBox
						.getSelectedItem()));
			} else if (e.getSource() == timetext)
			{
				mouseHandle.setOutTime(Integer.parseInt(timetext.getText()));
			}

		}
	}

	// , "特征量", "分拣模式", "起始时间", "终止时间", "采样频率", "录波时间",
	// "文件数量", "处理周期", "文件规格", "起始点号", "终止点号"
	private void setTableValue()
	{
		table.setValueAt(treeMenuPanel.getCmtrCfgInfo().channelCount, 0, 1);// "通道数"

		Feature = FeatureMax;
		Mode = ModeAut;
		table.setValueAt(comboitem[0], 1, 1);// "特征量"
		table.setValueAt(ModeAut, 2, 1);// "分拣模式"
		table.setValueAt(30 + "", 3, 1);// "分拣模式"
		table.setValueAt(treeMenuPanel.getCmtrCfgInfo().getBeginTime(), 4, 1);// "起始时间"
		table.setValueAt(treeMenuPanel.getCmtrCfgInfo().getEndTime(), 5, 1);// "终止时间"
		table.setValueAt(treeMenuPanel.getCmtrCfgInfo().pointCount, 6, 1);// "采样频率"
		table.setValueAt(treeMenuPanel.getCmtrCfgInfo().getBeginTime(), 7, 1);// "录波时间"
		// table.setValueAt(getCmtrContent.beginTime, 6, 1);// "文件数量"
		table.setEditingRow(3);

	}

	/**
	 * Describe :这个是实现actionlistener接口的函数，实现对右键菜单的监听，完成不同操�?
	 * 入口参数:传入触发的事件，就是右键菜单中被选中的项目事�?返回值：�?
	 */
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		if (command.equals(START_SELECT_COMMAND))
		{
			if (null == mouseHandle.getSections()
					|| mouseHandle.getSections().isEmpty())
			{
				LoggerUtil.log(Level.WARNING, "select section err");
				JOptionPane.showMessageDialog(this, "请选择一个分拣段");
				return;
			}
			new SelectCheckDialog(mouseHandle.getSections(), filePath)
					.setVisible(true);
		}
	}

	// 用于设置表格大小
	private static Dimension adjustTableColumnWidths(JTable table)
	{

		JTableHeader header = table.getTableHeader(); // 表头
		int rowCount = table.getRowCount(); // 表格的行数
		TableColumnModel cm = table.getColumnModel(); // 表格的列模型
		float sumsize = 0;
		for (int i = 0; i < cm.getColumnCount(); i++)
		{ // 循环处理每一列
			TableColumn column = cm.getColumn(i); // 第i个列对象
			int width = (int) header
					.getDefaultRenderer()
					.getTableCellRendererComponent(table,
							column.getIdentifier(), false, false, -1, i)
					.getPreferredSize().getWidth(); // 用表头的绘制器计算第i列表头的宽度
			for (int row = 0; row < rowCount; row++)
			{ // 循环处理第i列的每一行，用单元格绘制器计算第i列第row行的单元格宽度
				int preferedWidth = (int) table
						.getCellRenderer(row, i)
						.getTableCellRendererComponent(table,
								table.getValueAt(row, i), false, false, row, i)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth); // 取最大的宽度
			}
			column.setPreferredWidth(width + table.getIntercellSpacing().width); // 设置第i列的首选宽度
			sumsize += width + table.getIntercellSpacing().width;

		}

		table.doLayout(); // 按照刚才设置的宽度重新布局各个列
		return new Dimension((int) sumsize, rowCount * 12);
	}

	public static void main(String[] args)
	{

		// JFrame jFrame = new JFrame();
		// jFrame.setTitle("WaveForm");
		// jFrame.setSize(800, 600);
		// jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		// OfflineDataSelectJPanel waveform = new OfflineDataSelectJPanel();
		//
		// jFrame.add(waveform);// 添加到主界面中
		// jFrame.setVisible(true);

	}

}
