// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   MouseGesturesRecognizer.java

package com.smardec.mousegestures;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

// Referenced classes of package com.smardec.mousegestures:
//			MouseGestures

class MouseGesturesRecognizer
{

	private static final String LEFT_MOVE = "L";
	private static final String RIGHT_MOVE = "R";
	private static final String UP_MOVE = "U";
	private static final String DOWN_MOVE = "D";
	private int gridSize;
	private MouseGestures mouseGestures;
	private Point startPoint;
	private StringBuffer gesture;

	MouseGesturesRecognizer(MouseGestures mouseGestures)
	{
		gridSize = 30;
		startPoint = null;
		gesture = new StringBuffer();
		this.mouseGestures = mouseGestures;
	}

	void processMouseEvent(MouseEvent mouseEvent)
	{
		if (!(mouseEvent.getSource() instanceof Component))
			return;
		Point mouseEventPoint = mouseEvent.getPoint();
		SwingUtilities.convertPointToScreen(mouseEventPoint, (Component)mouseEvent.getSource());
		if (startPoint == null)
		{
			startPoint = mouseEventPoint;
			return;
		}
		int deltaX = getDeltaX(startPoint, mouseEventPoint);
		int deltaY = getDeltaY(startPoint, mouseEventPoint);
		int absDeltaX = Math.abs(deltaX);
		int absDeltaY = Math.abs(deltaY);
		if (absDeltaX < gridSize && absDeltaY < gridSize)
			return;
		float absTangent = (float)absDeltaX / (float)absDeltaY;
		if (absTangent < 1.0F)
		{
			if (deltaY < 0)
				saveMove("U");
			else
				saveMove("D");
		} else
		if (deltaX < 0)
			saveMove("L");
		else
			saveMove("R");
		startPoint = mouseEventPoint;
	}

	private int getDeltaX(Point a, Point b)
	{
		return b.x - a.x;
	}

	private int getDeltaY(Point a, Point b)
	{
		return b.y - a.y;
	}

	private void saveMove(String move)
	{
		if (gesture.length() > 0 && gesture.charAt(gesture.length() - 1) == move.charAt(0))
		{
			return;
		} else
		{
			gesture.append(move);
			mouseGestures.fireGestureMovementRecognized(getGesture());
			return;
		}
	}

	int getGridSize()
	{
		return gridSize;
	}

	void setGridSize(int gridSize)
	{
		this.gridSize = gridSize;
	}

	String getGesture()
	{
		return gesture.toString();
	}

	boolean isGestureRecognized()
	{
		return gesture.length() > 0;
	}

	void clearTemporaryInfo()
	{
		startPoint = null;
		gesture.delete(0, gesture.length());
	}
}
