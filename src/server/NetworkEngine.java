package server;

import net.GameStateManager;
import net.Protocol;

public class NetworkEngine {
	public static final int BCAST_BUF_SIZE = 4096;
	public static final int TCP_BUF_SIZE = 1024;
	
	public static final int BCAST_PORT = 5000;
	public static final int TCP_PORT   = 5001;
	
	private Broadcaster bcaster;
	private GameStateManager gm;
	private Protocol prot;
	private TcpServer tcpControl;
	
	public NetworkEngine(GameStateManager g) {
		gm = g;
		prot = new Protocol();
		bcaster = new Broadcaster(BCAST_PORT);
		
		bcaster.addIP(1, "127.0.0.1");
		//bcaster.addIP(2, "10.97.53.61");
		
		tcpControl = new TcpServer(TCP_PORT, gm);
		tcpControl.start();
	}
	
	public void update() {
		bcaster.spam(prot.encode(gm.getState()));
	}
}
