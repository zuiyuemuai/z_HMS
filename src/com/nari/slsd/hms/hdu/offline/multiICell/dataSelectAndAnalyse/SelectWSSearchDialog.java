package com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.offline.multiICell.historySearch.DateChooserJButton;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

public abstract class SelectWSSearchDialog extends JFrame implements
		ActionListener
{

	private static String path = "WorkRecords.txt";
	private static String CMD_COMMIT = "确认";
	private static String CMD_CANCEL = "取消";

	private static String CMD_STATION = "Station";
	private static String CMD_EXPER = "EXPER";

	private static String KEY_INDEX = "键相";

	private DateChooserJButton startTimeButton = new DateChooserJButton();
	private DateChooserJButton endTimeButton = new DateChooserJButton();

	private JButton commitButton;
	private JButton cancelButton;

	private JComboBox wavetypeBox;// 波形选择
	private JComboBox stationBox;// 站点选择
	private JComboBox experBox;// 实验选择

	private String[] waveType = null;
	private Vector<File> vstationFiles = new Vector<File>();
	private Vector<File> vexperFiles = new Vector<File>();

	private String rootpath;

	protected WorkSpaceProp workSpaceProp;

	// 输入根文件，就是站点的上一层
	public SelectWSSearchDialog(WorkSpaceProp workSpaceProp, String path)
	{
		super();

		this.rootpath = path;

		this.workSpaceProp = workSpaceProp;

		this.setSize(400, 200);
		this.setLayout(new BorderLayout());

		int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this
				.getSize().width) / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this
				.getSize().height) / 2;
		this.setLocation(x, y);

	}

	public boolean init()
	{
		if (false == getALLTypes())
		{
			return false;
		}
		JPanel jPanel = getUI();
		if (null == jPanel)
		{
			LoggerUtil.log(Level.WARNING, "can not create UI");
			return false;
		}

		this.add(jPanel, BorderLayout.CENTER);

		return true;

	}

	/**
	 * 输入一个文件夹路径输出这个路径下所有文件夹名称至vector
	 * 
	 * @param v
	 *            输出
	 * @param f
	 *            输入文件夹
	 */

	private void getFiles(File f, Vector<File> v)
	{

		File[] Files = f.listFiles();
		v.clear();// 先清除
		for (int i = 0; i < Files.length; i++)
		{
			if (Files[i].isDirectory())
			{
				v.add(Files[i]);
			}
		}
	}

	private boolean getALLTypes()
	{

		File rootFile = new File(rootpath);

		getFiles(rootFile, vstationFiles);

		if (0 == vstationFiles.size())
		{
			return false;
		}

		getFiles(vstationFiles.get(0), vexperFiles);

		if (0 == vexperFiles.size())
		{
			return false;
		}

		if (!getWaveTypes(0, 0))
		{
			return false;
		}

		return true;
	}

	private boolean getWaveTypes(int stationindex, int experindex)
	{
		// 入参检测
		if (stationindex < 0 || experindex < 0
				|| stationindex >= vstationFiles.size()
				|| experindex >= vexperFiles.size())
			return false;

		Vector<String> filename = new Vector<String>();

		File file = new File(rootpath + "\\"
				+ vstationFiles.get(stationindex).getName() + "\\"
				+ vexperFiles.get(experindex).getName() + "\\1\\定时\\wave");
		File[] files = file.listFiles();
		if (null != files)
		{
			for (File f : files)
			{
				filename.add("定时_" + f.getName());
			}
		} else
		{
			return false;
		}
		file = new File(rootpath + "\\"
				+ vstationFiles.get(stationindex).getName() + "\\"
				+ vexperFiles.get(experindex).getName() + "\\1\\整周期\\wave");
		files = file.listFiles();
		if (null != files)
		{
			for (File f : files)
			{
				filename.add("整周期_" + f.getName());
			}
		} else
		{
			return false;
		}

		waveType = new String[filename.size()];
		for (int i = 0; i < filename.size(); i++)
		{
			waveType[i] = filename.get(i);
		}
		return true;
	}

	private JPanel getUI()
	{
		// 获取机组信息
		wavetypeBox = new JComboBox();
		stationBox = new JComboBox();
		experBox = new JComboBox();

		stationBox.addActionListener(this);
		stationBox.setActionCommand(CMD_STATION);
		experBox.addActionListener(this);
		experBox.setActionCommand(CMD_EXPER);

		stationBox.removeAllItems();
		for (File f : vstationFiles)
		{
			stationBox.addItem(f.getName());
		}

		experBox.removeAllItems();
		for (File f : vexperFiles)
		{
			experBox.addItem(f.getName());
		}

		wavetypeBox.removeAllItems();
		if (null != waveType)
		{
			for (String s : waveType)
			{
				if (s.equals(KEY_INDEX))
				{
					continue;
				}
				wavetypeBox.addItem(s);
			}

		}

		// 确定和取消按键
		commitButton = new JButton(HduChartUtil.getResource("Common_Ensure"));
		commitButton.addActionListener(this);
		commitButton.setActionCommand(CMD_COMMIT);

		cancelButton = new JButton(HduChartUtil.getResource("Common_Cancle"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CMD_CANCEL);
		// UI设计

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(buttonPanel, 0, 0, 20, 1);
		GridBagUtil.setLocation(buttonPanel, commitButton, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(buttonPanel, 2, 0, 1, 1);
		GridBagUtil.setLocation(buttonPanel, cancelButton, 3, 0, 1, 1, true);

		JPanel mainJPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(mainJPanel, 0, 0, 1, 1);
		GridBagUtil.addBlankJLabel(mainJPanel, 5, 0, 1, 1);
		
		GridBagUtil.setLocation(mainJPanel, new JLabel(HduChartUtil.getResource("Centre_Station")),
				1, 1, 1, 3, true);
		GridBagUtil.setLocation(mainJPanel, stationBox, 3, 1, 1, 3, true);
		GridBagUtil.addBlankJLabel(mainJPanel, 1, 2, 1, 1);
		
		GridBagUtil.setLocation(mainJPanel, new JLabel(HduChartUtil.getResource("Centre_Exper")),
				1, 3, 1, 3, true);
		GridBagUtil.setLocation(mainJPanel, experBox, 3, 3, 1, 3, true);
		GridBagUtil.addBlankJLabel(mainJPanel, 1, 4, 1, 1);
		
		GridBagUtil.setLocation(mainJPanel, new JLabel(HduChartUtil.getResource("Centre_Channel")),
				1, 5, 1, 3, true);
		GridBagUtil.setLocation(mainJPanel, wavetypeBox, 3, 5, 1, 3, true);
		
		GridBagUtil.addBlankJLabel(mainJPanel, 1, 6, 1, 1);
		
		
		GridBagUtil
				.setLocation(mainJPanel, buttonPanel, 0, 7, 1, 3, 6, 1, true);
	
		GridBagUtil.addBlankJLabel(mainJPanel, 0, 8, 1, 1);
		
		return mainJPanel;

	}

	public static void main(String[] args)
	{
		// SelectWSSearchDialog dialog = new SelectWSSearchDialog()
		// {
		//
		// @Override
		// public void commitHandle(String station)
		// {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// };
		//
		// dialog.setVisible(true);

	}

	public abstract void commitHandle(String stationtype, String experType,
			String wavetype);

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String cmdString = e.getActionCommand();

		if (CMD_COMMIT == cmdString)
		{
			commitHandle((String) stationBox.getSelectedItem(),
					(String) experBox.getSelectedItem(),
					(String) wavetypeBox.getSelectedItem());
			this.setVisible(false);
		} else if (CMD_CANCEL == cmdString)
		{
			this.setVisible(false);
		} else if (CMD_STATION == cmdString)
		{
			if (-1 == stationBox.getSelectedIndex())
				return;
			// System.out.println(stationBox.getSelectedIndex());
			getFiles(vstationFiles.get(stationBox.getSelectedIndex()),
					vexperFiles);

			experBox.removeAllItems();
			for (File f : vexperFiles)
			{
				experBox.addItem(f.getName());
			}

			getWaveTypes(stationBox.getSelectedIndex(), 0);
			wavetypeBox.removeAllItems();
			if (null != waveType)
			{
				for (String s : waveType)
				{
					if (s.equals(KEY_INDEX))
					{
						continue;
					}
					wavetypeBox.addItem(s);
				}

			}
		} else if (CMD_EXPER == cmdString)
		{
			if (-1 == stationBox.getSelectedIndex()
					|| -1 == experBox.getSelectedIndex())
				return;
			getWaveTypes(stationBox.getSelectedIndex(),
					experBox.getSelectedIndex());

			wavetypeBox.removeAllItems();
			if (null != waveType)
			{
				for (String s : waveType)
				{
					if (s.equals(KEY_INDEX))
					{
						continue;
					}
					wavetypeBox.addItem(s);
				}

			}
		}
	}
}
