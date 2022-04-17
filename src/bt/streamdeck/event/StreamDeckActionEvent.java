package bt.streamdeck.event;

/**
 * @author Lukas Hartwig
 * @since 17.04.2022
 */
public class StreamDeckActionEvent extends StreamDeckEvent
{
    protected String actionName;
    protected String context;

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }
}