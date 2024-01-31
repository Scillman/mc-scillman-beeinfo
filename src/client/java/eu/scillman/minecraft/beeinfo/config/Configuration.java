package eu.scillman.minecraft.beeinfo.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Configuration extends ConfigurationFile
{
    /**
     * @brief Indicates whether the contents of the configuration have changed.
     */
    private boolean hasChanged;

    /**
     * @brief Creates a configuration instance.
     */
    public Configuration()
    {
        hasChanged = false;
    }

    /**
     * @brief Load the configuration from storage.
     */
    @Override
    public void load()
    {
        boolean hasChanged = this.hasChanged;
        super.load();
        this.hasChanged = hasChanged;
    }

    /**
     * @brief Save the configuration to storage.
     */
    @Override
    public void save()
    {
        if (hasChanged)
        {
            super.save();
            hasChanged = false;
        }
    }

    /**
     * @brief Register a configuration option.
     * @param key The key to use for the option.
     * @param hint The hint to describe what it does.
     * @param value The current value.
     * @param defaultValue The default value.
     */
    public void register(String key, String hint, @Nullable Object value, Object defaultValue)
    {
        assert(!items.containsKey(key));

        items.put(key, new ConfigurationItem(key, hint, value, defaultValue, (ConfigurationItem item) -> {
            hasChanged = true;
        }));
    }

    /**
     * @brief Register a configuration option.
     * @param key The key to use for the option.
     * @param hint The hint to describe what it does.
     * @param value The current value.
     * @param defaultValue The default value.
     * @param minValue The minimum value.
     * @param maxValue The maximum value.
     */
    public void register(String key, String hint, @Nullable Object value, Object defaultValue, Object minValue, Object maxValue)
    {
        assert(!items.containsKey(key));

        items.put(key, new ConfigurationItem(key, hint, value, defaultValue, minValue, maxValue, (ConfigurationItem item) -> {
            hasChanged = true;
        }));
    }

    /**
     * @brief Get the maximum allowed value of the key.
     * @param <T> The type of the returned value.
     * @param key The key of whom to get its maximum value.
     * @return The maximum allowed value of the key; otherwise, null.
     * @remarks Maximum is inclusive, e.g. [min, max]
     */
    @SuppressWarnings("unchecked")
    public <T extends Object> @Nullable T max(String key)
    {
        assert(items.containsKey(key));
        return ((T)(items.get(key).max()));
    }

    /**
     * @brief Get the minimum allowed value of the key.
     * @param <T> The type of the returned value.
     * @param key The key of whom to get its minimum value.
     * @return The minimum allowed value of the key; otherwise, null.
     * @remarks Minimum is inclusive, e.g. [min, max]
     */
    @SuppressWarnings("unchecked")
    public <T extends Object> @Nullable T min(String key)
    {
        assert(items.containsKey(key));
        return ((T)(items.get(key).min()));
    }

    /**
     * @brief Get the value of the key.
     * @param <T> The type of the returned value.
     * @param key The key whoms value to get.
     * @return The value of the key; otherwise, null.
     */
    @SuppressWarnings("unchecked")
    public <T extends Object> @Nullable T get(String key)
    {
        assert(items.containsKey(key));
        return ((T)(items.get(key).getValue()));
    }

    /**
     * @brief Set the value of a key.
     * @param key The key whoms value to change.
     * @param value The new value.
     */
    public void set(String key, Object value)
    {
        assert(items.containsKey(key));
        items.get(key).setValue(value);
    }

    /**
     * @brief Reset the value of the given key.
     * @param key The key whoms value to reset.
     */
    public void reset(String key)
    {
        assert(items.containsKey(key));

        ConfigurationItem item = items.get(key);
        item.setValue(item.getDefault());
    }

    /**
     * @brief Get a list of registered keys.
     * @return A list of all the registered keys.
     */
    public List<String> keys()
    {
        ArrayList<String> keys = new ArrayList<String>(items.keySet());
        Collections.sort(keys);
        return keys;
    }
}
