package server;

import net.NetStateManager;
import net.Protocol;

public class NetworkEngine {
	public static final int BCAST_BUF_SIZE = 4096;
	public static final int TCP_BUF_SIZE = 1024;
	
	public static final int BCAST_PORT = 5000;
	public static final int TCP_PORT   = 5001;
	
	private Broadcaster bcaster;
	private NetStateManager nsm;
	private ServerGameState sgm;
	private Protocol prot;
	private TcpServer tcpControl;
	
	public NetworkEngine(NetStateManager n, ServerGameState gm) {
		nsm = n;
		sgm = gm;
		prot = new Protocol();
		bcaster = new Broadcaster(BCAST_PORT);
		
		//bcaster.addIP(1, "127.0.0.1");
		//bcaster.addIP(2, "10.97.53.61");
		
		tcpControl = new TcpServer(TCP_PORT, nsm);
		tcpControl.start();
	}
	
	public void update() {
		bcaster.spam(prot.encode(nsm.getState()));
	}
}
