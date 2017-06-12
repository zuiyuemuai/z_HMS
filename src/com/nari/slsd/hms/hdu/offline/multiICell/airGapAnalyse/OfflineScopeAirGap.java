package com.nari.slsd.hms.hdu.offline.multiICell.airGapAnalyse;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.nari.slsd.hms.hdu.online.multiICell.airgap.AirGapStatorAnalyse;
import com.nari.slsd.hms.hdu.online.multiICell.airgap.AirgapRotatorAnalyse;

public class OfflineScopeAirGap extends JPanel implements ActionListener,
		MouseListener
{
	// ////////////
	// /////////////////////一些define
	static int UNIT_ROTATE_SPEED = 300;
	static int UNIT_VOLTAGE_FREQ = 50;
	static int ROTOR_POLE_NUM = 32;// (2*UNIT_VOLTAGE_FREQ*60/UNIT_ROTATE_SPEED);/*磁极对数
									// = 电压频率 * 60 / 转速*//*磁极个数 = 2 * 磁极对数*/
	static int AIRGAP_SENSOR_NUM = 4;
	static int MIN_AIRGAP_SENSOR_NUM = 4;
	// ///////////////////////
	public Rectangle m_rectClient; // /控件显示区域大小
	public Rectangle m_rectPlot;// /绘图区域
	public BasicStroke[] m_strokePlot = new BasicStroke[6];// 不同的线型
	public Color[] m_colorPlot = new Color[6];// 不同的线的对应颜色

	int m_nPole;
	int m_magneticNum;// 磁极个数

	float m_fAirGap[][] = new float[10][ROTOR_POLE_NUM + 20];/* 获取的磁极气隙数值 */
	//double RRadius;// 半径
	int numofChannelID;
	double m_ratedPace = 187.5;// ???????

	int m_nMaxAGIndex;// 最大气隙磁极号
	int m_nMinAGIndex;// 最小气隙磁极号

	boolean b_stator1 = true, b_stator2 = true, b_rotor1 = true,
			b_rotor2 = true, b_statorCircle = true, b_rotorCircle = true;// 控制是否显示一些图形元素
	int m_statorSel1 = 1, m_statorSel2 = 1, m_rotorSel1 = 1, m_rotorSel2 = 1;// 一些图形元素的参数
	float m_rotatorRadius=11288;
	float m_aigapdesign=26;
	
	public void setRotatorAndDesign(float r, float d)
	{
		m_rotatorRadius = r;
		m_aigapdesign = d;
	}
	public float[] getRotatorAndDesign()
	{
		float[] re = new float[2];
		re[0] = m_rotatorRadius;
		re[1] = m_aigapdesign;
		return re;
	}
	
	public OfflineScopeAirGap()
	{
		m_magneticNum = (int) (2 * 3000 / m_ratedPace);
		InitalCtrl();
		SimuPoleAirGapData();
//		SendData(m_fAirGap);//另外加的
		
	}

	public void SendData(float[][] m_fAirGapin)
	{
		m_fAirGap = m_fAirGapin.clone();
		updateUI();
	}

	public void Set_ifShow(int m_index, boolean b_in)// /设置是否显示一些图形元素
	{
		switch (m_index)
		{
		case 0:
			b_statorCircle = b_in;// /定子标准圆
			break;
		case 1:
			b_rotorCircle = b_in;// /转子标准圆
			break;
		case 2:
			b_stator1 = b_in;// /定子轮廓1
			break;
		case 3:
			b_stator2 = b_in;// /定子轮廓2
			break;
		case 4:
			b_rotor1 = b_in;// /转子轮廓1
			break;
		case 5:
			b_rotor2 = b_in;// //转子轮廓2
			break;
		}

	}

	public void Set_ShowSelect(int m_index, int m_in)// /设置显示图形参数
	{

		switch (m_index)
		{
		case 0:
			m_statorSel1 = m_in;
			break;
		case 1:
			m_statorSel2 = m_in;
			break;
		case 2:
			m_rotorSel1 = m_in;
			break;
		case 3:
			m_rotorSel2 = m_in;
			break;
		default:
			break;
		}

	}

	public void paint(Graphics g)
	{
		OnUpdateCurve(g);
	}

	void OnUpdateCurve(Graphics g)
	{
		m_rectClient = this.getBounds();
		int length = m_rectClient.width < m_rectClient.height ? m_rectClient.width
				: m_rectClient.height;
		m_rectPlot = new Rectangle((int) m_rectClient.getMinX() + 35,
				(int) m_rectClient.getMinY() + 35, length - 70, length - 70);
//		m_rectPlot = new Rectangle((int) m_rectClient.getMinX() ,
//				(int) m_rectClient.getMinY() , this.getWidth(), this.getHeight());

		double[] DRotorInfo1 = new double[10];
		double[] DStatorInfo1 = new double[6];
		double[] DRotorInfo2 = new double[10];
		double[] DStatorInfo2 = new double[6];
		double[] RCenCir = new double[4];
		double[] SCenCir = new double[4];

		GetRotorRoundness(0, DRotorInfo1, m_fAirGap); // 计算第一个测点选择的转子相关值
		GetRotorRoundness(0, DRotorInfo2, m_fAirGap); // 计算第二个测点选择的转子相关值

		GetStatorRoundness(0, DStatorInfo1, m_fAirGap); // 计算第一个磁极选择的定子相关值
		GetStatorRoundness(0, DStatorInfo2, m_fAirGap); // 计算第二个磁极选择的定子相关值

		// // Added by mzc 2011.5.18 9:45
		m_nMaxAGIndex = (int) DRotorInfo1[8];
		m_nMinAGIndex = (int) DRotorInfo1[9];

		double[][] AirGap = new double[AIRGAP_SENSOR_NUM][ROTOR_POLE_NUM];
		SCenCir[0] = DStatorInfo1[3]/* //圆心X位置 */;
		SCenCir[1] = DStatorInfo1[4];/* //圆心Y位置 */
		SCenCir[2] = DStatorInfo2[3];
		SCenCir[3] = DStatorInfo2[4];
		RCenCir[0] = DRotorInfo1[3];
		RCenCir[1] = DRotorInfo1[4];
		RCenCir[2] = DRotorInfo2[3];
		RCenCir[3] = DRotorInfo2[4];

		for (int i = 0; i < AIRGAP_SENSOR_NUM; i++)
			for (int j = 0; j < m_magneticNum; j++)
				AirGap[i][j] = (m_fAirGap[0][j]);// for sjh 2012.3.12

		int Num = 0;

		DrawFrame(g);

		if (b_rotor1)
			DrawRotorOutlineCurve(g, AirGap, RCenCir, m_rotorSel1, -1);// 显示转子轮廓

		if (b_rotor2)
			DrawRotorOutlineCurve(g, AirGap, RCenCir, -1, m_rotorSel2);

		if (b_stator1)/* 定子轮廓、转子轮廓 */
		{
			DrawStatorOutlineCurve(g, AirGap, SCenCir, m_statorSel1, -1);// 显示定子轮廓
			m_nPole = m_statorSel1;
		}

		if (b_stator2)
			DrawStatorOutlineCurve(g, AirGap, SCenCir, -1, m_statorSel2);

		if (b_statorCircle)
			Num = Num | 1;// 显示定子标准圆
		if (b_rotorCircle)
			Num = Num | 2;// 显示转子标准圆
		// ------------根据情况显示定子转子标准圆-----------
		if ((!b_stator1) && b_rotor1)
			DrawStandardCirCle(g, DStatorInfo2[5], DRotorInfo1[5], Num);
		else if (b_stator1 && (!b_rotor1))
			DrawStandardCirCle(g, DStatorInfo1[5], DRotorInfo2[5], Num);
		else if ((!b_stator1) && (!b_rotor1))
			DrawStandardCirCle(g, DStatorInfo2[5], DRotorInfo2[5], Num);
		else
			DrawStandardCirCle(g, DStatorInfo1[5], DRotorInfo1[5], Num);

		// //////////更新control面板信息
		DecimalFormat df = new DecimalFormat("0.00");
		if (b_stator1)
		{
//			m_ControlPanel.jTable1.setValueAt(df.format(DStatorInfo1[0]), 6, 1);// 定子不圆度
//			m_ControlPanel.jTable1.setValueAt(df.format(DStatorInfo1[1]), 7, 1);// 定子偏心距
//			m_ControlPanel.jTable1.setValueAt(df.format(DStatorInfo1[2]), 8, 1);// 定子中心偏移角
		}
		if (b_stator2)
		{
//			m_ControlPanel.jTable1.setValueAt(df.format(DStatorInfo2[0]), 6,1);// 定子不圆度
//			m_ControlPanel.jTable1.setValueAt(df.format(DStatorInfo2[1]), 7, 1);// 定子偏心距
//			m_ControlPanel.jTable1.setValueAt(df.format(DStatorInfo2[2]), 8, 1);// 定子中心偏移角

		}
		if (b_rotor1)
		{
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[0]), 9, 1);// 转子不圆度
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[1]), 10, 1);// 转子偏心距
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[2]), 11, 1);// 转子中心偏移角
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[6]), 0, 1);// 最大气隙
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[7]), 3, 1);// 最小气隙
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[8]), 1, 1);// 最大气隙磁极
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[9]), 4, 1);// 最小气隙磁极
			//m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[2]), 0, 8);
			//m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo1[2]), 0, 8);
