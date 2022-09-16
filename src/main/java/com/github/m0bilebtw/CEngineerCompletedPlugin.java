package net.runelite.client.plugins.pathofrunescape;

import com.google.inject.Provides;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import static net.runelite.api.Varbits.DIARY_KARAMJA_EASY;
import static net.runelite.api.Varbits.DIARY_KARAMJA_HARD;
import static net.runelite.api.Varbits.DIARY_KARAMJA_MEDIUM;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.account.AccountSession;
import net.runelite.client.account.SessionManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Path of Runescape",
	description = "Plays sounds when loot drops",
	tags = {"c engineer", "stats", "levels", "quests", "diary", "announce"}
)

public class CEngineerCompletedPlugin extends Plugin
{
	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;

	@Inject
	private SoundEngine soundEngine;
	@Inject
	private CEngineerCompletedConfig config;
	@Inject
	private ScheduledExecutorService executor;
	@Inject
	private OkHttpClient okHttpClient;

	// Killcount and new pb patterns from runelite/ChatCommandsPlugin
	//private static final AccountSession session = sessionManager.getAccountSession();
	//private static final String CurrentUser = session.getUsername();

	private final Map<Skill, Integer> oldExperience = new EnumMap<>(Skill.class);
	private final Map<Integer, Integer> oldAchievementDiaries = new HashMap<>();

	private int lastLoginTick = -1;
	private int lastGEOfferTick = -1;
	private int lastZulrahKillTick = -1;

	@Override
	protected void startUp() throws Exception
	{
		executor.submit(() -> {
			SoundFileManager.ensureDownloadDirectoryExists();
			SoundFileManager.downloadAllMissingSounds(okHttpClient);
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		oldExperience.clear();
		oldAchievementDiaries.clear();
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch(event.getGameState())
		{
			case LOGIN_SCREEN:
			case HOPPING:
			case LOGGING_IN:
			case LOGIN_SCREEN_AUTHENTICATOR:
				oldExperience.clear();
				oldAchievementDiaries.clear();
			case CONNECTION_LOST:
				// set to -1 here in-case of race condition with varbits changing before this handler is called
				// when game state becomes LOGGED_IN
				lastLoginTick = -1;
				break;
			case LOGGED_IN:
				lastLoginTick = client.getTickCount();
				break;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getType() != ChatMessageType.CLAN_GIM_MESSAGE && chatMessage.getType() != ChatMessageType.SPAM) {
			return;
		}
		if (chatMessage.getMessage().contains(client.getLocalPlayer().getName() + " received a drop")) {
			soundEngine.playClip(Sound.ITEM_DROP_1);
		}
	}


	@Provides
	CEngineerCompletedConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CEngineerCompletedConfig.class);
	}

// Disabled - fires continuously while spinner arrow is held - when this is avoidable, can enable
//	@Subscribe
//	public void onConfigChanged(ConfigChanged event) {
//		if (CEngineerCompletedConfig.GROUP.equals(event.getGroup())) {
//			if ("announcementVolume".equals(event.getKey())) {
//				soundEngine.playClip(Sound.LEVEL_UP);
//			}
//		}
//	}
}
