package com.nari.slsd.hms.hdu.offline.multiICell.magneticBalance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.Complex;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.iCell.PolarChartPanel;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse.BalanceAnalyse;
import com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse.BalanceMouseHandle;
import com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse.SelectDataDialog;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;
/**
 * 磁拉力平衡分析
 * @author YXQ
 * @version 1.0,14/12/25
 * @since JDK1.625
 */
public class MagneticBalance extends BalanceAnalyse {
	
	
	/** 加载试重前数据命令 */
	private final String BEFORE_DATA_COMMAND = "BEFORE_DATA";
	
	/** 取消加载试重前数据周期选择命令 */
	private final String CANCEL_BEFORE_DATA_COMMAND = "CANCEL_BEFORE_DATA";
	
	/** 图片另存为 */
	private final String SAVE_IMAGE_COMMAND = "SAVE_IMAGE";
	
	/** 输出word文档 */
	private final String CREAT_WORD_COMMAND = "CREAT_WORD";
	
	private Vector<WorkSpaceProp> workSpaceProps;
	private LineChartPanel lineChartPanel;
	private PolarChartPanel polarChartPanel;
	private JPanel showChartPanel;
	private DataTable dataTable;
	private BalanceMouseHandle mouseHandle;
	private PlaneXY jianxiang;
	private Complex[] valueFFT;
	private HduCreatWord hduCreatWord;
	private float frequency;
	private JLabel zhouqiNum = null;
	private JButton creatWordBtn;
	String selectedNum = HduChartUtil.getResource("OfflineMagnetic_SecNum");
	
	public MagneticBalance(Vector<WorkSpaceProp> workSpaceProp){
		setWorkSpace(workSpaceProp);
	}
	
//	public void MagneticBalance1(Vector<WorkSpaceProp> workSpaceProp){
//		setWorkSpace(workSpaceProp);
//	}
	
	protected void setWorkSpace(Vector<WorkSpaceProp> workSpaceProp){
		this.workSpaceProps = workSpaceProp;
		intion();
	}
	
	public JPanel getPanelSelf(){
		return this;
	}
	
	private void intion(){
		setLayout(new BorderLayout());
		lineChartPanel = new LineChartPanel();
		polarChartPanel = new PolarChartPanel();
		
		showChartPanel = new JPanel(new GridLayout(1,2));
		dataTable = new DataTable();
		zhouqiNum = new JLabel(selectedNum);
		creatWordBtn = new JButton(HduChartUtil.getResource("Common_CreatWord"));
		creatWordBtn.setActionCommand(CREAT_WORD_COMMAND);
		creatWordBtn.addActionListener(popupMenuListener);
		lineChartPanel.chartPanel.setMouseZoomable(false);
		
		//以下是GUI设计部分
		JPanel polarAndTablePanel = new JPanel(new BorderLayout());
		polarAndTablePanel.add(polarChartPanel);
		polarAndTablePanel.add(dataTable,BorderLayout.SOUTH);
		
		showChartPanel.add(lineChartPanel);
		showChartPanel.add(polarAndTablePanel);
		
		add(showChartPanel);
	//	add(dataTable,BorderLayout.SOUTH);
		JPanel messPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 10));
		messPanel.setBackground(Color.white);
		messPanel.add(zhouqiNum);
		messPanel.add(creatWordBtn);
		add(messPanel,BorderLayout.NORTH);
		JPopupMenu jPopupMenu = createPopupMenu();
		setComponentPopupMenu(jPopupMenu);
	}
	
	//导入加重前数据
		private void doImportData() {
			SelectDataDialog beforeHeavyDialog = new SelectDataDialog(
					workSpaceProps) {

				@Override
				public void CommitHandle(JTable table) {
					// TODO Auto-generated method stub
				}

				@Override
				public void CommitHandle(Vector<JComboBox> boxs) {
					// TODO Auto-generated method stub
					WorkSpaceProp workSpace = WorkSpaceProp.getWorkSpace(workSpaceProps, (String) boxs.get(0).getSelectedItem(), (String) boxs.get(1).getSelectedItem());
					String section = (String) boxs.get(2).getSelectedItem();
					frequency =  workSpace.allWaveCfgInfos.get(workSpace
							.getIndexFromSections(section)).smprateRate;
					
					String beforeChannelName = (String) boxs.get(3).getSelectedItem();
					PlaneXY planeXY = new PlaneXY();
					float[] xdata;
					float[] ydata;
					String unit = workSpace.allWaveCfgInfos.get(workSpace
							.getIndexFromSections(section)).getAnalogs().get(workSpace.getChannelIndex(section, beforeChannelName)).getUnit();
					// xdata = workSpaceProp.getWaveData(section, gallerys[0]);
					ydata = workSpace.getWaveData(section, beforeChannelName);
					xdata = new float[ydata.length];
				
					for (int i = 0; i < ydata.length; i++) {
						xdata[i] = i;
					}
					planeXY.setX(xdata);
					planeXY.setY(ydata);
					lineChartPanel.upAutSeriesData(beforeChannelName, planeXY, Color.blue);
					lineChartPanel.setTitle((String) boxs.get(1).getSelectedItem()+","+section+":"+beforeChannelName);
					lineChartPanel.setPlanexy(planeXY);
					lineChartPanel.setYLable(unit);
					jianxiang = showJianxiang(lineChartPanel,ydata,workSpace,section);
//					jianxiang = showJianxiang(lineChartPanel,ydata,workSpace,(int) frequency);
					valueFFT = Zhengfft(ydata,frequency);
					mouseHandle=selectData(lineChartPanel,1);
				}
			};
			beforeHeavyDialog.setVisible(true);
		}
		
		// 创建鼠标监听器，数据选择
		private BalanceMouseHandle selectData(final LineChartPanel lineChartPanel,final int chartNum){
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
						//获得选择的数据，待处理
						float speedFre = 0;
						zhouqiNum.setText(selectedNum+String.valueOf(planeXY.getX().length));
						inputDataToTable(0,2,Calculate.findMaxMin(planeXY.getY())[1],dataTable.jTable);
							speedFre = getSpeed(planeXY,jianxiang.getX(),frequency);
							findSignal(speedFre, valueFFT, chartNum , dataTable.jTable,polarChartPanel);
							drawCircle();
					}

					
				};
				float[] beforePoint = getFirstTracePoint(lineChartPanel);
				mouseHandle.xline = beforePoint[0];
				mouseHandle.firstXline = beforePoint[0];
