package org.opensrp.api.util;


import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreeNodeTest {
    int rootId = 1;
    int childId = 2;
    int grandChildId = 3;

    @Test
    public void testChildCreationUsingConstructor() {
        TreeNode<Integer, Integer> childNode = new TreeNode<Integer, Integer>(childId,"t1", 1, rootId);
        Map<Integer, TreeNode<Integer, Integer>> children = new HashMap<>();
        children.put(1, childNode);

        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "t2",2, null, children);

        assertEquals(childId, (int)rootNode.findChild(childId).getId());
        assertEquals(rootId, (int)rootNode.findChild(childId).getParent());
    }

    @Test
    public void testAddingChild() {

        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "root",2, null);
        TreeNode<Integer, Integer> childNode = new TreeNode<Integer, Integer>(childId,"child", 1, rootId);
        rootNode.addChild(childNode);

        assertEquals(childId, (int)rootNode.findChild(childId).getId());
        assertEquals(rootId, (int)rootNode.findChild(childId).getParent());
    }

    @Test
    public void findValidGrandChildren() {
        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "root",1, null);
        TreeNode<Integer, Integer> childNode = new TreeNode<Integer, Integer>(childId,"child", 2, rootId);
        TreeNode<Integer, Integer> grandChildNode = new TreeNode<Integer, Integer>(grandChildId,"grandChild", 3, childId);
        rootNode.addChild(childNode);
        childNode.addChild(grandChildNode);

        assertEquals(childId, (int)rootNode.findChild(childId).getId());
        assertEquals(rootId, (int)rootNode.findChild(childId).getParent());

        assertEquals(grandChildId, (int)rootNode.findChild(grandChildId).getId());
        assertEquals(childId, (int)rootNode.findChild(grandChildId).getParent());
    }

    @Test
    public void findInvalidChildren() {
        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "root",1, null);
        assertNull(rootNode.getChildren());
        assertNull(rootNode.findChild(childId));
    }

    @Test
    public void testRemoveValidChild() {
        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "root",2, null);
        TreeNode<Integer, Integer> childNode = new TreeNode<Integer, Integer>(childId,"child", 1, rootId);
        rootNode.addChild(childNode);

        assertEquals(childId, (int)rootNode.findChild(childId).getId());

        TreeNode<Integer, Integer> removedNode = rootNode.removeChild(childId);

        assertEquals(childId, (int)removedNode.getId());
        assertNull(rootNode.findChild(childId));
    }

    @Test
    public void testRemoveValidGrandChild() {
        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "root",1, null);
        TreeNode<Integer, Integer> childNode = new TreeNode<Integer, Integer>(childId,"child", 2, rootId);
        TreeNode<Integer, Integer> grandChildNode = new TreeNode<Integer, Integer>(grandChildId,"grandChild", 3, childId);
        rootNode.addChild(childNode);
        childNode.addChild(grandChildNode);

        assertEquals(childId, (int)rootNode.findChild(childId).getId());
        assertEquals(grandChildId, (int)rootNode.findChild(grandChildId).getId());

        TreeNode<Integer, Integer> removedNode = rootNode.removeChild(grandChildId);

        assertEquals(grandChildId, (int)removedNode.getId());
        assertNull(rootNode.findChild(grandChildId));
    }

    @Test
    public void testRemoveInvalidChild() {
        TreeNode<Integer, Integer> rootNode = new TreeNode<Integer, Integer>(rootId, "root",1, null);
        TreeNode<Integer, Integer> childNode = new TreeNode<Integer, Integer>(childId,"child", 2, rootId);
        TreeNode<Integer, Integer> grandChildNode = new TreeNode<Integer, Integer>(grandChildId,"grandChild", 3, childId);
        rootNode.addChild(childNode);
        childNode.addChild(grandChildNode);

        assertNull(rootNode.removeChild(4));
    }
}
