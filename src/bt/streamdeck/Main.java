package bt.streamdeck;

import bt.console.input.ArgumentParser;
import bt.console.input.ValueArgument;
import bt.io.json.JSON;
import bt.log.ConsoleLoggerHandler;
import bt.log.FileLoggerHandler;
import bt.log.Log;
import bt.streamdeck.exc.StreamDeckArgumentMissingException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Lukas Hartwig
 * @since 13.04.2022
 */
public class Main
{
    public static void main(String[] args) throws IOException
    {
        Log.createDefaultLogFolder();
        Log.configureDefaultJDKLogger(new ConsoleLoggerHandler(), new FileLoggerHandler());

        var config = new StreamDeckConfiguration();

        ArgumentParser parser = new ArgumentParser("-");

        ValueArgument portCmd = new ValueArgument("port");
        portCmd.onExecute(port -> {
            Log.debug("Using StreamDeck port: {}", port);
            config.setStreamDeckPort(Integer.parseInt(port));
        });
        portCmd.onMissing(() -> {
            throw new StreamDeckArgumentMissingException("port missing.");
        });
        parser.register(portCmd);

        ValueArgument serverPortCmd = new ValueArgument("serverPort");
        serverPortCmd.onExecute(port -> {
            Log.debug("Using server port: {}", port);
            config.setServerPort(Integer.parseInt(port));
        });
        serverPortCmd.onMissing(() -> {
            throw new StreamDeckArgumentMissingException("serverPort missing.");
        });
        parser.register(serverPortCmd);

        ValueArgument uuidCmd = new ValueArgument("pluginUUID");
        uuidCmd.onExecute(uuid -> {
            Log.debug("Received StreamDeck UUID: {}", uuid);
            config.setUuid(uuid);
        });
        uuidCmd.onMissing(() -> {
            throw new StreamDeckArgumentMissingException("pluginUUID missing.");
        });
        parser.register(uuidCmd);

        ValueArgument registerCmd = new ValueArgument("registerEvent");
        registerCmd.onExecute(event -> {
            Log.debug("Received StreamDeck registerEvent: {}", event);
            config.setRegisterEvent(event);
        });
        registerCmd.onMissing(() -> {
            throw new StreamDeckArgumentMissingException("registerEvent missing.");
        });
        parser.register(registerCmd);

        ValueArgument infoCmd = new ValueArgument("info");
        infoCmd.onExecute(info -> {
            JSONObject json = JSON.parse(info);
            Log.debug("Received StreamDeck info: {}", json.toString(4));
            config.setInfo(json);
        });
        parser.register(infoCmd);

        parser.registerDefaultHelpArgument("h", "help");

        try
        {
            parser.parse(args);

            new StreamDeckInterface(config.getStreamDeckPort(),
                                    config.getUuid(),
                                    config.getInfo(),
                                    config.getRegisterEvent(),
                                    config.getServerPort());
        }
        catch (StreamDeckArgumentMissingException | URISyntaxException | IOException e)
        {
            Log.error("Failed to setup StreamDeckInterface", e);
        }
    }
}