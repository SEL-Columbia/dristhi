package org.ei.commcare.listener.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class Xml {
    private final String xmlContent;

    public Xml(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public Map<String, String> getValuesOfFieldsSpecifiedByPath(Map<String, String> fieldInResultingMapVersusPathToUse) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        XPathFactory factory = XPathFactory.newInstance();

        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

        for (Map.Entry<String, String> entry : fieldInResultingMapVersusPathToUse.entrySet()) {
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(entry.getValue());
            map.put(entry.getKey(), expr.evaluate(doc, XPathConstants.STRING).toString());
        }
        return map;
    }

}
