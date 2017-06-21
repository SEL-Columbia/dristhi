package org.opensrp.api.util;


import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;


public class TreeTest {

    @Test
    public void testAddingNodeWithOutParent() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1, "test", 2, null);

        TreeNode<Integer, Integer> node = tree.getNode(1);

        assertEquals(1, (int) node.getId());
        assertEquals("test", (String) node.getLabel());
        assertEquals(2, (int) node.getNode());
        assertNull(node.getParent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotReAddExistingNode() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1, "test", 2, null);
        tree.addNode(1, "test", 2, null);
    }

    @Test
    public void testAddingNodeWithValidParent() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(1, "test", 1, null);
        tree.addNode(2, "test2", 2, 1);

        TreeNode<Integer, Integer> childNode = tree.getNode(2);

        assertEquals(2, (int) childNode.getId());
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

        assertEquals(2, (int) childNode.getId());
        assertEquals("test2", (String) childNode.getLabel());
        assertEquals(2, (int) childNode.getNode());

        assertNodeIsRoot(tree, childNode);
    }

    @Test
    public void testAddingNodeFirstChildThenParent() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(2, "test2", 2, 1);
        tree.addNode(3, "test3", 3, 1);

        tree.addNode(1, "test1", 1, null);

        TreeNode<Integer, Integer> rootNode = tree.getNode(1);
        assertNodeIsRoot(tree, rootNode);
        assertNull(rootNode.getParent());

        Map<Integer, TreeNode<Integer, Integer>> childNodes = rootNode.getChildren();

        assertEquals(2, (int) childNodes.get(2).getId());
        assertEquals(3, (int) childNodes.get(3).getId());

        Map<Integer, Set<Integer>> childParentList = tree.getChildParent();
        assertEquals(2, childParentList.get(1).size());

    }

    @Test
    public void testRemoveRootNode() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(2, "test2", 2, null);
        tree.addNode(3, "test3", 3, null);

        TreeNode<Integer, Integer> nodeToRemove = tree.removeNode(2);
        TreeNode<Integer, Integer> nodeToRemoveSecond = tree.removeNode(3);

        assertEquals(2, (int) nodeToRemove.getId());
        assertNull(tree.getNode(2));

        assertEquals(3, (int) nodeToRemoveSecond.getId());
        assertNull(tree.getNode(3));
    }

    @Test
    public void testRemovingChildNode() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(2, "test2", 2, 1);
        tree.addNode(3, "test3", 3, 1);
        tree.addNode(1, "test1", 1, null);

        TreeNode<Integer, Integer> nodeToRemove = tree.removeNode(2);
        TreeNode<Integer, Integer> nodeToRemoveSecond = tree.removeNode(3);

        TreeNode<Integer, Integer> rootNode = tree.getNode(1);

        assertEquals(0, rootNode.getChildren().size());
    }

    @Test
    public void testRemoveInvalidNode() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(2, "test2", 2, 1);

        TreeNode<Integer, Integer> nodeToRemove = tree.removeNode(5);
        assertNull(nodeToRemove);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableMemberThroughSetter() {
        Tree<Integer, Integer> tree = new Tree<>();
        tree.addNode(2, "test2", 2, null);
        tree.addNode(3, "test3", 3, null);

        Map<Integer, TreeNode<Integer, Integer>> nodesMap = tree.getTree();
        Map<Integer, Set<Integer>> childParentList = tree.getChildParent();

        assertEquals(2, (int) nodesMap.get(2).getId());

        nodesMap.remove(2);
        childParentList.put(1, new HashSet<Integer>() {
        });
    }

    private void assertNodeIsRoot(Tree<Integer, Integer> tree, TreeNode<Integer, Integer> node) {
        assertEquals(1, tree.map.size());
        assertEquals((int) node.getId(), (int) tree.map.get(node.getId()).getId());
    }

}
