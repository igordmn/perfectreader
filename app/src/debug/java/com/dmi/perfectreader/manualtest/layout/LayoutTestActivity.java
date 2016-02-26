package com.dmi.perfectreader.manualtest.layout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.dmi.perfectreader.layout.LayoutParagraph;
import com.dmi.perfectreader.layout.config.HangingConfig;
import com.dmi.perfectreader.layout.config.LayoutArea;
import com.dmi.perfectreader.layout.config.PaintTextMetrics;
import com.dmi.perfectreader.layout.layouter.ParagraphLayouter;
import com.dmi.perfectreader.layout.liner.BreakLiner;
import com.dmi.perfectreader.layout.liner.RuleBreakFinder;
import com.dmi.perfectreader.layout.run.TextRun;
import com.dmi.perfectreader.layout.wordbreak.TeXPatternsSource;
import com.dmi.perfectreader.layout.wordbreak.TeXWordBreaker;
import com.dmi.perfectreader.render.RenderObject;
import com.dmi.perfectreader.style.FontStyle;
import com.dmi.perfectreader.style.TextAlign;
import com.dmi.util.base.BaseActivity;

import java.util.Locale;

import static java.util.Arrays.asList;

public class LayoutTestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParagraphLayouter layouter = new ParagraphLayouter(
                null, new PaintTextMetrics(),
                new BreakLiner(
                        new RuleBreakFinder(
                                new TeXWordBreaker(new TeXPatternsSource(this))
                        )
                )
        );

        FontStyle.RenderParams fontRenderParams = new FontStyle.RenderParams(true, true, true, false);
        HangingConfig hangingConfig = new HangingConfig();
        LayoutParagraph paragraph = new LayoutParagraph(true, Locale.US, asList(
                new TextRun("This is text. This is text. This is te", new FontStyle(25, Color.RED, fontRenderParams)),
                new TextRun("xt. This is text.             This is text", new FontStyle(15, Color.BLUE, fontRenderParams)),
                new TextRun(" texttextextetetxetextxtextx", new FontStyle(15, Color.BLACK, fontRenderParams)),
                new TextRun("-text-text-text-exte-tet-xete-xtxt-extx,hhh,jj,,kk,llh,hh", new FontStyle(15, Color.BLACK, fontRenderParams))
        ), 0, TextAlign.JUSTIFY, hangingConfig);


        RenderObject renderParagraph = layouter.layout(paragraph, new LayoutArea(100, 100));

        View view = new View(this) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(Color.WHITE);
                renderParagraph.paintRecursive(canvas);
            }
        };

        setContentView(view);
    }
}
