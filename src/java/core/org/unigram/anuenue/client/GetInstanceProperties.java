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

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.Node;
import org.unigram.anuenue.node.NodeDatabase;
import org.unigram.anuenue.node.NodeDatabaseBuilder;
import org.unigram.anuenue.util.SimpleCommandLineParser;

/**
 * Class which gets properties of a instance.
 */
public final class GetInstanceProperties {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(GetInstanceProperties.class);

    /**
     * Instance Role.
     */
    public static enum INSTANCE_ROLE {
        /** standalone. */
        STANDALONE,
        /** index. */
        INDEX,
        /** replicate. */
        REPLICATE
    }

    /**
     * Private constructer.
     */
    private GetInstanceProperties() {
    }

    /**
     * Main method.
     * @param args
     *            arguments
     * @throws AnuenueException
     * @throws AnuenueException
     *             in case of command failure.
     */
    public static void main(final String[] args) throws AnuenueException {
        SimpleCommandLineParser parser = new SimpleCommandLineParser();
        parser.addOption(AnuenueCommandConstants.OPTION_HOST, "host", null);
        parser.addOption(AnuenueCommandConstants.OPTION_PORT, "port", "0");
        parser.addOption(AnuenueCommandConstants.OPTION_NAME, "name", null);
        parser.addOption(AnuenueCommandConstants.OPTION_CONF, "conf", AnuenueCommandConstants.DEFAULT_CONFIGURATION_FILENAME);

        try {
            parser.parse(args);
        } catch (ParseException e) {
            LOG.fatal("parse error");
            System.exit(1);
        }

        String host = parser.getValue(AnuenueCommandConstants.OPTION_HOST);
        int port = Integer.parseInt(parser.getValue(AnuenueCommandConstants.OPTION_PORT));
        String name = parser.getValue(AnuenueCommandConstants.OPTION_NAME);
        String nodeConfFile = parser.getValue(AnuenueCommandConstants.OPTION_CONF);

        if (host == null && name == null) {
            throw new AnuenueException(
                    "No target instance is not specified by: " + " host "
                            + host + "\tport " + port);
        }

        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder(nodeConfFile).build();
        } catch (AnuenueException e) {
            LOG.fatal(e.toString());
            System.exit(1);
        }

        if (host == null) {
            LOG.info("name of target instance: " + name);
        } else {
            if (port > 0) {
                LOG.info("target host: " + host + "\ntarget port: " + port);
            } else {
                LOG.info("target host: " + host + "\n");
            }
        }

        try {
            Node target = null;
            if (host == null) {
                target = resolveTargetNode(name, database);
            } else {
                target = resolveTargetNode(host, port, database);
            }
            printTargetNodeInformation(target, database);
        } catch (AnuenueException e) {
            LOG.fatal(e.toString());
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * Log target node Information.
     * @param target target node
     * @param database database of node
     * @throws AnuenueException if target is null
     */
    private static void printTargetNodeInformation(final Node target, final NodeDatabase database) throws AnuenueException {

        System.out.println("INSTANCE_TYPE=" + detectRole(target));
        String masterName = target.getReplicationMaster();
        if (masterName != null) {
            Node master = database.getNodeByName(masterName);
            System.out.println("REPLICATION_MASTER_NODE=" + master.getHostName());
            System.out.println("REPLICATION_MASTER_PORT=" + master.getPortNumber());
        }
    }

    /**
     * Resolve target node.
     * @param host
     *            host name
     * @param port
     *            port number
     * @param database database of node
     * @return target node
     * @throws AnuenueException in case of any error
     */
    private static Node resolveTargetNode(final String host, final int port,
            final NodeDatabase database)
            throws AnuenueException {

        Node target = null;

        List<Node> tmpHosts = database.getNodesByHost(host);
        if (tmpHosts == null) {
            throw new AnuenueException(
                    "There is no Anuenue instance in the specified host: "
                            + host);
        }
        if (tmpHosts.size() == 1) {
            Node node = tmpHosts.get(0);
            if (port == 0) {
                LOG.info("port is not specified.  Guessing . . ."
                        + " the port number of the host: " + host + " is "
                        + node.getPortNumber());
            } else if (node.getPortNumber() != port) {
                throw new AnuenueException("host: " + node.getHostName()
                        + " does not have spcified port " + port);
            }
            target = node;
        } else {
            LOG.info("host: " + host
                    + " has more than one Anuenue instance.");
            if (port == 0) {
                throw new AnuenueException(
                        "When there are more than one instance in one machine. "
                                + "You need to specify the port");
            }
            for (Node n : tmpHosts) {
                if (n.getPortNumber() == port) {
                    target = n;
                    break;
                }
            }
        }

        if (target == null) {
            throw new AnuenueException(
                    "There is no Anuenue instance in host: " + host
                            + " with port :" + port);
        }

        return target;
    }

    /**
     * Resolve target node.
     * @param name
     *            host alias name
     * @param database database of node
     * @return target node
     * @throws AnuenueException in case of any error
     */
    private static Node resolveTargetNode(final String name, final NodeDatabase database)
            throws AnuenueException {

        Node target = database.getNodeByName(name);

        if (target == null) {
            throw new AnuenueException(
                    "There is no Anuenue instance of name: " + name);
        }

        return target;
    }

    /**
     * detect Role of node.
     *
     * @param node
     *            Node
     * @return role
     */
    public static INSTANCE_ROLE detectRole(final Node node) {
        if (doIndex(node) && !doReplicate(node)) {
            return INSTANCE_ROLE.INDEX;
        } else if (!doIndex(node) && doReplicate(node)) {
            return INSTANCE_ROLE.REPLICATE;
        } else {
            return INSTANCE_ROLE.STANDALONE;
        }
    }

    /**
     * return whether node replicates data of aother node.
     *
     * @param node
     *            Node
     * @return if node replicates data of aother node, return true.
     */
    private static boolean doReplicate(final Node node) {
        return node.isSlave() || node.isBackup();
    }

    /**
     * return whether node indexes data.
     *
     * @param node
     *            Node
     * @return if node indexes data, return true
     */
    private static boolean doIndex(final Node node) {
        return node.isMaster();
    }

}
