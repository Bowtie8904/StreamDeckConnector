package bt.streamdeck;

import org.json.JSONObject;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
class StreamDeckConfiguration
{
    private int streamDeckPort;
    private int serverPort;
    private String uuid;
    private JSONObject info;
    private String registerEvent;

    public int getStreamDeckPort()
    {
        return streamDeckPort;
    }

    public void setStreamDeckPort(int streamDeckPort)
    {
        this.streamDeckPort = streamDeckPort;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public JSONObject getInfo()
    {
        return info;
    }

    public void setInfo(JSONObject info)
    {
        this.info = info;
    }

    public String getRegisterEvent()
    {
        return registerEvent;
    }

    public void setRegisterEvent(String registerEvent)
    {
        this.registerEvent = registerEvent;
    }
}