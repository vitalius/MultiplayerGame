package clients;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {
	
	private Socket socket;
	private String myIP;
	private String serverIP;
	private int port;

	public TcpClient(String server, int p) {
		serverIP = server;
		port = p;
		try {
			socket = new Socket(serverIP, port);
			myIP = socket.getInetAddress().toString().substring(1);
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSocket(String sendString) {
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
	
	public String getMyIP() {
		return myIP;
	}
}
