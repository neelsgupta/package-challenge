package com.mobiquity.packer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.mobiquity.exception.APIException;

public class Packer {

	private static final String INDEX = "index";
	private static final String WEIGHT = "weight";
	private static final String COST = "cost";
	private static final String PACKAGE_REGEX = "\\((?<" + INDEX + ">\\d+)\\,(?<" + WEIGHT
			+ ">\\d+(\\.\\d{1,2})?)\\,€(?<" + COST + ">\\d+(\\.\\d{1,2})?)\\)";
	private static final int MAX_WEIGHT = 100 * 100;
	private static final int MAX_ITEMS = 15;
	private static final int MAX_COST = 100 * 100;
	
  private Packer() {
  }

  public static String pack(String filePath) throws APIException {
	  return parsedInputFile(filePath).stream()
				.map(inputLine -> thingsToPutInPackage(inputLine.getMaxWeight(), inputLine.getPackages()))
				.collect(Collectors.joining("\n"));
  }
  
  /**
	 * This method provide parsed input for each line and map to java object.
	 * 
	 * @param filePath
	 * @return
	 * @throws APIException
	 */
	private static List<InputLine> parsedInputFile(String filePath) throws APIException {
		List<InputLine> inputLineList = new ArrayList<>();

		try (FileInputStream inputStream = new FileInputStream(filePath)) {
			try (Scanner scanner = new Scanner(inputStream)) {
				for (long lineNumber = 0; scanner.hasNext(); lineNumber++) {
					String line = scanner.nextLine();
					inputLineList.add(validateAndParseInputLine(lineNumber, line));
				}
			}
		} catch (IOException e) {
			throw new APIException(e);
		}

		return inputLineList;
	}

	/**
	 * This validates the input as per the given rules if it doesn't match it thorws API exception.
	 * 
	 * @param lineNumber
	 *            : number of line is going to validate
	 * @param line
	 *            : input line
	 * @return
	 * @throws APIException
	 *             : exception thrown as per validation.
	 */
	private static InputLine validateAndParseInputLine(long lineNumber, String line) throws APIException {
		String[] splited = line.split(":");

		if (splited.length != 2) {
			throw new APIException("Line must contain exactly one semicolan `:`", line, lineNumber);
		}

		final int maxWeight;

		try {
			maxWeight = (int) (Double.parseDouble(splited[0]) * 100);
		} catch (NumberFormatException e) {
			throw new APIException("Left side of semicolan `:` must be a number", e, line, lineNumber);
		}

		Pattern pattern = Pattern.compile(PACKAGE_REGEX);
		Matcher matcher = pattern.matcher(splited[1]);
		int lastEnd = 0;
		List<Package> packages = new ArrayList<>();

		while (matcher.find()) {
			if (matcher.start() != lastEnd + 1 || splited[1].charAt(lastEnd) != ' ') {
				throw new APIException(String.format(
						"Right side of semicolan `:` must be in the following pattern (%s) separated by space",
						PACKAGE_REGEX), line, lineNumber);
			}

			try {
				Integer index = Integer.valueOf(matcher.group(INDEX));
				int weight = (int) (Double.valueOf(matcher.group(WEIGHT)) * 100);
				Double cost = Double.valueOf(matcher.group(COST));

				if (index > MAX_ITEMS || index < 0) {
					throw new APIException(String.format("input index should be in range (1, %d)", MAX_ITEMS), line,
							lineNumber);
				}

				if (weight > MAX_WEIGHT || weight < 0) {
					throw new APIException(String.format("input weight should be in range (0, %f)", MAX_WEIGHT), line,
							lineNumber);
				}

				if (cost > MAX_COST || cost < 0) {
					throw new APIException(String.format("input cost should be in range (0, %f)", MAX_COST), line,
							lineNumber);
				}

				packages.add(new Package(index, weight, cost));

			} catch (NumberFormatException | IllegalFormatConversionException exception) {
				throw new APIException(exception, line, lineNumber);
			}

			lastEnd = matcher.end();
		}

		if (lastEnd != splited[1].length()) {
			throw new APIException("unexpected characters in the end of the line", line, lineNumber);
		}

		long[] indexes = packages.stream().mapToLong(Package::getIndex).toArray();

		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] != i + 1) {
				throw new APIException("The indexes in not order well or some index is missing", line, lineNumber);
			}
		}

		return new InputLine(maxWeight, packages);
	}

	
	private static String thingsToPutInPackage(int maxWeight, List<Package> packages) {
		int packageSize = packages.size() + 1;
		int weight = maxWeight + 1;
		double[][] a = new double[packageSize][weight];

		for (int i = 1; i < packageSize; i++) {
			Package pac = packages.get(i - 1);

			for (int j = 1; j < weight; j++) {
				if (pac.getWeight() > j) {
					a[i][j] = a[i - 1][j];
				} else {
					a[i][j] = Math.max(a[i - 1][j], a[i - 1][j - pac.getWeight()] + pac.getCost());
				}
			}
		}

		List<Integer> indexes = new ArrayList<>();
		int j = maxWeight;
		double totalcost = a[packageSize - 1][weight - 1];
		for (; j > 0 && a[packageSize - 1][j - 1] == totalcost; j--)
			;

		for (int i = packageSize - 1; i > 0; i--) {
			if (a[i][j] != a[i - 1][j]) {
				indexes.add(packages.get(i - 1).getIndex());
				j -= packages.get(i - 1).getWeight();
			}
		}

		String result = indexes.stream().mapToInt(i -> i).sorted().mapToObj(index -> Integer.toString(index))
				.collect(Collectors.joining(","));
		return result.isEmpty() ? "-" : result;
	}
}
