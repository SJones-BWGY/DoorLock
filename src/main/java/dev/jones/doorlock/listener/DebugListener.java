package dev.jones.doorlock.listener;

import dev.jones.doorlock.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DebugListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.getPlayer().getInventory().addItem(new ItemStackBuilder(Material.GREEN_WOOL).setName("§a§lGrün").setLore("§aDieser Block ist grün.","§awow").addNbtTag("debug","debug").build());

    }
}
