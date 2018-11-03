package com.dmi.perfectreader.book.pagination.page

import com.dmi.perfectreader.book.Locations
import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.TableOfContents
import com.dmi.perfectreader.book.content.common.PageConfig
import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.location.map
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.content.obj.common.ContentCompositeClass
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import com.dmi.perfectreader.book.layout.UniversalObjectLayouter
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.obj.*
import com.dmi.perfectreader.book.pagination.column.LayoutColumn
import com.dmi.util.collection.sumByFloat
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

// todo dynamic footer height with long chapter name. very long chapter long should be trimmed and add '...'
fun LocatedSequence<LayoutColumn>.pages(
        config: PageConfig,
        locations: Locations,
        tableOfContents: TableOfContents?,
        description: BookDescription,
        layouter: UniversalObjectLayouter,
        contentConfig: ContentConfig
): LocatedSequence<Page> {
    val cls = ContentCompositeClass(null, ContentClass.FOOTER)
    val range = LocationRange(Location(0.0), Location(0.0))
    fun paragraph(text: String) = ContentParagraph(listOf(ContentParagraph.Run.Text(text, cls, range)), cls, locale = null)
    fun ContentParagraph.layout(space: LayoutSpace) = layouter.layout(configure(contentConfig), space)

    /**
     * extract first line and trim width to fit it's content
     */
    fun LayoutObject.firstLineTrimmed(): LayoutObject {
        this as LayoutParagraph
        val line = children[0].obj as LayoutLine
        val width = line.children.sumByFloat { it.obj.width }
        return LayoutLine(width, line.height, line.blankVerticalMargins, line.children, line.range)
    }

    fun fullFooter(width: Float, height: Float, itemDistance: Float, left: ContentParagraph, right: ContentParagraph): LayoutObject {
        val layoutRight = right.layout(LayoutSpace.root(SizeF(width, height))).firstLineTrimmed()
        val layoutLeft = left.layout(LayoutSpace.root(SizeF(width - layoutRight.width - itemDistance, height))).firstLineTrimmed()

        return LayoutBox(width, height, listOf(
                LayoutChild(
                        0F,
                        height - layoutLeft.height,
                        layoutLeft
                ),
                LayoutChild(
                        width - layoutRight.width,
                        height - layoutRight.height,
                        layoutRight
                )
        ), range)
    }

    fun smallFooter(width: Float, height: Float, center: ContentParagraph): LayoutObject {
        val space = LayoutSpace.root(SizeF(width, height))
        val layoutCenter = center.layout(space).firstLineTrimmed()

        return LayoutBox(width, height, listOf(
                LayoutChild(
                        width / 2 - layoutCenter.width / 2,
                        height - layoutCenter.height,
                        layoutCenter
                )
        ), range)
    }

    fun footer(column: LayoutColumn): LayoutObject? = if (config.footer != null) {
        val location = column.range.start
        val pageNumber = locations.locationToPageNumber(location)
        val numberOfPages = locations.numberOfPages

        val layoutPageNumbers: ContentParagraph? = if (config.footer.pageNumber || config.footer.numberOfPages) {

            when {
                config.footer.pageNumber && config.footer.numberOfPages -> paragraph("$pageNumber / $numberOfPages")
                config.footer.pageNumber -> paragraph("$pageNumber")
                config.footer.numberOfPages -> paragraph("$numberOfPages")
                else -> null
            }
        } else {
            null
        }

        val layoutChapter: ContentParagraph? = if (config.footer.chapter) {
            val chapter = if (pageNumber == 0) {
                description.name
            } else {
                tableOfContents?.chapterAt(location)?.name ?: description.name
            }
            if (chapter != null) {
                paragraph(chapter)
            } else {
                null
            }
        } else {
            null
        }

        val width = config.contentSize.width
        val height = config.footer.height
        val itemDistance = config.footer.itemDistance

        when {
            layoutPageNumbers != null && layoutChapter != null -> fullFooter(width, height, itemDistance, layoutChapter, layoutPageNumbers)
            layoutPageNumbers != null -> smallFooter(width, height, layoutPageNumbers)
            layoutChapter != null -> smallFooter(width, height, layoutChapter)
            else -> null
        }
    } else {
        null
    }

    fun contentPosition() = PositionF(config.paddings.left, config.paddings.top)

    fun footerPosition() = if (config.footer != null) {
        PositionF(config.paddings.left, config.size.height - config.paddings.top - config.footer.height - config.footer.paddingBottom)
    } else {
        PositionF(config.paddings.left, config.size.height - config.paddings.top)
    }

    return map {
        Page(it, footer(it), config.size, contentPosition(), footerPosition(), config.textGammaCorrection)
    }
}