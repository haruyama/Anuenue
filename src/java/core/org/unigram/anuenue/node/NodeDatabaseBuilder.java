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
package org.unigram.anuenue.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.exception.AnuenueException;

/**
 * Parser of Anuenue Node Configuration File(anuenue-nodes.xml).
 */
public final class NodeDatabaseBuilder {
    /** logger. */
    private static final Log LOG = LogFactory.getLog(NodeDatabaseBuilder.class);

    /**
     * list of nodes.
     */
    private transient List<Node> nodeList;

    /**
     * host map.
     */
    private final Map<String, List<Node>> hostMap = new HashMap<String, List<Node>>();

    /**
     * masters.
     */
    private final List<Node> masters = new ArrayList<Node>();

    /**
     * backups.
     */
    private final List<Node> backups = new ArrayList<Node>();

    /**
     * slaves.
     */
    private final List<Node> slaves = new ArrayList<Node>();

    /**
     * slave groups.
     */
    private final Map<String, List<Node>> slaveGroups = new HashMap<String, List<Node>>();

    /**
     * Configuration file parser.
     */
    private final NodeConfFileParser parser;

    /**
     * Main method.
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        try {
            new NodeDatabaseBuilder(args[0]).build();
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    /**
     * Constructor.
     * @param configFile configuration file
     * @throws AnuenueException in case of parse error
     */
    public NodeDatabaseBuilder(final String configFile) throws AnuenueException {

        try {
            parser = new NodeConfFileParser(configFile);
        } catch (Exception e) {
            LOG.error(e.toString());
            throw new AnuenueException(e);
        }
    }

    /**
     * Parse a file.
     * @return all node information
     * @throws AnuenueException in case of parse error
     */
    public NodeDatabase build() throws AnuenueException {

        parser.parse();

        nodeList = parser.getNodeList();

        buildRoleLists();
        buildSlaveGroups(slaves);
        buildHostMap();
        return new NodeDatabase(nodeList, parser.getNameMap(), hostMap, masters,
                backups, slaves, slaveGroups);

    }

    /**
     * Add node to role lists according to node's role.
     */
    private void buildRoleLists() {
        for (Node n : nodeList) {
            if (n.isMaster()) {
                masters.add(n);
            }
            if (n.isMerger()) {
                LOG.info("Role merger is obsoleted. :"  + n.getName());
            }
            if (n.isSlave()) {
                slaves.add(n);
            }
            if (n.isBackup()) {
                backups.add(n);
            }
        }
    }

    /**
     * Create slaveGroups.
     * @param slaveNodes list of slave nodes.
     */
    private void buildSlaveGroups(final List<Node> slaveNodes) {
        for (Node slaveNode : slaveNodes) {
            String groupName = slaveNode.getGroupName();
            if (!slaveGroups.containsKey(groupName)) {
                slaveGroups.put(groupName, new ArrayList<Node>());
            }
            slaveGroups.get(groupName).add(slaveNode);
        }
    }

    /**
     * Build hostMap.
     */
    private void buildHostMap() {
        for (Node n : nodeList) {
            String host = n.getHostName();
            if (!hostMap.containsKey(host)) {
                hostMap.put(host, new ArrayList<Node>());
            }
            hostMap.get(host).add(n);
        }
    }
}
