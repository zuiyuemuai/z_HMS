package com.nari.slsd.hms.hdu.offline.multiICell.dataSelect;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;

import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.BaseChartJpanel;
import com.nari.slsd.hms.hdu.common.iCell.BaseChartJpanel.SeriesProperties;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.common.util.ConfigUtil;
import com.nari.slsd.hms.hdu.common.util.LoggerUtil;

/**
 * 根据鼠标操作进行分拣
 * 
 * @author LYNN
 * @version 1.0,14/12/24
 * @since JDK1.625
 */
public abstract class MouseHandle implements java.awt.event.MouseListener,
		MouseMotionListener, java.awt.event.KeyListener
{
	// 配置定义
	private float KeyMoveSpeed = 0.002f;// 键盘操作的时候移动的速度，是总长度的千分之几
	private Color KeyMoveColor = Color.BLUE;// 键盘操作时选中线的颜色

	// 区间的集合
	Vector<SelectSection> selectSections = new Vector<SelectSection>();
	private String nowMoveName;// 现在正在移动的这条线的名称
	private boolean nowMoveIsStart;// 现在在移动的先是开始线吗
	private SelectSection nowMoveSection;// 正在移动的这条线
	private float nowMoveX;// 正在移动的这条线的显示的X位置
	private SeriesProperties nowMoveProperties;// 现在正在移动的这条线的资源

	private float xLen = 0;// x轴的范围大小

	private boolean isKeyBoardMove = false;// 是否是在用键盘操作

	private Boolean xread = false;
	private final double LineWide = 3D;
	private Date startTime;
	private Date endTime;
	private JTable selectJTable;
	private JLabel selectJLabel;// 用于现在操作位置的显示

	private int outTime = 30;// 自动模式下分拣区间单位s
	private boolean isOut = true;// 自动模式

	private final int ColorNum = 63;
	private ColorMapper colormapper = new ColorMapper(new ColorMapRainbow(), 0,
			ColorNum);
	private float DIV = 1;
	{
		String valueString = ConfigUtil.getPropertiesValue(ConfigUtil.KEY_SELECTDIV);
		if(null == valueString)
		{
			LoggerUtil.log(Level.WARNING, ConfigUtil.KEY_SELECTDIV+" not exit, using 1");
			DIV = 1f;
		}
		else 
		{
			DIV = new Float(valueString);
		}
		
	}

	public void setIsOut(boolean is)
	{
		isOut = is;
	}

	public void setOutTime(int time)
	{
		outTime = time;
	}

	public Vector<SelectSection> getSelectSections()
	{
		return selectSections;
	}

	public void setJTable(JTable selectJTable)
	{
		this.selectJTable = selectJTable;
	}

	public void clearSection()
	{
		selectSections.clear();
	}

	public void setSelectJlabel(JLabel selectJLabel)
	{
		this.selectJLabel = selectJLabel;
	}

	public MouseHandle(Date startTime, Date endTime)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		this.xLen = map.get(BaseChartJpanel.XAxisUpper)
				- map.get(BaseChartJpanel.XAxisLower);// 获取x轴范围长度

	}

	/**
	 * 在表格中显示区间的信息
	 * 
	 * @param sections
	 */
	private void setSectionMessage(Vector<SelectSection> sections)
	{

		Vector<String> title = new Vector<String>();
		Vector<Vector<String>> data = new Vector<Vector<String>>();

		title.add("分拣段");
		title.add("开始时间");
		title.add("结束时间");
		for (int i = 0; i < sections.size(); i++)
		{
			Vector<String> t = new Vector<String>();
			t.add(i + 1 + "");
			if (sections.get(i).isOk)
			{
				t.add(sections.get(i).getStartTimeString());
				t.add(sections.get(i).getEndTimeString());
			} else
			{
				t.add("null");
				t.add("null");
			}

			data.add(t);
		}
		/* Modefied by luqianjie 2015/4/1 for 1108 自动分拣分区信息区时间建议不可更改 */
		SelectTableModel model = new SelectTableModel();
		model.setDataVector(data, title);
		selectJTable.setModel(model);
	}

	/* Begin:Added by luqianjie 2015/4/1 for 1108 自动分拣分区信息区时间建议不可更改 */
	private class SelectTableModel extends DefaultTableModel
	{
		public SelectTableModel()
		{
			super();
		}

		public SelectTableModel(Object[][] data, Object[] columnNames)
		{
			super(data, columnNames);
		}

		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}

	/* End:Added by luqianjie 2015/4/1 for 1108 自动分拣分区信息区时间建议不可更改 */

	public Vector<SelectSection> getSections()
	{
		return selectSections;
	}

	// 设置竖向
	private void setXline(String name, float pointx, Color c)
	{

		PlaneXY xy = new PlaneXY();
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		float x[] = { pointx, pointx };
		float y[] = { (float) (map.get(BaseChartJpanel.YAxisLower) * 0.95),
				(float) (map.get(BaseChartJpanel.YAxisUpper) * 0.95) };
		xy.setX(x);
		xy.setY(y);
		nowMoveX = pointx;
		getLineChartPanel().upAutSeriesData(name, xy, c, LineWide);

		long sumtime = endTime.getTime() - startTime.getTime();
		long result = (long) (sumtime / xLen * nowMoveX);
		long t = (long) (startTime.getTime() + result);

		selectJLabel.setText(SelectSection.getDataString(new Date(t)));

	}

	// 外部获取最新的linechartPanle
	protected abstract LineChartPanel getLineChartPanel();

	/**
	 * 判断是否在有效的区域中，就是不能重叠 但是会造成包含情况
	 * 
	 * @param x
	 * @return
	 */
	private boolean isInValidSection(float x)
	{
		for (SelectSection section : selectSections)
		{
			if (x > section.startindex && x < section.endindex)
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param x
	 * @param myself
	 *            自己的范围，也可以是其他的范围
	 * @param isstart
	 *            是否是开始线
	 * @return
	 */
	private boolean isInValidSection(float x, SelectSection myself,
			boolean isstart)
	{
		// 如果在自己的范围内能修改
		if ((isstart && x >= myself.endindex)// 移动的时候保证起始线不大于结束线
				|| (!isstart && x <= myself.startindex))
		{
			return false;
		}

		// 如果是缩小范围的话，肯定是true，因为都是不包含、不交叉的
		if (x > myself.startindex && x < myself.endindex)
		{
			return true;
		}

		float startx;
		float endx;
		if (isstart)
		{
			startx = x;
			endx = myself.endindex;
		} else
		{
			startx = myself.startindex;
			endx = x;
		}

		// 判断如果是的话，现在的范围是否会包含其他范围
		for (SelectSection section : selectSections)
		{
			if (Math.abs(myself.startindex - section.startindex) < 0.00001f
					&& Math.abs(myself.endindex - section.endindex) < 0.00001f)
				continue;// 如果扫描到相同的则不判定
			if (startx <= section.startindex && endx >= section.endindex)// 判断是否包含
				return false;
			if (x >= section.startindex && x <= section.endindex)// 判断是否交叉
				return false;
		}

		return true;
	}

	// 双击事件处理
	private void DoubleClickedHandle(java.awt.event.MouseEvent e)
	{

		SelectSection section = null;
		float[] mousePoint = BaseChartJpanel.translateScreenToValue(
				getLineChartPanel().chartPanel, new Point(e.getX(), e.getY()));
		System.out.println("x" + mousePoint[0] + "  Y" + mousePoint[1]);

		if (!isInValidSection(mousePoint[0]))
		{
			return;
		}
		for (int i = selectSections.size() - 1; i >= 0; i--)
		{
			if (!selectSections.get(i).isOk)
			{
				section = selectSections.get(i);
				break;
			}
		}

		if (null == section)
		{
			if (isOut)// 自动模式是1
			{

				section = new SelectSection();
				section.startindex = mousePoint[0];// 幅值
				section.name = "Select_" + selectSections.size();

				org.jzy3d.colors.Color c = colormapper.getColor(ColorNum - 4 * selectSections
						.size() % ColorNum);
				section.color = new Color(c.r, c.g, c.b);

				Map<String, Float> map = getLineChartPanel().getXYAxisRange();// 如超过最大值则按照最大值计算
				if (mousePoint[0] + outTime / DIV> map
						.get(BaseChartJpanel.XAxisUpper))
				{
					section.endindex = map.get(BaseChartJpanel.XAxisUpper);// 幅值
					section.isOk = true;
					// 如果自动模式的结束线不满足条件的话则不添加，直接返回
					if (!isInValidSection(section.endindex, section, false))
					{
						return;
					}

					setXline(section.name + "_" + 2,
							map.get(BaseChartJpanel.XAxisUpper), section.color);// 1表示开始线
					getLineChartPanel().setCloseSeriesVisibleInLegend(
							section.name + "_" + 2);
				} else
				{
					section.endindex = mousePoint[0] + outTime / DIV;// 幅值
					section.isOk = true;
					// 如果自动模式的结束线不满足条件的话则不添加，直接返回
					if (!isInValidSection(section.endindex, section, false))
					{
						return;
					}
					setXline(section.name + "_" + 2, mousePoint[0] + outTime / DIV,
							section.color);// 1表示开始线
					getLineChartPanel().setCloseSeriesVisibleInLegend(
							section.name + "_" + 2);
				}

				selectSections.add(section);

				setXline(section.name + "_" + 1, mousePoint[0], section.color);// 1表示开始线
				getLineChartPanel().setCloseSeriesVisibleInLegend(
						section.name + "_" + 1);

				getTime(section);

			} else
			{
				section = new SelectSection();
				section.startindex = mousePoint[0];// 幅值
				section.name = "Select_" + selectSections.size();
				org.jzy3d.colors.Color c = colormapper.getColor(ColorNum - 4 * selectSections
						.size() % ColorNum);
				section.color = new Color(c.r, c.g, c.b);
				
				selectSections.add(section);
				setXline(section.name + "_" + 1, mousePoint[0], section.color);// 1表示开始线
				getLineChartPanel().setCloseSeriesVisibleInLegend(
						section.name + "_" + 1);
			}

		} else
		{
			// 保证start比end要小
			if (section.startindex < mousePoint[0])
			{
				section.endindex = mousePoint[0];// 幅值
			} else
			{
				section.endindex = section.startindex;// 幅值
				section.startindex = mousePoint[0];
			}
			section.isOk = true;// 置为true
			getTime(section);
			setXline(section.name + "_" + 2, mousePoint[0], section.color);// 2表示终止线
			getLineChartPanel().setCloseSeriesVisibleInLegend(
					section.name + "_" + 2);
		}

		setSectionMessage(selectSections);

	}

	// 根据starttime和endtime和index得到时间
	void getTime(SelectSection section)
	{
		int sumindex = (int) xLen;

		long sumtime = endTime.getTime() - startTime.getTime();
		
		long result = (long) (sumtime / sumindex * section.startindex);
		long t = (long) (startTime.getTime() + result);
		section.startTime = new java.util.Date(t);
		
		result = (long) (sumtime / sumindex * section.endindex);
		t = (long) (startTime.getTime() + result);
		section.endTime = new java.util.Date(t);

	}

	private void MousePressHandle(java.awt.event.MouseEvent e)
	{
		if (isKeyBoardMove)
		{
			return;
		}
		for (SelectSection section : selectSections)
		{
			Point2D point = BaseChartJpanel.translateValueToScreen(
					getLineChartPanel().chartPanel, section.startindex, 0);
			if (Math.abs(e.getX() - point.getX()) < 5)
			{
				nowMoveName = section.name + "_" + 1;
				nowMoveSection = section;
				nowMoveIsStart = true;
				xread = true;
				getLineChartPanel().chartPanel.setMouseZoomable(false);
				return;

			} else
			{
				xread = false;
				getLineChartPanel().setMouseDragOperation_X();
			}
			point = BaseChartJpanel.translateValueToScreen(
					getLineChartPanel().chartPanel, section.endindex, 0);
			if (Math.abs(e.getX() - point.getX()) < 5)
			{
				nowMoveName = section.name + "_" + 2;
				nowMoveSection = section;
				nowMoveIsStart = false;
				xread = true;
				getLineChartPanel().chartPanel.setMouseZoomable(false);
				return;
			} else
			{
				xread = false;
				getLineChartPanel().setMouseDragOperation_X();
			}

		}
	}

	private void MouseReleasedHandle(java.awt.event.MouseEvent e)
	{

		float[] mousePoint = new float[2];
		mousePoint = BaseChartJpanel.translateScreenToValue(
				getLineChartPanel().chartPanel, new Point(e.getX(), e.getY()));

		if (xread)
		{
			float writeX = 0;
			if (isInValidSection(mousePoint[0], nowMoveSection, nowMoveIsStart))
			{
				writeX = mousePoint[0];
			} else
			{
				writeX = nowMoveX;
			}

			setXline(nowMoveName, writeX, Color.black);
			writeXintoSection(writeX);

			xread = false;
		}

	}

	/**
	 * 将x写入到现在移动线的区段中
	 * 
	 * @param writeX
	 */
	private void writeXintoSection(float writeX)
	{
		String[] nameStrings = nowMoveName.split("_");
		for (SelectSection s : selectSections)
		{
			if ((nameStrings[0] + "_" + nameStrings[1]).equals(s.name))
			{
				if (nameStrings[2].equals("1"))// 表示为start
				{
					s.startindex = writeX;
				} else if (nameStrings[2].equals("2"))// 表示为start
				{
					s.endindex = writeX;
				}
				getTime(s);

				break;
			}
		}
		setSectionMessage(selectSections);
	}

	private void MouseMoveHandle(java.awt.event.MouseEvent e)
	{

		float[] mousePoint = BaseChartJpanel.translateScreenToValue(
				getLineChartPanel().chartPanel, new Point(e.getX(), e.getY()));

		if (xread)
		{
			if (isInValidSection(mousePoint[0], nowMoveSection, nowMoveIsStart))
			{
				setXline(nowMoveName, mousePoint[0], Color.black);
			} else
			{
				setXline(nowMoveName, nowMoveX, Color.black);
			}

		}

	}

	/**
	 * 被选中进行键盘操作
	 * 
	 * @param e
	 */
	private void SelectLineHandle(java.awt.event.MouseEvent e)
	{
		if (isKeyBoardMove)
		{
			getLineChartPanel().deleteSeries(nowMoveName);
			setXline(nowMoveName, nowMoveX, nowMoveProperties.color);
			writeXintoSection(nowMoveX);
			isKeyBoardMove = false;
		} else
		{

			for (SelectSection section : selectSections)
			{
				Point2D point = BaseChartJpanel.translateValueToScreen(
						getLineChartPanel().chartPanel, section.startindex, 0);
				if (Math.abs(e.getX() - point.getX()) < 5)
				{
					nowMoveName = section.name + "_" + 1;
					nowMoveSection = section;
					nowMoveIsStart = true;
					isKeyBoardMove = true;
					nowMoveProperties = getLineChartPanel().deleteSeries(
							nowMoveName);
					setXline(nowMoveName, nowMoveX, KeyMoveColor);
					return;

				} else
				{
					isKeyBoardMove = false;
				}
				point = BaseChartJpanel.translateValueToScreen(
						getLineChartPanel().chartPanel, section.endindex, 0);
				if (Math.abs(e.getX() - point.getX()) < 5)
				{
					nowMoveName = section.name + "_" + 2;
					nowMoveSection = section;
					nowMoveIsStart = false;
					isKeyBoardMove = true;
					nowMoveProperties = getLineChartPanel().deleteSeries(
							nowMoveName);
					setXline(nowMoveName, nowMoveX, Color.BLUE);
					return;
				} else
				{
					isKeyBoardMove = false;
				}

			}
		}

	}

	private void leftKeyHandle()
	{
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		float nowxLen = map.get(BaseChartJpanel.XAxisUpper)
				- map.get(BaseChartJpanel.XAxisLower);// 获取x轴范围长度
		if (isKeyBoardMove)
		{
			float move = nowMoveX - nowxLen * KeyMoveSpeed;

			if (isInValidSection(move, nowMoveSection, nowMoveIsStart))
			{
				setXline(nowMoveName, move, Color.black);
			} else
			{
				setXline(nowMoveName, nowMoveX, Color.black);
			}
		}
	}

	private void rightKeyHandle()
	{
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		float nowxLen = map.get(BaseChartJpanel.XAxisUpper)
				- map.get(BaseChartJpanel.XAxisLower);// 获取x轴范围长度
		if (isKeyBoardMove)
		{
			float move = nowMoveX + nowxLen * KeyMoveSpeed;

			if (isInValidSection(move, nowMoveSection, nowMoveIsStart))
			{
				setXline(nowMoveName, move, Color.black);
			} else
			{
				setXline(nowMoveName, nowMoveX, Color.black);
			}
		}
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub
		getLineChartPanel().requestFocus();
		int clickTimes = e.getClickCount();
		int button = e.getButton();
		switch (button)
		{
		case 1:// 左键
			if (clickTimes == 1)
			{
				SelectLineHandle(e);// 选中要键盘操作的某一条线
			}
			if (clickTimes == 2)
			{
				DoubleClickedHandle(e);
			}
			break;
		case 2:// 滚轮按下
			SelectLineHandle(e);// 选中要键盘操作的某一条线
			break;
		default:
			break;
		}

	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub
		MousePressHandle(e);

	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub
		MouseReleasedHandle(e);
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e)
	{
		// TODO Auto-generated method stub
		MouseMoveHandle(e);
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e)
	{
		// TODO Auto-generated method stub
		switch (e.getKeyCode())
		{

		case 37:// 左
			leftKeyHandle();
			break;

		case 39:// 右
			rightKeyHandle();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

}
