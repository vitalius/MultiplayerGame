package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import net.GameState;


public class TcpServer extends Thread {
	
	private ServerSocket servSock;
	private GameState gs;
	
	public TcpServer(int p, GameState g) {
		gs = g;
		try {
			servSock = new ServerSocket(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		int msgSize; 
		byte[] buf = new byte[512]; 
		
		for(;;) {
			try {
				
				Socket clientSock = servSock.accept();
				InputStream in = clientSock.getInputStream();
				OutputStream out = clientSock.getOutputStream();

				while ((msgSize = in.read(buf)) != -1)
					out.write(buf, 0, msgSize);
				
				//System.out.println(new String(buf));
				gs.processAction(new String(buf));
				
				clientSock.close(); 
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}
}