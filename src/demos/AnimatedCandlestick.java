package demos;


import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import renderer.CandlestickRenderer;
import renderer.LineRenderer;

public class AnimatedCandlestick extends JFrame {
	
	// Here the data to be stored
	private double[][] data;
	private final XYPlot plot;
	private int predictMinute = -1;
	private double numberAxisRangeMax = 0.0;
	private double numberAxisRangeMin = 999999999.9;	
	
	public AnimatedCandlestick() {
		super("Demo");

		// Prepare axis
		DateAxis domainAxis = new DateAxis("Date");
		NumberAxis rangeAxis = new NumberAxis("Price");
		rangeAxis.setAutoRangeIncludesZero(false);
        // domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline()); // uncomment this if there are no data for weekends
		
		
		// Prepare source data and data sets
		loadDataCSV();
		
		
		// Create main plot
		plot = new XYPlot();
		plot.setDomainAxis(domainAxis); // X-axis with dates
		plot.setRangeAxis(rangeAxis); // Y-axis with price
		
		
		// OHLC dataset 1
		plot.setDataset(0, getDataSetOHLC1());
		CandlestickRenderer renderer1 = new renderer.CandlestickRenderer();
		plot.setRenderer(0, renderer1);
	
		/*
		// OHLC dataset 2
		plot.setDataset(1, getDataSetOHLC2());
		CandlestickRenderer renderer2 = new renderer.CandlestickRenderer();
		plot.setRenderer(1, renderer2);
		//plot.mapDatasetToDomainAxis(1, 0);
		//plot.mapDatasetToRangeAxis(1, 0);
		*/

		
		// Average price dataset
		plot.setDataset(1, getDataSet2());
		LineRenderer renderer2 = new LineRenderer();
		plot.setRenderer(1, renderer2);
		
	

		// Create the chart, set its colors, etc.
		JFreeChart chart = new JFreeChart("Demo", null, plot, true);
		chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
		chart.getXYPlot().setBackgroundPaint(Color.BLACK);
		chart.getXYPlot().setOutlineVisible(false);
		chart.getXYPlot().setRangeGridlinePaint(Color.darkGray);
		chart.getXYPlot().setDomainGridlinePaint(Color.darkGray);
		chart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.WHITE);
		chart.getXYPlot().getDomainAxis().setAxisLineVisible(true);
		chart.getXYPlot().getDomainAxis().setUpperMargin(0.05);
		chart.getXYPlot().getDomainAxis().setLowerMargin(0.05);
		chart.getXYPlot().getRangeAxis().setTickLabelPaint(Color.WHITE);
		chart.getXYPlot().getRangeAxis().setAxisLineVisible(true);
		chart.setBackgroundPaint(Color.BLACK);
		chart.setAntiAlias(false);

