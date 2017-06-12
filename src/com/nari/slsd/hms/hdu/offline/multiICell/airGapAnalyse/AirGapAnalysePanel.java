package com.nari.slsd.hms.hdu.offline.multiICell.airGapAnalyse;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse.SelectDataDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.online.multiICell.airgap.ScopeAirGap;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

public class AirGapAnalysePanel extends JPanel implements ActionListener
{

	LineChartPanel[] lineChart = new LineChartPanel[4];
	JPanel showChartPanel;
	OfflineScopeAirGap airGapPanel;
	private Vector<WorkSpaceProp> workSpaceProps;
	int chartFlag;
	
	private final String IMPORT_FIRST_DATA_COMMAND = "IMPORT_FIRST_DATA";
	private final String IMPORT_SECOND_DATA_COMMAND = "IMPORT_SECOND_DATA";
	private final String IMPORT_THIRD_DATA_COMMAND = "IMPORT_THIRD_DATA";
	private final String IMPORT_FORTH_DATA_COMMAND = "IMPORT_FORTH_DATA";
	public AirGapAnalysePanel(Vector<WorkSpaceProp> workSpaceProps)
	{
		this.workSpaceProps = workSpaceProps;
		init();
	}
	public AirGapAnalysePanel()
	{
		init();
	}

	private void init()
	{
		this.setLayout(new GridBagLayout());
		JPopupMenu jPopupMenu = createPopupMenu();
		setComponentPopupMenu(jPopupMenu);
		for (int i = 0; i < lineChart.length; i++)
		{
			lineChart[i] = new LineChartPanel();
			lineChart[i].chartPanel.setPopupMenu(jPopupMenu);
			lineChart[i].chartPanel.setRangeZoomable(false);
			lineChart[i].setXLable("a");
			lineChart[i].setYLable("a");
			
			lineChart[i].addAxisChangedListener(new LineChartAxisChangeListener(i));
			GridBagUtil.setLocation(this, lineChart[i], 1, i, 1, 1, true);
			
		}
		airGapPanel = new OfflineScopeAirGap();
		airGapPanel.setComponentPopupMenu(jPopupMenu);
		
		GridBagUtil.setLocation(this, airGapPanel, 0, 0, 1, 1, 1, 4, true);

//		showChartPanel = new JPanel(new GridLayout(2, 1));
//
//		showChartPanel.add(lineChart1);
//		showChartPanel.add(lineChart2);
		
//		add(showChartPanel);
//		add(airGapPanel);

	}
	private void syn(int id)
	{
		Map<String, Float> range = lineChart[id].getXAxisRange();
		// 另外三个同步
		for(int i=0;i<id ;i++){
			if (lineChart[i].isSameXAxis(range.get(LineChartPanel.XAxisLower),
					range.get(LineChartPanel.XAxisUpper)))
			{
				return;
			}

			lineChart[i].setXaxis(range.get(LineChartPanel.XAxisLower),
					range.get(LineChartPanel.XAxisUpper));
		}
		for(int j=id+1;j<4;j++){
			if (lineChart[j].isSameXAxis(range.get(LineChartPanel.XAxisLower),
					range.get(LineChartPanel.XAxisUpper)))
			{
				return;
			}

			lineChart[j].setXaxis(range.get(LineChartPanel.XAxisLower),
					range.get(LineChartPanel.XAxisUpper));
		}

		
//
//		sectionChartDisplay(xdata, ydata,
//				(int) (float) range.get(LineChartPanel.XAxisLower),
//				(int) (float) range.get(LineChartPanel.XAxisUpper));
	}

	/**
	 * 用于监听波形chart的范围变化
	 * @author LNYY
	 */
	class LineChartAxisChangeListener implements AxisChangeListener
	{
		int id = 0;// 表示是哪个chart

		public LineChartAxisChangeListener(int id)
		{
			this.id = id;
		}

		@Override
		public void axisChanged(AxisChangeEvent arg0)
		{
			// TODO Auto-generated method stub
			syn(id);
		}

	}
	
	private JPopupMenu createPopupMenu()
	{
		JPopupMenu jpopupMenu = new JPopupMenu();
		
		JMenuItem inputFiData = new JMenuItem(HduChartUtil.getResource("Common_Import_FirstData"));
		inputFiData.setActionCommand(IMPORT_FIRST_DATA_COMMAND);
		inputFiData.addActionListener(this);
		jpopupMenu.add(inputFiData);
		
		JMenuItem inputSeData = new JMenuItem(HduChartUtil.getResource("Common_Import_SecondData"));
		inputSeData.setActionCommand(IMPORT_SECOND_DATA_COMMAND);
		inputSeData.addActionListener(this);
		jpopupMenu.add(inputSeData);
		
		JMenuItem inputThData = new JMenuItem(HduChartUtil.getResource("Common_Import_ThirdData"));
		inputThData.setActionCommand(IMPORT_THIRD_DATA_COMMAND);
		inputThData.addActionListener(this);
		jpopupMenu.add(inputThData);
		
		JMenuItem inputFoData = new JMenuItem(HduChartUtil.getResource("Common_Import_ForthData"));
		inputFoData.setActionCommand(IMPORT_FORTH_DATA_COMMAND);
		inputFoData.addActionListener(this);
		jpopupMenu.add(inputFoData);
		
		return jpopupMenu;
	}
	
	private void doImportBeforeData(int num)
	{
		chartFlag = num;
		SelectDataDialog importDialog = new SelectDataDialog(workSpaceProps) {
			
			@Override
			public void CommitHandle(JTable table) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void CommitHandle(Vector<JComboBox> boxs) {
				// TODO Auto-generated method stub
				WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(workSpaceProps, (String) boxs.get(0).getSelectedItem(), (String) boxs.get(1).getSelectedItem());
				String section = (String) boxs.get(2).getSelectedItem();
//				frequency1 = workSpace.allWaveCfgInfos.get(workSpace
//						.getIndexFromSections(section)).smprateRate;
				String name = (String) boxs.get(3).getSelectedItem();
				String unit = workSpace.allWaveCfgInfos.get(workSpace
						.getIndexFromSections(section)).getAnalogs().get(workSpace.getChannelIndex(section, name)).getUnit();//获得单位
				PlaneXY planeXY = new PlaneXY();
				float[] xdata;
				float[] ydata;
				// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
				ydata = workSpace.getWaveData(section, name);
				xdata = new float[ydata.length];
				for (int i = 0; i < ydata.length; i++) {
					xdata[i] = i;
				}
				planeXY.setX(xdata);
				planeXY.setY(ydata);
				lineChart[chartFlag].upAutSeriesData(name, planeXY, ExtColor.getLineColor());
				lineChart[chartFlag].setYLable(unit);
			}
		};
		
		importDialog.setVisible(true);
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		JFrame frame = new JFrame();
		frame.setSize(800, 400);
		AirGapAnalysePanel airGapAnalysePanel = new AirGapAnalysePanel();
		frame.add(airGapAnalysePanel);
	//	frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String command = e.getActionCommand();
		
		if(command.equals(IMPORT_FIRST_DATA_COMMAND)){
			doImportBeforeData(0);
		}else if(command.equals(IMPORT_SECOND_DATA_COMMAND)){
			doImportBeforeData(1);
		}else if(command.equals(IMPORT_THIRD_DATA_COMMAND)){
			doImportBeforeData(2);
		}else if(command.equals(IMPORT_FORTH_DATA_COMMAND)){
			doImportBeforeData(3);
		}
		
	}

}
