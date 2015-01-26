package com.dmi.perfectreader.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmi.perfectreader.book.config.TextAlign;
import com.dmi.perfectreader.control.Action;
import com.dmi.perfectreader.control.HardKey;
import com.dmi.perfectreader.control.InteractiveClickMode;
import com.dmi.perfectreader.control.TapZone;
import com.dmi.perfectreader.control.TapZoneConfiguration;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean
public class Settings {
    public final Format format = new Format();
    public class Format {
        public final Setting<TextAlign> textAlign = new Setting<>("format.textAlign", TextAlign.JUSTIFY);
        public final Setting<Integer> fontSize = new Setting<>("format.fontSize", 200);
        public final Setting<Integer> lineHeight = new Setting<>("format.lineHeight", 100);
    }

    public final Control control = new Control();
    public class Control {
        public final TapZones tapZones = new TapZones();
        public class TapZones {
            public ShortTaps shortTaps = new ShortTaps();
            public class ShortTaps {
                public final Setting<TapZoneConfiguration> configuration = new Setting<>("control.tapZones.shortTaps.configuration", TapZoneConfiguration.NINE);
                private final Setting<Action> centerAction = new Setting<>("control.tapZones.shortTaps.centerAction", Action.SELECT_TEXT);
                private final Setting<Action> topAction = new Setting<>("control.tapZones.shortTaps.topAction", Action.TOGGLE_MENU);
                private final Setting<Action> bottomAction = new Setting<>("control.tapZones.shortTaps.bottomAction", Action.TOGGLE_MENU);
                private final Setting<Action> leftAction = new Setting<>("control.tapZones.shortTaps.leftAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> rightAction = new Setting<>("control.tapZones.shortTaps.rightAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> topLeftAction = new Setting<>("control.tapZones.shortTaps.topLeftAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> topRightAction = new Setting<>("control.tapZones.shortTaps.topRightAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> bottomLeftAction = new Setting<>("control.tapZones.shortTaps.bottomLeftAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> bottomRightAction = new Setting<>("control.tapZones.shortTaps.bottomRightAction", Action.GO_NEXT_PAGE);

                public Setting<Action> action(TapZone tapZone) {
                    switch (tapZone) {
                        case CENTER:
                            return centerAction;
                        case TOP:
                            return topAction;
                        case BOTTOM:
                            return bottomAction;
                        case LEFT:
                            return leftAction;
                        case RIGHT:
                            return rightAction;
                        case TOP_LEFT:
                            return topLeftAction;
                        case TOP_RIGHT:
                            return topRightAction;
                        case BOTTOM_LEFT:
                            return bottomLeftAction;
                        case BOTTOM_RIGHT:
                            return bottomRightAction;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
            public LongTaps longTaps = new LongTaps();
            public class LongTaps {
                public final Setting<TapZoneConfiguration> configuration = new Setting<>("control.tapZones.longTaps.configuration", TapZoneConfiguration.SINGLE);
                public final Setting<Long> timeout = new Setting<>("control.tapZones.longTaps.timeout", 500L);
                private final Setting<Action> centerAction = new Setting<>("control.tapZones.longTaps.centerAction", Action.SELECT_TEXT);
                private final Setting<Action> topAction = new Setting<>("control.tapZones.longTaps.topAction", Action.SELECT_TEXT);
                private final Setting<Action> bottomAction = new Setting<>("control.tapZones.longTaps.bottomAction", Action.SELECT_TEXT);
                private final Setting<Action> leftAction = new Setting<>("control.tapZones.longTaps.leftAction", Action.SELECT_TEXT);
                private final Setting<Action> rightAction = new Setting<>("control.tapZones.longTaps.rightAction", Action.SELECT_TEXT);
                private final Setting<Action> topLeftAction = new Setting<>("control.tapZones.longTaps.topLeftAction", Action.SELECT_TEXT);
                private final Setting<Action> topRightAction = new Setting<>("control.tapZones.longTaps.topRightAction", Action.SELECT_TEXT);
                private final Setting<Action> bottomLeftAction = new Setting<>("control.tapZones.longTaps.bottomLeftAction", Action.SELECT_TEXT);
                private final Setting<Action> bottomRightAction = new Setting<>("control.tapZones.longTaps.bottomRightAction", Action.SELECT_TEXT);

                public Setting<Action> action(TapZone tapZone) {
                    switch (tapZone) {
                        case CENTER:
                            return centerAction;
                        case TOP:
                            return topAction;
                        case BOTTOM:
                            return bottomAction;
                        case LEFT:
                            return leftAction;
                        case RIGHT:
                            return rightAction;
                        case TOP_LEFT:
                            return topLeftAction;
                        case TOP_RIGHT:
                            return topRightAction;
                        case BOTTOM_LEFT:
                            return bottomLeftAction;
                        case BOTTOM_RIGHT:
                            return bottomRightAction;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
            public DoubleTaps doubleTaps = new DoubleTaps();
            public class DoubleTaps {
                public final Setting<TapZoneConfiguration> configuration = new Setting<>("control.tapZones.doubleTaps.configuration", TapZoneConfiguration.SINGLE);
                public final Setting<Long> timeout = new Setting<>("control.tapZones.doubleTaps.timeout", 300L);
                private final Setting<Action> centerAction = new Setting<>("control.tapZones.doubleTaps.centerAction", Action.NONE);
                private final Setting<Action> topAction = new Setting<>("control.tapZones.doubleTaps.topAction", Action.NONE);
                private final Setting<Action> bottomAction = new Setting<>("control.tapZones.doubleTaps.bottomAction", Action.NONE);
                private final Setting<Action> leftAction = new Setting<>("control.tapZones.doubleTaps.leftAction", Action.NONE);
                private final Setting<Action> rightAction = new Setting<>("control.tapZones.doubleTaps.rightAction", Action.NONE);
                private final Setting<Action> topLeftAction = new Setting<>("control.tapZones.doubleTaps.topLeftAction", Action.NONE);
                private final Setting<Action> topRightAction = new Setting<>("control.tapZones.doubleTaps.topRightAction", Action.NONE);
                private final Setting<Action> bottomLeftAction = new Setting<>("control.tapZones.doubleTaps.bottomLeftAction", Action.NONE);
                private final Setting<Action> bottomRightAction = new Setting<>("control.tapZones.doubleTaps.bottomRightAction", Action.NONE);

                public Setting<Action> action(TapZone tapZone) {
                    switch (tapZone) {
                        case CENTER:
                            return centerAction;
                        case TOP:
                            return topAction;
                        case BOTTOM:
                            return bottomAction;
                        case LEFT:
                            return leftAction;
                        case RIGHT:
                            return rightAction;
                        case TOP_LEFT:
                            return topLeftAction;
                        case TOP_RIGHT:
                            return topRightAction;
                        case BOTTOM_LEFT:
                            return bottomLeftAction;
                        case BOTTOM_RIGHT:
                            return bottomRightAction;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
            private final Setting<InteractiveClickMode> interactiveClickMode = new Setting<>("control.tapZones.interactiveClickMode", InteractiveClickMode.CLICK);
        }
        public final HardKeys hardKeys = new HardKeys();
        public class HardKeys {
            public ShortPress shortPress = new ShortPress();
            public class ShortPress {
                private final Setting<Action> volumeUpAction = new Setting<>("control.hardKeys.shortPress.volumeUpAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> volumeDownAction = new Setting<>("control.hardKeys.shortPress.volumeDownAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> menuAction = new Setting<>("control.hardKeys.shortPress.menuAction", Action.TOGGLE_MENU);
                private final Setting<Action> backAction = new Setting<>("control.hardKeys.shortPress.backAction", Action.GO_BACK);
                private final Setting<Action> searchAction = new Setting<>("control.hardKeys.shortPress.searchAction", Action.NONE);
                private final Setting<Action> cameraAction = new Setting<>("control.hardKeys.shortPress.cameraAction", Action.NONE);
                private final Setting<Action> trackballPressAction = new Setting<>("control.hardKeys.shortPress.trackballPressAction", Action.NONE);
                private final Setting<Action> trackballLeftAction = new Setting<>("control.hardKeys.shortPress.trackballLeftAction", Action.NONE);
                private final Setting<Action> trackballRightAction = new Setting<>("control.hardKeys.shortPress.trackballRightAction", Action.NONE);
                private final Setting<Action> trackballUpAction = new Setting<>("control.hardKeys.shortPress.trackballUpAction", Action.NONE);
                private final Setting<Action> trackballDownAction = new Setting<>("control.hardKeys.shortPress.trackballDownAction", Action.NONE);

                public Setting<Action> action(HardKey hardKey) {
                    switch (hardKey) {
                        case VOLUME_UP:
                            return volumeUpAction;
                        case VOLUME_DOWN:
                            return volumeDownAction;
                        case MENU:
                            return menuAction;
                        case BACK:
                            return backAction;
                        case SEARCH:
                            return searchAction;
                        case CAMERA:
                            return cameraAction;
                        case TRACKBALL_PRESS:
                            return trackballPressAction;
                        case TRACKBALL_LEFT:
                            return trackballLeftAction;
                        case TRACKBALL_RIGHT:
                            return trackballRightAction;
                        case TRACKBALL_UP:
                            return trackballUpAction;
                        case TRACKBALL_DOWN:
                            return trackballDownAction;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
            public LongPress longPress = new LongPress();
            public class LongPress {
                public final Setting<Long> timeout = new Setting<>("control.hardKeys.longPress.timeout", 500L);
                private final Setting<Action> volumeUpAction = new Setting<>("control.hardKeys.longPress.volumeUpAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> volumeDownAction = new Setting<>("control.hardKeys.longPress.volumeDownAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> menuAction = new Setting<>("control.hardKeys.longPress.menuAction", Action.NONE);
                private final Setting<Action> backAction = new Setting<>("control.hardKeys.longPress.backAction", Action.NONE);
                private final Setting<Action> searchAction = new Setting<>("control.hardKeys.longPress.searchAction", Action.NONE);
                private final Setting<Action> cameraAction = new Setting<>("control.hardKeys.longPress.cameraAction", Action.NONE);
                private final Setting<Action> trackballPressAction = new Setting<>("control.hardKeys.longPress.trackballPressAction", Action.NONE);
                private final Setting<Action> trackballLeftAction = new Setting<>("control.hardKeys.longPress.trackballLeftAction", Action.NONE);
                private final Setting<Action> trackballRightAction = new Setting<>("control.hardKeys.longPress.trackballRightAction", Action.NONE);
                private final Setting<Action> trackballUpAction = new Setting<>("control.hardKeys.longPress.trackballUpAction", Action.NONE);
                private final Setting<Action> trackballDownAction = new Setting<>("control.hardKeys.longPress.trackballDownAction", Action.NONE);

                public Setting<Action> action(HardKey hardKey) {
                    switch (hardKey) {
                        case VOLUME_UP:
                            return volumeUpAction;
                        case VOLUME_DOWN:
                            return volumeDownAction;
                        case MENU:
                            return menuAction;
                        case BACK:
                            return backAction;
                        case SEARCH:
                            return searchAction;
                        case CAMERA:
                            return cameraAction;
                        case TRACKBALL_PRESS:
                            return trackballPressAction;
                        case TRACKBALL_LEFT:
                            return trackballLeftAction;
                        case TRACKBALL_RIGHT:
                            return trackballRightAction;
                        case TRACKBALL_UP:
                            return trackballUpAction;
                        case TRACKBALL_DOWN:
                            return trackballDownAction;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
            public DoublePress doublePress = new DoublePress();
            public class DoublePress {
                public final Setting<Long> timeout = new Setting<>("control.hardKeys.doublePress.timeout", 300L);
                private final Setting<Action> volumeUpAction = new Setting<>("control.hardKeys.doublePress.volumeUpAction", Action.NONE);
                private final Setting<Action> volumeDownAction = new Setting<>("control.hardKeys.doublePress.volumeDownAction", Action.NONE);
                private final Setting<Action> menuAction = new Setting<>("control.hardKeys.doublePress.menuAction", Action.NONE);
                private final Setting<Action> backAction = new Setting<>("control.hardKeys.doublePress.backAction", Action.NONE);
                private final Setting<Action> searchAction = new Setting<>("control.hardKeys.doublePress.searchAction", Action.NONE);
                private final Setting<Action> cameraAction = new Setting<>("control.hardKeys.doublePress.cameraAction", Action.NONE);
                private final Setting<Action> trackballPressAction = new Setting<>("control.hardKeys.doublePress.trackballPressAction", Action.NONE);
                private final Setting<Action> trackballLeftAction = new Setting<>("control.hardKeys.doublePress.trackballLeftAction", Action.NONE);
                private final Setting<Action> trackballRightAction = new Setting<>("control.hardKeys.doublePress.trackballRightAction", Action.NONE);
                private final Setting<Action> trackballUpAction = new Setting<>("control.hardKeys.doublePress.trackballUpAction", Action.NONE);
                private final Setting<Action> trackballDownAction = new Setting<>("control.hardKeys.doublePress.trackballDownAction", Action.NONE);

                public Setting<Action> action(HardKey hardKey) {
                    switch (hardKey) {
                        case VOLUME_UP:
                            return volumeUpAction;
                        case VOLUME_DOWN:
                            return volumeDownAction;
                        case MENU:
                            return menuAction;
                        case BACK:
                            return backAction;
                        case SEARCH:
                            return searchAction;
                        case CAMERA:
                            return cameraAction;
                        case TRACKBALL_PRESS:
                            return trackballPressAction;
                        case TRACKBALL_LEFT:
                            return trackballLeftAction;
                        case TRACKBALL_RIGHT:
                            return trackballRightAction;
                        case TRACKBALL_UP:
                            return trackballUpAction;
                        case TRACKBALL_DOWN:
                            return trackballDownAction;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
        }
    }

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
