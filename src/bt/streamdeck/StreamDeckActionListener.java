package bt.streamdeck;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
public interface StreamDeckActionListener
{
    public void onKeyDown(String actionName);

    public void onKeyUp(String actionName);
}