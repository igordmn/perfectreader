package com.dmi.perfectreader.manualtest.layout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.dmi.perfectreader.layout.LayoutText;
import com.dmi.perfectreader.layout.config.LayoutArea;
import com.dmi.perfectreader.layout.config.LayoutConfig;
import com.dmi.perfectreader.layout.config.PaintTextMetrics;
import com.dmi.perfectreader.layout.line.TextInline;
import com.dmi.perfectreader.render.RenderConfig;
import com.dmi.perfectreader.render.RenderObject;
import com.dmi.util.base.BaseActivity;

import static java.util.Arrays.asList;

public class LayoutObjectsTestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutConfig layoutConfig = new LayoutConfig(new PaintTextMetrics());

        LayoutText text = new LayoutText(asList(
                textInline("This is text. This is text. This is te"),
                textInline("xt. This is text. This is text")
        ));

        RenderConfig renderConfig = new RenderConfig();
        RenderObject renderText = text.layout(layoutConfig, LayoutArea.unlimited());
        View view = new View(this) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(Color.WHITE);
                renderText.paintRecursive(renderConfig, canvas);
            }
        };

        setContentView(view);
    }

    private TextInline textInline(String text) {
        return new TextInline(text, Color.RED);
    }
}
