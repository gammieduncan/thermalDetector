package thermalDetector;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class readImage {
	
	 Color [][] arrImage;
	int width;
	int height;
	static double MULTIPLIER = 2.5;
	static int IMGLENGTH = 10;
	double[] intervalAvg = new double[IMGLENGTH];
	
	private static final int MAX_SCORE = 20;
	private static final int PREF_W = 800;
	private static final int PREF_H = 650;
	private static final int BORDER_GAP = 30;
	private static final Color GRAPH_COLOR = Color.green;
	private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
    private static final int GRAPH_POINT_WIDTH = 12;
	private static final int Y_HATCH_CNT = 10;
	
	class DrawGraph extends JPanel
	{

		   public DrawGraph() 
		   {
		   }

		   @Override
		   protected void paintComponent(Graphics g) {
		      super.paintComponent(g);
		      Graphics2D g2 = (Graphics2D)g;
		      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		      double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (IMGLENGTH - 1);
		      double yScale = ((double) getHeight() - 2 * BORDER_GAP);

		      List<Point> graphPoints = new ArrayList<Point>();
		      for (int i = 0; i < IMGLENGTH; i++) {
		         int x1 = (int) (i * xScale + BORDER_GAP);
		         int y1 = (int) ((intervalAvg[i]) * yScale + BORDER_GAP);
		         graphPoints.add(new Point(x1, y1));
		      }

		      // create x and y axes 
		      g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
		      g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

		      // create hatch marks for y axis. 
		      for (int i = 0; i < Y_HATCH_CNT; i++) {
		         int x0 = BORDER_GAP;
		         int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
		         int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
		         int y1 = y0;
		         g2.drawLine(x0, y0, x1, y1);
		      }

		      // and for x axis
		      for (int i = 0; i < IMGLENGTH - 1; i++) {
		         int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (IMGLENGTH - 1) + BORDER_GAP;
		         int x1 = x0;
		         int y0 = getHeight() - BORDER_GAP;
		         int y1 = y0 - GRAPH_POINT_WIDTH;
		         g2.drawLine(x0, y0, x1, y1);
		      }

		      Stroke oldStroke = g2.getStroke();
		      g2.setColor(GRAPH_COLOR);
		      g2.setStroke(GRAPH_STROKE);
		      for (int i = 0; i < graphPoints.size() - 1; i++) {
		         int x1 = graphPoints.get(i).x;
		         int y1 = graphPoints.get(i).y;
		         int x2 = graphPoints.get(i + 1).x;
		         int y2 = graphPoints.get(i + 1).y;
		         g2.drawLine(x1, y1, x2, y2);         
		      }

		      g2.setStroke(oldStroke);      
		      g2.setColor(GRAPH_POINT_COLOR);
		      for (int i = 0; i < graphPoints.size(); i++) {
		         int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
		         int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
		         int ovalW = GRAPH_POINT_WIDTH;
		         int ovalH = GRAPH_POINT_WIDTH;
		         g2.fillOval(x, y, ovalW, ovalH);
		      }
		   }

		   @Override
		   public Dimension getPreferredSize() {
		      return new Dimension(PREF_W, PREF_H);
		   }

		   private void createAndShowGui() {
		     /* List<Integer> scores = new ArrayList<Integer>();
		      Random random = new Random();
		      int maxDataPoints = 16;
		      int maxScore = 20;
		      for (int i = 0; i < maxDataPoints ; i++) {
		         scores.add(random.nextInt(maxScore));
		      } */
		      DrawGraph mainPanel = new DrawGraph();

		      JFrame frame = new JFrame("DrawGraph");
		      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      frame.getContentPane().add(mainPanel);
		      frame.pack();
		      frame.setLocationByPlatform(true);
		      frame.setVisible(true);
		   }

		   public void doIt() {
		      SwingUtilities.invokeLater(new Runnable() {
		         public void run() {
		            createAndShowGui();
		         }
		      });
		   }
		}
	
	//finds the average for each 1cm wide slice of the image
	public void scanImage()
	{
		int i = 0;
		int numPixels = (width/IMGLENGTH)*height;  //numPixels in one 1cm interval
		
		//scanning centimeter by centimeter
		while(i < IMGLENGTH)
		{
			int c = i*(width/IMGLENGTH); 
			double sumLuminance = 0;
			//find the sum of pixel luminance on this interval in order to find avg.
			for(int x = c; x < c + (width/IMGLENGTH); x++)
			{
				for(int j = 0; j < height; j++)
				{
					//if the pixel was not null'd when we removed outliers
					if(arrImage[x][j] != null) 
						sumLuminance += getLuminance(arrImage[x][j]);
					
				}
			}
			
			double avg = sumLuminance/numPixels; //find avg.
			intervalAvg[i] = avg; //index is interval#
			i += 1; //increment i to next interval
			
		}
		
		for(int a = 0; a < 10; a++)
		{
			System.out.println(intervalAvg[a]);
		}
	}
	
	//calculates the luminance value for one pixel
	public double getLuminance(Color c)
	{
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		
		double luminance = (red * .2126d + green * .7152d + blue * 0.0722d) / 255;
		
		return luminance;
	}
	
	public void convertImg(BufferedImage image)
	{
		width = image.getWidth();
		height = image.getHeight();
		
		arrImage = new Color[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				arrImage[i][j] = new Color(image.getRGB(i, j));
			}
		}
				
	}
	
	//removes pixels not associated with human subject
	//any pixel whose blue values is THREE as large as the red and yellow combined is set to 0
	public void removeOutliers()
	{
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				int red = arrImage[i][j].getRed();
				int green = arrImage[i][j].getGreen();
				int blue = arrImage[i][j].getBlue();
				
				//if blue value exceeds the sum of red and green times MULTIPLIER, then ignore
				//that value by setting it to null
				if(blue > (MULTIPLIER * (red + green)))
						{
							arrImage[i][j] = null;
						}
				
			}
		}
	}
	
	public static void main(String[] args) 
	{
		readImage r = new readImage();
		
		Scanner s = new Scanner(System.in);
		BufferedImage img = null;
		try {
			//REPLACE "thermal.png" with s.readLine()
			img = ImageIO.read(readImage.class.getResource("thermal.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		r.convertImg(img); 
		r.removeOutliers();
		r.scanImage();
		
		readImage.DrawGraph toDraw = r. new DrawGraph();
		toDraw.doIt();
		
		
		s.close();
	}

}
