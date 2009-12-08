package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpSender {

	public TcpSender() {}

	public void sendSocket(String ipAdrs, String sendString) {		
		try {
			//System.out.println(sendString);
			Socket socket = new Socket(ipAdrs, NetworkEngine.TCP_CLIENT_PORT);
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
