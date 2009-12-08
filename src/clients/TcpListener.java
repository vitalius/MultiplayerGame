package clients;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import server.NetworkEngine;

public class TcpListener extends Thread {
	
	private ServerSocket servSock;
	private LinkedBlockingQueue<String> msgQueue;
	
	public TcpListener(int p, LinkedBlockingQueue<String> q) {
		msgQueue = q;
		try {
			servSock = new ServerSocket(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		String msg;
		byte[] buf = new byte[NetworkEngine.TCP_BUF_SIZE]; 
		
		for(;;) {
			try {
				
				Socket clientSock = servSock.accept();
				InputStream in = clientSock.getInputStream();
				in.read(buf, 0, NetworkEngine.TCP_BUF_SIZE);
		
				msg = new String(buf);
				//System.out.println(msg + " len:"+msg.length());
				synchronized(msgQueue) {
					msgQueue.add(msg);
				}
				
				clientSock.close(); 
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
}