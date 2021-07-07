package com.hk.spigot.lua;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;

public class EntityUserdata extends LuaUserdata
{
	protected final LivingEntity entity;

	public EntityUserdata(LivingEntity entity)
	{
		this.entity = entity;
		
		metatable = entityMetatable;
	}

	@Override
	public String getString(LuaInterpreter interp)
	{
		return entity.getClass().getSimpleName() + "[" + entity.getName() + "]";
	}
	
	private static LuaObject asLlama(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'asLlama' (ENTITY* expected)");
		Lua.checkArgs("asLlama", args, LuaType.USERDATA);

		EntityUserdata data = (EntityUserdata) args[0];

		if(data instanceof LlamaUserdata)
			return data;
		else if(data.entity instanceof Llama)
			return new LlamaUserdata((Llama) data.entity);
		
		return Lua.nil();
	}
	
	private static LuaObject asPlayer(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'asPlayer' (ENTITY* expected)");
		Lua.checkArgs("asPlayer", args, LuaType.USERDATA);

		EntityUserdata data = (EntityUserdata) args[0];

		if(data instanceof PlayerUserdata)
			return data;
		else if(data.entity instanceof Player)
			return new PlayerUserdata((Player) data.entity);
		
		return Lua.nil();
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
	
	private static LuaObject rayTrace(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'rayTrace' (ENTITY* expected)");
		Lua.checkArgs("rayTrace", args, LuaType.USERDATA, LuaType.NUMBER);

		EntityUserdata data = (EntityUserdata) args[0];

		RayTraceResult result = data.entity.rayTraceBlocks(args[1].getFloat());

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

			tbl.rawSet("entity", result.getHitEntity().getUniqueId().toString());
		}

		if(result.getHitBlockFace() != null)
			tbl.rawSet("face", result.getHitBlockFace().name());
		
		return tbl;
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
		{
			if(entity instanceof LivingEntity)
				tbl.rawSet(i++, new EntityUserdata((LivingEntity) entity));
		}

		return tbl;
	}
	
	private static LuaObject hasLignOfSight(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'hasLignOfSight' (ENTITY* expected)");
		if(!(args[1] instanceof EntityUserdata))
			throw new LuaException("bad argument #2 to 'hasLignOfSight' (ENTITY* expected)");

		EntityUserdata data = (EntityUserdata) args[0];
		EntityUserdata other = (EntityUserdata) args[1];

		return Lua.newBoolean(data.entity.hasLineOfSight(other.entity));
	}
	
	private static LuaObject getEyeHeight(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getEyeHeight' (ENTITY* expected)");
		Lua.checkArgs("getEyeHeight", args, LuaType.USERDATA, LuaType.BOOLEAN);

		EntityUserdata data = (EntityUserdata) args[0];

		return Lua.newNumber(data.entity.getEyeHeight(args[1].getBoolean()));
	}
	
	private static LuaObject hasPotionEffect(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'hasPotionEffect' (ENTITY* expected)");
		Lua.checkArgs("hasPotionEffect", args, LuaType.USERDATA, LuaType.STRING);

		EntityUserdata data = (EntityUserdata) args[0];

		return Lua.newBoolean(data.entity.hasPotionEffect(PotionEffectType.getByName(args[1].getString())));
	}
	
	private static LuaObject addPotionEffect(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'addPotionEffect' (ENTITY* expected)");
		
		EntityUserdata data = (EntityUserdata) args[0];
		PotionEffectType type;
		long duration, amplifier;
		if(args.length == 2)
		{
			Lua.checkArgs("addPotionEffect", args, LuaType.USERDATA, LuaType.TABLE);

			type = PotionEffectType.getByName(args[1].rawGet("type").getString());
			duration = args[1].rawGet("duration").getInteger();
			amplifier = args[1].rawGet("amplifier").getInteger();
		}
		else
		{
			Lua.checkArgs("addPotionEffect", args, LuaType.USERDATA, LuaType.STRING, LuaType.INTEGER, LuaType.INTEGER);

			type = PotionEffectType.getByName(args[1].getString());
			duration = args[2].getInteger();
			amplifier = args[3].getInteger();
		}
		
		return Lua.newBoolean(data.entity.addPotionEffect(type.createEffect((int) duration, (int) amplifier)));
	}
	
