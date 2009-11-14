package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import net.GameState;

import jig.engine.util.Vector2D;


public class TcpClient {
	
	private String serverIP;
	private int port;
	
	public TcpClient(String server, int p) {
		serverIP = server;
		port = p;
	}

	public void sendMove(int id, Vector2D p) {
		
		String sendString = id+":"+GameState.ACTION_MOVE+":"+p.getX()+":"+p.getY();
		
		Socket socket;
		try {
			socket = new Socket(serverIP, port);
			OutputStream out = socket.getOutputStream();
			out.write(sendString.getBytes());
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
