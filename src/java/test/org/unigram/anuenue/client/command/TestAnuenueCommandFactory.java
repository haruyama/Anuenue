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
package org.unigram.anuenue.client.command;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.eclipse.jetty.server.Server;
import org.unigram.anuenue.util.AnuenueTestUtil;
import org.unigram.anuenue.util.SimpleAnuenueInstanceFactory;

import com.meterware.httpunit.HttpUnitOptions;

/**
 * Test class of AnuenueCommandFactory.
 */
public final class TestAnuenueCommandFactory {

    /**
     * Test of createCommand().
     */
    @Test
    public void testCreateCommand() {

        /** location of anuenue-nodes.xml file for the testing. */
        String clusterConfFile = "resources/anuenue-nodes-single.xml";

        String dataDir = "data-test-command";
        String host = "localhost";
        int port = 18983;

        Server server = null;

        try {

            // start Solr
            server = SimpleAnuenueInstanceFactory.createSolrInstance(port,
                    dataDir, clusterConfFile);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            // create AnuenueCommands
            // run commands (post file, commit, optimize)
            AnuenueCommandFactory.createCommand(host, port, "addFile",
                    "resources/example-docs/add-sample01.txt").runCommand();
            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "commit", null)
                    .runCommand();
            assertSame(3, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "addFile",
                    "resources/example-docs/add-sample02.txt").runCommand();
            assertSame(3, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "softcommit", null)
                    .runCommand();
            assertSame(6, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "delete",
                    "blogid:39209").runCommand();
            assertSame(6, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "commit", null)
                    .runCommand();
            assertSame(5, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "deleteAll", null)
                    .runCommand();
            assertSame(5, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "commit", null)
                    .runCommand();
            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "ping", null)
                    .runCommand();

        } catch (Exception e) {
            fail();
        } finally {
            // stop Solr
            AnuenueTestUtil.stopServerQuietly(server);
            assertTrue(FileUtils.deleteQuietly(new File(dataDir)));
        }

    }

    /**
     * Test of createCommand() part2.
     */
    @Test
    public void testCreateCommand2() {

        /** location of anuenue-nodes.xml file for the testing. */
        String clusterConfFile = "resources/anuenue-nodes-single.xml";

        String dataDir = "data-test-command";
        String host = "localhost";
        int port = 18983;

        Server server = null;

        try {

            // start Solr
            server = SimpleAnuenueInstanceFactory.createSolrInstance(port,
                    dataDir, clusterConfFile);
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            // create AnuenueCommands
            // run commands (post file, commit, optimize)
            AnuenueCommandFactory.createCommand(host, port, "addData",
                    FileUtils.readFileToString(new File("resources/example-docs/add-sample01.txt"))).runCommand();
            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "commit", null)
                    .runCommand();
            assertSame(3, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "addData",
                    FileUtils.readFileToString(new File("resources/example-docs/add-sample02.txt"))).runCommand();
            assertSame(3, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "softcommit", null)
                    .runCommand();
            assertSame(6, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "delete",
                    "blogid:39209").runCommand();
            assertSame(6, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "commit", null)
                    .runCommand();
            assertSame(5, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "deleteAll", null)
                    .runCommand();
            assertSame(5, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "commit", null)
                    .runCommand();
            assertSame(0, AnuenueTestUtil.getNumberOfIndexedDocumentViaSolr(host, port));
            AnuenueCommandFactory.createCommand(host, port, "ping", null)
                    .runCommand();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            // stop Solr
            AnuenueTestUtil.stopServerQuietly(server);
            assertTrue(FileUtils.deleteQuietly(new File(dataDir)));
        }

    }
}
