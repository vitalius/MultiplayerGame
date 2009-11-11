import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is a comment
 * 
 * @author vitaliy
 *
 */

public class Client {
	public static void main (String[] vars) {

		Socket kkSocket = null;
		
	    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			kkSocket = new Socket("localhost", 4444);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
		                            kkSocket.getInputStream()));
			
			String fromServer;
			String fromUser;
			
			while ((fromServer = in.readLine()) != null) {
			    System.out.println("Server: " + fromServer);
			    if (fromServer.equals("Bye."))
			        break;
			    fromUser = stdIn.readLine();
			    if (fromUser != null) {
			        System.out.println("Client: " + fromUser);
			        out.println(fromUser);
			    }
			}
			

		} catch (IOException e) {
			System.exit(1);
		}
	}
}