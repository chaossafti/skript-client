package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import java.nio.file.Path;

public class FileTree {

    private final PathNode rootNode;

    public FileTree(Path path, StorageSidebar storageSidebar) {
        if(!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("Did not provide a directory as root!");
        }

        rootNode = new PathNode(path, storageSidebar, 0);
    }

    public PathNode getRootNode() {
        return rootNode;
    }



}
