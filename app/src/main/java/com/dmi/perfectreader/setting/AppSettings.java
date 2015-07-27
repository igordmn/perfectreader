package com.dmi.perfectreader.setting;

import android.content.Context;

import com.dmi.perfectreader.book.config.TextAlign;
import com.dmi.perfectreader.control.Action;
import com.dmi.perfectreader.control.HardKey;
import com.dmi.perfectreader.control.InteractiveClickMode;
import com.dmi.perfectreader.control.TapZone;
import com.dmi.perfectreader.control.TapZoneConfiguration;
import com.dmi.util.setting.AbstractSettings;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean(scope = EBean.Scope.Singleton)
public class AppSettings extends AbstractSettings {
    public final Format format = new Format();
    public class Format {
        public final Setting<TextAlign> textAlign = setting("format.textAlign", TextAlign.JUSTIFY);
        public final Setting<Integer> fontSizePercents = setting("format.fontSizePercents", 200);
        public final Setting<Integer> lineHeightPercents = setting("format.lineHeightPercents", 100);
        public final Setting<Boolean> hangingPunctuation = setting("format.hangingPunctuation", true);
        public final Setting<Boolean> hyphenation = setting("format.hyphenation", true);
    }

    public final Control control = new Control();
    public class Control {
        public final TapZones tapZones = new TapZones();
        public class TapZones {
            public ShortTaps shortTaps = new ShortTaps();
            public class ShortTaps {
                public final Setting<TapZoneConfiguration> configuration = setting("control.tapZones.shortTaps.configuration", TapZoneConfiguration.NINE);
                private final Setting<Action> centerAction = setting("control.tapZones.shortTaps.centerAction", Action.TOGGLE_MENU);
                private final Setting<Action> topAction = setting("control.tapZones.shortTaps.topAction", Action.TOGGLE_MENU);
                private final Setting<Action> bottomAction = setting("control.tapZones.shortTaps.bottomAction", Action.TOGGLE_MENU);
                private final Setting<Action> leftAction = setting("control.tapZones.shortTaps.leftAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> rightAction = setting("control.tapZones.shortTaps.rightAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> topLeftAction = setting("control.tapZones.shortTaps.topLeftAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> topRightAction = setting("control.tapZones.shortTaps.topRightAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> bottomLeftAction = setting("control.tapZones.shortTaps.bottomLeftAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> bottomRightAction = setting("control.tapZones.shortTaps.bottomRightAction", Action.GO_NEXT_PAGE);

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
                public final Setting<TapZoneConfiguration> configuration = setting("control.tapZones.longTaps.configuration", TapZoneConfiguration.SINGLE);
                public final Setting<Long> timeout = setting("control.tapZones.longTaps.timeout", 500L);
                private final Setting<Action> centerAction = setting("control.tapZones.longTaps.centerAction", Action.SELECT_TEXT);
                private final Setting<Action> topAction = setting("control.tapZones.longTaps.topAction", Action.SELECT_TEXT);
                private final Setting<Action> bottomAction = setting("control.tapZones.longTaps.bottomAction", Action.SELECT_TEXT);
                private final Setting<Action> leftAction = setting("control.tapZones.longTaps.leftAction", Action.SELECT_TEXT);
                private final Setting<Action> rightAction = setting("control.tapZones.longTaps.rightAction", Action.SELECT_TEXT);
                private final Setting<Action> topLeftAction = setting("control.tapZones.longTaps.topLeftAction", Action.SELECT_TEXT);
                private final Setting<Action> topRightAction = setting("control.tapZones.longTaps.topRightAction", Action.SELECT_TEXT);
                private final Setting<Action> bottomLeftAction = setting("control.tapZones.longTaps.bottomLeftAction", Action.SELECT_TEXT);
                private final Setting<Action> bottomRightAction = setting("control.tapZones.longTaps.bottomRightAction", Action.SELECT_TEXT);

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
                public final Setting<TapZoneConfiguration> configuration = setting("control.tapZones.doubleTaps.configuration", TapZoneConfiguration.SINGLE);
                public final Setting<Long> timeout = setting("control.tapZones.doubleTaps.timeout", 300L);
                private final Setting<Action> centerAction = setting("control.tapZones.doubleTaps.centerAction", Action.NONE);
                private final Setting<Action> topAction = setting("control.tapZones.doubleTaps.topAction", Action.NONE);
                private final Setting<Action> bottomAction = setting("control.tapZones.doubleTaps.bottomAction", Action.NONE);
                private final Setting<Action> leftAction = setting("control.tapZones.doubleTaps.leftAction", Action.NONE);
                private final Setting<Action> rightAction = setting("control.tapZones.doubleTaps.rightAction", Action.NONE);
                private final Setting<Action> topLeftAction = setting("control.tapZones.doubleTaps.topLeftAction", Action.NONE);
                private final Setting<Action> topRightAction = setting("control.tapZones.doubleTaps.topRightAction", Action.NONE);
                private final Setting<Action> bottomLeftAction = setting("control.tapZones.doubleTaps.bottomLeftAction", Action.NONE);
                private final Setting<Action> bottomRightAction = setting("control.tapZones.doubleTaps.bottomRightAction", Action.NONE);

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
            private final Setting<InteractiveClickMode> interactiveClickMode = setting("control.tapZones.interactiveClickMode", InteractiveClickMode.CLICK);
        }
        public final HardKeys hardKeys = new HardKeys();
        public class HardKeys {
            public ShortPress shortPress = new ShortPress();
            public class ShortPress {
                private final Setting<Action> volumeUpAction = setting("control.hardKeys.shortPress.volumeUpAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> volumeDownAction = setting("control.hardKeys.shortPress.volumeDownAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> menuAction = setting("control.hardKeys.shortPress.menuAction", Action.TOGGLE_MENU);
                private final Setting<Action> backAction = setting("control.hardKeys.shortPress.backAction", Action.EXIT);
                private final Setting<Action> searchAction = setting("control.hardKeys.shortPress.searchAction", Action.NONE);
                private final Setting<Action> cameraAction = setting("control.hardKeys.shortPress.cameraAction", Action.NONE);
                private final Setting<Action> trackballPressAction = setting("control.hardKeys.shortPress.trackballPressAction", Action.NONE);
                private final Setting<Action> trackballLeftAction = setting("control.hardKeys.shortPress.trackballLeftAction", Action.NONE);
                private final Setting<Action> trackballRightAction = setting("control.hardKeys.shortPress.trackballRightAction", Action.NONE);
                private final Setting<Action> trackballUpAction = setting("control.hardKeys.shortPress.trackballUpAction", Action.NONE);
                private final Setting<Action> trackballDownAction = setting("control.hardKeys.shortPress.trackballDownAction", Action.NONE);

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
                public final Setting<Long> timeout = setting("control.hardKeys.longPress.timeout", 500L);
                private final Setting<Action> volumeUpAction = setting("control.hardKeys.longPress.volumeUpAction", Action.GO_PREVIEW_PAGE);
                private final Setting<Action> volumeDownAction = setting("control.hardKeys.longPress.volumeDownAction", Action.GO_NEXT_PAGE);
                private final Setting<Action> menuAction = setting("control.hardKeys.longPress.menuAction", Action.NONE);
                private final Setting<Action> backAction = setting("control.hardKeys.longPress.backAction", Action.NONE);
                private final Setting<Action> searchAction = setting("control.hardKeys.longPress.searchAction", Action.NONE);
                private final Setting<Action> cameraAction = setting("control.hardKeys.longPress.cameraAction", Action.NONE);
                private final Setting<Action> trackballPressAction = setting("control.hardKeys.longPress.trackballPressAction", Action.NONE);
                private final Setting<Action> trackballLeftAction = setting("control.hardKeys.longPress.trackballLeftAction", Action.NONE);
                private final Setting<Action> trackballRightAction = setting("control.hardKeys.longPress.trackballRightAction", Action.NONE);
                private final Setting<Action> trackballUpAction = setting("control.hardKeys.longPress.trackballUpAction", Action.NONE);
                private final Setting<Action> trackballDownAction = setting("control.hardKeys.longPress.trackballDownAction", Action.NONE);

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
                public final Setting<Long> timeout = setting("control.hardKeys.doublePress.timeout", 300L);
                private final Setting<Action> volumeUpAction = setting("control.hardKeys.doublePress.volumeUpAction", Action.NONE);
                private final Setting<Action> volumeDownAction = setting("control.hardKeys.doublePress.volumeDownAction", Action.NONE);
                private final Setting<Action> menuAction = setting("control.hardKeys.doublePress.menuAction", Action.NONE);
                private final Setting<Action> backAction = setting("control.hardKeys.doublePress.backAction", Action.NONE);
                private final Setting<Action> searchAction = setting("control.hardKeys.doublePress.searchAction", Action.NONE);
                private final Setting<Action> cameraAction = setting("control.hardKeys.doublePress.cameraAction", Action.NONE);
                private final Setting<Action> trackballPressAction = setting("control.hardKeys.doublePress.trackballPressAction", Action.NONE);
                private final Setting<Action> trackballLeftAction = setting("control.hardKeys.doublePress.trackballLeftAction", Action.NONE);
                private final Setting<Action> trackballRightAction = setting("control.hardKeys.doublePress.trackballRightAction", Action.NONE);
                private final Setting<Action> trackballUpAction = setting("control.hardKeys.doublePress.trackballUpAction", Action.NONE);
                private final Setting<Action> trackballDownAction = setting("control.hardKeys.doublePress.trackballDownAction", Action.NONE);

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

    @AfterInject
    protected void init() {
        init(context, "settings");
    }
}
