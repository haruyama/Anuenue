package org.unigram.anuenue.node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.DocumentBuilderFactory;

import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.Node.Role;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parser of Node Configuration file(anuenue-nodes.xml).
 */
public final class NodeConfFileParser {

    /** logger. */
    private static final Log LOG = LogFactory.getLog(NodeConfFileParser.class);

    /** "master" element name. */
    private static final String ELEMENT_NAME_MASTER = "master";

    /** "merger" element name. */
    private static final String ELEMENT_NAME_MERGER = "merger";

    /** "backup" element name. */
    private static final String ELEMENT_NAME_BACKUP = "backup";

    /** "slave" element name. */
    private static final String ELEMENT_NAME_SLAVE = "slave";

    /** "host" element name. */
    private static final String ELEMENT_NAME_HOST = "host";

    /** "port" element name. */
    private static final String ELEMENT_NAME_PORT = "port";

    /** "ref" element name. */
    private static final String ELEMENT_NAME_REF = "ref";

    /** "replicate" element name. */
    private static final String ELEMENT_NAME_REPLICATE = "replicate";

    /** "iname" attribute name. */
    private static final String ATTRIBUTE_NAME_INAME = "iname";

    /** "group" attribute name. */
    private static final String ATTRIBUTE_NAME_GROUP = "group";

    /**
     * iname map.
     */
    private final Map<String, NodeData> inameMap = new HashMap<String, NodeData>();

    /**
     * set of NodeData.
     */
    private final Set<NodeData> nodeDataSet = new HashSet<NodeData>();

    /**
     * set of NodeRef.
     */
    private final Set<NodeRef> nodeRefSet = new HashSet<NodeRef>();

    /**
     * name map.
     */
    private final Map<String, Node> nameMap = new HashMap<String, Node>();

    /**
     * list of nodes.
     */
    private final List<Node> nodeList = new ArrayList<Node>();

    /**
     * root of xml.
     */
    private final Element root;

    /**
     * Constructor.
     * @param nodeconfFile configuration filename.
     * @throws AnuenueException in case of any error.
     */
    public NodeConfFileParser(final String nodeconfFile) throws AnuenueException {
        try {
            root = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder().parse(new File(nodeconfFile)).getDocumentElement();
        } catch (Exception e) {
            LOG.error(e.toString());
            throw new AnuenueException(e);
        }
    }

    /**
     * Parse XML configuration file.
     * @throws AnuenueException in case of parse error.
     */
    public void parse() throws AnuenueException {

        extractNodes(root.getElementsByTagName(ELEMENT_NAME_MERGER), Role.MERGER);
        extractNodes(root.getElementsByTagName(ELEMENT_NAME_MASTER), Role.MASTER);
        extractNodes(root.getElementsByTagName(ELEMENT_NAME_BACKUP), Role.BACKUP);
        extractNodes(root.getElementsByTagName(ELEMENT_NAME_SLAVE), Role.SLAVE);

        resolveRefs();

        buildNameMapAndNodes();
    }
    /**
     * Extract NodeData and NodeRef from xml NodeList.
     * @param xmlNodeList xml NodeList.
     * @param role Node role.
     * @throws AnuenueException in case of parse error
     */
    private void extractNodes(final NodeList xmlNodeList, final Role role)
            throws AnuenueException {
        LOG.debug("processing node: " + role);
        LOG.debug("number of " + role + " nodeList: " + xmlNodeList.getLength());
        for (int i = 0; i < xmlNodeList.getLength(); ++i) {
            Element node = (Element) xmlNodeList.item(i);
            if (node.getElementsByTagName(ELEMENT_NAME_REF).item(0) == null) {
                NodeData nodeData = createNodeData(node, role);
                nodeDataSet.add(nodeData);
                inameMap.put(nodeData.referenceName, nodeData);
            } else {
                nodeRefSet.add(createNodeRef(node, role));
            }
        }
    }

    /**
     * Resolve references.
     * @throws AnuenueException in case of parse error
     */
    private void resolveRefs() throws AnuenueException {
        for (NodeRef ref : nodeRefSet) {
            if (inameMap.containsKey(ref.referenceName)) {
                NodeData data = inameMap.get(ref.referenceName);
                if (!data.groupName.equals(ref.groupName)
                        && ref.assignedRole.equals(Role.SLAVE)) {
                    data.groupName = ref.groupName;
                }
                data.assignedRoles.add(ref.assignedRole);
            } else {
                throw new AnuenueException("cannot resolve reference name: "
                        + ref.referenceName);
            }
        }

    }

