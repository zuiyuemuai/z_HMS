/**
 * 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.Complex;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Created :2014-11-30 下午8:50:44 
 * Describe :显示频谱图主频 
 * Class : BasicFrePanel.java
 * Created by：YXQ
 * 
 * 
 */
public class BasicFrePanel extends JPanel {
	
//	protected static ResourceBundle res = ResourceBundleWrapper
//	.getBundle(PropertiesPATH.LocalizationBundle);
	private JPanel holePanel;
	private String numLab;
	private String freLab;
	private String amplitudeLab;
	private String phaseLab;

	private String firstLab;
	public String firFreTxt;
	public String firAmpTxt;
	public String firPhaseTxt;

	private String secondLab;
	public String secFreTxt;
	public String secAmpTxt;
	public String secPhaseTxt;

	private String thirdLab;
	public String thiFreTxt;
	public String thiAmpTxt;
	public String thiPhaseTxt;

	private String forthLab;
	public String forFreTxt;
	public String forAmpTxt;
	public String forPhaseTxt;

	private String fifthLab;
	public String fifFreTxt;
	public String fifAmpTxt;
	public String fifPhaseTxt;

	private String sixthLab;
	public String sixFreTxt;
	public String sixAmpTxt;
	public String sixPhaseTxt;

	private String seventhLab;
	public String sevFreTxt;
	public String sevAmpTxt;
	public String sevPhaseTxt;
	
	public JTable table;
	public JScrollPane jScrollPane;

	public BasicFrePanel() {
		setBackground(Color.white);
		inition();
		guiDesign();
	}

	private void inition() {
		holePanel = new JPanel(new GridLayout(8, 4));
		holePanel.setBackground(Color.white);
		numLab = new String(HduChartUtil.getResource("Common_Num"));
		freLab = new String(HduChartUtil.getResource("Common_Rate"));
		amplitudeLab = new String(HduChartUtil.getResource("Common_Amplitude"));
		phaseLab = new String(HduChartUtil.getResource("Common_Phase"));

		firstLab = new String("[1]");
		firFreTxt = new String();
		firAmpTxt = new String();
		firPhaseTxt = new String();

		secondLab = new String("[2]");
		secFreTxt = new String();
		secAmpTxt = new String();
		secPhaseTxt = new String();

		thirdLab = new String("[3]");
		thiFreTxt = new String();
		thiAmpTxt = new String();
		thiPhaseTxt = new String();

		forthLab = new String("[4]");
		forFreTxt = new String();
		forAmpTxt = new String();
		forPhaseTxt = new String();

		fifthLab = new String("[5]");
		fifFreTxt = new String();
		fifAmpTxt = new String();
		fifPhaseTxt = new String();

		sixthLab = new String("[6]");
		sixFreTxt = new String();
		sixAmpTxt = new String();
		sixPhaseTxt = new String();

		seventhLab = new String("[7]");
		sevFreTxt = new String();
		sevAmpTxt = new String();
		sevPhaseTxt = new String();
		
		String[] columnNames = {numLab,freLab,amplitudeLab,phaseLab};
		String[][] cellData = {{"[1]","","",""},{"[2]","","",""},{"[3]","","",""},{"[4]","","",""},{"[5]","","",""},{"[6]","","",""},{"[7]","","",""}};
		table = new JTable(cellData, columnNames);
	}

	private void guiDesign() {
//		holePanel.add(numLab);
//		holePanel.add(freLab);
//		holePanel.add(amplitudeLab);
//		holePanel.add(phaseLab);
//
//		holePanel.add(firstLab);
//		holePanel.add(firFreTxt);
//		holePanel.add(firAmpTxt);
//		holePanel.add(firPhaseTxt);
//
//		holePanel.add(secondLab);
//		holePanel.add(secFreTxt);
//		holePanel.add(secAmpTxt);
//		holePanel.add(secPhaseTxt);
//
//		holePanel.add(thirdLab);
//		holePanel.add(thiFreTxt);
//		holePanel.add(thiAmpTxt);
//		holePanel.add(thiPhaseTxt);
//
//		holePanel.add(forthLab);
//		holePanel.add(forFreTxt);
//		holePanel.add(forAmpTxt);
//		holePanel.add(forPhaseTxt);
//
//		holePanel.add(fifthLab);
//		holePanel.add(fifFreTxt);
//		holePanel.add(fifAmpTxt);
//		holePanel.add(fifPhaseTxt);
//
//		holePanel.add(sixthLab);
//		holePanel.add(sixFreTxt);
//		holePanel.add(sixAmpTxt);
//		holePanel.add(sixPhaseTxt);
//
//		holePanel.add(seventhLab);
//		holePanel.add(sevFreTxt);
//		holePanel.add(sevAmpTxt);
//		holePanel.add(sevPhaseTxt);
		setBackground(Color.white);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
		table.setDefaultRenderer(Object.class, render);
		// jTable.setVisible(true);
		jScrollPane = new JScrollPane(table);
		jScrollPane.setBackground(Color.white);
		jScrollPane.setPreferredSize(new Dimension(200, 600));
		// add(jScrollPane);
		jScrollPane.setVisible(true);
		add(jScrollPane);

//		add(holePanel);
	}

