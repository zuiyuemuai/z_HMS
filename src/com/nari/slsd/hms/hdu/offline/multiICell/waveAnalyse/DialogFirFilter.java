/**
 * 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

import com.nari.slsd.hms.hdu.common.algorithm.FirFilter;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;


/**
 * Created :2014-10-15 ����7:09:54 
 * Describe :有限长滤波
 * Class : FilterFrame.java
 * Designed by:YXQ
 *
 *
 */
public class DialogFirFilter extends JFrame{
	
//	protected static ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
	private float[] inputData;
	private float[] outputData; 
	private int sampleFre;
	private Box holeBox;
	private JPanel filterChoosePanel;
	private JPanel stopFrequencyPanel;
	private JPanel lowStopPanel;
	private JPanel highStopPanel;
	private JPanel orderPanel;
	private JPanel winPanel;
	private JPanel buttonPanel;
	private Label filterLab;
	private Choice filterChoose;
	private Label stopFreLab;
	private Label lowStopLab;
	private Label highStopLab;
	private TextField lowStopTfd;
	private TextField highStopTfd;
	private Label WindowTypeLab;
	private Choice WindowTypeChoice;
	private Label orderLab;
	private TextField orderTfd;
	private Button sureButton;
	private Button cancelButton;
	private OriginalChartPanel originalChart;
	private FrierChartPanel frierChart;

	private final int RECT_WIN = 1;// 矩形窗
	private final int TUKEY_WIN = 2;// 图基窗
	private final int TRIANG_WIN = 3;// 三角窗
	private final int KAISER_WIN = 4;// 凯塞窗
	private final int HANNING_WIN = 5;// 汉宁窗
	private final int HAMING_WIN = 6;// 海明窗
	private final int BLACKMAN_WIN = 7;// 布莱克曼窗
	
	String lowPass = HduChartUtil.getResource("OfflineFilter_Lowpass");
	String highPass = HduChartUtil.getResource("OfflineFilter_Highpass");
	String bandPass = HduChartUtil.getResource("OfflineFilter_Bandpass");
	String bandStop = HduChartUtil.getResource("OfflineFilter_Bandstop");
	
	String rectWin = HduChartUtil.getResource("OfflineFilter_RectWin");
	String turkeyWin = HduChartUtil.getResource("OfflineFilter_TurkeyWin");
	String triatWin = HduChartUtil.getResource("OfflineFilter_TriaWin");
	String hanningWin = HduChartUtil.getResource("OfflineFilter_KaiserWin");
	String hamingWin = HduChartUtil.getResource("OfflineFilter_HanningWin");
	String kasierWin = HduChartUtil.getResource("OfflineFilter_Haming");
	String blackWin = HduChartUtil.getResource("OfflineFilter_BlackmanWin");
	
	public DialogFirFilter(OriginalChartPanel originalChart,FrierChartPanel frierChart)
	{
		super(HduChartUtil.getResource("FIR_Filter"));
		setSize(250, 270);
		RefineryUtilities.centerFrameOnScreen(this);
		this.originalChart=originalChart;
		this.frierChart=frierChart;
		this.sampleFre=frierChart.smprateRate;
		this.inputData=originalChart.planeXY.getY().clone();
		for(int i=0;i<inputData.length;i++)
		{
			this.inputData[i]=inputData[i];
		}
		inition();
		guiDesign();
	}
	private void inition()
	{
		holeBox = Box.createVerticalBox();
		filterChoosePanel = new JPanel(new FlowLayout());
		stopFrequencyPanel = new JPanel(new GridLayout(3, 1));
		lowStopPanel = new JPanel(new FlowLayout());
		highStopPanel = new JPanel(new FlowLayout());

		orderPanel = new JPanel(new FlowLayout());
		winPanel = new JPanel(new FlowLayout());
		buttonPanel = new JPanel(new FlowLayout());

		filterLab = new Label(HduChartUtil.getResource("OfflineFilter_Type"));
		filterChoose = new Choice();
		filterChoose.add(lowPass);
		filterChoose.add(highPass);
		filterChoose.add(bandPass);
		filterChoose.add(bandStop);
		filterChoose.addItemListener(filterLisitener);

		stopFreLab = new Label(HduChartUtil.getResource("OfflineFilter_passFre"));
		lowStopLab = new Label(HduChartUtil.getResource("OfflineFilter_LowFre"));
		highStopLab = new Label(HduChartUtil.getResource("OfflineFilter_HighFre"));
		lowStopTfd = new TextField("50");
		highStopTfd = new TextField("100");
		highStopTfd.setEnabled(false);

		WindowTypeLab = new Label(HduChartUtil.getResource("OfflineFilter_WinType"));
		WindowTypeChoice = new Choice();
		WindowTypeChoice.add(rectWin);
		WindowTypeChoice.add(turkeyWin);
		WindowTypeChoice.add(triatWin);
		WindowTypeChoice.add(hamingWin);
		WindowTypeChoice.add(kasierWin);
		WindowTypeChoice.add(blackWin);

		orderLab = new Label(HduChartUtil.getResource("OfflineFilter_Order"));
		orderTfd = new TextField("30");
		sureButton = new Button(HduChartUtil.getResource("Common_Ensure"));
		cancelButton = new Button(HduChartUtil.getResource("Common_Cancle"));
	}

