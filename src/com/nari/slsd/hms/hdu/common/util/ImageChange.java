package com.nari.slsd.hms.hdu.common.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import sun.misc.BASE64Encoder;


public class ImageChange {
	 
	public static String getImageEncode(JPanel jpanel){
		
		BufferedImage image = new BufferedImage(jpanel.getWidth(),
				jpanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		jpanel.paint(g2d);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
			boolean flag = ImageIO.write(image, "png", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     byte[] b = out.toByteArray();
     BASE64Encoder encoder = new BASE64Encoder();
     return encoder.encode(b);
	
	}
	
	
	public static String get3DImageEncode(int x, int y, int width, int height )
	{
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		BufferedImage image = null;
		BufferedImage saveImage;
		try
		{
			Robot robot = new Robot();
			Dimension d2 = Toolkit.getDefaultToolkit().getScreenSize();
			image = robot.createScreenCapture(new Rectangle(0, 0, d2.width,
					d2.height));
//			image = robot.createScreenCapture(new Rectangle(x, y, width,
//					height));
		} catch (AWTException e)
		{
			e.printStackTrace();
		}
		saveImage = image.getSubimage(x, y, width, height);
		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(saveImage, x, y, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
			boolean flag = ImageIO.write(saveImage, "png", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     byte[] b = out.toByteArray();
     BASE64Encoder encoder = new BASE64Encoder();
     return encoder.encode(b);
	}
	

	
}
