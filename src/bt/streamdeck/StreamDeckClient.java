package bt.streamdeck;

import bt.async.Data;
import bt.io.json.JSON;
import bt.log.Log;
import bt.remote.socket.ObjectClient;
import bt.remote.socket.data.DataProcessor;
import bt.streamdeck.event.*;

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

    public void setListener(StreamDeckActionListener listener)
    {
        this.listener = listener;
    }

    @Override
    public Object process(Data data)
    {
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

        return null;
    }
}