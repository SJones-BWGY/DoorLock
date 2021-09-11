package dev.jones.doorlock.command;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[0].equalsIgnoreCase("getitemtags")){
            Player p=(Player) sender;
            p.sendMessage("§c§lThe item in your hand has the following keys in its PersistentDataContainer:");
            PersistentDataContainer container= Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getPersistentDataContainer();
            for (NamespacedKey key : container.getKeys()) {
                p.sendMessage("§4"+key.getKey()+ "§c: "+container.get(key, PersistentDataType.STRING));
            }
            p.sendMessage("§c§l-----------");
        }
        return false;
    }
}
