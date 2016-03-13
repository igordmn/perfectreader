package com.dmi.perfectreader.manualtest.layout

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dmi.perfectreader.app.AppThreads.postUITask
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.ObjectLayouter
import com.dmi.perfectreader.layout.liner.breaker.CompositeBreaker
import com.dmi.perfectreader.layout.liner.breaker.LineBreaker
import com.dmi.perfectreader.layout.liner.breaker.ObjectBreaker
import com.dmi.perfectreader.layout.liner.breaker.WordBreaker
import com.dmi.perfectreader.layout.paragraph.DefaultHangingConfig
import com.dmi.perfectreader.layout.config.LayoutArea
import com.dmi.perfectreader.layout.paragraph.PaintTextMetrics
import com.dmi.perfectreader.layout.paragraph.Run
import com.dmi.perfectreader.layout.liner.hyphenator.TeXHyphenatorResolver
import com.dmi.perfectreader.layout.liner.hyphenator.TeXPatternsSource
import com.dmi.perfectreader.layout.liner.BreakLiner
import com.dmi.perfectreader.render.RenderObject
import com.dmi.perfectreader.style.FontStyle
import com.dmi.perfectreader.style.TextAlign
import com.dmi.util.base.BaseActivity
import com.dmi.util.debug.measureTime
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class LayoutTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text =
                "      Рассказ у нас пойдет в особенности о хоббитах, и любознательный читатель многое узнает об их нравах и кое-что из их истории. Самых любознательных отсылаем к повести под названием «Хоббит», где пересказаны начальные главы Алой Книги Западных Пределов, которые написал Бильбо Торбинс, впервые прославивший свой народец в большом мире. Главы эти носят общий подзаголовок «Туда и обратно», потому что повествуют о странствии Бильбо на восток и возвращении домой. Как раз по милости Бильбо хоббиты и угодили в самую лавину грозных событий, о которых нам предстоит поведать.\n" +
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
                "      Беляки — порода северная и самая малочисленная. Они, не в пример прочим хоббитам, сблизились с эльфами: сказки и песни им были милее, нежели ремесла, а охота любезнее земледелия. Они пересекли горы севернее Раздела и спустились по левому берегу реки Буйной. В Эриадоре они вскоре смешались с новооседлыми хоббитами иных пород и, будучи по натуре смелее и предприимчивее прочих, то и дело волею судеб оказывались вожаками и старейшинами струсов и лапитупов. Даже во времена Бильбо беляцкая порода очень еще чувствовалась в главнейших семействах вроде Кролов и Правителей Забрендии.\n" +
                "      Между Мглистыми и Лунными горами хоббитам встретились и эльфы, и люди. В ту пору еще жили здесь дунаданцы, царственные потомки тех, кто приплыл по морю с Заокраинного Запада, но их становилось все меньше, и Северное Княжество постепенно обращалось в руины. Пришельцев-хоббитов не обижали, места хватало, и они быстро обжились на новых землях. Ко времени Бильбо от первых хоббитских селений большей частью и следа не осталось, однако важнейшее из них все-таки сохранилось; хоббиты попрежнему жили в Пригорье и окрестном лесу Четбор, милях в сорока к востоку от Хоббитании.\n" +
                "      В те же далекие времена они, должно быть, освоили и письменность — на манер дунаданцев, которые когда-то давным-давно переняли ее у эльфов. Скоро они перезабыли прежние наречия и стали говорить на всеобщем языке, распространившемся повсюду — от Арнора до Гондора и на всем морском побережье, от Золотистого Взморья до Голубых гор. Впрочем, кое-какие свои древние слова хоббиты все же сохранили: названия месяцев, дней недели и, разумеется, очень многие имена собственные.\n" +
                "      Тут легенды наконец сменяет история, а несчетные века — отсчет лет. Ибо в тысяча шестьсот первом году Третьей эпохи братья-беляки Марчо и Бланка покинули Пригорье и, получив на то дозволение от великого князя в Форносте,<a id=\"fn1_back\" href=\"section76.xhtml#fn1\">[1]</a> пересекли бурную реку Барандуин во главе целого полчища хоббитов. Они прошли по Большому Каменному мосту, выстроенному в лучшие времена Северного Княжества, и распространились по заречным землям до Западного взгорья. Требовалось от них всего-навсего, чтобы они чинили Большой мост, содержали в порядке остальные мосты и дороги, препровождали княжеских гонцов и признавали князя своим верховным владыкой.\n" +
                "      Отсюда и берет начало Летосчисление Хоббитании (Л. X.), ибо год перехода через Брендидуим (так изменили хоббиты название реки) стал для Хоббитании Годом Первым, рубежом дальнейшего отсчета.<a id=\"fn2_back\" href=\"section77.xhtml#fn2\">[2]</a> Западные хоббиты сразу же полюбили свой новообретенный край, за его пределами не появлялись и вскоре снова исчезли из истории людей и эльфов. Они хоть и считались княжескими подданными, но делами их вершили свои вожаки, а в чужие дела они носа не совали. Когда Форност ополчился на последнюю битву с ангмарским царем-колдуном, они будто бы послали на помощь князю отряд лучников, но людские хроники этого не подтверждают. В этой войне Северное Княжество сгинуло; с той поры хоббиты стали считать себя полновластными хозяевами дарованной им земли и выбрали из числа вожаков своего Хоббитана, как бы наместника бывшего князя. Добрую тысячу лет войны обходили их стороной, и, пережив поветрие Черной Смерти в 37 г. (Л. X.), они плодились и множились, пока их не постигла Долгая Зима, а за нею страшный голод. Многие тысячи погибли голодной смертью, но уже и Дни Нужды (1158–1160) ко времени нашего рассказа канули в далекое прошлое, и хоббиты снова привыкли к изобилию. Край их был богатый и щедрый, и хотя достался им заброшенным, но прежде земля возделывалась на славу, и хозяйский взор князя некогда радовали угодья и нивы, сады и виноградники.\n" +
                "      С востока на запад, от Западного взгорья до Брендидуимского моста, земли их простирались на сорок лиг и на пятьдесят — от северных топей до южных болот. Все это стало называться Хоббитанией; в этом уютном закоулке хоббиты наладили жизнь по-своему, не обращая внимания на всякие безобразия за рубежами их земель, и привыкли считать, что покой и довольство — обыденная судьба обитателей Средиземья, а иначе и быть не должно. Они забыли или предали забвению то немногое, что знали о ратных трудах Стражей — давних радетелей мира на северо-западе. Хоббиты состояли под их защитой и перестали думать об этом.\n" +
                "      Чего в хоббитах не было, так это воинственности, и между собой они не враждовали никогда. В свое время им, конечно, пришлось, как водится в нашем мире, постоять за себя, но при Бильбо это уже было незапамятное прошлое. Отошла в область преданий и единственная битва в пределах Хоббитании: в Зеленополье в 1147 г. (Л. X.), когда Брандобрас Крол наголову разгромил вторгнувшихся орков. Климат и тот смягчился: былые зимние нашествия волков с севера стали бабушкиными сказками. Так что если в Хоббитании и можно было найти какое-нибудь оружие, то разве что по стенам, над каминами или среди хлама, пылившегося в музее города Землеройска. Музей этот назывался Мусомный Амбар, ибо всякая вещь, которую девать было некуда, а выбросить жалко, называлась у хоббитов мусомом. Такого мусома в жилищах у них накапливалось изрядно, и многие подарки, переходившие из рук в руки, были того же свойства.\n" +
                "      Однако сытая и спокойная жизнь почему-то вовсе не изнежила этих малюток. Припугнуть, а тем более пришибить хоббита было совсем не просто; может статься, они потому так и любили блага земные, что умели спокойно обходиться без них, переносили беды, лишения, напасти и непогодь куда тверже, чем можно было подумать, глядя на их упитанные животики и круглые физиономии. Непривычные к драке, не признававшие охоты, они вовсе не терялись перед опасностью и не совсем отвыкли от оружия. Зоркий глаз и твердая рука делали их меткими лучниками. Если уж хоббит нагибался за камнем, то всякий зверь знал, что надо удирать без оглядки.\n" +
                "      По преданию, когда-то все хоббиты рыли себе норы; они и сейчас считают, что под землей уютнее всего, но со временем им пришлось привыкать и к иным жилищам. По правде сказать, во дни Бильбо по старинке жили только самые богатые и самые бедные хоббиты. Бедняки ютились в грубых землянках, сущих норах, без окон или с одним окошком; а тем, кто позажиточнее, из уважения к древнему обычаю строили себе подземные хоромы. Не всякое место годилось для рытья широких и разветвленных ходов (именовавшихся смиалами); и в низинах хоббиты, размножившись, начали строить наземные дома. Даже в холмистых областях и старых поселках, таких, как Норгорд или Кроли, да и в главном городе Хоббитании, в Землеройске на Светлом нагорье, выросли деревянные, кирпичные и каменные строения. Особенно они были сподручны мельникам, кузнецам, канатчикам, тележникам и вообще мастеровым; ведь, даже еще живучи в норах, хоббиты с древних пор строили сараи и мастерские.\n" +
                "      Говорят, будто обычай строить фермы и амбары завели в Болотищах у Брендидуима. Тамошние хоббиты, жители Восточного удела, были крупные и большеногие и в сырую погоду носили томские башмаки. Но они, известное дело, происходили от струсов: недаром у них почти у всех обрастали волосом подбородки. Ни у лапитупов, ни у беляков никакой бороды не росло. Действительно, на Болотище и на Заячьи Холмы к востоку от Брендидуима хоббиты явились особняком, большей частью с юга: у них остались диковинные имена, и слова они роняли такие, каких в Хоббитании никогда не слыхивали.\n" +
                "      Вполне вероятно, что строить хоббиты научились у дунаданцев, как научились многому другому. Но могли научиться и прямо у эльфов, у первых наставников людей. Ведь даже Вышние эльфы тогда еще не покинули Средиземье и жили в то время на западе, близ Серебристой Гавани, да и не только там, но совсем неподалеку от Хоббитании. С незапамятного века виднелись на Подбашенных горах за пограничными западными топями три эльфийские башни. Далеко окрест сияли они в лунном свете. Самая высокая была дальше всех: она одиноко высилась на зеленом кургане. Хоббиты из Западного удела говорили, будто с вершины этой башни видно Море; но, насколько известно, на вершине башни ни один хоббит не бывал. Вообще редкие хоббиты видели Море, мало кто из них по Морю плавал и уж совсем никто об этом не рассказывал. Море морем, а даже речонки и лодочки были хоббитам очень подозрительны, и тем более тем из них, кто почему-либо умел плавать. Все реже и реже хоббиты заговаривали с эльфами и стали их побаиваться, а заодно и тех, кто с ними якшался. И Море сделалось для них страшным словом, напоминающим о смерти, и они отвратили взгляды от западных холмов.\n" +
                "      У кого бы они строить ни научились, у эльфов или у людей, но строили хоббиты по-своему. Башен им не требовалось. А требовались длинные, низкие и уютные строения. Самые старинные из них походили на выползшие из-под земли смиалы, крытые сеном, соломой или торфяными пластами; стены их немного пучились. Правда, так строили в Хоббитании только поначалу, а с тех пор все изменилось и усовершенствовалось, отчасти благодаря гномам, отчасти собственными стараниями. Главной особенностью хоббитских строений остались круглые окна и даже круглые двери.\n" +
                "      Дома и норы в Хоббитании рассчитывались на большую ногу, и обитали там большие семьи. (Бильбо и Фродо Торбинсы — холостяки — составляли исключение, как и во многом другом, например, в своих эльфийских пристрастиях.) Иногда, подобно Кролам из Преогромных Смиалов или Брендизайкам из Хоромин-у-Брендидуима, многие поколения родственников жили — не сказать, чтобы мирно — в дедовских норах, то бишь наземных особняках. Кстати, хоббиты — народ чрезвычайно семейственный, и уж с родством они считались крайне старательно. Они вырисовывали длинные ветвистые родословные древа. С Хоббитами важнее всего понять, кто кому родня и кто кому какая родня. Однако же в нашей книге было бы совершенно невозможно изобразить родословное древо, даже обозначив на нем только самых главных членов самых главных семейств — тут никакой книги не хватит. Генеалогические древа в конце Алой Книги Западных Пределов — сама по себе книга, и в нее никогда не заглядывал никто, кроме хоббитов. А хоббитам, если они верны себе, только это и требуется: им надо, чтобы в книгах было то, что они и так уже знают, и чтобы изложено это было просто и ясно, без всякой путаницы.\n"

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
        val paragraph = LayoutParagraph(true, Locale("ru", "RU"), listOf(Run.Text(text, FontStyle(12f, Color.BLACK, fontRenderParams))), 0f, TextAlign.JUSTIFY, hangingConfig)


        val par = AtomicReference<RenderObject>()

        val view = object : View(this) {
            override fun onDraw(canvas: Canvas) {
                canvas.drawColor(Color.WHITE)
                paint(canvas)
            }

            private fun paint(canvas: Canvas) {
                measureTime("paint") {
                    par.get().paintRecursive(canvas)
                }
            }
        }

        object : Thread() {
            override fun run() {
                testlayout()
                testlayout()
                testlayout()
                testlayout()
                testlayout()
                testlayout()
                testlayout()
                try {
                    sleep(3500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                postUITask {
                    setContentView(view)
                    view.postInvalidate()
                }
            }

            private fun testlayout() {
                try {
                    sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                layout()
            }

            private fun layout() {
                measureTime("layout") {
                    val renderParagraph = layouter.layout(paragraph, LayoutArea(700f, 100f))
                    par.set(renderParagraph)
                }
            }
        }.start()
    }
}
