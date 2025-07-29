package de.safti.skriptclient.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatcher {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyToDir = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    private final WatchEventConsumer eventConsumer;

    public interface WatchEventConsumer {
        void onEvent(WatchEvent.Kind<?> kind, Path dir, Path path);
    }

    public DirectoryWatcher(Path rootDir, WatchEventConsumer consumer)  {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            registerAll(rootDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.eventConsumer = consumer;
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keyToDir.put(key, dir);
    }

    public void start() {
        if (running) return;
        running = true;

        executor.submit(() -> {
            while (running) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                Path dir = keyToDir.get(key);
                if (dir == null) {
                    key.reset();
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == OVERFLOW) continue;

                    Path name = (Path) event.context();
                    Path child = dir.resolve(name);

                    // Register new subdirectories on creation
                    if (kind == ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child)) {
                                registerAll(child);
                            }
                        } catch (IOException ignored) {}
                    }

                    eventConsumer.onEvent(kind, dir, child);
                }

                boolean valid = key.reset();
                if (!valid) {
                    keyToDir.remove(key);
                }
            }
        });
    }

    public void stop() throws IOException {
        running = false;
        executor.shutdownNow();
        watchService.close();
        keyToDir.clear();
    }

}
