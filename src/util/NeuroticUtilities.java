package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Scanner;

import objects.City;

/**
 * @version 19 okt 2009
 * @author Håvar Aambø Fosstveit, Andreas Dybdahl
 */

public class NeuroticUtilities {
	
	public static City[] getCityList(String path) {
		File tspFile = new File(path);
		FileReader fir = null;
		BufferedReader bis = null;
		double[][] ret = null;
		double[][] orig = null;
		City[] cityList = null;

		String line = null;

		try {
			fir = new FileReader(tspFile);

			LineNumberReader ln = new LineNumberReader(fir);

			int count = 0;

			while (ln.readLine() != null) {
				count++;
			}
			
			ln.close();
			
			fir = new FileReader(tspFile);
			
			ret = new double[count - 8][2];
			orig = new double[count - 8][2];

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedReader(fir);

			for (int i = 0; i < 7; i++) {
				bis.readLine();
			}

			int ind = 0;
			// bis.available() returns 0 if the file does not have more lines.
			while ((line = bis.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				scanner.useDelimiter(" ");

				if (line.charAt(0) == 'E') {
					break;
				}

				scanner.nextInt();
				double x = scanner.nextDouble();
				double y = scanner.nextDouble();
				
				ret[ind] = new double[] {x, y};
				orig[ind] = new double[] {x, y};

				scanner.close();
				ind++;
			}

			fir.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double maxX = 0.0;
		double maxY = 0.0;
		double minX = 9999999999.0;
		double minY = 9999999999.0;
		
		for (int i = 0; i < ret.length; i++) {
			if (ret[i][0] > maxX) {
				maxX = ret[i][0];
			}
			
			if (ret[i][1] > maxY) {
				maxY = ret[i][1];
			}
			
			if (ret[i][0] < minX) {
				minX = ret[i][0];
			}
			
			if (ret[i][1] < minY) {
				minY = ret[i][1];
			}
		}
		
		maxX = maxX - minX;
		maxY = maxY - minY;
		
		for (int i = 0; i < ret.length; i++) {
			ret[i][0] -= minX;
			ret[i][1] -= minY;
			
			ret[i][0] = ret[i][0] / maxX;
			ret[i][1] = ret[i][1] / maxY;
		}
		
		cityList = new City[ret.length];
		
		for (int i = 0; i < ret.length; i++) {
			cityList[i] = new City(ret[i][0], ret[i][1], orig[i][0], orig[i][1]);
		}
		
		return cityList;
	}
	
}
