package com.hk.spigot.lua;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import com.hk.func.BiConsumer;
import com.hk.lua.Environment;
import com.hk.lua.Lua;
import com.hk.lua.Lua.LuaMethod;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaLibrary;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.spigot.Helps;
import com.hk.spigot.lua.entity.EntityUserdata;

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

			Material mat = Helps.getEnum(Material.class, args[3].getString());

			if(mat == null || !mat.isBlock())
				throw new LuaException("Unknown block: " + mat);

			int x = (int) args[0].getInteger();
			int y = (int) args[1].getInteger();
			int z = (int) args[2].getInteger();

			
			world.getBlockAt(x, y, z).setType(mat);
			
			return Lua.nil();
		}
	},
	canGenerateStructures() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).canGenerateStructures());
		}
	},
	createExplosion() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			double power = args[3].getFloat();
			boolean setFire = args.length >= 5 ? args[4].getBoolean() : false;
			boolean breakBlocks = args.length >= 6 ? args[5].getBoolean() : true;
			
			boolean result = interp.getExtra("world", World.class).createExplosion(x, y, z, (float) power, setFire, breakBlocks);
			
			return Lua.newBoolean(result);
		}
	},
	dropItem() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.USERDATA);
			if(!(args[3] instanceof ItemStackUserdata))
				throw new LuaException("bad argument #4 to " + name() + " (ITEMSTACK* expected)");

			World world = interp.getExtra("world", World.class);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			boolean naturally = args.length >= 4 ? args[3].getBoolean() : false;
			ItemStackUserdata data = (ItemStackUserdata) args[3];

			Item item;
			if(naturally)
				item = world.dropItemNaturally(new Location(world, x, y, z), data.stack);
			else
				item = world.dropItem(new Location(world, x, y, z), data.stack);

			return EntityUserdata.get(item);
		}
	},
	generateTree() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING);

			World world = interp.getExtra("world", World.class);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			String str = args[3].getString();
			TreeType type = Helps.getEnum(TreeType.class, str);
			
			if(type == null)
				throw new LuaException("Unknown tree type: " + str);
			else
				return Lua.newBoolean(world.generateTree(new Location(world, x, y, z), type));
		}
	},
	getAllowAnimals() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).getAllowAnimals());
		}
	},
	getAllowMonsters() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).getAllowMonsters());
		}
	},
	getAmbientSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getAmbientSpawnLimit());
		}
	},
	setAmbientSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);
			
			interp.getExtra("world", World.class).setAmbientSpawnLimit((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getAnimalSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getAnimalSpawnLimit());
		}
	},
	setAnimalSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);
			
			interp.getExtra("world", World.class).setAnimalSpawnLimit((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getWaterAnimalSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getWaterAnimalSpawnLimit());
		}
	},
	setWaterAnimalSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);
			
			interp.getExtra("world", World.class).setWaterAnimalSpawnLimit((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getMonsterSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getMonsterSpawnLimit());
		}
	},
	setMonsterSpawnLimit() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);
			
			interp.getExtra("world", World.class).setMonsterSpawnLimit((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	setSpawnFlags() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.BOOLEAN, LuaType.BOOLEAN);

			boolean allowMonsters = args[0].getBoolean();
			boolean allowAnimals = args[1].getBoolean();
			
			interp.getExtra("world", World.class).setSpawnFlags(allowMonsters, allowAnimals);
			
			return Lua.nil();
		}
	},
	getBiomeAt() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			long x = args[0].getInteger();
			long y = args[1].getInteger();
			
			return Lua.newString(world.getBiome((int) x, (int) y).name());
		}
	},
	setBiomeAt() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER, LuaType.STRING);

			World world = interp.getExtra("world", World.class);
			
			long x = args[0].getInteger();
			long y = args[1].getInteger();
			String str = args[2].getString();
			Biome biome = Helps.getEnum(Biome.class, str);
			
			if(biome == null)
				throw new LuaException("Unknown biome name: " + str);
			
			world.setBiome((int) x, (int) y, biome);
			
			return Lua.nil();
		}
	},
	getDifficulty() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newString(interp.getExtra("world", World.class).getDifficulty().name());
		}
	},
	setDifficulty() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.STRING);

			String str = args[0].getString();
			Difficulty difficulty = Helps.getEnum(Difficulty.class, str);
			
			if(difficulty == null)
				throw new LuaException("Unknown difficulty: " + str);
			
			interp.getExtra("world", World.class).setDifficulty(difficulty);
			
			return Lua.nil();
		}
	},
	getEntities() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			World world = interp.getExtra("world", World.class);
			
			LuaObject tbl = Lua.newTable();
			
			long i = 1;
			for(Entity entity : world.getEntities())
				tbl.rawSet(i++, EntityUserdata.get(entity));
			
			return tbl;
		}
	},
	getLivingEntities() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			World world = interp.getExtra("world", World.class);
			
			LuaObject tbl = Lua.newTable();
			
			long i = 1;
			for(LivingEntity entity : world.getLivingEntities())
				tbl.rawSet(i++, EntityUserdata.get(entity));
			
			return tbl;
		}
	},
	forEachEntity() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.FUNCTION);

			World world = interp.getExtra("world", World.class);

			int count = 0;
			for(Entity entity : world.getEntities())
			{
				count++;

				if(args[0].call(interp, EntityUserdata.get(entity), Lua.newNumber(count)).getBoolean())
					break;
			}
			
			return Lua.newNumber(count);
		}
	},
	getWorldFolder() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newString(interp.getExtra("world", World.class).getWorldFolder().getAbsolutePath());
		}
	},
	environment() {
		@Override
		public void accept(Environment env, LuaObject table)
		{
			String str = env.interp.getExtra("world", World.class).getEnvironment().name();
			table.rawSet(name(), Lua.newString(str));
		}
	},
	type() {
		@Override
		public void accept(Environment env, LuaObject table)
		{
			String str = env.interp.getExtra("world", World.class).getWorldType().name();
			table.rawSet(name(), Lua.newString(str));
		}
	},
	getForceLoadedChunks() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			World world = interp.getExtra("world", World.class);

			LuaObject tbl = Lua.newTable();
			LuaObject tbl2;
			
			long i = 1;
			for(Chunk chunk : world.getForceLoadedChunks())
			{
				tbl2 = Lua.newTable();
				
				tbl2.rawSet("x", chunk.getX());
				tbl2.rawSet("z", chunk.getZ());
				
				tbl.rawSet(i++, tbl2);
			}
			
			return tbl;
		}
	},
	getLoadedChunks() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			World world = interp.getExtra("world", World.class);

			LuaObject tbl = Lua.newTable();
			LuaObject tbl2;
			
			long i = 1;
			for(Chunk chunk : world.getLoadedChunks())
			{
				tbl2 = Lua.newTable();
				
				tbl2.rawSet("x", chunk.getX());
				tbl2.rawSet("z", chunk.getZ());
				
				tbl.rawSet(i++, tbl2);
			}
			
			return tbl;
		}
	},
	getFullTime() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getFullTime());
		}
	},
	setFullTime() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);

			interp.getExtra("world", World.class).setFullTime(args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getHighestBlockAt() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			long x, z;
			
			if(args.length >= 3)
			{
				Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);
				
				x = args[0].getInteger();
				z = args[2].getInteger();
			}
			else
			{
				Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);

				x = args[0].getInteger();
				z = args[1].getInteger();
			}
			
			Block block = interp.getExtra("world", World.class).getHighestBlockAt((int) x, (int) z);
			
			return Lua.newString(block.getType().name());
		}
	},
	getHighestYAt() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			long x, z;
			
			if(args.length >= 3)
			{
				Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER, LuaType.INTEGER);
				
				x = args[0].getInteger();
				z = args[2].getInteger();
			}
			else
			{
				Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);

				x = args[0].getInteger();
				z = args[1].getInteger();
			}
			
			int y = interp.getExtra("world", World.class).getHighestBlockYAt((int) x, (int) z);
			
			return Lua.newNumber(y);
		}
	},
	getHumidity() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);
			long x = args[0].getInteger();
			long z = args[1].getInteger();
			
			double d = interp.getExtra("world", World.class).getHumidity((int) x, (int) z);
			
			return Lua.newNumber(d);
		}
	},
	getTemperature() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);
			long x = args[0].getInteger();
			long z = args[1].getInteger();
			
			double d = interp.getExtra("world", World.class).getTemperature((int) x, (int) z);
			
			return Lua.newNumber(d);
		}
	},
	getKeepSpawnInMemory() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).getKeepSpawnInMemory());
		}
	},
	setKeepSpawnInMemory() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.BOOLEAN);
			
			interp.getExtra("world", World.class).setKeepSpawnInMemory(args[0].getBoolean());

			return Lua.nil();
		}
	},
	getMaxHeight() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getMaxHeight());
		}
	},
	getName() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newString(interp.getExtra("world", World.class).getName());
		}
	},
