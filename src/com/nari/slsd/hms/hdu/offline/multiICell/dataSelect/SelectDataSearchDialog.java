package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

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
import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.SelectWSSearchDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.offline.multiICell.historySearch.DateChooserJButton;

public abstract class SelectDataSearchDialog extends JFrame implements
		ActionListener
{

	private static String CMD_COMMIT = "确认";
	private static String CMD_CANCEL = "取消";

	private static String CMD_STATION = "Station";
	private static String CMD_EXPER = "EXPER";

	private static String KEY_INDEX = "键相";

	private JButton commitButton;
	private JButton cancelButton;

	private JComboBox stationBox;// 站点选择
	private JComboBox experBox;// 实验选择

	private String rootpath;

	// 输入根文件，就是站点的上一层
	public SelectDataSearchDialog(String path)
	{
		super();

		String[] strings = path.split(OfflineDataSelectJPanel.KEYNAME);
		this.rootpath = strings[0];

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

		this.setVisible(true);
		return true;

	}

	private Vector<File> vstationFiles = new Vector<File>();
	private Vector<File> vexperFiles = new Vector<File>();

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

		return true;
	}

	private JPanel getUI()
	{
		// 获取机组信息
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

		// 确定和取消按键
		commitButton = new JButton("确定");
		commitButton.addActionListener(this);
		commitButton.setActionCommand(CMD_COMMIT);

		cancelButton = new JButton("取消");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CMD_CANCEL);
		// UI设计

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(buttonPanel, 0, 0, 20, 1);
		GridBagUtil.setLocation(buttonPanel, commitButton, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(buttonPanel, 2, 0, 1, 1);
		GridBagUtil.setLocation(buttonPanel, cancelButton, 3, 0, 1, 1, true);
		// GridBagUtil.addBlankJLabel(buttonPanel, 4, 0, 1, 1);

		JPanel mainJPanel = new JPanel(new GridBagLayout());
		
		GridBagUtil.addBlankJLabel(mainJPanel, 0, 0, 3, 1);
		
		GridBagUtil.addBlankJLabel(mainJPanel, 0, 1, 1, 1);
		GridBagUtil.setLocation(mainJPanel, stationBox, 1, 1, 1, 1, true);
		GridBagUtil.addBlankJLabel(mainJPanel, 3, 1, 1, 1);
		
		GridBagUtil.setLocation(mainJPanel, experBox, 1, 2, 1, 1, true);
		

		GridBagUtil.addBlankJLabel(mainJPanel, 0, 3, 1, 1);
		
		GridBagUtil
				.setLocation(mainJPanel, buttonPanel, 0, 4, 1, 1, 3, 1, true);
		// GridBagUtil.addBlankJLabel(mainJPanel, 0, 5, 1, 1);
		return mainJPanel;

	}

	public static void main(String[] args)
	{
//		SelectDataSearchDialog dialog = new SelectDataSearchDialog(
//				"C:\\Users\\Administrator\\Desktop\\南瑞后期\\Record\\key.info")
//		{
//
//			@Override
//			public void commitHandle(String stationtype, String experType,
//					String wavetype)
//			{
//				// TODO Auto-generated method stub
//
//			}
//
//		};
//
//		dialog.init();

	}

	public abstract void commitHandle(String experPath);
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String cmdString = e.getActionCommand();

		if (CMD_COMMIT == cmdString)
		{
			commitHandle(vexperFiles.get(experBox.getSelectedIndex())
					.getAbsolutePath());
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

		}
	}
}
