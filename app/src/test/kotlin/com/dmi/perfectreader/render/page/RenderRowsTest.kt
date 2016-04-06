package com.dmi.perfectreader.render.page

import com.dmi.perfectreader.location.BookLocation
import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.render.RenderChild
import com.dmi.perfectreader.render.RenderObject
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class RenderRowsTest {
    @Test
    fun `split complex object into rows`() {
        // given
        val rootObj =
                TestObject("root", true, 100F, 10F, 40F, listOf(
                        TestObject("child_0", true, 110F, 11F, 39F, listOf(
                                TestObject("!child_0_0", false, 120F, 12F, 38F),
                                TestObject("child_0_1", true, 130F, 13F, 37F, listOf(
                                        TestObject("!child_0_1_0", false, 140F, 14F, 36F, listOf(
                                                TestObject("child_0_1_0_0", true, 150F, 15F, 35F)
                                        )),
                                        TestObject("!child_0_1_1", true, 160F, 16F, 34F),
                                        TestObject("!child_0_1_2", false, 170F, 17F, 33F),
                                        TestObject("child_0_1_3", true, 180F, 18F, 32F, listOf(
                                                TestObject("!child_0_1_3_0", false, 190F, 19F, 31F),
                                                TestObject("!child_0_1_3_1", false, 200F, 20F, 30F)
                                        )),
                                        TestObject("!child_0_1_4", false, 210F, 21F, 29F)
                                ))
                        )),
                        TestObject("!child_1", false, 220F, 22F, 28F),
                        TestObject("child_2", true, 230F, 23F, 27F, listOf(
                                TestObject("child_2_0", true, 240F, 24F, 26F, listOf(
                                        TestObject("child_2_0_0", true, 240F, 25F, 24F, listOf(
                                                TestObject("!child_2_0_0_0", true, 260F, 26F, 23F)
                                        ))
                                ))
                        ))
                ))
        fun absoluteTopOf(id: String) = absoluteTopOf(rootObj, id)
        fun absoluteBottomOf(id: String) = absoluteBottomOf(rootObj, id)

        // when
        val rows = splitIntoRows(rootObj)

        // then
        objectsOf(rows) shouldEqual listOf(
                rootObj,
                rootObj,
                rootObj,
                rootObj,
                rootObj,
                rootObj,
                rootObj,
                rootObj,
                rootObj
        )
        topChildIndicesOf(rows) shouldEqual listOf(
                listOf(0, 0),
                listOf(0, 1, 0),
                listOf(0, 1, 1),
                listOf(0, 1, 2),
                listOf(0, 1, 3, 0),
                listOf(0, 1, 3, 1),
                listOf(0, 1, 4),
                listOf(1),
                listOf(2, 0, 0, 0)
        )
        bottomChildIndicesOf(rows) shouldEqual topChildIndicesOf(rows)

        topsOf(rows) shouldEqual listOf(
                absoluteTopOf("root") + 10F,
                absoluteTopOf("child_0_1") + 13F,
                absoluteTopOf("!child_0_1_1") + 16F,
                absoluteTopOf("!child_0_1_2") + 17F,
                absoluteTopOf("child_0_1_3") + 18F,
                absoluteTopOf("!child_0_1_3_1") + 20F,
                absoluteTopOf("!child_0_1_4") + 21F,
                absoluteTopOf("!child_1") + 22F,
                absoluteTopOf("child_2") + 23F
        )
        bottomsOf(rows) shouldEqual listOf(
                absoluteBottomOf("!child_0_0") - 38F,
                absoluteBottomOf("!child_0_1_0") - 36F,
                absoluteBottomOf("!child_0_1_1") - 34F,
                absoluteBottomOf("!child_0_1_2") - 33F,
                absoluteBottomOf("!child_0_1_3_0") - 31F,
                absoluteBottomOf("child_0_1_3") - 32F,
                absoluteBottomOf("child_0") - 39F,
                absoluteBottomOf("!child_1") - 28F,
                absoluteBottomOf("root") - 40F
        )
    }

    @Test
    fun `split solid object into single row`() {
        // given
        val obj = TestObject("", false, 100F, 10F, 20F, listOf(
                TestObject("", true, 100F, 10F, 20F, listOf(
                        TestObject("", false, 100F, 10F, 20F),
                        TestObject("", false, 100F, 10F, 20F)
                )),
                TestObject("", false, 100F, 10F, 20F)
        ))

        // when
        val rows = splitIntoRows(obj)

        // then
        objectsOf(rows) shouldEqual listOf(obj)
        topsOf(rows) shouldEqual listOf(10F)
        bottomsOf(rows) shouldEqual listOf(100F - 20F)
        topChildIndicesOf(rows) shouldEqual listOf(emptyList())
        bottomChildIndicesOf(rows) shouldEqual listOf(emptyList())
    }

    @Test
    fun `split single object into single row`() {
        // given
        val obj = TestObject("", true, 100F, 10F, 20F)

        // when
        val rows = splitIntoRows(obj)

        // then
        objectsOf(rows) shouldEqual listOf(obj)
        topsOf(rows) shouldEqual listOf(10F)
        bottomsOf(rows) shouldEqual listOf(100F - 20F)
        topChildIndicesOf(rows) shouldEqual listOf(emptyList())
        bottomChildIndicesOf(rows) shouldEqual listOf(emptyList())
    }

    inner class TestObject(
            val id: String,
            private val canPartiallyPaint: Boolean,
            height: Float,
            private val marginTop: Float,
            private val marginBottom: Float,
            private val childObjects: List<RenderObject> = emptyList()
    ) : RenderObject(0F, height, toChildren(marginTop, childObjects), range()) {
        override fun canPartiallyPaint() = canPartiallyPaint
        override fun internalMargins() = Margins(0F, 0F, marginTop, marginBottom)

        override fun equals(other: Any?) = (other as TestObject).id == id
        override fun hashCode() = id.hashCode()
        override fun toString() = id
    }

    fun toChildren(initialY: Float, childObjects: List<RenderObject>) = ArrayList<RenderChild>().apply {
        var y = initialY
        for (childObj in childObjects) {
            add(RenderChild(
                    0F, y, childObj
            ))
            y += childObj.height
        }
    }

    fun range() = BookRange(BookLocation(0.0), BookLocation(0.0))

    fun objectsOf(rows: List<RenderRow>) = rows.map { it.obj }
    fun topChildIndicesOf(rows: List<RenderRow>) = rows.map { it.top.childIndices }
    fun bottomChildIndicesOf(rows: List<RenderRow>) = rows.map { it.top.childIndices }

    fun topsOf(rows: List<RenderRow>) = rows.map { it.top.offset }
    fun bottomsOf(rows: List<RenderRow>) = rows.map { it.bottom.offset }

    fun absoluteTopOf(root: RenderObject, id: String) = absoluteOffsetOf(root, id, false)
    fun absoluteBottomOf(root: RenderObject, id: String) = absoluteOffsetOf(root, id, true)

    fun absoluteOffsetOf(root: RenderObject, id: String, isBottom: Boolean): Float {
        var result = -1F
        fun process(obj: RenderObject, absoluteTop: Float) {
            if (id == (obj as TestObject).id)
                result = absoluteTop + if (isBottom) obj.height else 0F

            for (child in obj.children) {
                process(child.obj, absoluteTop + child.y)
            }
        }
        process(root, 0F)
        return result
    }
}
