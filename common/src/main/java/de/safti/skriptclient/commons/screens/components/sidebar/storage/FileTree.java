package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;

import java.nio.file.Path;

public class FileTree {

    private final PathNode rootNode;

    public FileTree(ScriptManagementScreen screen, Path path, StorageSidebar storageSidebar) {
        if(!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("Did not provide a directory as root!");
        }

        rootNode = new PathNode(screen, path, storageSidebar, 0);
    }

    public PathNode getRootNode() {
        return rootNode;
    }



}
