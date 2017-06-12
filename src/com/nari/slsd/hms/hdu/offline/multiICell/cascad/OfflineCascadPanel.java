package com.nari.slsd.hms.hdu.offline.multiICell.cascad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import com.jogamp.opengl.util.HduDisplay;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.util.ExtColor;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.HduCreatWord;
import com.nari.slsd.hms.hdu.common.util.ImageChange;
import com.nari.slsd.hms.hdu.common.util.ScreenShots;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.PropDialog;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;

/**
 * 南瑞水电站监护系统离线级联界面 界面中3D图像，一个平台上横向显示频率,纵向显示转速，表示在某一个转速下的频谱图
 * 根据每个的转速信息画出级联线，初始共16级，可以通过右击标题可以设定
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class OfflineCascadPanel extends JPanel
{
	// protected static ResourceBundle localizationResources =
	// ResourceBundleWrapper
	// .getBundle(PropertiesPATH.LocalizationBundle);

	private static String CMD_SAVE = "save";// 保存
	private static String CMD_COUNT = "count";// 设置倍频数
	private static String CMD_SETSECTION = "setSection";// 设置区间

	// private Font titlefont = PropertiesUtil.titlefont;
	// private Font textfont = PropertiesUtil.textfont;

	private Vector<PlaneXY> dataIn = null;// 频谱信息
	private float[] rev = null;// 对应转速
	private int[] sort = null;// 转速从小到大排序
	private float maxRev = 0;// 最大转速
	private float maxAmp = 0;// 最大幅值
	private float maxFre = 0;// 最大频率

	private String dataName;
	private String refreName;
	private CascdChart3DCanvas canvas;
	private JButton creatWordBtn = new JButton(
			HduChartUtil.getResource("Common_CreatWord"));

	/**
	 * 姿态图初始化 获取画布，设置鼠键控制
	 * */
	public OfflineCascadPanel(String revname, String dataname, float[] rev,
			Vector<PlaneXY> dataIn)
	{
		this.dataName = dataname;
		this.refreName = revname;
		canvas = new CascdChart3DCanvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		this.add(
				getTitleJPanel(HduChartUtil.getResource("OfflineCascad_Cascad")
						+ "(" + revname + "-" + dataname + ")"),
				BorderLayout.NORTH);
		
		try
		{
			this.rev = rev.clone();
			this.dataIn = (Vector<PlaneXY>) dataIn.clone();
			dataAnalyse(rev, dataIn);
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();

		}

		canvas.updata(this.rev, this.dataIn, maxRev, maxAmp, maxFre);

	}

	private JPanel getTitleJPanel(String Title)
	{
		JPanel titleJPanel = new JPanel(new GridBagLayout());
		titleJPanel.setBackground(java.awt.Color.white);
		GridBagUtil.addBlankJLabel(titleJPanel, 0, 0, 8, 1);
		JLabel title = new JLabel(Title);
		 title.setFont(new Font("微软雅黑", Font.BOLD, 18));
		GridBagUtil.setLocation(titleJPanel, title, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(titleJPanel, 2, 0, 8, 1);

		GridBagUtil.setLocation(titleJPanel, creatWordBtn, 3, 0, 2, 2, true);
		GridBagUtil.addBlankJLabel(titleJPanel, 4, 0, 1, 1);

		
		title.setComponentPopupMenu(createPopupMenu());
		
		
		creatWordBtn.addActionListener(creatWordListener);
		//this.add(creatWordBtn, BorderLayout.SOUTH);
		
		
		return titleJPanel;
	}

	/**
	 * 创建弹出菜单
	 * 
	 * @return
	 */
	private JPopupMenu createPopupMenu()
	{
		JPopupMenu jpopupMenu = new JPopupMenu();

		JMenuItem saveItem = new JMenuItem(
				HduChartUtil.getResource("OfflineCascad_Save"));
		saveItem.setActionCommand(CMD_SAVE);
		saveItem.addActionListener(canvas);
		jpopupMenu.add(saveItem);

		JMenuItem countItem = new JMenuItem(
				HduChartUtil.getResource("OfflineCascad_SetCascadNum"));
		countItem.setActionCommand(CMD_COUNT);
		countItem.addActionListener(canvas);
		jpopupMenu.add(countItem);

		JMenuItem sectionItem = new JMenuItem(
				HduChartUtil.getResource("OfflineCascad_SetSection"));
		sectionItem.setActionCommand(CMD_SETSECTION);
		sectionItem.addActionListener(canvas);
		jpopupMenu.add(sectionItem);

		return jpopupMenu;

	}

	// 对于数据进行分析技计算出最大最小阈值
	// 并对数据进行放大缩小，x轴2，z轴1，y轴0.5
	// 对于z轴 z = 1-2*x/max
	// 对于x轴 x = 4*x/max-2
	// 对于y轴 y = 0.5*x/max
	private void dataAnalyse(float[] rev, Vector<PlaneXY> dataIn)
	{
		maxRev = (Calculate.findMaxMin(rev))[1] * 1.05f;// 最大值
		sort = new int[rev.length];
		float[] sortdata = rev.clone();
		for (int i = 0; i < rev.length; i++)
		{
			maxFre = Math.max(maxFre,
					(Calculate.findMaxMin(dataIn.get(i).getX()))[1]);
			maxAmp = Math.max(maxAmp,
					(Calculate.findMaxMin(dataIn.get(i).getY()))[1]);
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

		// this.rev = div(rev, maxRev, -2, 1);
		// for (int i = 0; i < rev.length; i++)
		// {
		// dataIn.get(i).setX(div(dataIn.get(i).getX(), maxFre, 4, -2));
		// dataIn.get(i).setY(div(dataIn.get(i).getY(), maxAmp, 0.5f, 0));
		// }

	}

	private float[] div(float[] d, float v, float a, float b)
	{
		float[] re = new float[d.length];
		for (int i = 0; i < d.length; i++)
		{
			re[i] = a * d[i] / v + b;
		}
		return re;
	}

	public JPanel getPanelSelf()
	{
		return this;
	}

	private void creatWord()
	{
		String savePath = "";
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setDialogType(jFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle(HduChartUtil
				.getResource("Common_ChooseSavePath"));
		jFileChooser.setSelectedFile(new File(HduChartUtil
				.getResource("OfflineCasca_Word")));
		jFileChooser.setMultiSelectionEnabled(false);
		int returnVal = jFileChooser.showSaveDialog(jFileChooser);
		if (returnVal != JFileChooser.APPROVE_OPTION)// 判断对话框是否选择“取消”
		{
			savePath = null;
			return;
		} else
		{
			savePath = jFileChooser.getSelectedFile().getPath() + ".doc";
		}

		HduCreatWord hduCreatWord = new HduCreatWord("//wordModel", savePath,
				"cascadModel.ftl")
		{

			@Override
			public void getData(Map<String, Object> dataMap)
			{
				// TODO Auto-generated method stub
				Calendar now = Calendar.getInstance();
				Point imagePoint = canvas.getLocationOnScreen();
				dataMap.put("image", ImageChange.get3DImageEncode(
						(int) imagePoint.getX(), (int) imagePoint.getY(),
						canvas.getWidth(), canvas.getHeight()));
				dataMap.put("datachannel", dataName);
				dataMap.put("refrechannel", refreName);
				dataMap.put("year", String.valueOf(now.get(Calendar.YEAR)));
				dataMap.put("month",
						String.valueOf(now.get(Calendar.MONTH) + 1));
				dataMap.put("date",
						String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
			}
		};
	}

	public ActionListener creatWordListener = new ActionListener()
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			creatWord();
		}
	};

	private static class CascdChart3DCanvas extends GLCanvas implements
			ActionListener
	{
		private Vector<PlaneXY> dataIn = null;// 频谱信息
		private float[] rev = null;// 对应转速

		private float maxRev = 1;// 最大转速
		private float maxAmp = 1;// 最大幅值
		private float maxFre = 1;// 最大频率

		// 需要显示部分实际最大最小
		private float nowXmax = 0;
		private float nowXmin = 0;
		private float nowYmax = 0;
		private float nowYmin = 0;

		private int cascadLineNum = 16;// 级联线的个数

		private int FPS = 30;// 帧数

		private float yrot = -0;// Y轴上的旋转量 0z轴正对
		private float xzrot = 60;// xz平面俯仰角度
		private float deep = 66;// 视角

		protected GLRender listener;
		protected HduDisplay animator = null;
		protected static GLCapabilities glcaps = new GLCapabilities(null);

	

		/**
		 * 姿态图初始化 获取画布，设置鼠键控制
		 * */
		public CascdChart3DCanvas()
		{
			super(glcaps);

			animator = new HduDisplay(this);// 设置动画

			listener = new GLRender();// 得到监听器
			this.addGLEventListener(listener);

			MouseListener mouseListener = new MouseListener(listener);
			this.addMouseMotionListener(mouseListener);// 拖动
			this.addMouseListener(mouseListener);// 单击
			this.addMouseWheelListener(mouseListener);// 滑轮
		}

		/**
		 * 数据更新 // 最大转速 // 最大幅值 // 最大频率
		 * 
		 * @param dataIn
		 *            数据
		 * @param normalization
		 *            归一化参数
		 */

		public void updata(float[] rev, Vector<PlaneXY> dataIn, float maxRev,
				float maxAmp, float maxFre)
		{
			this.dataIn = dataIn;
			this.rev = rev;
			this.maxRev = maxRev;
			this.maxAmp = maxAmp;
			this.maxFre = maxFre;
			this.nowXmax = maxFre;
			this.nowYmax = maxRev;
		}

		/*
		 * 姿态图的鼠键控制
		 */
		class MouseListener extends MouseAdapter
		{

			private GLRender glRender;
			private float lastx = -1, lasty = -1;

			// 初始化
			public MouseListener(GLRender glRender)
			{
				super();
				this.glRender = glRender;
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub
				super.mousePressed(e);

			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				float x, y;

				if (lastx == -1 && lasty == -1)// 如果是第一次进入则赋值
				{
					lastx = e.getX();
					lasty = e.getY();
					return;
				}

				x = e.getX();
				y = e.getY();

				yrot += (lastx - x) / 10;

				xzrot -= (lasty - y) / 10;

				lastx = x;
				lasty = y;

				animator.hduDisplay();
				// System.out.println("y"+yrot+"z"+xzrot);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub
				lastx = -1;
				lasty = -1;// 特殊标志

			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				// TODO Auto-generated method stub

				deep += e.getWheelRotation() * 2;
				animator.hduDisplay();
				// System.out.println("Deep"+deep);
			}

		}

		/***
		 * 渲染
		 * 
		 * @author Administrator
		 * 
		 */
		class GLRender implements GLEventListener
		{

			private GLU glu = new GLU();
			private float h = 0;// 界面长宽比
			private Bsipic bsipic;

			private TextRenderer textRenderer = new TextRenderer(new Font("宋体",
					Font.BOLD, 16), true, true);// text渲染器

			private int width = 0;
			private int height = 0;

			// 环境光参数
			private float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
			/**
			 * 下一行代码我们生成最亮的漫射光。所有的参数值都取成最大值1.0f。 它将照在我们木板箱的前面，看起来挺好。
			 */
			private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
			/**
			 * 最后我们保存光源的位置。 前三个参数和glTranslate中的一样。依次分别是XYZ轴上的位移。
			 * 由于我们想要光线直接照射在木箱的正面，所以XY轴上的位移都是0.0f。 第三个值是Z轴上的位移。为了保证光线总在木箱的前面，
			 * 所以我们将光源的位置朝着观察者(就是您哪。)挪出屏幕。 我们通常将屏幕也就是显示器的屏幕玻璃所处的位置称作Z轴的0.0f点。
			 * 所以Z轴上的位移最后定为2.0f。假如您能够看见光源的话，它就浮在您显示器的前方。
			 */
			private float[] lightPosition = { 0.0f, 0.0f, 1f, 1.0f };
			
			public Bsipic getBsipic()
			{
				return bsipic;
			}

			public GLRender()
			{
				// TODO Auto-generated constructor stub

			}

			@Override
			public void init(GLAutoDrawable drawable)
			{

				GL2 gl = drawable.getGL().getGL2();

				bsipic = new Bsipic(gl);

				gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

				gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f); // Black Background

				// 启用深度缓存
				gl.glClearDepth(1.0f);

				// 启用深度测试
				gl.glEnable(GL.GL_DEPTH_TEST);
				// 所作的深度测试的类型
				gl.glDepthFunc(GL.GL_LEQUAL);
				// 真正的精细的透视修正
				gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
				// 启用纹理映射
			//	gl.glEnable(GL.GL_TEXTURE_2D);

				gl.glEnable(GL.GL_BLEND); // Turn Blending On
				gl.glDisable(GL.GL_DEPTH_TEST); // Turn Depth Testing Off

				// 设置光源
				/**
				 * 现在开始设置光源。下面下面一行设置环境光的发光量，光源light1开始发光。
				 * 这一课的开始处我们我们将环境光的发光量存放在LightAmbient数组中。 现在我们就使用此数组(半亮度环境光)。在int
				 * InitGL(GLvoid)函数中添加下面的代码。
				 */
				// 设置环境光
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, this.lightAmbient, 0);
				// 设置漫射光
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, this.lightDiffuse, 0);
				// 设置光源位置
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, this.lightPosition, 0);
				// 启用一号光源
				gl.glEnable(GL2.GL_LIGHT1);
				// 我们启用GL_LIGHTING，所以您看见任何光线
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glEnable(GL2.GL_COLOR_MATERIAL); // 使用颜色材质
				
			}

			@Override
			public void display(GLAutoDrawable drawable)
			{
				final GL2 gl = drawable.getGL().getGL2();
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // 娓呴櫎灞忓箷鍜屾繁搴︾紦瀛�

				// Reset projection matrix stack
				gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
				// 重置模型观察矩阵堆栈
				gl.glLoadIdentity();

				// 透视投影 眼角度,比例,近可视,远可视
				glu.gluPerspective(deep, h, 0.01, 400.0);

				gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
				gl.glLoadIdentity();

				bsipic.drawPostureBackground();

			}

			@Override
			/** 
			 *
			 * @param glDrawable 
			 * @param x 
			 * @param y 
			 * @param width 
			 * @param height 
			 */
			public void reshape(GLAutoDrawable glDrawable, int x, int y,
					int width, int height)
			{
				// TODO Auto-generated method stub
				final GL2 gl = glDrawable.getGL().getGL2();
				// 防止为零
				if (height == 0) // avoid a divide by zero error!
					height = 1;
				h = (float) width / height;
				// 视口设置为窗口尺寸
				gl.glViewport(0, 0, width, height);
				// Reset projection matrix stack
				gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
				// 重置模型观察矩阵堆栈
				gl.glLoadIdentity();
				// 透视投影 眼角度,比例,近可视>0,远可视
				glu.gluPerspective(45.0f, h, 0.01, 400.0);

				gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
				gl.glLoadIdentity();

				this.width = width;
				this.height = height;
			}

			@Override
			public void dispose(GLAutoDrawable arg0)
			{

			}
		}

		/**
		 * 画图类
		 * 
		 * @author Administrator
		 * 
		 */
		class Bsipic
		{

			private GL2 gl;

			// OpenGL绘制工具包类
			private GLU glu = new GLU();
			private GLUT glut = new GLUT();
			private TextRenderer textRenderer = new TextRenderer(new Font("微软雅黑",
					Font.BOLD, 16), true, true);// text渲染器

			public Bsipic(GL2 gl)
			{
				this.gl = gl;
			}

			// 绕x轴旋转
			public void spin()
			{
				gl.glLoadIdentity(); // 重置模型观察矩阵
				gl.glTranslatef(0.0f, 0.2f, -3); // 深度

				float z = (float) Math.tan(yrot / 180 * Math.PI);// 获取xz平面上下变换的线

				gl.glRotatef(yrot, 0.0f, -1.0f, 0.0f);// 旋转
				gl.glRotatef(xzrot, 1.0f, 0.0f, -z);// 旋转zx平面向下倾斜30度

			}

			// 画姿态的平面背景
			public void drawPostureBackground()
			{

				gl.glLoadIdentity(); // 重置模型观察矩阵
				gl.glTranslatef(-0.2f, 0.2f, -3); // 深度
				gl.glLineWidth(0.05f);

				textRenderer.setColor(Color.black);
				gl.glRotatef(-90, 1.0f, 0.0f, 0);// 旋转zx平面向下倾斜度
				float z = (float) Math.tan(yrot / 180 * Math.PI);// 获取xz平面上下变换的线

				gl.glRotatef(xzrot, 1.0f, 0.0f, -z);// 旋转zx平面向下倾斜30度
				textRenderer.begin3DRendering();
				textRenderer.draw3D(HduChartUtil
						.getResource("OfflineCascad_Label_Frequenty"), 0,
						-1.3f, 0f, 0.005f);
				textRenderer.draw3D(
						HduChartUtil.getResource("OfflineCascad_Label_Rev"),
						-2.4f, 0f, 0, 0.005f);
				textRenderer.end3DRendering();

				spin();
				drawPlane(0, Color.gray);

				for (int i = 0; i < rev.length; i++)
				{
					drawPanelLine(dataIn.get(i).getX(), dataIn.get(i).getY(),
							rev[i], (1 - rev[i]) / 2 * maxRev);
				}
				drawCascaLine();
			}

			// 画级联线
			private void drawCascaLine()
			{

				float ybase = -2 / (nowYmax - nowYmin);
				float xbase = 4 / (nowXmax - nowXmin);

				float x_L = nowYmin / 60;
				float x_H = nowYmax / 60;
				int i = 1;
				for (i = (int) (nowXmin / x_H); i <= (nowXmax) / x_H
						&& i <= cascadLineNum; i++)
				{
					float x1 = xbase * (x_L * i - nowXmin) - 2;
					float y1 = ybase * (nowYmin - nowYmin) + 1;
					float x2 = xbase * (x_H * i - nowXmin) - 2;
					float y2 = ybase * (nowYmax - nowYmin) + 1;
					float k = (y2 - y1) / (x2 - x1);

					float y = k * (-2 - x1) + y1;
					if (y < -1)
						continue;
					float _x = (1 - y1 + k * x1) / k;
					float _y = k * (-2 - x1) + y1;
					if (_y > 1)
						_y = 1;

					drawLine(_x, 0, _y, xbase * (x_H * i - nowXmin) - 2, 0,
							ybase * (nowYmax - nowYmin) + 1, ExtColor.getCascadColor());
					gl.glRasterPos3f(xbase * (x_H * i - nowXmin) - 2, 0f, ybase
							* (nowYmax - nowYmin) + 0.9f);// 显示文字的地方
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,// 字的大小
							i + "x");// 显示的内容
				}
				for (; i <= cascadLineNum; i++)
				{
					float x1 = xbase * (x_L * i - nowXmin) - 2;
					float y1 = ybase * (nowYmin - nowYmin) + 1;
					float x2 = xbase * (x_H * i - nowXmin) - 2;
					float y2 = ybase * (nowYmax - nowYmin) + 1;

					float k = (y2 - y1) / (x2 - x1);
					float y = k * (2 - x1) + y1;
					if (y > 1)
						continue;

					float _y = k * (-2 - x1) + y1;
					if (_y > 1)
						_y = 1;
					float _x = (1 - y1 + k * x1) / k;

					drawLine(_x, 0, _y, 2, 0, y, ExtColor.getCascadColor());

					gl.glRasterPos3f(2.1f, 0f, y);// 显示文字的地方
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,// 字的大小
							i + "x");// 显示的内容
				}

			}

			/**
			 * 画一个频谱
			 * 
			 * @param xvalue
			 *            频率
			 * @param zvalue
			 *            幅度
			 * @param y
			 *            转速
			 * @param title
			 *            标题的值
			 */
			public void drawPanelLine(float[] xvalue, float[] zvalue, float y,
					float title)
			{

				if (y < nowYmin || y > nowYmax)
					return;

				float temx = xvalue[0];
				float temz = zvalue[0];

				float Y = -2 * (y - nowYmin) / (nowYmax - nowYmin) + 1;

				float xbase = 4 / (nowXmax - nowXmin);

				float zbase = 0.5f / maxAmp;
				for (int i = 1; i < xvalue.length && xvalue[i] <= nowXmax; i++)
				{
					if (temx < nowXmin || temx > nowXmax)
					{
						temx = xvalue[i];
						temz = zvalue[i];
						continue;
					}
					drawLine(xbase * (temx - nowXmin) - 2, zbase * temz, Y,
							xbase * (xvalue[i] - nowXmin) - 2, zbase
									* zvalue[i], Y, ExtColor.getLineColor());
					temx = xvalue[i];
					temz = zvalue[i];
				}

			}

			/**
			 * 画一个zx水平平面
			 * 
			 * @param y
			 *            y轴坐标
			 * */
			public void drawPlane(float y, Color c)
			{
				gl.glColor3f((float) (0xD1 / 255.0), (float) (0xD1 / 255.0),
						(float) (0xD1 / 255.0));

				// 开始绘制立方体
				gl.glBegin(GL2.GL_QUADS);

				// 顶面
				gl.glNormal3f(0.0f, 1.0f, 0.0f);// 法线向上

				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3f(-2.0f, y, -1.0f);

				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex3f(-2.0f, y, 1.0f);

				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex3f(2.0f, y, 1.0f);

				gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex3f(2.0f, y, -1.0f);

				gl.glEnd();
				
				
				gl.glLineWidth(2);
				drawLine(2, y, 1, 2, y, -1, c);
				drawLine(2, y, 1, -2, y, 1, c);
				drawLine(-2, y, -1, 2, y, -1, c);
				drawLine(-2, y, -1, -2, y, 1, c);

				drawScale(10, 5);
				
				
			}

			/**
			 * 画虚线
			 * 
			 * @param t密集度
			 *            小于1的float 越小越密集 注意：xyz要比tox小
			 * */
			public void drawDashedLine(float x, float y, float z, float tox,
					float toy, float toz, Color c, float t)
			{
				gl.glLineWidth(2);
				float xe = ((tox - x) * t);
				float ye = ((toy - y) * t);
				float ze = ((toz - z) * t);
				gl.glColor3f((float) (c.getRed() / 255.0),
						(float) (c.getGreen() / 255.0),
						(float) (c.getBlue() / 255.0));

				while (x <= tox && y <= toy && z <= toz)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(x, y, z);
					gl.glVertex3f((x + xe), (y + ye), (z + ze));// Z
					gl.glEnd();

					x += xe * 2;
					y += ye * 2;
					z += ze * 2;

				}

			}

			/**
			 * 画直线函数
			 * 
			 * @param x
			 *            点的坐标
			 * @param y
			 * @param z
			 * 
			 * */
			public void drawLine(float x, float y, float z, float tox,
					float toy, float toz, Color c)
			{

				gl.glBegin(GL.GL_LINES);
				gl.glColor3f((float) (c.getRed() / 255.0),
						(float) (c.getGreen() / 255.0),
						(float) (c.getBlue() / 255.0));
				gl.glVertex3f(x, y, z);
				gl.glVertex3f(tox, toy, toz);// Z
				gl.glEnd();
			}

			/**
			 * 画刻度
			 * 
			 * @param xminium
			 *            x最小刻度 没xminum画上断线
			 * */
			public void drawScale(int xnum, int znum)
			{

				// 对于z轴 z = 1-2*x/max
				// 对于x轴 x = 4*x/max-2
				// 对于y轴 y = 0.5*x/max
				float xminimum = (float) Math.ceil((nowXmax - nowXmin) / xnum);
				float zminimum = (float) Math.ceil((nowYmax - nowYmin) / znum);
				if (zminimum < 0.000001 && zminimum > -0.000001)
				{
					//return;
					zminimum = 1;
				}

				for (float i = 0; i <= (nowYmax - nowYmin); i += zminimum)// 最小刻度
				{
					drawLine(-2f, 0f, 1 - 2 * i / (nowYmax - nowYmin), -1.9f,
							0f, 1 - 2 * i / (nowYmax - nowYmin), Color.gray);// 画xz
					gl.glRasterPos3f(-2.3f, 0f, 1 - 2 * i / (nowYmax - nowYmin));// 显示文字的地方
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,// 字的大小
							"" + (i + nowYmin));// 显示的内容
				}

				for (float i = 0; i <= (nowXmax - nowXmin); i += xminimum)// 最小刻度
				{
					drawLine(4 * i / (nowXmax - nowXmin) - 2, 0f, 1f, 4 * i
							/ (nowXmax - nowXmin) - 2, 0f, 0.9f, Color.gray);// 画xz
					gl.glRasterPos3f(4 * i / (nowXmax - nowXmin) - 2, 0f, 1.2f);// 显示文字的地方
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,// 字的大小
							"" + (i + nowXmin));// 显示的内容
				}

			}

			/**
			 * 画点函数
			 * 
			 * @param x
			 *            点的坐标
			 * @param y
			 * @param z
			 * 
			 * */
			public void drawApoint(float x, float y, float z, Color c)
			{
				gl.glPointSize(2f);
				gl.glBegin(GL.GL_POINTS);
				gl.glColor3f((float) (c.getRed() / 255.0),
						(float) (c.getGreen() / 255.0),
						(float) (c.getBlue() / 255.0));
				gl.glVertex3f(x, y, z);// Z
				gl.glEnd();
			}

			/**
			 * 画箭头 输入参数必须是在坐标系上的某个点，即其他两个坐标为零 颜色是调用前的颜色
			 * 
			 * @param x
			 *            箭头起始点的坐标
			 * @param y
			 * @param z
			 * */
			private void drawArrows(float x, float y, float z)
			{
				if (x != 0)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(x, 0.0f, 0.0f);
					gl.glVertex3f((float) (x - 0.1), 0.0f, 0.1f);// Z
					gl.glEnd();

					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(x, 0.0f, 0.0f);
					gl.glVertex3f((float) (x - 0.1), 0.0f, -0.1f);// Z
					gl.glEnd();
				}

				if (y != 0)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(0.0f, y, 0.0f);
					gl.glVertex3f(0.1f, (float) (y - 0.1), 0.0f);// Z
					gl.glEnd();

					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(0.0f, y, 0.0f);
					gl.glVertex3f(-0.1f, (float) (y - 0.1), 0.0f);// Z
					gl.glEnd();
				}

				if (z != 0)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(0.0f, 0.0f, z);
					gl.glVertex3f(0.1f, 0.0f, (float) (z - 0.1));// Z
					gl.glEnd();

					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(0.0f, 0.0f, z);
					gl.glVertex3f(-0.1f, 0.0f, (float) (z - 0.1));// Z
					gl.glEnd();
				}
			}

		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			String actString = e.getActionCommand();

			if (CMD_SAVE == actString)
			{
				new ScreenShots(this.getLocationOnScreen().x,
						this.getLocationOnScreen().y, getWidth(), getHeight());
			} else if (CMD_COUNT == actString)
			{
				new SetCascadNumDialog().setVisible(true);
			} else if (CMD_SETSECTION == actString)
			{
				new SetSectionDialog().setVisible(true);
			}
		}

		private class SetSectionDialog extends PropDialog
		{

			public SetSectionDialog()
			{
				super(300, 200);

				table.setValueAt(nowXmax + "", 0, 1);
				table.setValueAt(nowXmin + "", 1, 1);
				table.setValueAt(nowYmax + "", 2, 1);
				table.setValueAt(nowYmin + "", 3, 1);
			}

			@Override
			protected void JcomBoxsInit()
			{
				// TODO Auto-generated method stub
			}

			@Override
			protected void JcomBoxsSelectHandle(JComboBox choise)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				// TODO Auto-generated method stub

			}

			@Override
			protected void IntemInit()
			{
				// TODO Auto-generated method stub
				item = new String[] {
						HduChartUtil
								.getResource("OfflineCascad_SetSection_MaxX"),
						HduChartUtil
								.getResource("OfflineCascad_SetSection_MinX"),
						HduChartUtil
								.getResource("OfflineCascad_SetSection_MaxY"),
						HduChartUtil
								.getResource("OfflineCascad_SetSection_MinY") };
			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub

				nowXmax = Float.parseFloat((String) table.getValueAt(0, 1));
				nowXmin = Float.parseFloat((String) table.getValueAt(1, 1));
				nowYmax = Float.parseFloat((String) table.getValueAt(2, 1));
				nowYmin = Float.parseFloat((String) table.getValueAt(3, 1));

				animator.hduDisplay();

			}
		}

		private class SetCascadNumDialog extends PropDialog
		{

			public SetCascadNumDialog()
			{
				super(300, 100);

				table.setValueAt(cascadLineNum + "", 0, 1);
			}

			@Override
			protected void JcomBoxsInit()
			{
				// TODO Auto-generated method stub
			}

			@Override
			protected void JcomBoxsSelectHandle(JComboBox choise)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void CommitHandle(Vector<JComboBox> boxs)
			{
				// TODO Auto-generated method stub

			}

			@Override
			protected void IntemInit()
			{
				// TODO Auto-generated method stub
				item = new String[] { HduChartUtil
						.getResource("OfflineCascad_SetCascadNum_Num") };
			}

			@Override
			public void CommitHandle(JTable table)
			{
				// TODO Auto-generated method stub
				cascadLineNum = Integer.parseInt((String) table
						.getValueAt(0, 1));

				animator.hduDisplay();

			}
		}

	}

	public static void main(String[] args)
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		float[] xvalue = { 0, 2, 3, 5f, 10, 15f, 20, 25f, 35 };
		float[] zvalue = { 0, 4, 0, 3.7f, 2.1f, 0.02f, 0.07f, 0.08f, 0.0f };
		PlaneXY planeXY = new PlaneXY(xvalue, zvalue);
		Vector<PlaneXY> dataIn = new Vector<PlaneXY>();// 频谱信息
		float[] index = { 50, 150, 200 };// 对应转

		dataIn.add(planeXY.clone());
		dataIn.add(planeXY.clone());
		dataIn.add(planeXY.clone());

		OfflineCascadPanel waveform = new OfflineCascadPanel("0", "1", index,
				dataIn);

		jFrame.add(waveform);// 添加到主界面中
		jFrame.setVisible(true);
	}
}