//	getNearbyEntities() {
//	},
	getPlayers() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			World world = interp.getExtra("world", World.class);
			
			LuaObject tbl = Lua.newTable();
			
			long i = 1;
			for(Player player : world.getPlayers())
				tbl.rawSet(i++, EntityUserdata.get(player));
			
			return tbl;
		}
	},
	forEachPlayer() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.FUNCTION);

			World world = interp.getExtra("world", World.class);

			int count = 0;
			for(Player player : world.getPlayers())
			{
				count++;

				if(args[0].call(interp, EntityUserdata.get(player), Lua.newNumber(count)).getBoolean())
					break;
			}
			
			return Lua.newNumber(count);
		}
	},
	getPVP() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).getPVP());
		}
	},
	setPVP() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.BOOLEAN);

			interp.getExtra("world", World.class).setPVP(args[0].getBoolean());
			
			return Lua.nil();
		}
	},
	getSeaLevel() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getSeaLevel());
		}
	},
	getSeed() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getSeed());
		}
	},
	getSpawn() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			World world = interp.getExtra("world", World.class);
			Location spawn = world.getSpawnLocation();
			
			LuaObject tbl = Lua.newTable();
			
			tbl.rawSet("x", spawn.getX());
			tbl.rawSet("y", spawn.getY());
			tbl.rawSet("z", spawn.getZ());
			
			return tbl;
		}
	},
	setSpawn() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);

			World world = interp.getExtra("world", World.class);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			
			return Lua.newBoolean(world.setSpawnLocation(new Location(world, x, y, z)));
		}
	},
	getThunderDuration() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getThunderDuration());
		}
	},
	setThunderDuration() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			world.setThunderDuration((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getWeatherDuration() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getWeatherDuration());
		}
	},
	setWeatherDuration() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			world.setWeatherDuration((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getTicksPerAnimalSpawns() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getTicksPerAnimalSpawns());
		}
	},
	setTicksPerAnimalSpawns() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			world.setTicksPerAnimalSpawns((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getTicksPerMonsterSpawns() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getTicksPerMonsterSpawns());
		}
	},
	setTicksPerMonsterSpawns() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			world.setTicksPerMonsterSpawns((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	getTime() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newNumber(interp.getExtra("world", World.class).getTime());
		}
	},
	setTime() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			world.setTime((int) args[0].getInteger());
			
			return Lua.nil();
		}
	},
	uuid() {
		@Override
		public void accept(Environment env, LuaObject table)
		{
			table.rawSet(name(), Lua.newString(env.interp.getExtra("world", World.class).getUID().toString()));
		}
	},
	hasStorm() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).hasStorm());
		}
	},
	setStorm() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.BOOLEAN);
			
			interp.getExtra("world", World.class).setStorm(args[0].getBoolean());

			return Lua.nil();
		}
	},
	isThundering() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).isThundering());
		}
	},
	setThundering() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.BOOLEAN);
			
			interp.getExtra("world", World.class).setThundering(args[0].getBoolean());

			return Lua.nil();
		}
	},
	isAutoSave() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return Lua.newBoolean(interp.getExtra("world", World.class).isAutoSave());
		}
	},
	setAutoSave() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.BOOLEAN);
			interp.getExtra("world", World.class).setAutoSave(args[0].getBoolean());
			return Lua.nil();
		}
	},
	isChunkForceLoaded() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);

			long x = args[0].getInteger();
			long z = args[1].getInteger();
			
			return Lua.newBoolean(interp.getExtra("world", World.class).isChunkForceLoaded((int) x, (int) z));
		}
	},
	setChunkForceLoaded() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER, LuaType.BOOLEAN);

			long x = args[0].getInteger();
			long z = args[1].getInteger();
			boolean loaded = args[2].getBoolean();
			
			interp.getExtra("world", World.class).setChunkForceLoaded((int) x, (int) z, loaded);
			
			return Lua.nil();
		}
	},
	isChunkLoaded() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);

			long x = args[0].getInteger();
			long z = args[1].getInteger();
			
			return Lua.newBoolean(interp.getExtra("world", World.class).isChunkLoaded((int) x, (int) z));
		}
	},
	isChunkGenerated() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);

			long x = args[0].getInteger();
			long z = args[1].getInteger();
			
			return Lua.newBoolean(interp.getExtra("world", World.class).isChunkGenerated((int) x, (int) z));
		}
	},
	loadChunk() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);
			
			World world = interp.getExtra("world", World.class);

			long x = args[0].getInteger();
			long z = args[1].getInteger();
			
			if(args.length >= 3)
			{
				boolean b = world.loadChunk((int) x, (int) z, args[2].getBoolean());
				return Lua.newBoolean(b);
			}
			else
				world.loadChunk((int) x, (int) z);
			
			return Lua.nil();
		}
	},
	unloadChunk() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);
			
			World world = interp.getExtra("world", World.class);

			long x = args[0].getInteger();
			long z = args[1].getInteger();

			boolean b;
			if(args.length >= 3)
				b = world.unloadChunk((int) x, (int) z, args[2].getBoolean());
			else
				b = world.unloadChunk((int) x, (int) z);
			
			return Lua.newBoolean(b);
		}
	},
	unloadChunkRequest() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.INTEGER, LuaType.INTEGER);
			
			World world = interp.getExtra("world", World.class);

			long x = args[0].getInteger();
			long z = args[1].getInteger();

			return Lua.newBoolean(world.unloadChunkRequest((int) x, (int) z));
		}
	},
	locateNearestStructure() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			if(args.length >= 6)
				Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING, LuaType.INTEGER, LuaType.BOOLEAN);
			else
				Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING, LuaType.INTEGER);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			String str = args[3].getString();
			StructureType type = StructureType.getStructureTypes().get(str);
			long radius = args[4].getInteger();
			boolean findUnexplored = args.length >= 6 ? args[5].getBoolean() : false;
			
			if(type == null)
				throw new LuaException("Unknown structure type: " + str);
			
			World world = interp.getExtra("world", World.class);

			Location nearest = world.locateNearestStructure(new Location(world, x, y, z), type, (int) radius, findUnexplored);
			
			if(nearest == null)
				return Lua.nil();
			
			LuaObject bx = Lua.newNumber(nearest.getBlockX());
			LuaObject by = Lua.newNumber(nearest.getBlockY());
			LuaObject bz = Lua.newNumber(nearest.getBlockZ());
			
			return Lua.newVarargs(bx, by, bz);
		}
	},
	playEffect() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			if(args.length >= 6)
				Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING, LuaType.INTEGER, LuaType.INTEGER);
			else
				Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING, LuaType.INTEGER);

			World world = interp.getExtra("world", World.class);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			String str = args[3].getString();
			Effect type = Helps.getEnum(Effect.class, str);
			long data = args[4].getInteger();

			if(type == null)
				throw new LuaException("Unknown effect: " + str);
			
			if(args.length >= 6)
				world.playEffect(new Location(world, x, y, z), type, (int) data, (int) args[5].getInteger());
			else
				world.playEffect(new Location(world, x, y, z), type, (int) data);

			return Lua.nil();
		}
	},
	playSound() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING, LuaType.NUMBER, LuaType.NUMBER);

			World world = interp.getExtra("world", World.class);
			
			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			String sound = args[3].getString();
			float volume = (float) args[4].getFloat();
			float pitch = (float) args[5].getFloat();

			world.playSound(new Location(world, x, y, z), sound, volume, pitch);
			
			return Lua.nil();
		}
	},
