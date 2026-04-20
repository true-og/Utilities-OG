// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle.
package net.trueog.utilitiesog;

import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;

// Internal-only accessors for states owned by the main UtilitiesOG class.
public final class Internal {

    private Internal() {

    }

    // Live plugin instance.
    public static UtilitiesOG getPlugin() {

        return UtilitiesOG.getPluginInstance();

    }

    // Formatted Utilities-OG console prefix.
    public static String getPrefix() {

        return UtilitiesOG.getPluginPrefix();

    }

    // Shared jasync Postgres connection pool.
    public static ConnectionPool<PostgreSQLConnection> getPostgres() {

        return UtilitiesOG.getPluginPostgres();

    }

    // Three-argument functional interface used for the relational-with-args
    // placeholder registration API.
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {

        R apply(T t, U u, V v);

    }

}