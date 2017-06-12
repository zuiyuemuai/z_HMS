package com.nari.slsd.hms.hdu.offline.multiICell.bode;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线伯德图界面 界面中上面chart显示转速与相位的关系，下面chart显示幅值与转速的关系
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class OfflineBodePanel extends JPanel
{
	private Font titlefont = new Font("微软雅黑", Font.BOLD, 18);
	private Font textfont = new Font("微软雅黑", Font.ITALIC, 12);
	
	private String dataName;
	private String refreName;
	LineChartPanel phaseChartPanel = new LineChartPanel(Color.black);
	LineChartPanel anpChartPanel = new LineChartPanel(Color.black);
	JButton creatWordBtn = new JButton(
			HduChartUtil.getResource("Common_CreatWord"));


	public OfflineBodePanel(String refreName, String dataName, PlaneXY phaXy,
			PlaneXY absXy)
	{
		this.dataName = dataName;
		this.refreName = refreName;
		for (int i = 0; i < phaXy.getX().length; i++)
		{
			if (phaXy.getX()[i] < 0)
			{
				JOptionPane
						.showMessageDialog(
								this,
								(i + 1)
										+ HduChartUtil
												.getResource("OfflineBode_InputErrMessage_front")
										+ phaXy.getX()[i]
										+ HduChartUtil
												.getResource("OfflineBode_InputErrMessage_behind"));
			}
		}

		UIDesign();
		phaseChartPanel.upAutSeriesData("pha", phaXy, ExtColor.getLineColor());
		phaseChartPanel.setCloseSeriesVisibleInLegend("pha");
		anpChartPanel.upAutSeriesData("abs", absXy, ExtColor.getLineColor());
		anpChartPanel.setCloseSeriesVisibleInLegend("abs");
	}

	private void UIDesign()
	{
		this.setLayout(new GridBagLayout());
		GridBagUtil.setLocation(this, getTitleJPanel(HduChartUtil
				.getResource("OfflineBode_BodeChart")), 0, 0, 1, 1, true);
		GridBagUtil.setLocation(this, phaseChartPanel, 0, 1, 1, 20, true);
		GridBagUtil.setLocation(this, anpChartPanel, 0, 2, 1, 20, true);

		phaseChartPanel.setRangeGridlinesVisible(false);
		anpChartPanel.setRangeGridlinesVisible(false);
		phaseChartPanel
				.setYLable(HduChartUtil.getResource("OfflineBode_Phase"));
		phaseChartPanel.setXLable(HduChartUtil.getResource("OfflineBode_Rev"));
		anpChartPanel.setYLable(HduChartUtil.getResource("OfflineBode_Amp"));
		anpChartPanel.setXLable(HduChartUtil.getResource("OfflineBode_Rev"));

	}

	private JPanel getTitleJPanel(String Title)
	{
		JPanel titleJPanel = new JPanel(new GridBagLayout());
		titleJPanel.setBackground(java.awt.Color.white);
		GridBagUtil.addBlankJLabel(titleJPanel, 0, 0, 15, 10);
		JLabel title = new JLabel(Title);
		title.setFont(titlefont);
		GridBagUtil.setLocation(titleJPanel, title, 1, 0, 1, 10, true);
		GridBagUtil.addBlankJLabel(titleJPanel, 2, 0, 12, 10);

		creatWordBtn.addActionListener(creatWordListener);
		GridBagUtil.setLocation(titleJPanel, creatWordBtn, 3, 0, 1, 0, true);

		GridBagUtil.addBlankJLabel(titleJPanel, 4, 0, 1, 10);
		
		GridBagUtil.addBlankJLabel(titleJPanel, 0, 1, 1, 1);

		return titleJPanel;
	}

	public JPanel getPanelSelf()
	{
		return this;
	}

	private void creatWord()
	{
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File(HduChartUtil
				.getResource("OfflineBode_Word")));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else
		{
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"bodeModel.ftl")
		{

			@Override
			public void getData(Map<String, Object> dataMap)
			{
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				dataMap.put("image", ImageChange.getImageEncode(getPanelSelf()));
				dataMap.put("datachannel", dataName);
				dataMap.put("refrechannel", refreName);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}

	public ActionListener creatWordListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			creatWord();
		}
	};

	public static void main(String[] args)
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		OfflineBodePanel waveform = new OfflineBodePanel("1", "2", null, null);

		jFrame.add(waveform);// 添加到主界面中
		jFrame.setVisible(true);
	}

}
