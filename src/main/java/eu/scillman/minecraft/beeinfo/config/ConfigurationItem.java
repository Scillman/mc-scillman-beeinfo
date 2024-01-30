package eu.scillman.minecraft.beeinfo.config;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class ConfigurationItem
{
    private String key;
    private String hint;
    private Object value;
    private @Nullable Object minValue;
    private @Nullable Object maxValue;
    private Object defaultValue;
    private Consumer<ConfigurationItem> callback;

    public ConfigurationItem(String key, String hint, @Nullable Object value, Object defaultValue, Consumer<ConfigurationItem> callback)
    {
        this(key, hint, value, defaultValue, null, null, callback);
    }

    public ConfigurationItem(String key, String hint, @Nullable Object value, Object defaultValue, @Nullable Object minValue, @Nullable Object maxValue, Consumer<ConfigurationItem> callback)
    {
        this.key = key;
        this.hint = hint;
        this.value = (value != null ? value : defaultValue);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.callback = callback;
    }

    public void setValue(Object value)
    {
        if (!value.equals(this.value))
        {
            this.value = value;
            callback.accept(this);
        }
    }

    public String key()
    {
        return key;
    }

    public String hint()
    {
        return hint;
    }

    @Nullable
    public Object min()
    {
        return minValue;
    }

    @Nullable
    public Object max()
    {
        return maxValue;
    }

    public Object getDefault()
    {
        return defaultValue;
    }

    public Object getValue()
    {
        return value;
    }

    private Integer clamp(Integer newValue)
    {
        if (newValue > ((Integer)(maxValue)))
        {
            return (Integer)maxValue;
        }

        if (newValue < ((Integer)(minValue)))
        {
            return (Integer)minValue;
        }

        return newValue;
    }

    private Float clamp(Float newValue)
    {
        if (newValue > ((Float)(maxValue)))
        {
            return (Float)maxValue;
        }

        if (newValue < ((Float)(minValue)))
        {
            return (Float)minValue;
        }

        return newValue;
    }

    public ConfigurationItem merge(ConfigurationItem other)
    {
        // There is no value to merge
        if (other.value == null)
        {
            return this;
        }

        // Overwrite the value, but keep values
        if (defaultValue.getClass() == other.value.getClass())
        {
            if (other.value instanceof Integer newValue)
            {
                value = clamp(newValue);
            }
            else if (other.value instanceof Float newValue)
            {
                value = clamp(newValue);
            }
            else
            {
                value = other.value;
            }

            return this;
        }

        // Type check
        if (defaultValue.getClass() != other.value.getClass())
        {
            if (defaultValue instanceof String)
            {
                value = other.value.toString();
                return this;
            }

            if (other.value instanceof String strValue)
            {
                if (defaultValue instanceof Integer)
                {
                    value = clamp(Integer.valueOf(strValue));
                    return this;
                }

                if (defaultValue instanceof Float)
                {
                    value = clamp(Float.valueOf(strValue));
                    return this;
                }
            }
        }

        if (this.value == null)
        {
            this.value = other.value;
        }

        if (value.getClass() == other.value.getClass())
        {
            if (value.getClass() == String.class)
            {
                if (value.getClass() == Integer.class)
                {

                }
            }
        }

        return this;
    }
}
