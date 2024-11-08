package org.liam.echoBoxEvent.setup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.liam.echoBoxEvent.spawns.SpawnsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventAdminTabCompletion implements TabCompleter {

    private final List<String> events = Arrays.asList("sumo", "hideandseek", "spleef", "murdermystery", "glassbridge");
    private final List<String> sumoActions = Arrays.asList("create", "waiting", "arena", "fightlocation1", "fightlocation2");
    private final List<String> hideAndSeekActions = Arrays.asList("create", "seekerwait", "hiderswait");
    private final List<String> spleefActions = Arrays.asList("create", "waiting", "arena", "randomspawner1", "randomspawner2");
    private final List<String> murderMysteryActions = Arrays.asList("create", "waiting", "murdererspawn", "innocentspawn");
    private final List<String> glassBridgeActions = Arrays.asList("create", "waiting", "arena");

    private final SpawnsManager spawnsManager;

    public EventAdminTabCompletion(SpawnsManager spawnsManager) {
        this.spawnsManager = spawnsManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return events.stream()
                    .filter(e -> e.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String eventType = args[0].toLowerCase();
            if (events.contains(eventType)) {
                List<String> availableMaps = spawnsManager.getAvailableMaps(eventType);
                return availableMaps.isEmpty() ? List.of("NoMapsFound") :
                        availableMaps.stream()
                                .filter(m -> m.toLowerCase().startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            String eventType = args[0].toLowerCase();
            return switch (eventType) {
                case "sumo" -> getMatchingActions(sumoActions, args[2]);
                case "hideandseek" -> getMatchingActions(hideAndSeekActions, args[2]);
                case "spleef" -> getMatchingActions(spleefActions, args[2]);
                case "murdermystery" -> getMatchingActions(murderMysteryActions, args[2]);
                case "glassbridge" -> getMatchingActions(glassBridgeActions, args[2]);
                default -> new ArrayList<>();
            };
        }

        return new ArrayList<>();
    }

    private List<String> getMatchingActions(List<String> actions, String input) {
        return actions.stream()
                .filter(a -> a.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
