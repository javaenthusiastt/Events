package org.liam.echoBoxEvent.maps;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class GlassBridgeMapReset {

    public static final Map<Location, Material> redblocks = new HashMap<>();
    public static final Map<Location, Material> greenblocks = new HashMap<>();

    public static void resetRedBlocks() {
        if (!redblocks.isEmpty()) {
            for (Map.Entry<Location, Material> entry : redblocks.entrySet()) {
                Location loc = entry.getKey();
                Material originalMaterial = entry.getValue();

                Block block = loc.getBlock();
                block.setType(originalMaterial);
            }
            redblocks.clear();
        }
    }

    public static void resetGreenBlocks() {
        if (!greenblocks.isEmpty()) {
            for (Map.Entry<Location, Material> entry : greenblocks.entrySet()) {
                Location loc = entry.getKey();
                Material originalMaterial = entry.getValue();
                Block block = loc.getBlock();
                block.setType(originalMaterial);
            }
            greenblocks.clear();
        }
    }
}
