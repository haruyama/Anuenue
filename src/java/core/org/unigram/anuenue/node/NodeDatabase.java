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

import java.util.List;
import java.util.Set;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unigram.anuenue.exception.AnuenueException;


/**
 * Database Class which has all node information.
 */
public final class NodeDatabase {

    /** list of nodes. */
    private final List<Node> nodeList;

    /** map (name-> nodes). */
    private final Map<String, Node> nodeNameMap;

    /** map (hostName -> set of nodes). */
    private final Map<String, List<Node>> hostNameMap;

    /** masters. */
    private final List<Node> masterList;

    /** slaves. */
    private final List<Node> slaveList;

    /** slave groups. */
    private final Map<String, List<Node>> slaveGroupList;

    /** backups. */
    private final List<Node> backupList;

    /** logger. */
    private static final Log LOG = LogFactory.getLog(NodeDatabase.class);

    /**
     * Constructor.
     *
     * @param nodes list of nodes
     * @param nameMap nodeMap
     * @param hostMap hostMap
     * @param masters masters
     * @param backups backups
     * @param slaves slaves
     * @param slaveGroups slaveGroups
     * @throws AnuenueException in case of validation error.
     */
    public NodeDatabase(final List <Node> nodes, final Map<String, Node> nameMap, final Map<String, List<Node>> hostMap,
            final List<Node> masters, final List<Node> backups, final List<Node> slaves,
            final Map<String, List<Node>> slaveGroups) throws AnuenueException {
        nodeNameMap = nameMap;
        hostNameMap = hostMap;
        masterList = masters;
        backupList = backups;
        slaveList = slaves;
        nodeList  = nodes;
        slaveGroupList = slaveGroups;
        validate();
    }

    /**
     * Validate the input xml. - check if more than one master,
     * slave, merger should be assigned - check if refereed name exist - flush
     * info if there is no replicate for slaves and backups
     *
     * @throws AnuenueException in case of validation error.
     */
    private void validate() throws AnuenueException {

        if (masterList.isEmpty()) {
            throw new AnuenueException("no master node");
        } else if (slaveList.isEmpty()) {
            throw new AnuenueException("no slave node");
        }

        // check refereed names
        for (String name : nodeNameMap.keySet()) {
            Node node = nodeNameMap.get(name);
            if (node.getHostName() == null || node.getPortNumber() == 0) {
                throw new AnuenueException("Invalide node with name: " + name
                        + ". Maybe there is no node named " + name + ".");
            }
        }

        checkReplication(slaveList);
        checkReplication(backupList);

        // check if there are more than instances in one host
        for (String hostName : hostNameMap.keySet()) {
            List<Node> listOfNode = hostNameMap.get(hostName);
            if (listOfNode.size() > 1) {
                LOG.info("host: " + hostName
                        + " contains more than one Anuenue instances.");
            }
        }
    }

    /**
     * Check replication configuaration of slaves and backups.
     * @param nodes list of nodes.
     * @throws AnuenueException in case that replication node is not found
     */
    private void checkReplication(final List<Node> nodes)
            throws AnuenueException {
        for (Node n : nodes) {
            String replicationName = n.getReplicationMaster();
            if (replicationName == null) {
                LOG.info("node (slave or backup): " + n.getHostName() + ":"
                        + n.getPortNumber() + " does not have the replicate section");
            } else {
                if (!nodeNameMap.containsKey(replicationName)) {
                    throw new AnuenueException("no node named "
                            + replicationName);
                }
            }
        }
    }

    /**
     * Get list of nodes.
     * @return list of nodes
     */
    public List<Node> getNodeList() {
        return nodeList;
    }

    /**
     * Get list of masters.
     * @return list of masters
     */
    public List<Node> getMasterList() {
        return masterList;
    }

    /**
     * Get list of slaves.
     * @return list of slaves
     */
    public List<Node> getSlaveList() {
        return slaveList;
    }

    /**
     * Get list of backups.
     * @return list of backups
     */
    public List<Node> getBackups() {
        return backupList;
    }

    /**
     * Get set of names of slavegroups.
     * @return set of names of slavegroups.
     */
    public Set<String> getSlaveGroupNames() {
        return slaveGroupList.keySet();
    }

    /**
     * Get list of nodes which belong to the slavegroup whose name is given name.
     * @param groupName name of slave group
     * @return list of nodes which belong to the slavegroup whose name is given name
     */
    public List<Node> getSlavesInGroup(final String groupName) {
        return slaveGroupList.get(groupName);
    }

    /**
     * Get list of nodes by host name.
     * @param hostName host name
     * @return list of node
     */
    public List<Node> getNodesByHost(final String hostName) {
        return hostNameMap.get(hostName);
    }

    /**
     * Get node by host name and port number (for test).
     * @param host host name
     * @param port port number
     * @return Node
     */
    public Node getNodeByHostAndPort(final String host, final int port) {
        return getNodeByName(host + ":" + port);
    }

    /**
     * Get node by node name(host:port).
     * @param name node name
     * @return Node
     */
    public Node getNodeByName(final String name) {
        return nodeNameMap.get(name);
    }

    /**
     * Get hostNameMap (for jsp).
     * @return hostNameMap
     */
    public Map<String, List<Node>> getHostNameMap() {
        return hostNameMap;
    }
}
