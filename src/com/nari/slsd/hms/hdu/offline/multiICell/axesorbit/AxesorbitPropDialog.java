package com.nari.slsd.hms.hdu.offline.multiICell.axesorbit;

import java.util.Vector;

import javax.swing.JComboBox;

import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.PropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 离线轴心轨迹界面的输入资源选择对话框 用于选择站点,实验名等信息
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class AxesorbitPropDialog extends PropDialog
{

	public AxesorbitPropDialog(Vector<WorkSpaceProp> workSpaceProps)
	{
		super(workSpaceProps, 514, 164);
	}

	JComboBox pathBox;// 站点名称
	JComboBox nameBox;// 实验名称
	JComboBox sectionBox;// 区间选择
	JComboBox refBox;// 参考通道选择
	JComboBox dataBox;// 数据通道选择

	@Override
	protected void JcomBoxsInit()
	{
		// TODO Auto-generated method stub

		pathBox = new JComboBox();
		nameBox = new JComboBox();
		sectionBox = new JComboBox();
		refBox = new JComboBox();
		dataBox = new JComboBox();

		jComboBoxs.add(pathBox);
		jComboBoxs.add(nameBox);
		jComboBoxs.add(sectionBox);
		jComboBoxs.add(dataBox);
		jComboBoxs.add(refBox);

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
		refBox.removeAllItems();
		dataBox.removeAllItems();

		for (int i = 0; i < workSpaceProps.get(id).sectionName.size(); i++)
		{
			sectionBox.addItem(workSpaceProps.get(id).sectionName.get(i));
		}

		for (int i = 0; i < workSpaceProps.get(id).allWaveCfgInfos.get(0).channelName.length; i++)
		{
			String prop = workSpaceProps.get(id).allWaveCfgInfos.get(0).channelName[i];
			refBox.addItem(prop);
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
		item = new String[] { HduChartUtil.getResource("OfflinePropDialog_StationName"),
				HduChartUtil.getResource("OfflinePropDialog_ExperimentalType"),
				HduChartUtil.getResource("OfflinePropDialog_SectionType"),
				HduChartUtil.getResource("OfflinePropDialog_XAxisSignal"),
				HduChartUtil.getResource("OfflinePropDialog_YAxisSignal") };
	}

}
