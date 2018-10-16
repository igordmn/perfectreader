package com.kursx.parser.fb2;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @see com.kursx.parser.fb2.FictionBook
 */
public class FictionBookExt {
    protected Xmlns[] xmlns;
    protected Description description;
    protected List<Body> bodies = new ArrayList();
    protected Map<String, Binary> binaries = new HashMap();

    public FictionBookExt(InputStream stream) throws ParserConfigurationException, IOException, SAXException, OutOfMemoryError {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new InputStreamReader(stream)));
        this.initXmlns(doc);
        this.description = new Description(doc);
        NodeList bodyNodes = doc.getElementsByTagName("body");

        for(int item = 0; item < bodyNodes.getLength(); ++item) {
            this.bodies.add(new Body(bodyNodes.item(item)));
        }

        NodeList binary = doc.getElementsByTagName("binary");

        for(int item = 0; item < binary.getLength(); ++item) {
            Binary binary1 = new Binary(binary.item(item));
            this.binaries.put(binary1.getId().replace("#", ""), binary1);
        }
    }

    protected void setXmlns(List<Node> nodeList) {
        this.xmlns = new Xmlns[nodeList.size()];

        for(int index = 0; index < nodeList.size(); ++index) {
            Node node = (Node)nodeList.get(index);
            this.xmlns[index] = new Xmlns(node);
        }

    }

    protected void initXmlns(Document doc) {
        NodeList fictionBook = doc.getElementsByTagName("FictionBook");
        List<Node> xmlns = new ArrayList();

        for(int item = 0; item < fictionBook.getLength(); ++item) {
            NamedNodeMap map = fictionBook.item(item).getAttributes();

            for(int index = 0; index < map.getLength(); ++index) {
                Node node = map.item(index);
                xmlns.add(node);
            }
        }

        this.setXmlns(xmlns);
    }

    public List<Author> getAuthors() {
        return this.description.getDocumentInfo().getAuthors();
    }

    public Xmlns[] getXmlns() {
        return this.xmlns;
    }

    public Description getDescription() {
        return this.description;
    }

    @Nullable
    public Body getBody() {
        return this.getBody((String)null);
    }

    @Nullable
    public Body getNotes() {
        return this.getBody("notes");
    }

    @Nullable
    public Body getComments() {
        return this.getBody("comments");
    }

    @Nullable
    private Body getBody(String name) {
        Iterator var2 = this.bodies.iterator();

        Body body;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            body = (Body)var2.next();
        } while(!(name + "").equals(body.getName() + ""));

        return body;
    }

    @NotNull
    public Map<String, Binary> getBinaries() {
        return this.binaries;
    }

    public String getTitle() {
        return this.description.getTitleInfo().getBookTitle();
    }

    public String getLang() {
        return this.description.getTitleInfo().getLang();
    }

    @Nullable
    public Annotation getAnnotation() {
        return this.description.getTitleInfo().getAnnotation();
    }
}
