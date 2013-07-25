/*
 * Copyright (c) The Anuenue Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unigram.anuenue.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;

/**
 * Test Utility Class.
 */
public final class AnuenueTestUtil {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(AnuenueTestUtil.class);

    /** localhost. */
    private static final String LOCALHOST = "localhost";

    /** "result" XML element. */
    private static final String ELEMENT_RESULT = "result";

    /**
     * Private constructor.
     */
    private AnuenueTestUtil() {
    }

    /**
     * Get 1st "result" Node.
     * @param url url
     * @return 1st "result" Node
     * @throws IOException in case of I/O error
     * @throws ParserConfigurationException in case of XML parse error
     * @throws SAXException in case of XML parse error
     */
    private static Node getResultNode(final URL url) throws IOException, ParserConfigurationException, SAXException {
        WebResponse webResponse = new WebConversation().getResponse(url.toString());
        InputStream bais = new ByteArrayInputStream(webResponse.getText().getBytes("UTF-8"));
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(bais);
        return document.getElementsByTagName(ELEMENT_RESULT).item(0);
    }

    /**
     * Get number of indexed document via solr.
     * @param host host name
     * @param port port number
     * @return number of indexed document
     * @throws IOException in case of I/O error
     * @throws ParserConfigurationException in case of XML parse error
     * @throws SAXException in case of XML parse error
     */
    public static int getNumberOfIndexedDocumentViaSolr(final String host, final int port) throws IOException, ParserConfigurationException, SAXException {
        Node resultNode = getResultNode(new URL("http", host, port, "/solr/select?q=*:*"));
        NamedNodeMap attributes = resultNode.getAttributes();
        Node response = attributes.getNamedItem("numFound");
        String number = response.getNodeValue();
        return Integer.parseInt(number);
    }

    /**
     * Get number of indexed document via solr(localhost).
     * @param port port number
     * @return number of indexed document
     * @throws IOException in case of I/O error
     * @throws ParserConfigurationException in case of XML parse error
     * @throws SAXException in case of XML parse error
     */
    public static int getNumberOfIndexedDocumentViaLocalhostSolr(final int port) throws IOException, ParserConfigurationException, SAXException {
        return getNumberOfIndexedDocumentViaSolr(LOCALHOST, port);
    }

    /**
     * Get contents which match query.
     * @param host host name
     * @param port port number
     * @param query query
     * @param targetField target field
     * @return query result
     * @throws IOException in case of I/O error
     * @throws ParserConfigurationException in case of XML parse error
     * @throws SAXException in case of XML parse error
     */
    public static Map<String, String> getMachtedContents(final String host,
            final int port, final String query, final String targetField)
            throws IOException, ParserConfigurationException, SAXException {
        Node resultNode = getResultNode(new URL("http", host, port, "/solr/select?" +  query));
        NodeList childNodes = resultNode.getChildNodes();

        // extract contents
        Map<String, String> contents = new HashMap<String, String>();
        // System.out.println("childNodes.length: " + childNodes.getLength());
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node doc = childNodes.item(i);
            NodeList fields = doc.getChildNodes();
            // System.out.println("fields.length: " + fields.getLength());
            String blogid = null;
            String target = null;
            for (int j = 0; j < fields.getLength(); ++j) {
                Node field = fields.item(j);
                NamedNodeMap attributes = field.getAttributes();
                if (attributes == null) {
                    continue;
                }
                // System.out.println("atttributes.length:" +
                // attributes.getLength());
                Node fieldNode = attributes.item(0);
                if (fieldNode == null) {
                    continue;
                }

                String fieldValue = fieldNode.getNodeValue();
                if (fieldValue != null && fieldValue.equals(targetField)) {
                    // System.out.println("fieldValue: " +
                    // field.getTextContent());
                    target = field.getTextContent();
                } else if (fieldValue != null && fieldValue.equals("blogid")) {
                    blogid = field.getTextContent();
                }
            }
            contents.put(blogid, target);
        }
        return contents;
    }

    /**
     * Get ranking.
     * @param host host name
     * @param port port number
     * @param query query
     * @return query result
     * @throws IOException in case of I/O error
     * @throws ParserConfigurationException in case of XML parse error
     * @throws SAXException in case of XML parse error
     */
    public static List<Integer> getRanking(final String host, final int port,
            final String query) throws IOException, ParserConfigurationException, SAXException {
        Node resultNode = getResultNode(new URL("http", host, port, "/solr/select?" + query));
        NodeList childNodes = resultNode.getChildNodes();

        // extract contents
        List<Integer> ranking = new ArrayList<Integer>();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node doc = childNodes.item(i);
            NodeList fields = doc.getChildNodes();
            String blogid = null;
            for (int j = 0; j < fields.getLength(); ++j) {
                Node field = fields.item(j);
                NamedNodeMap attributes = field.getAttributes();
                if (attributes == null) {
                    continue;
                }
                Node fieldNode = attributes.item(0);
                if (fieldNode == null) {
                    continue;
                }
                String fieldValue = fieldNode.getNodeValue();
                if ("blogid".equals(fieldValue)) {
                    blogid = field.getTextContent();
                }
            }
            ranking.add(Integer.parseInt(blogid));
        }
        return ranking;
    }


    /**
     * Stop server quietly.
     * @param server Server
     */
    public static void stopServerQuietly(final Server server) {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                LOG.info(e.toString());
            }
        }
    }
}
