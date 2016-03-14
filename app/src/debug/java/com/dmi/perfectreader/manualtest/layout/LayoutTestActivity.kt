package com.dmi.perfectreader.manualtest.layout

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dmi.perfectreader.layout.LayoutImage
import com.dmi.perfectreader.layout.ObjectLayouter
import com.dmi.perfectreader.layout.config.LayoutContext
import com.dmi.perfectreader.layout.config.LayoutSize
import com.dmi.perfectreader.layout.config.ResourceLoader
import com.dmi.perfectreader.layout.layouter.CachedBitmapLoader
import com.dmi.perfectreader.layout.layouter.DecodeBitmapLoader
import com.dmi.perfectreader.layout.liner.BreakLiner
import com.dmi.perfectreader.layout.liner.breaker.CompositeBreaker
import com.dmi.perfectreader.layout.liner.breaker.LineBreaker
import com.dmi.perfectreader.layout.liner.breaker.ObjectBreaker
import com.dmi.perfectreader.layout.liner.breaker.WordBreaker
import com.dmi.perfectreader.layout.liner.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.layout.liner.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.layout.paragraph.PaintTextMetrics
import com.dmi.util.base.BaseActivity

class LayoutTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resourceLoader = object : ResourceLoader {
            override fun load(src: String) = assets.open(src)
        }
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
                ),
                CachedBitmapLoader(DecodeBitmapLoader(resourceLoader))
        )

        /*
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
        */

        val rootBox = LayoutImage(
                LayoutSize(
                        LayoutSize.LimitedValue(
                                LayoutSize.Value.WrapContent(), LayoutSize.Limit.Absolute(700F), LayoutSize.Limit.Absolute(1000F)
                        ),
                        LayoutSize.LimitedValue(
                                LayoutSize.Value.Absolute(500F), LayoutSize.Limit.None(), LayoutSize.Limit.None()
                        )
                ),
                "manualtest/pagebook/image.png"
        )

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
