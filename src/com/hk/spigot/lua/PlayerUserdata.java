package com.hk.spigot.lua;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;

public class PlayerUserdata extends EntityUserdata
{
	private final Player player;

	public PlayerUserdata(Player player)
	{
		super(player);

		this.player = player;
		
		metatable = playerMetatable;
	}
	
	@Override
	public LuaObject doIndex(LuaInterpreter interp, LuaObject key)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
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
		return "PLAYER*";
	}

	@Override
	public Player getUserdata()
	{
		return player;
	}
	
	public static LuaObject metatable()
	{
		LuaObject tbl = EntityUserdata.metatable();
		tbl.rawSet("__name", "PLAYER*");
		
		tbl.rawSet("whutUp", Lua.newString("test one two three"));
		
		return tbl;
	}
	
	private static final LuaObject playerMetatable = metatable();
}
