package cz.vutbr.wislab.symphony.TVScreenGenerator.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Manager implements SlideGenerator {
	private String inputFolder = "//tiny/DLNA/Elementy/";
	private String outputFolder = "//tiny/DLNA/";
	private String date;
	
	private DecimalFormat df;

	private BufferedImage slide1 = null;
	private BufferedImage slide2 = null;
	private BufferedImage slide3 = null;
	private BufferedImage slide4 = null;
	private BufferedImage slide5 = null;

	public Manager() {
		// TODO Auto-generated constructor stub
		df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.CEILING);
	}

	@Override
	public void initialize(String date) {
		// TODO Auto-generated method stub
		//this.inputFolder = inputFolder;
		//this.outputFolder = outputFolder;
		System.out.println("TVScreenGeneratorBundle: initializing...");
		this.date = date;
		System.out.println("TVScreenGeneratorBundle: date... ok!");
	}

	@Override
	public void setFirstSlide(boolean isChecked, double temperature,
			double humidity, double water, boolean isMoreThanAverage,
			boolean isElectricityBalancePlus, double electricity,
			double electrityBalanceEur, boolean isArmed) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// set date and time
		try {
			slide1 = ImageIO.read(new File(this.inputFolder	+ "Slide_1/energy_dashboard_s1.jpg"));
			System.out.println("TVScreenGeneratorBundle: slide1... generating!");
		} catch (IOException e1) {
			System.out.println("TVScreenGeneratorBundle: slide1... err!");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		setText(1550, 125, date, slide1, new Font("Arial", Font.BOLD, 30),
				new Color(70, 72, 73));

		// All Systems are connected
		if (isChecked) {
			try {
				setSubImage(
						350,
						350,
						ImageIO.read(new File(this.inputFolder
								+ "Slide_1/check.jpg")), slide1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out
						.println("TVScreenGeneratorUser: Slide1 - check symbol is N/A");
			}
			setText(335, 655, "All systems", slide1, new Font("Arial",
					Font.BOLD, 40), new Color(70, 72, 73));
			setText(305, 705, "are connected", slide1, new Font("Arial",
					Font.BOLD, 40), new Color(70, 72, 73));
		} else {
			setText(335, 655, "Warning", slide1, new Font("Arial", Font.BOLD,
					40), Color.orange);
		}

		// set temperature
		setText(840, 445, temperature + "\u2103", slide1, new Font("Arial",
				Font.BOLD, 50), Color.black);

		// set humidity
		setText(840, 508, humidity + " %", slide1, new Font("Arial", Font.BOLD,
				50), Color.black);

		// set water consumption
		setText(760, 750, water + " L", slide1,
				new Font("Arial", Font.BOLD, 40), new Color(0, 125, 189));

		if (isMoreThanAverage) {
			setText(760, 800, "More than Average", slide1, new Font("Arial",
					Font.PLAIN, 40), Color.red);
			setText(760, 830, "(last 24 h)", slide1, new Font("Arial",
					Font.PLAIN, 30), Color.red);
		} else {
			setText(760, 800, "Less than Average", slide1, new Font("Arial",
					Font.PLAIN, 40), Color.red);
			setText(760, 830, "(last 24 h)", slide1, new Font("Arial",
					Font.PLAIN, 30), Color.red);

		}

		// electricity balance
		setText(1265, 445, df.format(electricity) + " kWh", slide1, new Font("Arial",
				Font.PLAIN, 50), Color.red);
		setText(1265, 508, "\u20AC" + df.format(electrityBalanceEur), slide1, new Font(
				"Arial", Font.BOLD, 50), Color.red);

		// alarm system
		if (isArmed) {
			try {
				setText(1400, 780, "Armed", slide1, new Font("Arial",
						Font.BOLD, 40), Color.gray);
				setSubImage(
						1265,
						690,
						ImageIO.read(new File(this.inputFolder
								+ "Slide_1/alarm-armed.jpg")), slide1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out
						.println("TVScreenGeneratorUser: Slide1 - alarm-armed symbol is N/A");
			}
		} else {
			try {
				setText(1400, 780, "Not Armed", slide1, new Font("Arial",
						Font.BOLD, 40), Color.gray);
				setSubImage(
						1265,
						690,
						ImageIO.read(new File(this.inputFolder
								+ "Slide_1/alarm-notarmed.jpg")), slide1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out
						.println("TVScreenGeneratorUser: Slide1 - alarm-armed symbol is N/A");
			}

		}

		saveImage(slide1, this.outputFolder + "1.jpg");

		System.out.println("TVScreenGeneratorUser - Slide 1 is completed");
		slide1 = null;

	}

	public BufferedImage setSubImage(int x, int y, BufferedImage subImage,
			BufferedImage image) {
		Graphics g = image.getGraphics();
		g.drawImage(subImage, x, y, null);
		g.dispose();
		return null;
	}

	public BufferedImage setRectangle(int x, int y, int w, int h, Color color,
			BufferedImage image) {
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.fillRect(x, y, w, h);
		g.dispose();
		return null;
	}

	public void setText(int x, int y, String text, BufferedImage image,
			Font font, Color color) {
		Graphics g = image.getGraphics();
		// g.setFont(new Fontname, style, size));
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, x, y);
		g.dispose();
	}

	public void saveImage(BufferedImage bImage, String name) {
		File outputfile = new File(name);
		try {
			// ImageIO.write(bImage, "jpg", outputfile);
			ImageOutputStream ios = ImageIO.createImageOutputStream(outputfile);
			Iterator<ImageWriter> iter = ImageIO
					.getImageWritersByFormatName("jpeg");
			ImageWriter writer = iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(1.00f);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(bImage, null, null), iwp);
			writer.dispose();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Final image can not be saved.");
		}
	}

	@Override
	public void setSecondSlide(double temperature, double minTemperature,
			double maxTemperature, double humidity, double minHumidity,
			double maxHumidity) {
		try {
			slide2 = ImageIO.read(new File(this.inputFolder	+ "Slide_2/energy_dashboard_s2.jpg"));
			System.out.println("TVScreenGeneratorBundle: slide2... generating!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("TVScreenGeneratorBundle: slide2... err!");
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		// set date and time
		setText(1550, 125, date, slide2, new Font("Arial", Font.BOLD, 30),
				new Color(70, 72, 73));
		// temperature
		setText(510, 540, temperature + "\u2103", slide2, new Font("Arial",
				Font.BOLD, 90), Color.black);
		setText(560, 630, maxTemperature + "\u2103", slide2, new Font("Arial",
				Font.PLAIN, 50), Color.red);
		setText(560, 690, minTemperature + "\u2103", slide2, new Font("Arial",
				Font.PLAIN, 50), new Color(0, 125, 189));
		setText(1370, 540, humidity + " %", slide2, new Font("Arial",
				Font.BOLD, 90), Color.black);
		setText(1420, 630, maxHumidity + " %", slide2, new Font("Arial",
				Font.PLAIN, 50), Color.red);
		setText(1420, 690, minHumidity + " %", slide2, new Font("Arial",
				Font.PLAIN, 50), new Color(0, 125, 189));
		saveImage(slide2, this.outputFolder + "2.jpg");

		System.out.println("TVScreenGeneratorUser - Slide 2 is completed");
		slide2 = null;
	}

	@Override
	public void setThirdSlide(double consumption, double production,
			double difference, double eurConsumption) {
		// TODO Auto-generated method stub
		// set date and time
		try {
			slide3 = ImageIO.read(new File(this.inputFolder + "Slide_3/energy_dashboard_s3.jpg"));
			System.out.println("TVScreenGeneratorBundle: slide3... generating!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("TVScreenGeneratorBundle: slide3... err!");
			e.printStackTrace();
		}

		setText(1550, 125, date, slide3, new Font("Arial", Font.BOLD, 30),
				new Color(70, 72, 73));
		// set consumption
		setRectangle(745, 425, 750, 110, new Color(33, 33, 33), slide3);
		setText(765, 500, df.format(consumption) + " kWh", slide3, new Font("Arial",
				Font.PLAIN, 50), Color.white);

		// set difference
		/*setRectangle(745 + (int) (Math.round(750 * production) / consumption),
					535, (int) (Math.round(750 * difference) / consumption), 110,
					new Color(232, 23, 22), slide3);*/
		setRectangle(745, 535, 750, 110, new Color(232, 23, 22), slide3);
		setText(765 + (int) (Math.round(750 * production) / consumption), 610,
				df.format(difference) + " kWh", slide3, new Font("Arial", Font.PLAIN, 50),
				Color.white);
		
		// set production
		setRectangle(745, 535,
				(int) (Math.round(750 * production) / consumption), 110,
				new Color(117, 170, 0), slide3);
		if (production > 0.28 * consumption)
		{
		setText(765, 610, df.format(production) + " kWh", slide3, new Font("Arial",
				Font.PLAIN, 50), Color.white);
		}
		
		

		saveImage(slide3, this.outputFolder + "3.jpg");

		System.out.println("TVScreenGeneratorUser - Slide 3 is completed");
		slide3 = null;

	}

	@Override
	public void setFourthSlide(int waterToday, int waterYesterday,
			int waterAverageSevenDays) {

		// set date and time
		try {
			slide4 = ImageIO.read(new File(this.inputFolder + "Slide_4/energy_dashboard_s4.jpg"));
			System.out.println("TVScreenGeneratorBundle: slide4... generating!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("TVScreenGeneratorBundle: slide4... err!");
			e.printStackTrace();
		}

		setText(1550, 125, date, slide4, new Font("Arial", Font.BOLD, 30),
				new Color(70, 72, 73));
		// TODO Auto-generated method stub
		// set today consumption Maximum is 1500L/day, Value 570 is pixel height
		// of
		// gray rectangle

		// setRectangle(905, 263, 193, 570, new Color(30, 108, 187), slide4);
		setRectangle(905, 833 - (570 * waterToday) / /*1500*/10000, 193,
				(570 * waterToday) / /*1500*/10000, new Color(30, 108, 187), slide4);
		setText(930, 800, Integer.toString(waterToday) + " L", slide4,
				new Font("Arial", Font.PLAIN, 50), Color.white);

		// set yesterday consumption Maximum is 1500L/day, Value 570 is pixel
		// height of
		// gray rectangle

		// setRectangle(905, 263, 193, 570, new Color(30, 108, 187), slide4);
		setRectangle(1106, 833 - (570 * waterYesterday) / /*1500*/10000, 193,
				(570 * waterYesterday) / /*1500*/10000, new Color(100, 100, 100), slide4);
		setText(1131, 800, Integer.toString(waterYesterday) + " L", slide4,
				new Font("Arial", Font.PLAIN, 50), Color.white);

		// set average consumption Maximum is 1500L/day, Value 570 is pixel
		// height
		// of
		// gray rectangle

		// setRectangle(905, 263, 193, 570, new Color(30, 108, 187), slide4);
		setRectangle(905, 833 - (570 * waterAverageSevenDays) / /*1500*/10000, 193, 5,
				new Color(239, 78, 35), slide4);
		setRectangle(1106, 833 - (570 * waterAverageSevenDays) / /*1500*/10000, 193, 5,
				new Color(239, 78, 35), slide4);

		saveImage(slide4, this.outputFolder + "4.jpg");

		System.out.println("TVScreenGeneratorUser - Slide 4 is completed");
		slide4 = null;

	}

	@Override
	public void setFifthSlide(boolean isArmed, boolean isAlert,
			String alertDate, String alertMessage) {
		// TODO Auto-generated method stub
		// set date and time

		try {
			slide5 = ImageIO.read(new File(this.inputFolder + "Slide_5/energy_dashboard_s5.jpg"));
			System.out.println("TVScreenGeneratorBundle: slide5... generating!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("TVScreenGeneratorBundle: slide5... err!");
			e1.printStackTrace();
		}

		setText(1550, 125, date, slide5, new Font("Arial", Font.BOLD, 30),
				new Color(70, 72, 73));
		if (isArmed) {
			try {
				setSubImage(
						386,
						290,
						ImageIO.read(new File(this.inputFolder
								+ "Slide_5/alarm-armed.jpg")), slide5);
				setText(650, 450, "The Alarm System is armed", slide5,
						new Font("Arial", Font.BOLD, 70), Color.gray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out
						.println("TVScreenGeneratorUser: Slide5 - alarm-armed symbol is N/A");
			}

		} else {
			try {
				setSubImage(
						386,
						290,
						ImageIO.read(new File(this.inputFolder
								+ "Slide_5/alarm-notarmed.jpg")), slide5);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out
						.println("TVScreenGeneratorUser: Slide5 - alarm-armed symbol is N/A");
			}
		}

		if (isAlert) {
			try {
				setSubImage(
						460,
						580,
						ImageIO.read(new File(this.inputFolder
								+ "Slide_5/alarm-alert.jpg")), slide5);
				setText(650, 640, alertDate, slide5, new Font("Arial",
						Font.PLAIN, 40), Color.red);
				setText(650, 695, alertMessage, slide5, new Font("Arial",
						Font.PLAIN, 50), Color.red);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out
						.println("TVScreenGeneratorUser: Slide5 - alarm-alert symbol is N/A");
			}

		} else {

		}

		saveImage(slide5, this.outputFolder + "5.jpg");

		System.out.println("TVScreenGeneratorUser - Slide 5 is completed");
		slide5 = null;
	}
}