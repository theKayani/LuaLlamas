package com.hk.spigot.lua;

import org.bukkit.Location;
import org.bukkit.entity.Llama;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.lua.LuaUserdata;

public class LlamaUserdata extends LuaUserdata
{
	private final Llama llama;
	
	public LlamaUserdata(Llama llama)
	{
		this.llama = llama;
		
		metatable = llamaMetatable;
	}
	
	private static LuaObject teleport(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'teleport' (LLAMA* expected)");
		Lua.checkArgs("teleport", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);

		LlamaUserdata data = (LlamaUserdata) args[0];

		double x = args[1].getFloat();
		double y = args[2].getFloat();
		double z = args[3].getFloat();
		
		return Lua.newBoolean(data.llama.teleport(new Location(data.llama.getWorld(), x, y, z)));
	}
	
	private static LuaObject setRotation(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'setRotation' (LLAMA* expected)");
		Lua.checkArgs("setRotation", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER);

		LlamaUserdata data = (LlamaUserdata) args[0];

		double pitch = args[1].getFloat();
		double yaw = args[2].getFloat();
		data.llama.setRotation((float) pitch, (float) yaw);

		return Lua.nil();
	}
	
	private static LuaObject rayTrace(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'rayTrace' (LLAMA* expected)");
		Lua.checkArgs("rayTrace", args, LuaType.USERDATA, LuaType.NUMBER);

		LlamaUserdata data = (LlamaUserdata) args[0];

		RayTraceResult result = data.llama.rayTraceBlocks(args[1].getFloat());

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
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'getDirection' (LLAMA* expected)");
		Lua.checkArgs("getDirection", args, LuaType.USERDATA);

		LlamaUserdata data = (LlamaUserdata) args[0];
		Vector v = data.llama.getLocation().getDirection();

		LuaObject tbl = Lua.newTable();
		
		tbl.rawSet("x", v.getX());
		tbl.rawSet("y", v.getY());
		tbl.rawSet("z", v.getZ());
		
		return tbl;
	}
	
	private static LuaObject setDirection(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'setDirection' (LLAMA* expected)");
		Lua.checkArgs("setDirection", args, LuaType.USERDATA, LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER);

		LlamaUserdata data = (LlamaUserdata) args[0];
		
		double x = args[1].getFloat();
		double y = args[2].getFloat();
		double z = args[3].getFloat();
		
		data.llama.getLocation().setDirection(new Vector(x, y, z));

		return Lua.nil();
	}

