package de.safti.skriptclient.commons.screens.components.main.file;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.api.EditableTextView;
import de.safti.skriptclient.commons.screens.components.main.AbstractMainArea;
import io.wispforest.owo.ui.core.Sizing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMainArea extends AbstractMainArea {
    static final float TEXT_SIZE = 0.5f;
    private static final Logger log = LoggerFactory.getLogger(FileMainArea.class);

    private final EditableTextView textComponent;

    public FileMainArea(ScriptManagementScreen screen, Path path) {
        super(screen, Sizing.expand(), Sizing.expand(), Algorithm.VERTICAL);


        try {
            String content = Files.readString(path);
            textComponent = new EditableTextView(Sizing.expand(), Sizing.expand());
            textComponent.setText(content);

        } catch (IOException e) {
            log.error("Failed to read content of file: {}", path);
            throw new RuntimeException(e);
        }

        child(textComponent);

    }
}
