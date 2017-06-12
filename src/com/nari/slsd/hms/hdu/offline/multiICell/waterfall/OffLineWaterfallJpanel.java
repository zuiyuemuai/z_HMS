package com.nari.slsd.hms.hdu.offline.multiICell.waterfall;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jzy3d.maths.Coord3d;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.Coordinate3D;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.OffLineICellInterface;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线瀑布图界面 将每个区段的按照特征值在3D坐标中画出频谱图
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class OffLineWaterfallJpanel extends JPanel implements
		OffLineICellInterface, ActionListener {
	// private Font titlefont = PropertiesUtil.titlefont;
	// private Font textfont = PropertiesUtil.textfont;

	private OfflineCoordinate coordinate3d;
	private JButton creatWordBtn = new JButton(
			HduChartUtil.getResource("Common_CreatWord"));
	private String dataName;
	private String refreName;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		coordinate3d = new OfflineCoordinate();

		this.setLayout(new BorderLayout());
		this.add(coordinate3d, BorderLayout.CENTER);
		creatWordBtn.addActionListener(creatWordListener);
		
		
		this.add(getTopPanel(), BorderLayout.NORTH);

	}
	
	private JPanel getTopPanel()
	{
		JPanel topJPanel = new JPanel(new GridBagLayout());
		topJPanel.setBackground(Color.white);
		GridBagUtil.addBlankJLabel(topJPanel, 0, 0, 15, 1);
		GridBagUtil.setLocation(topJPanel, creatWordBtn, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(topJPanel, 2, 0, 1, 1);
		return topJPanel;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public OffLineWaterfallJpanel() {
		init();

	}

	public JPanel getPanelSelf() {
		return this;
	}

	/**
	 * 
	 * @param waveMessage
	 *            波形数据
	 * @param featureValue
	 *            特征值
	 */
	public OffLineWaterfallJpanel(Vector<PlaneXY> waveMessage,
			float[] featureValue, String dataName, String refreName) {
		this.dataName = dataName;
		this.refreName = refreName;
		init();
		coordinate3d.update(waveMessage, featureValue);
	}

	private class OfflineCoordinate extends Coordinate3D {
		protected void initCoordinate() {

			// 初始化坐标系的范围
			list.add(new Coord3d(0, 0, 0));
			list.add(new Coord3d(1, 1, 1));
			points = list.toArray(new Coord3d[list.size()]);

			super.initCoordinate();

		}

		/**
		 * 更新数据
		 * 
		 * @param waveMessage
		 *            数据通道
		 * @param featureValue
		 *            数据通道对应的特征值
		 */
		public void update(Vector<PlaneXY> waveMessage, float[] featureValue) {
			// TODO Auto-generated method stub
			list.clear();
			for (int i = 0; i < featureValue.length; i++) {
				PlaneXY xy = waveMessage.get(i);
				int len = xy.getX().length;
				for (int j = 0; j < len; j++) {
					z_max = Math.max(z_max, xy.getY()[j]);
					y_max = Math.max(y_max, xy.getX()[j]);
					list.add(new Coord3d(featureValue[i], xy.getX()[j], xy
							.getY()[j]));
				}
			}

			// 更新
			display();
			myColorMapper.setZMax(z_max);

		}

	}

	private void creatWord() {
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File(HduChartUtil
				.getResource("OfflineWater_Word")));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else {
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"waterFallModel.ftl") {

			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				Point imagePoint = coordinate3d.getLocationOnScreen();
				dataMap.put("image", ImageChange.get3DImageEncode(
						(int)imagePoint.getX(), (int)imagePoint.getY(),
						coordinate3d.getWidth(), coordinate3d.getHeight()));
				dataMap.put("datachannel", dataName);
				dataMap.put("refrechannel", refreName);
//				dataMap.put("datachannel", "shangdao");
//				dataMap.put("refrechannel", "xiadao");
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
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

	public static void main(String args[]) {
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());

		OffLineWaterfallJpanel posture3d = new OffLineWaterfallJpanel();

		jFrame.add(posture3d, BorderLayout.CENTER);

		jFrame.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String actString = e.getActionCommand();

	}

}
