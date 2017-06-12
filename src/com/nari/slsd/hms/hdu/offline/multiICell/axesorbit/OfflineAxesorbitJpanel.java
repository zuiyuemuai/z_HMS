package com.nari.slsd.hms.hdu.offline.multiICell.axesorbit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import javax.swing.JButton;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;

import sun.net.www.content.image.jpeg;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.AxesOrbitChart;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.OffLineICellInterface;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;


/**
 * 南瑞水电站监护系统离线轴心轨迹界面
 * 界面中 左侧是输入两个通道的数据波形显示，右侧是两个通道的波形合成，轴心轨迹图。
 * 拖动其中一个联动显示其他部分。
 * @author 	LYNN
 * @version 1.0,14/12/24
 * @since 	JDK1.625
 */
public class OfflineAxesorbitJpanel extends JPanel implements
		OffLineICellInterface, ActionListener
{
	private Font titlefont = new Font("微软雅黑", Font.BOLD, 18);
	private Font textfont = new Font("微软雅黑", Font.ITALIC, 12);
	
	/**两个波形显示的chart**/
	LineChartPanel lineCharts[] = new LineChartPanel[2];
	/**主容器**/
	JPanel mainJpanel;
	/**显示轴心轨迹的chart**/
	AxesOrbitChart sectionChart;
	/**输出word报表功能**/
	JButton creatWordBtn = new JButton(HduChartUtil.getResource("Common_CreatWord"));

	/**需要显示的数据**/
	float[] xdata;
	float[] ydata;
	String namex;
	String namey;
	String unitx;
	String unity;
	
	private static final String CMD_OutWord = "Common_PrintOutWord"; 
	/**
	 * 创建一个轴心轨迹显示界面
	 * @param x 输入的x通道数据
	 * @param y 输入的y通道数据
	 */
	public OfflineAxesorbitJpanel(float[] x, float[]y, String namex, String namey,String unitx,String unity)
	{
		xdata = x;
		ydata = y;
		this.namex =namex;
		this.namey =namey;
		this.unitx = unitx;
		this.unity = unity;
		init();
	}

	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		this.setLayout(new BorderLayout());
		this.add(getMainJPanel(), BorderLayout.CENTER);

		sectionChart.display(xdata, ydata,
				true);
		sectionChart.setXLabel(unitx);//单位
		sectionChart.setYLabel(unity);

		float index[] = new float[xdata.length];
		for (int i = 0; i < index.length; i++)
		{
			index[i] = (float) i;
		}

		PlaneXY coordinateXY = new PlaneXY(index, xdata);
		lineCharts[0].upAutSeriesData(namex, coordinateXY, ExtColor.getLineColor());
		lineCharts[0].setYLable(unitx);

		coordinateXY = new PlaneXY(index, ydata);
		lineCharts[1].upAutSeriesData(namey, coordinateXY, ExtColor.getLineColor());
		lineCharts[1].setYLable(unity);
		
		

		creatWordBtn.addActionListener(creatWordListener);
		this.addComponentListener(sectionChart.getSizeListener());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String cmdString = e.getActionCommand();
		if(cmdString.equals(CMD_OutWord))
		{
			//这里添加输出报表
			creatWord();
		}
	}
	
	private JPanel getMainJPanel()
	{
		JPanel mainJPanel = new JPanel(new GridBagLayout());

		JPanel titleJPanel = new JPanel(new GridBagLayout());
		titleJPanel.setBackground(Color.white);
		
		GridBagUtil.addBlankJLabel(titleJPanel, 0, 0, 12, 1);
		JLabel title = new JLabel(HduChartUtil.getResource("offlineAxesorbit_Title"));
		title.setFont(titlefont);
		GridBagUtil.setLocation(titleJPanel, title, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(titleJPanel, 2, 0, 12, 1);
		
		JButton outButton = new JButton(HduChartUtil.getResource("Common_PrintOutWord"));
		outButton.setActionCommand(CMD_OutWord);
		outButton.addActionListener(this);
		GridBagUtil.setLocation(titleJPanel, outButton, 3, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(titleJPanel, 4, 0, 2, 1);
		
		
		GridBagUtil.setLocation(mainJPanel, titleJPanel, 0, 0, 1, 1,2,1, true);
		
		for (int i = 0; i < 2; i++)
		{
			lineCharts[i] = new LineChartPanel(" ", Color.gray);
			lineCharts[i]
					.addAxisChangedListener(new LineChartAxisChangeListener(i));
			lineCharts[i].setMouseDragOperation_X();
			lineCharts[i].setBorder(BorderFactory.createLineBorder(Color.gray));
			GridBagUtil.setLocation(mainJPanel, lineCharts[i], 0, i+1, 10, 10,
					true);
		}

		sectionChart = new AxesOrbitChart(null, Color.gray,
				ExtColor.getLineColor(), 1, false);
		sectionChart.setCloseLableXandY();
		
		JPanel jPanel = new JPanel(new GridBagLayout());
		jPanel.setBackground(Color.white);
		jPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		GridBagUtil.addBlankJLabel(jPanel, 0, 0, 4, 1);
		GridBagUtil.setLocation(jPanel, sectionChart.getPanel(), 0, 1, 15, 10, true);
		
		
		GridBagUtil.setLocation(mainJPanel, jPanel, 1, 1, 5,
				20, 1, 2, true);
		
		
	//	GridBagUtil.setLocation(mainJPanel, creatWordBtn, 1, 0, 1, 1, true);

		this.mainJpanel = mainJPanel;
		return mainJPanel;
	}

	public void sectionChartDisplay(float[] xList, float[] yList, int min,
			int max)
	{
		float newxList[] = new float[max - min];
		float newyList[] = new float[max - min];

		System.arraycopy(xList, min, newxList, 0, max - min);
		System.arraycopy(yList, min, newyList, 0, max - min);

		sectionChart.display(newxList, newyList, false);
	}
	
	
	public ActionListener creatWordListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			creatWord();
		}
	};
	
	private void creatWord()
	{
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil.getResource("Common_ChooseSavePath"));
		   jFileChooser.setSelectedFile(new  
		   File(HduChartUtil.getResource("offlineAxesorbit_Word"))); 
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
		
		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel",savePath,"axesorbitModel.ftl") {
			
			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				dataMap.put("image", ImageChange.getImageEncode(mainJpanel));
				dataMap.put("xchannel", namex);
				dataMap.put("ychannel", namey);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month", String.valueOf(now.get(Calendar.MONTH)+1));
				dataMap.put("date", String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}

	/**
	 * 同步
	 * 
	 * @param id
	 *            其他的一个同步id
	 */
	private void syn(int id)
	{
		Map<String, Float> range = lineCharts[id].getXAxisRange();
		// 另外一个同步
		int newid = (id == 0 ? 1 : 0);

		if (lineCharts[newid].isSameXAxis(range.get(LineChartPanel.XAxisLower),
				range.get(LineChartPanel.XAxisUpper)))
		{
			return;
		}

		lineCharts[newid].setXaxis(range.get(LineChartPanel.XAxisLower),
				range.get(LineChartPanel.XAxisUpper));

		sectionChartDisplay(xdata, ydata,
				(int) (float) range.get(LineChartPanel.XAxisLower),
				(int) (float) range.get(LineChartPanel.XAxisUpper));
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

	@Override
	public void close()
	{
		// TODO Auto-generated method stub

	}




}
