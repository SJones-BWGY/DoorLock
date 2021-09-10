package dev.jones.doorlock.listener;

import dev.jones.doorlock.util.DoorlockHearbeat;
import dev.jones.doorlock.util.SaveUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;

public class BlockClaimerListener implements Listener {
    List<Player> timeout=new ArrayList<>();
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(timeout.contains(e.getPlayer())){
            e.setCancelled(true);
            return;
        }
        timeout.add(e.getPlayer());
        DoorlockHearbeat.queueRunnable(()->{
            timeout.remove(e.getPlayer());
        });

        if(e.getClickedBlock()==null)return;
        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta()!=null){
            PersistentDataContainer container=e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer();
            boolean cont=false;
            for (NamespacedKey key : container.getKeys()) {
                if(key.getKey().equals("isblocklocker"))cont=true;
            }
            if(!cont)return;
            e.setCancelled(true);
            if(SaveUtil.isLockable(e.getClickedBlock().getLocation())&&SaveUtil.getKey(e.getClickedBlock().getLocation())==null){
                SaveUtil.disableLocking(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage("§a§lThe block is no longer lockable!");
            }else if(!SaveUtil.isLockable(e.getClickedBlock().getLocation())&&SaveUtil.getKey(e.getClickedBlock().getLocation())==null){
                SaveUtil.enableLocking(e.getClickedBlock().getLocation());
                e.getPlayer().sendMessage("§a§lThe block is now lockable!");
            }else{
                e.getPlayer().sendMessage("§c§lThis block is currently locked!");
            }
        }
    }

}
