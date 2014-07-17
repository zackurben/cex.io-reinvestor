package test;

import utils.Config;

public class tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Config cfg = Config.getInstance();
		System.out.println(cfg.getNMCMinOrder());
		System.out.println(cfg.getBTCMinOrder());

	}

}
