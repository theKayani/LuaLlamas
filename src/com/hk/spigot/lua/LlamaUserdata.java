package com.hk.spigot.lua;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;

import com.hk.lua.Lua;
import com.hk.lua.LuaException;
import com.hk.lua.LuaInterpreter;
import com.hk.lua.LuaObject;

public class LlamaUserdata extends EntityUserdata
{
	private final Llama llama;
	
	public LlamaUserdata(Llama llama)
	{
		super(llama);
		this.llama = llama;
		
		metatable = llamaMetatable;
	}
	
	private static LuaObject getTarget(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'setTarget' (LLAMA* expected)");

		LlamaUserdata data = (LlamaUserdata) args[0];
		LivingEntity target = data.llama.getTarget();

		if(target != null)
			return new EntityUserdata(target);
		else
			return Lua.nil();
	}
	
	private static LuaObject setTarget(LuaInterpreter interp, LuaObject[] args)
	{
		if(!(args[0] instanceof LlamaUserdata))
			throw new LuaException("bad argument #1 to 'setTarget' (LLAMA* expected)");
		if(!(args[1] instanceof EntityUserdata))
			throw new LuaException("bad argument #2 to 'setTarget' (ENTITY* expected)");

		LlamaUserdata data = (LlamaUserdata) args[0];
		EntityUserdata target = (EntityUserdata) args[1];

		data.llama.setTarget(target.entity);

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
			case "loveModeTicks":
				return Lua.newNumber(llama.getLoveModeTicks());
			case "seed":
				return Lua.newNumber(llama.getSeed());
			case "jumpStrength":
				return Lua.newNumber(llama.getJumpStrength());
			case "canBreed":
				return Lua.newBoolean(llama.canBreed());
			case "ageLock":
				return Lua.newBoolean(llama.getAgeLock());
			case "isLoveMode":
				return Lua.newBoolean(llama.isLoveMode());
			case "tamed":
				return Lua.newBoolean(llama.isTamed());
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
			case "loveModeTicks":
				llama.setLoveModeTicks((int) value.getInteger());
				return;
			case "seed":
				llama.setSeed(value.getInteger());
				return;
			case "jumpStrength":
				llama.setJumpStrength(value.getFloat());
				return;
			case "ageLock":
				llama.setAgeLock(value.getBoolean());
				return;
			case "tamed":
				llama.setTamed(value.getBoolean());
				return;
			case "canBreed":
			case "isLoveMode":
				throw new LuaException("cannot change value of llama." + key.getString());
			}
		}
		super.doNewIndex(interp, key, value);
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
	
	public static LuaObject metatable()
	{
		LuaObject tbl = EntityUserdata.metatable();
		tbl.rawSet("__name", "LLAMA*");

		tbl.rawSet("getTarget", Lua.newFunc(LlamaUserdata::getTarget));
		tbl.rawSet("setTarget", Lua.newFunc(LlamaUserdata::setTarget));
		
		return tbl;
	}
	
	private static final LuaObject llamaMetatable = metatable();
}
