package com.nari.slsd.hms.hdu.common.iCell;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.mouse.MouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot2d.primitive.ColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.MultiColorScatter;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.AbstractTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.DateTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

import com.jogamp.opengl.util.HduJzyChart;
import com.nari.slsd.hms.hdu.common.util.GridBagUtil;
import com.nari.slsd.hms.hdu.common.util.ScreenShots;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelectAndAnalyse.PropDialog;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;
import com.smardec.mousegestures.MouseGestures;
import com.smardec.mousegestures.MouseGesturesListener;

/**
 * 南瑞水电站监护系统基本图元
 * 3D坐标系图元
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public class Coordinate3D extends JPanel implements ActionListener
{
//	protected static ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
	protected MultiColorScatter scatter;
	public HduJzyChart chart;
	protected Coord3d[] points = null;
	protected LinkedList<Coord3d> list = new LinkedList<Coord3d>();// 现在显示数据

	protected GLAnimatorControl animator;

	protected GL2 gl;

	protected float z_max = 0;// 自动中z轴最大值
	protected float y_max = 0;
	protected float mintime = 0;// 时间最小值
	protected float maxtime = 0;

	protected ColorMapper myColorMapper;
	protected MouseController mouseController;// 鼠標控制
	protected IAxeLayout axeLayout;

//	private Font titlefont = PropertiesUtil.titlefont;
//	private Font textfont = PropertiesUtil.textfont;
	private Font titlefont = new Font("微软雅黑", Font.BOLD, 18);
	private Font textfont = new Font("微软雅黑", Font.ITALIC, 12);

	private static String CMD_SETSECTION = "setSection";// 设置区间
	private static String CMD_SAVE = "save";// 设置区间

	private static float zbound = 1.05f;//设置z轴最大预留数  1_23 lqj add
	
	public ICanvas getCanvas()
	{
		return chart.getCanvas();
	}

	public Coordinate3D()
	{
		this(HduChartUtil.getResource("ICell_Coordinate3D_Title"), HduChartUtil.getResource("ICell_Coordinate3D_AxiseLable"));
	}

	public Coordinate3D(String Title, String bottomTitle)
	{
		initCoordinate();
		this.setLayout(new BorderLayout());
		this.add((Component) chart.getCanvas(), BorderLayout.CENTER);
		this.add(getTitleJPanel(Title), BorderLayout.NORTH);
		this.add(getBottomJPanel(bottomTitle), BorderLayout.SOUTH);
	}

	private JLabel titleLable;
	private String title;
	
	public void setTitle(String name)
	{
		titleLable.setText(title+"("+name+")");
	}
	private JPanel getTitleJPanel(String Title)
	{
		title = Title;
		JPanel titleJPanel = new JPanel(new GridBagLayout());
		titleJPanel.setBackground(java.awt.Color.white);
		GridBagUtil.addBlankJLabel(titleJPanel, 0, 0, 2, 1);
		titleLable = new JLabel(Title);
		titleLable.setFont(titlefont);
		GridBagUtil.setLocation(titleJPanel, titleLable, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(titleJPanel, 2, 0, 2, 1);

		titleLable.setComponentPopupMenu(createPopupMenu());
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

		JMenuItem sectionItem = new JMenuItem(HduChartUtil.getResource("ICell_Coordinate3D_SetSection"));
		sectionItem.setActionCommand(CMD_SETSECTION);
		sectionItem.addActionListener(this);
		jpopupMenu.add(sectionItem);

		JMenuItem saveItem = new JMenuItem(HduChartUtil.getResource("ICell_Coordinate3D_Save"));
		saveItem.setActionCommand(CMD_SAVE);
		saveItem.addActionListener(this);
		jpopupMenu.add(saveItem);

		return jpopupMenu;

	}

	private JPanel getBottomJPanel(String bottomTitle)
	{
		JPanel buttom = new JPanel(new GridBagLayout());
		buttom.setBackground(java.awt.Color.white);
		JLabel jLabel = new JLabel(bottomTitle);
		jLabel.setFont(titlefont);
		GridBagUtil.addBlankJLabel(buttom, 0, 0, 1.8, 1);
		GridBagUtil.setLocation(buttom, jLabel, 1, 0, 1, 1, true);
		GridBagUtil.addBlankJLabel(buttom, 2, 0, 1, 1);
		return buttom;
	}

	public void display()
	{
		this.display(false);
	}
	/**
	 * 顯示信息
	 */
	public void display(boolean isaut)
	{
		points = list.toArray(new Coord3d[list.size()]);
		scatter.setData(points);
//		for (int i = 0; i < points.length; i++)
//		{
//			if(points[i].z > 0.9)
//			{
//				System.out.println("have over 0.9");
//			}
//		}
		chart.hduDisplay();
		
		if(isaut)
		{
			BoundingBox3d bb = chart.getView().getBounds();
			z_max =  bb.getZmax();
		}
		
	}

	/**
	 * 自动修改范围
	 */
	public void aut(float mintime, float maxtime)
	{
		chart.getView().setBoundManual(
				new BoundingBox3d(mintime, maxtime, 0, y_max, 0, z_max*zbound));
		chart.getView().updateBounds();
	}

	// 自动适应y轴数据
	public void autOnlyY(float mintime, float maxtime)
	{
		BoundingBox3d bb = chart.getView().getBounds();
		chart.getView().setBoundManual(
				new BoundingBox3d(mintime, maxtime, bb.getYmin(), bb.getYmax(),
						bb.getZmin(), bb.getZmax()));
		chart.getView().updateBounds();
	}

	protected void initCoordinate()
	{

		points = list.toArray(new Coord3d[list.size()]);

		// Create a chart and add scatter
		chart = new HduJzyChart();

		// 对于坐标轴的设置
		axeLayout = chart.getAxeLayout();
		axeLayout.setMainColor(Color.BLACK);
		axeLayout.setZTickColor(Color.BLACK);// z轴坐标颜色
		
		// 颜色
		org.jzy3d.plot3d.rendering.view.View view = chart.getView();
		view.setBackgroundColor(Color.WHITE);

		// 散列点
		ChartScene chartScene = chart.getScene();
		// Create a drawable scatter with a colormap
		myColorMapper = new ColorMapper(new ColorMapRainbow(), 0f, 1f);
		scatter = new MyMultiColorScatter(points, myColorMapper);
		scatter.setWidth(5);
		// 设置图标
		scatter.setLegend(new ColorbarLegend(scatter, chart.getView().getAxe()
				.getLayout().getZTickProvider(), chart.getView().getAxe()
				.getLayout().getZTickRenderer()));
		scatter.setLegendDisplayed(true);// 显示
		chartScene.add(scatter);

		GLCapabilities capabilities = chart.getCapabilities();

		// 添加鼠标控制
		mouseController = new MouseController(chart);
		mouseController.install();

		animator = ((IScreenCanvas) chart.getCanvas()).getAnimator();
		animator.stop();

		list.clear();

	}

	/**
	 * 重新建立坐標
	 */
	public void reinitCoordinate()
	{
		initCoordinate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		String actString = e.getActionCommand();

		if (CMD_SETSECTION == actString)
		{
			new SetSectionDialog().setVisible(true);
		} else if (CMD_SAVE == actString)
		{

			new ScreenShots(this.getLocationOnScreen().x,
					this.getLocationOnScreen().y, getWidth(), getHeight());
		}
	}

	private class SetSectionDialog extends PropDialog
	{

		public SetSectionDialog()
		{
			super(300, 200);
			BoundingBox3d bb = chart.getView().getBounds();
			table.setValueAt(bb.getXmax() + "", 0, 1);
			table.setValueAt(bb.getXmin() + "", 1, 1);
			table.setValueAt(bb.getYmax() + "", 2, 1);
			table.setValueAt(bb.getYmin() + "", 3, 1);
			table.setValueAt(bb.getZmax() + "", 4, 1);
			table.setValueAt(bb.getZmin() + "", 5, 1);
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
					
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MaxX"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MinX"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MaxY"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MinY"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MaxZ"),
					HduChartUtil.getResource("ICell_Coordinate3D_SetSection_MinZ")};
		}

		@Override
		public void CommitHandle(JTable table)
		{
			// TODO Auto-generated method stub
			System.out
					.println(Float.parseFloat((String) table.getValueAt(0, 1)));

			chart.getView().setBoundManual(
					new BoundingBox3d(Float.parseFloat((String) table
							.getValueAt(1, 1)), Float.parseFloat((String) table
							.getValueAt(0, 1)), Float.parseFloat((String) table
							.getValueAt(3, 1)), Float.parseFloat((String) table
							.getValueAt(2, 1)), Float.parseFloat((String) table
							.getValueAt(5, 1)), Float.parseFloat((String) table
							.getValueAt(4, 1))*zbound));
			chart.getView().updateBounds();

		}

	}

	// X轴时间刻度定制
	public class MyDateTickRenderer extends DateTickRenderer
	{
		private final long nowTime = new Date().getTime();

		public long getNowTime()
		{
			return nowTime;
		}

		public MyDateTickRenderer()
		{
			this.format = "HH:mm:ss";
		}

		public MyDateTickRenderer(String format)
		{
			this.format = format;
		}

		public String format(float value)
		{
			long temp = ((long) value + nowTime);
			java.util.Date date = Utils.num2dat(temp);
			return Utils.dat2str(date, format);
		}
	}

	// 颜色绘图器
	protected class MyMultiColorScatter extends MultiColorScatter
	{

		public MyMultiColorScatter(Coord3d[] coordinates, Color[] colors,
				ColorMapper mapper)
		{
			super(coordinates, colors, mapper);
			// TODO Auto-generated constructor stub
		}

		public MyMultiColorScatter(Coord3d coordinates[], ColorMapper mapper)
		{
			super(coordinates, null, mapper, 1.0F);
		}

		public void draw(GL2 gl, GLU glu, Camera cam)
		{
			if (transform != null)
				transform.execute(gl);

			BoundingBox3d bb = chart.getView().getBounds();
			float limitx = bb.getXmax();
			float limity = bb.getYmax();
			float limitz = z_max*zbound;

			float limitmx = bb.getXmin();
			float limitmy = bb.getYmin();
			float limitmz = bb.getZmin();

		//	System.out.println("maxz"+limitz);
			
			gl.glLineWidth(2f);// 线的宽度
			gl.glPointSize(2f);
			if (coordinates != null && coordinates.length > 0)
			{
				Coord3d arr$[] = coordinates;
				int len$ = arr$.length;
				z_max = 0;
				Coord3d coorfCoord3d = arr$[0];
				if (coorfCoord3d.x < limitmx || coorfCoord3d.y < limitmy
						|| coorfCoord3d.z < limitmz)
				{
					coorfCoord3d.y = limitmy;
				}

				for (int i$ = 0; i$ < len$; i$++)
				{
					Coord3d coord = arr$[i$];
					if (coord.x > limitx || coord.y > limity
							|| coord.z > limitz || coord.x < limitmx
							|| coord.y < limitmy || coord.z < limitmz)
						continue;
					if (coorfCoord3d.x == coord.x)
					{
						if (coord.z >= 0)
						{
							z_max = Math.max(z_max, coord.z);
							drawColorLine(coorfCoord3d, coord, gl);
						}
					}
					coorfCoord3d = coord;
				}

			}
		}

		private void drawColorLine(Coord3d from, Coord3d to, GL2 gl)
		{
			float step = z_max/ 100;
			if (step == 0)
				return;
			// y = az+b
			if (Math.abs(from.z - to.z) < step)
			{
				Color color = mapper.getColor(from.z);

				gl.glBegin(GL2.GL_LINES);
				gl.glColor4f(color.r, color.g, color.b, color.a);
				gl.glVertex3f(from.x, from.y, from.z);
				gl.glVertex3f(from.x, to.y, to.z);// 画直线
				gl.glEnd();
				return;
			}

			float a = (from.y - to.y) / (from.z - to.z);
			float b = from.y - a * from.z;

			if (from.z <= to.z)
			{
				float lastz = from.z;
				for (float z = from.z; z <= to.z; z += step)
				{
					Color color = mapper.getColor(z);

					gl.glBegin(GL2.GL_LINES);
					gl.glColor4f(color.r, color.g, color.b, color.a);
					gl.glVertex3f(from.x, a * lastz + b, lastz);
					gl.glVertex3f(from.x, a * z + b, z);// 画直线
					gl.glEnd();
					lastz = z;

				}
			} else
			{
				float lastz = from.z;
				for (float z = from.z; z >= to.z; z -= step)
				{
					Color color = mapper.getColor(z);

					gl.glBegin(GL2.GL_LINES);
					gl.glColor4f(color.r, color.g, color.b, color.a);
					gl.glVertex3f(from.x, a * lastz + b, lastz);
					gl.glVertex3f(from.x, a * z + b, z);// 画直线
					gl.glEnd();
					lastz = z;

				}
			}

		}

	}

	// 图例
	protected class LegendRenderer implements Renderer2d
	{

		private final ICanvas c;

		public LegendRenderer(ICanvas c)
		{
			this.c = c;
		}

		public void paint(Graphics g)
		{
			g.setColor(java.awt.Color.BLACK);
			Font font = new Font("隶书", Font.BOLD, 20);// 设置字体
			g.setFont(font);

//			g.drawString("X轴:" + "时间", 10, 20);
//			g.drawString("Y轴:" + "频率", 10, 40);
//			g.drawString("Z轴:" + "幅值", 10, 60);

			// g.drawImage(toImage(100, 100), c.getRendererWidth() - 100, 0,
			// null);
		}

		public Image toImage(int width, int height)
		{
			ColorbarImageGenerator bar = new ColorbarImageGenerator(
					new ColorMapper(new ColorMapRainbow(), 0f, 0.1f),
					new AbstractTickProvider()
					{

						public float[] generateTicks(float min, float max,
								int steps)
						{
							return new float[] { 0f, 1f, 2f };
						}

						public int getDefaultSteps()
						{
							return 3;
						}
					}, new ITickRenderer()
					{

						public String format(float value)
						{
							switch ((int) value)
							{
							case 0:
								return " ";
							case 1:
								return " ";
							case 2:
								return " ";
							default:
								return "";
							}
						}
					});

			bar.setForegroundColor(Color.BLACK);
			bar.setHasBackground(false);

			// render @ given dimensions
			return bar.toImage(Math.max(width - 25, 1),
					Math.max(height - 25, 1));
		}
	}

	// 鼠标操作
	protected class MouseController extends CameraMouseController
	{
		private MouseGestures mouseGestures;// 手势
		private HduJzyChart chart;
		private final Coord3d originalgEye = new Coord3d(0.8471976, 0.4471976,
				2000.0);// 最初的视角
		private final Coord3d UpEye = new Coord3d(5.318625E-4, 1.5707964,
				85048.89);// 从上往下看的视角

		public MouseController(HduJzyChart chart)
		{
			super();
			this.chart = chart;
			// this.chart = chart;
			/*
			 * System.out.println(originalgEye.x + ":" + originalgEye.y + ":" +
			 * originalgEye.z);
			 */
			mouseGestures = new MouseGestures();
			mouseGestures.setMouseButton(8);// 设置中间滚轮操作 4是右键，16是左键
			mouseGestures.addMouseGesturesListener(new MouseGesturesListener()
			{
				// 手势识别返回
				public void gestureMovementRecognized(String currentGesture)
				{
					System.out.println(currentGesture);

					if (currentGesture.equals("DR"))
					{
						setEye(originalgEye);
					} else if (currentGesture.equals("UR"))// 上右
					{
						setEye(UpEye);
					}

				}

				public void processGesture(String gesture)
				{
					try
					{
						Thread.sleep(200L);
					} catch (InterruptedException e)
					{
					}
				}
			});
			mouseGestures.start();
			
			setEye(originalgEye);
		}

		void setEye(Coord3d eye)
		{
			Coord3d now = chart.getView().getViewPoint();
			eye.z = now.z;
			chart.getView().setViewPoint(eye.clone());
		}

		float factor = 1;// 放大缩小的系数

		public float getfactor()
		{
			return factor;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			if (threadController != null)
			{
				threadController.stop();
			}

			factor = 1 + (e.getWheelRotation() / 20.0f);
			float timefactor = 1 + (e.getWheelRotation() / 20.0f);
			zoomAll(factor, timefactor);
		}

		public void mouseDragged(MouseEvent e)
		{
			Coord2d mouse = new Coord2d(e.getX(), e.getY());

			//System.out.println("Coordinate3D.MouseController.mouseDragged()");
			// Rotate
			if (MouseUtilities.isLeftDown(e))
			{
				Coord2d move = mouse.sub(prevMouse).div(150);
				rotate(move);
			} // Shift
			else if (MouseUtilities.isRightDown(e))
			{
				Coord2d move = mouse.sub(prevMouse);
				if (move.y != 0)
				{
					Scale s = chart.getScale();
					s.setMax((1f + move.y / 100f) * s.getMax());
					chart.setScale(s, true);
					// shift(move.y/1000f);
				}
			}
			prevMouse = mouse;
			chart.hduDisplay();
		}

		protected void zoomAll(final float factor, float timefactor)
		{
			for (Chart c : targets)
			{
				BoundingBox3d bb = c.getView().getBounds();
				c.getView().setBoundManual(
						new BoundingBox3d(bb.getXmin(), bb.getXmax()
								* timefactor, bb.getYmin(), bb.getYmax()
								* factor, bb.getZmin(), bb.getZmax() * factor));
				c.getView().updateBounds();
				z_max = bb.getZmax();
				
			}

		}

		public void install()
		{
			CameraThreadController threadCamera = new CameraThreadController(
					chart);
			this.addSlaveThreadController(threadCamera);
			chart.addController(this);
		}

	}

	public static void main(String args[])
	{
		JFrame jFrame = new JFrame();
		jFrame.setTitle("WaveForm");
		jFrame.setSize(800, 600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLayout(new BorderLayout());

		Coordinate3D posture3d = new Coordinate3D();

		jFrame.add(posture3d, BorderLayout.CENTER);

		jFrame.setVisible(true);

	}

}
