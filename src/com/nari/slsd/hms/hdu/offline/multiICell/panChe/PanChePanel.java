package com.nari.slsd.hms.hdu.offline.multiICell.panChe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;
/**
 * 盘车试验
 * @author YXQ
 * @version 1.0,14/12/25
 * @since JDK1.625
 */
public class PanChePanel extends JPanel{
//	protected static ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
//	
	private final String IMPORT_DATA_COMMAND = "IMPORT_DATA";
	private final String IMPORT_OUT_DATA_COMMAND = "IMPORT_OUT_DATA";
	private final String SAVE_DATA_COMMAND = "SAVE_DATA";
	private final String SAVE_IMAGE_COMMAND = "SAVE_IMAGE";
	/** 输出word文档 */
	private final String CREAT_WORD_COMMAND = "CREAT_WORD";
	
	private LineChartPanel line1;
	private LineChartPanel line2;
	private JButton computerBtn;
	private JLabel deviateXLab;
	private JLabel deviateXTxt;
	private JLabel deviateYLab;
	private JLabel deviateYTxt;
	private JLabel deviateDisLab;
	private JLabel deviateDisTxt;
	private JLabel deviateAngleLab;
	private JLabel deviateAngleTxt;
	private JPanel linePanel;
	private JPanel txtPanel;
	private JButton creatWord;
	
	public PanChePanel(){
		init();
		guiDesign();
	}
	
	private void init(){
		line1 = new LineChartPanel();
		line2 = new LineChartPanel();
		computerBtn = new JButton(HduChartUtil.getResource("OfflinePanche_Computer"));
		deviateXLab = new JLabel(HduChartUtil.getResource("OfflinePanche_DeviateX"));
		deviateYLab = new JLabel(HduChartUtil.getResource("OfflinePanche_DeviateY"));
		deviateDisLab = new JLabel(HduChartUtil.getResource("OfflinePanche_DeviateDis"));
		deviateAngleLab = new JLabel(HduChartUtil.getResource("OfflinePanche_DeviateAngle"));
		deviateXTxt = new JLabel();
		deviateYTxt = new JLabel();
		deviateDisTxt = new JLabel();
		deviateAngleTxt = new JLabel();
		creatWord = new JButton(HduChartUtil.getResource("Common_CreatWord"));
		creatWord.setActionCommand(CREAT_WORD_COMMAND);
		creatWord.addActionListener(popupMenuListener);
		txtPanel = new JPanel(new GridLayout(1,10));
		txtPanel.setBackground(Color.white);
		linePanel = new JPanel(new GridLayout(2,1));
		setLayout(new BorderLayout());
		
	}
	
	private void guiDesign(){
		
		txtPanel.add(deviateXLab);
		txtPanel.add(deviateXTxt);
		txtPanel.add(deviateYLab);
		txtPanel.add(deviateXTxt);
		txtPanel.add(deviateDisLab);
		txtPanel.add(deviateDisTxt);
		txtPanel.add(deviateAngleLab);
		txtPanel.add(deviateAngleTxt);
		txtPanel.add(creatWord);
		txtPanel.add(computerBtn);
		linePanel.add(line1);
		linePanel.add(line2);
		add(txtPanel,BorderLayout.NORTH);
		add(linePanel);
	
		setComponentPopupMenu(creatPopuMenu());
	}
	
	private void creatWord(){
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil.getResource("Common_ChooseSavePath"));
		   jFileChooser.setSelectedFile(new  
		   File(HduChartUtil.getResource("OfflinePanche_Word"))); 
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
		
		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel",savePath,"pancheModel.ftl") {
			
			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();

				
				dataMap.put("image", ImageChange.getImageEncode(linePanel));
				dataMap.put("deviateX",deviateXTxt.getText());
				dataMap.put("deviateY", deviateYTxt.getText());
				dataMap.put("deviateDis", deviateDisTxt.getText());
				dataMap.put("deviateAgl", deviateAngleTxt.getText());
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month", String.valueOf(now.get(Calendar.MONTH)+1));
				dataMap.put("date", String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
				
			}
		};
	}
	
	private JPopupMenu creatPopuMenu(){
		
		JPopupMenu jPopupMenu = new JPopupMenu();
		
		JMenuItem importData = new JMenuItem(HduChartUtil.getResource("OfflinePanche_ImportData"));
		importData.setActionCommand(IMPORT_DATA_COMMAND);
//		importData.addActionListener(popupMenuListener);
		jPopupMenu.add(importData);
		
		JMenuItem importOutside = new JMenuItem(HduChartUtil.getResource("OfflinePanche_ImportOutData"));
		importOutside.setActionCommand(IMPORT_OUT_DATA_COMMAND);
		importOutside.addActionListener(popupMenuListener);
		jPopupMenu.add(importOutside);
		
		JMenuItem saveData = new JMenuItem(HduChartUtil.getResource("OfflinePanche_SaveData"));
		saveData.setActionCommand(SAVE_DATA_COMMAND);
		saveData.addActionListener(popupMenuListener);
		jPopupMenu.add(saveData);
		
		JMenuItem saveImage = new JMenuItem(HduChartUtil.getResource("OfflinePanche_SaveImage"));
		saveImage.setActionCommand(SAVE_IMAGE_COMMAND);
		saveImage.addActionListener(popupMenuListener);
		jPopupMenu.add(saveImage);
		
		JMenuItem creatWord = new JMenuItem(
				HduChartUtil.getResource("Common_CreatWord"));
		creatWord.setActionCommand(CREAT_WORD_COMMAND);
		creatWord.addActionListener(popupMenuListener);
		jPopupMenu.add(creatWord);
		
		line1.chartPanel.setPopupMenu(jPopupMenu);
		line2.chartPanel.setPopupMenu(jPopupMenu);
		return jPopupMenu;
	}
	
	private ActionListener popupMenuListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String command = e.getActionCommand();
			if(command.equals(CREAT_WORD_COMMAND)){
				creatWord();
			}
		}
	};
	
}
