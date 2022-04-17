package bt.streamdeck.exc;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
public class StreamDeckSendException extends RuntimeException
{
    public StreamDeckSendException(String message)
    {
        super(message);
    }

    public StreamDeckSendException(String message, Throwable cause)
    {
        super(message, cause);
    }
}