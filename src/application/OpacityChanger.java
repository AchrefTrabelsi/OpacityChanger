package application;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class OpacityChanger {

	
	private double opacity=1;
	private WritableImage newImage;
	private Image img;
	private int height,width;
	private boolean updated=true;
	public OpacityChanger(String path)
	{
		img = new Image(path );
		width =(int) img.getWidth();
		height =(int) img.getHeight();
		newImage = new WritableImage(width,height);
	}
	public double getopacity()
	{
		return opacity;
	}
	public void setopacity(double p)
	{
		opacity=p;
		updated=false;
	}
	public void updateimage()
	{
		if(updated)
			return;
		PixelReader pixelReader = img.getPixelReader();
		PixelWriter pixelWriter = newImage.getPixelWriter();
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
			{
				Color sc =pixelReader.getColor(i, j);
				Color dc = new Color(sc.getRed(),sc.getGreen(),sc.getBlue(),sc.getOpacity()*opacity);
				pixelWriter.setColor(i,j,dc);
			}
		updated=true;

	}
	public WritableImage getupdatedcopy(double p)
	{
		WritableImage nimage = new WritableImage(width,height);
		PixelReader pixelReader = img.getPixelReader();
		PixelWriter pixelWriter = nimage.getPixelWriter();
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
			{
				Color sc =pixelReader.getColor(i, j);
				Color dc = new Color(sc.getRed(),sc.getGreen(),sc.getBlue(),sc.getOpacity()*p);
				pixelWriter.setColor(i,j,dc);
			}
		return nimage;
		
	}
	public WritableImage getimage()
	{
		return newImage;
		
	}
	
}
