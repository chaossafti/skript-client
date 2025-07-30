package de.safti.skriptclient.commons.screens.components.sidebar.storage;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class PathNode {
    Comparator<PathNode> PATH_NODE_COMPARATOR = Comparator
            .comparing((PathNode node) -> Files.isDirectory(node.path()) ? 0 : 1) // directories first
            .thenComparing(node -> node.path().getFileName().toString().toLowerCase()); // alphabetic, case-insensitive

    private final Path path;
    private final @Nullable Set<PathNode> children;
    private final FlowLayout parentLayout;
    private final FlowLayout content;

    public PathNode(ScriptManagementScreen screen, Path path, @NotNull FlowLayout parentLayout, int depth) {
        this.path = path;
        this.parentLayout = parentLayout;
        content = isDirectory() ? new DirectoryComponent(this, depth) : new FileComponent(screen, this, depth);
        content.id("content");
        this.children = Files.isDirectory(path) ? collectChildren(screen, path, depth) : new HashSet<>();
    }

    private Set<PathNode> collectChildren(ScriptManagementScreen screen, Path path, int depth) {
        Set<PathNode> result = new HashSet<>();
        try(var stream = Files.list(path)) {
            stream.forEach(p -> result.add(new PathNode(screen, p, content, depth+1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public Stream<PathNode> streamChildren() {
        if(children == null) {
            throw new IllegalStateException("tried streaming non-directory");
        }

        return children.stream();
    }

    public boolean isDirectory() {
        return path.toFile().isDirectory();
    }

    public File asFile() {
        return path.toFile();
    }

    public String fullFileName() {
        return asFile().getName();
    }

    public void render(int depth) {
        parentLayout.child(content);
        content.allowOverflow(true);
        if(children == null) return;
        streamChildren()
                .sorted(PATH_NODE_COMPARATOR)
                .forEach(pathNode -> pathNode.render(depth+1));
    }

    public Path path() {
        return path;
    }

    public @Nullable Set<PathNode> children() {
        return children;
    }



}
