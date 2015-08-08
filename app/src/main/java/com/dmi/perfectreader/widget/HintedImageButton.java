package com.dmi.perfectreader.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.text.TextUtils.isEmpty;
import static com.dmi.util.Units.dipToPx;

public class HintedImageButton extends ImageButton {
    public HintedImageButton(Context context) {
        super(context);
        init();
    }

    public HintedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HintedImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setOnLongClickListener(v -> {
            showContentDescription();
            return true;
        });
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        throw new UnsupportedOperationException();
    }

    @SuppressLint("RtlHardcoded")
    private void showContentDescription()
    {
        String contentDesc = getContentDescription().toString();
        if (!isEmpty(contentDesc)) {
            final int OFFSET = (int) dipToPx(32);

            int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            getLocationOnScreen(screenPos);
            getWindowVisibleDisplayFrame(displayFrame);
            int y = screenPos[1];
            int height = getHeight();
            int screenHeight = displayFrame.height();
            boolean onTopPartOfScreen = y + height / 2 <= screenHeight / 2;

            Toast toast = Toast.makeText(getContext(), contentDesc, Toast.LENGTH_SHORT);
            if (onTopPartOfScreen) {
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, y + height + OFFSET);
            } else {
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, (screenHeight - y) + OFFSET);
            }
            toast.show();
        }
    }
}
