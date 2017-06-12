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

import com.nari.slsd.hms.hdu.common.algorithm.IirFilter;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Created :2014-11-16 ����3:03:11 
 * Describe : 无限长滤波对话框，用于设置滤波器的参数
 * Class : IIRFilter.java
 * Created by: YXQ
 * 
 */
public class DialogIIRFilter extends JFrame {
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
	private JPanel buttonPanel;
	private Label filterLab;
	private Choice filterChoose;
	private Label stopFreLab;
	private Label lowStopLab;
	private Label highStopLab;
	private TextField lowStopTfd;
	private TextField highStopTfd;
	private Label orderLab;
	private TextField orderTfd;
	private Button sureButton;
	private Button cancelButton;
	
	private OriginalChartPanel originalChart;
	private FrierChartPanel frierChart;
	private final int LOWPASS=1;
	private final int HIGHPASS=2;
	private final int BANDPASS=3;
	private final int BANDSTOP=4;
	
	String lowPass = HduChartUtil.getResource("OfflineFilter_Lowpass");
	String highPass = HduChartUtil.getResource("OfflineFilter_Highpass");
	String bandPass = HduChartUtil.getResource("OfflineFilter_Bandpass");
	String bandStop = HduChartUtil.getResource("OfflineFilter_Bandstop");
	
	public DialogIIRFilter(OriginalChartPanel originalChart,FrierChartPanel frierChart)
	{
		super(HduChartUtil.getResource("IIR_Filter"));
		setSize(250, 270);
		RefineryUtilities.centerFrameOnScreen(this);
		this.originalChart=originalChart;	
		
		this.frierChart=frierChart;
		this.sampleFre=frierChart.smprateRate;
		this.inputData=originalChart.planeXY.getY().clone();
		
		inition();
		guiDesign();
	}
	private void inition()
	{
		holeBox=Box.createVerticalBox();
		filterChoosePanel=new JPanel(new FlowLayout());
		stopFrequencyPanel=new JPanel(new GridLayout(3,1));
		lowStopPanel=new JPanel(new FlowLayout());
		highStopPanel=new JPanel(new FlowLayout());
		orderPanel=new JPanel(new FlowLayout());
		buttonPanel=new JPanel(new FlowLayout());
		
		filterLab=new Label(HduChartUtil.getResource("OfflineFilter_Filter"));
		filterChoose=new Choice();
		filterChoose.add(lowPass);
		filterChoose.add(highPass);
		filterChoose.add(bandPass);
		filterChoose.add(bandStop);
		filterChoose.addItemListener(filterLisitener);
		
		stopFreLab=new Label(HduChartUtil.getResource("OfflineFilter_passFre"));
		lowStopLab=new Label(HduChartUtil.getResource("OfflineFilter_LowFre"));
		highStopLab=new Label(HduChartUtil.getResource("OfflineFilter_HighFre"));
		lowStopTfd=new TextField("50");
		highStopTfd=new TextField("100");
		highStopTfd.setEnabled(false);
		
		orderLab=new Label(HduChartUtil.getResource("OfflineFilter_Order"));
		orderTfd=new TextField("5");
		sureButton=new Button(HduChartUtil.getResource("Common_Ensure"));
		cancelButton=new Button(HduChartUtil.getResource("Common_Cancle"));
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
		
		orderPanel.add(orderLab);
		orderPanel.add(orderTfd);
		
		buttonPanel.add(sureButton);
		sureButton.addActionListener(suerBtnListener);
		buttonPanel.add(cancelButton);
		
		holeBox.add(filterChoosePanel);
		holeBox.add(stopFrequencyPanel);
		holeBox.add(orderPanel);
		holeBox.add(buttonPanel);
		
		add(holeBox);
	}
	
	protected int bandType() {
		int bandType = LOWPASS;
		String type = filterChoose.getSelectedItem();
		if (type.equals(lowPass)) {
			bandType = LOWPASS;
		} else if (type.equals(highPass)) {
			bandType = HIGHPASS;
		} else if (type.equals(bandPass)) {
			bandType = BANDPASS;
		} else if (type.equals(bandStop)) {
			bandType = BANDSTOP;
		}
		return bandType;
	}
	
	
	/**
	 * 
	 * @param type 滤波类型，低通，高通之类的
	 * @param order 阶数
	 * @param lowFre 低通带频率
	 * @param highFre 高通带频率
	 * @param input 输入的数值
	 * @return 得到滤波以后的数值
	 */
	private float[] filterDesign(int type,int order,int lowFre,int highFre,float input[])
	{
		IirFilter iirFilter=new IirFilter(sampleFre, input);
		float[] output = null;
		double num;
		num=order/2.0;
		float[] a=new float[(int) (Math.ceil(num)*(2+1))];
		float[] b=new float[(int) (Math.ceil(num)*(2+1))];
		switch (type) {
		case LOWPASS:{
			a=new float[(int) (Math.ceil(num)*(2+1))];
			b=new float[(int) (Math.ceil(num)*(2+1))];
			output=iirFilter.iirFilterDesign(type, (int)Math.ceil(num), 2, lowFre/(float)sampleFre, 0, 0, 0, 30, b, a);
		}
			break;
		case HIGHPASS:{
			num=order/2.0;
			a=new float[(int) (Math.ceil(num)*(2+1))];
			b=new float[(int) (Math.ceil(num)*(2+1))];
			output=iirFilter.iirFilterDesign(type, (int)Math.ceil(num), 2, 0, highFre/(float)sampleFre, 0, 0, 30, b, a);
		}
			break;
		case BANDPASS:{
			num=order/4.0;
			a=new float[(int) (Math.ceil(num)*(4+1))];
			b=new float[(int) (Math.ceil(num)*(4+1))];
			output=iirFilter.iirFilterDesign(type, (int)Math.ceil(num), 4,  0, lowFre/(float)sampleFre, highFre/(float)sampleFre, 0,30, b, a);
		}
			break;
		case BANDSTOP:{
			
			num= order/4.0;
			a=new float[(int) (Math.ceil(num)*(4+1))];
			b=new float[(int) (Math.ceil(num)*(4+1))];
			highStopTfd.setEnabled(false);
			output=iirFilter.iirFilterDesign(type, (int)Math.ceil(num), 4, lowFre/(float)sampleFre, 0, 0, highFre/(float)sampleFre, 30, b, a);
		}
			break;
		}
		return output;
	}
	
	private ActionListener suerBtnListener=new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			PlaneXY planeXY=new PlaneXY();
			int type = bandType();
			int order = Integer.parseInt(orderTfd.getText());
			int lowFre = Integer.parseInt(lowStopTfd.getText());
			int highFre = Integer.parseInt(highStopTfd.getText());
			
			outputData=filterDesign(type,order,lowFre,highFre,inputData);
			if(outputData==null){
				JOptionPane.showMessageDialog(null, HduChartUtil.getResource("OfflineFilter_FilterError"));
				return;
			}
			float[] xvalue = new float[outputData.length];
			for(int i=0;i<outputData.length;i++)
			{
				xvalue[i]=i;
			}
			planeXY.setX(xvalue);
			planeXY.setY(outputData);
			originalChart.upAutSeriesData(originalChart.chartName, planeXY, Color.blue);
			originalChart.mouseListener.setPlanexy(planeXY);
			frierChart.creatFrierChart(outputData);
//			DataAnalysePanel.originalChartPanel.creatOriginalCharts(xvalue, outputData);
//			DataAnalysePanel.frierChartPanel.creatFrierChart(outputData, outputData.length, sampleFre);
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
