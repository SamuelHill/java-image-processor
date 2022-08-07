import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.imageio.*;
import java.util.Stack;
import java.util.Random;
import java.lang.*;

public class Picture extends JPanel {
	private BufferedImage src;
	private BufferedImage display;
	private Stack <BufferedImage> image_mods;
	private Stack <String> modifications;
	private Stack <Double> mod_inputs;
	
	private Boolean grayscale_bool, threshold_bool, brightness_bool,
					saturation_bool, contrast_bool, noise_bool, blur_bool,
					edge_bool, requan_bool;
	private Double threshold_val, brightness_val, saturation_val,
					contrast_val, noise_val, requan_val;
	// swap has 3 possible values representing the three states it can be in...
	private int swap_val;
	
	public Picture(String filename) {
		this.image_mods = new Stack<BufferedImage>();
		this.modifications = new Stack<String>();
		this.mod_inputs = new Stack<Double>();
		try {
			this.src = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("problem loading image");
			System.out.println(e);
		}
		this.image_mods.push(clone(src));
		this.display = clone(src);
		
		this.grayscale_bool = false;
		this.threshold_bool = false;
		this.brightness_bool = false;
		this.saturation_bool = false;
		this.contrast_bool = false;
		this.noise_bool = false;
		this.blur_bool = false;
		this.edge_bool = false;
		this.requan_bool = false;
		
		this.swap_val = 0;
		this.threshold_val = null;
		this.brightness_val = null;
		this.saturation_val = null;
		this.contrast_val = null;
		this.noise_val = null;
		this.requan_val = null;
	}

	public Picture(BufferedImage bi) {
		this.image_mods = new Stack<BufferedImage>();
		this.modifications = new Stack<String>();
		this.mod_inputs = new Stack<Double>();
		this.src = bi;
		this.image_mods.push(clone(src));
		this.display = clone(src);
		
		this.grayscale_bool = false;
		this.threshold_bool = false;
		this.brightness_bool = false;
		this.saturation_bool = false;
		this.contrast_bool = false;
		this.noise_bool = false;
		this.blur_bool = false;
		this.edge_bool = false;
		this.requan_bool = false;
		
		this.swap_val = 0;
		this.threshold_val = null;
		this.brightness_val = null;
		this.saturation_val = null;
		this.contrast_val = null;
		this.noise_val = null;
		this.requan_val = null;
	}
	
