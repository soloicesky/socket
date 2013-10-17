package com.soloicesky.socket;

public class TestMain {
	public static void main(String[] args) {
		if (CommonSocket.connectToHost("127.0.0.1", 8776)  == 0) {
			System.out.println("connected!");
			
			if (CommonSocket.sendBytes("hello world".getBytes(), "hello world".length(), 0) ==0) {
				System.out.println("sent!");
			}
			
			byte[] rspMsg = new byte[1024];
			int wantedLen = 10;
			int timeOutMs = 10 * 1000;
			
			if (CommonSocket.receiveBytes(rspMsg, wantedLen, timeOutMs) > 0) {
				System.out.println(new String(rspMsg, 0, wantedLen));
			}
			
			CommonSocket.close();
		}
	}
}
