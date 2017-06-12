package com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.historySearch.DateChooserJButton;
import com.nari.slsd.hms.hdu.offline.multiICell.historySearch.OfflineHistorySearchPanel;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

public abstract class OriginWSSearchDialog extends JFrame implements
		ActionListener
{

	private static String path = "WorkRecords.txt";
	private static String CMD_COMMIT = "确认";
	private static String CMD_CANCEL = "取消";

	private DateChooserJButton startTimeButton = new DateChooserJButton();
	private DateChooserJButton endTimeButton = new DateChooserJButton();

	private JButton commitButton;
	private JButton cancelButton;

	private JComboBox stationBox;// 机组信息选择
	private JComboBox workBox;// 工况选择
	private static String[] workCases;

	// 静态区初始化工况类型
	{
		String value = ConfigUtil.getPropertiesValue(ConfigUtil.KEY_WORKTYPES);
		if (null == value)
		{
			workCases = new String[] { "未知工况","黑匣子", "解列运行", "自动开机过程", "手动开机过程",
					"空转过程", "空载过程", "自动停机过程", "紧急停机过程", "定时试验工况", "并网运行",
					"负载稳定运行", "有功增负荷", "有功减负荷", "无功加负荷", "无功减负荷", "调相",
					"整周期试验工况", "停机备用", "开机准备", "检修态" };
		} else
		{
			workCases = value.split(",");
		}
		
	}

	protected WorkSpaceProp workSpaceProp;
	private String rootPath;

	// 输入根文件，就是站点的上一层
	public OriginWSSearchDialog(String rootPath, WorkSpaceProp prop)
	{
		super();
		this.workSpaceProp = prop;
		this.rootPath = rootPath;
		this.setSize(550, 230);
		this.setLayout(new BorderLayout());
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this
				.getSize().width) / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this
				.getSize().height) / 2;
		this.setLocation(x, y);

	}

	public boolean init()
	{
		JPanel jPanel = getUI(rootPath);
		if (null == jPanel)
		{
			LoggerUtil.log(Level.WARNING, "can not create UI");
			return false;
		}
		this.add(jPanel, BorderLayout.CENTER);
		return true;
	}

	private JPanel getUI(String rootPath)
	{
		// 获取机组信息
		stationBox = new JComboBox();
		File rootFile = new File(rootPath);
		if (!rootFile.exists())
		{
			LoggerUtil.log(Level.WARNING, "rootPath is not exist");
			return null;
		}

		File[] files = rootFile.listFiles();
		if (null == files)
		{
			LoggerUtil.log(Level.WARNING, "rootPath has not station records");
			return null;
		}
		boolean isok = false;
		for (File f : files)
		{
			if (f.getName().equals(OfflineHistorySearchPanel.filename))// 判断标志文件是否存在，以防错误选择文件夹
			{
				isok = true;
				continue;
			}
			stationBox.addItem(f.getName());
		}

		if (!isok)
		{
			LoggerUtil.log(Level.WARNING, "rootPath is err path");
			JOptionPane.showMessageDialog(this,
					HduChartUtil.getResource("Common_WrongPath"));
			return null;
		}
		// 填充
		workBox = new JComboBox();
		for (String s : workCases)
		{
			workBox.addItem(s);
		}

		// 确定和取消按键
		commitButton = new JButton(HduChartUtil.getResource("Common_Ensure"));
		commitButton.addActionListener(this);
		commitButton.setActionCommand(CMD_COMMIT);

		cancelButton = new JButton(HduChartUtil.getResource("Common_Cancle"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CMD_CANCEL);
		// UI设计
		JPanel boxPanel = new JPanel(new GridBagLayout());
		// GridBagUtil.addBlankJLabel(boxPanel, 0, 0, 1, 1);

		GridBagUtil.setLocation(boxPanel,
				new JLabel(HduChartUtil.getResource("Centre_Unit")), 0, 0, 1,
				1, true);
		GridBagUtil.setLocation(boxPanel, stationBox, 1, 0, 1, 1, true);
		// GridBagUtil.addBlankJLabel(boxPanel, 2, 0, 1, 1);
		GridBagUtil.setLocation(boxPanel,
				new JLabel(HduChartUtil.getResource("Centre_WorkType")), 2, 0,
				1, 1, true);
		GridBagUtil.setLocation(boxPanel, workBox, 3, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(boxPanel, 4, 0, 1, 1);

		JPanel datePanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(datePanel, 0, 0, 1, 1);
		GridBagUtil.setLocation(datePanel,
				new JLabel(HduChartUtil.getResource("Common_StartTime")), 1, 0,
				1, 1, true);
		GridBagUtil.setLocation(datePanel, startTimeButton, 2, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(datePanel, 3, 0, 1, 1);
		GridBagUtil.setLocation(datePanel,
				new JLabel(HduChartUtil.getResource("Common_EndTime")), 4, 0,
				1, 1, true);
		GridBagUtil.setLocation(datePanel, endTimeButton, 5, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(datePanel, 6, 0, 1, 1);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(buttonPanel, 0, 0, 20, 1);
		GridBagUtil.setLocation(buttonPanel, commitButton, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(buttonPanel, 2, 0, 1, 1);
		GridBagUtil.setLocation(buttonPanel, cancelButton, 3, 0, 1, 1, true);
		// GridBagUtil.addBlankJLabel(buttonPanel, 4, 0, 1, 1);

		JPanel mainJPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(mainJPanel, 0, 0, 1, 1);
		GridBagUtil.setLocation(mainJPanel, boxPanel, 0, 1, 1, 3, true);
		GridBagUtil.addBlankJLabel(mainJPanel, 0, 2, 1, 1);
		GridBagUtil.setLocation(mainJPanel, datePanel, 0, 3, 1, 3, true);

		GridBagUtil.addBlankJLabel(mainJPanel, 0, 4, 1, 1);
		GridBagUtil.setLocation(mainJPanel, buttonPanel, 0, 5, 1, 3, true);
		// GridBagUtil.addBlankJLabel(mainJPanel, 0, 5, 1, 1);
		return mainJPanel;

	}

	public static void main(String[] args)
	{
		// OriginWSSearchDialog dialog = new OriginWSSearchDialog(
		// "C:\\Users\\Administrator\\Desktop\\MODE\\Record")
		// {
		//
		// @Override
		// public void commitHandle(String station, String work,
		// Date startTime, Date endTime)
		// {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// };

		// dialog.setVisible(true);

	}

	public abstract void commitHandle(String station, String work,
			Date startTime, Date endTime);

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String cmdString = e.getActionCommand();

		if (CMD_COMMIT == cmdString)
		{
			commitHandle((String) stationBox.getSelectedItem(),
					(String) workBox.getSelectedItem(),
					startTimeButton.getDate(), endTimeButton.getDate());
			this.setVisible(false);
		} else if (CMD_CANCEL == cmdString)
		{
			this.setVisible(false);
		}
	}

}
