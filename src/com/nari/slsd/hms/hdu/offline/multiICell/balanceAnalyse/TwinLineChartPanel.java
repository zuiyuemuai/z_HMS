package com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.axesorbit.OfflineAxesorbitJpanel;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelect.MouseHandle;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;

public class TwinLineChartPanel extends JPanel {

	private final String BEFORE_DATA_COMMAND = "BEFORE_DATA";
	private final String AFTER_DATA_COMMAND = "AFTER_DATA";
	private final String CANCEL_BEFORE_DATA_COMMAND = "CANCEL_BEFORE_DATA";
	private final String CANCEL_AFTER_DATA_COMMAND = "CANCEL_AFTER_DATA";
	LineChartPanel beforeHeavyChart;
	LineChartPanel afterHeavyChart;
	Vector<WorkSpaceProp> workSpaceProp;
	
	String beforeChannelName;
	String afterChannelName;
	BalanceMouseHandle beforeMouseHandle;
	BalanceMouseHandle afterMouseHandle;
	JPopupMenu jPopupMenu;

	public TwinLineChartPanel(Vector<WorkSpaceProp> workSpaceProp) {
		this.workSpaceProp = workSpaceProp;
		init();
	}

	private void init() {
		setLayout(new GridLayout(2, 1));
		setSize(300,500);
		beforeHeavyChart = new LineChartPanel();
		afterHeavyChart = new LineChartPanel();

		add(beforeHeavyChart);
		add(afterHeavyChart);
		beforeHeavyChart.chartPanel.setMouseZoomable(false);
		afterHeavyChart.chartPanel.setMouseZoomable(false);
		setComponentPopupMenu(createPopupMenu());
	}
	
	private BalanceMouseHandle selectData(final LineChartPanel lineChartPanel){
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		java.util.Date startdata;
//		java.util.Date enddata;
//		try {
			
//			startdata = format.parse(workSpaceProp.allWaveCfgInfos.get(0).
//					getBeginTime());
//			enddata = format.parse(workSpaceProp.allWaveCfgInfos.get(0).getEndTime());
		BalanceMouseHandle mouseHandle;
			mouseHandle = new BalanceMouseHandle() {
				
				@Override
				protected LineChartPanel getLineChartPanel() {
					// TODO Auto-generated method stub
					return lineChartPanel;
				}

				@Override
				protected void operationSelectRange(PlaneXY planeXY) {
					// TODO Auto-generated method stub
					
				}
			};
			float[] beforePoint = getFirstTracePoint(lineChartPanel);
			mouseHandle.xline = beforePoint[0];
			mouseHandle.firstXline = beforePoint[0];
//			mouseHandle.yline = beforePoint[1];
			mouseHandle.setXline("first",beforePoint[0], Color.red);
			mouseHandle.setYline(beforePoint[0],beforePoint[1],Color.red);
			lineChartPanel.addMouseListener(mouseHandle);
			lineChartPanel.chartPanel.addMouseListener(mouseHandle);
			lineChartPanel.chartPanel.setFocusable(true);
			return mouseHandle;
//			lineChartPanel.addKeyListener(mouseHandle);
//		} catch (Exception e) {
			// TODO: handle exception
//		}
		
	}

	public JPopupMenu createPopupMenu() {
		JPopupMenu jPopupMenu = new JPopupMenu();

		JMenuItem beforeDataItem = new JMenuItem("加载试重前的数据");
		beforeDataItem.setActionCommand(BEFORE_DATA_COMMAND);
		beforeDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(beforeDataItem);

		JMenuItem afterDataItem = new JMenuItem("加载试重后的数据");
		afterDataItem.setActionCommand(AFTER_DATA_COMMAND);
		afterDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(afterDataItem);

		JMenuItem cancelBeforeDataItem = new JMenuItem("取消配重前周期选择");
		cancelBeforeDataItem.setActionCommand(CANCEL_BEFORE_DATA_COMMAND);
		cancelBeforeDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(cancelBeforeDataItem);

		JMenuItem cancelAfterDataItem = new JMenuItem("取消配重后周期选择");
		cancelAfterDataItem.setActionCommand(CANCEL_AFTER_DATA_COMMAND);
		cancelAfterDataItem.addActionListener(popupMenuListener);
		jPopupMenu.add(cancelAfterDataItem);
		
		beforeHeavyChart.chartPanel.setPopupMenu(jPopupMenu);
		afterHeavyChart.chartPanel.setPopupMenu(jPopupMenu);
		setComponentPopupMenu(jPopupMenu);
		return jPopupMenu;

	}

