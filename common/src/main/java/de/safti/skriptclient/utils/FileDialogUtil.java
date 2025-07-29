package de.safti.skriptclient.utils;

import net.minecraft.client.Minecraft;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

public class FileDialogUtil {

    public static void openFileChooser(Consumer<File> onComplete) {
        new Thread(() -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);

            File selected = (result == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
            if(selected == null) {
                return;
            }

            // Schedule back to the main client thread
            Minecraft.getInstance().execute(() -> onComplete.accept(selected));
        }, "FileChooserThread").start();
    }
}