package com.ancevt.d2d2world.client.ui;

import com.ancevt.d2d2world.client.settings.MonitorManager;

public class MonitorChooser extends Chooser<Long> {

    public MonitorChooser() {
        setWidth(180f);
        MonitorManager.getInstance().getMonitors().forEach((id, name) -> {
            addItem(name, id);
        });
    }
}
