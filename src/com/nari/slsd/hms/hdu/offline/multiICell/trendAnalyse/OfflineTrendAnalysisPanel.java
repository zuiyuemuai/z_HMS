package com.nari.slsd.hms.hdu.offline.multiICell.trendAnalyse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import jxl.CellView;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.algorithm.FFT;
import com.nari.slsd.hms.hdu.common.comtrade.CmtrCfgInfo;
import com.nari.slsd.hms.hdu.common.data.Complex;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.WorkSpaceProp;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 类名：OfflineTrendAnalysisPanel
 * 作用：实现离线分析中的趋势分析功能
 * @author HongLiChen
 *
 */
public class OfflineTrendAnalysisPanel extends JPanel
{

	// Excel文件的输出名字
	final String filepath = "1.xls";
	Color[] DefaultColors =
	ExtColor.getColors();
	
	Vector<CmtrCfgInfo> allCfgInfos = null;
	WorkSpaceProp workSpaceProp = null;
	// 用于存放数据通道的名字
	private String dataName;
	// 用于存放参考通道的名字
	private String refreName;
	private String[] unit;
	
	
	/**
	 * 计算平均值，有效值，峰峰值，最大值
	 * 
	 * @param workSpaceProp
	 * @param referChannel
	 *            参考通道
	 * @param dataChannel
	 *            数据通道
	 */
	public OfflineTrendAnalysisPanel(WorkSpaceProp workSpaceProp,
			String dataChannel, final String referChannel,String[] unit)
	{
		this.dataName = dataChannel;
		this.refreName = referChannel;
		this.unit = unit;
		// comtrade通道名字
		allCfgInfos = workSpaceProp.allWaveCfgInfos;
		this.workSpaceProp = workSpaceProp;

		float[] y = null;
		float[] x = null;
		float[] my = new float[allCfgInfos.size()];

		float[] wy = new float[allCfgInfos.size()];

		float[] ay = new float[allCfgInfos.size()];
		float[] ax = new float[allCfgInfos.size()];

		float[] ey = new float[allCfgInfos.size()];
		

		Vector<PlaneXY> messages = new Vector<PlaneXY>();// 原始数据

		// 初始化窗体
		setSize(800, 600);
		setLayout(new BorderLayout());
		
		
		// 进行数据的读取
		for (int k = 0; k < allCfgInfos.size(); k++)
		{
			String[] names = allCfgInfos.get(k).channelName;
			// 找到目标的通道
			for (int i = 0; i < names.length; i++)
			{

				if (dataChannel.equals(names[i]))
				{
					y = workSpaceProp.getWaveData(workSpaceProp.sectionName.get(k),//-1的原因是序号是从1开始的
							i);
				}
				if (referChannel.equals(names[i]))
				{
					x = workSpaceProp.getWaveData(workSpaceProp.sectionName.get(k),//-1的原因是序号是从1开始的
							i);
				}
			}
			messages.add(new PlaneXY(x, y));
			// 计算峰峰值
			float[] wavePeaky = Calculate.CalWavePeak(messages.get(k).getY());
			// 读取最大值
			my[k] = wavePeaky[0];
			// 读取峰峰值
			wy[k] = wavePeaky[2];
			// 读取平均值
			ax[k] = Calculate.getAve(messages.get(k).getX());
			ay[k] = Calculate.getAve(messages.get(k).getY());
			// 读取有效值
			ey[k] = Calculate.RMS(messages.get(k).getY());
		}

		LineChartPanel tempLineChartPanel = createChart(ax, my, wy, ay, ey);
		// 添加图表名字
		tempLineChartPanel.setTitle(workSpaceProp.testName+"_"+referChannel+"趋势分析图");
		JButton button = new JButton("ExcelTest");
		JButton creatWordBtn = new JButton(HduChartUtil.getResource("Common_CreatWord"));
		creatWordBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				creatWord();
			}
		});
		button.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				saveAsExcel(referChannel);
			}
		});
		// tempLineChartPanel.add(button,BorderLayout.SOUTH);
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.setBackground(Color.white);
	//	btnPanel.add(button);
		btnPanel.add(creatWordBtn);
		add(tempLineChartPanel, BorderLayout.CENTER);
		add(btnPanel,BorderLayout.NORTH);
