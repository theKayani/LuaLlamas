package com.hk.spigot.lua.entity;

import org.bukkit.entity.Player;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.spigot.lua.ItemStackUserdata;

public class PlayerUserdata extends LivingEntityUserdata
{
	public final Player player;

	protected PlayerUserdata(Player player)
	{
		super(player);

		this.player = player;
		
		metatable = playerMetatable;
	}
	
	private static LuaObject sendMessage(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof PlayerUserdata))
			throw new LuaException("bad argument #1 to 'sendMessage' (PLAYER* expected)");

		PlayerUserdata data = (PlayerUserdata) args[0];

		data.player.sendMessage(args[0].getString());
		
		return Lua.nil();
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
				return new ItemStackUserdata(player.getInventory().getItemInMainHand());
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
				if(!(value instanceof ItemStackUserdata))
					throw new LuaException("Expected heldItem to be of type ITEMSTACK*");
				player.getInventory().setItemInMainHand(((ItemStackUserdata) value).stack);
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
		LuaObject tbl = LivingEntityUserdata.metatable();
		tbl.rawSet("__name", "PLAYER*");
		
		tbl.rawSet("sendMessage", Lua.newFunc(PlayerUserdata::sendMessage));
		
		return tbl;
	}
	
	private static final LuaObject playerMetatable = metatable();
}
