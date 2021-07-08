package com.hk.spigot;

public class Helps
{
	public static <T extends Enum<T>> T getEnum(Class<T> cls, String name)
	{
		try
		{
			return Enum.valueOf(cls, name);
		}
		catch(IllegalArgumentException ex)
		{
			return null;
		}
	}
}