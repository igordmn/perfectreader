package com.dmi.perfectreader.book.html;

import com.dmi.perfectreader.book.config.BookConfig;
import com.dmi.perfectreader.error.BookInvalidException;
import com.google.common.base.CharMatcher;

import org.ccil.cowan.tagsoup.AttributesImpl;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.transform.TransformerConfigurationException;

public class HtmlSegmentModifier {
    private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
    private static final String CONFIG_SCRIPT_INJECTION = "javabridge://configscript";
    private static final String INIT_SCRIPT_INJECTION = "javabridge://initscript";
    private static final String MAIN_DIV_ID = "__mainDiv";
    private static final Set<String> DELETE_TRAILING_SPACES_ELEMENTS = new HashSet<>(Arrays.asList(
            // block elements
            "p", "td", "div", "h1", "h2", "h3", "h4", "h5", "h6", "dt", "dd",
            // inline elements
            "br", "li"
    ));

    public String version(BookConfig bookConfig) {
        return String.valueOf(Version.VERSION) + String.valueOf(bookConfig.deleteTrailingSpaces);
    }

    public void modify(InputStream inputStream, OutputStream outputStream, BookConfig bookConfig) throws IOException {
        try {
            tryModify(inputStream, outputStream, bookConfig);
        } catch (TransformerConfigurationException | SAXException e) {
            throw new BookInvalidException(e);
        }
    }

    private void tryModify(InputStream is, OutputStream os, final BookConfig bookConfig) throws SAXException, IOException, TransformerConfigurationException {
        final XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(os));
        xmlWriter.setOutputProperty(XMLWriter.METHOD, "html");
        xmlWriter.setOutputProperty(XMLWriter.ENCODING, "utf-8");
        xmlWriter.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
        xmlWriter.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                exception.printStackTrace();
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                exception.printStackTrace();
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });

        final XMLReader xmlReader = new Parser();
        xmlReader.setContentHandler(new ContentHandler() {
            private ThreadLocal<Boolean> configScriptUrlInjected = new ThreadLocal<>();
            private ThreadLocal<Boolean> initScriptUrlInjected = new ThreadLocal<>();
            private Stack<String> currentElements = new Stack<>();
            private StringBuilder currentText = new StringBuilder();

            {
                configScriptUrlInjected.set(false);
                initScriptUrlInjected.set(false);
            }

            @Override
            public void setDocumentLocator(Locator locator) {
                xmlWriter.setDocumentLocator(locator);
            }

            @Override
            public void startDocument() throws SAXException {
                xmlWriter.startDocument();
            }

            @Override
            public void endDocument() throws SAXException {
                xmlWriter.endDocument();
            }

            @Override
            public void startPrefixMapping(String prefix, String uri) throws SAXException {
                xmlWriter.startPrefixMapping(prefix, uri);
            }

            @Override
            public void endPrefixMapping(String prefix) throws SAXException {
                xmlWriter.endPrefixMapping(prefix);
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                currentElements.add(localName);
                writeCurrentText();
                xmlWriter.startElement(uri, localName, qName, atts);
                if ("body".equals(qName)) {
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "id", "", "CDATA", MAIN_DIV_ID);
                    xmlWriter.startElement(XHTML_NAMESPACE, "div", "", attributes);
                }
                if ("body".equals(qName) && !configScriptUrlInjected.get()) {
                    processScriptInjection(CONFIG_SCRIPT_INJECTION);
                    configScriptUrlInjected.set(true);
                }
                if ("body".equals(qName) && !initScriptUrlInjected.get()) {
                    processScriptInjection(INIT_SCRIPT_INJECTION);
                    initScriptUrlInjected.set(true);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                writeCurrentText();
                if ("body".equals(qName)) {
                    xmlWriter.endElement(XHTML_NAMESPACE, "div");
                }
                xmlWriter.endElement(uri, localName, qName);
                currentElements.pop();
            }

            private void processScriptInjection(String scriptUrl) throws SAXException {
                if (scriptUrl != null) {
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "type", "", "CDATA", "text/javascript");
                    xmlWriter.startElement(XHTML_NAMESPACE, "script", "", attributes);
                    // такой сложный код нужен для того, чтобы добавить randomId при загрузке скриптов
                    // это нужно, чтобы WebView не кэшировал скрипты
                    xmlWriter.characters("document.write('" +
                                         "  \\x3Cscript type=\"text/javascript\" src=\"" +
                                         scriptUrl + "?randomId='+" + "new Date().getTime().toString() + Math.random()" + "+'" +
                                         "  \"\\x3E\\x3C/script\\x3E" +
                                         "');");
                    xmlWriter.endElement(XHTML_NAMESPACE, "script");
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                currentText.append(ch, start, length);
            }

            private void writeCurrentText() throws SAXException {
                if (currentText.length() > 0) {
                    CharSequence modifiedText = modifyText(currentText);
                    char[] chars = new char[modifiedText.length()];
                    for (int i = 0; i < modifiedText.length(); i++) {
                        chars[i] = modifiedText.charAt(i);
                    }
                    xmlWriter.characters(chars, 0, chars.length);
                    currentText.setLength(0);
                }
            }

            private CharSequence modifyText(CharSequence textChars) {
                if (bookConfig.deleteTrailingSpaces && DELETE_TRAILING_SPACES_ELEMENTS.contains(currentElement())) {
                    return CharMatcher.WHITESPACE.trimLeadingFrom(textChars);
                } else {
                    return textChars;
                }
            }

            @Override
            public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                xmlWriter.ignorableWhitespace(ch, start, length);
            }

            @Override
            public void processingInstruction(String target, String data) throws SAXException {
                xmlWriter.processingInstruction(target, data);
            }

            @Override
            public void skippedEntity(String name) throws SAXException {
                xmlWriter.skippedEntity(name);
            }

            private String currentElement() {
                return currentElements.size() > 0 ? currentElements.peek() : null;
            }
        });
        xmlReader.parse(new InputSource(is));
    }
}
