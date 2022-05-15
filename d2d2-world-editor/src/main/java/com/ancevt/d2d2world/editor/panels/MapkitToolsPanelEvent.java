
package com.ancevt.d2d2world.editor.panels;

import com.ancevt.d2d2.event.Event;
import com.ancevt.d2d2world.mapkit.MapkitItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MapkitToolsPanelEvent extends Event<MapkitToolsPanel> {

    public static final String MAPKIT_ITEM_SELECT = "mapkitItemSelect";

    private final MapkitItem mapkitItem;
}
