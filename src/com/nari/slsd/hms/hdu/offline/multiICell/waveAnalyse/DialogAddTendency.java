/**
 * 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.ui.RefineryUtilities;

import com.nari.slsd.hms.hdu.common.algorithm.WindowFilter;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;


/**
 * Created :2014-10-30 ����10:16:15 
 * Describe : 增加趋势通道
 * Class : AddTendencyFrame.java 
 * Designed by:YXQ
 * 
 * 
 */
public class DialogAddTendency extends JFrame {
//	protected static ResourceBundle res = ResourceBundleWrapper
//	.getBundle(PropertiesPATH.LocalizationBundle);
	private Box holeBox;
//	private JPanel panelTotal;
	private JPanel panelBase;
	private JLabel labBase;
	private Choice chooseBase;
	private JPanel panelWindow1;
	private JLabel labWindowWidth;
	private JTextField txtWindowWidth;
	private JPanel panelWindow2;
	private JLabel labWindowStep;
	private JTextField txtWindowStep;
	private JPanel panelColor;
	private JLabel labColor;
	private Choice chooseColor;
	private JPanel panelButton;
	private JButton btnSure;
	private JButton btnCancel;
	private OriginalChartPanel originalChart;
	private float[] input;
	
	private String colorBlack = HduChartUtil.getResource("Color_black");
	private String colorRed = HduChartUtil.getResource("Color_red");
	private String colorBlue = HduChartUtil.getResource("Color_blue");
	private String colorYellow = HduChartUtil.getResource("Color_yellow");
	private String colorGreen = HduChartUtil.getResource("Color_green");
	private String colorCyan = HduChartUtil.getResource("Color_cyan");
	private String colorPink = HduChartUtil.getResource("Color_pink");
	private String colorGrey = HduChartUtil.getResource("Color_grey");

	public DialogAddTendency(OriginalChartPanel originalChart) {
		super(HduChartUtil.getResource("OfflineAnalyse_AddTendency"));
		setSize(250, 260);
		RefineryUtilities.centerFrameOnScreen(this);
		this.originalChart = originalChart;
		// for(int i=0;i<input.length;i++)
		// {
		// this.input[i]=input[i];
		// }
		inition();
		guiDesign();
	}

	public void inition() {
//		panelTotal = new JPanel(new GridLayout(5, 1));
		holeBox = Box.createVerticalBox();
		panelBase = new JPanel();
		labBase = new JLabel(HduChartUtil.getResource("OfflineAnalyse_ComputerDepend"));
		chooseBase = new Choice();
//		chooseBase.setSize(20, 10);
		chooseBase.add(HduChartUtil.getResource("OfflineAnalyse_MeanValue"));
		chooseBase.add(HduChartUtil.getResource("OfflineAnalyse_MidValue"));
		chooseBase.add(HduChartUtil.getResource("OfflineAnalyse_ValidValue"));
		chooseBase.add(HduChartUtil.getResource("OfflineAnalyse_PeakValue"));
		panelWindow1 = new JPanel(new FlowLayout());
		labWindowWidth = new JLabel(HduChartUtil.getResource("OfflineAnalyse_WindowWidth"));
		txtWindowWidth = new JTextField("10");
		panelWindow2 = new JPanel(new FlowLayout());
		labWindowStep = new JLabel(HduChartUtil.getResource("OfflineAnalyse_WindowStep"));
		txtWindowStep = new JTextField("1");

		panelColor = new JPanel(new FlowLayout());
		labColor = new JLabel(HduChartUtil.getResource("Choose_line_color"));
		chooseColor = new Choice();

		chooseColor.addItem(colorBlack);
		chooseColor.addItem(colorRed);
		chooseColor.addItem(colorBlue);
		chooseColor.addItem(colorYellow);
		chooseColor.addItem(colorGreen);
		chooseColor.addItem(colorCyan);
		chooseColor.addItem(colorPink);
		chooseColor.addItem(colorGrey);
		panelButton = new JPanel(new FlowLayout());
		btnSure = new JButton(HduChartUtil.getResource("Common_Ensure"));
		btnSure.addActionListener(sureBtnListener);
		btnCancel = new JButton(HduChartUtil.getResource("Common_Cancle"));
	}

	private void guiDesign() {
		panelBase.add(labBase);
		panelBase.add(chooseBase);
		holeBox.add(panelBase);

		panelWindow1.add(labWindowWidth);
		panelWindow1.add(txtWindowWidth);
		panelWindow2.add(labWindowStep);
		panelWindow2.add(txtWindowStep);
		holeBox.add(panelWindow1);
		holeBox.add(panelWindow2);

		panelColor.add(labColor);
		panelColor.add(chooseColor);
		holeBox.add(panelColor);

		panelButton.add(btnSure);
		panelButton.add(btnCancel);
		holeBox.add(panelButton);

		add(holeBox);
	}

	private ActionListener sureBtnListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String baseType = chooseBase.getSelectedItem();
			int width = Integer.parseInt(txtWindowWidth.getText());
			int step = Integer.parseInt(txtWindowStep.getText());
			Color color = colorChoose();
			PlaneXY outplaneXY = new PlaneXY();
			float[] output = null;
			float[] outputX;
			if (baseType.equals(HduChartUtil.getResource("OfflineAnalyse_MeanValue"))) {
				output = WindowFilter.averageFilter(originalChart.planeXY.getY(),
						step, width);
			} else if (baseType.equals(HduChartUtil.getResource("OfflineAnalyse_MidValue"))) {
				output = WindowFilter.middleFilter(originalChart.planeXY.getY(),
						step, width);
			} else if (baseType.equals(HduChartUtil.getResource("OfflineAnalyse_PeakValue"))) {
				output = WindowFilter.peakFilter(originalChart.planeXY.getY(), step,
						width);
			} else if (baseType.equals(HduChartUtil.getResource("OfflineAnalyse_ValidValue"))) {
				output = WindowFilter.usefulValueFilter(
						originalChart.planeXY.getY(), step, width);
			}

			outputX = new float[output.length];
			for (int j = 0; j < output.length; j++) {
				outputX[j] = j*step;
			}
			outplaneXY.setX(outputX);
			outplaneXY.setY(output);
			originalChart.upAutSeriesData(baseType, outplaneXY,  color);
			// originalChart.creatOriginalCharts(outputX, output);
			setVisible(false);
		}
	};

	private Color colorChoose() {
		Color color = Color.red;
		String colorChoosed = chooseColor.getSelectedItem();
		if (colorChoosed.equals(colorBlack)) {
			color = Color.black;
		} else if (colorChoosed.equals(colorRed)) {
			color = Color.red;
		} else if (colorChoosed.equals(colorGreen)) {
			color = Color.green;
		} else if (colorChoosed.equals(colorBlue)) {
			color = Color.blue;
		} else if (colorChoosed.equals(colorCyan)) {
			color = Color.cyan;
		} else if (colorChoosed.equals(colorYellow)) {
			color = Color.yellow;
		} else if (colorChoosed.equals(colorPink)) {
			color = Color.pink;
		} else if (colorChoosed.equals(colorGrey)) {
			color = Color.gray;
		}
		return color;

	}

}
