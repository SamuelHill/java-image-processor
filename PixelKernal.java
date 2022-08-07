import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.imageio.*;

class PixelKernal {
	Pixel[][] pk;

	public PixelKernal() {
		this.pk = new Pixel[3][3];
	}

	public PixelKernal(int n) {
		if (n%2 == 1) {
			System.out.println("we don't do even kernals round these parts pinhead");
		} else {
			this.pk = new Pixel[n][n];
		}
	}

	public Pixel getV(int i, int j) {
		if (i == -1) {
			if (j == -1) {
				return this.pk[0][0];
			} else if (j == 0) {
				return this.pk[0][1];
			} else {
				return this.pk[0][2];
			}
		} else if (i == 0) {
			if (j == -1) {
				return this.pk[1][0];
			} else if (j == 0) {
				return this.pk[1][1];
			} else {
				return this.pk[1][2];
			}
		} else {
			if (j == -1) {
				return this.pk[2][0];
			} else if (j == 0) {
				return this.pk[2][1];
			} else {
				return this.pk[2][2];
			}
		}
	}

	public void setV(int i, int j, Pixel p) {
		if (i == -1) {
			if (j == -1) {
				this.pk[0][0] = p;
			} else if (j == 0) {
				this.pk[0][1] = p;
			} else {
				this.pk[0][2] = p;
			}
		} else if (i == 0) {
			if (j == -1) {
				this.pk[1][0] = p;
			} else if (j == 0) {
				this.pk[1][1] = p;
			} else {
				this.pk[1][2] = p;
			}
		} else {
			if (j == -1) {
				this.pk[2][0] = p;
			} else if (j == 0) {
				this.pk[2][1] = p;
			} else {
				this.pk[2][2] = p;
			}
		}
	}

	public void setup(BufferedImage src, int x, int y) {
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				int new_x = x + i;
				int new_y = y + j;
				this.setV(i, j, new Pixel(src, new_x, new_y));
			}
		}
	}

	public Color apply(Kernal k) {
		Pixel p;
		double w;
		double r = 0.0;
		double b = 0.0;
		double g = 0.0;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				w = k.getW(i,j);
				p = this.getV(i,j);
				double r_temp = w * p.getRed();
				r = r + r_temp;
				double b_temp = w * p.getBlue();
				b = b + b_temp;
				double g_temp = w * p.getGreen();
				g = g + g_temp;
			}
		}
		if (r < 0) { r = 0; } else if (r > 255) { r = 255; } // clamp to 0..255
		if (g < 0) { g = 0; } else if (g > 255) { g = 255; }
		if (b < 0) { b = 0; } else if (b > 255) { b = 255; }
		return new Color((int) r, (int) g, (int) b);
	}
}