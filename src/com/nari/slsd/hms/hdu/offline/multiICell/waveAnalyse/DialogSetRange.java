package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.ui.RefineryUtilities;
import org.jzy3d.maths.Grid;

import com.nari.slsd.hms.hdu.utils.HduChartUtil;

public abstract class DialogSetRange extends JFrame{
	private JPanel panel;
	private JPanel setPanel;
	private JPanel btnPanel;
	private JLabel setRangeLab;
	private JTextField lowRangeTxt;
	private JTextField upRangeTxt;
	private JButton sureBtn;
	private JButton cancelBtn;
	public DialogSetRange(){
		RefineryUtilities.centerFrameOnScreen(this);
		init();
	}
	
	private void init(){
		panel = new JPanel(new GridLayout(2,1));
		setPanel = new JPanel(new GridLayout(1,4));
		btnPanel = new JPanel(new GridLayout(1,2));
		setRangeLab = new JLabel(HduChartUtil.getResource("setRange")+":");
		lowRangeTxt = new JTextField();
		upRangeTxt = new JTextField();
		sureBtn = new JButton(HduChartUtil.getResource("sure"));
		cancelBtn = new JButton(HduChartUtil.getResource("cancel"));
		
		setPanel.add(setRangeLab);
		setPanel.add(lowRangeTxt);
		setPanel.add(new JLabel("——"));
		setPanel.add(upRangeTxt);
		
		sureBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sureEvent( Float.parseFloat(lowRangeTxt.getText()), Float.parseFloat(upRangeTxt.getText()));
			}
		});
		btnPanel.add(sureBtn);
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setVisible(false);
			}
		});
		btnPanel.add(cancelBtn);
		
		panel.add(setPanel);
		panel.add(btnPanel);
		add(panel);
	}
	public abstract void sureEvent(float lowRange,float upRange);
	
}
