/**
 * 
 */
package com.nari.slsd.hms.hdu.offline.multiICell.waveAnalyse;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.RefineryUtilities;

import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * Created :2014-8-28 下午9:00:04 
 * Describe :保存图片 
 * Class : SavePicture.java
 * Designed by:YXQ
 * 
 */
public class DialogSetSave extends JFrame
{
	// super("自定义保存图片");
//	protected static ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
	private Container container = getContentPane();
	private Panel objp = new Panel();
	private CheckboxGroup saveobj;
	private Checkbox allimage;
	private Checkbox xylineimage;
	private Checkbox fftimage;
	private final int IMAGE_ALL = 2000;
	private final int IMAGE_XYLINE = 2001;
	private final int IMAGE_FFT = 2002;

	private int objselect;

	private Panel formatp = new Panel();
	private CheckboxGroup saveformat;
	private Checkbox png;
	private Checkbox jpg;
	private Checkbox bmp;
	// private int formatselect;

	private Panel buttonp = new Panel();
	private Button sure;
	private Button cacel;

	private String imageFormat = "PNG";
	private OriginalChartPanel chartOriginal;
	private FrierChartPanel chartFrier;
	private JPanel panel;
	String allImage = HduChartUtil.getResource("Image_all");
	String originalImg = HduChartUtil.getResource("Image_Ori");
	String frierImg = HduChartUtil.getResource("Image_Frier");
	String saveSuccess = HduChartUtil.getResource("Common_SaveSuccess");

	public DialogSetSave(JPanel panel, OriginalChartPanel  chartOriginal,
			FrierChartPanel chartFrier)
	{

		super(HduChartUtil.getResource("OfflineSaveImage_SaveImage"));
		RefineryUtilities.centerFrameOnScreen(this);
		setSize(250, 180);
		this.chartOriginal = chartOriginal;
		this.chartFrier = chartFrier;
		this.panel = panel;
		guiDesign();

	}

	private void guiDesign()
	{
		saveobj = new CheckboxGroup();
		allimage = new Checkbox(allImage, saveobj, false);
		xylineimage = new Checkbox(originalImg, saveobj, true);
		fftimage = new Checkbox(frierImg, saveobj, false);
		objp.add(allimage);
		objp.add(xylineimage);
		objp.add(fftimage);
		objselect = IMAGE_XYLINE;
		// allimage.addItemListener(this);
		// xylineimage.addItemListener(this);
		// fftimage.addItemListener(this);

		saveformat = new CheckboxGroup();
		png = new Checkbox("PNG", saveformat, true);
		jpg = new Checkbox("JPEG", saveformat, false);
		bmp = new Checkbox("BMP", saveformat, false);

		formatp.add(png);
		formatp.add(jpg);
		formatp.add(bmp);

		sure = new Button(HduChartUtil.getResource("Common_Ensure"));

		cacel = new Button(HduChartUtil.getResource("Common_Cancle"));
		buttonp.add(sure);
		buttonp.add(cacel);

		container.setLayout(new GridLayout(3, 1));
		container.add(objp);
		container.add(formatp);
		container.add(buttonp);

		ObjItemListener obj = new ObjItemListener();
		allimage.addItemListener(obj);
		xylineimage.addItemListener(obj);
		fftimage.addItemListener(obj);

		ForItemListener format = new ForItemListener();
		png.addItemListener(format);
		jpg.addItemListener(format);
		bmp.addItemListener(format);

		sure.addActionListener(surebtnLisitener);
		cacel.addActionListener(cancelLisitener);
	}

	class ObjItemListener implements ItemListener
	{

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			// TODO Auto-generated method stub
			Checkbox cb = (Checkbox) e.getItemSelectable();
			if (cb.getLabel().equals(allImage))
			{
				if (cb.getState())
					objselect = IMAGE_ALL;
			}

			if (cb.getLabel().equals(originalImg))
			{
				if (cb.getState())
					objselect = IMAGE_XYLINE;
			}

