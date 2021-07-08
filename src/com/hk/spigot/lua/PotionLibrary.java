package com.hk.spigot.lua;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hk.func.BiConsumer;
import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public enum PotionLibrary implements BiConsumer<Environment, LuaObject>, LuaMethod
{
	types() {
		@Override
		public void accept(Environment env, LuaObject table)
		{
			PotionEffectType[] types = PotionEffectType.values();
			
			LuaObject tbl = Lua.newTable();
			long i = 1;
			for(PotionEffectType type : types)
				tbl.rawSet(i++, Lua.newString(type.getName()));

			table.rawSet(toString(), tbl);
		}
	},
	color() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(toString(), args, LuaType.STRING);

			PotionEffectType type = PotionEffectType.getByName(args[0].getString());
			
			if(type == null)
				return Lua.nil();
			
			Color clr = type.getColor();
			LuaObject tbl = Lua.newTable();
			
			tbl.rawSet("r", clr.getRed());
			tbl.rawSet("g", clr.getGreen());
			tbl.rawSet("b", clr.getBlue());
			
			return tbl;
		}
	};

	@Override
	public LuaObject call(LuaInterpreter interp, LuaObject[] args)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(Environment env, LuaObject table)
	{
		String name = toString();
		if(name != null && !name.trim().isEmpty())
			table.rawSet(Lua.newString(name), Lua.newFunc(this));
	}
	
	public static LuaObject toTable(PotionEffect effect)
	{
		if(effect == null)
			return Lua.nil();
		
		LuaObject tbl = Lua.newTable();
		
		tbl.rawSet("type", effect.getType().getName());
		tbl.rawSet("duration", effect.getDuration());
		tbl.rawSet("amplifier", effect.getAmplifier());
		
		return tbl;
	}

	public static final LuaLibrary<PotionLibrary> INS = new LuaLibrary<>("potions", PotionLibrary.class);
}
