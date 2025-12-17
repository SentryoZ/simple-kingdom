package me.sentryozvn.simpleKingdom.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Kingdom {
    private final String name;
    private final String tag;
    private final String permission;
    private final Set<UUID> members = new HashSet<>();

    public Kingdom(String name, String tag, String permission) {
        this.name = name;
        this.tag = tag;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getPermission() {
        return permission;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }

    public int getMemberCount() {
        return members.size();
    }
}
