package com.dmi.perfectreader.epub;

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

import javax.xml.transform.TransformerConfigurationException;

/**
 * ВНИМАНИЕ!!! После каждого изменения поведения этого класса необходимо изменить поле VERSION.
 * Если этого не сделать, то для кэшированных ресурсов, изменения не произойдут.
 *
 * Также нужно помнить, что при модификации этого класса нужно обеспечить,
 * чтобы он имел минимальное количество внешних зависимостей.
 * Иначе будет сложно проследить, изменилось ли поведение класса.
 */
public class EpubSegmentModifier {
    private static final int VERSION = 1;
    private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
    private static final String INIT_SCRIPT_INJECTION = "javabridge://initscript";

    private ThreadLocal<Boolean> initScriptUrlInjected = new ThreadLocal<>();

    public int version() {
        return VERSION;
    }

    public void modify(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            tryModify(inputStream, outputStream);
        } catch (TransformerConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryModify(InputStream is, OutputStream os) throws SAXException, IOException, TransformerConfigurationException {
        initScriptUrlInjected.set(false);
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
                xmlWriter.startElement(uri, localName, qName, atts);
                if ("body".equals(qName) && !initScriptUrlInjected.get()) {
                    processInitScriptInjection();
                    initScriptUrlInjected.set(true);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                xmlWriter.endElement(uri, localName, qName);
            }

            private void processInitScriptInjection() throws SAXException {
                processScriptInjection(INIT_SCRIPT_INJECTION);
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
                    xmlWriter.endElement("script");
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                xmlWriter.characters(ch, start, length);
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
        });
        xmlReader.parse(new InputSource(is));
    }
}
