package com.mobiquity.packer;

import com.mobiquity.exception.APIException;

public class PackerMain {

	public static void main(String[] args) throws APIException {
		if (args.length != 1) {
			System.out.println("wrong input file path, please give correct path");
			System.exit(1);
		}

		System.out.println(Packer.pack(args[0]));
	}
}