		// nastavenie osi Y
		double diff = numberAxisRangeMax - numberAxisRangeMin;
		double offset2 = diff * 0.2;
		ValueAxis numberAxis = chart.getXYPlot().getRangeAxis();
		numberAxis.setRange(numberAxisRangeMin - offset2, numberAxisRangeMax + offset2);
		chart.getXYPlot().setRangeAxis(numberAxis);
		
		
		// Create chart panel & set its dimensions
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(1500, 800));
		chartPanel.setMaximumDrawHeight(2000);
		chartPanel.setMaximumDrawWidth(3000);
		chartPanel.setMinimumDrawWidth(10);
		chartPanel.setMinimumDrawHeight(10);
		
		
		// Show application, chart, etc.
		setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(chartPanel, BorderLayout.CENTER);
		this.add(getControls(), BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	
	private JPanel getControls() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		JButton btnRefresh = new JButton("do something");
		btnRefresh.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				int rnd = randInt(0, plot.getDataset(0).getItemCount(0));
				
				predictMinute = rnd;
				
				renderer.CandlestickRenderer renderer1 = (renderer.CandlestickRenderer)plot.getRenderer(0);
				renderer1.setSemitransparentAfter(rnd);
				
				plot.setDataset(0, getDataSetOHLC1());
				//plot.setDataset(1, getDataSetOHLC2());
				
				/*
				LineRenderer renderer2 = (LineRenderer)plot.getRenderer(1);
				renderer2.setSemitransparentAfter(rnd);
				plot.setDataset(1, getDataSet2());
				*/
				
			}
			
		});		
		panel.add(btnRefresh);
	
		return panel;
	}
	
	
	protected OHLCSeriesCollection getDataSetOHLC1() {
		OHLCSeries ohlcReal = new OHLCSeries("OHLC real");
		
		for (int i=0; i<this.data.length; i++) {

			// real OHLC data
			ohlcReal.add(new OHLCItem(new Millisecond(new Date((long)this.data[i][0])), this.data[i][1], this.data[i][2], this.data[i][3], this.data[i][4]));
			
		}

		OHLCSeriesCollection ret = new OHLCSeriesCollection();
		ret.addSeries(ohlcReal);
		return ret;
	}

	
	
	protected OHLCSeriesCollection getDataSetOHLC2() {
		OHLCSeries ohlcPrediction = new OHLCSeries("OHLC prediction");
		
		for (int i=0; i<this.data.length; i++) {

			// prediction OHLC data
			if (predictMinute >= 0 && i >= predictMinute && i < predictMinute+10) {
				ohlcPrediction.add(new OHLCItem(new Millisecond(new Date((long)this.data[i][0])), 
						this.data[i][1] + randInt(-5, 5), 
						this.data[i][2] + randInt(-5, 5), 
						this.data[i][3] + randInt(-5, 5), 
						this.data[i][4] + randInt(-5, 5)
				));
			}
			else {
				ohlcPrediction.add(new OHLCItem(new Millisecond(new Date((long)this.data[i][0])), 0, 0, 0, 0));
			}
			
		}

		OHLCSeriesCollection ret = new OHLCSeriesCollection();
		ret.addSeries(ohlcPrediction);
		return ret;
	}

	
	
	
	
	
	/**
	 * Set of OHLC data
	 * 
	 * @return XYDataset
	 */
	protected TimeSeriesCollection getDataSet2() {
		TimeSeries datasetReal = new TimeSeries("Average real");
		TimeSeries datasetPrediction = new TimeSeries("Average prediction");
		
		for (int i=0; i<this.data.length; i++) {

			// real OHLC data
			datasetReal.add(new Millisecond(new Date((long)this.data[i][0])), this.data[i][5]);
			
			
			// prediction OHLC data
			if (predictMinute >= 0 && i >= predictMinute && i < predictMinute+10) {
				datasetPrediction.add(new Millisecond(new Date((long)this.data[i][0])), this.data[i][5] + randInt(-2, 2));
			}
			
		}

		TimeSeriesCollection ret = new TimeSeriesCollection();
		ret.addSeries(datasetReal);
		ret.addSeries(datasetPrediction);
		return ret;
	}

	
	
	
	
	
	
	
	/**
	 * Load OHLC, volume, moving average etc. data from CSV file into private variable this.data
	 */
	private void loadDataCSV() {
		List<double[]> dataList = new ArrayList<>();

		try {
			BufferedReader in = new BufferedReader(new FileReader("src\\demos\\sampleData2.csv"));
			DateFormat df = new SimpleDateFormat("y-M-d H:m");

			String inputLine;
			in.readLine();
			while ((inputLine = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(inputLine, ",");

				double date = df.parse(st.nextToken()).getTime();
				double open = Double.parseDouble(st.nextToken());
				double high = Double.parseDouble(st.nextToken());
				double low = Double.parseDouble(st.nextToken());
				double close = Double.parseDouble(st.nextToken());
				double priceAverage = Double.parseDouble(st.nextToken());
				double volume = Double.parseDouble(st.nextToken());
				double volumeMA = Double.parseDouble(st.nextToken());
				
				numberAxisRangeMax = Math.max(numberAxisRangeMax, high);
				numberAxisRangeMin = Math.min(numberAxisRangeMin, low);				

				double[] item = {date, open, high, low, close, priceAverage, volume, volumeMA};
				dataList.add(item);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		// pretypovanie
		this.data = new double[dataList.size()][8];
		for (int i=0; i<dataList.size(); i++) {
			this.data[i] = dataList.get(i);
		}
	}
	

	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	
	// Main app creator
	public static void main(String[] args) {
		new AnimatedCandlestick();
	}
}
