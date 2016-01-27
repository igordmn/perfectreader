package com.dmi.perfectreader.layout.line;

import com.dmi.perfectreader.layout.config.LayoutConfig;
import com.dmi.perfectreader.layout.config.TextMetrics;
import com.dmi.perfectreader.render.RenderSpace;
import com.dmi.perfectreader.render.RenderText;

public class TextInline implements Inline {
    private final String text;
    private final int color;
    private final float size;

    public TextInline(String text, int color, float size) {
        this.text = text;
        this.color = color;
        this.size = size;
    }

    @Override
    public void layout(LayoutConfig config, LinesBuilder linesBuilder) {
        TextMetrics metrics = config.textMetrics();
        TextMetrics.VerticalMetrics verticalMetrics = new TextMetrics.VerticalMetrics();
        metrics.getVerticalMetrics(size, verticalMetrics);
        float height = -verticalMetrics.ascent + verticalMetrics.descent;
        float baseline = -verticalMetrics.ascent;
        float whitespaceWidth = whitespaceWidth(metrics);
        StringBuilder token = new StringBuilder(16);
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isSpaceChar(ch) || Character.isWhitespace(ch)) {
                createToken(metrics, height, baseline, linesBuilder, token);
                linesBuilder.appendObject(new RenderSpace(whitespaceWidth, height), baseline);
            } else {
                token.append(ch);
            }
        }
        createToken(metrics, height, baseline, linesBuilder, token);
    }

    private void createToken(TextMetrics metrics, float height, float baseline, LinesBuilder linesBuilder, StringBuilder token) {
        if (token.length() > 0) {
            String text = token.toString();
            token.setLength(0);

            float width = 0F;
            float[] charWidths = new float[text.length()];
            metrics.getTextWidths(text, size, charWidths);
            for (float charWidth : charWidths) {
                width += charWidth;
            }
            linesBuilder.appendObject(new RenderText(width, height, text, baseline, color, size), baseline);
        }
    }

    private float whitespaceWidth(TextMetrics metrics) {
        float[] spaceWidths = new float[1];
        metrics.getTextWidths(" ", size, spaceWidths);
        return spaceWidths[0];
    }
}
