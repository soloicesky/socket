package com.soloicesky.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommonSocket {
	public static final int ERR_PARAMETERS = -1;
	public static final int ERR_CONNECT_FAILED = -2;
	public static final int ERR_BUILD_CONNECTOIN_FIRST = -3;
	public static final int ERR_SEND_REQ_FAILED = -4;
	public static final int ERR_RECEIVE_RSP_FAILED = -5;

	private static Socket sk = null;
	private static String hostIP = null;
	private static int hostPort;
	private static InputStream IS = null;
	private static OutputStream OS = null;

	public static int connectToHost(String ip, int port) {
		if (sk != null) {
			if (!ip.equals(hostIP) || port != hostPort) {
				if (IS != null) {
					try {
						IS.close();
						OS.close();
						sk.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else { // already connected to the host
				return 0;
			}
		}

		try {
			sk = new Socket(ip, port);

			if (sk != null) {
				IS = sk.getInputStream();
				OS = sk.getOutputStream();

				if (IS == null || OS == null) {
					return ERR_CONNECT_FAILED;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERR_CONNECT_FAILED;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERR_CONNECT_FAILED;
		}

		return 0;
	}

	public static int sendBytes(byte[] reqMsg, int reqMsgLen, long timeOutMs) {
		if (reqMsg == null || reqMsgLen <= 0) {
			return ERR_PARAMETERS;
		}

		if (OS == null) {
			return ERR_BUILD_CONNECTOIN_FIRST;
		}

		try {
			OS.write(reqMsg, 0, reqMsgLen);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ERR_SEND_REQ_FAILED;
		}

		return 0;
	}

	public static int receiveBytes(byte[] rspMsg, int wantedLen, long timeOutMs) {
		int reveiveLen = 0;
		int availabelLen = 0;
		int readLenPerTime = 0;

		if (rspMsg == null) {
			return ERR_PARAMETERS;
		}

		if (IS == null) {
			return ERR_BUILD_CONNECTOIN_FIRST;
		}

		while (wantedLen > 0) {
			try {
				availabelLen = IS.available();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ERR_RECEIVE_RSP_FAILED;
			}

			if (availabelLen > 0) {
				try {
					readLenPerTime = IS.read(rspMsg, reveiveLen, wantedLen);
					wantedLen -= readLenPerTime;
					reveiveLen += readLenPerTime;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return 0;
	}
}
