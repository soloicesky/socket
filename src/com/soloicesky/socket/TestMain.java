package com.soloicesky.socket;

public class TestMain {
	public static void main(String[] args) {

		CommonSocket cs = new CommonSocket();

		int ret = cs.connectToHost("127.0.0.1", 8776);

		System.out.println("ret = " + ret);

		if (ret == 0) {
			System.out.println("connected!");

			if (cs.sendBytes("hello world".getBytes(), "hello world".length(),
					0) == 0) {
				System.out.println("sent!");
			}

			byte[] rspMsg = new byte[1024];
			int wantedLen = 10;
			int timeOutMs = 10 * 1000;

			if (cs.receiveBytes(rspMsg, wantedLen, timeOutMs) > 0) {
				System.out.println(new String(rspMsg, 0, wantedLen));
			}

			cs.close();
		}
		
		while (ret ++ < 0) {
			System.out.println("" + ret);
			
		}
	}
}
