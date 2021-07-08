package com.hk.spigot.lua.entity;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;
import com.hk.spigot.LuaLlamas;

public class EntityUserdata extends LuaUserdata
{
	public final Entity entity;

	protected EntityUserdata(Entity entity)
	{
		this.entity = entity;
		
		metatable = entityMetatable;
	}

	@Override
	public String getString(LuaInterpreter interp)
	{
		return entity.getClass().getSimpleName() + "[" + entity.getName() + "]";
	}
	
	private static LuaObject teleport(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'teleport' (ENTITY* expected)");
		
		EntityUserdata data = (EntityUserdata) args[0];
		if(args.length >= 4)
		{
			Lua.checkArgs("teleport", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);

			double x = args[1].getFloat();
			double y = args[2].getFloat();
			double z = args[3].getFloat();
			
			return Lua.newBoolean(data.entity.teleport(new Location(data.entity.getWorld(), x, y, z)));
		}
		else
		{
			if(!(args[1] instanceof EntityUserdata))
				throw new LuaException("bad argument #2 to 'teleport' (ENTITY* expected)");

			EntityUserdata other = (EntityUserdata) args[1];

			return Lua.newBoolean(data.entity.teleport(other.entity));
		}
	}
	
	private static LuaObject setRotation(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'setRotation' (ENTITY* expected)");
		Lua.checkArgs("setRotation", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER);

		EntityUserdata data = (EntityUserdata) args[0];

		double pitch = args[1].getFloat();
		double yaw = args[2].getFloat();
		data.entity.setRotation((float) pitch, (float) yaw);

		return Lua.nil();
	}
	
	private static LuaObject getDirection(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getDirection' (ENTITY* expected)");
		Lua.checkArgs("getDirection", args, LuaType.USERDATA);

		EntityUserdata data = (EntityUserdata) args[0];
		Vector v = data.entity.getLocation().getDirection();

		LuaObject tbl = Lua.newTable();
		
		tbl.rawSet("x", v.getX());
		tbl.rawSet("y", v.getY());
		tbl.rawSet("z", v.getZ());
		
		return tbl;
	}
	
	private static LuaObject setDirection(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'setDirection' (ENTITY* expected)");
		Lua.checkArgs("setDirection", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);

		EntityUserdata data = (EntityUserdata) args[0];
		
		double x = args[1].getFloat();
		double y = args[2].getFloat();
		double z = args[3].getFloat();
		
		data.entity.getLocation().setDirection(new Vector(x, y, z));

		return Lua.nil();
	}
	
	private static LuaObject getNearbyEntities(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getNearbyEntities' (ENTITY* expected)");
		Lua.checkArgs("getNearbyEntities", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);

		EntityUserdata data = (EntityUserdata) args[0];
		
		double x = args[1].getFloat();
		double y = args[2].getFloat();
		double z = args[3].getFloat();
		
		LuaObject tbl = Lua.newTable();
		List<Entity> entities = data.entity.getNearbyEntities(x, y, z);
		
		long i = 1;
		for(Entity entity : entities)
			tbl.rawSet(i++, EntityUserdata.get(entity));

		return tbl;
	}
	
	private static LuaObject getScoreboardTags(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getScoreboardTags' (ENTITY* expected)");
		Lua.checkArgs("getScoreboardTags", args, LuaType.USERDATA);

		EntityUserdata data = (EntityUserdata) args[0];

		LuaObject tbl = Lua.newTable();
		
		long i = 1;
		for(String tag : data.entity.getScoreboardTags())
			tbl.rawSet(i++, Lua.newString(tag));
		
		return tbl;
	}
	
	private static LuaObject hasScoreboardTag(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'hasScoreboardTag' (ENTITY* expected)");
		Lua.checkArgs("hasScoreboardTag", args, LuaType.USERDATA, LuaType.STRING);

		EntityUserdata data = (EntityUserdata) args[0];

		return Lua.newBoolean(data.entity.getScoreboardTags().contains(args[1].getString()));
	}
	
	private static LuaObject addScoreboardTag(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'addScoreboardTag' (ENTITY* expected)");
		Lua.checkArgs("addScoreboardTag", args, LuaType.USERDATA, LuaType.STRING);

		EntityUserdata data = (EntityUserdata) args[0];

		return Lua.newBoolean(data.entity.addScoreboardTag(args[1].getString()));
	}
	
