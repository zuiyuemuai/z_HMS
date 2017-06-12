/**
 * 
 */
package com.nari.slsd.hms.hdu.common.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JPanel;
import javax.xml.ws.Endpoint;

/**
 * Created :2014-8-26 ����5:07:25 Describe : Class : ArrowPanel.java love you
 * 
 * 
 */
public class DrawArrow {
	int startPointX;
	int startPointY;
	int endPointX;
	int endPointY;
	String tipMean;

	public DrawArrow(int startPointX, int startPointY, int endPointX, int endPointY, String tipMean) {
		this.startPointX = startPointX;
		this.startPointY = startPointY;
		this.endPointX = endPointX;
		this.endPointY = endPointY;
		this.tipMean = tipMean;
	}

	public void paintComponent(Graphics g) {
		// this.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.red);

		drawAL(startPointX, startPointY, endPointX, endPointY, g2, tipMean);// ����x1,
																			// y1,
		// x2,
		// y2����Ҫ�������ҳ�ʼ���������������λ�úͳ�ʼ����ֵ

		// �ɱ����Լ����������������û���������ǡ�

	}

	public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2, String tipMean) {

		double H = 6; // ��ͷ�߶�
		double L = 6; // �ױߵ�һ��
		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H); // ��ͷ�Ƕ�
		double arraow_len = Math.sqrt(L * L + H * H); // ��ͷ�ĳ���
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0]; // (x3,y3)�ǵ�һ�˵�
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0]; // (x4,y4)�ǵڶ��˵�
		double y_4 = ey - arrXY_2[1];

		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();
		// g.setColor(SWT.COLOR_WHITE);
		// ����
		g2.drawLine(sx, sy, ex, ey);

		// GeneralPath triangle = new GeneralPath();
		// triangle.moveTo(ex, ey);
		// triangle.lineTo(x3, y3);
		// triangle.lineTo(x4, y4);
		// triangle.closePath();
		// //ʵ�ļ�ͷ
		// g2.fill(triangle);
		// //��ʵ�ļ�ͷ
		// //g2.draw(triangle);
		// // ����ͷ��һ��
		g2.drawLine(ex, ey, x3, y3);
		// // ����ͷ����һ��
		g2.drawLine(ex, ey, x4, y4);
		g2.drawString(tipMean, ex, ey);

	}

	// ����
	public static double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {

		double mathstr[] = new double[2];
		// ʸ����ת���������ֱ���x������y��������ת�ǡ��Ƿ�ı䳤�ȡ��³���
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

}
