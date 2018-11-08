package com.dmi.perfectreader.book.parse.format.fb2

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.Content.SectionBuilder
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.ContentEmpty
import com.dmi.perfectreader.book.content.obj.ContentImage
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.parse.BookParser
import com.dmi.perfectreader.book.parse.CharsetDetector
import com.dmi.perfectreader.book.parse.format.fb2.entities.*
import com.dmi.perfectreader.book.parse.format.fb2.entities.Annotation
import com.dmi.util.io.ByteSource
import com.dmi.util.io.withoutUtfBom
import com.dmi.util.xml.Text
import com.dmi.util.xml.XMLDesc
import com.google.common.base.CharMatcher
import com.google.common.io.BaseEncoding
import com.google.common.io.ByteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.util.*

// todo support empty paragraphs (with text = "")
class FB2Parser(
        private val charsetDetector: CharsetDetector,
        private val source: ByteSource,
        private val fileName: String
) : BookParser {
    override suspend fun content(): Content = withContext(Dispatchers.IO) {
        // xml parser doesn't detect charset automatically
        val charset = charsetDetector.detectForXML(source)

        val fictionBook: FictionBook = source
                .openBufferedStream()
                .withoutUtfBom()
                .reader(charset)
                .use(::parseFictionBook)

        val description = description(fictionBook.description)

        val content = Content.Builder()
        val root = content.root(
                locale = fictionBook.description?.titleInfo?.lang?.let(::Locale)
        )
        for (body in fictionBook.bodies)
            root.section(body)

        content.build(description, ::imageResource)
    }

    override suspend fun description(): BookDescription = withContext(Dispatchers.IO) {
        val charset = charsetDetector.detectForXML(source)
        val description: Description? = source
                .openBufferedStream()
                .reader(charset)
                .use(::parseFictionBookDescription)
        description(description)
    }

    private fun description(xmlDescription: Description?): BookDescription {
        val titleInfo = xmlDescription?.titleInfo
        val author = titleInfo?.compositeAuthorName()
        val name = titleInfo?.bookTitle
        val cover = titleInfo?.coverpage?.image?.href?.let(::imageResource)
        return BookDescription(author, name, fileName, cover)
    }

    override suspend fun descriptionOnFail() = BookDescription(author = null, name = null, fileName = fileName, cover = null)

    private fun imageResource(src: String) = ByteSource {
        val id = src.removePrefix("#")

        fun fictionBookBinaries() = source
                .openBufferedStream()
                .reader()
                .use(::parseFictionBookBinaries)

        fun findBinary() = fictionBookBinaries().find { it.id == id }
        val binary = findBinary() ?: throw FileNotFoundException()

        BaseEncoding
                .base64()
                .decode(CharMatcher.whitespace().removeFrom(binary.data))
                .inputStream()
    }

    private fun Image.toContent(): ContentImage? {
        val href = href
        return if (href != null) {
            ContentImage(href, range())
        } else {
            null
        }
    }

    private fun SectionBuilder.emptyLine(emptyLine: EmptyLine) {
        frame {
            obj(ContentEmpty(emptyLine.range()))
        }
    }

    private fun SectionBuilder.table(table: Table) {
        paragraph("<There is a table, but they aren't supported now>", table.range())
    }

    private fun SectionBuilder.box(
            box: Box,
            cls: ContentClass? = null,
            addChild: (child: XMLDesc) -> Unit = {}
    ): Unit = customized(
            cls = cls,
            locale = box.lang?.let(::Locale)
    ) {
        for (child in box) {
            when (child) {
                is Image -> image(child)
                is Epigraph -> box(child, ContentClass.EPIGRAPH)
                is Annotation -> box(child, ContentClass.EPIGRAPH)
                is P -> lines(child)
                is Poem -> box(child)
                is Stanza -> stanza(child)
                is V -> lines(child, ContentClass.POEM_LINE)
                is Title -> title(child)
                is Subtitle -> lines(child, ContentClass.STRONG)
                is Cite -> box(child, ContentClass.EPIGRAPH)
                is TextAuthor -> lines(child, ContentClass.AUTHOR)
                is EmptyLine -> emptyLine(child)
                is Table -> table(child)
                else -> addChild(child)
            }
        }
    }

    private fun SectionBuilder.image(image: Image) {
        customized(cls = ContentClass.IMAGE_BLOCK) {
            frame {
                obj(image.toContent())
            }
        }
    }

    private fun SectionBuilder.title(child: Box) {
        customized(ContentClass.H_BLOCK(chapterLevel)) {
            frame {
                customized(ContentClass.H(chapterLevel)) {
                    box(child)
                }
            }
        }
    }

    private fun SectionBuilder.stanza(child: Box) {
        customized(ContentClass.POEM_STANZA) {
            frame {
                box(child)
            }
        }
    }

    private fun SectionBuilder.lines(
            lines: Lines,
            cls: ContentClass? = null
    ) {
        val locale = lines.lang?.let(::Locale)
        customized(cls = cls, locale = locale) {
            frame {
                paragraph {
                    inline(lines)
                }
            }
        }
    }

    private fun ContentParagraph.Builder.inline(
            inline: Inline,
            cls: ContentClass? = null,
            addChild: (child: XMLDesc) -> Unit = {}
    ): Unit = customized(cls = cls) {
        for (child in inline) {
            when (child) {
                is Strong -> inline(child, ContentClass.STRONG)
                is Emphasis -> inline(child, ContentClass.EMPHASIS)
                is Strikethrough -> inline(child)
                is Sub -> inline(child)
                is Sup -> inline(child)
                is Code -> inline(child, ContentClass.CODE_LINE)
                is Style -> inline(child)
                is A -> inline(child)
                is Image -> obj(child.toContent())
                is Text -> text(child)
                else -> addChild(child)
            }
        }
    }

    private fun ContentParagraph.Builder.text(text: Text) = text(text.data, text.range())

    private fun SectionBuilder.section(section: Section, addChapter: Boolean = true) {
        val title = section.title
        if (title != null) {
            val chapterName = title.plainText()
            if (chapterName.isNotEmpty() && addChapter) {
                chapter(chapterName, title.begin()) {
                    sectionContent(section)
                }
            }
        } else {
            sectionContent(section)
        }
    }

    private fun SectionBuilder.sectionContent(section: Section) = box(section) {
        when (it) {
            is Section -> section(it)
            else -> Unit
        }
    }

    private fun Title.plainText(): String {
        fun StringBuilder.appendText(text: Text) = append(text.data)

        fun StringBuilder.appendText(inline: Inline) {
            for (child in inline) {
                when (child) {
                    is Inline -> appendText(child)
                    is Text -> appendText(child)
                    else -> Unit
                }
            }
        }

        fun StringBuilder.appendText(box: Box) {
            var isFirst = true
            for (child in box) {
                when (child) {
                    is Box -> appendText(child)
                    is Lines -> {
                        if (!isFirst)
                            append('\n')
                        appendText(child)
                    }
                    else -> Unit
                }

                isFirst = false
            }
        }

        return StringBuilder()
                .apply { appendText(this@plainText) }
                .toString()
    }

    private fun XMLDesc.range() = LocationRange(begin(), end())
    private fun XMLDesc.begin() = Location(index.toDouble())
    private fun XMLDesc.end() = Location((index + 1).toDouble())
}