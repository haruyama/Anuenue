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

import org.junit.Test;
import org.unigram.anuenue.exception.AnuenueException;
import org.unigram.anuenue.node.Node.Role;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
/**
 * Test class of AnuenueNode.
 */
public final class TestNodeDatabase {

    /**
     * Test of parsing XML file.
     */
    @Test
    public void testParse() {
        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-1.xml").build();
        } catch (AnuenueException e) {
            fail();
        }

        assertSame("size of nodeList", 5, database.getNodeList().size());
        assertSame("size of masterList", 2, database.getMasterList().size());
        assertSame("size of slaveList", 2, database.getSlaveList().size());
        assertSame("size of backupList", 0, database.getBackups().size());
    }

    /**
     * Test of replication settiong.
     */
    @Test
    public void testReplication() {
        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-1.xml").build();
        } catch (AnuenueException e) {
            fail();
        }
        List<Node> slaves = database.getSlaveList();
        assertSame(2, slaves.size());

        assertTrue(slaves.contains(new Node("dddd", 7983, Role.SLAVE,
                "node2")));
        assertTrue(slaves.contains(new Node("eeee", 6983, Role.SLAVE,
                "node3")));
        assertFalse(slaves.contains(new Node("dddd", 7983, Role.MASTER,
                "node2")));
        assertFalse(slaves.contains(new Node("eeee", 7983, Role.SLAVE,
                "node2")));
        assertFalse(slaves.contains(new Node("dddd", 6983, Role.SLAVE,
                "node2")));
    }

    /**
     * Test of getNodesByHost().
     */
    @Test
    public void testGetNodesByHost() {
        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-2.xml").build();
        } catch (AnuenueException e) {
            fail();
        }

        List<Node> aaaaNodes = database.getNodesByHost("aaaa");
        assertSame("getNodesByHost()", 4, aaaaNodes.size());
        assertTrue("getNodesByHost(), 18983", aaaaNodes.contains(new Node("aaaa", 18983, Role.MERGER, null)));
        assertTrue("getNodesByHost(), 7893", aaaaNodes.contains(new Node("aaaa", 7983, Role.MASTER, null)));
        assertTrue("getNodesByHost(), 6983", aaaaNodes.contains(new Node("aaaa", 6983, Role.SLAVE, "node2")));
        assertTrue("getNodesByHost(), 5983", aaaaNodes.contains(new Node("aaaa", 5983, Role.BACKUP, "node2")));

        List<Node> bbbbNodes = database.getNodesByHost("bbbb");
        assertSame("getNodesByHost()", 3, bbbbNodes.size());
        assertTrue("getNodesByHost(), 7983", bbbbNodes.contains(new Node("bbbb", 7983, Role.MASTER, null)));
        assertTrue("getNodesByHost(), 6983", bbbbNodes.contains(new Node("bbbb", 6983, Role.SLAVE, "node3")));
        assertTrue("getNodesByHost(), 5983", bbbbNodes.contains(new Node("bbbb", 5983, Role.BACKUP, "node3")));
    }

    /**
     * Test of getNodeByName().
     */
    @Test
    public void testGetNodeByName() {
        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-2.xml").build();
        } catch (AnuenueException e) {
            fail();
        }
        assertEquals("getNodeByName(aaaa, 7983, ...)", new Node("aaaa", 7983, Role.MASTER, null), database.getNodeByName("node2"));
        assertEquals("getNodeByName(bbbb, 7983, ...)", new Node("bbbb", 7983, Role.MASTER, null), database.getNodeByName("node3"));
        assertEquals("getNodeByName(aaaa, 18983, ...)", new Node("aaaa", 18983, Role.MERGER, null), database.getNodeByName("aaaa:18983"));
        assertEquals("getNodeByName(aaaa, 6983, ...)", new Node("aaaa", 6983, Role.SLAVE, "node2"), database.getNodeByName("aaaa:6983"));
        assertEquals("getNodeByName(bbbb, 6983, ...)", new Node("bbbb", 6983, Role.SLAVE, "node3"), database.getNodeByName("bbbb:6983"));
        assertEquals("getNodeByName(aaaa, 5983, ...)", new Node("aaaa", 5983, Role.BACKUP, "node2"), database.getNodeByName("aaaa:5983"));
        assertEquals("getNodeByName(bbbb, 5983, ...)", new Node("bbbb", 5983, Role.BACKUP, "node3"), database.getNodeByName("bbbb:5983"));
    }

    /**
     * Test of roles.
     */
    @Test
    public void testMultiRoles() {
        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-3.xml").build();
        } catch (AnuenueException e) {
            fail();
        }

        assertSame("size of nodelist", 3, database.getNodeList().size());

        Node node1 = database.getNodeByName("node1");
        Role[] expectedRoles1 = {Role.MERGER, Role.MASTER, Role.SLAVE};
        assertTrue("node1.checkRoles()", node1.checkRoles(expectedRoles1));

        Node node2 = database.getNodeByName("node2");
        Role[] expectedRoles2 = {Role.MERGER, Role.MASTER};
        assertTrue("node2.checkRoles()", node2.checkRoles(expectedRoles2));

        Node node3 = database.getNodeByName("eeee:6983");
        Role[] expectedRoles3 = {Role.SLAVE};
        assertTrue("node3.checkRoles()", node3.checkRoles(expectedRoles3));
    }

    /**
     * Test of bad XML file.
     * @throws AnuenueException always
     */
    @Test(expected = AnuenueException.class)
    public void testException() throws AnuenueException {
        new NodeDatabaseBuilder("resources/anuenue-nodes-4.xml").build();
        fail();
    }

    /**
     * Test of slave group.
     */
    @Test
    public void testSlaveGroups() {

        // test with two groups
        NodeDatabase database = null;
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-groups.xml").build();
        } catch (AnuenueException e) {
            fail("parse error.");
        }

        Set<String> groupNames = database.getSlaveGroupNames();
        assertSame("size of slavegroup", 2, groupNames.size());

        for (String groupName : groupNames) {
            List<Node> members = database.getSlavesInGroup(groupName);
            assertSame("size of slaves in slavegroup", 2, members.size());
        }

        // test with single group without label
        try {
            database = new NodeDatabaseBuilder("resources/anuenue-nodes-1.xml").build();
        } catch (AnuenueException e) {
            fail();
        }
        groupNames = database.getSlaveGroupNames();
        assertSame("size of slavegroup, only Default group", 1, groupNames.size());
    }
}
