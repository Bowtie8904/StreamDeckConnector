package bt.streamdeck.event;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
public class MessageEvent extends StreamDeckEvent
{
    private String message;

    public MessageEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
