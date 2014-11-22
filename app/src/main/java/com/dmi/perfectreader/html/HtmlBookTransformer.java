package com.dmi.perfectreader.html;

import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

public class HtmlBookTransformer {
    public void transform(InputStream is, OutputStream os) throws IOException {
        try {
            tryTransform(is, os);
        } catch (TransformerConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryTransform(InputStream is, OutputStream os) throws SAXException, IOException, TransformerConfigurationException {
        final TransformerFactory tf = TransformerFactory.newInstance();
        if(!tf.getFeature(SAXTransformerFactory.FEATURE)){
            throw new RuntimeException(
                    "Did not find a SAX-compatible TransformerFactory.");
        }
        SAXTransformerFactory stf = (SAXTransformerFactory)tf;
        final TransformerHandler th = stf.newTransformerHandler();
        th.setResult(new StreamResult(os));

        SAXParserImpl.newInstance(null).parse(
                is,
                new DefaultHandler() {
                    @Override
                    public void startDocument() throws SAXException {
                        th.startDocument();
                    }

                    @Override
                    public void endDocument() throws SAXException {
                        th.endDocument();
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        th.startElement(uri, localName, qName, attributes);
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName) throws SAXException {
                        th.endElement(uri, localName, qName);
                    }

                    @Override
                    public void characters(char[] ch, int start, int length) throws SAXException {
                        th.characters(ch, start, length);
                    }

                    @Override
                    public void startPrefixMapping(String prefix, String uri) throws SAXException {
                        th.startPrefixMapping(prefix, uri);
                    }

                    @Override
                    public void endPrefixMapping(String prefix) throws SAXException {
                        th.endPrefixMapping(prefix);
                    }

                    @Override
                    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
                        th.notationDecl(name, publicId, systemId);
                    }

                    @Override
                    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
                        th.unparsedEntityDecl(name, publicId, systemId, notationName);
                    }

                    @Override
                    public void setDocumentLocator(Locator locator) {
                        th.setDocumentLocator(locator);
                    }

                    @Override
                    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                        th.ignorableWhitespace(ch, start, length);
                    }

                    @Override
                    public void processingInstruction(String target, String data) throws SAXException {
                        th.processingInstruction(target, data);
                    }

                    @Override
                    public void skippedEntity(String name) throws SAXException {
                        th.skippedEntity(name);
                    }

                    @Override
                    public void warning(SAXParseException e) throws SAXException {
                        e.printStackTrace();
                    }

                    @Override
                    public void error(SAXParseException e) throws SAXException {
                        e.printStackTrace();
                    }

                    @Override
                    public void fatalError(SAXParseException e) throws SAXException {
                        e.printStackTrace();
                    }
                }
        );
    }
}
