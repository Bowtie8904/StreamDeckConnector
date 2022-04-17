package bt.streamdeck;

import bt.async.Data;
import bt.io.json.JSON;
import bt.io.json.JSONBuilder;
import bt.log.Log;
import bt.remote.socket.Server;
import bt.remote.socket.data.DataProcessor;
import bt.remote.socket.evnt.server.NewClientConnection;
import bt.remote.socket.evnt.server.ServerClientKilled;
import bt.streamdeck.event.*;
import bt.streamdeck.exc.StreamDeckSendException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukas Hartwig
 * @since 13.04.2022
 */
class StreamDeckInterface extends WebSocketClient implements DataProcessor
{
    private int port;
    private String registerEvent;
    private JSONObject info;
    private String uuid;
    private Map<String, String> actions = new HashMap<>();
    private Server server;

    protected StreamDeckInterface(int streamDeckPort, String uuid, JSONObject info, String registerEvent, int serverPort) throws URISyntaxException, IOException
    {
        super(new URI("ws://127.0.0.1:" + streamDeckPort + "/"));
        this.port = streamDeckPort;
        this.uuid = uuid;
        this.info = info;
        this.registerEvent = registerEvent;
        this.server = new Server(serverPort);
        this.server.configureDefaultEventListeners();
        this.server.getEventDispatcher().subscribeTo(NewClientConnection.class, this::onNewClient);
        this.server.getEventDispatcher().subscribeTo(ServerClientKilled.class, this::onClientKilled);
        this.server.setName("StreamDeckInterface " + uuid);
        this.server.start();
    }

    protected void onClientKilled(ServerClientKilled e)
    {
        showAlert();
    }

    protected void showAlert()
    {
        for (String context : this.actions.values())
        {
            send(new JSONBuilder().put("event", "showAlert")
                                  .put("context", context).toString());
        }
    }

    protected void showOk()
    {
        for (String context : this.actions.values())
        {
            send(new JSONBuilder().put("event", "showOk")
                                  .put("context", context).toString());
        }
    }

    protected void onNewClient(NewClientConnection e)
    {
        e.getClient().setDataProcessor(this);

        for (String actionName : this.actions.keySet())
        {
            var event = new WillAppearEvent();
            event.setActionName(actionName);
            event.setContext(this.actions.get(actionName));

            try
            {
                e.getClient().send(event);
            }
            catch (IOException ex)
            {
                throw new StreamDeckSendException("Failed to send action to client " + e.getClient().getHost() + ":" + e.getClient().getPort(), ex);
            }
        }

        showOk();
    }

    @Override
    public Object process(Data data)
    {
        Object rawData = data.get();

        if (rawData instanceof String)
        {
            send((String)rawData);
        }

        return null;
    }

    public synchronized void onMessage(String message)
    {
        JSONObject json = JSON.parse(message);

        String action = "";
        String context = "";
        String eventName = "";
        StreamDeckEvent event = null;

        if (json.has("action"))
        {
            action = json.getString("action");
        }

        if (json.has("context"))
        {
            context = json.getString("context");
        }

        if (json.has("event"))
        {
            eventName = json.getString("event");
        }

        if (eventName.equalsIgnoreCase("willAppear"))
        {
            this.actions.put(action.toLowerCase(), context);
            event = new WillAppearEvent();
            ((StreamDeckActionEvent)event).setActionName(action.toLowerCase());
            ((StreamDeckActionEvent)event).setContext(context);
        }
        else if (eventName.equalsIgnoreCase("willDisappear"))
        {
            this.actions.remove(action.toLowerCase());
            event = new WillDisappearEvent();
            ((StreamDeckActionEvent)event).setActionName(action.toLowerCase());
            ((StreamDeckActionEvent)event).setContext(context);
        }
        else if (eventName.equalsIgnoreCase("keyDown"))
        {
            event = new KeyDownEvent();
            ((StreamDeckActionEvent)event).setActionName(action.toLowerCase());
            ((StreamDeckActionEvent)event).setContext(context);
        }
        else if (eventName.equalsIgnoreCase("keyUp"))
        {
            event = new KeyDownEvent();
            ((StreamDeckActionEvent)event).setActionName(action.toLowerCase());
            ((StreamDeckActionEvent)event).setContext(context);
        }
        else
        {
            event = new MessageEvent(message);
        }

        for (var client : this.server.getClients())
        {
            try
            {
                client.send(event);
            }
            catch (IOException e)
            {
                throw new StreamDeckSendException("Failed to send event to client " + client.getHost() + ":" + client.getPort(), e);
            }
        }
    }

    public void onOpen(ServerHandshake serverHandshake)
    {
        Log.entry();

        send(new JSONBuilder().put("event", this.registerEvent)
                              .put("uuid", this.uuid)
                              .toString());

        Log.info("Connected to StreamDeck. " + this);

        Log.exit();
    }

    public void onClose(int code, String reason, boolean remote)
    {
        Log.entry(code, reason, remote);

        Log.info("Closed connection to StreamDeck: {} {} {}", code, reason, remote);

        System.exit(0);

        Log.exit();
    }

    public void onError(Exception e)
    {
        Log.entry(e);

        Log.error("Error in StreamDeck connection", e);

        Log.exit();
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getRegisterEvent()
    {
        return registerEvent;
    }

    public void setRegisterEvent(String registerEvent)
    {
        this.registerEvent = registerEvent;
    }

    public JSONObject getInfo()
    {
        return info;
    }

    public void setInfo(JSONObject info)
    {
        this.info = info;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public String toString()
    {
        return "StreamDeckInterface{" +
                "port=" + port +
                ", registerEvent='" + registerEvent + '\'' +
                ", info=" + info +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}