			if (cb.getLabel().equals(frierImg))
			{
				if (cb.getState())
					objselect = IMAGE_FFT;
			}
		}
	}

	class ForItemListener implements ItemListener
	{

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			// TODO Auto-generated method stub
			Checkbox cb = (Checkbox) e.getItemSelectable();
			if (cb.getState())
			{
				if (cb.getLabel().equals("PNG"))
				{
					imageFormat = "PNG";
				}
				if (cb.getLabel().equals("JPEG"))
				{
					imageFormat = "JPEG";
				}
				if (cb.getLabel().equals("BMP")){
					imageFormat = "BMP";
				}
			}
		}
	}

	private ActionListener surebtnLisitener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			// TODO Auto-generated method stub
			String savePath = chartOriginal.chartName;
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
			jFileChooser.setDialogTitle(HduChartUtil.getResource("Common_ChooseSavePath"));
			   jFileChooser.setSelectedFile(new  
			   File(chartOriginal.chartName)); 
			jFileChooser.setMultiSelectionEnabled(false);
			int returnVal = jFileChooser.showSaveDialog(jFileChooser);
			if(returnVal != JFileChooser.APPROVE_OPTION){
				savePath = null;
				return;
			}
			else
				savePath = jFileChooser.getSelectedFile().getPath();
//			FileDialog d1 = new FileDialog(DialogSetSave.this, HduChartUtil.getResource("Common_ChooseSavePath"),
//					FileDialog.SAVE);
//			d1.setVisible(true);
//			String path = d1.getDirectory() + d1.getFile();

			switch (objselect)
			{
			case IMAGE_ALL:
			{
				BufferedImage bi = new BufferedImage(panel.getWidth(),
						panel.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = bi.createGraphics();
				panel.paint(g2d);

				try
				{
					savePath +=allImage+ "." + imageFormat;
//					path = path + "." + imageFormat;
					ImageIO.write(bi, imageFormat, new File(savePath));
					JOptionPane.showMessageDialog(null, saveSuccess);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				break;

			case IMAGE_XYLINE:
			{
				try
				{
					if (imageFormat.equals("PNG"))
					{
						savePath +=originalImg+ "." + imageFormat;
//						path = path + "." + imageFormat;
						ChartUtilities.saveChartAsPNG(new File(savePath),
								chartOriginal.chart, chartOriginal.getWidth(), chartOriginal.getHeight());
					} else if (imageFormat.equals("JPEG"))
					{
						savePath +=originalImg+ "." + imageFormat;
//						path = path + "." + imageFormat;
						ChartUtilities.saveChartAsJPEG(new File(savePath),
								chartOriginal.chart, chartOriginal.getWidth(), chartOriginal.getHeight());
					} else if(imageFormat.equals("BMP")){
						BufferedImage bi = new BufferedImage(chartOriginal.getWidth(),
								chartOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics2D g2d = bi.createGraphics();
						chartOriginal.paint(g2d);
						savePath +=originalImg+ "." + imageFormat;
//						path = path + "." + imageFormat;
						ImageIO.write(bi, imageFormat, new File(savePath));
					}
					JOptionPane.showMessageDialog(null, saveSuccess);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				break;

			case IMAGE_FFT:
			{
				try
				{
					if (imageFormat.equals("PNG"))
					{
						savePath +=frierImg+ "." + imageFormat;
//						path = path + "." + imageFormat;
						ChartUtilities.saveChartAsPNG(new File(savePath),
								chartFrier.chart, chartFrier.getWidth(), chartFrier.getHeight());
					} else if (imageFormat.equals("JPEG"))
					{
						savePath +=frierImg+ "." + imageFormat;
//						path = path + "." + imageFormat;
						ChartUtilities.saveChartAsJPEG(new File(savePath),
								chartFrier.chart, chartFrier.getWidth(), chartFrier.getHeight());

					}else if(imageFormat.equals("BMP")){
						BufferedImage bi = new BufferedImage(chartFrier.getWidth(),
								chartFrier.getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics2D g2d = bi.createGraphics();
						chartFrier.paint(g2d);
						savePath +=frierImg+ "." + imageFormat;
//						path = path + "." + imageFormat;
						ImageIO.write(bi, imageFormat, new File(savePath));
					}
					JOptionPane.showMessageDialog(null, saveSuccess);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				break;

			default:

			}
			setVisible(false);
		}

	};

	public ActionListener cancelLisitener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			setVisible(false);
		}
	};
}
