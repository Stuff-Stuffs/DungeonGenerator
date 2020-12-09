package io.github.stuff_stuffs.dungeon_generator.tree;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ReferenceMutableTree<V, E> implements MutableTree<V, E> {
    private final MutableNodeImpl<V, E> root;
    private final Map<V, MutableNodeImpl<V, E>> nodeMap;

    public ReferenceMutableTree(final V root) {
        this.root = new MutableNodeImpl<>(root, null);
        nodeMap = new Reference2ReferenceOpenHashMap<>();
        nodeMap.put(root, this.root);
    }


    protected MutableNodeImpl<V, E> find(final V target, final MutableNodeImpl<V, E> current) {
        if (current.getValue() == target) {
            return current;
        }
        return nodeMap.get(target);
    }


    @Override
    public MutableTree.MutableNode<V, E> getRoot() {
        return root;
    }

    @Override
    public boolean insert(final V parent, final V child, final E edge) {
        final MutableNodeImpl<V, E> childNode = find(child, root);
        if (childNode != null) {
            //TODO throw exception?
            return false;
        }
        final MutableNodeImpl<V, E> node = find(parent, root);
        if (parent != null) {
            MutableNodeImpl<V, E> mutableNode = new MutableNodeImpl<>(child, null);
            node.addChild(mutableNode, edge);
            nodeMap.put(child, mutableNode);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(final V parent, final V child) {
        final MutableNodeImpl<V, E> childNode = find(child, root);
        if (childNode == null) {
            //TODO throw exception?
            return false;
        }
        final MutableNodeImpl<V, E> node = find(parent, root);
        if (parent == null) {
            return false;
        }
        node.removeChild(child);
        remove(childNode);
        return true;
    }

    private void remove(MutableNodeImpl<V, E> node) {
        for (MutableEdgeImpl<V, E> edge : node.edges) {
            remove(edge.getChild());
        }
        nodeMap.remove(node.getValue());
    }


    private static class MutableNodeImpl<V, E> implements MutableTree.MutableNode<V, E> {
        private final V value;
        private MutableEdgeImpl<V, E> parent;
        private final Collection<MutableEdgeImpl<V, E>> edges = new ArrayList<>();

        public MutableNodeImpl(final V value, final MutableEdgeImpl<V, E> parent) {
            this.value = value;
            this.parent = parent;
        }

        public void removeChild(final V childValue) {
            edges.removeIf(i -> i.getChild().getValue() == childValue);
        }

        public void addChild(final MutableNodeImpl<V, E> mutableNode, final E edgeValue) {
            final MutableEdgeImpl<V, E> mutableEdge = new MutableEdgeImpl<>(edgeValue, this, mutableNode);
            mutableNode.parent = mutableEdge;
            edges.add(mutableEdge);
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public MutableEdgeImpl<V, E> getParent() {
            return parent;
        }

        @Override
        public Collection<MutableTree.MutableEdge<V, E>> getEdges() {
            return Collections.unmodifiableCollection(edges);
        }
    }

    private static class MutableEdgeImpl<V, E> implements MutableTree.MutableEdge<V, E> {
        private final MutableNodeImpl<V, E> parent;
        private final MutableNodeImpl<V, E> child;
        private final E value;

        public MutableEdgeImpl(final E value, final MutableNodeImpl<V, E> parent, final MutableNodeImpl<V, E> child) {
            this.value = value;
            this.parent = parent;
            this.child = child;
        }

        @Override
        public E getValue() {
            return value;
        }

        @Override
        public MutableNodeImpl<V, E> getParent() {
            return parent;
        }

        @Override
        public MutableNodeImpl<V, E> getChild() {
            return child;
        }
    }
}
