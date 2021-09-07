package webrtc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

public class WebrtcServer {

	private static List<SocketIOClient> clients = new ArrayList<SocketIOClient>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration config = new Configuration();
		//����������
		config.setHostname("192.168.6.103");
		//���ü����˿�
		config.setPort(3222);
		final SocketIOServer server = new SocketIOServer(config);
		//�������Ӽ����¼�
		server.addConnectListener(new ConnectListener() {

			@Override
			public void onConnect(SocketIOClient client) {
				if(clients.size()!=0){
					client.sendEvent("SomeOneOnline", "");
				}
				System.out.println(client.getSessionId().toString()+"������");
				clients.add(client);
			}
		});
		//�Ͽ����Ӽ����¼�
		server.addDisconnectListener(new DisconnectListener() {

			@Override
			public void onDisconnect(SocketIOClient client) {
				System.err.println(client.getSessionId().toString()+"�ѶϿ�");
				for (SocketIOClient c : clients) {
					if(client.getSessionId() == c.getSessionId()){
						clients.remove(c);
						break;
					}
				}
			}
		});

		server.addEventListener("SdpInfo", String.class,new DataListener<String>() {

			@Override
			public void onData(SocketIOClient client, String data,
					AckRequest ackSender) throws Exception {
				getOtherClient(client).sendEvent("SdpInfo",data);
			}
		});
		
		server.addEventListener("IceInfo", String.class,new DataListener<String>() {

			@Override
			public void onData(SocketIOClient client, String data,
					AckRequest ackSender) throws Exception {
				getOtherClient(client).sendEvent("IceInfo",data);
			}
		});
		
		//��������
		server.start();

	}
	
	private static SocketIOClient getOtherClient(SocketIOClient client){
		SocketIOClient c = null;
		for (SocketIOClient socketIOClient : clients) {
			if(socketIOClient != client){
				c = socketIOClient;
				break;
			}
		}
		return c;
	}

}