//	rayTrace() {
//	},
	save() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			interp.getExtra("world", World.class).save();
			return Lua.nil();
		}
	},
	spawnEntity() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER, LuaType.STRING);

			World world = interp.getExtra("world", World.class);

			double x = args[0].getFloat();
			double y = args[1].getFloat();
			double z = args[2].getFloat();
			String str = args[3].getString();
			EntityType type = Helps.getEnum(EntityType.class, str);
			
			if(type == null || type.getEntityClass() == null)
				throw new LuaException("Unknown entity class: " + str);
			
			Location l = new Location(world, x, y, z);
			
			if(args.length >= 5)
			{
				if(!args[4].isFunction())
					throw new LuaException("bad argument #5 to '" + name() + "' (function expected)");

				Entity e = world.spawn(l, type.getEntityClass(), (entity) -> {
					args[4].call(interp, EntityUserdata.get(entity));
				});

				return EntityUserdata.get(e);
			}
			else
			{
				return EntityUserdata.get(world.spawnEntity(l, type));
			}
		}
	},
	getEntity() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			Lua.checkArgs(name(), args, LuaType.STRING);

			String uuid = args[0].getString();

			return EntityUserdata.get(Bukkit.getEntity(UUID.fromString(uuid)));
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
	
	public static LuaObject toTable(RayTraceResult result)
	{
		if(result == null)
			return Lua.nil();
		
		LuaObject tbl = Lua.newTable();
		if(result.getHitBlock() != null)
		{
			tbl.rawSet("blockX", result.getHitPosition().getBlockX());
			tbl.rawSet("blockY", result.getHitPosition().getBlockY());
			tbl.rawSet("blockZ", result.getHitPosition().getBlockZ());

			tbl.rawSet("block", result.getHitBlock().getType().name());
		}
		if(result.getHitEntity() != null)
		{
			tbl.rawSet("hitX", result.getHitPosition().getX());
			tbl.rawSet("hitY", result.getHitPosition().getY());
			tbl.rawSet("hitZ", result.getHitPosition().getZ());

			tbl.rawSet("entity", result.getHitEntity().getType().name());
			tbl.rawSet("uuid", result.getHitEntity().getUniqueId().toString());
		}

		if(result.getHitBlockFace() != null)
			tbl.rawSet("face", result.getHitBlockFace().name());
		
		return tbl;
	}

	public static final LuaLibrary<WorldLibrary> INS = new LuaLibrary<>("world", WorldLibrary.class);
}
