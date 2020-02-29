package com.mobiquity.packer;

import java.util.List;

public class InputLine {
	private int maxWeight;
	private List<Package> packageList;

	public InputLine(int maxWeight, List<Package> packages) {
		super();
		this.maxWeight = maxWeight;
		this.packageList = packages;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public List<Package> getPackages() {
		return packageList;
	}

}
