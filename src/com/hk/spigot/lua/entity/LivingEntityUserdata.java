package com.hk.spigot.lua.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;
import com.hk.lua.LuaType;
import com.hk.spigot.lua.PotionLibrary;
import com.hk.spigot.lua.WorldLibrary;

public class LivingEntityUserdata extends EntityUserdata
{
	public final LivingEntity livingEntity;

	protected LivingEntityUserdata(LivingEntity livingEntity)
	{
		super(livingEntity);
		this.livingEntity = livingEntity;
		
		metatable = livingEntityMetatable;
	}

	private static LuaObject rayTrace(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'rayTrace' (LIVINGENTITY* expected)");
		Lua.checkArgs("rayTrace", args, LuaType.USERDATA, LuaType.NUMBER);

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];

		return WorldLibrary.toTable(data.livingEntity.rayTraceBlocks(args[1].getFloat()));
	}
	
	private static LuaObject hasLignOfSight(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'hasLignOfSight' (LIVINGENTITY* expected)");
		if(!(args[1] instanceof EntityUserdata))
			throw new LuaException("bad argument #2 to 'hasLignOfSight' (ENTITY* expected)");

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];
		EntityUserdata other = (EntityUserdata) args[1];

		return Lua.newBoolean(data.livingEntity.hasLineOfSight(other.entity));
	}
	
	private static LuaObject getEyeHeight(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'getEyeHeight' (LIVINGENTITY* expected)");
		Lua.checkArgs("getEyeHeight", args, LuaType.USERDATA, LuaType.BOOLEAN);

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];

		return Lua.newNumber(data.livingEntity.getEyeHeight(args[1].getBoolean()));
	}
	
	private static LuaObject hasPotionEffect(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'hasPotionEffect' (LIVINGENTITY* expected)");
		Lua.checkArgs("hasPotionEffect", args, LuaType.USERDATA, LuaType.STRING);

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];

		return Lua.newBoolean(data.livingEntity.hasPotionEffect(PotionEffectType.getByName(args[1].getString())));
	}
	
	private static LuaObject addPotionEffect(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'addPotionEffect' (LIVINGENTITY* expected)");
		
		LivingEntityUserdata data = (LivingEntityUserdata) args[0];
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
		
		return Lua.newBoolean(data.livingEntity.addPotionEffect(type.createEffect((int) duration, (int) amplifier)));
	}
	
	private static LuaObject getPotionEffect(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'getPotionEffect' (LIVINGENTITY* expected)");
		Lua.checkArgs("getPotionEffect", args, LuaType.USERDATA, LuaType.STRING);

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];

		return PotionLibrary.toTable(data.livingEntity.getPotionEffect(PotionEffectType.getByName(args[1].getString())));
	}
	
	private static LuaObject getPotionEffects(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'getPotionEffects' (LIVINGENTITY* expected)");
		Lua.checkArgs("getPotionEffects", args, LuaType.USERDATA, LuaType.STRING);

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];

		LuaObject tbl = Lua.newTable();

		long i = 1;
		for(PotionEffect effect : data.livingEntity.getActivePotionEffects())
			tbl.rawSet(i++, PotionLibrary.toTable(effect));
		
		return tbl;
	}
	
	private static LuaObject damage(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'damage' (LIVINGENTITY* expected)");
		Lua.checkArgs("damage", args, LuaType.USERDATA, LuaType.NUMBER);

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];

		if(args.length >= 3)
		{
			if(args[2] instanceof EntityUserdata)
				data.livingEntity.damage(args[1].getFloat(), ((EntityUserdata) args[2]).entity);
			else
				throw new LuaException("bad argument #3 to 'damage' (ENTITY* or nil expected)");
		}
		else
			data.livingEntity.damage(args[1].getFloat());
		
		return Lua.nil();
	}
	
	private static LuaObject getTarget(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'setTarget' (LLAMA* expected)");

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];
		if(data.livingEntity instanceof Mob)
			return EntityUserdata.get(((Mob) data.livingEntity).getTarget());
		else
			return Lua.nil();
	}
	
	private static LuaObject setTarget(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #1 to 'setTarget' (LIVINGENTITY* expected)");
		if(!(args[1] instanceof LivingEntityUserdata))
			throw new LuaException("bad argument #2 to 'setTarget' (LIVINGENTITY* expected)");

		LivingEntityUserdata data = (LivingEntityUserdata) args[0];
		LivingEntityUserdata target = (LivingEntityUserdata) args[1];

		boolean b = data.livingEntity instanceof Mob;
		if(b)
			((Mob) data.livingEntity).setTarget(target.livingEntity);

		return Lua.newBoolean(b);
	}
	
	@Override
	public LuaObject doIndex(LuaInterpreter interp, LuaObject key)
	{
		if(key.isString())
		{
			switch(key.getString())
			{
			case "remainingAir":
				return Lua.newNumber(livingEntity.getRemainingAir());
			case "maximumAir":
				return Lua.newNumber(livingEntity.getMaximumAir());
			case "noDamageTicks":
				return Lua.newNumber(livingEntity.getNoDamageTicks());
			case "maximumNoDamageTicks":
				return Lua.newNumber(livingEntity.getMaximumNoDamageTicks());
			case "absorptionAmount":
				return Lua.newNumber(livingEntity.getAbsorptionAmount());
			case "eyeHeight":
				return Lua.newNumber(livingEntity.getEyeHeight());
			case "lastDamage":
				return Lua.newNumber(livingEntity.getLastDamage());
			case "health":
				return Lua.newNumber(livingEntity.getHealth());
			case "canPickupItems":
				return Lua.newBoolean(livingEntity.getCanPickupItems());
			case "removeWhenFarAway":
				return Lua.newBoolean(livingEntity.getRemoveWhenFarAway());
			case "hasAI":
				return Lua.newBoolean(livingEntity.hasAI());
			case "collidable":
				return Lua.newBoolean(livingEntity.isCollidable());
			case "gliding":
				return Lua.newBoolean(livingEntity.isGliding());
			case "isLeashed":
				return Lua.newBoolean(livingEntity.isLeashed());
			case "isRiptiding":
				return Lua.newBoolean(livingEntity.isRiptiding());
			case "swimming":
				return Lua.newBoolean(livingEntity.isSwimming());
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
			case "remainingAir":
				livingEntity.setRemainingAir((int) value.getInteger());
				return;
			case "maximumAir":
				livingEntity.setMaximumAir((int) value.getInteger());
				return;
			case "noDamageTicks":
				livingEntity.setNoDamageTicks((int) value.getInteger());
				return;
			case "maximumNoDamageTicks":
				livingEntity.setMaximumNoDamageTicks((int) value.getInteger());
				return;
			case "absorptionAmount":
				livingEntity.setAbsorptionAmount(value.getFloat());
				return;
			case "lastDamage":
				livingEntity.setLastDamage(value.getFloat());
				return;
			case "health":
				livingEntity.setHealth(value.getFloat());
				return;
			case "canPickupItems":
				livingEntity.setCanPickupItems(value.getBoolean());
				return;
			case "removeWhenFarAway":
				livingEntity.setRemoveWhenFarAway(value.getBoolean());
				return;
			case "hasAI":
				livingEntity.setAI(value.getBoolean());
				return;
			case "collidable":
				livingEntity.setCollidable(value.getBoolean());
				return;
			case "gliding":
				livingEntity.setGliding(value.getBoolean());
				return;
			case "swimming":
				livingEntity.setSwimming(value.getBoolean());
				return;
			case "eyeHeight":
			case "canBreed":
			case "isLeashed":
			case "isLoveMode":
			case "isRiptiding":
				throw new LuaException("cannot change value of livingentity." + key.getString());
			}
		}
		super.doNewIndex(interp, key, value);
	}

	@Override
	public String name()
	{
		return "LIVINGENTITY*";
	}

	@Override
	public LivingEntity getUserdata()
	{
		return livingEntity;
	}
	
	public static LuaObject metatable()
	{
		LuaObject tbl = EntityUserdata.metatable();
		tbl.rawSet("__name", "LIVINGENTITY*");

		tbl.rawSet("rayTrace", Lua.newFunc(LivingEntityUserdata::rayTrace));
		tbl.rawSet("hasLignOfSight", Lua.newFunc(LivingEntityUserdata::hasLignOfSight));
		tbl.rawSet("getEyeHeight", Lua.newFunc(LivingEntityUserdata::getEyeHeight));
		tbl.rawSet("hasPotionEffect", Lua.newFunc(LivingEntityUserdata::hasPotionEffect));
		tbl.rawSet("addPotionEffect", Lua.newFunc(LivingEntityUserdata::addPotionEffect));
		tbl.rawSet("getPotionEffect", Lua.newFunc(LivingEntityUserdata::getPotionEffect));
		tbl.rawSet("getPotionEffects", Lua.newFunc(LivingEntityUserdata::getPotionEffects));
		tbl.rawSet("damage", Lua.newFunc(LivingEntityUserdata::damage));
		tbl.rawSet("getTarget", Lua.newFunc(LivingEntityUserdata::getTarget));
		tbl.rawSet("setTarget", Lua.newFunc(LivingEntityUserdata::setTarget));

		return tbl;
	}
	
	private static final LuaObject livingEntityMetatable = metatable();
}
