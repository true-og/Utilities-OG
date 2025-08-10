// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.listeners;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.jasync.sql.db.QueryResult;

import net.trueog.utilitiesog.UtilitiesOG;

// A class for storing and caching FastOfflinePlayers during player join and player leave.
public class FastOfflinePlayerListener {

    // When a player joins the server, do this...
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Get the player from the event.
        final Player player = event.getPlayer();

        // Add or update player data in the offline player database.
        upsertOrUpdatePlayer(player);

        // Inform the player of the result.
        UtilitiesOG.trueogMessage(player, "Your data is cached!");

    }

    // Insert new user or update user metadata in one query depending on need.
    private CompletableFuture<QueryResult> upsertOrUpdatePlayer(Player player) {

        // Direct postgres query for inserting and/or updating a player.
        final String sql = "INSERT INTO offline_players (uuid, username) " + "VALUES ($1, $2) "
                + "ON CONFLICT (uuid) DO UPDATE " + "   SET username = EXCLUDED.username, "
                + "       last_seen = NOW();";

        // Add player UUID and player name key-value pair to database.
        return query(sql, player.getUniqueId(), player.getName());

    }

    // Send an async query using jasync Postgres.
    private CompletableFuture<QueryResult> query(String sql, Object... params) {

        // Gift wrap around jasync’s sendPreparedStatement.
        return UtilitiesOG.getPostgres().sendPreparedStatement(sql, Arrays.asList(params));

    }

}