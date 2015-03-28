
package renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


public class LineRenderer extends XYLineAndShapeRenderer {
	
	
	private final int transparentFactor = 64;
	private int semitransparentAfter = -1;
    private final Stroke strokeSolid = new BasicStroke(1.0f);
    private final Stroke strokeDashed = new BasicStroke(1.0f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {5.0f, 5.0f}, 0.0f);
	
	public int getSemitransparentAfter() {
		return semitransparentAfter;
	}
	
	public void setSemitransparentAfter(int item) {
		semitransparentAfter = item;
	}

	
	@Override
	public Stroke getItemStroke(int series, int item) {
		boolean semiTransparent = false;
		if (semitransparentAfter >= 0 && semitransparentAfter <= item) {
			semiTransparent = true;
		}
		
		if (semiTransparent) {
			return strokeDashed;
		}
		else {
			return strokeSolid;
		}
	}

	@Override
	public Paint getItemPaint(int row, int column) {
		return new Color(255, 255, 255);
	}
	
	
	
	
}
