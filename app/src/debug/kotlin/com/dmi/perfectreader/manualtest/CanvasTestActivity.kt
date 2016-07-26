package com.dmi.perfectreader.manualtest

import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.Matrix
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.support.v7.app.AppCompatActivity
import com.dmi.util.android.graphics.FontConfig
import com.dmi.util.android.graphics.FontFaceID
import com.dmi.util.android.graphics.TextLibrary
import com.dmi.util.android.opengl.*
import com.dmi.util.android.paint.Canvas
import com.dmi.util.debug.measureTime
import com.dmi.util.graphic.Color
import com.dmi.util.graphic.Size
import java.io.File

class CanvasTestActivity : AppCompatActivity() {
    private lateinit var glSurface: GLSurfaceViewExt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurface = GLSurfaceViewExt(this)
        glSurface.setFixedRenderer { RendererImpl(it) }
        setContentView(glSurface)
    }

    override fun onResume() {
        super.onResume()
        glSurface.onResume()
    }

    override fun onPause() {
        glSurface.onPause()
        super.onPause()
    }

    private val bookChars = "ПРЕДИСЛОВИЕ АВТОРА Эта сказка возникла в устных рассказах, пока не стала историей Великой Войны Кольца, включая множество эскурсов в более древние времена. Она начала создаваться после того, как был написан Хоббит, и по его первой публикации в 1937 году: но я не торопился с продолжением, потому что хотел прежде собрать и привести в порядок мифологию и легенды древних дней, а для этого потребовалось несколько лет. Я делал это для собственного удовольствия и мало надеялся, что другие люди заинтересуются моей работой, особенно потому что она была преимущественно лингвистической по побуждениям и возникла из необходимости привести в порядок мои отрывочные сведения о языках эльфов. Когда те, чьими советами и поддержкой я пользовался, заменили выражение малая надежда на никакой надежды, я вернулся к продолжению, подбадриваемый требованиями читателей сообщить больше информации, касающейся хоббитов и их приключений. Но мой рассказ, все более углубляясь в прошлое, все не мог кончиться. Процесс этот начался при написании Хоббита, в котором были упоминания о более давних событиях: Элронд, Гондолин, перворожденные эльфы, орки, словно проблески на фоне более недавних событий: Дурин (Дьюрин, Дарин - варианты написания), Мория, Гэндальф, Некромант,Кольцо. Постепенно раскрытие значения этих упоминаний в их отношении к древней истории раскрывало Третью эпоху и ее кульминацию в войне Кольца. Те, кто просил больше информации о хоббитах, постепенно получили ее, но им пришлось долго ждать: создание Властелина Колец заняло интервал с 1936 по 1949 год, период, когда у меня было множество обязанностей, которыми я не мог пренебречь, и мои собственные интересы в качестве преподавателя и лектора поглощали меня. Отсрочка еще более удлиннилась из-за начавшейся в 1939 году войны: к ее окончанию я едва достиг конца первой книги. Несмотря на трудные пять военных лет, я понял, что не могу совершенно отказаться от своего рассказа, и продолжал работать, большей частью по ночам, пока не оказался у могилы Балина в Мории. Здесь я надолго задержался. Почти год спустя я возобновил работу и к концу 1941 года добрался до Лориена и Великой Реки. В следующем году я набросал первые главы того, что сейчас является книгой третьей, а также начало первой и пятой глав пятой книги. Здесь я снова остановился. Предвидеть будущее оказалось невозможно, и не было времени для раздумий. В 1944 году, позавязав все узлы и пережив все затруднения войны, которые я считаю своей обязанностью решить или по крайней мере попытаться решить, и начал рассказывать о путешествии Фродо в Мордор. Эти главы, постепенно выраставшие в книгу четвертую, писались и посылались по частям моему сыну Кристоферу в Южную Америку при помощи английских военно-воздушных сил. Тем не менее потребовалось еще пять лет для завершения сказки: за это время я сменил дом, работу, дни эти хотя и не были менее мрачными, оставались очень напряженными. Затем всю сказку нужно было перечитать, переработать. Напечатать и перепечать. Я делал это сам: у меня не было средств для найма профессиональной машинистки. С тех пор как десять лет назад Властелин Колец был напечатан впервые, его прочитали многие; и мне хочется здесь выразить свое отношение к множеству отзывов и предложений, высказанных по поводу этой сказки, ее героев и побудительных мотивов автора. Главным побудительным мотивом было желание сказочника испробовать свои силы в действительно длинной сказке, которая удержала бы внимание читателей, развлекла их и доставила им радость, а иногда, может быть, и тронула. В качестве проводника мне служило лишь мое собственное чувство, а многих такой проводник подводил. Некоторые из читателей нашли книгу скучной, нелепой или недостойной внимания, и я не собираюсь с ними спорить, ибо испытываю анологичные чувства по отношению к их книгам или книге, которые они прочитают. Но даже с точки зрения тех, кому понравилась моя книга, в ней есть немало недостатков. Вероятно, невозможно в длинной сказке в равной мере удовлетворить всех читателей: я обнаружил, что те отрывки или главы, которые одни мои читатели считают слабыми, другим очень нравятся. Наиболее критичный читатель - сам автор - видит теперь множество недостатков, больших и малых, но так как он, к счастью, не обязан пересматривать книгу или писать ее заново, то пройдет мимо них в молчании, отметив лишь один недостаток, отмеченный некоторыми читателями: эта книга слишком коротка. Что касается внутреннего смысла - подтекста книги, то автор его не видит вовсе. Книга не является ни аллегорической, ни злободневной. По мене своего роста сказка пускала корни в прошлое и выбрасывала неожиданные ветви, но главное ее содержание основывалось на неизбежном выборе Кольца как связи между нею и Хоббитом. Ключевая глава - тень прошлого - является одной из самых первых написанных глав сказки. Она была написана задолго до того, как 1939 год предвестил угрозу всеобщего уничтожения, и с этого пункта рассказ развивается дальше по тем же основным линиям, как будто это уничтожение уже было предотвращено. Источники этой сказки заключены глубоко в сознании и имеют мало общего с войной, начавшейся в 1939 году, и с ее последствиями. Реальная война не соответствует легендарной ни по ходу, ни по последствиям. Если бы война вызывала или бы направляла развитие легенды, тогда, несомненно, Кольцо было бы использовано против Саурона: он не был бы уничтожен, но порабощен, а Барад-Дур не разрушен, а оккупирован. Мало того, Саруман, не сумев завладеть Кольцом, нашел бы в Мордоре недостающие сведения о нем, сделал бы Великое Кольцо своим и сменил бы самозваного правителя Средиземья. В этой борьбе обе стороны возненавидели бы хоббитов; хоббиты недолго бы выжили даже как рабы. И другие изменения могли бы быть сделаны с точки зрения тех, кто любит аллегорические или злободневные соответствия. Но я страшно не люблю аллегории при всех их проявлениях, и сколько я себя помню, всегда относился к ним так. Я предпочитаю историю, истиную или притворную, с ее применимостью к мыслям и опыту читателей. Мне кажется, что многие смешивают применимость с аллегоричностью: но первая оставляет читателей свободными, а вторая провозглашает господство автора. Автор, конечно, не может оставаться полностью незатронутый своим опытом, но пути, на которых зародыш рассказа использует почву опыта, очень сложны, и попытки понять этот процесс в лучшем случае получаются загадками. Которые, хотя и весьма привлекательно предположить, когда жизнь автора или авторов критики частично сокращают во времени, что общие для них обоих события или направления мысли делаются наиболее сильными влияниями. Которые действительно могут испытать сильные воздействия войны: но годы идут, и часто забывают, что в войну 1914 года испытали не меньшее потрясение, чем те, что встретили войну 1939 года. К 1918 году все мои близкие друзья, за исключением одного, был мертвы. Или возьмем другой, еще более прискорбный случай. Некоторые предположили, что очищение Удела напоминает ситуацию в Англии времени окончания моей сказки. Это неверно. Эта ситуация является существенной частью общего плана, намеченного с самого начала, хотя в ходе написания события несколько изменились в соответствии с характером Сарумана, но без всякого аллегорического значения или злободневных перекличек с политическими событиями. Это описание, конечно, основано на опыте, хотя основания эти довольно слабые (экономическая ситуация совершенно различна). Местность, в которой я провел детство, обеднела к тому времени, когда мне стукнуло десять, в дни, когда автомобили были редкостью, я не видел ни одного, а люди все еще строили пригородные железные дороги. Недавно я видел рисунок дряхлой мельницы у пруда, а когда-то она мне казалась такой огромной. Внешность молодого мельника мне никогда не нравилась, но его отец, старый мельник, носил черную бороду и его нельзя было назвать рыжим. Властелин Колец появляются в новом издании, и у меня появилась возможность пересмотреть книгу. Было исправлено некоторое количество ошибок и несообразностей в тексте; была так же предпринята попытка представить информацию по нескольким пунктам, на которые обратили внимание вдумчивые читатели. Я собирал все их запросы и замечания, и если некоторые из них остались без внимания, то причина в том, что я все еще не могу привести их в порядок; впрочем на некоторые запросы можно ответить лишь добавив новые главы, содержащие материалы, не включенные в первое издание. Пока же настоящее издание предлагает читателю это предисловие, пролог и индекс имен и мест".toCharArray()

    private inner class RendererImpl(val size: Size) : FixedRenderer {
        private val sizeF = size.toFloat()

        val textLibrary = TextLibrary()
        val facePath = FontFaceID(File(getExternalStorageDirectory(), "fonts/ARIAL.TTF"), 0)
        val fontConfig = run {
            val sizeX = 14F
            val sizeY = 14F

            val hinting = true
            val forceAutoHinting = false
            val lightHinting = true

            val scaleX = 1.0F
            val scaleY = 1.0F
            val skewX = 1.0F
            val skewY = 1.0F

            val embolden = false
            val emboldenStrengthX = 0.0F
            val emboldenStrengthY = 0.0F

            val strokeInside = false
            val strokeOutside = false
            val strokeLineCap = FontConfig.StrokeLineCap.BUTT
            val strokeLineJoin = FontConfig.StrokeLineJoin.BEVEL
            val strokeMiterLimit = 0F
            val strokeRadius = 0F

            val antialias = true
            val gamma = 1.0F
            val blurRadius = 0F
            val color = Color.BLUE.value

            FontConfig(
                    facePath,
                    sizeX,
                    sizeY,

                    hinting,
                    forceAutoHinting,
                    lightHinting,

                    scaleX,
                    scaleY,
                    skewX,
                    skewY,

                    embolden,
                    emboldenStrengthX,
                    emboldenStrengthY,

                    strokeInside,
                    strokeOutside,
                    strokeLineCap,
                    strokeLineJoin,
                    strokeMiterLimit,
                    strokeRadius,

                    antialias,
                    gamma,
                    blurRadius,
                    color
            )
        }

        val glyphIndices = IntArray(bookChars.size).apply {
            textLibrary.getGlyphIndices(facePath, bookChars, this)
        }
        val coordinates = FloatArray(glyphIndices.size * 2).apply {
            var x = 0F
            var y = 0F
            for (i in 0..size - 1 step 2) {
                this[i] = x
                this[i + 1] = y
                x += 10
                if (x > sizeF.width) {
                    x = 0F
                    y += 10F
                }
            }
        }

        private val plane = GLTexturePlane(this@CanvasTestActivity, sizeF)

        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val viewProjectionMatrix = FloatArray(16)

        private val texture: GLTexture
        private val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        private val canvas = Canvas(textLibrary, bitmap)

        init {
            glDisable(GL_DEPTH_TEST)
            glEnable(GL_BLEND)
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
            glViewport(0, 0, size.width, size.height)

            Matrix.orthoM(projectionMatrix, 0, 0F, sizeF.width, sizeF.height, 0F, -1F, 1F)
            Matrix.setLookAtM(viewMatrix, 0, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F)
            Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

            glClearColor(1F, 1F, 1F, 1F)
            glClear(GL_COLOR_BUFFER_BIT)

            texture = GLTexture(size)
        }

        override fun destroy() {
            textLibrary.destroy()
        }

        override fun draw() {
            glClearColor(1F, 1F, 1F, 1F)
            glClear(GL_COLOR_BUFFER_BIT)

            canvas.clear()

            measureTime {
                canvas.drawText(fontConfig, glyphIndices, coordinates)
            }

            texture.refreshBy(bitmap)
            plane.draw(viewProjectionMatrix, texture)
        }
    }
}