package dev.jones.doorlock.util;

import com.sun.istack.internal.NotNull;
import dev.jones.doorlock.Doorlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemStackBuilder {
    private String name;
    private Material material;
    private List<String> lore;
    private Hashtable<String,String> tags=new Hashtable<>();
    private ItemStack stack;

    public ItemStackBuilder(Material material){
        this.material=material;
    }
    public ItemStackBuilder(@NotNull ItemStack stack){
        this.material=stack.getType();
        this.name=Objects.requireNonNull(stack.getItemMeta()).getDisplayName();
        this.lore=stack.getItemMeta().getLore();
        this.stack=stack;
    }

    public ItemStackBuilder setName(String name) {
        this.name=name;
        return this;
    }
    public ItemStackBuilder setLore(String... lore){
        this.lore=Arrays.asList(lore);
        return this;
    }
    public ItemStackBuilder addNbtTag(String key,String value){
        tags.put(key,value);
        return this;
    }

    public ItemStack build(){
        ItemStack stack=this.stack;
        if(stack==null) {
            stack = new ItemStack(material);
        }
        ItemMeta meta=stack.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        PersistentDataContainer container=meta.getPersistentDataContainer();
        tags.forEach((key,value)->{
            NamespacedKey nkey=new NamespacedKey(Doorlock.getInstance(), key);
            meta.getPersistentDataContainer().set(nkey,PersistentDataType.STRING,value);
        });
        stack.setItemMeta(meta);
        return stack;
    }

}
