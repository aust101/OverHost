package com.overaitis.overhost;

import java.io.IOException;

public class OverHost {
	public static int PORT = 6969;
	public static void main(String[] args) {
		try {
			new Server(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
