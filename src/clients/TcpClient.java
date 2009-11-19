package clients;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class TcpClient {
	
	private String serverIP;
	private int port;
	
	public TcpClient(String server, int p) {
		serverIP = server;
		port = p;
	}
	
	public void sendSocket(String sendString) {
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
