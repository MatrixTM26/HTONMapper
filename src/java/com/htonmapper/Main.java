package com.htonmapper;

import com.htonmapper.gui.HTONMapperGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] ArgumentsArg) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ExceptionArg) {
            /* fallback to default look and feel silently */
        }
        SwingUtilities.invokeLater(() -> {
            HTONMapperGUI GUIInstance = new HTONMapperGUI();
            GUIInstance.LaunchApplication();
        });
    }
}
