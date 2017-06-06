package org.opensrp.api.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;


public class TreeTest {

    @Test
    public void testAddingNodeWithOutParent() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1,"test", 2, null );

        TreeNode<Integer, Integer> node = tree.getNode(1);

        assertEquals(1, (int)node.getId());
        assertEquals("test", (String) node.getLabel());
        assertEquals(2, (int) node.getNode());
        assertNull(node.getParent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotReAddExistingNode() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1,"test", 2, null );
        tree.addNode(1,"test", 2, null );
    }

    @Test
    public void testAddingNodeWithValidParent() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1,"test", 1, null );
        tree.addNode(2, "test2", 2, 1);

        TreeNode<Integer, Integer> childNode = tree.getNode(2);

        assertEquals(2, (int)childNode.getId());
        assertEquals("test2", (String) childNode.getLabel());
        assertEquals(2, (int) childNode.getNode());

        assertNotNull(childNode.getParent());

        int parentNodeId = childNode.getParent();

        assertEquals(1, parentNodeId);
    }

    @Test
    public void testAddingNodeWithInvalidParent() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(2, "test2", 2, 1);

        TreeNode<Integer, Integer> childNode = tree.getNode(2);

        assertEquals(2, (int)childNode.getId());
        assertEquals("test2", (String) childNode.getLabel());
        assertEquals(2, (int) childNode.getNode());

        assertNodeIsRoot(tree, childNode);

    }

    private void assertNodeIsRoot(Tree<Integer,Integer> tree, TreeNode<Integer, Integer> node) {
        assertEquals(1 , tree.map.size());
        assertEquals((int)node.getId(), (int)tree.map.get(node.getId()).getId());
    }

}
