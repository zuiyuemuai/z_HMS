package com.nari.slsd.hms.hdu.offline.multiICell.cascad;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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

import com.jogamp.opengl.util.HduDisplay;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.data.ThreePlaneXY;
import com.nari.slsd.hms.hdu.common.util.ExtColor;

public class CascadChart3D extends GLCanvas
{
	protected GLRender listener;
	protected static HduDisplay animator = null;
	protected static GLCapabilities glcaps = new GLCapabilities(null);

	private Vector<PlaneXY> dataIn = null;// 频谱信息
	private float[] rev = null;// 对应转速
	private int[] sort = null;// 转速从小到大排序

	private float maxRev = 0;// 最大转速
	private float maxAmp = 0;// 最大幅值
	private float maxFre = 0;// 最大频率

	private int FPS = 30;// 帧数

	private float yrot = -0;// Y轴上的旋转量 0z轴正对
	private float xzrot = 60;// xz平面俯仰角度
	private float deep = 66;// 视角

	/**
	 * 姿态图初始化 获取画布，设置鼠键控制
	 * */
	public CascadChart3D(float[] rev, Vector<PlaneXY> dataIn)
	{
		super(glcaps);

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

		animator = new HduDisplay(this);// 设置动画

		listener = new GLRender();// 得到监听器
		this.addGLEventListener(listener);

		MouseListener mouseListener = new MouseListener(listener);
		this.addMouseMotionListener(mouseListener);// 拖动
		this.addMouseListener(mouseListener);// 单击
		this.addMouseWheelListener(mouseListener);// 滑轮

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

		this.rev = div(rev, maxRev, -2, 1);
		for (int i = 0; i < rev.length; i++)
		{
			dataIn.get(i).setX(div(dataIn.get(i).getX(), maxFre, 4, -2));
			dataIn.get(i).setY(div(dataIn.get(i).getY(), maxAmp, 0.5f, 0));
		}

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

	/**
	 * 数据更新
	 * 
	 * @param dataIn
	 *            数据
	 * @param normalization
	 *            归一化参数
	 */
	public void updata(ThreePlaneXY dataIn, float normalization)
	{
		// this.dataIn = dataIn;
		// this.normalization = normalization;
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
		private float[] lightPosition = { 0.0f, 0.0f, 0.5f, 1.0f };

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

			gl.glClearDepth(1.0f);

			gl.glEnable(GL.GL_DEPTH_TEST);

			gl.glDepthFunc(GL.GL_LEQUAL);

			gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

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
		public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
				int height)
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

			spin();

			drawPlane(0, Color.gray);

			for (int i = 0; i < rev.length; i++)
			{
				drawPanelLine(dataIn.get(i).getX(), dataIn.get(i).getY(),
						rev[i], (1 - rev[i]) / 2 * maxRev);
			}

		}

		// 画级联线
		private void drawCascaLine()
		{
			// int n =
			// drawLine(xvalue[i], zvalue[i], y, xvalue[i + 1], zvalue[i + 1],
			// y, Color.green);
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
			if (y > 1)// 小于0的数据不显示
			{
				return;
			}
			for (int i = 0; i < xvalue.length - 1; i++)
			{
				drawLine(xvalue[i], zvalue[i], y, xvalue[i + 1], zvalue[i + 1],
						y, ExtColor.getLineColor());
			}
			// gl.glRasterPos3f(-2.5f, 0, y);// 显示文字的地方
			// glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18,// 字的大小
			// "" + title);// 显示的内容

		}

		/**
		 * 画一个zx水平平面
		 * 
		 * @param y
		 *            y轴坐标
		 * */
		public void drawPlane(float y, Color c)
		{
			gl.glLineWidth(2);
			drawLine(2, y, 1, 2, y, -1, c);
			drawLine(2, y, 1, -2, y, 1, c);
			drawLine(-2, y, -1, 2, y, -1, c);
			drawLine(-2, y, -1, -2, y, 1, c);

			drawScale(10, 5);

			gl.glColor3f((float) (0xD1 / 255.0), (float) (0xD1 / 255.0),
					(float) (0xD1 / 255.0));

			// 开始绘制立方体
			gl.glBegin(GL2.GL_QUADS);

			// 顶面
			gl.glNormal3f(0.0f, 1.0f, 0.0f);// 法线向上

			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(-2.0f, y, -2.0f);

			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-2.0f, y, 2.0f);

			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(2.0f, y, 2.0f);

			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(2.0f, y, -2.0f);

			gl.glEnd();

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
		public void drawLine(float x, float y, float z, float tox, float toy,
				float toz, Color c)
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
			float xminimum = (float) Math.ceil(maxFre / xnum);
			float zminimum = (float) Math.ceil(maxRev / znum);

			for (float i = 0; i <= maxRev; i += zminimum)// 最小刻度
			{
				drawLine(-2f, 0f, 1 - 2 * i / maxRev, -1.9f, 0f, 1 - 2 * i
						/ maxRev, Color.red);// 画xz
				gl.glRasterPos3f(-2.3f, 0f, 1 - 2 * i / maxRev);// 显示文字的地方
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,// 字的大小
						"" + i);// 显示的内容
			}

			for (float i = 0; i <= maxFre; i += xminimum)// 最小刻度
			{
				drawLine(4 * i / maxFre - 2, 0f, 1f, 4 * i / maxFre - 2, 0f,
						0.9f, Color.red);// 画xz
				gl.glRasterPos3f(4 * i / maxFre - 2, 0f, 1.2f);// 显示文字的地方
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,// 字的大小
						"" + i);// 显示的内容
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

}
