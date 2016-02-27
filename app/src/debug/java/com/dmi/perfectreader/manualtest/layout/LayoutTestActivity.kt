package com.dmi.perfectreader.manualtest.layout

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.ObjectLayouter
import com.dmi.perfectreader.layout.config.DefaultHangingConfig
import com.dmi.perfectreader.layout.config.LayoutArea
import com.dmi.perfectreader.layout.config.PaintTextMetrics
import com.dmi.perfectreader.layout.liner.BreakLiner
import com.dmi.perfectreader.layout.liner.RuleBreakFinder
import com.dmi.perfectreader.layout.run.TextRun
import com.dmi.perfectreader.layout.wordbreak.TeXPatternsSource
import com.dmi.perfectreader.layout.wordbreak.TeXWordBreaker
import com.dmi.perfectreader.style.FontStyle
import com.dmi.perfectreader.style.TextAlign
import com.dmi.util.base.BaseActivity
import java.util.*
import java.util.Arrays.asList

class LayoutTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layouter = ObjectLayouter(
                PaintTextMetrics(),
                BreakLiner(
                        RuleBreakFinder(
                                TeXWordBreaker(TeXPatternsSource(this))
                        )
                )
        )

        val fontRenderParams = FontStyle.RenderParams(true, true, true, false)
        val hangingConfig = DefaultHangingConfig()
        val paragraph = LayoutParagraph(true, Locale.US, asList(
                TextRun("This is text. This is text. This is te", FontStyle(25f, Color.RED, fontRenderParams)),
                TextRun("xt. This is text.             This is text", FontStyle(15f, Color.BLUE, fontRenderParams)),
                TextRun(" texttextextetetxetextxtextx", FontStyle(15f, Color.BLACK, fontRenderParams)),
                TextRun("-text-text-text-exte-tet-xete-xtxt-extx,hhh,jj,,kk,llh,hh", FontStyle(15f, Color.BLACK, fontRenderParams))), 0f, TextAlign.JUSTIFY, hangingConfig)


        val renderParagraph = layouter.layout(paragraph, LayoutArea(100f, 100f))

        val view = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                canvas.drawColor(Color.WHITE)
                renderParagraph.paintRecursive(canvas)
            }
        }

        setContentView(view)
    }
}
