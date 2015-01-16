package com.dmi.perfectreader.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmi.perfectreader.book.config.TextAlign;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class Settings {
    public class Format {
        public final Setting<TextAlign> TEXT_ALIGN = new Setting<>("format.textAlign", TextAlign.JUSTIFY);
        public final Setting<Integer> FONT_SIZE = new Setting<>("format.fontSize", 200);
        public final Setting<Integer> LINE_HEIGHT = new Setting<>("format.lineHeight", 100);
    }
    public final Format format = new Format();

    @RootContext
    protected Context context;

    private SharedPreferences sharedPreferences;

    @AfterInject
    protected void init() {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
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

    @Background
    protected <T> void saveValue(Setting<T> setting, T value) {
        String valueString = setting.toString(value);
        sharedPreferences.edit().putString(setting.name(), valueString).apply();
    }

    public class Setting<T> {
        private final String name;
        private final T defaultValue;
        private final Class valueClass;
        private volatile T cachedValue = null;

        Setting(String name, T defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
            valueClass = defaultValue.getClass();
        }

        String name() {
            return name;
        }

        T defaultValue() {
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
                throw new ParseException(e);
            }
            throw new UnsupportedOperationException();
        }

        public T get() {
            if (cachedValue == null) {
                cachedValue = loadValue(this);
            }
            return cachedValue;
        }

        public void set(T value) {
            cachedValue = value;
            saveValue(this, value);
        }
    }
}
