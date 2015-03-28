package renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

public class CandlestickRenderer extends org.jfree.chart.renderer.xy.CandlestickRenderer {
	
	private final int transparentFactor = 64;
	private final Paint colorRaisingFull = new Color(0, 255, 0);
	private final Paint colorFallingFull = new Color(255, 0, 0);
	private final Paint colorUnknownFull = new Color(128, 128, 128);
	private final Paint colorTransparentFull = new Color(0, 0, 0, 240);
	private final Paint colorRaisingSemi = new Color(80, 80, 80);  // new Color(0, 255, 0, transparentFactor);
	private final Paint colorFallingSemi = new Color(40, 40, 40); // new Color(255, 0, 0, transparentFactor);
	private final Paint colorUnknownSemi = new Color(128, 128, 128); // new Color(128, 128, 128, transparentFactor);
	private final Paint colorTransparentSemi = new Color(15, 15, 15); // new Color(0, 0, 0, transparentFactor);
	private int semitransparentAfter = -1;

	public CandlestickRenderer() {
		setDrawVolume(false);
		setUpPaint(colorUnknownFull); // use unknown color if error
		setDownPaint(colorUnknownFull); // use unknown color if error	
		setCandleWidth(15);
	}
	
	

	
	public int getSemitransparentAfter() {
		return semitransparentAfter;
	}
	
	public void setSemitransparentAfter(int item) {
		semitransparentAfter = item;
	}

	
	@Override
	public Paint getItemPaint(int series, int item) {
		//OHLCDataset highLowData = (OHLCDataset) getPlot().getDataset(series);
		
		OHLCSeriesCollection seriesCol = (OHLCSeriesCollection) getPlot().getDataset();
		OHLCSeries highLowData = seriesCol.getSeries(series);
		
		OHLCItem currentItem = (OHLCItem) highLowData.getDataItem(item);
		OHLCItem prevItem = (OHLCItem) highLowData.getDataItem(item > 0 ? item-1 : 0);
		
		Number curClose = currentItem.getCloseValue();
		Number prevClose = prevItem.getCloseValue(); //highLowData.getClose(series, item>0 ? item-1 : 0);

		boolean semiTransparent = false;
		if (semitransparentAfter >= 0 && semitransparentAfter <= item) {
			semiTransparent = true;
		}
		
		if (prevClose.doubleValue() <=  curClose.doubleValue()) {
			return semiTransparent ? colorRaisingSemi : colorRaisingFull;
		} else {
			return semiTransparent ? colorFallingSemi : colorFallingFull;
		}
	}
	
	
	
	
	
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {
	
		OHLCDataset highLowData = (OHLCDataset) dataset;
		double yOpen = highLowData.getOpenValue(series, item);
        double yClose = highLowData.getCloseValue(series, item);
		
		boolean semiTransparent = false;
		if (semitransparentAfter >= 0 && semitransparentAfter <= item) {
			semiTransparent = true;
		}
		
		
		// set color for filled candle
		if (yClose >= yOpen) {
			setUpPaint(semiTransparent ? colorRaisingSemi : colorRaisingFull);
			setDownPaint(semiTransparent ? colorFallingSemi : colorFallingFull);
		}
		
		// set color for hollow (not filled) candle
		else {
			setUpPaint(semiTransparent ? colorTransparentSemi : colorTransparentFull);
			setDownPaint(semiTransparent ? colorTransparentSemi : colorTransparentFull);
		}
		

		
		// call parent method
		super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    }	

}
