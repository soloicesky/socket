
package com.soloicesky.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.soloicesky.timer.Timer;

/**
 * 
 * @filname CommonSocket.java
 * @description A C style socket lib for java
 * @author soloicesky
 * @data 2013-10-17		@time ÏÂÎç2:53:12
 */

public class CommonSocket {
	public static final int ERR_PARAMETERS = -1;
	public static final int ERR_CONNECT_FAILED = -2;
	public static final int ERR_BUILD_CONNECTOIN_FIRST = -3;
	public static final int ERR_SEND_REQ_FAILED = -4;
	public static final int ERR_RECEIVE_RSP_FAILED = -5;
	public static final int ERR_RECEIVE_TIMEOUT = -6;

	private static Socket sk = null;
	private static String hostIP = null;
	private static int hostPort;
	private static InputStream IS = null;
	private static OutputStream OS = null;
	
	public CommonSocket()
	{
		
	}

	/**
	 * 
	 * @param ip remote  host ip
	 * @param port remote host port
	 * @return 0 connect successful; others are failed
	 */
	public int connectToHost(String ip, int port) {
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

	/**
	 * 
	 * @param reqMsg request message that will be send to the remote 
	 * @param reqMsgLen request message's length
	 * @param timeOutMs NO use just in case for reserved
	 * @return 0 send successful; others are failed
	 */
	public int sendBytes(byte[] reqMsg, int reqMsgLen, long timeOutMs) {
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

	/**
	 * 
	 * @param rspMsg storage the data
	 * @param wantedLen expected receive data length
	 * @param timeOutMs receive timeout milliseconds
	 * @return < 0 some errors occurs; >=0 received data's length
	 */
	public int receiveBytes(byte[] rspMsg, int wantedLen, long timeOutMs) {
		int reveiveLen = 0;
		int availabelLen = 0;
		int readLenPerTime = 0;
		Timer tm = null;

		if (rspMsg == null) {
			return ERR_PARAMETERS;
		}

		if (IS == null) {
			return ERR_BUILD_CONNECTOIN_FIRST;
		}

		if (timeOutMs > 0) {
			tm = new Timer(timeOutMs);
			tm.start();
		}

		while (wantedLen > 0) {

			if (timeOutMs > 0) {
				if (tm.timeOut()) {
					return ERR_RECEIVE_TIMEOUT;
				}
			}

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
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return reveiveLen;
	}

	/**
	 * close the socket
	 */
	public void close() {
		try {
			OS.close();
			OS = null;
			IS.close();
			IS = null;
			sk.close();
			sk = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
