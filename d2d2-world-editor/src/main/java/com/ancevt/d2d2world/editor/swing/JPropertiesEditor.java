/*
 *   D2D2 World Editor
 *   Copyright (C) 2022 Ancevt (i@ancevt.ru)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ancevt.d2d2world.editor.swing;

import com.ancevt.d2d2.display.Color;
import com.ancevt.d2d2world.data.DataEntry;
import com.ancevt.d2d2world.data.DataKey;
import com.ancevt.d2d2world.gameobject.IGameObject;
import com.ancevt.d2d2world.map.GameMap;
import com.ancevt.d2d2world.map.Room;
import com.ancevt.d2d2world.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

import static com.ancevt.d2d2world.data.Properties.getProperties;
import static com.ancevt.d2d2world.data.Properties.setProperties;

public class JPropertiesEditor extends JFrame implements ActionListener {

    public static void main(String[] args) {
        create("(floating)", "some default text", null);
    }

    private final JTextArea textArea;
    private final JButton buttonCancel;
    private final JButton buttonOK;

    private OkFunction okFunction;

    public JPropertiesEditor() {
        setLocationByPlatform(true);
        setPreferredSize(new Dimension(1000, 600));
        setSize(new Dimension(1000, 600));
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        scrollPane.setPreferredSize(new Dimension(200, 530));

        buttonCancel = new JButton("Cancel");
        buttonOK = new JButton("OK");

        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) ok();
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
            }
        });

        // textArea.setPreferredSize(new Dimension(272, 220));
        final GridBagLayout gbl = new GridBagLayout();
        getContentPane().setLayout(gbl);

        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 10;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 8;
        c.insets = new Insets(10, 10, 5, 10);
        getContentPane().add(scrollPane, c);

        c.anchor = GridBagConstraints.SOUTHWEST;
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 10, 10);
        getContentPane().add(buttonOK, c);

        c.anchor = GridBagConstraints.SOUTHEAST;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 2;
        c.gridy = 4;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 10, 10);
        getContentPane().add(buttonCancel, c);

        pack();

        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);

        textArea.requestFocus();
    }


    public void setOkFunction(OkFunction okFunction) {
        this.okFunction = okFunction;
    }

    public OkFunction getOkFunction() {
        return okFunction;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JButton button = (JButton) e.getSource();

        if (button == buttonOK) {
            ok();
        } else {
            dispose();
        }
    }

    @Override
    public void dispose() {
        buttonOK.removeActionListener(this);
        buttonCancel.removeActionListener(this);
        setVisible(false);
    }

    private void ok() {
        if (okFunction != null) okFunction.ok(getText());
        dispose();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }

    public static JPropertiesEditor create(String title, String text, OkFunction okFunction) {
        //SwingUtilities.invokeLater(() -> {
        JPropertiesEditor textEditor = new JPropertiesEditor();
        textEditor.setText(text);
        textEditor.setTitle(title + " (floating)");
        textEditor.setOkFunction(okFunction);
        //textWindow.setLocationByPlatform(true);
        textEditor.setVisible(true);
        //});

        return textEditor;
    }

    public static void create(World world, IGameObject gameObject) {
        create(gameObject.toString(), split(getProperties(gameObject).stringify()), text -> {
            DataEntry dataEntry = DataEntry.newInstance(collect(text));

            GameMap map = world.getMap();

            map.getAllGameObjectsFromAllRooms().forEach(current -> {
                if (Objects.equals(current.getName(), dataEntry.getString(DataKey.NAME)) && gameObject != current) {
                    JOptionPane.showMessageDialog(null, "Error: duplicate game object name " + current.getName());
                    throw new IllegalStateException("duplicate game object name " + current.getName());
                }
            });


            setProperties(gameObject, dataEntry);
        });
    }

    public static void create(Room room, OkFunction okFunction) {

        DataEntry dataEntry = getProperties(room);
        dataEntry.add(DataKey.BACKGROUND_COLOR, room.getBackgroundColor().toHexString());

        String oldId = room.getName();

        create("Room " + room.getName(), split(dataEntry.stringify()), text -> {
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

    private static String split(String string) {
        return string.replaceAll(" \\| ", "\n");
    }

    private static String collect(String string) {
        return string.replace("\n", " | ");
    }

    @FunctionalInterface
    public interface OkFunction {
        void ok(String text);
    }

}































