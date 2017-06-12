package com.nari.slsd.hms.hdu.common.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

/**
 * 截屏程序
 * 
 * @author Administrator
 * 
 */
public class ScreenShots extends JFrame
{
	private static final long serialVersionUID = 1L;
	int orgx, orgy, endx, endy;
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	BufferedImage image;
	BufferedImage tempImage;
	BufferedImage saveImage;
	Graphics g;

	@Override
	public void paint(Graphics g)
	{
		RescaleOp ro = new RescaleOp(0.8f, 0, null);
		tempImage = ro.filter(image, null);
		g.drawImage(tempImage, 0, 0, this);
	}

	public ScreenShots(int x, int y, int width, int height )
	{
		setSize(1, 1);
		snapshot();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		saveImage = image.getSubimage(x, y, width, height);
		g = getGraphics();
		g.drawImage(saveImage, x, y, ScreenShots.this);
		setVisible(false);
		saveToFile();
	}

	public void saveToFile( )
	{
//		String pathString = PropertiesUtil.getPropertiesValue(PropertiesUtil.KEY_3DSAVE_PATH);
//		if(null == pathString)//读取配置文件，如果没有则默认桌面
//		{
		String pathString = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
//		}
		JFileChooser fileChooser = new JFileChooser(pathString);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 选择的是文件夹
		int returnVal = fileChooser.showOpenDialog(fileChooser);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
//			HashMap<String, String> map = new HashMap<String, String>();
			
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();// 这个就是你选择的文件夹的路径
		
//			map.put(PropertiesUtil.KEY_WORKSPACE_PATH, filePath);
//			PropertiesUtil.updateProperties(map);//更新配置文件
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
			String name = sdf.format(new Date());
			String format = "jpg";
			File f = new File(filePath + File.separator + name + "." + format);
			try
			{
				ImageIO.write(saveImage, format, f);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public void snapshot()
	{
		try
		{
			Robot robot = new Robot();
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			image = robot.createScreenCapture(new Rectangle(0, 0, d.width,
					d.height));
		} catch (AWTException e)
		{
			e.printStackTrace();
		}
	}

}