    /**
     * Extract group name of nodes.
     * @param node XML node
     * @return group name.
     */
    private static String extractGroupName(final Element node) {
        String groupName = null;
        if (node.getParentNode().getAttributes().getNamedItem(ATTRIBUTE_NAME_GROUP) != null) {
            groupName = node.getParentNode().getAttributes()
                    .getNamedItem(ATTRIBUTE_NAME_GROUP).toString();
        }
        if (groupName == null || groupName.equals("")) {
            groupName = Node.DEFAULT_GROUP_NAME;
        }
        return groupName;
    }

    /**
     * Create NodeData.
     * @param node xml Node
     * @param role node role
     * @return NodeData
     * @throws AnuenueException in case of parse error
     */
    private static NodeData createNodeData(final Element node, final Role role)
            throws AnuenueException {

        Element hostElement = (Element) node.getElementsByTagName(ELEMENT_NAME_HOST).item(
                0);
        if (hostElement == null) {
            throw new AnuenueException("host not found");
        }

        /* extracting hostname and port */
        String host = hostElement.getFirstChild().getNodeValue();
        Element portElement = (Element) node.getElementsByTagName(ELEMENT_NAME_PORT).item(
                0);
        int port = Integer.valueOf(portElement.getFirstChild().getNodeValue());

        /* extracting name */
        String name = node.getAttribute(ATTRIBUTE_NAME_INAME);
        if (name == null || name.equals("")) {
            name = host + ":" + port;
        }

        /* extracting group name (slave) */
        String groupName = extractGroupName(node);

        /* create SolrInstance and assign information */
        /* replication settings */
        Element replicatElement = (Element) node.getElementsByTagName(
                ELEMENT_NAME_REPLICATE).item(0);
        String replication = null;

        if (replicatElement != null) {
            replication = replicatElement.getFirstChild().getNodeValue();
            LOG.debug("replication: " + replication);
        }

        return new NodeData(host, port, replication, role, groupName, name);
    }

    /**
     * Build nameMap and list of nodes.
     */
    private void buildNameMapAndNodes() {
        for (NodeData data : nodeDataSet) {
            Node node = data.toNode();
            nodeList.add(node);
            for (String name : data.names) {
                nameMap.put(name, node);
            }
        }
    }
    /**
     * Create NodeRef.
     * @param node xml Node
     * @param role node role
     * @return NodeRef
     */
    private static NodeRef createNodeRef(final Element node, final Role role) {
        Element hostElement = (Element) node.getElementsByTagName(ELEMENT_NAME_REF)
                .item(0);
        String refName = hostElement.getFirstChild().getNodeValue();

        String groupName = extractGroupName(node);

        return new NodeRef(refName, role, groupName);
    }

    /**
     * Temporary Storage of Node data.
     */
    private static final class NodeData {
        /** host name. */
        private final String hostName;
        /** port number. */
        private final int portNumber;
        /** replication master. */
        private final String replicationMaster;
        /** Node role. */
        private final Set<Role> assignedRoles;
        /** node group name. */
        private transient String groupName;
        /** reference name. */
        private final String referenceName;
        /** names which indicate this node. */
        private final Set<String> names;

        /**
         * Constructor.
         * @param host host name
         * @param port port number
         * @param replicate replication master
         * @param role Node role
         * @param group group name
         * @param iname reference name
         */
        public NodeData(final String host, final int port, final String replicate,
                final Role role, final String group, final String iname) {
            hostName = host;
            portNumber = port;
            replicationMaster = replicate;
            assignedRoles = new HashSet<Role>();
            assignedRoles.add(role);
            groupName = group;
            referenceName = iname;
            names = new HashSet<String>();
            names.add(hostName + ":" + portNumber);
            if (referenceName != null) {
                names.add(referenceName);
            }
        }

        /**
         * Convert to Node class.
         * @return Node class.
         */
        private Node toNode() {
            return new Node(hostName, portNumber, assignedRoles, replicationMaster, groupName);
        }
    }

    /**
     *  Temporary Storage of Node reference.
     */
    private static final class NodeRef {
        /** reference name. */
        private final String referenceName;
        /** Node role. */
        private final Role assignedRole;
        /** node group name. */
        private final String groupName;

        /**
         * Constructor.
         * @param ref reference name.
         * @param role Node role.
         * @param group group name.
         */
        public NodeRef(final String ref, final Role role, final String group) {
            referenceName = ref;
            assignedRole = role;
            groupName = group;
        }
    }

    /**
     * Get nameMap.
     * @return nameMap
     */
    public Map<String, Node> getNameMap() {
        return nameMap;
    }

    /**
     * Get nodeList.
     * @return nodeList
     */
    public List<Node> getNodeList() {
        return nodeList;
    }
}
