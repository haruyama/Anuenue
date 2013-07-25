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

import java.util.HashSet;
import java.util.Set;

/**
 * Anuenue Node.
 */
public final class Node {

    /** host name. */
    private final String hostName;

    /** port number. */
    private int portNumber;

    /** assigned roles (master, slave, merger, backup). */
    private final Set<Role> assignedRoles;

    /** name of node. */
    private final String name;

    /** (optional) node whose index is replicated by this node. */
    private final String replicationMaster;

    /** (optional) group name. */
    private final String nameOfNodeGroup;

    /** Default Group Name. */
    public static final String DEFAULT_GROUP_NAME = "DEFAULT";

    /**
     * Convert to String.
     * @return String class.
     */
    @Override
    public String toString() {
        return "Node [host=" + hostName + ", port=" + portNumber + ", roles=" + assignedRoles
                + ", name=" + name + ", replicationNode=" + replicationMaster
                + ", groupName=" + nameOfNodeGroup + "]";
    }

    /**
     * Calcurate hash code.
     * @return hash code.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((nameOfNodeGroup == null) ? 0 : nameOfNodeGroup.hashCode());
        result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + portNumber;
        result = prime * result
                + ((replicationMaster == null) ? 0 : replicationMaster.hashCode());
        result = prime * result + ((assignedRoles == null) ? 0 : assignedRoles.hashCode());
        return result;
    }

    /**
     * Check this object equals other object.
     * @param obj other object
     * @return true if this object equals other object
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        if (nameOfNodeGroup == null) {
            if (other.nameOfNodeGroup != null) {
                return false;
            }
        } else if (!nameOfNodeGroup.equals(other.nameOfNodeGroup)) {
            return false;
        }
        if (hostName == null) {
            if (other.hostName != null) {
                return false;
            }
        } else if (!hostName.equals(other.hostName)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (portNumber != other.portNumber) {
            return false;
        }
        if (replicationMaster == null) {
            if (other.replicationMaster != null) {
                return false;
            }
        } else if (!replicationMaster.equals(other.replicationMaster)) {
            return false;
        }
        if (assignedRoles == null) {
            if (other.assignedRoles != null) {
                return false;
            }
        } else if (!assignedRoles.equals(other.assignedRoles)) {
            return false;
        }
        return true;
    }

    /**
     * Constructor.
     * @param host host name
     * @param port port number
     * @param role Node role
     * @param replication replication master
     */
    public Node(final String host, final int port, final Role role,
            final String replication) {
        this(host, port, new HashSet<Role>(), replication, DEFAULT_GROUP_NAME);
        assignedRoles.add(role);
    }

    /**
     * Constructor.
     * @param host host name
     * @param port port number
     * @param roles Node roles
     * @param replication replication master
     * @param groupName group name
     */
    public Node(final String host, final int port, final Set<Role> roles,
            final String replication, final String groupName) {
        hostName = host;
        portNumber = port;
        assignedRoles = roles;
        replicationMaster = replication;
        name = host + ":" + port;
        nameOfNodeGroup = groupName;
    }

    /**
     * Node role.
     */
    public static enum Role {
        /** merger. */
        MERGER,
        /** master. */
        MASTER,
        /** slave. */
        SLAVE,
        /** backup. */
        BACKUP
    }

    /**
     * Is merger.
     * @return true if this node is merger
     */
    public boolean isMerger() {
        return assignedRoles.contains(Role.MERGER);
    }

    /**
     * Is master.
     * @return true if this node is master
     */
    public boolean isMaster() {
        return assignedRoles.contains(Role.MASTER);
    }

    /**
     * Is slave.
     * @return true if this node is slave.
     */
    public boolean isSlave() {
        return assignedRoles.contains(Role.SLAVE);
    }

    /**
     * Is backup.
     * @return true if this node is backup.
     */
    public boolean isBackup() {
        return assignedRoles.contains(Role.BACKUP);
    }

    /**
     * Return name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Return group name.
     * @return group name.
     */
    public String getGroupName() {
        return nameOfNodeGroup;
    }

    /**
     * Return host name..
     * @return host name.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Return port number.
     * @return port number
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * Return replication master.
     * @return replication master.
     */
    public String getReplicationMaster() {
        return replicationMaster;
    }

    /**
     * Check roles of node (for test).
     * @param expectedRoles expected roles.
     * @return true if roles of this node equals expectedRoles
     */
    public boolean checkRoles(final Role[] expectedRoles) {
        if (assignedRoles.size() != expectedRoles.length) {
            return false;
        }
        for (int i = 0; i < expectedRoles.length; i++) {
            if (!assignedRoles.contains(expectedRoles[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert to String for Solr "shards" parameter.
     * @return String for Solr "shards" parameter
     */
    public String toShardString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(hostName);
        buffer.append(':');
        buffer.append(portNumber);
        buffer.append("/solr");
        return buffer.toString();
    }

    /**
     * get assignedRoles (for jsp).
     * @return assignedRoles
     */
    public Set<Role> getAssignedRoles() {
        return assignedRoles;
    }
}
