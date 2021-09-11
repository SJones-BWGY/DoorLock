package dev.jones.doorlock;

import dev.jones.doorlock.command.DebugCommand;
import dev.jones.doorlock.command.DisabledCommand;
import dev.jones.doorlock.command.DoorlockCommand;
import dev.jones.doorlock.command.DoorlockCommandTabCompleter;
import dev.jones.doorlock.listener.BlockClaimerListener;
import dev.jones.doorlock.listener.DebugListener;
import dev.jones.doorlock.listener.KeyListener;
import dev.jones.doorlock.util.DoorlockHearbeat;
import dev.jones.doorlock.util.ItemStackBuilder;
import dev.jones.doorlock.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class Doorlock extends JavaPlugin {
    private static Plugin instance;
    private static List<NamespacedKey> recipes=new ArrayList<>();
    private static File file;
    private static final boolean DEBUG=false;
    @Override
    public void onEnable() {
        /*
        Initialize Variables
         */
        instance=this;
        file=this.getFile();
        /*
        Load config
         */
        this.saveDefaultConfig();
        for (String key : this.getConfig().getDefaults().getKeys(true)) {
            if(!this.getConfig().contains(key,true)){
                this.getLogger().warning("Config path "+key+" is missing! Adding it.");
                this.getConfig().set(key,this.getConfig().getDefaults().get(key));
            }
        }
        this.saveConfig();
        /*
        Scan for updates
         */
        if(this.getConfig().getLong("update")==0&&Updater.fetchUpdates()){
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        /*
        Register Generic Commands
         */
        if(DEBUG) {
            Bukkit.getPluginManager().registerEvents(new DebugListener(), this);
            this.getCommand("dldebug").setExecutor(new DebugCommand());
        }else{
            this.getCommand("dldebug").setExecutor(new DisabledCommand());
        }

        this.getCommand("doorlock").setExecutor(new DoorlockCommand());
        this.getCommand("doorlock").setTabCompleter(new DoorlockCommandTabCompleter());

        /*
        Register Key
         */
        ItemStack keyItem=new ItemStackBuilder(Material.GOLD_NUGGET)
                .setName("§a§lKey")
                .setLore("§7Locks doors.","§7(Shift-Click to unlock)")
                .addNbtTag("isKey","1")
                .build();

        NamespacedKey keyKey=new NamespacedKey(this,"key");
        recipes.add(keyKey);

        ShapedRecipe keyRecipe=new ShapedRecipe(keyKey,keyItem);
        keyRecipe.shape("GNN");
        keyRecipe.setIngredient('G', Material.GOLD_INGOT);
        keyRecipe.setIngredient('N', Material.GOLD_NUGGET);

        if(getConfig().getBoolean("items.key")) {
            Bukkit.addRecipe(keyRecipe);
            Bukkit.getPluginManager().registerEvents(new KeyListener(), this);
        }
        /*
        Register BlockLocker
         */
        ItemStack blockClaimerItem=new ItemStackBuilder(Material.IRON_AXE)
                .setName("§a§lBlock Locker")
                .setLore("§7Makes blocks lockable with keys.")
                .addNbtTag("isBlockLocker","1")
                .build();

        NamespacedKey blockClaimerKey=new NamespacedKey(this,"block_locker");
        recipes.add(blockClaimerKey);

        ShapedRecipe blockClaimerRecipe=new ShapedRecipe(blockClaimerKey,blockClaimerItem);
        blockClaimerRecipe.shape(
                "SGS",
                "IHI",
                "XPX");
        blockClaimerRecipe.setIngredient('S',Material.SAND);
        blockClaimerRecipe.setIngredient('G',Material.GRAVEL);
        blockClaimerRecipe.setIngredient('I',Material.IRON_BLOCK);
        blockClaimerRecipe.setIngredient('H',Material.HOPPER);
        blockClaimerRecipe.setIngredient('P',Material.IRON_PICKAXE);

        if(getConfig().getBoolean("items.blocklocker")) {
            Bukkit.addRecipe(blockClaimerRecipe);
            Bukkit.getPluginManager().registerEvents(new BlockClaimerListener(), this);
        }
        /*
        Register Doordrill
         */
        ItemStack doorDrillItem=new ItemStackBuilder(Material.DIAMOND_AXE)
                .setName("§a§lDoordrill")
                .setLore("§7Can drill through locked doors")
                .addNbtTag("isdoordrill","1")
                .build();
        Damageable doorDrillMeta=(Damageable) doorDrillItem.getItemMeta();
        doorDrillMeta.setDamage(1550);
        doorDrillItem.setItemMeta(doorDrillMeta);

        NamespacedKey doorDrillKey=new NamespacedKey(this,"door_drill");
        recipes.add(doorDrillKey);

        ShapedRecipe doorDrillRecipe=new ShapedRecipe(doorDrillKey,doorDrillItem);

        doorDrillRecipe.shape(
                "BDX",
                "IND",
                "BDX");
        doorDrillRecipe.setIngredient('B',Material.BEACON);
        doorDrillRecipe.setIngredient('D',Material.DIAMOND);
        doorDrillRecipe.setIngredient('I',Material.IRON_BLOCK);
        doorDrillRecipe.setIngredient('N',Material.NETHER_STAR);

        Bukkit.addRecipe(doorDrillRecipe);


        DoorlockHearbeat.start();

    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        recipes.forEach(getServer()::removeRecipe);
        Updater.pluginDisabled();
    }

    public static Plugin getInstance() {
        return instance;
    }
    public static File getJarfile(){return file;}

    public static List<NamespacedKey> getRecipes() {
        return recipes;
    }
}
