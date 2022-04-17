package bt.streamdeck.exc;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
public class StreamDeckArgumentMissingException extends RuntimeException
{
    public StreamDeckArgumentMissingException(String message)
    {
        super(message);
    }
}