//				mouseHandle.yline = beforePoint[1];
				mouseHandle.setXline("first",beforePoint[0], Color.red);
				mouseHandle.setYline(beforePoint[0],beforePoint[1],Color.red);
				lineChartPanel.addMouseListener(mouseHandle);
				lineChartPanel.chartPanel.addMouseListener(mouseHandle);
				lineChartPanel.chartPanel.setFocusable(true);
				return mouseHandle;
//				lineChartPanel.addKeyListener(mouseHandle);
//			} catch (Exception e) {
				// TODO: handle exception
//			}
			
		}
		
		//极坐标上画圈
		protected void drawCircle(){
			Object radio1 =  dataTable.jTable.getValueAt(0, 3);
			String circleName="";
			float r1 = 0;
			if(radio1 =="" ){
				return;
			}
			if(radio1 !=""){
				r1 = Float.parseFloat(radio1.toString());
				circleName = "r1";
				drawCircle(circleName,r1);
			}
			
		}
		
		//画圈
		protected void drawCircle(String name,float radio){
			PlaneXY planeXY = new PlaneXY();
			float[] angle=new float[(int) (360/0.5)];
			float[] amp=new float[(int) (360/0.5)];
			for(int i=0;i<360/0.5;i++){
				angle[i] = (float) (0.5*i);
				amp[i] = radio;
			}
			planeXY.setX(angle);
			planeXY.setY(amp);
			polarChartPanel.upAutSeriesData(name, planeXY, Color.RED);
		}

		
		//取消周期数据选择
		private void deleteChoice(){

				lineChartPanel.chartPanel.restoreAutoBounds();
				lineChartPanel.deleteSeries("second");
				lineChartPanel.deleteSeries("up");
				lineChartPanel.deleteSeries("down");
				polarChartPanel.deleteSeries("1");
				polarChartPanel.deleteSeries("r1");
				drawCircle();
				dataTable.clearRowData(0,dataTable.jTable);
				mouseHandle.xline = mouseHandle.firstXline;
				mouseHandle.ifdoubleClick = false;
		
		}
		
		
		//创建word报表
		private void creatWord(){
			String savePath = "";
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
			jFileChooser.setDialogTitle(HduChartUtil.getResource("Common_ChooseSavePath"));
			   jFileChooser.setSelectedFile(new  
			   File(HduChartUtil.getResource("OfflineMagnetic_Word"))); 
			jFileChooser.setMultiSelectionEnabled(false);
			int returnVal = jFileChooser.showSaveDialog(jFileChooser);
			if (returnVal != JFileChooser.APPROVE_OPTION )//判断对话框是否选择“取消”
			{
				savePath = null;
				return;
			}
			else
			{
				savePath = jFileChooser.getSelectedFile().getPath()+".doc";
			}
			hduCreatWord = new HduCreatWord("//wordModel",savePath,"magneticAnalyseModel.ftl") {
				
				@Override
				public void getData(Map<String, Object> dataMap) {
					// TODO Auto-generated method stub
					Calendar now = Calendar.getInstance();
				
					dataMap.put("image", ImageChange.getImageEncode(showChartPanel));
					dataMap.put("speedA", dataTable.jTable.getValueAt(0, 1));
					dataMap.put("vibrationA", dataTable.jTable.getValueAt(0, 2));
					dataMap.put("amplA", dataTable.jTable.getValueAt(0, 3));
					dataMap.put("phaseA", dataTable.jTable.getValueAt(0, 4));
					dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
					dataMap.put("month", String.valueOf(now.get(Calendar.MONTH)+1));
					dataMap.put("date", String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
				}
			};
			
		}
	
	//创建右键菜单
		public JPopupMenu createPopupMenu() {
			JPopupMenu jPopupMenu = new JPopupMenu();

			JMenuItem beforeDataItem = new JMenuItem(HduChartUtil.getResource("OfflineBalance_ImportBeforeData"));
			beforeDataItem.setActionCommand(BEFORE_DATA_COMMAND);
			beforeDataItem.addActionListener(popupMenuListener);
			jPopupMenu.add(beforeDataItem);
			jPopupMenu.addSeparator();

			JMenuItem cancelBeforeDataItem = new JMenuItem(HduChartUtil.getResource("OfflineBalance_CancelBeforeData"));
			cancelBeforeDataItem.setActionCommand(CANCEL_BEFORE_DATA_COMMAND);
			cancelBeforeDataItem.addActionListener(popupMenuListener);
			jPopupMenu.add(cancelBeforeDataItem);
			
			JMenuItem saveImage = new JMenuItem(HduChartUtil.getResource("Save_as..."));
			saveImage.setActionCommand(SAVE_IMAGE_COMMAND);
			saveImage.addActionListener(popupMenuListener);
			jPopupMenu.add(saveImage);
			
			JMenuItem creatWord = new JMenuItem(
					HduChartUtil.getResource("Common_CreatWord"));
			creatWord.setActionCommand(CREAT_WORD_COMMAND);
			creatWord.addActionListener(popupMenuListener);
			jPopupMenu.add(creatWord);

//			
			lineChartPanel.chartPanel.setPopupMenu(jPopupMenu);
			setComponentPopupMenu(jPopupMenu);
			return jPopupMenu;

		}
		
		//右键菜单响应
		private ActionListener popupMenuListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String command = e.getActionCommand();
				System.out.println(command);
				if (command.equals(BEFORE_DATA_COMMAND)) {
					doImportData();
				} else if (command.equals(CANCEL_BEFORE_DATA_COMMAND)) {
					deleteChoice();
				} else if(command.equals(SAVE_IMAGE_COMMAND)){
					saveImage(getPanelSelf());
				} else if(command.equals(CREAT_WORD_COMMAND)){
					creatWord();
				} 
			}
		};
		
		
		protected class DataTable extends JPanel
		{
			public JTable jTable;
			JScrollPane jScrollPane;
			public String speed = HduChartUtil.getResource("OfflineBalance_Speed");//转速
			public String vibration = HduChartUtil.getResource("OfflineBalance_TotalVibration");//总振值
			public String amplitude = HduChartUtil.getResource("OfflineBalance_TurnFre_Amplitude");//转频幅值
			public String phase = HduChartUtil.getResource("OfflineBalance_TurnFre_Phase");//转频相位
			String originalData = HduChartUtil.getResource("OfflineBalance_OriginalData");//原始数据
			
			public DataTable()
			{
				intion();
			}

			private void intion()
			{
				String[] columnNames = { "", speed, vibration, amplitude, phase};
				Object[][] cellData = { { originalData, "", "", "", "" } };

				jTable = new JTable(cellData, columnNames);
				// jTable.setDragEnabled(false);
				FitTableColumns(jTable);
				DefaultTableCellRenderer render = new DefaultTableCellRenderer();
				render.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
				jTable.setDefaultRenderer(Object.class, render);
				// jTable.setVisible(true);
				jScrollPane = new JScrollPane(jTable);
				jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				jScrollPane.setPreferredSize(new Dimension(450,90));
				add(jScrollPane);
				setVisible(true);
			}
			//清楚表格某一行数据
			public void clearRowData(int row , JTable jTable){
				int i=1;
				for(i=1;i<5;i++){
					jTable.setValueAt("", row, i);
				}
			}
			// 列宽自适应
			public void FitTableColumns(JTable myTable)
			{
				JTableHeader header = myTable.getTableHeader();
				int rowCount = myTable.getRowCount();

				Enumeration columns = myTable.getColumnModel().getColumns();
				while (columns.hasMoreElements())
				{
					TableColumn column = (TableColumn) columns.nextElement();
					int col = header.getColumnModel().getColumnIndex(
							column.getIdentifier());
					int width = (int) myTable
							.getTableHeader()
							.getDefaultRenderer()
							.getTableCellRendererComponent(myTable,
									column.getIdentifier(), false, false, -1, col)
							.getPreferredSize().getWidth();
					for (int row = 0; row < rowCount; row++)
					{
						int preferedWidth = (int) myTable
								.getCellRenderer(row, col)
								.getTableCellRendererComponent(myTable,
										myTable.getValueAt(row, col), false, false,
										row, col).getPreferredSize().getWidth();
						width = Math.max(width, preferedWidth);
					}
					header.setResizingColumn(column); // 此行很重要
					column.setWidth(width + myTable.getIntercellSpacing().width );
				}
			}

		}
}
