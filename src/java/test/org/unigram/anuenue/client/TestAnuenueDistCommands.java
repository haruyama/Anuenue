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
package org.unigram.anuenue.client;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.eclipse.jetty.server.Server;
import org.unigram.anuenue.client.distcommand.AbstractDistCommand;
import org.unigram.anuenue.client.distcommand.AnuenueDistCommandFactory;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.NodeDatabase;
import org.unigram.anuenue.node.NodeDatabaseBuilder;
import org.unigram.anuenue.util.AnuenueTestUtil;
import org.unigram.anuenue.util.SimpleAnuenueInstanceFactory;

import com.meterware.httpunit.HttpUnitOptions;

/**
 * Test of Anuenue distribution commands.
 */
public final class TestAnuenueDistCommands {

    /** location of anuenue-nodes.xml file for the testing. */
    private static final String NODE_CONF_FILE = "resources/anuenue-nodes-disttest.xml";

    /**
     * Test of anuenue distribution commands.
     */
    @Test
    public void testRun() {

        Server server1 = null;
        Server server2 = null;
        String dataDir1 = "data-test-dist1";
        String dataDir2 = "data-test-dist2";

        try {

            /** role: merger, master, slave */
            int port1 = 18070;

            server1 = SimpleAnuenueInstanceFactory
                    .createSolrInstance(port1, dataDir1,
                            NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame("initial size(1)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));

            /** role: merger, master, slave */
            int port2 = 17070;

            server2 = SimpleAnuenueInstanceFactory
                    .createSolrInstance(port2, dataDir2,
                            NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame("initial size(2)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));

            NodeDatabase database = new NodeDatabaseBuilder("resources/anuenue-nodes-disttest.xml").build();

            AbstractDistCommand command = AnuenueDistCommandFactory.createDistCommand("addDir", "resources/example-docs", database);
            command.setMaxLinePerFile(2);
            command.execute();

            assertSame("addDir, before commit(1)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("addDir, before commit(2)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));

            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            assertTrue("addDir and commit(1)", AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1) > 0);
            assertTrue("addDir and commit(2)", AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2) > 0);

            AnuenueDistCommandFactory.createDistCommand("delete", "*:*", database).execute();
            assertTrue("delete, before commit(1)", AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1) > 0);
            assertTrue("delete, before commit(2)", AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2) > 0);

            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            assertSame("delete and commit(1)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("delete and commit(2)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));

            // tests for update
            AnuenueDistCommandFactory.createDistCommand("addDir", "resources/example-docs", database).execute();
            assertSame("addDir, before commit(1)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("addDir, before commit(2)", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));

            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            Map<String, String> results1 = AnuenueTestUtil.getMachtedContents("localhost", port1,
                    "q=content:Hadoop&shards=localhost:" + port1 + "/solr,localhost:" + port2 + "/solr", "content");
            assertSame("addDir and commit", 2, results1.size());
            assertEquals("blogid:39209", "hadoop \u3063\u3066\u3044\u3044\u3088\u306d\u3002", results1.get("39209"));

            // update by another directory
            AnuenueDistCommandFactory.createDistCommand("updateDir", "resources/update-docs", database).execute();
            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            Map<String, String> results2 = AnuenueTestUtil.getMachtedContents("localhost", port2,
                    "q=content:Hadoop&shards=localhost:" + port1 + "/solr,localhost:" + port2 + "/solr", "content");
            assertSame("updateDir and commit", 2, results2.size());
            assertEquals("blogid:39209", "hadoop \u306f\u826f\u304b\u3063\u305f\u3002", results2.get("39209"));

            // query with sort
            List<Integer> ranking = AnuenueTestUtil.getRanking("localhost", port1,
                    "q=text:Hadoop&sort=blogid+asc&shards=localhost:" + port1 + "/solr,localhost:" + port2 + "/solr");
            assertSame("size of sorted result", 3, ranking.size());
            assertEquals("blog id of first ranking(asc)", Integer.valueOf(23493), ranking.get(0));
            List<Integer> ranking2 = AnuenueTestUtil.getRanking("localhost", port2,
                    "q=text:Hadoop&sort=blogid+desc&sort=blogid+asc&shards=localhost:" + port1 + "/solr,localhost:" + port2 + "/solr");
            assertEquals("blog id of first ranking(desc)", Integer.valueOf(4830849), ranking2.get(0));

            // "AND" query
            Map<String, String> results3 = AnuenueTestUtil.getMachtedContents("localhost", port1,
                    "q=hadoop+AND+blogid:39209&shards=localhost:" + port1 + "/solr,localhost:" + port2 + "/solr", null);
            assertSame("AND query", 1, results3.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            // stop instances
            AnuenueTestUtil.stopServerQuietly(server1);
            AnuenueTestUtil.stopServerQuietly(server2);

            // remove directories
            assertTrue(FileUtils.deleteQuietly(new File(dataDir1)));
            assertTrue(FileUtils.deleteQuietly(new File(dataDir2)));
        }
    }

