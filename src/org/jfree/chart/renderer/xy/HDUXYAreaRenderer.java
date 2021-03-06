package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;


public class HDUXYAreaRenderer extends XYAreaRenderer
{
	XYDataset hdudataset;
	public HDUXYAreaRenderer(XYDataset dataset)
	{
		super();
		this.hdudataset = dataset;
	}

	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass)
	{

		if (!getItemVisible(series, item))
		{
			return;
		}
		XYAreaRendererState areaState = (XYAreaRendererState) state;

		// get the data point...
		double x1 = hdudataset.getXValue(series, item);
		double y1 = hdudataset.getYValue(series, item);
		if (Double.isNaN(y1))
		{
			y1 = 0.0;
		}
		double transX1 = domainAxis.valueToJava2D(x1, dataArea,
				plot.getDomainAxisEdge());
		double transY1 = rangeAxis.valueToJava2D(y1, dataArea,
				plot.getRangeAxisEdge());

		// get the previous point and the next point so we can calculate a
		// "hot spot" for the area (used by the chart entity)...
		int itemCount = hdudataset.getItemCount(series);
		double x0 = hdudataset.getXValue(series, Math.max(item - 1, 0));
		double y0 = hdudataset.getYValue(series, Math.max(item - 1, 0));
		if (Double.isNaN(y0))
		{
			y0 = 0.0;
		}
		double transX0 = domainAxis.valueToJava2D(x0, dataArea,
				plot.getDomainAxisEdge());
		double transY0 = rangeAxis.valueToJava2D(y0, dataArea,
				plot.getRangeAxisEdge());

		double x2 = hdudataset
				.getXValue(series, Math.min(item + 1, itemCount - 1));
		double y2 = hdudataset
				.getYValue(series, Math.min(item + 1, itemCount - 1));
		if (Double.isNaN(y2))
		{
			y2 = 0.0;
		}
		double transX2 = domainAxis.valueToJava2D(x2, dataArea,
				plot.getDomainAxisEdge());
		double transY2 = rangeAxis.valueToJava2D(y2, dataArea,
				plot.getRangeAxisEdge());

		double transZero = rangeAxis.valueToJava2D(0.0, dataArea,
				plot.getRangeAxisEdge());
		Polygon hotspot = null;
		if (plot.getOrientation() == PlotOrientation.HORIZONTAL)
		{
			hotspot = new Polygon();
			hotspot.addPoint((int) transZero, (int) ((transX0 + transX1) / 2.0));
			hotspot.addPoint((int) ((transY0 + transY1) / 2.0),
					(int) ((transX0 + transX1) / 2.0));
			hotspot.addPoint((int) transY1, (int) transX1);
			hotspot.addPoint((int) ((transY1 + transY2) / 2.0),
					(int) ((transX1 + transX2) / 2.0));
			hotspot.addPoint((int) transZero, (int) ((transX1 + transX2) / 2.0));
		} else
		{ // vertical orientation
			hotspot = new Polygon();
			hotspot.addPoint((int) ((transX0 + transX1) / 2.0), (int) transZero);
			hotspot.addPoint((int) ((transX0 + transX1) / 2.0),
					(int) ((transY0 + transY1) / 2.0));
			hotspot.addPoint((int) transX1, (int) transY1);
			hotspot.addPoint((int) ((transX1 + transX2) / 2.0),
					(int) ((transY1 + transY2) / 2.0));
			hotspot.addPoint((int) ((transX1 + transX2) / 2.0), (int) transZero);
		}

		if (item == 0)
		{ // create a new area polygon for the series
			areaState.area = new Polygon();
			// the first point is (x, 0)
			double zero = rangeAxis.valueToJava2D(0.0, dataArea,
					plot.getRangeAxisEdge());
			if (plot.getOrientation() == PlotOrientation.VERTICAL)
			{
				//areaState.area.addPoint((int) transX1, (int) zero);
			} else if (plot.getOrientation() == PlotOrientation.HORIZONTAL)
			{
				//areaState.area.addPoint((int) zero, (int) transX1);
			}
		}

		// Add each point to Area (x, y)
		if (plot.getOrientation() == PlotOrientation.VERTICAL)
		{
			areaState.area.addPoint((int) transX1, (int) transY1);
		} else if (plot.getOrientation() == PlotOrientation.HORIZONTAL)
		{
			areaState.area.addPoint((int) transY1, (int) transX1);
		}

		PlotOrientation orientation = plot.getOrientation();
		Paint paint = getItemPaint(series, item);
		Stroke stroke = getItemStroke(series, item);
		g2.setPaint(paint);
		g2.setStroke(stroke);

		Shape shape = null;
		if (getPlotShapes())
		{
			shape = getItemShape(series, item);
			if (orientation == PlotOrientation.VERTICAL)
			{
				shape = ShapeUtilities.createTranslatedShape(shape, transX1,
						transY1);
			} else if (orientation == PlotOrientation.HORIZONTAL)
			{
				shape = ShapeUtilities.createTranslatedShape(shape, transY1,
						transX1);
			}
			g2.draw(shape);
		}

		if (getPlotLines())
		{
			if (item > 0)
			{
				if (plot.getOrientation() == PlotOrientation.VERTICAL)
				{
					areaState.line.setLine(transX0, transY0, transX1, transY1);
				} else if (plot.getOrientation() == PlotOrientation.HORIZONTAL)
				{
					areaState.line.setLine(transY0, transX0, transY1, transX1);
				}
				g2.draw(areaState.line);
			}
		}

		// Check if the item is the last item for the series.
		// and number of items > 0. We can't draw an area for a single point.
		if (getPlotArea() && item > 0 && item == (itemCount - 1))
		{

			if (orientation == PlotOrientation.VERTICAL)
			{
				// Add the last point (x,0)
			//	areaState.area.addPoint((int) transX1, (int) transZero);
			} else if (orientation == PlotOrientation.HORIZONTAL)
			{
				// Add the last point (x,0)
			//	areaState.area.addPoint((int) transZero, (int) transX1);
			}

			g2.fill(areaState.area);

			// draw an outline around the Area.
			if (isOutline())
			{
				Shape area = areaState.area;

				// Java2D has some issues drawing dashed lines around "large"
				// geometrical shapes - for example, see bug 6620013 in the
				// Java bug database. So, we'll check if the outline is
				// dashed and, if it is, do our own clipping before drawing
				// the outline...
				Stroke outlineStroke = lookupSeriesOutlineStroke(series);
				if (outlineStroke instanceof BasicStroke)
				{
					BasicStroke bs = (BasicStroke) outlineStroke;
					if (bs.getDashArray() != null)
					{
						Area poly = new Area(areaState.area);
						// we make the clip region slightly larger than the
						// dataArea so that the clipped edges don't show lines
						// on the chart
						Area clip = new Area(new Rectangle2D.Double(
								dataArea.getX() - 5.0, dataArea.getY() - 5.0,
								dataArea.getWidth() + 10.0,
								dataArea.getHeight() + 10.0));
						poly.intersect(clip);
						area = poly;
					}
				} // end of workaround

				g2.setStroke(outlineStroke);
				g2.setPaint(lookupSeriesOutlinePaint(series));
				g2.draw(area);
			}
		}

		int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
		int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
		updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex,
				rangeAxisIndex, transX1, transY1, orientation);

		// collect entity and tool tip information...
		EntityCollection entities = state.getEntityCollection();
		if (entities != null && hotspot != null)
		{
			addEntity(entities, hotspot, hdudataset, series, item, 0.0, 0.0);
		}

	}
}
