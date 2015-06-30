package com.dmi.util.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.util.Log.getStackTraceString;

public abstract class AbstractSettings {
    private static final String LOG_TAG = AbstractSettings.class.getName();

    private SharedPreferences sharedPreferences;

    protected void init(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private <T> T loadValue(Setting<T> setting) {
        String defValue = setting.toString(setting.defaultValue());
        String valueString = sharedPreferences.getString(setting.name(), defValue);
        try {
            return setting.parseString(valueString);
        } catch (ParseException e) {
            return setting.defaultValue();
        }
    }

    protected <T> void saveValue(Setting<T> setting, T value) {
        String valueString = setting.toString(value);
        sharedPreferences.edit().putString(setting.name(), valueString).apply();
    }

    protected <T> Setting<T> setting(String name, T defaultValue) {
        return new Setting<>(name, defaultValue);
    }

    public class Setting<T> {
        private final String name;
        private final T defaultValue;
        private final Class valueClass;
        private volatile T cachedValue = null;
        private SettingListener<T> listener = null;

        Setting(String name, T defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
            valueClass = defaultValue.getClass();
        }

        public String name() {
            return name;
        }

        public T defaultValue() {
            return defaultValue;
        }

        String toString(T value) {
            if (Integer.class.isAssignableFrom(valueClass)) {
                return value.toString();
            } else if (Float.class.isAssignableFrom(valueClass)) {
                return value.toString();
            }  else if (String.class.isAssignableFrom(valueClass)) {
                return (String) value;
            } else if (Enum.class.isAssignableFrom(valueClass)) {
                return ((Enum) value).name();
            }
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        T parseString(String string) throws ParseException {
            try {
                if (Integer.class.isAssignableFrom(valueClass)) {
                    return (T) Integer.valueOf(Integer.parseInt(string));
                } else if (Float.class.isAssignableFrom(valueClass)) {
                    return (T) Float.valueOf(Float.parseFloat(string));
                } else if (String.class.isAssignableFrom(valueClass)) {
                    return (T) string;
                } else if (Enum.class.isAssignableFrom(valueClass)) {
                    return (T) Enum.valueOf(valueClass, string);
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, getStackTraceString(e));
                throw new ParseException();
            }
            throw new UnsupportedOperationException();
        }

        public synchronized T get() {
            if (cachedValue == null) {
                cachedValue = loadValue(this);
            }
            return cachedValue;
        }

        public synchronized void set(T value) {
            cachedValue = value;
            listener.onValueSet(value);
            saveValue(this, value);
        }

        public synchronized void setListener(SettingListener<T> listener) {
            this.listener = listener;
        }
    }

    private static class ParseException extends Exception {
        public ParseException() {
        }
    }
}
