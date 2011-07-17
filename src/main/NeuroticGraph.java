package main;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import objects.City;
import objects.Neuron;

public class NeuroticGraph extends JPanel {

	private static final long serialVersionUID = 6705155784890009248L;

	private Neuron[] n;
	
	private City[] path;

	private JFrame f;

	private int width = 640;
	
	private int height = 480;

	public NeuroticGraph(Neuron[] n, City[] path) {
		this.n = n;
		this.path = path;

		initGUI();
	}

	private void initGUI() {
		f = new JFrame("Neurotic");

		setSize(width, height);
		f.setContentPane(this);
		f.setSize(width + 50, height + 50);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		f.setVisible(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, width, height);
		// Draw cities
		
		g.setColor(Color.RED);
		for (int i = 0; i < path.length; i++) {
			g.fillRect((int) (path[i].getX() * width) - 2,
					(int) (path[i].getY() * height) - 2, 4, 4);
		}
		
		for (int i = 0; i < path.length - 1; i++) {
			g.drawLine((int) (path[i].getX() * width), (int) (path[i].getY() * height), (int) (path[i + 1].getX() * width), (int) (path[i + 1].getY() * height));
		}

		g.setColor(Color.GREEN);
		// Draw neurons
		for (int i = 0; i < n.length; i++) {
			g.fillOval((int) (n[i].getX() * width), (int) (n[i].getY() * height),
					4, 4);
		}
		
		for (int i = 0; i < n.length - 1; i++) {
			g.drawLine((int) (n[i].getX() * width), (int) (n[i].getY() * height), (int) (n[i + 1].getX() * width), (int) (n[i + 1].getY() * height));
		}
	}
}