    /**
     * Test of adding file including invalid line.
     */
    @Test
    public void testInvalidInput() {

        Server server = null;
        String dataDir = "data-test-invalid-input";

        try {
            int port = 18983;

            server = SimpleAnuenueInstanceFactory.createSolrInstance(port, dataDir, NODE_CONF_FILE);

            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame("initial size", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port));

            NodeDatabase database = new NodeDatabaseBuilder("resources/anuenue-nodes-single.xml").build();

            AnuenueDistCommandFactory.createDistCommand("addDir", "resources/example-failure-docs", database).execute();
            assertSame("addDir, before commit", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port));
            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            assertSame("addDir and commit", 2, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port));

        } catch (Exception e) {
            fail();
        } finally {
            AnuenueTestUtil.stopServerQuietly(server);
            assertTrue(FileUtils.deleteQuietly(new File(dataDir)));
        }
    }

    /**
     * Test of CommandUncaughtExceptionHandler.
     *
     *  @throws AnuenueException if error occured.
     */
    @Test(expected = AnuenueException.class)
    public void testExceptionHandling() throws AnuenueException {
        AnuenueDistCommandFactory.createDistCommand("addDir", "resources/example-docs", new NodeDatabaseBuilder("resources/anuenue-nodes-single.xml").build()).execute();
        fail();
    }

    /**
     * Test of Anuenue distribution command indicating target node.
     */
    @Test
    public void testRunWithTargets() {

        Server server1 = null;
        Server server2 = null;
        String dataDir1 = "data-test-dist-targets1";
        String dataDir2 = "data-test-dist-targets2";

        try {

            /** role: merger, master, slave */
            int port1 = 18070;
            server1 = SimpleAnuenueInstanceFactory.createSolrInstance(port1, dataDir1, NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame("initial size", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));

            /** role: merger, master, slave */
            int port2 = 17070;
            server2 = SimpleAnuenueInstanceFactory.createSolrInstance(port2, dataDir2, NODE_CONF_FILE);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            assertSame("initial size", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));

            NodeDatabase database = new NodeDatabaseBuilder("resources/anuenue-nodes-disttest.xml").build();

            AbstractDistCommand command = AnuenueDistCommandFactory.createDistCommand("node1", "addDir", "resources/example-docs", database);
            command.setMaxLinePerFile(2);
            command.execute();
            assertSame("addDir, before commit", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("addDir, before commit. nothing changed", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));
            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            assertSame("addDir and commit", 9, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("addDir and commit. nothing changed", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));
            AnuenueDistCommandFactory.createDistCommand("delete", "*:*", database).execute();
            assertSame("delete, before commit", 9, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("delete, before commit. nothing changed", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));
            AnuenueDistCommandFactory.createDistCommand("commit", "", database).execute();
            assertSame("delete and commit", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port1));
            assertSame("delete and commit. nothing changed", 0, AnuenueTestUtil.getNumberOfIndexedDocumentViaLocalhostSolr(port2));
        } catch (Exception e) {
            fail();
        } finally {
            AnuenueTestUtil.stopServerQuietly(server1);
            AnuenueTestUtil.stopServerQuietly(server2);

            assertTrue(FileUtils.deleteQuietly(new File(dataDir1)));
            assertTrue(FileUtils.deleteQuietly(new File(dataDir2)));
        }
    }

}
