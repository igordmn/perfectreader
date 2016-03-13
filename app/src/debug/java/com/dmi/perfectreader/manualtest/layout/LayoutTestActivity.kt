package com.dmi.perfectreader.manualtest.layout

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.view.View
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.ObjectLayouter
import com.dmi.perfectreader.layout.config.LayoutContext
import com.dmi.perfectreader.layout.config.LayoutSize
import com.dmi.perfectreader.layout.liner.BreakLiner
import com.dmi.perfectreader.layout.liner.breaker.CompositeBreaker
import com.dmi.perfectreader.layout.liner.breaker.LineBreaker
import com.dmi.perfectreader.layout.liner.breaker.ObjectBreaker
import com.dmi.perfectreader.layout.liner.breaker.WordBreaker
import com.dmi.perfectreader.layout.liner.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.layout.liner.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.layout.paragraph.DefaultHangingConfig
import com.dmi.perfectreader.layout.paragraph.PaintTextMetrics
import com.dmi.perfectreader.layout.paragraph.Run
import com.dmi.perfectreader.style.FontStyle
import com.dmi.perfectreader.style.TextAlign
import com.dmi.util.base.BaseActivity
import java.util.*

class LayoutTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layouter = ObjectLayouter(
                PaintTextMetrics(),
                BreakLiner(
                        CompositeBreaker(
                                LineBreaker(),
                                ObjectBreaker(),
                                WordBreaker(
                                        TeXHyphenatorResolver(
                                                TeXPatternsSource(
                                                        this
                                                )
                                        )
                                )
                        )
                )
        )

        val fontRenderParams = FontStyle.RenderParams(true, true, true, false)
        val hangingConfig = DefaultHangingConfig()
        val rootBox = LayoutParagraph(
                LayoutSize(
                        LayoutSize.LimitedValue(
                                LayoutSize.Value.Absolute(700F), LayoutSize.Limit.None(), LayoutSize.Limit.None()
                        ),
                        LayoutSize.LimitedValue(
                                LayoutSize.Value.WrapContent(), LayoutSize.Limit.None(), LayoutSize.Limit.None()
                        )
                ), Locale.US, listOf(
                Run.Text("This is text. This is text. This is te", FontStyle(25F, RED, fontRenderParams)),
                Run.Text("xt. This is text.             This is text", FontStyle(15F, BLUE, fontRenderParams)),
                Run.Text(" texttextextetetxetextxtextx", FontStyle(15F, BLACK, fontRenderParams)),
                Run.Text("-text-text-text-exte-tet-xete-xtxt-extx,hhh,jj,,kk,llh,hh", FontStyle(15F, BLACK, fontRenderParams))
        ), 0F, TextAlign.JUSTIFY, hangingConfig)

        val renderRoot = layouter.layout(rootBox, LayoutContext.root(700F, 700F))

        val view = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                canvas.drawColor(Color.WHITE)
                renderRoot.paintRecursive(canvas)
            }
        }

        setContentView(view)
    }
}
