package com.dmi.util.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmi.util.TypeConverters;

import timber.log.Timber;

import static com.dmi.util.TypeConverters.stringToType;
import static com.dmi.util.TypeConverters.typeToString;

public abstract class AbstractSettings {
    private SharedPreferences sharedPreferences;

    protected void init(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private <T> T loadValue(Setting<T> setting) {
        String defValue = setting.toString(setting.defaultValue());
        String valueString = sharedPreferences.getString(setting.name(), defValue);
        try {
            return setting.parseString(valueString);
        } catch (TypeConverters.ParseException e) {
            Timber.w(e, "Parse saved value error");
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
        private final Class<?> valueClass;
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
            return typeToString(value, valueClass);
        }

        T parseString(String string) throws TypeConverters.ParseException {
            return stringToType(string, valueClass);
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
}