	private void guiDesign()
	{
		filterChoosePanel.add(filterLab);
		filterChoosePanel.add(filterChoose);
		
		lowStopPanel.add(lowStopLab);
		lowStopPanel.add(lowStopTfd);
		highStopPanel.add(highStopLab);
		highStopPanel.add(highStopTfd);
		stopFrequencyPanel.add(stopFreLab);
		stopFrequencyPanel.add(lowStopPanel);
		stopFrequencyPanel.add(highStopPanel);

		winPanel.add(WindowTypeLab);
		winPanel.add(WindowTypeChoice);
		orderPanel.add(orderLab);
		orderPanel.add(orderTfd);

		buttonPanel.add(sureButton);
		sureButton.addActionListener(suerBtnListener);
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(cancelBtnListener);

		holeBox.add(filterChoosePanel);
		holeBox.add(stopFrequencyPanel);
		holeBox.add(winPanel);
		holeBox.add(orderPanel);
		holeBox.add(buttonPanel);

		add(holeBox);
		
	}
	
	//选择窗口滤波的类型
	private int windowType()
	{
		int windowType = RECT_WIN;
		String type = WindowTypeChoice.getSelectedItem();
		if (type.equals(rectWin)) {
			windowType = RECT_WIN;
		} else if (type.equals(turkeyWin)) {
			windowType = TUKEY_WIN;
		} else if (type.equals(triatWin)) {
			windowType = TRIANG_WIN;
		} else if (type.equals(hanningWin)) {
			windowType = HANNING_WIN;
		} else if (type.equals(hamingWin)) {
			windowType = HAMING_WIN;
		} else if (type.equals(kasierWin)) {
			windowType = KAISER_WIN;
		} else if (type.equals(blackWin)) {
			windowType = BLACKMAN_WIN;
		}
		return windowType;
	}
	
	//确定滤波类型，低通，高通，带通，或者带阻
	private int bandChoose() {
		String filterType = filterChoose.getSelectedItem();
		int band = 1;
		if (filterType == lowPass) {
			band = 1;
		} else if (filterType == highPass) {
			band = 2;
		} else if (filterType == bandPass) {
			band = 3;
		} else if (filterType == bandStop) {
			band = 4;
		}
		return band;
	}
	
	
	public ActionListener suerBtnListener=new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
//			String filterType=filterChoose.getSelectedItem();
			PlaneXY planeXY=new PlaneXY();
			int order=Integer.parseInt(orderTfd.getText());
			int passFre=Integer.parseInt(lowStopTfd.getText());
			int stopFre=Integer.parseInt(highStopTfd.getText());
			if(stopFre<=passFre)
			{
				JOptionPane.showMessageDialog(null,HduChartUtil.getResource("OfflineFilter_FilterError"));
				return;	
			}
			
			FirFilter filterFir = new FirFilter();
			int type = windowType();
			int band = bandChoose();
			outputData = filterFir.FirWin(order, band, passFre
					/ (float) sampleFre, (float)stopFre / (float) sampleFre, type,
					inputData);
			float[] xvalue = new float[outputData.length];
			for (int i = 0; i < outputData.length; i++) {
				xvalue[i] = i;
			}
			planeXY.setX(xvalue);
			planeXY.setY(outputData);
			originalChart.upAutSeriesData(originalChart.chartName, planeXY, Color.blue);
			originalChart.mouseListener.setPlanexy(planeXY);
			frierChart.creatFrierChart(outputData);
			hide();
		}
	};
	
	public ActionListener cancelBtnListener=new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			hide();
		}
	};
	
	private ItemListener filterLisitener=new ItemListener() {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			String filterType=e.toString();
			if(filterType.equals(lowPass))
			{
				highStopTfd.setEnabled(false);
				lowStopTfd.setEnabled(true);
			}
			else if(filterType.equals(highPass)){
				highStopTfd.setEnabled(true);
				lowStopTfd.setEnabled(false);
				
			}else if(filterType.equals(bandPass)){
				highStopTfd.setEnabled(true);
				lowStopTfd.setEnabled(true);
				
			}else if(filterType.equals(bandStop)){
				highStopTfd.setEnabled(true);
				lowStopTfd.setEnabled(true);
				
			}
		}
	};
}

