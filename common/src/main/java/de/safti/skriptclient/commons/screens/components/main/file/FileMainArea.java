package de.safti.skriptclient.commons.screens.components.main.file;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.api.ALScrollContainer;
import de.safti.skriptclient.commons.screens.components.api.BiScrollContainer;
import de.safti.skriptclient.commons.screens.components.api.EditableTextView;
import de.safti.skriptclient.commons.screens.components.main.AbstractMainArea;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMainArea extends AbstractMainArea {
    public static final float FONT_SCALE = 1f; // TODO: config option
    public static final int LINE_HEIGHT = (int) (Minecraft.getInstance().font.lineHeight* FONT_SCALE);
    public static final int LINE_GAP = 4;


    private static final Logger log = LoggerFactory.getLogger(FileMainArea.class);

    private final BiScrollContainer<FlowLayout> scrollContainer;
    private final EditableTextView editableTextView;
    private final LineColumn lineColumn;
    private final FlowLayout contentLayout;

    public FileMainArea(ScriptManagementScreen screen, Path path) {
        super(screen, Sizing.expand(), Sizing.expand(), Algorithm.HORIZONTAL);

        // config
        padding(Insets.left(12));


        // place EditableTextView
        try {

            contentLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());

            String content = Files.readString(path);
            scrollContainer = new BiScrollContainer<>(Sizing.expand(), contentLayout);
            editableTextView = new EditableTextView(Sizing.content(), Sizing.content());
            editableTextView.setText(content);

            lineColumn = new LineColumn(this);



            contentLayout.child(lineColumn);
            contentLayout.child(editableTextView);


        } catch (IOException e) {
            log.error("Failed to read content of file: {}", path);
            throw new RuntimeException(e);
        }




        editableTextView.type().subscribe((index, str) -> {
            lineColumn.onTextChange(index, str, editableTextView.getContent());
        });


        // children
        child(scrollContainer);
    }

    public EditableTextView getEditableTextView() {
        return editableTextView;
    }

    public int getLineCount() {
        return Math.toIntExact(editableTextView.getContent().chars()
                .asDoubleStream()
                .filter(value -> value == (double) '\n')
                .count())+1;
    }

}
