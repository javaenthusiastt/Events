package org.liam.echoBoxEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.liam.echoBoxEvent.enums.EventEnums;
import org.liam.echoBoxEvent.events.glassbridge.GlassBridgeManager;
import org.liam.echoBoxEvent.events.murdermystery.MurderMysteryManager;
import org.liam.echoBoxEvent.events.spleef.SpleefManager;
import org.liam.echoBoxEvent.events.sumo.SumoManager;
import org.liam.echoBoxEvent.gamestates.GameState;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.*;

public class EventManager {

    private final SpawnsManager spawnsManager;

    private SpleefManager spleefManager;
    private SumoManager sumoManager;
    private MurderMysteryManager murderMysteryManager;
    private GlassBridgeManager glassBridgeManager;

    public GameState gameState = GameState.NO_EVENT;
    private EventEnums currentEventType = EventEnums.NONE;

    private final long JOIN_TIME = 5L;

    public final int Max_Players_For_Sumo = 32;
    public final int Min_Players_For_Sumo = 2;

    public final int Max_Players_For_Spleef = 40;
    public final int Min_Players_For_Spleef = 2;

    public final int Max_Players_For_MurderMystery = 32;
    public final int Min_Players_For_MurderMystery = 2;

    public final int Max_Players_For_GlassBridge = 32;
    public final int Min_Players_For_GlassBridge = 1;

    public final Set<Player> participants = new HashSet<>();
    public final List<Player> battlingPlayers = new ArrayList<>();
    public Set<Player> frozenPlayers = new HashSet<>();

    public boolean isEventPillarsOfFortune = false;
    public boolean isEventSpleef = false;
    public boolean isEventHideAndSeek = false;
    public boolean isEventSumo = false;
    public boolean isEventMurderMystery = false;
    public boolean isEventGlassBridge = false;

    public int sumoRoundNumber = 0;
    public String currentMap;

    public EventManager(SpawnsManager spawnsManager, SpleefManager spleefManager, SumoManager sumoManager, GlassBridgeManager glassBridgeManager) {
        this.spawnsManager = spawnsManager;
        this.spleefManager = spleefManager;
        this.sumoManager = sumoManager;
        this.glassBridgeManager = glassBridgeManager;
    }

    public void startJoinCooldown(String eventName) {
        new BukkitRunnable() {
            int countdown = (int) JOIN_TIME;

            @Override
            public void run() {

                if (countdown <= 0) {

                    if (eventName.equalsIgnoreCase("Sumo")) {
                        if (participants.size() < Min_Players_For_Sumo) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cNot enough players for Sumo."));
                            sumoManager.cancelSumoEvent();
                        } else {
                            sumoManager.sumo();
                            sumoManager.RandomFight();
                            gameState = GameState.IN_PROGRESS;
                            currentEventType = EventEnums.SUMO;
                        }
                        this.cancel();
                        return;
                    }

                    if (eventName.equalsIgnoreCase("Spleef")) {
                        if (participants.size() < Min_Players_For_Spleef) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cNot enough players for Spleef."));
                            spleefManager.cancelSpleefEvent();
                        } else {
                            spleefManager.Spleef();
                            gameState = GameState.IN_PROGRESS;
                            currentEventType = EventEnums.SPLEEF;
                        }
                        this.cancel();
                        return;
                    }

                    if (eventName.equalsIgnoreCase("Murder Mystery")){
                        if(participants.size() < Min_Players_For_MurderMystery){
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cNot enough players for Murder Mystery."));
                            murderMysteryManager.cancelMurderMystery();
                        }else{
                            murderMysteryManager.MurderMystery();
                            gameState = GameState.IN_PROGRESS;
                            currentEventType = EventEnums.MURDER_MYSTERY;
                        }
                        this.cancel();
                        return;
                    }

                    if (eventName.equalsIgnoreCase("Glass Bridge")) {
                        if (participants.size() < Min_Players_For_GlassBridge) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cNot enough players for Glass Bridge."));
                            glassBridgeManager.cancelGlassBridge();
                        } else {
                            gameState = GameState.IN_PROGRESS;
                            currentEventType = EventEnums.GLASS_BRIDGE;
                            glassBridgeManager.Start();
                        }
                        this.cancel();
                        return;
                    }
                }

                if (countdown == 45 || countdown == 30 ||  countdown == 10 || countdown <= 3) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&l► &e&l" + eventName + " &bis starting in &6&l" + countdown + " &6&lsecond" + (countdown == 1 ? "" : "&6&ls") + "&b!"));
                }

                countdown--;
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("EchoBoxEvent")), 0L, 20L);
    }

    public void setCurrentMap(String mapName) {
        this.currentMap = mapName;
    }

    public boolean isAnyEventActive() {
        return isEventSumo || isEventPillarsOfFortune || isEventHideAndSeek || isEventSpleef || isEventMurderMystery || isEventGlassBridge;
    }

    public boolean isPlayerInEvent(Player player) {
        return participants.contains(player);
    }

    public GameState getGameState() {
        return gameState;
    }

    public Set<Player> getParticipants() {
        return participants;
    }

    public void resetEvents() {
        isEventSumo = false;
        isEventPillarsOfFortune = false;
        isEventSpleef = false;
        isEventHideAndSeek = false;
        isEventMurderMystery = false;
        isEventGlassBridge = false;
        gameState = GameState.NO_EVENT;
        currentEventType = EventEnums.NONE;
        sumoRoundNumber = 0;
    }

    public void broadcastEventStart(String eventName) {
        Bukkit.broadcastMessage("");
        TextComponent joinMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&b&l► &bA game of &e&l"+eventName+" &bis available to join! &6&lCLICK HERE &bto join!"));
        joinMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join"));
        Bukkit.spigot().broadcast(joinMessage);
        Bukkit.broadcastMessage("");
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void setSpleefManager(SpleefManager spleefManager){
        this.spleefManager = spleefManager;
    }

    public void setSumoManager(SumoManager sumoManager){
        this.sumoManager = sumoManager;
    }

    public void setMurderMysterymanager(MurderMysteryManager murderMysterymanager){
        this.murderMysteryManager = murderMysterymanager;
    }

    public void setGlassBridgeManager(GlassBridgeManager glassBridgeManager){
        this.glassBridgeManager = glassBridgeManager;
    }

    public boolean isSumoEventActive() {
        return isEventSumo;
    }

    public boolean isSpleefEventActive(){
        return isEventSpleef;
    }

    public boolean isGlassBridgeEventActive(){
        return isEventGlassBridge;
    }

    public boolean isMurderMysteryEventActive(){
        return isEventMurderMystery;
    }
}
