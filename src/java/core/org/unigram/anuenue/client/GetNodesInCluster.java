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

import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.Node;
import org.unigram.anuenue.node.NodeDatabase;
import org.unigram.anuenue.node.NodeDatabaseBuilder;
import org.unigram.anuenue.util.SimpleCommandLineParser;

/**
 * Class which gets nodes in cluster.
 */
public final class GetNodesInCluster {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(GetNodesInCluster.class);

    /**
     * Private constructor.
     */
    private GetNodesInCluster() {
    }

    /**
     * main method.
     *
     * @param args arguments
     * @throws AnuenueException
     */
    public static void main(final String[] args) {
        SimpleCommandLineParser parser = new SimpleCommandLineParser();
        parser.addOption(AnuenueCommandConstants.OPTION_CONF, "conf", AnuenueCommandConstants.DEFAULT_CONFIGURATION_FILENAME);

        try {
            parser.parse(args);
        } catch (ParseException e) {
            LOG.fatal("parse error", e);
            System.exit(1);
        }

        NodeDatabase database = null;
        try {
            String nodeConfFile = parser.getValue(AnuenueCommandConstants.OPTION_CONF);
            database = new NodeDatabaseBuilder(nodeConfFile).build();
        } catch (AnuenueException e) {
            LOG.fatal("build database error", e);
            System.exit(1);
        }

        for (Node n : database.getNodeList()) {
            System.out.println(n.getHostName() + "_" + n.getPortNumber() + "_"
                    + GetInstanceProperties.detectRole(n) + " ");
        }
        System.exit(0);
    }

}