	private static LuaObject removeScoreboardTag(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'addScoreboardTag' (ENTITY* expected)");
		Lua.checkArgs("addScoreboardTag", args, LuaType.USERDATA, LuaType.STRING);

		EntityUserdata data = (EntityUserdata) args[0];

		return Lua.newBoolean(data.entity.removeScoreboardTag(args[1].getString()));
	}
	
	private static LuaObject getFacing(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getFacing' (ENTITY* expected)");
		Lua.checkArgs("getFacing", args, LuaType.USERDATA);

		EntityUserdata data = (EntityUserdata) args[0];

		return Lua.newString(data.entity.getFacing().name());
	}
	
	private static LuaObject addControl(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'addControl' (ENTITY* expected)");
		Lua.checkArgs("addControl", args, LuaType.USERDATA, LuaType.FUNCTION);

		EntityUserdata data = (EntityUserdata) args[0];
		LuaLlamas ins = JavaPlugin.getPlugin(LuaLlamas.class);
		
		int id = new BukkitRunnable() {
			@Override
			public void run()
			{
				if(data.entity.isDead() || !data.entity.isValid() || !args[1].call(interp, data).getBoolean())
					cancel();
			}
		}.runTaskTimer(ins, LuaLlamas.START_TICK_DELAY, LuaLlamas.INTERVAL_TICK_DELAY).getTaskId();
		
		return Lua.newNumber(id);
	}

