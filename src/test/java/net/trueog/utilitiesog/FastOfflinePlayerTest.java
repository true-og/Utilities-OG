// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle.
package net.trueog.utilitiesog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;
import com.github.jasync.sql.db.pool.ConnectionPool;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

// Test cases for FastOfflinePlayer.java
class FastOfflinePlayerTest {

    // Declare a test UUID.
    final UUID testUUID = UUID.randomUUID();
    // Declare a test username.
    final String testName = "NotAlexNoyle";
    // Declare a mock server container using MockBukkit.
    private static ServerMock server;

    // Runs before all tests.
    @BeforeAll
    static void setUp() {

        // Initialize the MockBukkit server.
        server = MockBukkit.mock();

    }

    // Runs after each test.
    @AfterEach
    void resetDb() {

        // Ensure each test gets a clean supplier.
        FastOfflinePlayer.resetConnectionSupplierForTests();

    }

    // Runs after all tests.
    @AfterAll
    static void tearDown() {

        // Bring down MockBukkit server.
        MockBukkit.unmock();

    }

    // TODO: Uncomment and finish
    /*
     * @Test void testIsOp() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetOp() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     */

    // FastOfflinePlayer.serialize() test.
    @Test
    void testSerialize() {

        // Declare a test player.
        final FastOfflinePlayer testSerializePlayer = FastOfflinePlayer.deserialize(testUUID, Map.of("name", testName));

        // JUnit: Compare the expected map to the FastOfflinePlayer map.
        assertEquals(Map.of("name", testName), testSerializePlayer.serialize());

    }

    // FastOfflinePlayer.deserialize() test.
    @Test
    void testDeserialize() {

        // Compose a deserialization control group map.
        final Map<String, Object> yaml = Map.of("name", testName);

        // Declare a test player.
        final FastOfflinePlayer testDeserializePlayer = FastOfflinePlayer.deserialize(testUUID, yaml);

        // JUnit: Compare the expected UUID to the FastOfflinePlayer UUID.
        assertEquals(testUUID, testDeserializePlayer.getUniqueId());
        // JUnit: Compare the expected name to the FastOfflinePlayer name.
        assertEquals(testName, testDeserializePlayer.getName());

    }

    // TODO: Uncomment and finish
    /*
     * @Test void testIsOnline() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     */

    // FastOfflinePlayer.getName() test.
    @Test
    void testGetName() {

        // Declare a jasync Postgres connection pool.
        final ConnectionPool<?> pool = mockPoolReturning(
                mockQueryResult(mockResultSet(mockRow(Map.of("username", testName)))));
        installMockDb(pool);

        // Declare a test player.
        final FastOfflinePlayer testGetNamePlayer = new FastOfflinePlayer(UUID.randomUUID());

        // JUnit: Compare the expected name to the actual name from database.
        assertEquals(testName, testGetNamePlayer.getNameAsync().join());
        // JUnit: Compare the expected name to the actual name from cache.
        assertEquals(testName, testGetNamePlayer.getName());

        // Verify that multiple queries are not overlapping.
        verify(pool, times(1)).sendPreparedStatement(anyString(), anyList());

    }

    // FastOfflinePlayer.getUniqueID() test.
    @Test
    void testGetUniqueId() {

        // Declare a test player.
        final FastOfflinePlayer uuidTestPlayer = new FastOfflinePlayer(testUUID);

        // JUnit: Compare the expected UUID to the FastOfflinePlayer UUID.
        assertEquals(testUUID, uuidTestPlayer.getUniqueId());

    }

