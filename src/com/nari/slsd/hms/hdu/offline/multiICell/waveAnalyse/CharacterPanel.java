/**
 * 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Created :2014-11-28 下午9:33:42 Describe :显示特征值的panel Class :
 * CharacterPanel.java Designed by:YXQ
 * 
 * 
 */
public class CharacterPanel extends JPanel {
	// protected static ResourceBundle res = ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);
	private String nowPosition;
	public static String positionX = ""+" ";
	public static String positionY = "";
	private String startTimeLab;
	private String endTimeLab;
	private String smpRateLab;
	private String startPointLab;
	private String endPointLab;
	private String maxValueLab;
	private String minValueLab;
	private String meanValueLab;
	private String midValueLab;
	private String validValueLab;
	private String peakValueLab;

	String startTime;
	String endTime;
	public String meanValue;
	String validValue;
	String midValue;
	public float[] maxAndMinAndPeak;
	int startPoint;
	public int endPoint;

	public static JTable table;
	public JScrollPane jScrollPane;

	public static DecimalFormat df = new DecimalFormat("######0.00");// 保留两位有效数字

	public CharacterPanel() {
		setBackground(Color.white);
		inition();
		guiDesign();
	}

	private void inition() {
		String[] columnNames = {
				HduChartUtil.getResource("OfflineAnalyse_Name"),
				HduChartUtil.getResource("OfflineAnalyse_Value") };


		nowPosition = new String(
				HduChartUtil.getResource("OfflineAnalyse_NowPosition"));
		startTimeLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_StartTime"));
		endTimeLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_EndTime"));
		smpRateLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_SamRate"));
		startPointLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_StartPoint"));
		endPointLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_EndPoint"));
		maxValueLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_MaxValue"));
		minValueLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_Minvalue"));
		meanValueLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_MeanValue"));
		midValueLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_MidValue"));
		validValueLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_ValidValue"));
		peakValueLab = new String(
				HduChartUtil.getResource("OfflineAnalyse_PeakValue"));

		Object[][] cellData = { { nowPosition, positionX + positionX },
				{ startTimeLab, "" }, { endTimeLab, "" }, { smpRateLab, "" },
				{ startPointLab, "" }, { endPointLab, "" },
				{ maxValueLab, "" }, { minValueLab, "" }, { meanValueLab, "" },
				{ midValueLab, "" }, { validValueLab, "" },
				{ peakValueLab, "" } };
		table = new JTable(cellData, columnNames);
	}

	private void guiDesign() {
		setBackground(Color.white);
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
		table.setDefaultRenderer(Object.class, render);
		// jTable.setVisible(true);
		jScrollPane = new JScrollPane(table);
		jScrollPane.setBackground(Color.white);
		jScrollPane.setPreferredSize(new Dimension(200, 600));
		TableColumn firsetColumn = table.getColumnModel().getColumn(0);
		firsetColumn.setPreferredWidth(25);
		// add(jScrollPane);
		jScrollPane.setVisible(true);
		add(jScrollPane);
		// add(holePanel);

	}

	public static void setCheckPoint(float xValue, float yValue) {
		positionX = "X:" + df.format(xValue) + " ";
		positionY = "Y:" + df.format(yValue);
		table.setValueAt(positionX + positionY, 0, 1);
		// positionX.setText("X:" + String.format("%3f", xValue) + "  ");
		// positionY.setText("Y:" + String.format("%3f", yValue));
	}

	public static void setCheckPointX(float xValue) {
		positionX = "X:" + String.valueOf(df.format(xValue)) + " ";
		table.setValueAt(positionX + positionY, 0, 1);
		// positionX.setText(String.valueOf(xValue) + "  ");
	}

	public static void setCheckPointY(float yValue) {
		positionY = "Y:" + String.valueOf(df.format(yValue));
		table.setValueAt(positionX + positionY, 0, 1);
		// positionY.setText(String.valueOf(yValue));
	}

	// 显示参数
	public void getValue(int start, float input[], CmtrCfgInfo cmtrCfgInfo) {
		float[] data = input.clone();

		startTime = df.format(start / cmtrCfgInfo.smprateRate);
		endTime = df.format((start + data.length) / cmtrCfgInfo.smprateRate);
		startPoint = start;
		endPoint = start + data.length;
		maxAndMinAndPeak = Calculate.CalWavePeak(data);
		midValue = df.format(Calculate.MID(data));
		validValue = df.format(Calculate.RMS(data));
		meanValue = df.format(Calculate.getAve(data));

		// float startTime,endTime,meanValue,midValue,validValue;
		// float[] maxAndMinAndPeak;
		// int startPoint,endPoint;

		table.setValueAt(startTime, 1, 1);
		table.setValueAt(endTime, 2, 1);
		table.setValueAt(cmtrCfgInfo.smprateRate, 3, 1);
		table.setValueAt(startPoint, 4, 1);
		table.setValueAt(endPoint, 5, 1);
		table.setValueAt(df.format(maxAndMinAndPeak[0]), 6, 1);
		table.setValueAt(df.format(maxAndMinAndPeak[1]), 7, 1);
		table.setValueAt(meanValue, 8, 1);
		table.setValueAt(midValue, 9, 1);
		table.setValueAt(validValue, 10, 1);
		table.setValueAt(df.format(maxAndMinAndPeak[2]), 11, 1);

	}
}
