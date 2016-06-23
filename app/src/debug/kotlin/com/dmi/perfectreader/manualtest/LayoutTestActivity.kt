package com.dmi.perfectreader.manualtest

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dmi.perfectreader.fragment.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.fragment.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.layouter.UniversalLayouter
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.CompositeBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.LineBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.ObjectBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.breaker.WordBreaker
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator.CachedHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.liner.BreakLiner
import com.dmi.perfectreader.fragment.book.layout.layouter.paragraph.metrics.PaintTextMetrics
import com.dmi.perfectreader.fragment.book.layout.pagination.*
import com.dmi.perfectreader.fragment.book.layout.painter.UniversalObjectPainter
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.*
import com.dmi.perfectreader.fragment.book.obj.layout.*
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutParagraph.Run
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutSize
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutSize.Dimension
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.SizeF
import java.util.*

class LayoutTestActivity : AppCompatActivity() {
    val range = LocationRange(Location(0.0), Location(0.0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openResource = { path: String -> assets.open(path) }
        val bitmapDecoder = CachedBitmapDecoder(AndroidBitmapDecoder(openResource))
        val layouter = UniversalLayouter(
                PaintTextMetrics(),
                BreakLiner(
                        CompositeBreaker(
                                LineBreaker(),
                                ObjectBreaker(),
                                WordBreaker(
                                        CachedHyphenatorResolver(
                                                TeXHyphenatorResolver(
                                                        TeXPatternsSource(
                                                                this
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                bitmapDecoder
        )


        val textRenderConfig = TextRenderConfig(true, true, true, false)
        val hangingConfig = DefaultHangingConfig

        val bookText = "      Рассказ у нас пойдет в особенности о хоббитах, и любознательный читатель многое узнает об их нравах и кое-что из их истории. Самых любознательных отсылаем к повести под названием «Хоббит», где пересказаны начальные главы Алой Книги Западных Пределов, которые написал Бильбо Торбинс, впервые прославивший свой народец в большом мире. Главы эти носят общий подзаголовок «Туда и обратно», потому что повествуют о странствии Бильбо на восток и возвращении домой. Как раз по милости Бильбо хоббиты и угодили в самую лавину грозных событий, о которых нам предстоит поведать.\n" +
                       "      Многие, однако, и вообще про хоббитов ничего не знают, а хотели бы знать — но не у всех же есть под рукой книга «Хоббит». Вот и прочтите, если угодно, начальные сведения о хоббитах, а заодно и краткий пересказ приключений Бильбо.\n" +
                       "      Хоббиты — неприметный, но очень древний народец; раньше их было куда больше, чем нынче: они любят тишину и покой, тучную пашню и цветущие луга, а сейчас в мире стало что-то очень шумно и довольно тесно. Умелые и сноровистые, хоббиты, однако, терпеть не могли — не могут и поныне — устройств сложнее кузнечных мехов, водяной мельницы и прялки.\n" +
                       "      Издревле сторонились они людей — на их языке Громадин, — а теперь даже и на глаза им не показываются. Слух у них завидный, глаз острый; они, правда, толстоваты и не любят спешки, но в случае чего проворства и ловкости им не занимать. Хоббиты привыкли исчезать мгновенно и бесшумно при виде незваной Громадины, да так наловчились, что людям это стало казаться волшебством. А хоббиты ни о каком волшебстве и понятия не имели: отроду мастера прятаться, они — чуть что — скрывались из глаз, на удивление своим большим и неуклюжим соседям.\n" +
                       "      Они ведь маленькие, в полчеловека ростом, меньше даже гномов — пониже и не такие крепкие да кряжистые. Сейчас-то и трехфутовый хоббит — редкость, а раньше, говорят, все они были не очень уж малорослые. Согласно Алой Книге, Бандобрас Крол (Быкобор), сын Изенгрима Второго, был ростом четыре фута пять дюймов и сиживал верхом на лошади. Во всей хоббитской истории с ним могут сравниться лишь два достопамятных мужа древности; об их-то похождениях и повествуется в нашей хронике.\n" +
                       "      Во дни мира и благоденствия хоббиты жили как жилось — а жилось весело. Одевались пестро, все больше в желтое и зеленое, башмаков не носили: твердые их ступни обрастали густой курчавой шерсткой, обычно темно-русой, как волосы на голове. Так что сапожное ремесло было у них не в почете, зато процветали другие ремесла, и длинные искусные пальцы хоббитов мастерили очень полезные, а главное — превосходные вещи. Лица их красотою не отличались, скорее добродушием — щекастые, ясноглазые, румяные, рот чуть не до ушей, всегда готовый смеяться, есть и пить. Смеялись до упаду, пили и ели всласть, шутки были незатейливые, еда по шесть раз на день (было бы что есть). Радушные хоббиты очень любили принимать гостей и получать подарки — и сами в долгу не оставались.\n" +
                       "      Вероятно, хоббиты — наши прямые сородичи, не в пример ближе эльфов, да и гномов. Исстари говорили они на человеческом наречии, по-своему перекроенном, и во многом походили на людей. Но что у нас с ними за родство — теперь уж не выяснить. Хоббиты — порождение незапамятных дней Предначальной Эпохи. Одни эльфы хранят еще письменные предания тех канувших в прошлое древних времен, да и то лишь о себе — про людей там мало, а про хоббитов и вовсе не вспоминается. Так, никем не замеченные, хоббиты жили себе в Средиземье долгие века. В мире ведь полным-полно всякой чудной твари, и кому было какое дело до этих малюток? Но при жизни Бильбо и наследника его Фродо они вдруг, сами того ничуть не желая, стали всем важны и всем известны, и о них заговорили на Советах Мудрецов и Властителей.\n" +
                       "      Третья эпоха Средиземья давно минула, и мир сейчас уж совсем не тот, но хоббиты живут там же, где жили тогда: на северо-западе Старого Света, к востоку от Моря. А откуда они взялись и где жили изначально — этого никто не знал уже и во времена Бильбо. Ученость была у них не в почете (разве что родословие), но в старинных семействах по-прежнему водился обычай не только читать свои хоббитские книги, но и разузнавать о прежних временах и дальних странах у эльфов, гномов и людей. Собственные их летописи начинались с заселения Хоббитании, и даже самые старые хоббитские были восходят к Дням Странствий, не ранее того. Однако же и по этим преданиям, и по некоторым словечкам и обычаям понятно, что хоббиты, подобно другим народам, пришли когда-то с востока.\n" +
                       "      Древнейшие были их хранят смутный отблеск тех дней, когда они обитали в равнинных верховьях Андуина, между закраинами Великой Пущи и Мглистыми горами. Но почему они вдруг пустились в трудное и опасное кочевье по горам и перебрались в Эриадор — теперь уж не понять. Упоминалось там у них, правда, что, мол, и людей кругом развелось многовато и что на Пущу надвинулась какая-то тень и омраченная Пуща даже и называться стала по-новому — Лихолесье.\n" +
                       "      Еще до кочевья через горы насчитывалось три породы хоббитов: лапитупы, струсы и беляки. Лапитупы были посмуглее и помельче, бород не имели, башмаков не носили; у них были цепкие руки и хваткие ноги, и жили они преимущественно в горах, на горных склонах. Струсы были крепенькие, коренастенькие, большерукие и большеногие; селились они на равнинах и в поречье. А беляки — светлокожие и русоволосые, выше и стройнее прочих; им по душе была зелень лесов.\n" +
                       "      Лапитупы в старину водили дружбу с гномами и долго прожили в предгорьях. На запад они стронулись рано и блуждали по Эриадору близ горы Заверть, еще когда их сородичи и не думали покидать свое Глухоманье. Они были самые нормальные, самые правильные хоббиты, и они дольше всех сохранили обычай предков — рыть норы и подземные ходы.\n" +
                       "      Струсы давным-давно жили по берегам Великой Реки Андуин и там привыкли к людям. На запад они потянулись за лапитупами, однако же свернули к югу вдоль реки Бесноватой; многие из них расселились от переправы Тарбад до Сирых Равнин; потом они опять немного подались на север.\n" +
                       "      Беляки — порода северная и самая малочисленная. Они, не в пример прочим хоббитам, сблизились с эльфами: сказки и песни им были милее, нежели ремесла, а охота любезнее земледелия. Они пересекли горы севернее Раздела и спустились по левому берегу реки Буйной. В Эриадоре они вскоре смешались с новооседлыми хоббитами иных пород и, будучи по натуре смелее и предприимчивее прочих, то и дело волею судеб оказывались вожаками и старейшинами струсов и лапитупов. Даже во времена Бильбо беляцкая порода очень еще чувствовалась в главнейших семействах вроде Кролов и Правителей Забрендии.\n"

        val testParagraph = LayoutParagraph(
                Locale.US,
                listOf(
                        Run.Text("This is text. This is text. This is te", LayoutFontStyle(25F, Color.Companion.RED, textRenderConfig), range),
                        Run.Text("xt. This is text.             This is text", LayoutFontStyle(15F, Color.Companion.BLUE, textRenderConfig), range),
                        Run.Text(" texttextextetetxetextxtextx", LayoutFontStyle(15F, Color.Companion.BLACK, textRenderConfig), range),
                        Run.Text("-text-text-text-exte-tet-xete-xtxt-extx,hhh,jj,,kk,llh,hh", LayoutFontStyle(15F, Color.Companion.BLACK, textRenderConfig), range)
                ),
                0F,
                TextAlign.JUSTIFY,
                true,
                hangingConfig, range
        )

        val bookParagraphs = bookText.split("\n").map {
            LayoutParagraph(
                    Locale("ru", "RU"),
                    listOf(Run.Text(it, LayoutFontStyle(10F, Color.Companion.BLACK, textRenderConfig), range)),
                    0F,
                    TextAlign.JUSTIFY,
                    true,
                    hangingConfig, range
            )
        }

        val boxedParagraphs = bookParagraphs.map {
            testFrame(LayoutBox(
                    LayoutSize(Dimension.Auto(), Dimension.Auto()),
                    Align.LEFT,
                    listOf(it),
                    range
            ))
        }

        val image = testFrame(LayoutImage(
                LayoutSize(Dimension.Auto(), Dimension.Auto()),
                "manualtest/pagebook/image.png",
                range
        ))

        val box1 = testFrame(LayoutBox(
                LayoutSize(Dimension.Auto(), Dimension.Auto()),
                Align.LEFT,
                boxedParagraphs,
                range
        ))

        val rootBox = LayoutBox(
                LayoutSize(Dimension.Auto(), Dimension.Auto()),
                Align.LEFT,
                listOf(testParagraph, image) + box1,
                range
        )

        val renderRoot = layouter.layout(rootBox, LayoutSpace.root(SizeF(700F, 700F)))
        val renderParts = splitIntoParts(renderRoot)

        val painter = RenderColumnPainter(RenderPartPainter(UniversalObjectPainter(bitmapDecoder)))

        val begin = renderParts.first().range.begin
        var column = RenderColumn(emptyList(), 0F, LocationRange(begin, begin))
        for (part in renderParts) {
            column = column merge part
        }

        val view = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                val density = resources.displayMetrics.density
                canvas.save()
                canvas.scale(density, density)
                canvas.drawColor(Color.WHITE.value)
                painter.paint(column, canvas)
                canvas.restore()
            }
        }

        setContentView(view)
    }

    fun testFrame(obj: LayoutObject) = LayoutFrame(
            LayoutFrame.Margins(
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F)
            ),
            LayoutFrame.Paddings(
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F)
            ),
            LayoutFrame.Borders(
                    LayoutFrame.Border(4F, Color.Companion.RED),
                    LayoutFrame.Border(4F, Color.Companion.GREEN),
                    LayoutFrame.Border(4F, Color.Companion.BLUE),
                    LayoutFrame.Border(4F, Color.Companion.MAGENTA)
            ),
            LayoutFrame.Background(Color.Companion.GRAY),
            obj,
            range
    )
}