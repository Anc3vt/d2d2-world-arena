
package com.ancevt.d2d2world.editor.swing;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.Room;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class JPropertiesEditor extends JFrame {

    private final JScrollPane scrollPane;

    //private static final JPropertiesEditor instance = new JPropertiesEditor();

    public static void main(String[] args) {
        create("Test", "some default text", null);
    }

    private OkFunction okFunction;

    private final JTextArea textArea;
    private final JButton buttonOk;
    private final JButton buttonCancel;

    private JPropertiesEditor() {
        setSize(700, 700);
        setPreferredSize(new Dimension(700, 700));

        textArea = new JTextArea();
        textArea.setBackground(java.awt.Color.BLACK);
        textArea.setForeground(java.awt.Color.LIGHT_GRAY);
        textArea.setCaretColor(java.awt.Color.WHITE);

        if (checkFontExists("Terminus (TTF)")) {
            textArea.setFont(new Font("Terminus (TTF)", Font.BOLD, 20));
        } else {
            textArea.setFont(new Font("Monospaced", Font.BOLD, 20));
        }

        textArea.setBorder(null);

        buttonCancel = new JButton("Cancel");
        buttonOk = new JButton("OK");

        buttonCancel.setBackground(java.awt.Color.BLACK);
        buttonOk.setBackground(java.awt.Color.BLACK);
        buttonCancel.setBorder(BorderFactory.createLoweredBevelBorder());
        buttonOk.setBorder(BorderFactory.createLoweredBevelBorder());

        setLocationByPlatform(true);
        setBackground(java.awt.Color.BLACK);
        getContentPane().setBackground(java.awt.Color.BLACK);
        getContentPane().add(textArea);
        getContentPane().add(buttonOk);
        getContentPane().add(buttonCancel);
        getContentPane().setLayout(null);

        pack();

        buttonOk.addActionListener(e -> ok());
        buttonCancel.addActionListener(e -> dispose());

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(textArea.getSize());
        scrollPane.setBorder(null);

        getContentPane().add(scrollPane);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                resize();
            }
        });

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) ok();
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
            }
        });
        resize();
    }

    public static boolean checkFontExists(String fontName) {
        for (String font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (font.equals(fontName)) return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        //super.dispose();
        setVisible(false);

    }

    private void ok() {
        if (okFunction != null) okFunction.ok(getText());
        dispose();
    }

    private void resize() {
        scrollPane.setSize(getWidth(), getHeight() - 80);

        buttonOk.setSize(100, 20);
        buttonCancel.setSize(100, 20);

        buttonOk.setLocation(10, getHeight() - 50);
        buttonCancel.setLocation(getWidth() - buttonCancel.getWidth() - 10, getHeight() - 50);
    }


    public void setText(String text) {
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }

    public OkFunction getOkFunction() {
        return okFunction;
    }

    public void setOkFunction(OkFunction okFunction) {
        this.okFunction = okFunction;
    }

    public static void create(Room room, OkFunction okFunction) {
        DataEntry dataEntry = getProperties(room);
        dataEntry.add(DataKey.BACKGROUND_COLOR, room.getBackgroundColor().toHexString());

        String oldId = room.getId();

        create("Room " + room.getId(), split(dataEntry.stringify()), text -> {
            DataEntry dataEntryToSet = DataEntry.newInstance(collect(text));

            String newId = dataEntryToSet.getString(DataKey.ID);

            GameMap map = room.getMap();

            if (map.getRoom(newId) != null && map.getRoom(newId) != room) {
                JOptionPane.showMessageDialog(null, "Room with id " + newId + " is already exists");
                throw new IllegalStateException("Room with id " + newId + " is already exists");
            }

            if (!newId.equals(oldId)) {
                map.removeRoom(room);
            }

            setProperties(room, dataEntryToSet);

            room.setBackgroundColor(new Color(dataEntryToSet.getString(DataKey.BACKGROUND_COLOR)));

            if (!newId.equals(oldId)) {
                map.putRoom(room);
            }

            okFunction.ok(text);
        });
    }

    public static @NotNull JPropertiesEditor create(String title, String text, OkFunction okFunction) {
        JPropertiesEditor editor = new JPropertiesEditor();

        SwingUtilities.invokeLater(() -> {
            editor.setText(text);
            editor.setTitle(title + " (floating)");
            editor.setOkFunction(okFunction);
            editor.setVisible(true);
            editor.setSize(editor.getSize());
            editor.repaint();
        });

        return editor;
    }

    public static void create(@NotNull IGameObject gameObject) {
        create(gameObject.toString(), split(getProperties(gameObject).stringify()), text -> {
            DataEntry dataEntry = DataEntry.newInstance(collect(text));

            GameMap map = gameObject.getWorld().getMap();

            map.getAllGameObjectsFromAllRooms().forEach(current -> {
                if (Objects.equals(current.getName(), dataEntry.getString(DataKey.NAME)) && gameObject != current) {
                    //JOptionPane.showMessageDialog(null, "Error: duplicate game object name " + current.getName());
                    throw new IllegalStateException("duplicate game object name " + current.getName());
                }
            });


            setProperties(gameObject, dataEntry);
        });
    }

    @Contract(pure = true)
    private static @NotNull String split(@NotNull String string) {
        return string.replaceAll(" \\| ", "\n");
    }

    @Contract(pure = true)
    private static @NotNull String collect(@NotNull String string) {
        return string.replace("\n", " | ");
    }

    @FunctionalInterface
    public interface OkFunction {
        void ok(String text);
    }
}
