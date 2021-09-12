package dev.jones.doorlock.command;

import dev.jones.doorlock.Doorlock;
import dev.jones.doorlock.util.SaveUtil;
import dev.jones.doorlock.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;

public class DoorlockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==1){
            if(args[0].equalsIgnoreCase("version")){
                sender.sendMessage("§cDoorlock by _joones is installed on this server");
                sender.sendMessage("§7Plugin version: "+ SaveUtil.getVersion());
                return true;
            }else if(args[0].equalsIgnoreCase("update")){
                sender.sendMessage("§cChecking for updates...");
                sender.sendMessage("§7Look in the console for more information.");
                Updater.fetchUpdates();
                return true;
            }else if(args[0].equalsIgnoreCase("reload")){
                sender.sendMessage("§cReloading the plugin...");
                sender.sendMessage("§7This can take a little while.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(Doorlock.getInstance(),()->{
                    Doorlock.getRecipes().forEach(Doorlock.getInstance().getServer()::removeRecipe);
                    Doorlock.getInstance().onEnable();
                },1);

                return true;
            }
        }
        return false;
    }
}
