package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.sidebar.Sidebar;
import io.wispforest.owo.ui.core.Surface;

import java.nio.file.Path;

public class StorageSidebar extends Sidebar {

    private final Path rootPath;
    private final FileTree tree;

    public StorageSidebar(ScriptManagementScreen screen, Path rootPath) {
        if(!rootPath.toFile().isDirectory()) {
            throw new IllegalArgumentException("Did not provide a directory as root path!");
        }

        this.rootPath = rootPath;
        tree = new FileTree(screen, rootPath, this);
        allowOverflow(true); // TODO: remove
        tree.getRootNode().render(1);


        id("Storage Sidebar");
        surface(Surface.DARK_PANEL);
    }

    public Path getRootPath() {
        return rootPath;
    }

    public FileTree getTree() {
        return tree;
    }
}