	public void paintComponent(Graphics g) {
		// Modified from CodeJava to auto scale the image if container smaller than image -
		// http://www.codejava.net/java-se/graphics/drawing-an-image-with-automatic-scaling
		int imgHeight = this.display.getHeight();
		int imgWidth = this.display.getWidth();
		double imgAspect = (double) imgHeight / imgWidth;
		
		int picHeight = this.getHeight();
		int picWidth = this.getWidth();
		double picAspect = (double) picHeight / picWidth;
		
		int x1 = 0; // top left X position
		int y1 = 0; // top left Y position
		int x2 = 0; // bottom right X position
		int y2 = 0; // bottom right Y position
		
		if (imgWidth < picWidth && imgHeight < picHeight) { // the image is smaller than the canvas
			x1 = (picWidth - imgWidth)  / 2;
			y1 = (picHeight - imgHeight) / 2;
			x2 = imgWidth + x1;
			y2 = imgHeight + y1;
		} else {
			if (picAspect > imgAspect) {
				y1 = picHeight;
				// keep image aspect ratio
				picHeight = (int) (picWidth * imgAspect);
				y1 = (y1 - picHeight) / 2;
			} else {
				x1 = picWidth;
				// keep image aspect ratio
				picWidth = (int) (picHeight / imgAspect);
				x1 = (x1 - picWidth) / 2;
			}
			x2 = picWidth + x1;
			y2 = picHeight + y1;
		}
		g.drawImage(this.display, x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
	}

	public BufferedImage getDisplay() {
		return this.display;
	}
	
	public int getImgHeight() {
		if (this.image_mods.peek() == null) { return 100; }
		else { return this.image_mods.peek().getHeight(); }
	}
	public int getImgWidth() {
		if (this.image_mods.peek() == null) { return 100; }
		else { return this.image_mods.peek().getWidth(); }
	}
	public Dimension getPreferredSize() {
		return new Dimension(this.getImgWidth(), this.getImgHeight());
	}
	
	public static Pixel getPixel(BufferedImage bi, int x, int y) {
		return new Pixel(bi, x, y);
	}
	
	private BufferedImage clone(BufferedImage bi) {
		BufferedImage new_bi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Pixel p_1, p_2;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p_1 = this.getPixel(bi, x, y);
				p_2 = this.getPixel(new_bi, x, y);
				p_2.setColor(new Color(p_1.getRed(), p_1.getGreen(), p_1.getBlue()));
			}
		}
		return new_bi;
	}
	
	private void unapply(String modification) {
		int dist = this.modifications.search(modification);
		Stack <String> temp_modifications = new Stack<String>();
		Stack <Double> temp_mod_inputs = new Stack<Double>();
		BufferedImage temp;
		String mod;
		Double mod_input;
		Boolean swap_later = false;
		int swap_count = 0;
		int invert_count = 0;
		for (int i = 1; i <= dist; i++) {
			temp = this.image_mods.pop();
			mod = this.modifications.pop();
			mod_input = this.mod_inputs.pop();
			if (i != dist) {
				if (mod == "swap") {
					swap_later = true;
					swap_count++;
				}
				else if (mod == "grayscale") {
					this.grayscale_bool = false;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "invert") {
					invert_count++;
				}
				else if (mod == "threshold") {
					this.threshold_bool = false;
					this.threshold_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "brightness") {
					this.brightness_bool = false;
					this.brightness_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "saturation") {
					this.saturation_bool = false;
					this.saturation_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "contrast") {
					this.contrast_bool = false;
					this.contrast_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "noise") {
					this.noise_bool = false;
					this.noise_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "blur") {
					this.blur_bool = false;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "edge") {
					this.edge_bool = false;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "requan") {
					this.requan_bool = false;
					this.requan_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "rotate") {
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "scale") {
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
			}
			else {
				this.display = this.image_mods.peek();
			}
		}
		for (int i = 0; i < temp_modifications.size(); i++) {
			mod = temp_modifications.pop();
			mod_input = temp_mod_inputs.pop();
			if (mod == "grayscale") {
				grayscale_modification();
			}
			else if (mod == "threshold") {
				threshold_modification(mod_input.intValue());
			}
			else if (mod == "brightness") {
				brightness_modification(mod_input);
			}
			else if (mod == "saturation") {
				saturation_modification(mod_input);
			}
			else if (mod == "contrast") {
				contrast_modification(mod_input);
			}
			else if (mod == "noise") {
				noise_modification(mod_input);
			}
			else if (mod == "blur") {
				blur_modification();
			}
			else if (mod == "edge") {
				edgeDetect_modification();
			}
			else if (mod == "requan") {
				quantize_modification(mod_input.intValue());
			}
			else if (mod == "rotate") {
				rotate_modification(mod_input);
			}
			else if (mod == "scale") {
				scale2x_modification();
			}
		}
		if (swap_later == true) {
			// i is already take within this loop...
			for (int j = 0; j < swap_count; j++) { swapChannels_modification(); }
		}
		if ((invert_count % 2) == 1) {
			invert_modification();
		}
	}
	
	private void reapply(String modification, Double modification_input) {
		int dist = this.modifications.search(modification);
		Stack <String> temp_modifications = new Stack<String>();
		Stack <Double> temp_mod_inputs = new Stack<Double>();
		BufferedImage temp;
		String mod;
		Double mod_input;
		Boolean swap_later = false;
		int swap_count = 0;
		int invert_count = 0;
		for (int i = 1; i <= dist; i++) {
			temp = this.image_mods.pop();
			mod = this.modifications.pop();
			mod_input = this.mod_inputs.pop();
			if (i != dist) {
				if (mod == "swap") {
					swap_later = true;
					swap_count++;
				}
				else if (mod == "grayscale") {
					this.grayscale_bool = false;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "invert") {
					invert_count++;
				}
				else if (mod == "threshold") {
					this.threshold_bool = false;
					this.threshold_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "brightness") {
					this.brightness_bool = false;
					this.brightness_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "saturation") {
					this.saturation_bool = false;
					this.saturation_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "contrast") {
					this.contrast_bool = false;
					this.contrast_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "noise") {
					this.noise_bool = false;
					this.noise_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "blur") {
					this.blur_bool = false;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "edge") {
					this.edge_bool = false;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "requan") {
					this.requan_bool = false;
					this.requan_val = null;
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "rotate") {
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
				else if (mod == "scale") {
					temp_modifications.push(mod);
					temp_mod_inputs.push(mod_input);
				}
			}
			else {
				temp_modifications.push(mod);
				temp_mod_inputs.push(modification_input);
				// IDEALLY IN HERE YOU SET THE BOOL FOR THE MOD TO FALSE...
				// However we will need to set that in the public methods
			}
		}
		for (int i = 0; i < temp_modifications.size(); i++) {
			mod = temp_modifications.pop();
			mod_input = temp_mod_inputs.pop();
			if (mod == "grayscale") {
				grayscale_modification();
			}
			else if (mod == "threshold") {
				threshold_modification(mod_input.intValue());
			}
			else if (mod == "brightness") {
				brightness_modification(mod_input);
			}
			else if (mod == "saturation") {
				saturation_modification(mod_input);
			}
			else if (mod == "contrast") {
				contrast_modification(mod_input);
			}
			else if (mod == "noise") {
				noise_modification(mod_input);
			}
			else if (mod == "blur") {
				blur_modification();
			}
			else if (mod == "edge") {
				edgeDetect_modification();
			}
			else if (mod == "requan") {
				quantize_modification(mod_input.intValue());
			}
			else if (mod == "rotate") {
				rotate_modification(mod_input);
			}
			else if (mod == "scale") {
				scale2x_modification();
			}
		}
		if (swap_later) {
			// i is already take within this loop...
			for (int j = 0; j < swap_count; j++) { swapChannels_modification(); }
		}
		if ((invert_count % 2) == 1) {
			invert_modification();
		}
	}
	
	private BufferedImage swapChannels(BufferedImage bi) {
		BufferedImage new_bi = clone(bi);
		Pixel p;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = this.getPixel(new_bi, x, y);
				p.setColor(new Color(p.getGreen(), p.getBlue(), p.getRed()));
			}
		}
		return new_bi;
	}
	
	public void swapChannels_modification() {
		BufferedImage prev_img = clone(image_mods.peek());
		BufferedImage modified_img = swapChannels(prev_img);
		this.image_mods.push(modified_img);
		this.display = modified_img;
		this.modifications.push("swap");
		this.mod_inputs.push(null);
		if (this.swap_val < 2) { this.swap_val++; }
		else this.swap_val = 0;
		this.repaint();
	}
	
	private BufferedImage grayscale(BufferedImage bi) {
		BufferedImage new_bi = clone(bi);
		Pixel p;
		int weightedAvg;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = this.getPixel(new_bi, x, y);
				weightedAvg = (int)((0.2126 * p.getRed()) + (0.7152 * p.getGreen())
									+ (0.0722 * p.getBlue()));
				p.setColor(new Color(weightedAvg, weightedAvg, weightedAvg));
			}
		}
		return new_bi;
	}
	
	public void grayscale_modification() {
		if (!this.grayscale_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = grayscale(prev_img);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("grayscale");
			this.mod_inputs.push(null);
			this.grayscale_bool = true;
		}
		else {
			unapply("grayscale");
			this.grayscale_bool = false;
		}
		this.repaint();
	}
	
	private BufferedImage invert(BufferedImage bi) {
		BufferedImage new_bi = clone(bi);
		Pixel p;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				p.setColor(new Color(255-p.getRed(), 255-p.getGreen(), 255-p.getBlue()));
			}
		}
		return new_bi;
	}
	
	public void invert_modification() {
		BufferedImage prev_img = clone(image_mods.peek());
		BufferedImage modified_img = invert(prev_img);
		this.image_mods.push(modified_img);
		this.display = modified_img;
		this.modifications.push("invert");
		this.mod_inputs.push(null);
		this.repaint();
	}
	
	private BufferedImage threshold(BufferedImage bi, int thresh) {
		BufferedImage new_bi = clone(bi);
		Pixel p;
		int r, g, b;
		Color newColor;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				if (p.getRed() < thresh) { r = 0; } else { r = 255; }
				if (p.getGreen() < thresh) { g = 0; } else { g = 255; }
				if (p.getBlue() < thresh) { b = 0; } else { b = 255; }
				newColor = new Color(r, g, b);
				p.setColor(newColor);
			}
		}
		return new_bi;
	}
	
	public void threshold_modification(int thresh) {
		if (!this.threshold_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = threshold(prev_img, thresh);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("threshold");
			this.mod_inputs.push((double)thresh);
			this.threshold_bool = true;
			this.threshold_val = (double)thresh;
		}
		else {
			unapply("threshold");
			this.threshold_bool = false;
		}
		this.repaint();
	}
	
	public void reapply_threshold_modification(double a) {
		threshold_bool = false; // to trick noise_modification...
		reapply("threshold", a);
		this.repaint();
	}
	
	private BufferedImage interpolate(BufferedImage bi1, BufferedImage bi2, double a) {
		int w = Math.max(bi1.getWidth(), bi2.getWidth());
		int h = Math.max(bi1.getHeight(), bi2.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Pixel p1, p2, pc;
		int r, g, b;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				p1 = getPixel(bi1, x, y);
				p2 = getPixel(bi2, x, y);
				r = (int)((a * p1.getRed()) + ((1 - a) * p2.getRed()));
				if (r < 0) { r = 0; } else if (r > 255) { r = 255; } // clamp to 0..255
				g = (int)((a * p1.getGreen()) + ((1 - a) * p2.getGreen()));
				if (g < 0) { g = 0; } else if (g > 255) { g = 255; }
				b = (int)((a * p1.getBlue()) + ((1 - a) * p2.getBlue()));
				if (b < 0) { b = 0; } else if (b > 255) { b = 255; }
				pc = getPixel(combined, x, y);
				pc.setColor(new Color(r, g, b));
			}
		}
		return combined;
	}
	
	private BufferedImage allblack(BufferedImage bi){
		BufferedImage new_bi = clone(bi);
		Pixel p;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				p.setColor(new Color(0, 0, 0));
			}
		}
		return new_bi;
	}
	
	private BufferedImage allgray(BufferedImage bi){
		BufferedImage new_bi = clone(bi);
		Pixel p;
		int weightedAvg = 0;
		int count = 0;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				weightedAvg += (int)((0.2126 * p.getRed()) + (0.7152 * p.getGreen()) + (0.0722 * p.getBlue()));
				count++;
			}
		}
		int new_color = (int)(weightedAvg/count);
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				p.setColor(new Color(new_color, new_color, new_color));
			}
		}
		return new_bi;
	}
	
	private BufferedImage random(BufferedImage bi){
		BufferedImage new_bi = clone(bi);
		Pixel p;
		Random rg = new Random();
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				p.setColor(new Color(rg.nextInt(255), rg.nextInt(255), rg.nextInt(255)));
			}
		}
		return new_bi;
	}
	
	private BufferedImage brightness(BufferedImage bi, double a) {
		return interpolate(bi, allblack(bi), a);
	}
	
	public void brightness_modification(double a) {
		if (!this.brightness_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = brightness(prev_img, a);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("brightness");
			this.mod_inputs.push(a);
			this.brightness_bool = true;
			this.brightness_val = a;
		}
		else {
			unapply("brightness");
			this.brightness_bool = false;
			this.brightness_val = null;
		}
		this.repaint();
	}
	
	public void reapply_brightness_modification(double a) {
		brightness_bool = false; // to trick brightness_modification...
		reapply("brightness", a);
		this.repaint();
	}
	
	private BufferedImage saturation(BufferedImage bi, double a) {
		return interpolate(bi, grayscale(bi), a);
	}
	
	public void saturation_modification(double a) {
		if (!this.saturation_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = saturation(prev_img, a);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("saturation");
			this.mod_inputs.push(a);
			this.saturation_bool = true;
			this.saturation_val = a;
		}
		else {
			unapply("saturation");
			this.saturation_bool = false;
			this.saturation_val = null;
		}
		this.repaint();
	}
	
	public void reapply_saturation_modification(double a) {
		saturation_bool = false; // to trick saturation_modification...
		reapply("saturation", a);
		this.repaint();
	}
	
	private BufferedImage contrast(BufferedImage bi, double a) {
		return interpolate(bi, grayscale(bi), a);
	}
	
	public void contrast_modification(double a) {
		if (!this.contrast_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = contrast(prev_img, a);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("contrast");
			this.mod_inputs.push(a);
			this.contrast_bool = true;
			this.contrast_val = a;
		}
		else {
			unapply("contrast");
			this.contrast_bool = false;
			this.contrast_val = null;
		}
		this.repaint();
	}
	
	public void reapply_contrast_modification(double a) {
		this.contrast_bool = false; // to trick contrast_modification...
		this.reapply("contrast", a);
		this.repaint();
	}
	
	private BufferedImage noise(BufferedImage bi, double a) {
		return interpolate(bi, random(bi), a);
	}
	
	public void noise_modification(double a) {
		if (!this.noise_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = noise(prev_img, a);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("noise");
			this.mod_inputs.push(a);
			this.noise_bool = true;
			this.noise_val = a;
		}
		else {
			unapply("noise");
			this.noise_bool = false;
			this.noise_val = null;
		}
		this.repaint();
	}
	
	public void reapply_noise_modification(double a) {
		this.noise_bool = false; // to trick noise_modification...
		this.reapply("noise", a);
		this.repaint();
	}

	private BufferedImage composite(BufferedImage bi1, BufferedImage bi2, BufferedImage mask) {
		int w = Math.min(Math.min(bi1.getWidth(), bi2.getWidth()), mask.getWidth());
		int h = Math.min(Math.min(bi1.getHeight(), bi2.getHeight()), mask.getHeight());
		BufferedImage composite = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Pixel p1, p2, pm, pc;
		int r, g, b;
		double ra, ga, ba;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				p1 = getPixel(bi1, x, y);
				p2 = getPixel(bi2, x, y);
				pm = getPixel(mask, x, y);
				ra = (pm.getRed()/255.0);
				ga = (pm.getGreen()/255.0);
				ba = (pm.getBlue()/255.0);
				r = (int) ((ra * p1.getRed()) + ((1.0 - ra) * p2.getRed()));
				if (r < 0) { r = 0; } else if (r > 255) { r = 255; } // clamp to 0..255
				g = (int) ((ga * p1.getGreen()) + ((1.0 - ga) * p2.getGreen()));
				if (g < 0) { g = 0; } else if (g > 255) { g = 255; }
				b = (int) ((ba * p1.getBlue()) + ((1.0 - ba) * p2.getBlue()));
				if (b < 0) { b = 0; } else if (b > 255) { b = 255; }
				pc = getPixel(composite, x, y);
				pc.setColor(new Color(r, g, b));
			}
		}
		return composite;
	}

	public BufferedImage composite_mod(BufferedImage bi, BufferedImage mask) {
		BufferedImage prev_img = clone(image_mods.peek());
		return composite(prev_img, bi, mask);
	}

	private BufferedImage convolution(BufferedImage bi, Kernal k) {
		BufferedImage new_bi = clone(bi);
		int h = new_bi.getHeight();
		int w = new_bi.getWidth();
		PixelKernal pk = new PixelKernal();
		Pixel p, p1;
		Color newColor;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x == 0 && y == 0) {
					p = getPixel(new_bi, x, y);
					p1 = getPixel(new_bi, 1, 1);
					p.setColor(new Color (p1.getRed(), p1.getBlue(), p1.getGreen()));
				} else if (x == 0) {
					p = getPixel(new_bi, x, y);
					p1 = getPixel(new_bi, 1, y);
					p.setColor(new Color (p1.getRed(), p1.getBlue(), p1.getGreen()));
				} else if (y == 0) {
					p = getPixel(new_bi, x, y);
					p1 = getPixel(new_bi, x, 1);
					p.setColor(new Color (p1.getRed(), p1.getBlue(), p1.getGreen()));
				} else if (x == w - 1 && y == h - 1) {
					p = getPixel(new_bi, x, y);
					p1 = getPixel(new_bi, w - 2, h - 2);
					p.setColor(new Color (p1.getRed(), p1.getBlue(), p1.getGreen()));
				} else if (x == w - 1) {
					p = getPixel(new_bi, x, y);
					p1 = getPixel(new_bi, w - 2, y);
					p.setColor(new Color (p1.getRed(), p1.getBlue(), p1.getGreen()));
				} else if (y == h - 1) {
					p = getPixel(new_bi, x, y);
					p1 = getPixel(new_bi, x, h - 2);
					p.setColor(new Color (p1.getRed(), p1.getBlue(), p1.getGreen()));
				} else {
					pk.setup(new_bi, x, y);
					newColor = pk.apply(k);
					p = getPixel(new_bi, x, y);
					p.setColor(newColor);
				}
			}
		}
		return new_bi;
	}

	public void blur_modification() {
		if (!this.blur_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			Kernal k = new Kernal();
			k.makeBlur();
			BufferedImage modified_img = convolution(prev_img, k);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("blur");
			this.mod_inputs.push(null);
			this.blur_bool = true;
		}
		else {
			unapply("blur");
			this.blur_bool = false;
		}
		this.repaint();
	}

	public void edgeDetect_modification() {
		if (!this.edge_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			Kernal k = new Kernal();
			k.makeEdgeDetect();
			BufferedImage modified_img = convolution(prev_img, k);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("edge");
			this.mod_inputs.push(null);
			this.edge_bool = true;
		}
		else {
			unapply("edge");
			this.edge_bool = false;
		}
		this.repaint();
	}

	private BufferedImage quantize(BufferedImage bi, int num_levels) {
		BufferedImage new_bi = clone(bi);
		Pixel p;
		int r, g, b;
		Color newColor;
		double n_1 = num_levels - 1.0;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				r = (int) (255.0 * Math.floor((p.getRedDec() * n_1) + 0.5) / n_1);
				g = (int) (255.0 * Math.floor((p.getGreenDec() * n_1) + 0.5) / n_1);
				b = (int) (255.0 * Math.floor((p.getBlueDec() * n_1) + 0.5) / n_1);
				p.setColor(new Color(r, g, b));
			}
		}
		return new_bi;
	}

	public void quantize_modification(int num_levels) {
		if (!this.requan_bool) {
			BufferedImage prev_img = clone(image_mods.peek());
			BufferedImage modified_img = quantize(prev_img, num_levels);
			this.image_mods.push(modified_img);
			this.display = modified_img;
			this.modifications.push("requan");
			this.mod_inputs.push((double) num_levels);
			this.requan_bool = true;
			this.requan_val = (double) num_levels;
		}
		else {
			unapply("requan");
			this.requan_bool = false;
			this.requan_val = null;
		}
		this.repaint();
	}

	public void reapply_quantize_modification(int num_levels) {
		requan_bool = false; // to trick noise_modification...
		reapply("requan", (double) num_levels);
		this.repaint();
	}

	private BufferedImage rotate(BufferedImage bi, int x_u, int y_u, double angle) {
		double a = Math.toRadians(angle);
		double sa = Math.sin(a);
		double ca = Math.cos(a);
		BufferedImage new_bi = clone(bi);
		Pixel p, p2;
		int r, g, b;
		int x_src, y_src;
		for (int y = 0; y < new_bi.getHeight(); y++) {
			for (int x = 0; x < new_bi.getWidth(); x++) {
				p = getPixel(new_bi, x, y);
				x_src = (int) Math.round((ca * (x - x_u)) - (sa * (y - y_u)) + x_u);
				y_src = (int) Math.round((sa * (x - x_u)) + (ca * (y - y_u)) + y_u);
				if (x_src < bi.getWidth() && y_src < bi.getHeight() && x_src >= 0 && y_src >= 0) {
					p2 = getPixel(bi, x_src, y_src);
					r = p2.getRed();
					b = p2.getBlue();
					g = p2.getGreen();
				} else {
					r = 0;
					b = 0;
					g = 0;
				}
				p.setColor(new Color(r, g, b));
			}
		}
		return new_bi;
	}

	public void rotate_modification(double angle) {
		BufferedImage prev_img = clone(image_mods.peek());
		BufferedImage modified_img = rotate(prev_img, (int) prev_img.getWidth() / 2, (int) prev_img.getHeight() / 2, angle);
		this.image_mods.push(modified_img);
		this.display = modified_img;
		this.modifications.push("rotate");
		this.mod_inputs.push(angle);
		this.repaint();
	}

	private BufferedImage resize(BufferedImage bi, int newWidth, int newHeight) {
		BufferedImage new_bi = new BufferedImage(newWidth, newHeight, bi.getType());
		double x_ratio = bi.getWidth() / (double) newWidth;
		double y_ratio = bi.getHeight() / (double) newHeight;
		int pos_x, pos_y;
		Pixel p1, p2;
		for (int y = 0; y < newHeight; y++) {
			for (int x = 0; x < newWidth; x++) {
				pos_x = (int) Math.floor(x * x_ratio);
				pos_y = (int) Math.floor(y * y_ratio);
				p1 = getPixel(bi, pos_x, pos_y);
				p2 = getPixel(new_bi, x, y);
				p2.setColor(new Color(p1.getRed(), p1.getGreen(), p1.getBlue()));
			}
		}
		return new_bi;
	}

	public void scale2x_modification() {
		BufferedImage prev_img = clone(image_mods.peek());
		BufferedImage modified_img = resize(prev_img, (int) prev_img.getWidth() * 2, (int) prev_img.getHeight() * 2);
		this.image_mods.push(modified_img);
		this.display = modified_img;
		this.modifications.push("scale");
		this.mod_inputs.push(null);
		this.repaint();
	}
}
