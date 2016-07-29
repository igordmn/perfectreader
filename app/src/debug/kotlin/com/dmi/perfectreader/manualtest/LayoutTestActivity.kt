package com.dmi.perfectreader.manualtest

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dmi.perfectreader.fragment.book.bitmap.AndroidBitmapDecoder
import com.dmi.perfectreader.fragment.book.bitmap.CachedBitmapDecoder
import com.dmi.perfectreader.fragment.book.content.obj.*
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredParagraph.Run
import com.dmi.perfectreader.fragment.book.content.obj.param.*
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredSize.Dimension
import com.dmi.perfectreader.fragment.book.layout.UniversalObjectLayouter
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.CompositeBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.LineBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.ObjectBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.WordBreaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.CachedHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.fragment.book.layout.paragraph.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.BreakLiner
import com.dmi.perfectreader.fragment.book.layout.paragraph.metrics.PaintTextMetrics
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.perfectreader.fragment.book.pagination.column.merge
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.perfectreader.fragment.book.pagination.part.splitIntoParts
import com.dmi.perfectreader.fragment.book.paint.ColumnPainter
import com.dmi.perfectreader.fragment.book.paint.PartPainter
import com.dmi.perfectreader.fragment.book.paint.UniversalObjectPainter
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.SizeF
import java.util.*

class LayoutTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openResource = { path: String -> assets.open(path) }
        val bitmapDecoder = CachedBitmapDecoder(AndroidBitmapDecoder(openResource))
        val layouter = UniversalObjectLayouter(
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
        val selectionConfig = SelectionConfig(Color(255, 180, 213, 254), Color.WHITE)
        val hangingConfig = DefaultHangingConfig

        val testParagraph = ConfiguredParagraph(
                Locale.US,
                listOf(
                        Run.Text("This is text. This is text. This is te", ConfiguredFontStyle(25F, Color.RED, textRenderConfig, selectionConfig), range(0, 1000)),
                        Run.Text("xt. This is text.             This is text", ConfiguredFontStyle(15F, Color.BLUE, textRenderConfig, selectionConfig), range(1200, 1300)),
                        Run.Text(" texttextextetetxetextxtextx", ConfiguredFontStyle(15F, Color.BLACK, textRenderConfig, selectionConfig), range(1300, 1400)),
                        Run.Text("-text-text-text-exte-tet-xete-xtxt-extx,hhh,jj,,kk,llh,hh", ConfiguredFontStyle(15F, Color.BLACK, textRenderConfig, selectionConfig), range(1400, 1500))
                ),
                0F,
                TextAlign.JUSTIFY,
                true,
                hangingConfig, range(0, 1500)
        )

        val image = testFrame(ConfiguredImage(
                ConfiguredSize(Dimension.Auto(), Dimension.Auto()),
                "manualtest/pagebook/image.png",
                2F,
                true,
                range(1500, 1600)
        ))

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

        val paragraphs = bookText.split("\n").mapIndexed { i, it ->
            val range = range(1600 + i * 100, 1600 + (i + 1) * 100)
            ConfiguredParagraph(
                    Locale("ru", "RU"),
                    listOf(Run.Text(it, ConfiguredFontStyle(10F, Color.BLACK, textRenderConfig, selectionConfig), range)),
                    0F,
                    TextAlign.JUSTIFY,
                    true,
                    hangingConfig,
                    range
            )
        }

        val boxedParagraphs = paragraphs.map {
            testFrame(ConfiguredBox(
                    ConfiguredSize(Dimension.Auto(), Dimension.Auto()),
                    Align.LEFT,
                    listOf(it),
                    it.range
            ))
        }

        val box1 = testFrame(ConfiguredBox(
                ConfiguredSize(Dimension.Auto(), Dimension.Auto()),
                Align.LEFT,
                boxedParagraphs,
                LocationRange(boxedParagraphs.first().range.begin, boxedParagraphs.last().range.end)
        ))

        val rootBox = ConfiguredBox(
                ConfiguredSize(Dimension.Auto(), Dimension.Auto()),
                Align.LEFT,
                listOf(testParagraph, image) + box1,
                LocationRange(testParagraph.range.begin, box1.range.end)
        )

        val layoutRoot = layouter.layout(rootBox, LayoutSpace.root(SizeF(700F, 700F)))
        val layoutParts = splitIntoParts(layoutRoot)

        val painter = ColumnPainter(PartPainter(UniversalObjectPainter(bitmapDecoder)))

        val begin = layoutParts.first().range.begin
        var column = LayoutColumn(emptyList(), 0F, LocationRange(begin, begin))
        for (part in layoutParts) {
            column = column merge part
        }

        val selectionRange = run {
            val run1 = testParagraph.runs[0] as ConfiguredParagraph.Run.Text
            val run2 = paragraphs[1].runs[0] as ConfiguredParagraph.Run.Text
            LocationRange(run1.sublocation(1), run2.sublocation(10))
        }
        val context = PageContext(selectionRange)

        val view = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                canvas.save()
                canvas.drawColor(Color.WHITE.value)
                painter.paint(column, context, canvas)
            }
        }

        setContentView(view)
    }

    fun testFrame(obj: ConfiguredObject) = ConfiguredFrame(
            ConfiguredFrame.Margins(
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F)
            ),
            ConfiguredFrame.Paddings(
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F),
                    Length.Absolute(10F)
            ),
            ConfiguredFrame.Borders(
                    ConfiguredFrame.Border(8F, Color.RED),
                    ConfiguredFrame.Border(8F, Color.GREEN),
                    ConfiguredFrame.Border(4F, Color.BLUE),
                    ConfiguredFrame.Border(4F, Color.MAGENTA)
            ),
            ConfiguredFrame.Background(Color.GRAY),
            obj,
            obj.range
    )

    private fun range(begin: Int, end: Int) = LocationRange(Location(begin.toDouble()), Location(end.toDouble()))
}