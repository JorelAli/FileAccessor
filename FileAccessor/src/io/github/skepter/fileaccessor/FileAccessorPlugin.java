package io.github.skepter.fileaccessor;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FileAccessorPlugin extends JavaPlugin implements Listener {
	
	private FileAccessor<Block> file;
	private Set<Block> blocks;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("saveblocks").setExecutor(this);
		getCommand("getblocks").setExecutor(this);
		
		blocks = new HashSet<Block>();
		file = new FileAccessor<Block>(this, "blocks.txt", "") {

			@Override
			public String serialize(Block type) {
				return type.getWorld().getName() + ":" + type.getLocation().getBlockX() + ":" + type.getLocation().getBlockY() + ":" + type.getLocation().getBlockZ();
			}

			@Override
			public Block deserialize(String s) {
				String[] args = s.split(":");
				return Bukkit.getWorld(args[0]).getBlockAt(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
		};
	}		
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("saveblocks")) {
			file.store(blocks);
			sender.sendMessage("blocks stored");
			return true;
		} else if(label.equalsIgnoreCase("getblocks")) {
			for(Block b : file.get()) {
				b.setType(Material.GOLD_BLOCK);
			}
			sender.sendMessage("blocks retrieved");
			return true;
		}
		return false;
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		blocks.add(e.getBlock());
	}
}
