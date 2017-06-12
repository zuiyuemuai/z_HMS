package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线分拣数据信息确认界面 界面中出现要分拣段的信息和分拣存放的位置
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class SelectCheckDialog extends JDialog implements ActionListener
{
	// protected static ResourceBundle res = ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);

	final private String CancelCommand = "cancel";
	final private String CommitCommand = "commit";

	private String rootpath;// 根文件目录
	JFileChooser fileChooser;
	Vector<SelectSection> sections;

	public SelectCheckDialog(Vector<SelectSection> sections, String path)
	{
		this.rootpath = path;
		this.sections = sections;

		if (!this.sections.get(this.sections.size()-1).isOk)
		{
			this.sections.remove(this.sections.size()-1);
		}

		this.setSize(500, 500);
		this.setLayout(new BorderLayout());
		JButton cancelButton = new JButton(
				HduChartUtil
						.getResource("OfflineDataSelect_CheckDialog_Cancle"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(CancelCommand);
		JButton commitButton = new JButton(
				HduChartUtil
						.getResource("OfflineDataSelect_CheckDialog_Ensure"));
		commitButton.addActionListener(this);
		commitButton.setActionCommand(CommitCommand);

		JTable table = new JTable();
		JScrollPane scrollPane = getJScrollPane(table, sections);

		String pathString = ConfigUtil
				.getPropertiesValue(ConfigUtil.KEY_SELECT_SAVEPATH);

		if (null == pathString)// 读取配置文件，如果没有则默认桌面
		{
			pathString = FileSystemView.getFileSystemView().getHomeDirectory()
					.getAbsolutePath();
		}

		fileChooser = new JFileChooser(pathString);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setControlButtonsAreShown(false);// 确认和取消不显示
		JPanel panel = new JPanel(new BorderLayout());
		JPanel buttonJPanel = new JPanel(new GridBagLayout());
		GridBagUtil.addBlankJLabel(buttonJPanel, 0, 0, 1, 1);
		GridBagUtil.setLocation(buttonJPanel, commitButton, 1, 0, 1, 1, true);
		GridBagUtil.setLocation(buttonJPanel, cancelButton, 2, 0, 1, 1, true);
		panel.add(buttonJPanel, BorderLayout.SOUTH);

		panel.add(scrollPane, BorderLayout.NORTH);
		panel.add(fileChooser, BorderLayout.CENTER);
		this.add(panel, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String command = e.getActionCommand();

		if (command.equals(CancelCommand))
		{
			setVisible(false);

		} else if (command.equals(CommitCommand))
		{
			setVisible(false);
			String destpath;// 目的目录
			if (null == fileChooser.getSelectedFile())
			{
				destpath = fileChooser.getCurrentDirectory().getAbsolutePath();
			} else
			{
				destpath = fileChooser.getSelectedFile().getAbsolutePath();
			}

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(ConfigUtil.KEY_SELECT_SAVEPATH, destpath);
			ConfigUtil.updateProperties(map);// 更新配置文件

			//创建key文件
			File keyFile = new File(destpath + "\\"+OfflineDataSelectJPanel.KEYNAME);
			if(!keyFile.exists())
			{
				try
				{
					keyFile.createNewFile();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			new SelectHandleThread(sections, destpath, rootpath).start();
		}

	}

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
				t.add(i + 1 + "");
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

		return jscrollPane;
	}

}
