package com.hk.spigot.lua;

import org.bukkit.inventory.ItemStack;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaUserdata;

public class ItemStackUserdata extends LuaUserdata
{
	private final ItemStack stack;
	
	public ItemStackUserdata(ItemStack stack)
	{
		this.stack = stack;
		
		metatable = itemstackMetatable;
	}

	@Override
	public String getString(LuaInterpreter interp)
	{
		return stack.toString();
	}
	
	private static LuaObject clone(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof ItemStackUserdata))
			throw new LuaException("bad argument #1 to 'clone' (ITEMSTACK* expected)");

		ItemStackUserdata data = (ItemStackUserdata) args[0];
		
		return new ItemStackUserdata(data.stack.clone());
	}
	
	@Override
	public LuaObject doIndex(LuaInterpreter interp, LuaObject key)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
			case "amount":
				return Lua.newNumber(stack.getAmount());
			case "type":
				return Lua.newString(stack.getType().name());
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
			case "amount":
				stack.setAmount((int) value.getInteger());
				return;
			case "type":
				throw new LuaException("cannot change value of itemstack." + key.getString());
			}
		}
		
		super.doNewIndex(interp, key, value);
	}

	@Override
	public String name()
	{
		return "ITEMSTACK*";
	}

	@Override
	public ItemStack getUserdata()
	{
		return stack;
	}
	
	private static final LuaObject itemstackMetatable = Lua.newTable();
	
	static
	{
		itemstackMetatable.rawSet("__name", "ITEMSTACK*");
		itemstackMetatable.rawSet("__index", itemstackMetatable);

		itemstackMetatable.rawSet("clone", Lua.newFunc(ItemStackUserdata::clone));
	}
}
