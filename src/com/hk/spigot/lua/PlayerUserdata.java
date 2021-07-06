package com.hk.spigot.lua;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaUserdata;

public class PlayerUserdata extends LuaUserdata
{
	private final Player player;

	public PlayerUserdata(Player player)
	{
		this.player = player;
	}

	@Override
	public String getString(LuaInterpreter interp)
	{
		return "Player[" + player.getName() + "]";
	}
	
	@Override
	public LuaObject doIndex(LuaInterpreter interp, LuaObject key)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
			case "health":
				return Lua.newNumber(player.getHealth());
			case "posX":
				return Lua.newNumber(player.getLocation().getX());
			case "posY":
				return Lua.newNumber(player.getLocation().getY());
			case "posZ":
				return Lua.newNumber(player.getLocation().getZ());
			case "xp":
				return Lua.newNumber(player.getExp());
			case "heldItem":
				return Lua.newString(player.getInventory().getItemInMainHand().getType().name());
			case "flying":
				return Lua.newBoolean(player.isFlying());
			case "name":
				return Lua.newString(player.getName());
			}
		}
		return super.doIndex(interp, key);
	}

	@Override
	public void doNewIndex(LuaInterpreter interp, LuaObject key, LuaObject value)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
			case "health":
				player.setHealth(value.getFloat());
				return;
			case "posX":
				player.getLocation().setX(value.getFloat());
				return;
			case "posY":
				player.getLocation().setY(value.getFloat());
				return;
			case "posZ":
				player.getLocation().setZ(value.getFloat());
				return;
			case "xp":
				player.setExp((float) value.getFloat());
				return;
			case "heldItem":
				player.getInventory().setItemInMainHand(new ItemStack(Material.valueOf(value.getString())));
				return;
			case "flying":
				player.setFlying(value.getBoolean());
				return;
			case "name":
				throw new LuaException("Cannot change immutable player.name");
			}
		}
		super.doNewIndex(interp, key, value);
	}

	@Override
	public String name()
	{
		return "*PLAYER";
	}

	@Override
	public Player getUserdata()
	{
		return player;
	}
}