/*
			m_ControlPanel.jLabel22.setText(df.format(DRotorInfo1[0]));
			m_ControlPanel.jLabel23.setText(df.format(DRotorInfo1[1]));
			m_ControlPanel.jLabel24.setText(df.format(DRotorInfo1[2]));
			m_ControlPanel.jLabel13.setText(df.format(DRotorInfo1[6]));
			m_ControlPanel.jLabel16.setText(df.format(DRotorInfo1[7]));
			m_ControlPanel.jLabel14.setText(df.format(DRotorInfo1[8]));
			m_ControlPanel.jLabel16.setText(df.format(DRotorInfo1[9]));*/

			String text = "";
			switch (m_rotorSel1 % MIN_AIRGAP_SENSOR_NUM)
			{
			case 0:
				text = "+Y";
				break;
			case 1:
				text = "+X+Y";
				break;
			case 2:
				text = "+X";
				break;
			case 3:
				text = "+X-Y";
				break;
			case 4:
				text = "-Y";
				break;
			case 5:
				text = "-X-Y";
				break;
			case 6:
				text = "-X";
				break;
			case 7:
				text = "-X+Y";
				break;
			default:
				break;
			}
//			m_ControlPanel.jTable1.setValueAt(text, 2, 1);// 最大气隙测点
//			m_ControlPanel.jTable1.setValueAt(text, 5, 1);// 最小气隙测点
			//m_ControlPanel.jLabel15.setText(text);
			//m_ControlPanel.jLabel18.setText(text);

		}
		if (b_rotor2)
		{
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[0]), 9, 1);// 转子不圆度
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[1]), 10, 1);// 转子偏心距
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[2]), 11, 1);// 转子中心偏移角
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[6]), 0, 1);// 最大气隙
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[7]), 3, 1);// 最小气隙
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[8]), 1, 1);// 最大气隙磁极
//			m_ControlPanel.jTable1.setValueAt(df.format(DRotorInfo2[9]), 4, 1);// 最小气隙磁极
			/*
			m_ControlPanel.jLabel22.setText(df.format(DRotorInfo2[0]));// 转子不圆度
			m_ControlPanel.jLabel23.setText(df.format(DRotorInfo2[1]));// 转子偏心距
			m_ControlPanel.jLabel24.setText(df.format(DRotorInfo2[2]));// 转子中心偏移角
			m_ControlPanel.jLabel13.setText(df.format(DRotorInfo2[6]));// 最大气隙
			m_ControlPanel.jLabel16.setText(df.format(DRotorInfo2[7]));// 最小气隙
			m_ControlPanel.jLabel14.setText(df.format(DRotorInfo2[8]));// 最大气隙磁极
			m_ControlPanel.jLabel16.setText(df.format(DRotorInfo2[9]));// 最小气隙磁极*/

			String text = "";
			switch (m_rotorSel2 % MIN_AIRGAP_SENSOR_NUM)
			{
			case 0:
				text = "+X";
				break;
			case 1:
				text = "-X";
				break;
			case 2:
				text = "+Y";
				break;
			case 3:
				text = "-Y";
				break;
			default:
				break;
			}
