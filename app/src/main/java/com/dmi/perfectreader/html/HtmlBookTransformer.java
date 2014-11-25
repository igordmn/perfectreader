package com.dmi.perfectreader.html;

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

public class HtmlBookTransformer {
    private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

    private String styleInjection;
    private String scriptInjection;

    private ThreadLocal<Boolean> styleInjected = new ThreadLocal<>();
    private ThreadLocal<Boolean> scriptInjected = new ThreadLocal<>();

    public void setStyleInjection(String styleInjection) {
        this.styleInjection = styleInjection;
    }

    public void setScriptInjection(String scriptInjection) {
        this.scriptInjection = scriptInjection;
    }

    public void transform(InputStream is, OutputStream os) throws IOException {
        try {
            tryTransform(is, os);
        } catch (TransformerConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryTransform(InputStream is, OutputStream os) throws SAXException, IOException, TransformerConfigurationException {
        styleInjected.set(false);
        scriptInjected.set(false);
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
                if ("body".equals(qName) && !styleInjected.get()) {
                    xmlWriter.startElement(XHTML_NAMESPACE, "head");
                    processStyleInjection();
                    xmlWriter.endElement(XHTML_NAMESPACE, "head");
                    styleInjected.set(true);
                }
                xmlWriter.startElement(uri, localName, qName, atts);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                xmlWriter.endElement(uri, localName, qName);
                if ("head".equals(qName) && !styleInjected.get()) {
                    processStyleInjection();
                    styleInjected.set(true);
                }
                if ("html".equals(qName) && !scriptInjected.get()) {
                    processScriptInjection();
                    scriptInjected.set(true);
                }
            }

            private void processStyleInjection() throws SAXException {
                if (styleInjection != null) {
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "type", "", "CDATA", "text/css");
                    xmlWriter.startElement(XHTML_NAMESPACE, "style", "", attributes);
                    xmlWriter.characters(styleInjection);
                    xmlWriter.endElement("style");
                }
            }

            private void processScriptInjection() throws SAXException {
                if (scriptInjection != null) {
                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "type", "", "CDATA", "text/javascript");
                    xmlWriter.startElement(XHTML_NAMESPACE, "script", "", attributes);
                    xmlWriter.characters(scriptInjection);
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
