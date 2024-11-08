package org.liam.echoBoxEvent.api;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import java.util.UUID;

public class LPSupport {

    private static LuckPerms luckPerms;

    public static void setLuckPerms(LuckPerms luckPerms) {
        LPSupport.luckPerms = luckPerms;
    }

    public static String getLpPrefix(UUID uuid) {

        if (luckPerms == null) {
            return "couldn't find lp api, failed to add...";
        }

        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(uuid);

        if (user == null) {
            return "N/A";
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? prefix : "N/A";
    }
}
