package org.liam.echoBoxEvent;

import com.earth2me.essentials.Essentials;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.liam.echoBoxEvent.api.LPSupport;
import org.liam.echoBoxEvent.events.glassbridge.GlassBridgeCommand;
import org.liam.echoBoxEvent.events.glassbridge.GlassBridgeListener;
import org.liam.echoBoxEvent.events.glassbridge.GlassBridgeManager;
import org.liam.echoBoxEvent.maps.GlassBridgeMapReset;
import org.liam.echoBoxEvent.setup.EventAdminCommand;
import org.liam.echoBoxEvent.setup.EventAdminTabCompletion;
import org.liam.echoBoxEvent.commands.*;
import org.liam.echoBoxEvent.cosmetics.TestAbilitiesCommand;
import org.liam.echoBoxEvent.events.murdermystery.MurderMysterCommand;
import org.liam.echoBoxEvent.events.murdermystery.MurderMysteryManager;
import org.liam.echoBoxEvent.maps.SpleefMapReset;
import org.liam.echoBoxEvent.events.spleef.SpleefCommand;
import org.liam.echoBoxEvent.events.spleef.SpleefListener;
import org.liam.echoBoxEvent.events.spleef.SpleefManager;
import org.liam.echoBoxEvent.events.sumo.SumoCommand;
import org.liam.echoBoxEvent.events.sumo.SumoListener;
import org.liam.echoBoxEvent.events.sumo.SumoManager;
import org.liam.echoBoxEvent.players.Data.PlayerData;
import org.liam.echoBoxEvent.spawns.SpawnsManager;
import org.liam.echoBoxEvent.api.PapiSupport;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends JavaPlugin {

    private File spawnsFile;
    private FileConfiguration spawnsConfig;
    private static Main main;
    private static Essentials essentials;
    private static LuckPerms luckPerms;
    private PlayerData playerData;

    @Override
    public void onEnable() {
        this.playerData = new PlayerData(this.getDataFolder());
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        luckPerms = LuckPermsProvider.get();

        main = this;
        createSpawnsConfig();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiSupport(playerData).register();
        }

        SpawnsManager spawnsManager = new SpawnsManager(this);
        EventManager eventManager = new EventManager(null, null, null, null);
        SpleefManager spleefManager = new SpleefManager(eventManager, spawnsManager);
        SumoManager sumoManager = new SumoManager(eventManager, spawnsManager, playerData);
        MurderMysteryManager murderMysteryManager = new MurderMysteryManager(eventManager, spawnsManager);
        GlassBridgeManager glassBridgeManager = new GlassBridgeManager(eventManager, spawnsManager, playerData);

        eventManager.setSpleefManager(spleefManager);
        eventManager.setSumoManager(sumoManager);
        eventManager.setMurderMysterymanager(murderMysteryManager);
        eventManager.setGlassBridgeManager(glassBridgeManager);

        Objects.requireNonNull(getCommand("event")).setExecutor(new HostEventCommand(playerData));
        Objects.requireNonNull(getCommand("joinevent")).setExecutor(new JoinEventCommand(eventManager, spleefManager, sumoManager, murderMysteryManager, glassBridgeManager));
        Objects.requireNonNull(getCommand("leaveevent")).setExecutor(new LeaveEventCommand(eventManager, sumoManager, spleefManager, glassBridgeManager));
        Objects.requireNonNull(getCommand("echoboxsumo")).setExecutor(new SumoCommand(eventManager,spawnsManager,sumoManager));
        Objects.requireNonNull(getCommand("echoboxspleef")).setExecutor(new SpleefCommand(spawnsManager,eventManager,spleefManager));
        Objects.requireNonNull(getCommand("eventadmin")).setExecutor(new EventAdminCommand(spawnsManager));
        Objects.requireNonNull(getCommand("eventadmin")).setTabCompleter(new EventAdminTabCompletion(spawnsManager));
        Objects.requireNonNull(getCommand("echoboxmurdermystery")).setExecutor(new MurderMysterCommand(eventManager, spawnsManager, murderMysteryManager));
        Objects.requireNonNull(getCommand("echoboxglassbridge")).setExecutor(new GlassBridgeCommand(eventManager, spawnsManager, glassBridgeManager));
        Objects.requireNonNull(getCommand("eventinfo")).setExecutor(new InformationCommand());
        Objects.requireNonNull(getCommand("eventstats")).setExecutor(new EventInformation(playerData));
        Objects.requireNonNull(getCommand("eventstore")).setExecutor(new EventStore(playerData));

        getServer().getPluginManager().registerEvents(new EventInformation(playerData), this);
        getServer().getPluginManager().registerEvents(new EventStore(playerData), this);
        getServer().getPluginManager().registerEvents(new SumoListener(eventManager,spawnsManager,this, sumoManager, playerData), this);
        getServer().getPluginManager().registerEvents(new SpleefListener(eventManager, spleefManager, spawnsManager),this);
        getServer().getPluginManager().registerEvents(new SpleefMapReset(eventManager),this);
        getServer().getPluginManager().registerEvents(new HostEventCommand(playerData), this);

        getServer().getPluginManager().registerEvents(new GlassBridgeListener(eventManager, glassBridgeManager, spawnsManager), this);

        Objects.requireNonNull(getCommand("eventcosmetics")).setExecutor(new TestAbilitiesCommand());

        LPSupport.setLuckPerms(luckPerms);
    }

    @Override
    public void onDisable(){
        GlassBridgeMapReset.resetGreenBlocks();
        GlassBridgeMapReset.resetRedBlocks();
    }

    public void createSpawnsConfig() {
        spawnsFile = new File(getDataFolder(), "spawns.yml");
        if (!spawnsFile.exists()) {
            spawnsFile.getParentFile().mkdirs();
            saveResource("spawns.yml", false);
        }
        spawnsConfig = YamlConfiguration.loadConfiguration(spawnsFile);
    }

    public FileConfiguration getSpawnsConfig() {
        if (spawnsConfig == null) {
            createSpawnsConfig();
        }
        return spawnsConfig;
    }

    public void saveSpawnsConfig() {
        try {
            getSpawnsConfig().save(spawnsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Main getPluginInstance(){
        return main;
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
