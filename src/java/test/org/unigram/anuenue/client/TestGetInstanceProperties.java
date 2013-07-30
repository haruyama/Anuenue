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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.unigram.anuenue.node.Node;
import org.unigram.anuenue.node.Node.Role;

/**
 * Test class of GetInstanceProperties.
 */
public final class TestGetInstanceProperties {

    /**
     * Test of detectRole().
     */
    @Test
    public void testDetectRole() {
        /** simple cases */
        Node node1 = createTestNode(Role.MASTER);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.INDEX, GetInstanceProperties.detectRole(node1));

        Node node2 = createTestNode(Role.BACKUP);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.REPLICATE, GetInstanceProperties.detectRole(node2));

        Node node3 = createTestNode(Role.SLAVE);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.REPLICATE, GetInstanceProperties.detectRole(node3));

        Node node4 = createTestNode(Role.MERGER);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.STANDALONE, GetInstanceProperties.detectRole(node4));

        /** cases with more than one roles */
        Node node5 = createTestNode(Role.MASTER, Role.SLAVE);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.STANDALONE, GetInstanceProperties.detectRole(node5));

        Node node6 = createTestNode(Role.MASTER, Role.BACKUP);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.STANDALONE, GetInstanceProperties.detectRole(node6));

        Node node7 = createTestNode(Role.MASTER, Role.SLAVE, Role.MERGER);
        assertEquals(GetInstanceProperties.INSTANCE_ROLE.STANDALONE, GetInstanceProperties.detectRole(node7));
    }

    /**
     * Create Node for test.
     * @param roles array of roles.
     * @return node for test
     */
    private Node createTestNode(final Role... roles) {
        return new Node("host", 10000, new HashSet<Role>(Arrays.asList(roles)), null, null);
    }

}
