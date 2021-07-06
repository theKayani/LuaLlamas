package com.hk.spigot;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaObject;
import com.hk.math.Rand;
import com.hk.spigot.lua.PlayerUserdata;
import com.hk.spigot.lua.WorldLibrary;

public class LuaLlamas extends JavaPlugin implements Listener
{
	@Override
    public void onEnable()
	{
		Cmd cmd = new Cmd();
		getCommand("lua").setExecutor(cmd);
		
		getServer().getPluginManager().registerEvents(cmd, this);
		
		getLogger().info("Hello fellow plugins!");
		
		for(Plugin plugin : getServer().getPluginManager().getPlugins())
		{
			if(plugin != this)
				plugin.getLogger().info("Hi there LuaLlamas!");
		}
    }

    @Override
    public void onDisable()
    {
		getLogger().info("Stopping LuaLlamas!");
    }
    
	public static final String GENERATED_LLAMA_KEY = "snc_generated_llama";
	public static final Material[] CARPETS = {
			Material.RED_CARPET,
			Material.GREEN_CARPET,
			Material.BLUE_CARPET,
			Material.WHITE_CARPET,
			Material.BLACK_CARPET,
			
			Material.PURPLE_CARPET,
			Material.BROWN_CARPET,
			Material.CYAN_CARPET,
			Material.YELLOW_CARPET,
			Material.ORANGE_CARPET,

			Material.GRAY_CARPET,
			Material.LIGHT_BLUE_CARPET,			
			Material.LIME_CARPET,
			Material.MAGENTA_CARPET,
			Material.PINK_CARPET,
	};
	public static final Material[] WOOLS = {
			Material.RED_WOOL,
			Material.GREEN_WOOL,
			Material.BLUE_WOOL,
			Material.WHITE_WOOL,
			Material.BLACK_WOOL,
			
			Material.PURPLE_WOOL,
			Material.BROWN_WOOL,
			Material.CYAN_WOOL,
			Material.YELLOW_WOOL,
			Material.ORANGE_WOOL,

			Material.GRAY_WOOL,
			Material.LIGHT_BLUE_WOOL,			
			Material.LIME_WOOL,
			Material.MAGENTA_WOOL,
			Material.PINK_WOOL,
	};

	private class Cmd implements CommandExecutor, Listener
    {
		@EventHandler
		public void onInventoryClickEvent(InventoryClickEvent event)
		{
			if(event.getClickedInventory() != null)
			{
				InventoryHolder holder = event.getView().getTopInventory().getHolder();
				if(holder instanceof Llama && ((Llama) holder).hasMetadata(GENERATED_LLAMA_KEY))
				{
					Llama llama = (Llama) holder;
					if(event.getClickedInventory() == event.getView().getTopInventory())
					{
						if(event.getSlot() >= 2 && event.getSlot() <= 16)
						{
							LlamaController controller = (LlamaController) llama.getMetadata(GENERATED_LLAMA_KEY).get(0).value();
							
							controller.trigger(event.getSlot() - 2);
						}
					}
					event.setCancelled(true);
				}
			}
		}
		
		@EventHandler
    	public void onPlayerDropItemEvent(PlayerDropItemEvent event)
    	{
    		if(event.getItemDrop().getItemStack().getType() == Material.NETHER_STAR)
    		{
    			Player player = event.getPlayer();
    			Item item = event.getItemDrop();
    			new BukkitRunnable() {
    				int count = 0;

					@Override
					public void run()
					{
						if(!item.isDead() && item.isValid())
						{
							if(item.getVelocity().length() < 0.1)
							{
								count++;
								
								if(count >= 40)
								{
									Block b = item.getLocation().getBlock();
									
									if(b.getType() == Material.CHEST)
									{
										Chest chest = (Chest) b.getState();
										Inventory inv = chest.getInventory();
										
										ItemStack[] llamaInv = new ItemStack[17];
										
										llamaInv[1] = new ItemStack(Rand.<Material>nextFrom(CARPETS));

										for(int i = 2; i < llamaInv.length; i++)
											llamaInv[i] = new ItemStack(WOOLS[i - 2]);

										List<String> files = new LinkedList<>();
										for(ItemStack stack : inv.getContents())
										{
											if(stack != null)
											{
												if(stack.getType() == Material.WRITABLE_BOOK)
													files.add(String.join("\n", ((BookMeta) stack.getItemMeta()).getPages()));
												else
													player.getWorld().dropItemNaturally(b.getLocation(), stack);
											}
										}
										inv.clear();

										b.setType(Material.AIR);

										Location loc = b.getLocation();
										loc = loc.add(0.5, 0.5, 0.5);
										Llama llama = (Llama) player.getWorld().spawnEntity(loc, EntityType.LLAMA);
										llama.setCarryingChest(true);
										llama.setOwner(player);
										llama.setStrength(5);
//										llama.setAI(false);
										llama.setAdult();										
										llama.getInventory().setContents(llamaInv);
										llama.setCustomName(player.getDisplayName() + "'s Nether Llama");
										
										LlamaController controller = new LlamaController(player, llama, files);
										llama.setMetadata(GENERATED_LLAMA_KEY, new FixedMetadataValue(LuaLlamas.this, controller));
										
										controller.runTaskTimer(LuaLlamas.this, 20, 20);
									}
									
									cancel();
								}
							}
							else
								count = 0;
						}
						else
							cancel();
					}
    			}.runTaskTimer(LuaLlamas.this, 0, 0);
    		}
    	}
    	
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{
			if(sender instanceof Player && command.getName().startsWith("lua"))
			{
				Player player = (Player) sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				
				if(item.getType() == Material.WRITABLE_BOOK)
				{
					BookMeta meta = (BookMeta) item.getItemMeta();
					
					LuaInterpreter interp = Lua.interpreter();
					interp.setExtra("player", player);
					interp.setExtra("world", player.getWorld());
					
					interp.importLib(LuaLibrary.BASIC);
					interp.importLib(LuaLibrary.COROUTINE);
					interp.importLib(LuaLibrary.STRING);
					interp.importLib(LuaLibrary.TABLE);
					interp.importLib(LuaLibrary.MATH);
					interp.importLib(LuaLibrary.IO);
					interp.importLib(LuaLibrary.OS);
			
					interp.importLib(LuaLibrary.JSON);
					interp.importLib(LuaLibrary.HASH);
//					interp.importLib(LuaLibrary.DATE);
					interp.importLib(WorldLibrary.INS);
					
					Environment env = interp.getGlobals();
					
					env.setVar("player", new PlayerUserdata(player));
					
					env.setVar("print", Lua.newFunc((interp2, args2) -> {
						for(LuaObject arg2 : args2)
							player.sendMessage(arg2.getString());
						
						return Lua.nil();
					}));
					
					for(String page : meta.getPages())
						interp.require(page);
				}
				
				return true;
			}
			
			return false;
		}
    }
}
