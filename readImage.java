package thermalDetector;
import java.util.Scanner;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class readImage {
	
	Color [][] arrImage;
	int width;
	int height;
	static double MULTIPLIER = 2.5;
	static int IMGLENGTH = 10;
	double[] intervalAvg = new double[IMGLENGTH];
	
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
		
		
		s.close();
	}

}
