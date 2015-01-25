package com.dmi.perfectreader.control;

import android.view.KeyEvent;

public enum HardKey {
    UNKNOWN,
    VOLUME_UP,
    VOLUME_DOWN,
    MENU,
    BACK,
    SEARCH,
    CAMERA,
    TRACKBALL_PRESS,
    TRACKBALL_LEFT,
    TRACKBALL_RIGHT,
    TRACKBALL_UP,
    TRACKBALL_DOWN;

    public static HardKey fromKeyCode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return VOLUME_UP;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return VOLUME_DOWN;
            case KeyEvent.KEYCODE_MENU:
                return MENU;
            case KeyEvent.KEYCODE_BACK:
                return BACK;
            case KeyEvent.KEYCODE_SEARCH:
                return SEARCH;
            case KeyEvent.KEYCODE_CAMERA:
                return CAMERA;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return TRACKBALL_PRESS;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return TRACKBALL_LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return TRACKBALL_RIGHT;
            case KeyEvent.KEYCODE_DPAD_UP:
                return TRACKBALL_UP;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return TRACKBALL_DOWN;
            default:
                return UNKNOWN;
        }
    }
}
