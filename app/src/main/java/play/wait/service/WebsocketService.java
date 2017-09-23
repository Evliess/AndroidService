package play.wait.service;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by guijj on 8/23/2017.
 */

public class WebsocketService {
    private WebSocketClient wsc;
    private final String serverUri = "ws://169.146.24.223:8887";
    //private final String serverUri = "ws://10.0.2.2:8887";
    //private final String serverUri = "ws://9.110.78.236:8887";
    private final Draft draft = new Draft_6455();

    private String msgFromServer;

    private WebSocketClient getInstance() {
        WebSocketImpl.DEBUG = false;
        try {
            wsc = new WebSocketClient(new URI(serverUri), draft) {
                @Override
                public void onMessage(String message) {
                    System.out.println("Get Message Form Server: " + message);
                    msgFromServer = message;
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    msgFromServer = "Connected Success!";
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    msgFromServer = "Close Success!" + reason;
                }

                @Override
                public void onError(Exception ex) {
                    msgFromServer = "Error!" + ex.getMessage();
                }
            };
        } catch (Exception e) {
            msgFromServer = e.getMessage();
            e.printStackTrace();
        }
        return wsc;
    }

    public void send(String message) {
        wsc.send(message);
    }

    public WebSocket.READYSTATE getReadState() {
        return wsc.getReadyState();
    }

    public void connect() {
        wsc = this.getInstance();
        this.wsc.connect();
    }

    public String getMsgFromServer() {
        return msgFromServer;
    }

    public void setMsgFromServer(String msgFromServer) {
        this.msgFromServer = msgFromServer;
    }
}