    // TODO: Uncomment and finish
    /*
     * @Test void testGetPlayerProfile() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIsBanned() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIsWhitelisted() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetWhitelisted() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetPlayer() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetFirstPlayed() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetLastPlayed() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testHasPlayedBefore() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetBedSpawnLocation() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetLastLogin() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetLastSeen() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIncrementStatisticStatistic() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testDecrementStatisticStatistic() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIncrementStatisticStatisticInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testDecrementStatisticStatisticInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetStatisticStatisticInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetStatisticStatistic() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIncrementStatisticStatisticMaterial() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testDecrementStatisticStatisticMaterial() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetStatisticStatisticMaterial() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIncrementStatisticStatisticMaterialInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testDecrementStatisticStatisticMaterialInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetStatisticStatisticMaterialInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIncrementStatisticStatisticEntityType() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testDecrementStatisticStatisticEntityType() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetStatisticStatisticEntityType() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIncrementStatisticStatisticEntityTypeInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testDecrementStatisticStatisticEntityTypeInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetStatisticStatisticEntityTypeInt() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetLastDeathLocation() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetAllowFlight() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetAllowFlight() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testIsFlying() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetFlying() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetFlySpeed() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testSetWalkSpeed() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetFlySpeed() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetWalkSpeed() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testGetLocation() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testTeleportOfflineLocation() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testTeleportOfflineLocationTeleportCause() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testTeleportOfflineAsyncLocation() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     * 
     * @Test void testTeleportOfflineAsyncLocationTeleportCause() {
     * 
     * fail("Not yet implemented");
     * 
     * }
     */

    // Declare a fake jasync Postgres row for tests to use.
    private static RowData mockRow(Map<String, Object> cols) {

        // Declare a Mockito mock for a single jasync Postgres row.
        final RowData row = mock(RowData.class);

        // When getString(columnName) is called, look up the value from the map.
        when(row.getString(anyString())).thenAnswer(inv -> {

            // If absent, return null; otherwise, coerce to String to match the API.
            final Object v = cols.get(inv.getArgument(0));
            return v == null ? null : String.valueOf(v);

        });

        // Return the configured mock row.
        return row;

    }

    // Declare a fake jasync Postgres result set for tests to use.
    private static ResultSet mockResultSet(RowData... rowArray) {

        // Create a Mockito mock for a jasync Postgres ResultSet.
        final ResultSet rs = mock(ResultSet.class);

        // Mock a result set. isEmpty() should reflect whether any rows have been passed
        // in.
        when(rs.isEmpty()).thenReturn(rowArray.length == 0);

        // Loop that runs once for each result row.
        for (int i = 0; i < rowArray.length; i++) {

            // Stub indexed access to return the corresponding RowData from the array.
            when(rs.get(i)).thenReturn(rowArray[i]);

        }

        // Return the configured mock result set.
        return rs;

    }

    // Declare a fake jasync Postgres query result for tests to use.
    private static QueryResult mockQueryResult(ResultSet rows) {

        // Create a Mockito mock for a jasync Postgres QueryResult that wraps a given
        // ResultSet.
        final QueryResult qr = mock(QueryResult.class);

        // Mock a query result.
        when(qr.getRows()).thenReturn(rows);

        // Return the mocked query result.
        return qr;

    }

    // Declare a fake jasync Postgres ConnectionPool for tests to use.
    private static ConnectionPool<?> mockPoolReturning(QueryResult result) {

        // Create a Mockito mock for a jasync Postgres ConnectionPool.
        final ConnectionPool<?> pool = mock(ConnectionPool.class);

        // Mock a ConnectionPool.
        when(pool.sendPreparedStatement(anyString(), anyList())).thenReturn(CompletableFuture.completedFuture(result));

        // Return the mocked mock pool.
        return pool;

    }

    // Initialize the mocked jasync Postgres database.
    private static void installMockDb(ConnectionPool<?> pool) {

        // Install the mocked pool into FastOfflinePlayer via its test seam so code
        // under test uses this pool instead of touching UtilitiesOG. This prevents
        // missing class errors due to not all UtilitiesOG.java calls being on the test
        // class path.
        FastOfflinePlayer.overrideConnectionSupplierForTests(() -> pool);

    }

}