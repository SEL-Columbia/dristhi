package org.opensrp.api.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


public class TreeTest {

    @Test
    public void testAddingNode() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1,"test", 2, null );

        TreeNode<Integer, Integer> node = tree.getNode(1);

        assertEquals(1, (int)node.getId());
        assertEquals("test", (String) node.getLabel());
        assertEquals(2, (int) node.getNode());
        assertNull(node.getParent());
    }

}