	@Override
	public LuaObject doIndex(LuaInterpreter interp, LuaObject key)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
			case "age":
				return Lua.newNumber(llama.getAge());
			case "domestication":
				return Lua.newNumber(llama.getDomestication());
			case "maxDomestication":
				return Lua.newNumber(llama.getMaxDomestication());
			case "entityID":
				return Lua.newNumber(llama.getEntityId());
			case "fireTicks":
				return Lua.newNumber(llama.getFireTicks());
			case "maxFireTicks":
				return Lua.newNumber(llama.getMaxFireTicks());
			case "loveModeTicks":
				return Lua.newNumber(llama.getLoveModeTicks());
			case "remainingAir":
				return Lua.newNumber(llama.getRemainingAir());
			case "maximumAir":
				return Lua.newNumber(llama.getMaximumAir());
			case "noDamageTicks":
				return Lua.newNumber(llama.getNoDamageTicks());
			case "maximumNoDamageTicks":
				return Lua.newNumber(llama.getMaximumNoDamageTicks());
			case "portalCooldown":
				return Lua.newNumber(llama.getPortalCooldown());
			case "ticksLived":
				return Lua.newNumber(llama.getTicksLived());
			case "seed":
				return Lua.newNumber(llama.getSeed());
			case "fallDistance":
				return Lua.newNumber(llama.getFallDistance());
			case "absorptionAmount":
				return Lua.newNumber(llama.getAbsorptionAmount());
			case "eyeHeight":
				return Lua.newNumber(llama.getEyeHeight());
			case "jumpStrength":
				return Lua.newNumber(llama.getJumpStrength());
			case "height":
				return Lua.newNumber(llama.getHeight());
			case "lastDamage":
				return Lua.newNumber(llama.getLastDamage());
			case "width":
				return Lua.newNumber(llama.getWidth());
			case "health":
				return Lua.newNumber(llama.getHealth());
			case "posX":
				return Lua.newNumber(llama.getLocation().getX());
			case "posY":
				return Lua.newNumber(llama.getLocation().getY());
			case "posZ":
				return Lua.newNumber(llama.getLocation().getZ());
			case "velX":
				return Lua.newNumber(llama.getVelocity().getX());
			case "velY":
				return Lua.newNumber(llama.getVelocity().getY());
			case "velZ":
				return Lua.newNumber(llama.getVelocity().getZ());
			case "pitch":
				return Lua.newNumber(llama.getLocation().getPitch());
			case "yaw":
				return Lua.newNumber(llama.getLocation().getYaw());
			case "canBreed":
				return Lua.newBoolean(llama.canBreed());
			case "ageLock":
				return Lua.newBoolean(llama.getAgeLock());
			case "canPickupItems":
				return Lua.newBoolean(llama.getCanPickupItems());
			case "removeWhenFarAway":
				return Lua.newBoolean(llama.getRemoveWhenFarAway());
			case "hasAI":
				return Lua.newBoolean(llama.hasAI());
			case "hasGravity":
				return Lua.newBoolean(llama.hasGravity());
			case "collidable":
				return Lua.newBoolean(llama.isCollidable());
			case "customNameVisible":
				return Lua.newBoolean(llama.isCustomNameVisible());
			case "dead":
				return Lua.newBoolean(llama.isDead());
			case "gliding":
				return Lua.newBoolean(llama.isGliding());
			case "glowing":
				return Lua.newBoolean(llama.isGlowing());
			case "isInsideVehicle":
				return Lua.newBoolean(llama.isInsideVehicle());
			case "invulnerable":
				return Lua.newBoolean(llama.isInvulnerable());
			case "isLeashed":
				return Lua.newBoolean(llama.isLeashed());
			case "isLoveMode":
				return Lua.newBoolean(llama.isLoveMode());
			case "isOnGround":
				return Lua.newBoolean(llama.isOnGround());
			case "isOp":
				return Lua.newBoolean(llama.isOp());
			case "isRiptiding":
				return Lua.newBoolean(llama.isRiptiding());
			case "silent":
				return Lua.newBoolean(llama.isSilent());
			case "swimming":
				return Lua.newBoolean(llama.isSwimming());
			case "tamed":
				return Lua.newBoolean(llama.isTamed());
			case "uuid":
				return Lua.newString(llama.getUniqueId().toString());
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
			case "age":
				llama.setAge((int) value.getInteger());
				return;
			case "domestication":
				llama.setDomestication((int) value.getInteger());
				return;
			case "maxDomestication":
				llama.setMaxDomestication((int) value.getInteger());
				return;
			case "fireTicks":
				llama.setFireTicks((int) value.getInteger());
				return;
			case "loveModeTicks":
				llama.setLoveModeTicks((int) value.getInteger());
				return;
			case "remainingAir":
				llama.setRemainingAir((int) value.getInteger());
				return;
			case "maximumAir":
				llama.setMaximumAir((int) value.getInteger());
				return;
			case "noDamageTicks":
				llama.setNoDamageTicks((int) value.getInteger());
				return;
			case "maximumNoDamageTicks":
				llama.setMaximumNoDamageTicks((int) value.getInteger());
				return;
			case "portalCooldown":
				llama.setPortalCooldown((int) value.getInteger());
				return;
			case "ticksLived":
				llama.setTicksLived((int) value.getInteger());
				return;
			case "seed":
				llama.setSeed(value.getInteger());
				return;
			case "fallDistance":
				llama.setFallDistance((float) value.getFloat());
				return;
			case "absorptionAmount":
				llama.setAbsorptionAmount(value.getFloat());
				return;
			case "jumpStrength":
				llama.setJumpStrength(value.getFloat());
				return;
			case "lastDamage":
				llama.setLastDamage(value.getFloat());
				return;
			case "health":
				llama.setHealth(value.getFloat());
				return;
			case "posX":
				llama.getLocation().setX(value.getFloat());
				return;
			case "posY":
				llama.getLocation().setY(value.getFloat());
				return;
			case "posZ":
				llama.getLocation().setZ(value.getFloat());
				return;
			case "velX":
				llama.getVelocity().setX(value.getFloat());
				return;
			case "velY":
				llama.getVelocity().setY(value.getFloat());
				return;
			case "velZ":
				llama.getVelocity().setZ(value.getFloat());
				return;
			case "pitch":
				llama.getLocation().setPitch((float) value.getFloat());
				return;
			case "yaw":
				llama.getLocation().setYaw((float) value.getFloat());
				return;
			case "ageLock":
				llama.setAgeLock(value.getBoolean());
				return;
			case "canPickupItems":
				llama.setCanPickupItems(value.getBoolean());
				return;
			case "removeWhenFarAway":
				llama.setRemoveWhenFarAway(value.getBoolean());
				return;
			case "hasAI":
				llama.setAI(value.getBoolean());
				return;
			case "hasGravity":
				llama.setGravity(value.getBoolean());
				return;
			case "collidable":
				llama.setCollidable(value.getBoolean());
				return;
			case "isCustomNameVisible":
				llama.setCustomNameVisible(value.getBoolean());
				return;
			case "dead":
				if(value.getBoolean()) llama.damage(llama.getHealth());
				return;
			case "gliding":
				llama.setGliding(value.getBoolean());
				return;
			case "glowing":
				llama.setGlowing(value.getBoolean());
				return;
			case "invulnerable":
				llama.setInvulnerable(value.getBoolean());
				return;
			case "silent":
				llama.setSilent(value.getBoolean());
				return;
			case "swimming":
				llama.setSwimming(value.getBoolean());
				return;
			case "tamed":
				llama.setTamed(value.getBoolean());
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
				throw new LuaException("cannot change value of llama." + key.getString());
			}
		}
		super.doNewIndex(interp, key, value);
	}

	@Override
	public String getString(LuaInterpreter interp)
	{
		return "Llama";
	}

	@Override
	public String name()
	{
		return "LLAMA*";
	}

	@Override
	public Llama getUserdata()
	{
		return llama;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof LlamaUserdata && llama.equals(((LlamaUserdata) o).llama);
	}
	
	@Override
	public int hashCode()
	{
		return llama.hashCode();
	}
	
	private static final LuaObject llamaMetatable = Lua.newTable();
	
	static
	{
		llamaMetatable.rawSet("__name", "LLAMA*");
		llamaMetatable.rawSet("__index", llamaMetatable);

		llamaMetatable.rawSet("teleport", Lua.newFunc(LlamaUserdata::teleport));
		llamaMetatable.rawSet("setRotation", Lua.newFunc(LlamaUserdata::setRotation));
		llamaMetatable.rawSet("rayTrace", Lua.newFunc(LlamaUserdata::rayTrace));
		llamaMetatable.rawSet("getDirection", Lua.newFunc(LlamaUserdata::getDirection));
		llamaMetatable.rawSet("setDirection", Lua.newFunc(LlamaUserdata::setDirection));
//		llamaMetatable.rawSet("addPotionEffect", Lua.newFunc(LlamaUserdata::addPotionEffect));
//		boolean b8 = llama.hasLineOfSight(arg0);
//		boolean b9 = llama.hasPotionEffect(arg0);
//		double d2 = llama.getEyeHeight(/* boolean */);
//		boolean b2 = llama.eject();
//		llama.setLeashHolder(arg0)
//		llama.teleport(arg0)
	}
}