	/**
	 * @author YXQ
	 * @param planeXY 波形数据
	 * @param frier 傅里叶变换后的数据
	 * @return 返回空，实现设置值
	 */
	public void setValue(PlaneXY planeXY,Complex frier[]) {

		float[] xValue = planeXY.getX().clone();
		float[] yValue = planeXY.getY().clone();
		int len = yValue.length;
		Calculate.orderMintoMax(yValue);
		int first = Calculate.findValue(planeXY.getY(), yValue[len - 1]);
		int second = Calculate.findValue(planeXY.getY(), yValue[len - 2]);
		int third = Calculate.findValue(planeXY.getY(), yValue[len - 3]);
		int forth = Calculate.findValue(planeXY.getY(), yValue[len - 4]);
		int fifth = Calculate.findValue(planeXY.getY(), yValue[len - 5]);
		int sixth = Calculate.findValue(planeXY.getY(), yValue[len - 6]);
		int seventh = Calculate.findValue(planeXY.getY(), yValue[len - 7]);

		DecimalFormat df1 = new DecimalFormat("######0.00");// 保留2位有效数字
		DecimalFormat df2 = new DecimalFormat("######0.000");// 保留3位有效数字
		
		table.setValueAt(df2.format(xValue[first]), 0, 1);
		table.setValueAt(df1.format(yValue[len - 1]), 0, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[first])), 0, 3);
		
		table.setValueAt(df2.format(xValue[second]), 1, 1);
		table.setValueAt(df1.format(yValue[len - 2]), 1, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[second])), 1, 3);
		
		table.setValueAt(df2.format(xValue[third]), 2, 1);
		table.setValueAt(df1.format(yValue[len - 3]), 2, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[third])), 2, 3);
		
		table.setValueAt(df2.format(xValue[forth]), 3, 1);
		table.setValueAt(df1.format(yValue[len - 4]), 3, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[forth])), 3, 3);
		
		table.setValueAt(df2.format(xValue[fifth]), 4, 1);
		table.setValueAt(df1.format(yValue[len - 5]), 4, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[fifth])), 4, 3);
		
		table.setValueAt(df2.format(xValue[sixth]), 5, 1);
		table.setValueAt(df1.format(yValue[len - 6]), 5, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[sixth])), 5, 3);
		
		table.setValueAt(df2.format(xValue[seventh]), 6, 1);
		table.setValueAt(df1.format(yValue[len - 7]), 6, 2);
		table.setValueAt(df1.format(Calculate.getPhase(frier[seventh])), 6, 3);
//		
//		firFreTxt.setText(String.valueOf(xValue[first]));
//		firAmpTxt.setText(String.valueOf(yValue[len - 1]));
//		firPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[first])));
//
//		secFreTxt.setText(String.valueOf(xValue[second]));
//		secAmpTxt.setText(String.valueOf(yValue[len - 2]));
//		secPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[second])));
//
//		thiFreTxt.setText(String.valueOf(xValue[third]));
//		thiAmpTxt.setText(String.valueOf(yValue[len - 3]));
//		thiPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[third])));
//
//		forFreTxt.setText(String.valueOf(xValue[forth]));
//		forAmpTxt.setText(String.valueOf(yValue[len - 4]));
//		forPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[forth])));
//
//		fifFreTxt.setText(String.valueOf(xValue[fifth]));
//		fifAmpTxt.setText(String.valueOf(yValue[len - 5]));
//		fifPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[fifth])));
//
//		sixFreTxt.setText(String.valueOf(xValue[sixth]));
//		sixAmpTxt.setText(String.valueOf(yValue[len - 6]));
//		sixPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[sixth])));
//
//		sevFreTxt.setText(String.valueOf(xValue[seventh]));
//		sevAmpTxt.setText(String.valueOf(yValue[len - 7]));
//		sevPhaseTxt.setText(String.valueOf(Calculate.getPhase(frier[seventh])));
	}
}
