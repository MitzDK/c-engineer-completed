package com.github.MitzDK;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DropSoundsCompletedPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DropSoundsCompletedPlugin.class);
		RuneLite.main(args);
	}
}