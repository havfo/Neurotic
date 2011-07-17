/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.Random;

import objects.City;
import objects.Neuron;
import util.NeuroticUtilities;

/**
 * @author fosstvei
 * 
 */
public class Neurotic {

	private Neuron[] neuronList;

	private int neighbourhoodSize;

	private int origNeigh;

	private City[] cityList;

	private City[] cityPath;

	private int[] closestNeuronToCityIndex;

	private String path;

	private double learningRate;

	private double origLearn;

	private int iterations;

	private int k;

	private NeuroticGraph gui;

	public Neurotic(int neurons, int neighbourhoodSize, int iterations, int k,
			double learningRate, String path) {
		neuronList = new Neuron[neurons];
		this.neighbourhoodSize = neighbourhoodSize;
		this.origNeigh = neighbourhoodSize;
		this.learningRate = learningRate;
		this.origLearn = learningRate;
		this.iterations = iterations;
		this.k = k;
		this.path = path;

		init();
	}

	private void init() {
		Random r = new Random();

		cityList = NeuroticUtilities.getCityList(path);

		cityPath = new City[cityList.length];
		closestNeuronToCityIndex = new int[cityList.length];

		for (int i = 0; i < closestNeuronToCityIndex.length; i++) {
			closestNeuronToCityIndex[i] = 0;
			cityPath[i] = cityList[i];
		}

		for (int i = 0; i < neuronList.length; i++) {
			double x = r.nextDouble();
			double y = r.nextDouble();

			neuronList[i] = new Neuron(i, x, y);
		}

		gui = new NeuroticGraph(neuronList, cityPath);
	}

	private void expLRP(int generation) {
		learningRate = (origLearn * Math.exp(-1.0 * ((double) generation)
				/ ((double) iterations / 3.0))); // Divide by 3 to make it drop
													// more
	}

	private void neighbourhoodSize(int generation) {
		// Linear falloff
		// neighbourhoodSize = (int) (origNeigh - ((origNeigh * generation) /
		// ((double) iterations / 1.0)));

		// Gaussian function falloff
		// neighbourhoodSize = (int) (origNeigh * Math.exp(-1.0 * (generation *
		// generation) / (2 * (iterations / 3) * (iterations / 3))));

		// Inverse exponential falloff
		neighbourhoodSize = (int) ((origNeigh + (double) (origNeigh / 10)) / ((double) (1 / ((double) (iterations / 10) + 1)) * (double) ((generation + 1) + (double) (iterations / 10))))
				- (origNeigh / 10);

		// Ensure we don't stall with no neighbours
		if (neighbourhoodSize < 1) {
			neighbourhoodSize = 1;
		}
	}

	private void rewardNode(int nodeIndex, int distanceFromWinner, int cityIndex) {
		double gaussianLearningRate = learningRate;

		gaussianLearningRate = learningRate
				* Math.pow(
						Math.E,
						(-1.0 * (distanceFromWinner * distanceFromWinner) / ((double) neighbourhoodSize * ((double) neighbourhoodSize / 2.0))));

		double xReward = (cityList[cityIndex].getX() - neuronList[nodeIndex]
				.getX()) * gaussianLearningRate;
		neuronList[nodeIndex].setX(neuronList[nodeIndex].getX() + xReward);

		double yReward = (cityList[cityIndex].getY() - neuronList[nodeIndex]
				.getY()) * gaussianLearningRate;
		neuronList[nodeIndex].setY(neuronList[nodeIndex].getY() + yReward);
	}

	private void rewardNodes(int winnerIndex, int cityIndex, int generation) {

		int distFromWinner = 1;

		// Reward winner here

		rewardNode(winnerIndex, 0, cityIndex);

		// Iterate right
		for (int i = winnerIndex + 1; distFromWinner <= neighbourhoodSize; i++) {
			if (i >= neuronList.length) { // Start from left
				i = 0;
			} else {
				// Reward neuron here
				rewardNode(i, distFromWinner, cityIndex);

				distFromWinner++;
			}
		}

		distFromWinner = 1;

		// Iterate left
		for (int i = winnerIndex - 1; distFromWinner <= neighbourhoodSize; i--) {
			if (i < 0) { // Start from right
				i = neuronList.length;
			} else {
				// Reward neuron here
				rewardNode(i, distFromWinner, cityIndex);

				distFromWinner++;
			}
		}
	}

	private double getDistance(City c, Neuron n) {
		// Pythagoras
		double a = Math.abs(c.getX() - n.getX());
		double b = Math.abs(c.getY() - n.getY());

		return Math.sqrt((a * a) + (b * b));
	}

	private void next(int generation) {

		double minDistance;
		double distance;
		int index;

		// Iterate over cities and find winner neuron
		for (int i = 0; i < cityList.length; i++) {
			minDistance = Double.MAX_VALUE;
			distance = 0.0;
			index = 0;
			// Find closest neuron
			for (int j = 0; j < neuronList.length; j++) {
				distance = getDistance(cityList[i], neuronList[j]);
				if (distance < minDistance) {
					minDistance = distance;
					index = j;
				}
			}

			closestNeuronToCityIndex[i] = index;
		}

		// Let winner and neighbours learn
		for (int i = 0; i < closestNeuronToCityIndex.length; i++) {
			// Learn/reward

			rewardNodes(closestNeuronToCityIndex[i], i, generation);
		}
	}

	private double getTotalRealDistance() {
		ArrayList<City> cities = new ArrayList<City>();

		for (int i = 0; i < neuronList.length; i++) {
			for (int j = 0; j < closestNeuronToCityIndex.length; j++) {
				if (closestNeuronToCityIndex[j] == i) {
					cities.add(cityList[j]);
				}
			}
		}

		for (int i = 0; i < cities.size(); i++) {
			cityPath[i] = cities.get(i);
		}

		double dist = 0.0;

		for (int i = 0; i < cities.size() - 1; i++) {
			dist += getRealDistance(cities.get(i), cities.get(i + 1));
		}

		dist += getRealDistance(cities.get(0), cities.get(cities.size() - 1));

		return dist;
	}

	private double getRealDistance(City c1, City c2) {
		// Pythagoras
		double a = Math.abs(c1.getOrigX() - c2.getOrigX());
		double b = Math.abs(c1.getOrigY() - c2.getOrigY());

		return Math.sqrt((a * a) + (b * b));
	}

	public void run() {

		int generation = 0;
		
		while (generation < iterations) {
			next(generation);

			if (generation % k == 0) {
				
				System.out.println("Generation: " + (generation + 1));
				System.out.println("Total distance (D): " + getTotalRealDistance());
				System.out.println("Learning rate: " + learningRate);
				System.out.println("Neighbourhood size: " + neighbourhoodSize);
				gui.repaint();
				
//				JOptionPane.showMessageDialog(null, "Next iteration!");
			}
			
			gui.repaint();

			generation++;

			// Set new learning rate
			expLRP(generation);

			// Set new neighbourhood size
			neighbourhoodSize(generation);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int neu = 0;
		int nei = 0;
		int ite = 0;
		int k = 0;
		double lea = 0.0;
		String pat = "";

		try {
			neu = Integer.parseInt(args[0]);
			nei = Integer.parseInt(args[1]);
			ite = Integer.parseInt(args[2]);
			k = Integer.parseInt(args[3]);
			lea = Double.parseDouble(args[4]);
			pat = args[5];
		} catch (Exception e) {
			System.out.println("Wrong input!");
			System.exit(0);
		}

		Neurotic n = new Neurotic(neu, nei, ite, k, lea, pat);
		n.run();
	}

}