//		test();
//		 saveAsExcel(referChannel);
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
		   File("趋势图报告")); 
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
		
		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel",savePath,"trendModel.ftl") {
			
			@Override
			public void getData(Map<String, Object> dataMap) {
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				dataMap.put("image", ImageChange.getImageEncode(getPanelSelf()));
				dataMap.put("datachannel", dataName);
				dataMap.put("refrechannel", refreName);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month", String.valueOf(now.get(Calendar.MONTH)+1));
				dataMap.put("date", String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}
	private void test()
	{
		JDialog jDialog = new JDialog();
		final JProgressBar aJProgressBar = new JProgressBar(0, 100);
		aJProgressBar.setStringPainted(true); // 显示百分比字符
		aJProgressBar.setIndeterminate(false); // 不确定的进度条
		aJProgressBar.setValue(0);
		jDialog.setSize(100, 100);
		jDialog.add(aJProgressBar);
		jDialog.setVisible(true);

		for (int i = 0; i < 100; i++)
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Dimension d = aJProgressBar.getSize();
			Rectangle rect = new Rectangle(0,0, d.width, d.height);
			aJProgressBar.setValue(i);
			aJProgressBar.paintImmediately(rect);
			
			/*
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					System.out.println(111);
					aJProgressBar.setValue(j);
				}
			});*/

		}
	}

	/**
	 * 用于创建图元
	 * @param ax 横坐标
	 * @param my 最大值
	 * @param wy 峰峰值
	 * @param ay 平均值
	 * @param ey 有效值
	 * @return
	 */
	private LineChartPanel createChart(float[] ax, float[] my, float[] wy,
			float[] ay, float[] ey)
	{

		PlaneXY wavePoints = new PlaneXY();
		PlaneXY maxPoints = new PlaneXY();
		PlaneXY AvePoints = new PlaneXY();
		PlaneXY effPoints = new PlaneXY();
		Vector<PlaneXY> resultMessages = new Vector<PlaneXY>();// 最后的结果数据

		maxPoints.setX(ax);
		maxPoints.setY(my);

		wavePoints.setX(ax);
		wavePoints.setY(wy);

		AvePoints.setX(ax);
		AvePoints.setY(ay);

		effPoints.setX(ax);
		effPoints.setY(ey);

		resultMessages.add(effPoints);
		resultMessages.add(maxPoints);
		resultMessages.add(AvePoints);
		resultMessages.add(wavePoints);

		String[] channelnames =
		{ "有效值", "最大值", "平均值", "峰峰值" };
		LineChartPanel lineChartPanel = new LineChartPanel();
		lineChartPanel.upAutSeriesData(channelnames, resultMessages,
				DefaultColors, false);
		lineChartPanel.setXLable(unit[0]);
		lineChartPanel.setYLable(unit[1]);
		lineChartPanel.chartPanel.addMouseMotionListener(new TrendMouseListener(lineChartPanel));
		
		lineChartPanel.setCloseSeriesVisibleInLegend(TrendMouseListener.FLOAT_RANGE);
		
		lineChartPanel.setMargin5();//added by lqj for bug 1174 趋势分析图中的游标值显示问题
		
		return lineChartPanel;
	}

	/**
	 * FFT转换
	 * @param sy待转换 数组
	 * @return 转换后的复数结果
	 */
	private Complex[] FFT(float sy[]){
		int N = sy.length;
		Complex[] x = new Complex[N];
		// original data
		for (int i = 0; i < N; i++)
		{
			x[i] = new Complex(sy[i], 0);
		}
		return FFT.fft(x);
	}
	
	
	/**
	 * 获得模
	 * @param ffty
	 * @param N 点数
	 * @return
	 */
	private float[] ABS(Complex[] ffty,int N)
	{
		float abs[] = new float[N];
		for (int i = 0; i < N; i++)
		{
			abs[i] = (float) ffty[i].abs();
			abs[i] = abs[i] * 2 / N;
		}
		abs[0] = abs[0] / 2;

		float[] result = new float[N / 2];
		System.arraycopy(abs, 0, result, 0, N / 2);

		return result;
	}
	
	
	/**
	 * 计算距i最近的2^N之差
	 * @param i 需要判断的数字
	 * @return 返回离最近的2^N之差
	 */
	private  int Square(int i){
		
		int result = 2;

		while(i > result){
			result *= 2;
		}
		return result-i;
	}
	
	/**
	 * 保存为excel表格的形式
	 * @param referChannel 参考通道的名字
	 */
	public void saveAsExcel(String referChannel)
	{

		float[] x = null;
		float[] y = null;
		float[] my = new float[allCfgInfos.size()];
		float[] ny = new float[allCfgInfos.size()];

		float[] wy = new float[allCfgInfos.size()];

		float[] ay = new float[allCfgInfos.size()];
		float[] ax = new float[allCfgInfos.size()];

		float[] ey = new float[allCfgInfos.size()];
		
		float fft[][] = new float[allCfgInfos.size()][];
		int fftindex[][] = new int[allCfgInfos.size()][];
		float phase[][]  = new float[allCfgInfos.size()][8];
		float frequent = allCfgInfos.get(0).smprateRate;
		float frequents[][]  = new float[allCfgInfos.size()][8];
		
		Complex[][] complexArray = new Complex[allCfgInfos.size()][];
		
		
		JDialog jDialog = new JDialog();
		final JProgressBar aJProgressBar = new JProgressBar(0, 100);
		aJProgressBar.setStringPainted(true); // 显示百分比字符
		aJProgressBar.setIndeterminate(false); // 不确定的进度条
		aJProgressBar.setValue(0);
		jDialog.setSize(100, 100);
		jDialog.add(aJProgressBar);
		jDialog.setVisible(true);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		jDialog.setLocation(width / 2 - 100, height / 2 - 100);
		
		Dimension d = aJProgressBar.getSize();
		Rectangle rect = new Rectangle(0,0, d.width, d.height);
		
		Vector<PlaneXY> messages = new Vector<PlaneXY>();// 原始数据

		ExcelUtil createExcel = new ExcelUtil();
		WritableWorkbook workbook = null;
		try
		{
			workbook = createExcel.createExcel(filepath);
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 名称是否会不同?
		String[] names = allCfgInfos.get(0).channelName;
		// 查找出参考通道的序号
		int referindex = 0;
		for (referindex = 0; referindex < names.length; referindex++)
		{
			if (referChannel.equals(names[referindex]))
			{
				break;
			}
		}
	
		for (int k = 0; k < allCfgInfos.size(); k++)
		{
			x = workSpaceProp.getWaveData(workSpaceProp.sectionName.get(k),referindex);//-1的原因是序号是从1开始的
			ax[k] = Calculate.getAve(x);
			/*
			int length = Square(x.length);
			float[] tempArray = new float[x.length+length];
			System.arraycopy(x, 0, tempArray, 0, x.length);
			// 读取平均值
		
			fft[k] = FFT(tempArray,complexArray[k]);
			fftindex[k] = Calculate.orderMaxtoMin(fft[k]);
			
			for (int i = 0; i < 8; i++)
			{
				// 相位
				phase[k][i] = (float) (complexArray[k][i].phase() * 180/ Math.PI);
				
				// 频率
			}*/
		}
		

		// 每个通道的数据
		for (int i = 0; i < names.length; i++)
		{
			messages.clear();
			// 1,2,3分拣后的组号
			for (int k = 0; k < allCfgInfos.size(); k++)
			{
				y = workSpaceProp.getWaveData(workSpaceProp.sectionName.get(k), i);//-1的原因是序号是从1开始的
				// 计算峰峰值
				float[] wavePeaky = Calculate.CalWavePeak(y);
				// 读取最大值
				my[k] = wavePeaky[0];
				// 读取最小值
				ny[k] = wavePeaky[1];
				// 读取峰峰值
				wy[k] = wavePeaky[2];
				// 读取平均值
				ay[k] = Calculate.getAve(y);
				// 读取有效值
				ey[k] = Calculate.RMS(y);
				
				
				int length = Square(y.length);
				float[] tempArray = new float[y.length+length];
				System.arraycopy(y, 0, tempArray, 0, y.length);
				complexArray[k] = FFT(tempArray);// 改动了
				fft[k] = ABS(complexArray[k],tempArray.length);// 动了
				fftindex[k] = Calculate.orderMaxtoMin(fft[k]);
		
				float N = (float) (1.0 * frequent / y.length);
				for (int l = 0; l < 8; l++)
				{
					// 相位
					phase[k][l] = (float) (complexArray[k][fftindex[k][l]].phase() * 180/ Math.PI);
					frequents[k][l] = fftindex[k][l]*N;
				}
				
			}
			try
			{
				WritableSheet sheet = ExcelUtil.createSheet(workbook, names[i], i);
				// 写入属性
				setAttribute(sheet);
				// 实验类型
				ExcelUtil.addString(sheet, 0, 1, workSpaceProp.testName);
				// 当前通道
				ExcelUtil.addString(sheet, 0, 3, names[i]);
				// 当前通道合并单元格
				sheet.mergeCells(3, 2, 8, 2);
				ExcelUtil.addString(sheet, 2, 3, names[i]);
				// 参考通道
				ExcelUtil.addString(sheet, 0, 5, names[referindex]);
				// 频率
				ExcelUtil.addData(sheet, 0, 7, frequent);
				
				// 通道的属性
				for (int j = 0; j < allCfgInfos.size(); j++)
				{
					// 序号
					ExcelUtil.addData(sheet, 4+j, 0, j+1);
					// 参考通道
					

					ExcelUtil.addString(sheet, 4+j, 1, (workSpaceProp.sectionName).get(j)+"");//-1的原因是序号是从1开始的
					// 有功功率
					ExcelUtil.addData(sheet, 4+j, 2, ax[j]);
					// 最小值
					ExcelUtil.addData(sheet, 4+j, 3, ny[j]);
					// 最大值
					ExcelUtil.addData(sheet, 4+j, 4, my[j]);
					// 平均值
					ExcelUtil.addData(sheet, 4+j, 5, ay[j]);
					// 中间值
					ExcelUtil.addData(sheet, 4+j, 6, my[j]);
					// 有效值
					ExcelUtil.addData(sheet, 4+j, 7, ey[j]);
					// 峰峰值
					ExcelUtil.addData(sheet, 4+j, 8, wy[j]);
					
					for (int l = 0; l < 8; l++)
					{
						// 频率
						ExcelUtil.addData(sheet, 4+j, 9+l*3, frequents[j][l]);
						// 幅值
						ExcelUtil.addData(sheet, 4+j, 9+l*3+1, fft[j][fftindex[j][l]]);
						// 相位
						ExcelUtil.addData(sheet, 4+j, 9+l*3+2, phase[j][l]);
					}
				}
				// 输出图表
				LineChartPanel tempchart = createChart(ax, my, wy, ay, ey);
				aJProgressBar.setValue(100*i/names.length);
				aJProgressBar.paintImmediately(rect);
				add(tempchart, BorderLayout.CENTER);
				ExcelUtil.saveChartAsPNG(tempchart.getChart(), sheet);
				ExcelUtil.setColSize(sheet, 32, 12);
			} catch (RowsExceededException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		        	   
		}
		try
		{
			// 保存文件
			ExcelUtil.saveFile(workbook);
			jDialog.dispose();
		} catch (WriteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String[] string1 = {"实验类型","当前通道","参考通道","频率(HZ)"};
	private String[] string2 =
	{ 
	"序号", "参考通道", "有功功率", "最小值", "最大值", "平均值", "中间值", "有效值", "峰峰值", 
	"频率1","幅值1", "相位1", "频率2", "幅值2", "相位2", "频率3", "幅值3", "相位3", "频率4",
	"幅值4", "相位4", "频率5", "幅值5", "相位5", "频率6", "幅值6", "相位6", "频率7","幅值7", "相位7","频率8","幅值8", "相位8" 
	};

	/**
	 * 初始化输出excel的属性
	 * @param sheet 要初始化的表格对象
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void setAttribute(WritableSheet sheet) throws RowsExceededException, WriteException{
		for (int i = 0; i < string1.length; i++)
		{
			ExcelUtil.addString(sheet, 0, 2*i, string1[i],true);
		}
		
		for (int i = 0; i < string2.length; i++)
		{
			ExcelUtil.addString(sheet, 3, i, string2[i],true);
		}

	}
}
