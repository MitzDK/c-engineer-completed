package com.github.MitzDK;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(DropSoundsConfig.GROUP)
public interface DropSoundsConfig extends Config {

    String GROUP = "cengineercompleted";

    @Range(
            min = 0,
            max = 200
    )
    @ConfigItem(
            keyName = "announcementVolume",
            name = "Announcement volume",
            description = "Adjust how loud the audio announcements are played!",
            position = 8
    )
    default int announcementVolume() {
        return 100;
    }
}
