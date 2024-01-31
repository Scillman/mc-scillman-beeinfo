package eu.scillman.minecraft.beeinfo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import static eu.scillman.minecraft.beeinfo.BeeInfo.LOGGER;

public class ConfigurationFile
{
    /**
     * @brief The GSON type token of member {@link #items}.
     */
    private static final java.lang.reflect.Type STORAGE_TYPE = new TypeToken<HashMap<String, Object>>() {}.getType();

    /**
     * @brief The file that has been opened for reading/writing the configuration to/from.
     */
    private File file;

    /**
     * @brief A map of all the items inside the configuration file.
     */
    protected HashMap<String, ConfigurationItem> items;

    /**
     * @brief Constructs a new configuration file instance.
     */
    protected ConfigurationFile()
    {
        items = new HashMap<String, ConfigurationItem>();
    }

    /**
     * @brief Initialize the configuration.
     * @param modId The guid of the mod.
     */
    public void init(String modId)
    {
        try
        {
            file = getFile(modId);
            if (file == null)
            {
                return;
            }
        }
        catch (SecurityException ex)
        {
            file = null;
        }
    }

    /**
     * @brief Load the configuration file from storage.
     */
    public void load()
    {
        HashMap<String, Object> data;

        if (file == null)
        {
            return;
        }

        try (JsonReader reader = new JsonReader(new FileReader(file)))
        {
            Gson gson = new Gson();
            data = gson.fromJson(reader, STORAGE_TYPE);
            data.forEach((key, value) -> {
                if (items.containsKey(key))
                {
                    LOGGER.info("Overwriting setting of \"" + key + "\" to value of: " + value);
                    items.get(key).setValue(value);
                }
                else
                {
                    LOGGER.warn("Does not contain key => \"" + key + "\"");
                }
            });
        }
        catch (JsonSyntaxException ex)
        {
            LOGGER.warn("Encountered a Json syntax error while reading configuration file.");
            file = null;
        }
        catch (IOException ex)
        {
            // It should not reach here since the conditions to cause the
            // exception have been cleared by the getFile function.
            assert(false);
            file = null;
        }
    }

    /**
     * @brief Save the configuration file to storage.
     */
    public void save()
    {
        if (file == null)
        {
            LOGGER.warn("Could not save configuration file");
            return;
        }

        HashMap<String, Object> data = new HashMap<String, Object>();
        items.forEach((String key, ConfigurationItem item) -> {
            data.put(key, item.getValue());
        });

        try (FileWriter writer = new FileWriter(file))
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        }
        catch (IOException ex)
        {
            // It should not reach here since the conditions to cause the
            // exception have been cleared by the getFile function.
            assert(false);
        }
    }

    /**
     * Get the file to read/write to.
     * @param modId The guid of the mod.
     * @return A file when opened in read/write mode; otherwise, null.
     * @throws  SecurityException
     *          In the case of the default provider, and a security manager is
     *          installed, the {@link SecurityManager#checkRead(String) checkRead}
     *          is invoked to check read access to the file.
     */
    private File getFile(String modId) throws SecurityException
    {
        Path directoryPath = Paths.get("config");

        // Create the config directory if it does not exist
        if (!Files.exists(directoryPath))
        {
            LOGGER.info("Creating config directory");

            Path executePath = directoryPath.getParent();

            // Minecraft files (should) reside here, it should be readable by default
            assert(Files.isReadable(executePath));

            // It may not be writable though...
            if (!Files.isWritable(executePath))
            {
                return null;
            }

            try
            {
                directoryPath = Files.createDirectories(directoryPath);
            }
            catch (IOException ex)
            {
                // Generic I/O exception cannot be resolved by us, just assume no directory
                return null;
            }
        }

        // Ensure config is not a file but a directory
        if (!Files.isDirectory(directoryPath))
        {
            LOGGER.warn("Cannot make config directory");
            return null;
        }

        // Double check we have the user-rights to read/write
        if (!Files.isReadable(directoryPath) || !Files.isWritable(directoryPath))
        {
            LOGGER.warn("Cannot read and/or write to the config directory.");
            return null;
        }

        return new File(directoryPath.toString(), (modId + ".json"));
    }
}
