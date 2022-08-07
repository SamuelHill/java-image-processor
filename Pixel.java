import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class Pixel {
	private BufferedImage img;
	private int x, y;
	private Color c;
	
	// CONSTRUCTOR - grabs a pixel from the image without us storing each pixel
	public Pixel(BufferedImage img, int x, int y) {
		this.img = img;
		this.x = x;
		this.y = y;
		this.c = new Color(this.img.getRGB(this.x, this.y));
	}
	
	// SUPER SIMPLE ACCESSOR METHODS:
	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public Color getColor() { return this.c; }
	public int getRed() { return this.c.getRed(); }
	public int getGreen() { return this.c.getGreen(); }
	public int getBlue() { return this.c.getBlue(); }

	// SORTA SIMPLE ACCESSOR METHODS:
	public double getRedDec() { return this.c.getRed()/255.0; }
	public double getGreenDec() { return this.c.getGreen()/255.0; }
	public double getBlueDec() { return this.c.getBlue()/255.0; }
	
	// PRETTY SIMPLE MUTATOR METHODS:
	// we only allow the color of a pixel to be changed, not the location
	public void setColor(Color c) {
		this.c = c;
		this.img.setRGB(this.x, this.y, this.c.getRGB());
	}
	public void setRed(int r) {
		this.setColor(new Color(r, this.c.getGreen(), this.c.getBlue()));
	}
	public void setGreen(int g) {
		this.setColor(new Color(this.c.getRed(), g, this.c.getBlue()));
	}
	public void setBlue(int b) {
		this.setColor(new Color(this.c.getRed(), this.c.getGreen(), b));
	}
}