	private static LuaObject getPotionEffect(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getPotionEffect' (ENTITY* expected)");
		Lua.checkArgs("getPotionEffect", args, LuaType.USERDATA, LuaType.STRING);

		EntityUserdata data = (EntityUserdata) args[0];

		return PotionLibrary.toTable(data.entity.getPotionEffect(PotionEffectType.getByName(args[1].getString())));
	}
	
	private static LuaObject getPotionEffects(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'getPotionEffects' (ENTITY* expected)");
		Lua.checkArgs("getPotionEffects", args, LuaType.USERDATA, LuaType.STRING);

		EntityUserdata data = (EntityUserdata) args[0];

		LuaObject tbl = Lua.newTable();

		long i = 1;
		for(PotionEffect effect : data.entity.getActivePotionEffects())
			tbl.rawSet(i++, PotionLibrary.toTable(effect));
		
		return tbl;
	}
	
	private static LuaObject damage(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof EntityUserdata))
			throw new LuaException("bad argument #1 to 'damage' (ENTITY* expected)");
		Lua.checkArgs("damage", args, LuaType.USERDATA, LuaType.NUMBER);

		EntityUserdata data = (EntityUserdata) args[0];

		if(args.length >= 3)
		{
			if(args[2] instanceof EntityUserdata)
				data.entity.damage(args[1].getFloat(), ((EntityUserdata) args[2]).entity);
			else
				throw new LuaException("bad argument #3 to 'damage' (ENTITY* or nil expected)");
		}
		else
			data.entity.damage(args[1].getFloat());
		
		return Lua.nil();
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
			case "remainingAir":
				return Lua.newNumber(entity.getRemainingAir());
			case "maximumAir":
				return Lua.newNumber(entity.getMaximumAir());
			case "noDamageTicks":
				return Lua.newNumber(entity.getNoDamageTicks());
			case "maximumNoDamageTicks":
				return Lua.newNumber(entity.getMaximumNoDamageTicks());
			case "portalCooldown":
				return Lua.newNumber(entity.getPortalCooldown());
			case "ticksLived":
				return Lua.newNumber(entity.getTicksLived());
			case "fallDistance":
				return Lua.newNumber(entity.getFallDistance());
			case "absorptionAmount":
				return Lua.newNumber(entity.getAbsorptionAmount());
			case "eyeHeight":
				return Lua.newNumber(entity.getEyeHeight());
			case "height":
				return Lua.newNumber(entity.getHeight());
			case "lastDamage":
				return Lua.newNumber(entity.getLastDamage());
			case "width":
				return Lua.newNumber(entity.getWidth());
			case "health":
				return Lua.newNumber(entity.getHealth());
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
			case "canPickupItems":
				return Lua.newBoolean(entity.getCanPickupItems());
			case "removeWhenFarAway":
				return Lua.newBoolean(entity.getRemoveWhenFarAway());
			case "hasAI":
				return Lua.newBoolean(entity.hasAI());
			case "hasGravity":
				return Lua.newBoolean(entity.hasGravity());
			case "collidable":
				return Lua.newBoolean(entity.isCollidable());
			case "customNameVisible":
				return Lua.newBoolean(entity.isCustomNameVisible());
			case "dead":
				return Lua.newBoolean(entity.isDead());
			case "gliding":
				return Lua.newBoolean(entity.isGliding());
			case "glowing":
				return Lua.newBoolean(entity.isGlowing());
			case "isInsideVehicle":
				return Lua.newBoolean(entity.isInsideVehicle());
			case "invulnerable":
				return Lua.newBoolean(entity.isInvulnerable());
			case "isLeashed":
				return Lua.newBoolean(entity.isLeashed());
			case "isOnGround":
				return Lua.newBoolean(entity.isOnGround());
			case "isOp":
				return Lua.newBoolean(entity.isOp());
			case "isRiptiding":
				return Lua.newBoolean(entity.isRiptiding());
			case "silent":
				return Lua.newBoolean(entity.isSilent());
			case "swimming":
				return Lua.newBoolean(entity.isSwimming());
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
			case "remainingAir":
				entity.setRemainingAir((int) value.getInteger());
				return;
			case "maximumAir":
				entity.setMaximumAir((int) value.getInteger());
				return;
			case "noDamageTicks":
				entity.setNoDamageTicks((int) value.getInteger());
				return;
			case "maximumNoDamageTicks":
				entity.setMaximumNoDamageTicks((int) value.getInteger());
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
			case "absorptionAmount":
				entity.setAbsorptionAmount(value.getFloat());
				return;
			case "lastDamage":
				entity.setLastDamage(value.getFloat());
				return;
			case "health":
				entity.setHealth(value.getFloat());
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
			case "canPickupItems":
				entity.setCanPickupItems(value.getBoolean());
				return;
			case "removeWhenFarAway":
				entity.setRemoveWhenFarAway(value.getBoolean());
				return;
			case "hasAI":
				entity.setAI(value.getBoolean());
				return;
			case "hasGravity":
				entity.setGravity(value.getBoolean());
				return;
			case "collidable":
				entity.setCollidable(value.getBoolean());
				return;
			case "isCustomNameVisible":
				entity.setCustomNameVisible(value.getBoolean());
				return;
			case "dead":
				if(value.getBoolean()) entity.damage(entity.getHealth());
				return;
			case "gliding":
				entity.setGliding(value.getBoolean());
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
			case "swimming":
				entity.setSwimming(value.getBoolean());
				return;
			case "height":
			case "eyeHeight":
			case "width":
			case "maxFireTicks":
			case "entityID":
			case "canBreed":
			case "isInsideVehicle":
			case "isLeashed":
			case "isLoveMode":
			case "isOnGround":
			case "isOp":
			case "isRiptiding":
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
	public LivingEntity getUserdata()
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
	
	public static LuaObject metatable()
	{
		LuaObject tbl = Lua.newTable();
		tbl.rawSet("__name", "ENTITY*");
		tbl.rawSet("__index", tbl);
		
		tbl.rawSet("asLlama", Lua.newFunc(EntityUserdata::asLlama));
		tbl.rawSet("asPlayer", Lua.newFunc(EntityUserdata::asPlayer));
		tbl.rawSet("teleport", Lua.newFunc(EntityUserdata::teleport));
		tbl.rawSet("setRotation", Lua.newFunc(EntityUserdata::setRotation));
		tbl.rawSet("rayTrace", Lua.newFunc(EntityUserdata::rayTrace));
		tbl.rawSet("getDirection", Lua.newFunc(EntityUserdata::getDirection));
		tbl.rawSet("setDirection", Lua.newFunc(EntityUserdata::setDirection));
		tbl.rawSet("getNearbyEntities", Lua.newFunc(EntityUserdata::getNearbyEntities));
		tbl.rawSet("hasLignOfSight", Lua.newFunc(EntityUserdata::hasLignOfSight));
		tbl.rawSet("getEyeHeight", Lua.newFunc(EntityUserdata::getEyeHeight));
		tbl.rawSet("hasPotionEffect", Lua.newFunc(EntityUserdata::hasPotionEffect));
		tbl.rawSet("addPotionEffect", Lua.newFunc(EntityUserdata::addPotionEffect));
		tbl.rawSet("getPotionEffect", Lua.newFunc(EntityUserdata::getPotionEffect));
		tbl.rawSet("getPotionEffects", Lua.newFunc(EntityUserdata::getPotionEffects));
		tbl.rawSet("damage", Lua.newFunc(EntityUserdata::damage));
		tbl.rawSet("getScoreboardTags", Lua.newFunc(EntityUserdata::getScoreboardTags));
		tbl.rawSet("hasScoreboardTag", Lua.newFunc(EntityUserdata::hasScoreboardTag));
		tbl.rawSet("addScoreboardTag", Lua.newFunc(EntityUserdata::addScoreboardTag));
		tbl.rawSet("removeScoreboardTag", Lua.newFunc(EntityUserdata::removeScoreboardTag));
		tbl.rawSet("getFacing", Lua.newFunc(EntityUserdata::getFacing));

//		boolean b2 = llama.eject();
//		llama.setLeashHolder(arg0)

		return tbl;
	}
	
	private static final LuaObject entityMetatable = metatable();
}
