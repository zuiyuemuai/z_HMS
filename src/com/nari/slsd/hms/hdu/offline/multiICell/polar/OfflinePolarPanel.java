package com.nari.slsd.hms.hdu.offline.multiICell.polar;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.PolarChartPanel;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线极坐标界面
 * 将不同转速下的幅值和相位在极坐标系下显示
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class OfflinePolarPanel extends JPanel
{
//	protected static ResourceBundle localizationResources = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
	
	private PolarChartPanel chartPanel;

	private PlaneXY planeXYs = new PlaneXY();
	private float[] ref;
	private int[] sort = null;// 转速从小到大排序
	private String dataName;
	private String refreName;
	/**输出word报表功能**/
	JButton creatWordBtn = new JButton(HduChartUtil.getResource("Common_CreatWord"));
	
	/**
	 * 
	 * @param planeXYs
	 *            ：x轴是角度，y轴是相位
	 * @param ref
	 */
	public OfflinePolarPanel(String revname, String dataname,PlaneXY planeXYs, float[] ref)
	{
		this.dataName = dataname;
		this.refreName = revname;
		this.ref = ref.clone();
		this.planeXYs = planeXYs;
		chartPanel = new PolarChartPanel("");
		this.setLayout(new BorderLayout());
		this.add(chartPanel, BorderLayout.CENTER);
		creatWordBtn.addActionListener(creatWordListener);
		
		this.add(getTopJPanel(),BorderLayout.NORTH);
		
		dataAnalyse(ref, planeXYs);
		chartPanel.upAutSeriesData("ref："+ref[0], this.planeXYs, Color.blue);
		chartPanel.suitTickUnit();
		chartPanel.setCloseMouseDragOperation_XY();
	}

	private JPanel getTopJPanel()
	{
		JPanel topJPanel = new JPanel(new GridBagLayout());
		topJPanel.setBackground(Color.white);
		
		JLabel title = new JLabel(HduChartUtil.getResource("OfflinePolar_Polar"));
		title.setFont( new Font("微软雅黑", Font.BOLD, 16) );
		
		GridBagUtil.addBlankJLabel(topJPanel, 0, 0, 25, 1);
		GridBagUtil.setLocation(topJPanel, title, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(topJPanel, 2, 0, 15, 1);
		GridBagUtil.setLocation(topJPanel, creatWordBtn, 3, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(topJPanel, 4, 0, 1, 1);
		return topJPanel;
		
		
	}
	private void dataAnalyse(float[] rev, PlaneXY planeXYs)
	{

		sort = new int[rev.length];
		float[] sortdata = rev;
		float[] x = new float[rev.length]; 
		float[] y = new float[rev.length]; 
		for (int i = 0; i < sort.length; i++)
		{
			sort[i] = i;
		}
		// 排序，用了最简单了冒泡，因为数据量肯定不会很大，简单
		for (int i = 0; i < rev.length; i++)
		{
			for (int j = i; j < rev.length; j++)
			{
				if (sortdata[i] > sortdata[j])
				{
					float temp = sortdata[i];
					sortdata[i] = sortdata[j];
					sortdata[j] = temp;
					int t = sort[i];
					sort[i] = sort[j];
					sort[j] = t;
				}
			}
		}
		
		for (int i = 0; i < sortdata.length; i++)
		{
			x[i] = planeXYs.getX()[sort[i]];
			y[i] = planeXYs.getY()[sort[i]];
		}
		planeXYs.setX(x);
		planeXYs.setY(y);
	}
	
	
	public JPanel getPanelSelf(){
		return this;
	}
	
	private void creatWord()
	{
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil.getResource("Common_ChooseSavePath"));
		   jFileChooser.setSelectedFile(new  
		   File(HduChartUtil.getResource("OfflinePolar_Word"))); 
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
		
		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel",savePath,"polarModel.ftl") {
			
			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				dataMap.put("image", ImageChange.getImageEncode(chartPanel));
				dataMap.put("datachannel", dataName);
				dataMap.put("refrechannel", refreName);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month", String.valueOf(now.get(Calendar.MONTH)+1));
				dataMap.put("date", String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}
	
	
	public ActionListener creatWordListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
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

		float[] x = {  45, 135,0, 225, 315 };
		float[] y = { 0.1f, 0.2f, 0.3f, 0.5f, 0.6f};

		OfflinePolarPanel waveform = new OfflinePolarPanel("1","2",new PlaneXY(x, y), x);

		jFrame.add(waveform);// 添加到主界面中
		jFrame.setVisible(true);

		System.out.println(Math.cos(180 / 180.00 * Math.PI));
	}

}
