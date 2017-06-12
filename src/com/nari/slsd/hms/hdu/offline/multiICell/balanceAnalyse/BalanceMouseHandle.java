package com.nari.slsd.hms.hdu.offline.multiICell.balanceAnalyse;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import com.nari.slsd.hms.hdu.common.algorithm.Calculate;
import com.nari.slsd.hms.hdu.common.data.PlaneXY;
import com.nari.slsd.hms.hdu.common.iCell.BaseChartJpanel;
import com.nari.slsd.hms.hdu.common.iCell.BaseChartJpanel.SeriesProperties;
import com.nari.slsd.hms.hdu.common.iCell.LineChartPanel;
import com.nari.slsd.hms.hdu.offline.multiICell.dataSelect.SelectSection;
import com.nari.slsd.hms.hdu.utils.HduChartUtil;


/**
 * 动平衡分析模块的鼠标操作
 * 鼠标的一些动作，选取数据
 * @author YXQ
 * @version 1.0,14/12/25
 * @since JDK1.625
 */
public abstract class BalanceMouseHandle implements
		java.awt.event.MouseListener, MouseMotionListener {

	// 区间的集合
//	protected static ResourceBundle res = ResourceBundleWrapper
//			.getBundle(PropertiesPATH.LocalizationBundle);
	Vector<SelectSection> selectSections = new Vector<SelectSection>();
	private String nowMoveName;// 现在正在移动的这条线的名称
	private boolean nowMoveIsStart;// 现在在移动的先是开始线吗
	private SelectSection nowMoveSection;// 正在移动的这条线
	private float nowMoveX;// 正在移动的这条线的显示的X位置
	private SeriesProperties nowMoveProperties;// 现在正在移动的这条线的资源
	private float xLen = 0;// x轴的范围大小
	private Boolean xread = false;
	private Date startTime;
	private Date endTime;
	private JTable selectJTable;
	private JLabel selectJLabel;// 用于现在操作位置的显示
	public boolean ifdoubleClick = false; //判断是否双击
	public float xline;//用于存储标记线的横坐标
	public float yline;//用于存储标记线的纵坐标
	public float firstXline;//用于存储第一条线的位置
	public float secondXline;//用于存储第二条线的位置

	public Vector<SelectSection> getSelectSections() {
		return selectSections;
	}

	public void setJTable(JTable selectJTable) {
		this.selectJTable = selectJTable;
	}

	public void setSelectJlabel(JLabel selectJLabel) {
		this.selectJLabel = selectJLabel;
	}

	public BalanceMouseHandle() {

		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		this.xLen = map.get(BaseChartJpanel.XAxisUpper)
				- map.get(BaseChartJpanel.XAxisLower);// 获取x轴范围长度
		ifdoubleClick = false;
		
	}

	
	private void setXYline(String name,float pointx,float pointy,Color c)
	{
		this.setXline(name,pointx,c);
		this.setYline(pointx,pointy,c);
	}
	
	
	// 设置竖向
	public void setXline(String name,float pointx,Color c) {

		PlaneXY xy = new PlaneXY();
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		float x[] = { pointx, pointx };
		float y[] = { (float) (map.get(BaseChartJpanel.YAxisLower)),
				(float) (map.get(BaseChartJpanel.YAxisUpper)) };
		xy.setX(x);
		xy.setY(y);
		nowMoveX = pointx;
		getLineChartPanel().upAutSeriesData(name, xy, c);
//		getLineChartPanel().setCloseSeriesVisibleInLegend(name);

	}

	// 设置横向
	public void setYline(float pointx,float pointy,Color c) {
		PlaneXY xy = new PlaneXY();
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();

//		float x[] = {pointx-150,pointx+150};
		float x[] = { (float)(map.get(BaseChartJpanel.XAxisLower)),
				(float)(map.get(BaseChartJpanel.XAxisUpper)) };
		float y[] = { pointy, pointy };
		xy.setX(x);
		xy.setY(y);
		getLineChartPanel().upAutSeriesData("horize", xy, c);
		getLineChartPanel().setCloseSeriesVisibleInLegend("horize");
	}

	
	//设置矩形上横线和下横线
	private void setRectUpAndDown(float firstPoint , float lastPoint){
		PlaneXY xyUpper = new PlaneXY();
		PlaneXY xyDown = new PlaneXY();
		Map<String, Float> map = getLineChartPanel().getXYAxisRange();
		float[] yUpper = {(float)(map.get(BaseChartJpanel.YAxisUpper)),(float)(map.get(BaseChartJpanel.YAxisUpper))};
		float[] yDown = {(float)(map.get(BaseChartJpanel.YAxisLower)),(float)(map.get(BaseChartJpanel.YAxisLower))};
		float[] x = {firstPoint,lastPoint};
		xyUpper.setX(x);
		xyUpper.setY(yUpper);
		xyDown.setX(x);
		xyDown.setY(yDown);
		getLineChartPanel().upAutSeriesData("up", xyUpper, Color.RED);
		getLineChartPanel().upAutSeriesData("down", xyDown, Color.RED);
		getLineChartPanel().setCloseSeriesVisibleInLegend("up");
		getLineChartPanel().setCloseSeriesVisibleInLegend("down");
	}
	/**
	 * 判断是否在有效的区域中，就是不能重叠 但是会造成包含情况
	 * 
	 * @param x
	 * @return
	 */
	private boolean isInValidSection(float x) {
		for (SelectSection section : selectSections) {
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
			boolean isstart) {
		// 如果在自己的范围内能修改
		if ((isstart && x >= myself.endindex)
				|| (!isstart && x <= myself.startindex)) {
			return false;
		}

		if (x >= myself.startindex && x <= myself.endindex) {
			return true;
		}
		return isInValidSection(x);
	}

	
	private PlaneXY getSelectRange(){
		PlaneXY planeXY = new PlaneXY();
		float[] selectRange = new float[2];
		if(firstXline - secondXline <0){
			selectRange[0] = firstXline;
			selectRange[1] = secondXline;
		}
		else if(firstXline - secondXline == 0){
			JOptionPane.showMessageDialog(null, HduChartUtil.getResource("OfflineBalance_Erro1"));
			return null;
		}
		else {
//			JOptionPane.showMessageDialog(null, HduChartUtil.getResource("OfflineBalance_Erro2"));
			selectRange[1] = firstXline;
			selectRange[0] = secondXline;
		}
		int len = (int) (selectRange[1] - selectRange[0]);
		float[] xvalue = new float[len];
		float[] yvalue = new float[len];
		for(int i=0,j=(int) selectRange[0];i < len;i++,j++){
			xvalue[i] = j;
			yvalue[i] = getLineChartPanel().planeXY.getY()[j];
		}
		planeXY.setX(xvalue);
		planeXY.setY(yvalue);
		return planeXY;
	}
	
	protected abstract void operationSelectRange(PlaneXY planeXY);
	
	// 双击事件处理
	private void DoubleClickedHandle(java.awt.event.MouseEvent e) {
		float[] mousePoint = new float[2];
		mousePoint = BaseChartJpanel.translateScreenToValue(getLineChartPanel().chartPanel,new Point(e.getX(), e.getY()));

		int nearPointX = Calculate.getNearPosition(getLineChartPanel().planeXY.getX(), mousePoint[0]);
		setXline("first", xline, Color.RED);
		setXYline("second", mousePoint[0], getLineChartPanel().planeXY.getY()[nearPointX], Color.green);
		setRectUpAndDown(xline,mousePoint[0]);
		
	}
	
	// 单击事件处理
	private void SingleClickedHandle(java.awt.event.MouseEvent e) {
		float[] mousePoint = new float[2];
		mousePoint = BaseChartJpanel.translateScreenToValue(getLineChartPanel().chartPanel,new Point(e.getX(), e.getY()));
		int nearPointX = Calculate.getNearPosition(getLineChartPanel().planeXY.getX(), mousePoint[0]);
		setXYline("first",mousePoint[0], getLineChartPanel(). planeXY.getY()[nearPointX],Color.green);
//		xline = mousePoint[0];
//		singleXline = mousePoint[0];
//		yline = getLineChartPanel().planeXY.getY()[nearPointX];
	}
//
//	void getTime(SelectSection section) {
//		int sumindex = (int) xLen;
//
//		long sumtime = endTime.getTime() - startTime.getTime();
//		long result = (long) (sumtime / sumindex * section.startindex);
//		long t = (long) (startTime.getTime() + result);
//		section.startTime = new java.util.Date(t);
//		result = (long) (sumtime / sumindex * section.endindex);
//		t = (long) (startTime.getTime() + result);
//		section.endTime = new java.util.Date(t);
//
//	}

	// 待外部获取
	protected abstract LineChartPanel getLineChartPanel();
	
	private void MousePressHandle(java.awt.event.MouseEvent e)
	{
//		firstPoint= BaseChartJpanel.translateScreenToValue(chartPanel,new Point((int)firstpointX, (int)firstpointY));
		Point2D point = BaseChartJpanel.translateValueToScreen(getLineChartPanel().chartPanel,xline, yline);

			if (Math.abs(e.getX() - point.getX()) < 5)
			{
				xread = true;
			} else
			{
				xread = false;
			}
	}
	
	
	private void MouseReleaseHandle(java.awt.event.MouseEvent e)
	{
//		firstPoint= BaseChartJpanel.translateScreenToValue(chartPanel,new Point((int)firstpointX, (int)firstpointY));
		float[] mousePoint = new float[2];
		mousePoint = BaseChartJpanel.translateScreenToValue(getLineChartPanel().chartPanel,new Point(e.getX(), e.getY()));
		if(xread){
			if(ifdoubleClick)
			{
				int nearPointX = Calculate.getNearPosition(getLineChartPanel().planeXY.getX(), mousePoint[0]);
				setXYline("second", mousePoint[0], getLineChartPanel().planeXY.getY()[nearPointX], Color.green);
				secondXline = mousePoint[0];
				setRectUpAndDown(firstXline,secondXline);
				xline = secondXline;
				operationSelectRange(getSelectRange());
			}
			else
			{
//				SingleClickedHandle(e);
				setXline("first",mousePoint[0],Color.red);
				firstXline = mousePoint[0];
				xline = firstXline;
			}
			int nearPointX = Calculate.getNearPosition(getLineChartPanel().planeXY.getX(), mousePoint[0]);
			setYline(mousePoint[0],getLineChartPanel().planeXY.getY()[nearPointX],Color.red);
			
//			xline = mousePoint[0];
		
		} else
		{
			if(ifdoubleClick)
			{
				setXline("second",xline,Color.green);
				operationSelectRange(getSelectRange());
			}
			else
			{
				setXline("first",xline,Color.red);
//				singleXline = xline;
			}

		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(xread){
			if(ifdoubleClick){
//				setXYline("first",xline, yline,Color.red);
				DoubleClickedHandle(e);
			}
			else {
				SingleClickedHandle(e);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		getLineChartPanel().requestFocus();
		int clickTimes = e.getClickCount();
		int button = e.getButton();
		switch (button)
		{
		case 1:// 左键
			if (clickTimes == 2)
			{
				ifdoubleClick = true;
			}
			else if(clickTimes == 1){
				ifdoubleClick = false;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		MousePressHandle(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		MouseReleaseHandle(arg0);
	}

}
