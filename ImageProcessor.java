import javax.swing.*;
import javax.swing.event.*;
import java.awt.*; //java.awt.FileDialog
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class ImageProcessor implements ActionListener {
	// Main window
	private JFrame frame;
	// Main panels to hold all the GUI stuff...
	private JPanel primary;
	
	/* I'm saving instance variables for each button and menu item so that
	  the action listener can check e.getSource() against the variable */
	
	// ----> sub of primary
	private Picture canvas;
	private JFrame comp;
	private Picture comp_canvas;
	
	// ----> sub of primary
	private JPanel toolbar;
	// --------> sub of toolbar - includes compos and requan
	private JPanel tool_layerInfo; 
	private JLabel file_label;
	private JButton composite_button;
	private JCheckBox requan_check;
	private JSlider requan_slider;
	// --------> sub of toolbar
	private JPanel tool_manipulations;
	private JButton swap_button, invert_button;
	private JCheckBox grayscale_check, threshold_check;
	private JSlider threshold_slider;
	// --------> sub of toolbar
	private JPanel tool_interpolations;
	private JCheckBox brightness_check, saturation_check, contrast_check, noise_check;
	private JSlider brightness_slider, saturation_slider, contrast_slider, noise_slider;
	// --------> sub of toolbar
	private JPanel tool_convolution;
	private JCheckBox blur_check, edgeDetect_check;
	// --------> sub of toolbar
	private JPanel tool_warp;
	private JButton rotate_button, scale_button;
	
	// Main menu
	private JMenuBar menuBar;
	private JMenu menu_save;
	private JMenuItem menuItem_save, menuItem_saveAs;
	private KeyStroke save_key;
	
	// File input...
	private String src;
	private FileDialog fd;
	
	public ImageProcessor() {}
	
	public void createGUI() {
		// -------------------------- Initialize the Frame --------------------------
	    this.frame = new JFrame("Image Processing Assignment");
	    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
		// -------------------------- Set up the Main Menu --------------------------
		this.menuBar = new JMenuBar();
		
		// -------> Save Menu
		this.menu_save = new JMenu("Save");
		this.menuBar.add(this.menu_save);
		// --------------> Save (saves the image in current layer back to the source)
		this.menuItem_save = new JMenuItem("Save");
		this.menuItem_save.addActionListener(this);
		this.menu_save.add(this.menuItem_save);
		// --------------> Save As (saves the image in current layer to new location)
		this.menuItem_saveAs = new JMenuItem("Save As");
		this.menuItem_saveAs.addActionListener(this);
		this.save_key = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK); /* Mac - KeyEvent.META_DOWN_MASK */
		this.menuItem_saveAs.setAccelerator(save_key);
		this.menu_save.add(this.menuItem_saveAs);
		
		// -------------------------- Set up the Image Panel --------------------------
		this.src = askForFile("Choose an image");
		this.canvas = new Picture(this.src);
		this.canvas.setPreferredSize(this.canvas.getPreferredSize());
		
		// -------------------------- Set up the Tool Bar Panel --------------------------
		this.toolbar = new JPanel();
		this.toolbar.setLayout(new BoxLayout(this.toolbar, BoxLayout.PAGE_AXIS));
		
		// -------------
		this.tool_layerInfo = new JPanel();
		this.tool_layerInfo.setLayout(new BoxLayout(this.tool_layerInfo, BoxLayout.PAGE_AXIS));
		
		this.file_label = new JLabel(this.src);
		this.tool_layerInfo.add(this.file_label);
		this.composite_button = new JButton("Create compostie");
		this.composite_button.addActionListener(this);
		this.tool_layerInfo.add(this.composite_button);
		
		this.requan_check = new JCheckBox("Requantize");
		this.requan_check.addActionListener(this);
		this.tool_layerInfo.add(this.requan_check);
		this.requan_slider = new JSlider(2, 256, 256);
		this.requan_slider.addChangeListener(new SliderListener());
		this.tool_layerInfo.add(this.requan_slider);
		
		this.toolbar.add(this.tool_layerInfo);
		this.toolbar.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// -------------
		this.tool_manipulations = new JPanel();
		this.tool_manipulations.setLayout(new BoxLayout(this.tool_manipulations, BoxLayout.PAGE_AXIS));
		
		this.grayscale_check = new JCheckBox("Grayscale");
		this.grayscale_check.addActionListener(this);
		this.tool_manipulations.add(this.grayscale_check);
		
		this.swap_button = new JButton("Swap Channels");
		this.swap_button.addActionListener(this);
		this.tool_manipulations.add(this.swap_button);
		
		this.invert_button = new JButton("Invert");
		this.invert_button.addActionListener(this);
		this.tool_manipulations.add(this.invert_button);
		
		this.threshold_check = new JCheckBox("Threshold");
		this.threshold_check.addActionListener(this);
		this.tool_manipulations.add(this.threshold_check);
		this.threshold_slider = new JSlider(0, 255, 128);
		this.threshold_slider.addChangeListener(new SliderListener());
		this.tool_manipulations.add(this.threshold_slider);
		
		this.toolbar.add(this.tool_manipulations);
		this.toolbar.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// -------------
		this.tool_interpolations= new JPanel();
		this.tool_interpolations.setLayout(new BoxLayout(this.tool_interpolations, BoxLayout.PAGE_AXIS));
		
		this.brightness_check = new JCheckBox("Brightness");
		this.brightness_check.addActionListener(this);
		this.tool_interpolations.add(this.brightness_check);
		this.brightness_slider = new JSlider(0, 200, 100);
		this.brightness_slider.addChangeListener(new SliderListener());
		this.tool_interpolations.add(this.brightness_slider);
		
		this.saturation_check = new JCheckBox("Saturation");
		this.saturation_check.addActionListener(this);
		this.tool_interpolations.add(this.saturation_check);
		this.saturation_slider = new JSlider(0, 200, 100);
		this.saturation_slider.addChangeListener(new SliderListener());
		this.tool_interpolations.add(this.saturation_slider);
		
		this.contrast_check = new JCheckBox("Contrast");
		this.contrast_check.addActionListener(this);
		this.tool_interpolations.add(this.contrast_check);
		this.contrast_slider = new JSlider(0, 200, 100);
		this.contrast_slider.addChangeListener(new SliderListener());
		this.tool_interpolations.add(this.contrast_slider);

		this.noise_check = new JCheckBox("Noise");
		this.noise_check.addActionListener(this);
		this.tool_interpolations.add(this.noise_check);
		this.noise_slider = new JSlider(0, 200, 100);
		this.noise_slider.addChangeListener(new SliderListener());
		this.tool_interpolations.add(this.noise_slider);
		
		this.toolbar.add(this.tool_interpolations);
		this.toolbar.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// -------------
		this.tool_convolution = new JPanel();
		this.tool_convolution.setLayout(new BoxLayout(this.tool_convolution, BoxLayout.PAGE_AXIS));
		
		this.blur_check = new JCheckBox("Blur");
		this.blur_check.addActionListener(this);
		this.tool_convolution.add(this.blur_check);

		this.edgeDetect_check = new JCheckBox("Edge Detect");
		this.edgeDetect_check.addActionListener(this);
		this.tool_convolution.add(this.edgeDetect_check);
		
		this.toolbar.add(this.tool_convolution);

		// -------------
		this.tool_warp = new JPanel();
		this.tool_warp.setLayout(new BoxLayout(this.tool_warp, BoxLayout.PAGE_AXIS));

		this.rotate_button = new JButton("Rotate");
		this.rotate_button.addActionListener(this);
		this.tool_warp.add(this.rotate_button);

		this.scale_button = new JButton("Scale 2x");
		this.scale_button.addActionListener(this);
		this.tool_warp.add(this.scale_button);

		this.toolbar.add(this.tool_warp);

		// -------------
		this.toolbar.setPreferredSize(new Dimension(200, 550));
		
		// -------------------------- Add image and tools to primary --------------------------
		this.primary = new JPanel();
		this.primary.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 4.0;
		c.weighty = 1.0;
		this.primary.add(this.canvas, c);
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 4;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		this.primary.add(this.toolbar, c);
		
		// Add the Main Menu and Primary Panel to the frame, then show it...
		this.frame.setJMenuBar(this.menuBar);
	    this.frame.getContentPane().add(this.primary);
	    this.frame.pack();
	    this.frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (this.menuItem_save == s) {
			try {
				ImageIO.write(canvas.getDisplay(), "jpg", new File(this.src));
			} catch (IOException ioe) {
				System.out.println("problem saving image");
			}
		}
		else if (this.menuItem_saveAs == s) {
			this.askSaveFile("Save As");
		}
		else if (this.composite_button == s) {
			try {
				BufferedImage bi = ImageIO.read(new File(this.askForFile("Image to composite")));
				BufferedImage mask = ImageIO.read(new File("mask.jpg"));
				BufferedImage comp = canvas.composite_mod(bi, mask);
				composite(comp);
				this.askSaveFile("Save As", comp);
			} catch (IOException ioe) {
				System.out.println("problem loading composite image");
			}
		}
		else if (this.requan_check == s) {
			this.canvas.quantize_modification((int)this.requan_slider.getValue());
		}
		else if (this.grayscale_check == s) {;
			this.canvas.grayscale_modification();
		}
		else if (this.swap_button == s) {;
			this.canvas.swapChannels_modification();
		}
		else if (this.invert_button == s) {
			this.canvas.invert_modification();
		}
		else if (this.threshold_check == s) {
			this.canvas.threshold_modification((int)this.threshold_slider.getValue());
		}
		else if (this.brightness_check == s) {
			this.canvas.brightness_modification((double)this.brightness_slider.getValue()/100);
		}
		else if (this.saturation_check == s) {
			this.canvas.saturation_modification((double)this.saturation_slider.getValue()/100);
		}
		else if (this.contrast_check == s) {
			this.canvas.contrast_modification((double)this.contrast_slider.getValue()/100);
		}
		else if (this.noise_check == s) {
			this.canvas.noise_modification((double)this.noise_slider.getValue()/100);
		}
		else if (this.blur_check == s) {
			this.canvas.blur_modification();
		}
		else if (this.edgeDetect_check == s) {
			this.canvas.edgeDetect_modification();
		}
		else if (this.rotate_button == s) {
			this.canvas.rotate_modification(90.0);
		}
		else if (this.scale_button == s) {
			this.canvas.scale2x_modification();
		}
	}

	public void composite(BufferedImage bi) {
		this.comp = new JFrame("Composite");
	    this.comp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.comp_canvas = new Picture(bi);
		this.comp_canvas.setPreferredSize(this.comp_canvas.getPreferredSize());
	    this.comp.getContentPane().add(this.comp_canvas);
	    this.comp.pack();
	    this.comp.setVisible(true);
	}

	public String askForFile(String title) {
		this.fd = new FileDialog(this.frame, title, FileDialog.LOAD);
		this.fd.setFile("*.jpg");
		this.fd.setVisible(true);
		return this.fd.getDirectory() + this.fd.getFile();
	}

	public void askSaveFile(String title) {
		this.fd = new FileDialog(this.frame, title, FileDialog.SAVE);
		this.fd.setVisible(true);
		try {
			ImageIO.write(canvas.getDisplay(), "jpg", new File(this.fd.getDirectory() + this.fd.getFile()));
		} catch (IOException ioe) {
			System.out.println("problem saving image");
		}
	}

	public void askSaveFile(String title, BufferedImage bi) {
		this.fd = new FileDialog(this.frame, title, FileDialog.SAVE);
		this.fd.setVisible(true);
		try {
			ImageIO.write(bi, "jpg", new File(this.fd.getDirectory() + this.fd.getFile()));
		} catch (IOException ioe) {
			System.out.println("problem saving image");
		}
	}

	public static void main(String[] args) {
	    ImageProcessor ip = new ImageProcessor();
	    ip.createGUI();
	}
	
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider s = (JSlider)e.getSource();
			if (requan_slider == s && requan_check.isSelected()) {
				if (!s.getValueIsAdjusting()) {}
				else {
					canvas.reapply_quantize_modification(requan_slider.getValue());
				}
			}
			else if (threshold_slider == s && threshold_check.isSelected()) {
				if (!s.getValueIsAdjusting()) {}
				else {
					canvas.reapply_threshold_modification((double)threshold_slider.getValue());
				}
			}
			else if (brightness_slider == s && brightness_check.isSelected()) {
				if (!s.getValueIsAdjusting()) {}
				else {
					canvas.reapply_brightness_modification((double)brightness_slider.getValue()/100);
				}
			}
			else if (saturation_slider == s && saturation_check.isSelected()) {
				if (!s.getValueIsAdjusting()) {}
				else { 
					canvas.reapply_saturation_modification((double)saturation_slider.getValue()/100);
				}
			}
			else if (contrast_slider == s && contrast_check.isSelected()) {
				if (!s.getValueIsAdjusting()) {}
				else {
					canvas.reapply_contrast_modification((double)contrast_slider.getValue()/100);
				}
			} 
			else if (noise_slider == s && noise_check.isSelected()) {
				if (!s.getValueIsAdjusting()) {}
				else {
					canvas.reapply_noise_modification((double)noise_slider.getValue()/100);
				}
			} 
		}
	}
}