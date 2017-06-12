package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.PropDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;


/**
 * Created :2014-11-30 下午8:50:44 
 * Describe :增加通道曲线
 * Class : AddLineProDialog.java
 * @author YXQ
 * 
 * 
 */
public abstract class AddLineProDialog extends PropDialog {

//	private Vector<WorkSpaceProp> workSpaceProps;
	private LineChartPanel chart;
	public AddLineProDialog(Vector<WorkSpaceProp> workSpaceProps,LineChartPanel chart)
	{
		super(workSpaceProps, 300, 300);
//		this.workSpaceProps = workSpaceProps;
		this.chart = chart;
	}
//	
//	protected ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
	private String colorBlack = HduChartUtil.getResource("Color_black");
	private String colorRed = HduChartUtil.getResource("Color_red");
	private String colorBlue = HduChartUtil.getResource("Color_blue");
	private String colorYellow = HduChartUtil.getResource("Color_yellow");
	private String colorGreen = HduChartUtil.getResource("Color_green");
	private String colorCyan = HduChartUtil.getResource("Color_cyan");
	private String colorPink = HduChartUtil.getResource("Color_pink");
	private String colorGrey = HduChartUtil.getResource("Color_grey");
	
	JComboBox pathBox;
	JComboBox nameBox;
	JComboBox sectionBox;
	JComboBox dataBox;
	JComboBox colorBox;

	@Override
	protected void JcomBoxsInit() {
		// TODO Auto-generated method stub
		pathBox = new JComboBox();
		nameBox = new JComboBox();
		sectionBox = new JComboBox();
		colorBox = new JComboBox();
		jComboBoxs.add(pathBox);
		jComboBoxs.add(nameBox);
		jComboBoxs.add(sectionBox);
		

		dataBox = new JComboBox();
		jComboBoxs.add(dataBox);
		jComboBoxs.add(colorBox);
		
		

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
		
		colorBox.addItem(HduChartUtil.getResource("Color_black"));
		colorBox.addItem(HduChartUtil.getResource("Color_red"));
		colorBox.addItem(HduChartUtil.getResource("Color_blue"));
		colorBox.addItem(HduChartUtil.getResource("Color_green"));
		colorBox.addItem(HduChartUtil.getResource("Color_cyan"));
		colorBox.addItem(HduChartUtil.getResource("Color_yellow"));
		colorBox.addItem(HduChartUtil.getResource("Color_pink"));
		colorBox.addItem(HduChartUtil.getResource("Color_grey"));
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

		
		private Color colorChoose() {
			Color addLineColor = Color.red;
			String colorChoosed = (String) colorBox.getSelectedItem();
			if (colorChoosed.equals(colorBlack)) {
				addLineColor = Color.black;
			} else if (colorChoosed.equals(colorRed)) {
				addLineColor = Color.red;
			} else if (colorChoosed.equals(colorGreen)) {
				addLineColor = Color.green;
			} else if (colorChoosed.equals(colorBlue)) {
				addLineColor = Color.blue;
			} else if (colorChoosed.equals(colorCyan)) {
				addLineColor = Color.cyan;
			} else if (colorChoosed.equals(colorYellow)) {
				addLineColor = Color.yellow;
			} else if (colorChoosed.equals(colorPink)) {
				addLineColor = Color.pink;
			} else if (colorChoosed.equals(colorGrey)) {
				addLineColor = Color.gray;
			}
			return addLineColor;

		}
	@Override
	protected void JcomBoxsSelectHandle(JComboBox choise) {
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
	public void CommitHandle(Vector<JComboBox> boxs) {
		// TODO Auto-generated method stub
		WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(workSpaceProps, (String) boxs.get(0).getSelectedItem(), (String) boxs.get(1).getSelectedItem());
		String section = (String) boxs.get(2).getSelectedItem();
//		for(int i=0;i<5;i++)
//		{
//			System.out.println(boxs.get(i).getSelectedItem());
//		}
		String channelName = (String) boxs.get(3).getSelectedItem();
		PlaneXY planeXY = new PlaneXY();
		float[] xdata;
		float[] ydata;
		// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
		ydata = workSpace.getWaveData(section, channelName);
		xdata = new float[ydata.length];
	
		for (int i = 0; i < ydata.length; i++) {
			xdata[i] = i;
		}
		planeXY.setX(xdata);
		planeXY.setY(ydata);
		Color color = colorChoose();
		chart.upAutSeriesData(channelName, planeXY, color);
	}

	@Override
	public void CommitHandle(JTable table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void IntemInit() {
		// TODO Auto-generated method stub
		String stationName = HduChartUtil.getResource("OfflinePropDialog_StationName");
		String experimentalType = HduChartUtil.getResource("OfflinePropDialog_ExperimentalType");
		String sectionType = HduChartUtil.getResource("OfflinePropDialog_SectionType");
		String dataChannel = HduChartUtil.getResource("OfflinePropDialog_DataChannel");
		String color = HduChartUtil.getResource("Choose_line_color");
		item = new String[] { stationName, experimentalType, sectionType, dataChannel,color };
		
	}

}
