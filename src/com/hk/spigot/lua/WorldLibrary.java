package com.hk.spigot.lua;

import org.bukkit.Material;
import org.bukkit.World;

import com.hk.func.BiConsumer;
import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;

public enum WorldLibrary implements BiConsumer<Environment, LuaObject>, LuaMethod
{
	getBlock() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);

			int x = (int) args[0].getInteger();
			int y = (int) args[1].getInteger();
			int z = (int) args[2].getInteger();

			return Lua.newString(world.getBlockAt(x, y, z).getType().name());
		}
	},
	setBlock() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER, LuaType.STRING);

			World world = interp.getExtra("world", World.class);

			int x = (int) args[0].getInteger();
			int y = (int) args[1].getInteger();
			int z = (int) args[2].getInteger();

			world.getBlockAt(x, y, z).setType(Material.valueOf(args[3].getString()));
			
			return Lua.nil();
		}
	},
	getWorldFolder() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newString(interp.getExtra("world", World.class).getWorldFolder().getAbsolutePath());
		}
	};

	@Override
	public void accept(Environment env, LuaObject table)
	{
		String name = toString();
		if(name != null && !name.trim().isEmpty())
			table.rawSet(Lua.newString(name), Lua.newFunc(this));
	}

	public static final LuaLibrary<WorldLibrary> INS = new LuaLibrary<>("world", WorldLibrary.class);
}
