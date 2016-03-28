package com.dmi.perfectreader.manualtest.layout

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dmi.perfectreader.layout.LayoutImage
import com.dmi.perfectreader.layout.ObjectLayouter
import com.dmi.perfectreader.layout.common.LayoutLength
import com.dmi.perfectreader.layout.common.LayoutSize
import com.dmi.perfectreader.layout.common.LayoutSize.Dimension
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.ResourceLoader
import com.dmi.perfectreader.layout.image.CachedBitmapLoader
import com.dmi.perfectreader.layout.image.DecodeBitmapLoader
import com.dmi.perfectreader.layout.paragraph.PaintTextMetrics
import com.dmi.perfectreader.layout.paragraph.liner.BreakLiner
import com.dmi.perfectreader.layout.paragraph.liner.breaker.CompositeBreaker
import com.dmi.perfectreader.layout.paragraph.liner.breaker.LineBreaker
import com.dmi.perfectreader.layout.paragraph.liner.breaker.ObjectBreaker
import com.dmi.perfectreader.layout.paragraph.liner.breaker.WordBreaker
import com.dmi.perfectreader.layout.paragraph.liner.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.layout.paragraph.liner.hyphenator.TeXPatternsSource
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
                        Dimension.Auto(
                                LayoutSize.Limits(
                                        LayoutLength.Absolute(700F),
                                        LayoutLength.Absolute(700F)
                                )
                        ),
                        Dimension.Fixed(
                                LayoutLength.Absolute(500F),
                                LayoutSize.Limits(
                                        LayoutLength.Absolute(Float.MAX_VALUE),
                                        LayoutLength.Absolute(Float.MAX_VALUE)
                                )
                        )
                ),
                "manualtest/pagebook/image.png"
        )

        val renderRoot = layouter.layout(rootBox, LayoutSpace.root(700F, 700F))

        val view = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                canvas.drawColor(Color.WHITE)
                renderRoot.paint(canvas)
            }
        }

        setContentView(view)
    }
}
