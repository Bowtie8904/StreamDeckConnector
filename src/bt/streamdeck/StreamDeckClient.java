package bt.streamdeck;

import bt.async.Data;
import bt.io.json.JSON;
import bt.io.json.JSONBuilder;
import bt.log.Log;
import bt.remote.socket.ObjectClient;
import bt.remote.socket.data.DataProcessor;
import bt.streamdeck.event.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
public class StreamDeckClient extends ObjectClient implements DataProcessor
{
    private Map<String, String> actions = new HashMap<>();
    private StreamDeckActionListener listener;

    public StreamDeckClient(String host, int port)
    {
        super(host, port);
        configureDefaultEventListeners();
        setDataProcessor(this);
        setSingleThreadProcessing(true);
        autoReconnect(-1);
    }

    public void setTitle(String actionName, String title)
    {
        String context = this.actions.get(actionName.toLowerCase());

        if (context != null)
        {
            var json = new JSONBuilder().put("event", "setTitle")
                                        .put("context", context)
                                        .put("payload", new JSONBuilder().put("title", title)
                                                                         .put("target", "both")
                                                                         .put("state", 0).toJSON()).toString();

            Log.debug("Sending title update: {}", json);

            try
            {
                send(json);
            }
            catch (IOException e)
            {
                Log.error("Failed to send", e);
            }
        }
    }

    public void setState(String actionName, int state)
    {
        Log.entry(actionName, state);

        String context = this.actions.get(actionName.toLowerCase());

        if (context != null)
        {
            var json = new JSONBuilder().put("event", "setState")
                                        .put("context", context)
                                        .put("payload", new JSONBuilder().put("state", state).toJSON()).toString();

            Log.debug("Sending state update: {}", json);

            try
            {
                send(json);
            }
            catch (IOException e)
            {
                Log.error("Failed to send", e);
            }
        }

        Log.exit();
    }

    public void setListener(StreamDeckActionListener listener)
    {
        this.listener = listener;
    }

    @Override
    public Object process(Data data)
    {
        Log.entry(data.get());

        Object rawData = data.get();

        if (rawData instanceof WillAppearEvent event)
        {
            this.actions.put(event.getActionName().toLowerCase(), event.getContext());
        }
        else if (rawData instanceof WillDisappearEvent event)
        {
            this.actions.remove(event.getActionName().toLowerCase());
        }
        else if (rawData instanceof KeyDownEvent event)
        {
            if (this.listener != null)
            {
                this.listener.onKeyDown(event.getActionName());
            }
        }
        else if (rawData instanceof KeyUpEvent event)
        {
            if (this.listener != null)
            {
                this.listener.onKeyUp(event.getActionName());
            }
        }
        else if (rawData instanceof MessageEvent event)
        {
            Log.debug(JSON.parse(event.getMessage()).toString(4));
        }
        else
        {
            Log.debug(rawData.toString());
        }

        Log.exit();

        return null;
    }
}