	public void doImportBeforeData() {
		SelectDataDialog beforeHeavyDialog = new SelectDataDialog(
				workSpaceProp) {

			@Override
			public void CommitHandle(JTable table) {
				// TODO Auto-generated method stub
			}

			@Override
			public void CommitHandle(Vector<JComboBox> boxs) {
				// TODO Auto-generated method stub
				WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(workSpaceProp, (String) boxs.get(0).getSelectedItem(), (String) boxs.get(1).getSelectedItem());
				String section = (String) boxs.get(2).getSelectedItem();
//				for(int i=0;i<5;i++)
//				{
//					System.out.println(boxs.get(i).getSelectedItem());
//				}
				beforeChannelName = (String) boxs.get(3).getSelectedItem();
				PlaneXY planeXY = new PlaneXY();
				float[] xdata;
				float[] ydata;
				// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
				ydata = workSpace.getWaveData(section, beforeChannelName);
				xdata = new float[ydata.length];
			
				for (int i = 0; i < ydata.length; i++) {
					xdata[i] = i;
				}
				planeXY.setX(xdata);
				planeXY.setY(ydata);
				beforeHeavyChart.upAutSeriesData(beforeChannelName, planeXY, Color.blue);
				beforeHeavyChart.setTitle((String) boxs.get(1).getSelectedItem()+","+section+":"+beforeChannelName);
				beforeHeavyChart.setPlanexy(planeXY);
//				beforeMouseHandle=selectData(beforeHeavyChart);
			}
		};
		
		beforeHeavyDialog.setVisible(true);
		
	}

	private void doImportAfterData() {
		SelectDataDialog afterHeavyDialog = new SelectDataDialog(
				workSpaceProp) {


			@Override
			public void CommitHandle(Vector<JComboBox> boxs) {
				// TODO Auto-generated method stub
				WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(workSpaceProp, (String) boxs.get(0).getSelectedItem(), (String) boxs.get(1).getSelectedItem());
				String section = (String) boxs.get(2).getSelectedItem();
				afterChannelName = (String) boxs.get(3).getSelectedItem();
				PlaneXY planeXY = new PlaneXY();
				float[] xdata;
				float[] ydata;
				// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
				ydata = workSpace.getWaveData(section, afterChannelName);
				xdata = new float[ydata.length];
			
				for (int i = 0; i < ydata.length; i++) {
					xdata[i] = i;
				}
				planeXY.setX(xdata);
				planeXY.setY(ydata);
				afterHeavyChart.upAutSeriesData(afterChannelName, planeXY, Color.blue);
				afterHeavyChart.setTitle((String) boxs.get(1).getSelectedItem()+","+section+":"+afterChannelName);
				afterHeavyChart.setPlanexy(planeXY);
//				afterMouseHandle = selectData(afterHeavyChart);
			}
			

			@Override
			public void CommitHandle(JTable table) {
				// TODO Auto-generated method stub
			}
		};
		afterHeavyDialog.setVisible(true);
	
	}
	
	
	/**
	 * Describe :获取浮点检测轨迹线的一个点，在该点的基础上画出横纵轨迹，该点最好存在与曲线上
	 * 所以获取的方法，便是在这段曲线数据中随机产生一个数 入口参数： 返回值：Point，返回一个屏幕坐标点
	 */
	private float[] getFirstTracePoint(LineChartPanel linechart)
	{
		float[] point = new float[2];
		XYPlot plot = (XYPlot)linechart. chart.getPlot();
		ValueAxis d = plot.getDomainAxis();
		float lowRange = (float) d.getLowerBound();
		float upRange = (float) d.getUpperBound();
		point[0] =  (float) (Math.random() * ((upRange - lowRange)/2 + 1) + lowRange);
		point[1] = linechart. planeXY.getY()[(int) point[0]];
		return point;
	}

	private ActionListener popupMenuListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String command = e.getActionCommand();
			System.out.println(command);
			if (command.equals(BEFORE_DATA_COMMAND)) {
				doImportBeforeData();
			} else if (command.equals(AFTER_DATA_COMMAND)) {
				doImportAfterData();
			} else if (command.equals(CANCEL_BEFORE_DATA_COMMAND)) {
				beforeHeavyChart.chartPanel.restoreAutoBounds();
//				beforeHeavyChart.deleteSeries("first");
				beforeHeavyChart.deleteSeries("second");
				beforeMouseHandle.xline = beforeMouseHandle.firstXline;
				beforeMouseHandle.ifdoubleClick = false;
			
			} else if (command.equals(CANCEL_AFTER_DATA_COMMAND)) {
				afterHeavyChart.chartPanel.restoreAutoBounds();
//				beforeHeavyChart.deleteSeries("first");
				afterHeavyChart.deleteSeries("second");
				afterMouseHandle.xline = afterMouseHandle.firstXline;
				afterMouseHandle.ifdoubleClick = false;
				
			}
		}
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame();
		// TwinLineChartPanel twinLineChartPanel = new TwinLineChartPanel();
		// frame.add(twinLineChartPanel);
		frame.setSize(400, 500);
		frame.setVisible(true);
	}

}
