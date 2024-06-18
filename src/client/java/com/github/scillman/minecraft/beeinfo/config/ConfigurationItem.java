package com.github.scillman.minecraft.beeinfo.config;

import java.util.function.Consumer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
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

    public void setValue(Object newValue)
    {
        Object value = clamp(newValue);
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

    private Object clamp(Object newValue)
    {
        if (defaultValue instanceof Float)
        {
            return clampFloat(newValue);
        }

        if (defaultValue instanceof Integer)
        {
            return clampInteger(newValue);
        }

        return newValue;
    }

    private Object clampInteger(Object obj)
    {
        Integer newValue;

        if (obj instanceof String strValue)
        {
            try
            {
                newValue = Integer.valueOf(strValue);
            }
            catch (NumberFormatException ex)
            {
                return defaultValue;
            }
        }
        else if (obj.getClass() == Long.class)
        {
            Long lv = ((Long)(obj));
            newValue = lv.intValue();
        }
        else if (obj.getClass() != Integer.class)
        {
            return defaultValue;
        }
        else
        {
            newValue = ((Integer)(obj));
        }

        if (minValue instanceof Integer min)
        {
            if (min > newValue)
            {
                return min;
            }
        }

        if (maxValue instanceof Integer max)
        {
            if (max < newValue)
            {
                return max;
            }
        }

        return newValue;
    }

    private Object clampFloat(Object obj)
    {
        Float newValue;

        if (obj instanceof String strValue)
        {
            try
            {
                newValue = Float.valueOf(strValue);
            }
            catch (NumberFormatException ex)
            {
                return defaultValue;
            }
        }
        else if (obj.getClass() == Number.class)
        {
            Number nv = ((Number)(obj));
            newValue = nv.floatValue();
        }
        else if (obj.getClass() == Double.class)
        {
            Double dv = ((Double)(obj));
            newValue = dv.floatValue();
        }
        else if (obj.getClass() != Float.class)
        {
            return defaultValue;
        }
        else
        {
            newValue = ((Float)(obj));
        }

        if (minValue instanceof Float min)
        {
            if (min > newValue)
            {
                return min;
            }
        }

        if (maxValue instanceof Float max)
        {
            if (max < newValue)
            {
                return max;
            }
        }

        return newValue;
    }
}
