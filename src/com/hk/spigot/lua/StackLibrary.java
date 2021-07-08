package com.hk.spigot.lua;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.hk.func.BiConsumer;
import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.spigot.Helps;

public enum StackLibrary implements BiConsumer<Environment, LuaObject>, LuaMethod
{
	_new() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			if(args.length >= 2)
				Lua.checkArgs(toString(), args, LuaType.STRING, LuaType.INTEGER);
			else
				Lua.checkArgs(toString(), args, LuaType.STRING);
			Material mat = Helps.getEnum(Material.class, args[0].getString());
			int size = args.length >= 2 ? (int) args[1].getInteger() : 1;

			return new ItemStackUserdata(new ItemStack(mat, size));
		}
		
		public String toString() { return "new"; }
	},
	enchants() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			LuaObject tbl = Lua.newTable();
			
			long i = 1;
			for(Enchantment enchant : Enchantment.values())
			{
				tbl.rawSet(i++, toTable(enchant));
			}
			
			return tbl;
		}
	},
	getEnchant() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0].getString()));
			
			if(enchant == null)
				return Lua.nil();
			else
				return toTable(enchant);
		}
	};

	@Override
	public LuaObject call(LuaInterpreter interp, LuaObject[] args)
	{
		throw new UnsupportedOperationException();
	}

	protected LuaObject toTable(Enchantment enchant)
	{
		LuaObject tbl = Lua.newTable();
		
		tbl.rawSet("itemTarget", enchant.getItemTarget().name().toLowerCase());
		tbl.rawSet("treasure", enchant.isTreasure());
		tbl.rawSet("startLevel", enchant.getStartLevel());
		tbl.rawSet("maxLevel", enchant.getMaxLevel());
		tbl.rawSet("key", enchant.getKey().getKey());

		return tbl;
	}

	@Override
	public void accept(Environment env, LuaObject table)
	{
		String name = toString();
		if(name != null && !name.trim().isEmpty())
			table.rawSet(Lua.newString(name), Lua.newFunc(this));
	}

	public static final LuaLibrary<StackLibrary> INS = new LuaLibrary<>("stacks", StackLibrary.class);
}
