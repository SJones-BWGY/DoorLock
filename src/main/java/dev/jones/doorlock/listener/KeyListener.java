package dev.jones.doorlock.listener;

import dev.jones.doorlock.util.DoorlockHearbeat;
import dev.jones.doorlock.util.ItemStackBuilder;
import dev.jones.doorlock.util.SaveUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KeyListener implements Listener {
    private static List<Player> timeout=new ArrayList<>();
    @EventHandler
    public void onCraft(CraftItemEvent e){
        e.getRecipe().getResult().getItemMeta().getPersistentDataContainer().getKeys().forEach(key->{
            if(key.getKey().equalsIgnoreCase("isKey")){
                e.setCurrentItem(new ItemStackBuilder(e.getRecipe().getResult()).addNbtTag("key", UUID.randomUUID().toString()).build());
            }
        });
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(timeout.contains(e.getPlayer()))return;
        timeout.add(e.getPlayer());
        DoorlockHearbeat.queueRunnable(()->{
            timeout.remove(e.getPlayer());
        });
        Location door=null;
        if(e.getClickedBlock()==null)return;
        try {
            Door d = (Door) e.getClickedBlock().getBlockData();
            if(d.getHalf()==Bisected.Half.BOTTOM){
                door=e.getClickedBlock().getLocation();
            }else{
                door=e.getClickedBlock().getLocation().subtract(0,1,0);
            }
        }catch (ClassCastException ex){
            try {
                TrapDoor d = (TrapDoor) e.getClickedBlock().getBlockData();
                door=e.getClickedBlock().getLocation();
            }catch (ClassCastException ignored){
                /*
                 * No Door Clicked
                 */
            }
        }catch (Exception ignored){
            /*
             *  Invalid block clicked
             */
        }
        if(SaveUtil.isLockable(e.getClickedBlock().getLocation())){
            door=e.getClickedBlock().getLocation();
        }
        if(door==null)return;
        boolean locked=false;
        if(SaveUtil.getKey(door)!=null) {
            e.setCancelled(true);
            locked=true;
        }
        if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta()==null){
            if(locked){
                e.getPlayer().sendMessage("§cYou need the correct key to interact with this block!");
            }
            return;
        }
        if(e.getClickedBlock()==null)return;
        PersistentDataContainer container=e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer();
        boolean cont=false;
        String key=null;
        for (NamespacedKey nkey : container.getKeys()) {
            if(nkey.getKey().equalsIgnoreCase("key")){
                key=container.get(nkey, PersistentDataType.STRING);
            }else if(nkey.getKey().equalsIgnoreCase("isdoordrill")){
                if(e.getAction()!=Action.LEFT_CLICK_BLOCK){
                    e.getPlayer().sendMessage("§cTo unlock a block you have to break it.");
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(false);
                return;
            }
        }
        if(key==null){
            key="missing";
        }
        if(key.equals(SaveUtil.getKey(door))){
            e.setCancelled(false);
            if(e.getPlayer().isSneaking()&&e.getAction()==Action.RIGHT_CLICK_BLOCK){
                SaveUtil.unlockDoor(door);
                e.getPlayer().sendMessage("§aBlock unlocked.");
            }
        }else if(SaveUtil.getKey(door)==null&&!key.equals("missing")){
            SaveUtil.lockDoor(key,door);
            e.getPlayer().sendMessage("§aBlock locked.");
            e.setCancelled(true);
        }else if(SaveUtil.getKey(door)==null){
            /*
            Ignore if door has no key
             */
        }else{
            e.getPlayer().sendMessage("§cYou need the correct key to interact with this block!");
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Location door=null;
        try {
            Door d = (Door) e.getBlock().getBlockData();
            if(d.getHalf()==Bisected.Half.BOTTOM){
                door=e.getBlock().getLocation();
            }else{
                door=e.getBlock().getLocation().subtract(0,1,0);
            }
        }catch (ClassCastException ex){
            try {
                TrapDoor d = (TrapDoor)e.getBlock().getBlockData();
                door=e.getBlock().getLocation();
            }catch (ClassCastException ignored){
                /*
                 * No Door Clicked
                 */
            }
        }catch (Exception ignored){
            /*
             *  Invalid block clicked
             */
        }
        if(door==null){
            if(SaveUtil.isLockable(e.getBlock().getLocation())){
                door=e.getBlock().getLocation();
            }else{
                return;
            }
        }
        SaveUtil.unlockDoor(door);
        SaveUtil.disableLocking(door);

    }
}
