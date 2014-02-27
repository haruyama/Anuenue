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
package org.unigram.anuenue.client.solr;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.eclipse.jetty.server.Server;

import com.meterware.httpunit.HttpUnitOptions;

import org.unigram.anuenue.util.SimpleAnuenueInstanceFactory;
import org.unigram.anuenue.util.AnuenueTestUtil;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class of SolrClient.
 */
public final class TestSolrClient {

    /** location of anuenue-nodes.xml file for the testing. */
    private static final String NODE_CONF_FILE = "resources/anuenue-nodes-single.xml";

    /** <commit/>. */
    private static final String XML_COMMIT = "<commit/>";

    /**
     * Test post.
     * @throws IOException in case of I/O error
     */
    @Test
    public void testPost() throws IOException {
        HttpURLConnection mockUrlc = mock(HttpURLConnection.class);
        OutputStream out = new ByteArrayOutputStream();
        String returnedCode = "200";
        try {
            when(mockUrlc.getOutputStream()).thenReturn(out);
            when(mockUrlc.getInputStream()).thenReturn(
                    new ByteArrayInputStream(returnedCode.getBytes()));
        } catch (IOException e) {
            fail();
        }
        String dataString = "blogid\tauthor\tgenre\tcontent\n"
                + "39209\tTakahiko Ito\tHadoop\tI like Hadoop.\n"
                + "4783\tTaro Gihu\tHadoop\tI also like Hadoop.\n";
        Reader data = new StringReader(dataString);
        String results = SolrClient.post(data, mockUrlc);

        // inputs and return code should be copied into buffers
        assertEquals(out.toString(), dataString);
        assertEquals(results, returnedCode);
    }

    /**
     * Test updateTsvData().
     */
    @Test
    public void testUpdateTSVData() {

        Server server = null;
        String dataDir = "data-test-updater1";

        try {
            String host = "localhost";
            int port = 18983;

            // start Solr
            server = SimpleAnuenueInstanceFactory.createSolrInstance(port,
                    dataDir, NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));

            // post a TSV data
            SolrClient client = new SolrClient(host, port);
            client.updateTSVData("blogid\tauthor\tgenre\tcontent\n"
                    + "39209\tTakahiko Ito\tHadoop\tI like Hadoop.\n"
                    + "4783\tTaro Gihu\tHadoop\tI also like Hadoop.\n");

            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            client.updateXML(XML_COMMIT);
            assertSame(2, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));

            client.updateTSVData("blogid\tauthor\tgenre\tcontent\n"
                    + "1\tTakahiko Ito\tHad\"oop\tI like Hadoop.\n"
                    + "2\tTa\"ro Gi\"hu\t\"Hadoop\"\tI also like Hado\"op.\n");
            assertSame(2, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            client.updateXML(XML_COMMIT);
            assertSame(4, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));

        } catch (Exception e) {
            fail();
        } finally {
            AnuenueTestUtil.stopServerQuietly(server);
            assertTrue(FileUtils.deleteQuietly(new File(dataDir)));
        }
    }

    /**
     * Test updateTSVFile().
     */
    @Test
    public void testUpdateTSVFile() {

        Server server = null;
        String dataDir = "data-test-updater2";

        try {
            String host = "localhost";
            int port = 18983;
            // start Solr
            server = SimpleAnuenueInstanceFactory.createSolrInstance(port,
                    dataDir, NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));

            // post a TSV file
            SolrClient client = new SolrClient(host, port);
            client.updateTSVFile(new File(
                    "resources/example-docs/add-sample01.txt"));

            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            client.updateXML(XML_COMMIT);
            assertSame(3, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));

            // post another TSV file
            client.updateTSVFile(new File(
                    "resources/example-docs/add-sample02.txt"));
            assertSame(3, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            client.updateXML(XML_COMMIT);
            assertSame(6, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));

            // encapsulator
            client.updateTSVFile(new File(
                    "resources/example-encapsulator-docs/add-sample04.txt"));
            assertSame(6, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            client.updateXML(XML_COMMIT);
            assertSame(13, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));


        } catch (Exception e) {
            fail();
        } finally {
            AnuenueTestUtil.stopServerQuietly(server);
            assertTrue(FileUtils.deleteQuietly(new File(dataDir)));
        }
    }


    /**
     * Test ping().
     */
    @Test
    public void testPing() {

        Server server = null;
        String dataDir = "data-test-ping1";

        try {
            String host = "localhost";
            int port = 18983;
            // start Solr
            server = SimpleAnuenueInstanceFactory.createSolrInstance(port,
                    dataDir, NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);

            SolrClient client = new SolrClient(host, port);
            client.ping();

        } catch (Exception e) {
            fail();
        } finally {
            AnuenueTestUtil.stopServerQuietly(server);
            assertTrue(FileUtils.deleteQuietly(new File(dataDir)));
        }
    }
}