	@Override
	public LuaObject doIndex(LuaInterpreter interp, LuaObject key)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
			case "entityID":
				return Lua.newNumber(entity.getEntityId());
			case "fireTicks":
				return Lua.newNumber(entity.getFireTicks());
			case "maxFireTicks":
				return Lua.newNumber(entity.getMaxFireTicks());
			case "portalCooldown":
				return Lua.newNumber(entity.getPortalCooldown());
			case "ticksLived":
				return Lua.newNumber(entity.getTicksLived());
			case "fallDistance":
				return Lua.newNumber(entity.getFallDistance());
			case "height":
				return Lua.newNumber(entity.getHeight());
			case "width":
				return Lua.newNumber(entity.getWidth());
			case "posX":
				return Lua.newNumber(entity.getLocation().getX());
			case "posY":
				return Lua.newNumber(entity.getLocation().getY());
			case "posZ":
				return Lua.newNumber(entity.getLocation().getZ());
			case "velX":
				return Lua.newNumber(entity.getVelocity().getX());
			case "velY":
				return Lua.newNumber(entity.getVelocity().getY());
			case "velZ":
				return Lua.newNumber(entity.getVelocity().getZ());
			case "pitch":
				return Lua.newNumber(entity.getLocation().getPitch());
			case "yaw":
				return Lua.newNumber(entity.getLocation().getYaw());
			case "hasGravity":
				return Lua.newBoolean(entity.hasGravity());
			case "customNameVisible":
				return Lua.newBoolean(entity.isCustomNameVisible());
			case "dead":
				return Lua.newBoolean(entity.isDead());
			case "glowing":
				return Lua.newBoolean(entity.isGlowing());
			case "isInsideVehicle":
				return Lua.newBoolean(entity.isInsideVehicle());
			case "invulnerable":
				return Lua.newBoolean(entity.isInvulnerable());
			case "isOnGround":
				return Lua.newBoolean(entity.isOnGround());
			case "isOp":
				return Lua.newBoolean(entity.isOp());
			case "silent":
				return Lua.newBoolean(entity.isSilent());
			case "uuid":
				return Lua.newString(entity.getUniqueId().toString());
			case "type":
				return Lua.newString(entity.getType().name());
			case "exists":
				return Lua.newBoolean(entity.isValid());
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
			case "fireTicks":
				entity.setFireTicks((int) value.getInteger());
				return;
			case "portalCooldown":
				entity.setPortalCooldown((int) value.getInteger());
				return;
			case "ticksLived":
				entity.setTicksLived((int) value.getInteger());
				return;
			case "fallDistance":
				entity.setFallDistance((float) value.getFloat());
				return;
			case "posX":
				entity.getLocation().setX(value.getFloat());
				return;
			case "posY":
				entity.getLocation().setY(value.getFloat());
				return;
			case "posZ":
				entity.getLocation().setZ(value.getFloat());
				return;
			case "velX":
				entity.setVelocity(entity.getVelocity().setX(value.getFloat()));
				return;
			case "velY":
				entity.setVelocity(entity.getVelocity().setY(value.getFloat()));
				return;
			case "velZ":
				entity.setVelocity(entity.getVelocity().setZ(value.getFloat()));
				return;
			case "pitch":
				entity.getLocation().setPitch((float) value.getFloat());
				return;
			case "yaw":
				entity.getLocation().setYaw((float) value.getFloat());
				return;
			case "hasGravity":
				entity.setGravity(value.getBoolean());
				return;
			case "isCustomNameVisible":
				entity.setCustomNameVisible(value.getBoolean());
				return;
			case "glowing":
				entity.setGlowing(value.getBoolean());
				return;
			case "invulnerable":
				entity.setInvulnerable(value.getBoolean());
				return;
			case "silent":
				entity.setSilent(value.getBoolean());
				return;
			case "height":
			case "width":
			case "maxFireTicks":
			case "entityID":
			case "isInsideVehicle":
			case "isOnGround":
			case "isOp":
			case "uuid":
			case "type":
			case "exists":
				throw new LuaException("cannot change value of entity." + key.getString());
			}
		}
		super.doNewIndex(interp, key, value);
	}

	@Override
	public String name()
	{
		return "ENTITY*";
	}

	@Override
	public Entity getUserdata()
	{
		return entity;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof EntityUserdata && entity.equals(((EntityUserdata) o).entity);
	}

	@Override
	public int hashCode()
	{
		return entity.hashCode();
	}
	
	public static EntityUserdata get(Entity entity)
	{
		List<MetadataValue> vals = entity.getMetadata(LuaLlamas.ENTITY_USERDATA_KEY);
		
		for(MetadataValue val : vals)
		{
			if(val instanceof EntityUserdata)
				return (EntityUserdata) val;
		}
		
		EntityUserdata val;
		
		if(entity instanceof Llama)
			val = new LlamaUserdata((Llama) entity);
		else if(entity instanceof Player)
			val = new PlayerUserdata((Player) entity);
		else if(entity instanceof LivingEntity)
			val = new LivingEntityUserdata((LivingEntity) entity);
		else
			val = new EntityUserdata(entity);

		LuaLlamas ins = JavaPlugin.getPlugin(LuaLlamas.class);
		entity.setMetadata(LuaLlamas.ENTITY_USERDATA_KEY, new FixedMetadataValue(ins, val));
		
		return val;
	}
	
	public static LuaObject metatable()
	{
		LuaObject tbl = Lua.newTable();
		tbl.rawSet("__name", "ENTITY*");
		tbl.rawSet("__index", tbl);
		
		tbl.rawSet("teleport", Lua.newFunc(EntityUserdata::teleport));
		tbl.rawSet("setRotation", Lua.newFunc(EntityUserdata::setRotation));
		tbl.rawSet("getDirection", Lua.newFunc(EntityUserdata::getDirection));
		tbl.rawSet("setDirection", Lua.newFunc(EntityUserdata::setDirection));
		tbl.rawSet("getNearbyEntities", Lua.newFunc(EntityUserdata::getNearbyEntities));
		tbl.rawSet("getScoreboardTags", Lua.newFunc(EntityUserdata::getScoreboardTags));
		tbl.rawSet("hasScoreboardTag", Lua.newFunc(EntityUserdata::hasScoreboardTag));
		tbl.rawSet("addScoreboardTag", Lua.newFunc(EntityUserdata::addScoreboardTag));
		tbl.rawSet("removeScoreboardTag", Lua.newFunc(EntityUserdata::removeScoreboardTag));
		tbl.rawSet("getFacing", Lua.newFunc(EntityUserdata::getFacing));
		tbl.rawSet("addControl", Lua.newFunc(EntityUserdata::addControl));

		return tbl;
	}
	
	private static final LuaObject entityMetatable = metatable();
}
