package com.hk.spigot;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaObject;
import com.hk.spigot.lua.PotionLibrary;
import com.hk.spigot.lua.StackLibrary;
import com.hk.spigot.lua.WorldLibrary;
import com.hk.spigot.lua.entity.EntityUserdata;

public class LlamaController extends BukkitRunnable
{
	private final Player owner;
	private final World world;
	private final Llama llama;
	private final List<String> files;
	
	private LuaInterpreter interp;
	private int ticks;

	public LlamaController(Player owner, Llama llama, List<String> files)
	{
		this.owner = owner;
		this.llama = llama;
		this.files = files;

		world = owner.getWorld();
		ticks = 0;
	}

	public void trigger(int i)
	{
		LuaObject func = interp.getGlobals().getVar("trigger");

		if(func.isFunction())
		{
			try
			{
				func.callFunction(interp, i);
			}
			catch(LuaException ex)
			{
				owner.sendMessage(ChatColor.RED + ex.getLocalizedMessage() + ChatColor.RESET);
				cancel();
			}
		}
	}
	
	@Override
	public void run()
	{
		if(llama.isDead() || !llama.isValid())
			cancel();

		ticks++;

		if(interp == null)
		{
			interp = Lua.interpreter();
			interp.setExtra("llama", llama);
			interp.setExtra("owner", owner);
			interp.setExtra("player", owner);
			interp.setExtra("world", world);

			LuaLibrary.importStandard(interp);

			interp.importLib(WorldLibrary.INS);
			interp.importLib(PotionLibrary.INS);
			interp.importLib(StackLibrary.INS);
			
			Environment env = interp.getGlobals();
			
			LuaObject blocks = Lua.newTable();
			LuaObject items = Lua.newTable();
			
			for(Material material : Material.values())
			{
				if(material.isBlock())
					blocks.rawSet(material.name(), Lua.newString(material.name()));
				if(material.isItem())
					items.rawSet(material.name(), Lua.newString(material.name()));
			}

			env.setVar("Items", items);
			env.setVar("Blocks", blocks);
			
			env.setVar("owner", EntityUserdata.get(owner));
			env.setVar("llama", EntityUserdata.get(llama));
			
			env.setVar("print", Lua.newFunc((interp2, args) -> {
				for(LuaObject arg : args)
					owner.sendMessage(arg.getString());
				
				return Lua.nil();
			}));

			env.setVar("signal", Lua.newFunc((interp2, args) -> {
				if(args.length > 0)
				{
					boolean unset = true;
					if(args[0].isInteger())
					{
						long l = args[0].getInteger();
						
						if(l >= 0 && l < LuaLlamas.CARPETS.length)
						{
							llama.getInventory().setDecor(new ItemStack(LuaLlamas.CARPETS[(int) l]));
							unset = false;
						}
					}

					if(unset)
						llama.getInventory().setDecor(null);

					return Lua.nil();
				}
				else
				{
					Material type = llama.getInventory().getDecor().getType();
					
					for(int i = 0; i < LuaLlamas.CARPETS.length; i++)
					{
						if(LuaLlamas.CARPETS[i] == type)
							return Lua.newNumber(i);
					}

					return Lua.nil();
				}
			}));

			try
			{
				for(String file : files)
					interp.require(ChatColor.stripColor(file));
			}
			catch(LuaException ex)
			{
				owner.sendMessage(ChatColor.RED + ex.getLocalizedMessage() + ChatColor.RESET);
				cancel();
			}
			
			ticks -= 2;
		}
		else
		{
			LuaObject func = interp.getGlobals().getVar("tick");

			if(func.isFunction())
			{
				try
				{
					func.callFunction(interp, ticks);
				}
				catch(LuaException ex)
				{
					owner.sendMessage(ChatColor.RED + ex.getLocalizedMessage() + ChatColor.RESET);
					cancel();
				}
			}
		}
	}
}
