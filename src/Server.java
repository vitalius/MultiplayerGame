import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is a comment
 * 
 * @author vitaliy
 *
 */

public class Server {
	public static void main (String[] vars) {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		
		try {
		    serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
		    System.out.println("Could not listen on port: 4444");
		    System.exit(-1);
		}
		
		
		try {
		    clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(
	                clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
			                  new InputStreamReader(
			                      clientSocket.getInputStream()));
			String inputLine, outputLine;
			
			//initiate conversation with client
			StateProtocol kkp = new StateProtocol();
			outputLine = kkp.processInput(null);
			out.println(outputLine);
			
			while ((inputLine = in.readLine()) != null) {	
			   outputLine = kkp.processInput(inputLine);
			   out.println(outputLine);
			   
			   if (outputLine.equals("Bye."))
			     break;
			}
		    
		} catch (IOException e) {
		    System.out.println("Accept failed: 4444");
		    System.exit(-1);
		}
		
	}
}