package com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse;

import java.util.Vector;

import javax.swing.JComboBox;

import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.PropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;
/**
 * 动平衡分析时，选择通道的窗口
 * @author YXQ
 * @version 1.0,14/11/25
 * @since JDK1.625
 */
public abstract class SelectDataDialog extends PropDialog
{

	public SelectDataDialog(Vector<WorkSpaceProp> workSpaceProps)
	{
		super(workSpaceProps, 500, 300);
		// TODO Auto-generated constructor stub
	}

	JComboBox pathBox;
	JComboBox nameBox;
	JComboBox sectionBox;
	JComboBox dataBox;

	@Override
	protected void JcomBoxsInit()
	{
		// TODO Auto-generated method stub

		pathBox = new JComboBox();
		nameBox = new JComboBox();
		sectionBox = new JComboBox();
		jComboBoxs.add(pathBox);
		jComboBoxs.add(nameBox);
		jComboBoxs.add(sectionBox);

		dataBox = new JComboBox();
		jComboBoxs.add(dataBox);

		for (int i = 0; i < workSpaceProps.size(); i++)
		{
			int j;
			for (j = 0; j < i; j++)// 防止两个相同的名称
			{
				if (workSpaceProps.get(i).stationName.equals(workSpaceProps
						.get(j).stationName))
				{
					break;
				}
			}
			if (j == i)
			{
				pathBox.addItem(workSpaceProps.get(i).stationName);
			}
		}

		for (int i = 0; i < workSpaceProps.size(); i++)
		{
			if (workSpaceProps.get(i).stationName
					.equals(workSpaceProps.get(0).stationName))
			{
				nameBox.addItem(workSpaceProps.get(i).testName);
			}
		}

		addItem(0);

	}

	// 第几个资源填入数据
	void addItem(int id)
	{

		sectionBox.removeAllItems();

		dataBox.removeAllItems();

		for (int i = 0; i < workSpaceProps.get(id).sectionName.size(); i++)
		{
			sectionBox.addItem(workSpaceProps.get(id).sectionName.get(i));
		}

		for (int i = 0; i < workSpaceProps.get(id).allWaveCfgInfos.get(0).channelName.length; i++)
		{
			String prop = workSpaceProps.get(id).allWaveCfgInfos.get(0).channelName[i];

			dataBox.addItem(prop);

		}

	}

	@Override
	protected void JcomBoxsSelectHandle(JComboBox choise)
	{
		// TODO Auto-generated method stub
		if (choise == pathBox)// 如果改变了站点的话
		{
			nameBox.removeAllItems();
			for (int i = 0; i < workSpaceProps.size(); i++)
			{
				if (workSpaceProps.get(i).stationName.equals(workSpaceProps
						.get(choise.getSelectedIndex()).stationName))
				{
					table.setValueAt(workSpaceProps.get(i).testName, 1, 1);
					nameBox.addItem(workSpaceProps.get(i).testName);
					nameBox.setSelectedItem(workSpaceProps.get(i).testName);
				}
			}
			String stationString = (String) choise.getSelectedItem();
			int id = 0;
			for (int i = 0; i < workSpaceProps.size(); i++)
			{
				if (stationString.equals(workSpaceProps.get(i).stationName))
				{
					id = i;
				}
			}

			addItem(id);
			table.setValueAt(
					workSpaceProps.get(choise.getSelectedIndex()).sectionName
							.get(0), 2, 1);
			nameBox.setSelectedItem(workSpaceProps.get(choise
					.getSelectedIndex()).sectionName.get(0));

		} else if (choise == nameBox)// 如果改变了实验名称的话
		{
			for (int i = 0; i < workSpaceProps.size(); i++)
			{
				if (workSpaceProps.get(i).stationName.equals(pathBox
						.getSelectedObjects())
						&& workSpaceProps.get(i).testName.equals(nameBox
								.getSelectedObjects()))
				{
					addItem(i);
					table.setValueAt(workSpaceProps.get(choise
							.getSelectedIndex()).sectionName.get(0), 2, 1);
					nameBox.setSelectedItem(workSpaceProps.get(choise
							.getSelectedIndex()).sectionName.get(0));
					break;
				}
			}

		}

	}

	@Override
	protected void IntemInit()
	{
		// TODO Auto-generated method stub
		String stationName = HduChartUtil.getResource("OfflinePropDialog_StationName");
		String experimentalType = HduChartUtil.getResource("OfflinePropDialog_ExperimentalType");
		String sectionType = HduChartUtil.getResource("OfflinePropDialog_SectionType");
		String dataChannel = HduChartUtil.getResource("OfflinePropDialog_DataChannel");
		item = new String[] { stationName,experimentalType, sectionType, dataChannel };
	}

}
