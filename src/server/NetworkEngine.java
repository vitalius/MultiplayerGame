package server;

import net.Protocol;

public class NetworkEngine {
	public static final int BCAST_BUF_SIZE = 8192;
	public static final int TCP_BUF_SIZE = 1024;
	
	public static final int BCAST_PORT = 49725;
	public static final int TCP_PORT   = 49726;
	public static final int TCP_CLIENT_PORT   = 49727;
	
	private Broadcaster bcaster;
	private Server gameserver;
	private Protocol prot;
	private TcpListener tcpControl;
	
	public NetworkEngine(Server gs) {
		gameserver = gs;
		prot = new Protocol();
		bcaster = new Broadcaster(BCAST_PORT);
		
		//bcaster.addIP(1, "127.0.0.1");
		//bcaster.addIP(2, "10.97.53.61");
		
		tcpControl = new TcpListener(TCP_PORT, gameserver);
		tcpControl.start();
	}
	
	public void addPlayer(int id, String ip) {
		bcaster.addIP(id, ip);
	}
	
	public void update() {
		bcaster.spam(prot.encode(gameserver.gameState.getNetState()));
	}
}
