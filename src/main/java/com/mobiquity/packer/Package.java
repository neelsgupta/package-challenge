package com.mobiquity.packer;

public class Package {
	private int index;
	private int weight;
	private double cost;

	public Package(int index, int weight, double cost) {
		super();
		this.index = index;
		this.weight = weight;
		this.cost = cost;
	}

	public int getIndex() {
		return index;
	}

	public int getWeight() {
		return weight;
	}

	public double getCost() {
		return cost;
	}

}