//			m_ControlPanel.jTable1.setValueAt(text, 2, 1);// 最大气隙测点
//			m_ControlPanel.jTable1.setValueAt(text, 5, 1);// 最小气隙测点
			
			//m_ControlPanel.jLabel15.setText(text);// 最大气隙测点
			//m_ControlPanel.jLabel18.setText(text);// 最小气隙测点
		}

	}

	void InitalCtrl()
	{
		float[] arr = { 4.0f, 0.0f };
		m_strokePlot[0] = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1.0f, arr, 0);
		m_strokePlot[1] = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1.0f, arr, 0);
		m_strokePlot[2] = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1.0f, arr, 0);
		m_strokePlot[3] = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1.0f, arr, 0);
		m_strokePlot[4] = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1.0f, arr, 0);
		m_strokePlot[5] = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1.0f, arr, 0);
		m_colorPlot[0] = Color.red;
		m_colorPlot[1] = Color.cyan;
		m_colorPlot[2] = Color.yellow;
		m_colorPlot[3] = Color.black;
		m_colorPlot[4] = Color.pink;
		m_colorPlot[5] = Color.blue;

	}

	void DrawStandardCirCle(Graphics g, double SRadius, double RRadius, int Num)// /绘制定子或转子的圆
	{
		Graphics2D g2 = (Graphics2D) g;

		int CCX, CCY;
		CCX = (int) m_rectPlot.getCenterX();
		CCY = (int) m_rectPlot.getCenterY();
		if ((Num & 1) > 0)
		{// 定子
			Stroke oldstroke = g2.getStroke();
			Color oldColor = g2.getColor();
			g2.setStroke(m_strokePlot[4]);// 设定线型
			g2.setColor(m_colorPlot[4]);// 设定颜色
			g2.drawOval((int) (CCX - m_rectPlot.getWidth() * 0.35),
					(int) (CCY - m_rectPlot.getWidth() * 0.35),
					(int) (0.7 * m_rectPlot.getWidth()),
					(int) (m_rectPlot.getWidth() * 0.7));

			// 以下恢复
			g2.setStroke(oldstroke);
			g2.setColor(oldColor);

		}
		if ((Num & 2) > 0)
		{// 转子
			Stroke oldstroke = g2.getStroke();
			Color oldColor = g2.getColor();
			g2.setStroke(m_strokePlot[5]);
			g2.setColor(m_colorPlot[5]);// 设定颜色

			g2.drawOval((int) (CCX - m_rectPlot.getWidth() * 0.25),
					(int) (CCY - m_rectPlot.getWidth() * 0.25),
					(int) (0.5 * m_rectPlot.getWidth()),
					(int) (m_rectPlot.getWidth() * 0.5));

			// 以下恢复
			g2.setStroke(oldstroke);
			g2.setColor(oldColor);
		}

	}

	void DrawFrame(Graphics g)// 绘制外窗口
	{
		// //////////////绘制定子外轮廓
		Graphics2D g2 = (Graphics2D) g;

		Color oldColor = g2.getColor();
		Stroke oldstroke = g2.getStroke();

		g2.setColor(new Color(0, 128, 255));
		g2.fillOval((int) m_rectPlot.getMinX(), (int) m_rectPlot.getMinY(),
				(int) m_rectPlot.getWidth(), (int) m_rectPlot.getHeight());

		g2.setColor(Color.black);
		g2.drawOval((int) m_rectPlot.getMinX(), (int) m_rectPlot.getMinY(),
				(int) m_rectPlot.getWidth(), (int) m_rectPlot.getHeight());

		g2.setColor(oldColor);
		g2.setStroke(oldstroke);

		// /////////////绘制圆心、x y
		Font oldfont = g2.getFont();
		g2.setFont(new Font("Arial", Font.PLAIN, 14));
		g2.setColor(Color.black);
		g2.drawString("+X", (int) m_rectPlot.getMaxX() + 10,
				(int) m_rectPlot.getCenterY() - 7);
		g2.drawString("-X", (int) m_rectPlot.getMinX() - 20,
				(int) m_rectPlot.getCenterY() - 7);
		g2.drawString("+Y", (int) m_rectPlot.getCenterX() + 7,
				(int) m_rectPlot.getMinY() - 10);
		g2.drawString("-Y", (int) m_rectPlot.getCenterX() + 12,
				(int) m_rectPlot.getMaxY() + 15);
		g2.drawLine((int) m_rectPlot.getCenterX() - 2,
				(int) m_rectPlot.getCenterY(),
				(int) m_rectPlot.getCenterX() + 3,
				(int) m_rectPlot.getCenterY());
		g2.drawLine((int) m_rectPlot.getCenterX(),
				(int) m_rectPlot.getCenterY() - 2,
				(int) m_rectPlot.getCenterX(),
				(int) m_rectPlot.getCenterY() + 3);
		g2.setFont(oldfont);

	}

	void GetRotorRoundness(int j, double[] RotorInfo, float[][] m_fAirGap)// 得到转子的圆度信息，包括不圆度、中心偏移量、中心偏移角
	{
			// 自己计算

		AirgapRotatorAnalyse rotaorA = new AirgapRotatorAnalyse();
		float[] temp = new float[m_magneticNum];
		for (int k = 0; k < m_magneticNum; k++)
		{
			temp[k] = m_fAirGap[j][k];

		}
		rotaorA.SetAirgapData(temp);
		rotaorA.SetStatorRadius(m_rotatorRadius+m_aigapdesign);
		// 将相应信息填写到转子信息数组
		RotorInfo[0] = rotaorA.m_roundness;// RRound; //转子不圆度
		RotorInfo[1] = rotaorA.m_offset;// ROffD; //转子偏心距
		RotorInfo[2] = rotaorA.m_angle;// ROffA/Math.PI*180; //转子中心偏移角
		RotorInfo[3] = rotaorA.Rx; // 圆心X位置
		RotorInfo[4] = rotaorA.Ry; // 圆心Y位置
		RotorInfo[5] = rotaorA.m_radius;// RRadius; //转子平均半径
		RotorInfo[6] = rotaorA.m_maxAirgapValue; // 最大气隙
		RotorInfo[7] = rotaorA.m_minAirgapValue; // 最小气隙
		RotorInfo[8] = rotaorA.PoleNoMax; // 最大气隙磁极号
		RotorInfo[9] = rotaorA.PoleNoMin; // 最小气隙磁极号

	}

	void GetStatorRoundness(int i, double[] StatorInfo, float m_fAirGap[][])// 得到定子的圆度信息，包括不圆度、中心偏移量、中心偏移角
	{
		
		// 自己计算
		AirGapStatorAnalyse StatorA = new AirGapStatorAnalyse();
		float[] temp = new float[m_fAirGap.length];
		for (int k = 0; k < m_fAirGap.length; k++)
		{
			temp[k] = m_fAirGap[k][i];
		}
		StatorA.SetAirgapData(temp);
		StatorA.SetRotatorRadius(m_rotatorRadius);
		StatorInfo[0] = StatorA.m_roundness;// SRound;//圆度，偏心距，偏心角，坐标x,y ，半径//
											// Added by mzc 2011.5.16 19:52
		StatorInfo[1] = StatorA.m_offset;// SOffD;
		StatorInfo[2] = StatorA.m_angle;// SOffA/Math.PI*180;
		StatorInfo[3] = StatorA.Sx;
		StatorInfo[4] = StatorA.Sy;
		StatorInfo[5] = StatorA.m_radius;// SRadius;

	}

	void SimuPoleAirGapData()// 模拟气隙数据
	{
		int RAND_MAX = 1;
		for (int i = 0; i < AIRGAP_SENSOR_NUM; i++)
			for (int j = 0; j < m_magneticNum; j++)
				m_fAirGap[i][j] = (float) (27 + 0.5 * Math.random() / RAND_MAX);

	}

	void DrawStatorOutlineCurve(Graphics g, double[][] Data, double[] SCenCir,
			int Pole1, int Pole2)// /绘制定子轮廓曲线
	{
		Graphics2D g2 = (Graphics2D) g;

		int i;
		int CCX, CCY;
		CCX = (int) m_rectPlot.getCenterX();// 绘图区中心// Added by mzc 2011.5.16
											// 20:44
		CCY = (int) m_rectPlot.getCenterY();
		double AvgValue1, AvgValue2;
		AvgValue1 = AvgValue2 = 0.0;
		if (Pole1 >= 0)
		{
			i = Pole1;
			// ------------
			AvgValue1 = 0.0;
			for (int kk = 0; kk < AIRGAP_SENSOR_NUM; kk++)
			{
				AvgValue1 = AvgValue1 + Data[kk][i];
			}
			AvgValue1 = AvgValue1 / AIRGAP_SENSOR_NUM;
			// ------------
			Stroke oldstroke = g2.getStroke();
			Color oldcolor = g2.getColor();
			g2.setStroke(m_strokePlot[2]);
			g2.setColor(m_colorPlot[2]);

			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue1 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue1 - 0.2)), 0, 90);

			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[2][i] / AvgValue1 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue1 - 0.2)), 90, 90);

			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[2][i] / AvgValue1 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue1 - 0.2)), 180, 90);

			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[2][i] / AvgValue1 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue1 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue1 - 0.2)), 270, 90);

			/*
			 * 四个象限，从第一象限依次绘制弧线，起落点顺序， 例如（(1,0), (0,1)） （(0,1), (-1,0)） （(-1,0),
			 * (0,-1)） （(0,-1), (1,0)）
			 */

			/* 下面划的线对应什么含义？绘制圆的四条短线及圆心 */
			g2.drawLine((int) (CCX + m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue1 - 0.2)), CCY,
					(int) (CCX + m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[0][i] / AvgValue1 - 0.2) - 8),
					CCY);
			g2.drawLine(CCX, (int) (CCY - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[1][i] / AvgValue1 - 0.2)), CCX,
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue1 - 0.2) + 8));

			g2.drawLine((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[2][i] / AvgValue1 - 0.2)), CCY,
					(int) (CCX - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[2][i] / AvgValue1 - 0.2) + 8),
					CCY);

			g2.drawLine(CCX, (int) (CCY + m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[3][i] / AvgValue1 - 0.2)), CCX,
					(int) (CCY + m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[3][i] / AvgValue1 - 0.2) - 8));

			g2.drawLine(
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[0]) - 2),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[1])),
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[0]) + 2),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[1])));

			g2.drawLine(
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[0])),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[1]) - 2),
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[0])),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[1]) + 2));

			g2.setStroke(oldstroke);
			g2.setColor(oldcolor);
		}
		if (Pole2 >= 0)
		{
			i = Pole2;
			// ------------
			AvgValue2 = 0.0;
			for (int kk = 0; kk < AIRGAP_SENSOR_NUM; kk++)
			{
				AvgValue2 = AvgValue2 + Data[kk][i];
			}
			AvgValue2 = AvgValue2 / AIRGAP_SENSOR_NUM;
			// ------------
			Stroke oldstroke = g2.getStroke();
			Color oldcolor = g2.getColor();
			g2.setStroke(m_strokePlot[3]);
			g2.setColor(m_colorPlot[3]);

			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue2 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue2 - 0.2)), 0, 90);
			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue2 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue2 - 0.2)), 90, 90);
			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue2 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue2 - 0.2)), 180, 90);
			g2.drawArc((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue2 - 0.2)),
					(int) (CCY - m_rectPlot.getWidth()
							* (0.35 + 0.2 * Data[1][i] / AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[0][i]
							/ AvgValue2 - 0.2)),
					(int) (2 * m_rectPlot.getWidth() * (0.35 + 0.2 * Data[1][i]
							/ AvgValue2 - 0.2)), 270, 90);

			g2.fillRect((int) (CCX + m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[0][i] / AvgValue2 - 0.2)) - 8, CCY, 8,
					16);
			g2.fillRect((int) (CCX - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[2][i] / AvgValue2 - 0.2)), CCY, 8, 16);
			g2.fillRect(CCX, (int) (CCY + m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[3][i] / AvgValue2 - 0.2)) - 8, 16, 8);
			g2.fillRect(CCX, (int) (CCY - m_rectPlot.getWidth()
					* (0.35 + 0.2 * Data[3][i] / AvgValue2 - 0.2)), 16, 8);

			g2.drawLine(
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[2]) - 2),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[3])),
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[2]) + 2),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[3])));
			g2.drawLine(
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[2])),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[3]) - 2),
					(int) (CCX + m_rectPlot.getWidth() / 60 * (SCenCir[2])),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (SCenCir[3]) + 2));

			g2.setStroke(oldstroke);
			g2.setColor(oldcolor);
		}
	}

	// 绘制定转子轮廓曲线
	void DrawRotorOutlineCurve(Graphics g, double[][] Data, double[] RCenCir,
			int Sensor1, int Sensor2)
	{
		Graphics2D g2 = (Graphics2D) g;
		int j, k;
		int CCX, CCY;
		CCX = (int) m_rectPlot.getCenterX();
		CCY = (int) m_rectPlot.getCenterY();
		double AvgValue1, AvgValue2;
		// // Added by mzc 2011.5.18 10:11
		Point[] pt = new Point[3];
		Point[] ptMin = new Point[3];

		if (Sensor1 >= 0)
		{
			j = Sensor1;
			// ------------
			AvgValue1 = 0.0;
			for (int kk = 0; kk < m_magneticNum; kk++)
			{
				AvgValue1 = AvgValue1 + Data[j][kk];
			}
			AvgValue1 = AvgValue1 / m_magneticNum;
			// ------------

			Stroke oldstroke = g2.getStroke();
			Color oldcolor = g2.getColor();
			g2.setStroke(m_strokePlot[3]);
			g2.setColor(m_colorPlot[3]);
			/*-------------------- mzc: code begin 2011-7-30 23:12:44 -----------------------*/
			int i = m_nPole;

			g2.setColor(new Color(255, 255, 255));
			g2.fillOval((int) (CCX - m_rectPlot.getWidth() * 0.35),
					(int) (CCY - m_rectPlot.getWidth() * 0.35),
					(int) (0.7 * m_rectPlot.getWidth()),
					(int) (m_rectPlot.getWidth() * 0.7));

			/*-------------------- mzc: code end 2011-7-30 23:12:44 -----------------------*/
			/************************ Added by mzc2011.5.17 13:57 *******************************/
			ArrayList ptList = new ArrayList();
			Point[] pPoint = null;

			Point line_prept = new Point(CCX
					+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1 * Data[j][0]
							/ AvgValue1 + 0.1)), CCY);

			// // Added by mzc 2011.5.17 14:0
			ptList.add((Object) (new Point(CCX
					+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1 * Data[j][0]
							/ AvgValue1 + 0.1)), CCY)));

			for (k = 1; k < m_magneticNum; k++)
			{
				// k 即Pole号 // Added by mzc 2011.5.18 9:51
				g2.drawLine(
						line_prept.x,
						line_prept.y,
						CCX
								+ (int) (m_rectPlot.getWidth()
										* (0.25 - 0.1 * Data[j][k] / AvgValue1 + 0.1) * Math
											.cos(2 * 3.1415926 / m_magneticNum
													* k)),
						CCY
								- (int) (m_rectPlot.getWidth()
										* (0.25 - 0.1 * Data[j][k] / AvgValue1 + 0.1) * Math
											.sin(2 * 3.1415926 / m_magneticNum
													* k)));
				line_prept = new Point(
						CCX
								+ (int) (m_rectPlot.getWidth()
										* (0.25 - 0.1 * Data[j][k] / AvgValue1 + 0.1) * Math.cos(2
										* 3.1415926 / m_magneticNum * k)),
						CCY
								- (int) (m_rectPlot.getWidth()
										* (0.25 - 0.1 * Data[j][k] / AvgValue1 + 0.1) * Math
											.sin(2 * 3.1415926 / m_magneticNum
													* k)));
				// 绘制最大、最小气隙对应的Pole号线// Added by mzc 2011.5.18 9:57
				if (k == m_nMaxAGIndex)
				{
					pt[0] = new Point(
							CCX
									+ (int) (m_rectPlot.getWidth()
											* (0.25 - 0.1 * Data[j][k]
													/ AvgValue1 + 0.1) * Math.cos(2
											* Math.PI / m_magneticNum * k)),
							CCY
									- (int) (m_rectPlot.getWidth()
											* (0.25 - 0.1 * Data[j][k]
													/ AvgValue1 + 0.1) * Math
												.sin(2 * Math.PI
														/ m_magneticNum * k)));
					pt[1] = (Point) ptList.get(ptList.size() - 1);
					pt[2] = new Point((pt[0].x + pt[1].x) / 2,
							(pt[0].y + pt[1].y) / 2);
				}

				if (k == m_nMinAGIndex)
				{
					ptMin[0] = new Point(
							CCX
									+ (int) (m_rectPlot.getWidth()
											* (0.25 - 0.1 * Data[j][k]
													/ AvgValue1 + 0.1) * Math.cos(2
											* 3.1415926 / m_magneticNum * k)),
							CCY
									- (int) (m_rectPlot.getWidth()
											* (0.25 - 0.1 * Data[j][k]
													/ AvgValue1 + 0.1) * Math
												.sin(2 * 3.1415926
														/ m_magneticNum * k)));
					ptMin[1] = (Point) ptList.get(ptList.size() - 1);
					ptMin[2] = new Point((ptMin[0].x + ptMin[1].x) / 2,
							(ptMin[0].y + ptMin[1].y) / 2);

				}

				// 填充区域的顶点// Added by mzc 2011.5.17 14:1
				ptList.add((Object) (new Point(
						CCX
								+ (int) (m_rectPlot.getWidth()
										* (0.25 - 0.1 * Data[j][k] / AvgValue1 + 0.1) * Math
											.cos(2 * 3.1415926 / m_magneticNum
													* k)),
						CCY
								- (int) (m_rectPlot.getWidth()
										* (0.25 - 0.1 * Data[j][k] / AvgValue1 + 0.1) * Math
											.sin(2 * 3.1415926 / m_magneticNum
													* k)))));
			}

			if (k == m_nMaxAGIndex)
			{
				pt[0] = new Point(CCX
						+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1
								* Data[j][0] / AvgValue1 + 0.1)), CCY);
				pt[1] = (Point) ptList.get(ptList.size() - 1);
				pt[2] = new Point((pt[0].x + pt[1].x) / 2,
						(pt[0].y + pt[1].y) / 2);

			}

			if (k == m_nMinAGIndex)
			{
				ptMin[0] = new Point(CCX
						+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1
								* Data[j][0] / AvgValue1 + 0.1)), CCY);
				ptMin[1] = (Point) ptList.get(ptList.size() - 1);
				ptMin[2] = new Point((ptMin[0].x + ptMin[1].x) / 2,
						(ptMin[0].y + ptMin[1].y) / 2);
			}

			g2.drawLine(line_prept.x, line_prept.y,
					CCX
							+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1
									* Data[j][0] / AvgValue1 + 0.1)), CCY);
			line_prept = new Point(CCX
					+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1 * Data[j][0]
							/ AvgValue1 + 0.1)), CCY);

			// 点链表转为点数组// Added by mzc 2011.5.17 14:2
			ptList.add((Object) (new Point(CCX
					+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1 * Data[j][0]
							/ AvgValue1 + 0.1)), CCY)));
			pPoint = new Point[ptList.size()];
			for (i = 0; i < ptList.size(); i++)
				pPoint[i] = (Point) ptList.get(i);

			g2.drawLine(
					(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[0]) - 2),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[1])),
					(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[0]) + 3),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[1])));

			g2.drawLine(
					(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[0])),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[1]) - 2),
					(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[0])),
					(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[1]) + 3));

			g2.setStroke(oldstroke);
			g2.setColor(oldcolor);
			// 气隙区域// Added by mzc 2011.5.17 19:48

			// 创建转子Pole形貌区域// Added by mzc 2011.5.17 14:
			Polygon rgn = new Polygon();
			for (i = 0; i < ptList.size(); i++)
				rgn.addPoint(pPoint[i].x, pPoint[i].y);

			g2.setColor(Color.gray);
			g2.fillPolygon(rgn);
			// 绘制Pole号线// Added by mzc 2011.5.18 10:9
			g2.setColor(Color.red);

			g2.drawLine(CCX, CCY, pt[2].x, pt[2].y);
			g2.setColor(Color.black);
			String strMax = String.format("磁极%d", m_nMaxAGIndex);
			g2.drawString(strMax, (pt[2].x + CCX) / 2, (pt[2].y + CCY) / 2);

			g2.setColor(Color.blue);
			g2.drawLine(CCX, CCY, ptMin[2].x, ptMin[2].y);
			g2.setColor(Color.black);
			String strMin = String.format("磁极%d", m_nMinAGIndex);

			g2.drawString(strMin, (ptMin[2].x + CCX) / 2,
					(ptMin[2].y + CCY) / 2);

			// // Added by mzc 2011.5.18 14:32 Pole号标签
			Point ptBase, ptEnd, ptHalf;
			ptBase = pPoint[0];
			int nCnt = ptList.size();

			if (nCnt > 2)
			{

				ptEnd = pPoint[nCnt - 2];// 倒数第二个点// Added by mzc 2011.5.18
											// 14:51

				ptHalf = pPoint[(nCnt - 1) / 2];
				g2.setColor(Color.black);
				String str = String.format("磁极%d", nCnt - 1);
				g2.drawString(str, (ptBase.x + ptEnd.x) / 2,
						(ptEnd.y + ptBase.y) / 2);

				str = String.format("磁极%d", 1);
				g2.drawString(str, (ptBase.x + pPoint[1].x) / 2,
						(pPoint[1].y + ptBase.y) / 2);

				int nHalf = (nCnt - 1) / 2;

				str = String.format("磁极%d", nHalf);// 链表的首尾处均有第一个点// mzc
													// 2011.5.18 14:57
				g2.drawString(str, (pPoint[nHalf].x + pPoint[nHalf - 1].x) / 2,
						(pPoint[nHalf].y + pPoint[nHalf - 1].y) / 2);

				str = String.format("气隙");

				g2.drawString(str,
						(pPoint[nHalf].x + pPoint[nHalf - 1].x) / 2 - 50,
						pPoint[nHalf - 1].y);

				str = "实测磁极形貌";

				g2.drawString(str, CCX - str.length() * 15, CCY);// ???

			}

			ptList.clear();

			if (Sensor2 >= 0)
			{
				j = Sensor2;
				// ------------
				AvgValue2 = 0.0;
				for (int kk = 0; kk < m_magneticNum; kk++)
				{
					AvgValue2 = AvgValue2 + Data[j][kk];
				}
				AvgValue2 = AvgValue2 / m_magneticNum;
				// ------------

				oldstroke = g2.getStroke();
				g2.setStroke(m_strokePlot[1]);
				Polygon p = new Polygon();
				p.addPoint(CCX
						+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1
								* Data[j][0] / AvgValue2 + 0.1)), CCY);

				for (k = 1; k < m_magneticNum; k++)
				{
					p.addPoint(
							CCX
									+ (int) (m_rectPlot.getWidth()
											* (0.25 - 0.1 * Data[j][k]
													/ AvgValue2 + 0.1) * Math
												.cos(2 * 3.1415926
														/ m_magneticNum * k)),
							CCY
									- (int) (m_rectPlot.getWidth()
											* (0.25 - 0.1 * Data[j][k]
													/ AvgValue2 + 0.1) * Math
												.sin(2 * 3.1415926
														/ m_magneticNum * k)));
				}
				p.addPoint(CCX
						+ (int) (m_rectPlot.getWidth() * (0.25 - 0.1
								* Data[j][0] / AvgValue2 + 0.1)), CCY);
				g2.drawPolygon(p);
				g2.drawLine(
						(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[2]) - 2),
						(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[3])),
						(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[2]) + 3),
						(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[3])));
				g2.drawLine(
						(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[2])),
						(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[3]) - 2),
						(int) (CCX + m_rectPlot.getWidth() / 60 * (RCenCir[2])),
						(int) (CCY - m_rectPlot.getWidth() / 60 * (RCenCir[3]) + 3));

				g2.setStroke(oldstroke);
			}

		}

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}

}
