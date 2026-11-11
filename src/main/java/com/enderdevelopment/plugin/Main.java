package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;

public final class Main extends JavaPlugin implements Listener, CommandExecutor {
    private NamespacedKey itemKey;
    private NamespacedKey displayKey;

    @Override
    public void onEnable() {
        itemKey = new NamespacedKey(this, "poplar_log");
        displayKey = new NamespacedKey(this, "poplar_display");
        Bukkit.getPluginManager().registerEvents(this, this);
        if (getCommand("poplarlog") != null) {
            getCommand("poplarlog").setExecutor(this);
        }
    }

    @Override
    public void onDisable() {
        for (Entity entity : Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntitiesByClass(BlockDisplay.class).stream())
                .toList()) {
            if (isPoplarDisplay(entity)) {
                entity.remove();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can receive this item.", NamedTextColor.RED));
            return true;
        }
        player.getInventory().addItem(createPoplarItem());
        player.sendMessage(Component.text("You received a poplar log display block.", NamedTextColor.GREEN));
        return true;
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || !isPoplarItem(event.getItem()) || event.getClickedBlock() == null) {
            return;
        }
        Block target = event.getClickedBlock().getRelative(event.getBlockFace());
        if (!target.getType().isAir()) {
            return;
        }
        event.setCancelled(true);
        BlockDisplay display = (BlockDisplay) target.getWorld().spawnEntity(
                target.getLocation(), EntityType.BLOCK_DISPLAY);
        display.setBlock(getPoplarBlockData());
        display.getPersistentDataContainer().set(displayKey, PersistentDataType.BYTE, (byte) 1);
        display.setPersistent(true);
        consumeHeldItem(event.getPlayer());
    }

    @EventHandler
    public void onDisplayInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!isPoplarDisplay(entity)) {
            return;
        }
        event.setCancelled(true);
        entity.remove();
        event.getPlayer().getInventory().addItem(createPoplarItem());
        event.getPlayer().sendMessage(Component.text("You picked up the poplar log display block.", NamedTextColor.YELLOW));
    }

    @EventHandler
    public void onDisplayDamage(EntityDamageEvent event) {
        if (!isPoplarDisplay(event.getEntity())) {
            return;
        }
        event.setCancelled(true);
        event.getEntity().remove();
    }

    private ItemStack createPoplarItem() {
        ItemStack item = new ItemStack(Material.BIRCH_LOG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Poplar Log Display", NamedTextColor.GOLD));
            meta.lore(java.util.List.of(Component.text("Place to display a poplar log.", NamedTextColor.GRAY)));
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    private BlockData getPoplarBlockData() {
        return Material.BIRCH_LOG.createBlockData();
    }

    private boolean isPoplarItem(ItemStack item) {
        if (item == null || item.getType() != Material.BIRCH_LOG || !item.hasItemMeta()) {
            return false;
        }
        Byte marker = item.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.BYTE);
        return marker != null && marker == (byte) 1;
    }

    private boolean isPoplarDisplay(Entity entity) {
        Byte marker = entity.getPersistentDataContainer().get(displayKey, PersistentDataType.BYTE);
        return entity instanceof BlockDisplay && marker != null && marker == (byte) 1;
    }

    private void consumeHeldItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (player.getGameMode().isSurvival() || player.getGameMode().isAdventure()) {
            item.setAmount(item.getAmount() - 1);
        }
    }
}
