/* This is free and unencumbered software released into the public domain */

/* ------------------------------ Plugins ------------------------------ */
plugins {
    id("java") // Import Java plugin.
    id("java-library") // Import Java Library plugin.
    id("com.diffplug.spotless") version "7.0.4" // Import Spotless plugin.
    id("com.gradleup.shadow") version "8.3.6" // Import Shadow plugin.
    id("checkstyle") // Import Checkstyle plugin.
    eclipse // Import Eclipse plugin.
}

/* ------------------------------- JDK --------------------------------- */
java {
    sourceCompatibility = JavaVersion.VERSION_17 // Compile with JDK 17 compatibility.
    toolchain { // Select Java toolchain.
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17.
        vendor.set(JvmVendorSpec.GRAAL_VM) // Use GraalVM CE.
    }
}

/* ----------------------------- Metadata ------------------------------ */
group = "net.trueog.utilities-og" // Declare bundle identifier.

version = "1.7" // Declare plugin version (will be in .jar).

val apiVersion = "1.19" // Declare minecraft server target version.

/* ----------------------------- Resources ----------------------------- */
tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to version, "apiVersion" to apiVersion)
    inputs.properties(props) // Indicates to rerun if version changes.
    filesMatching("plugin.yml") { expand(props) }
    from("LICENSE") { into("/") } // Bundle licenses into jarfiles.
}

/* ---------------------------- Repos ---------------------------------- */
repositories {
    mavenCentral() // Import the Maven Central Maven Repository.
    gradlePluginPortal() // Import the Gradle Plugin Portal Maven Repository.
    maven { url = uri("https://repo.purpurmc.org/snapshots") } // Import the PurpurMC Maven Repository.
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") } // Import the PaperMC Maven Repository.
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") } // Import the CodeMC Maven Repository.
    maven { url = uri("https://maven.enginehub.org/repo/") } // Import the EngineHub Maven Repository.
}

/* ---------------------- Java project deps ---------------------------- */
dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.19.4-R0.1-SNAPSHOT") // Declare Purpur API version to be packaged.
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8") // Import WorldGuard API.
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.11.2") // Import NBT API.
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.2.3") // Import MiniPlaceholders API.
    compileOnly("net.luckperms:api:5.4") // Import LuckPerms API.
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.3") // Import JUnit 5 API.
    testImplementation("com.github.seeseemelk:MockBukkit-v1.19:2.29.0") // Import MockBukkit 1.19.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") // Import JUnit's engine.
}

/* ---------------------- Reproducible jars ---------------------------- */
tasks.withType<AbstractArchiveTask>().configureEach { // Ensure reproducible .jars
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

/* ----------------------------- Shadow -------------------------------- */
tasks.shadowJar {
    exclude("io.github.miniplaceholders.*") // Exclude the MiniPlaceholders package from being shadowed.
    archiveClassifier.set("") // Use empty string instead of null.
    minimize()
}

tasks.jar { archiveClassifier.set("part") } // Applies to root jarfile only.

tasks.build { dependsOn(tasks.spotlessApply, tasks.shadowJar) } // Build depends on spotless and shadow.

/* --------------------------- Javac opts ------------------------------- */
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation") // Trigger deprecation warning messages.
    options.encoding = "UTF-8" // Use UTF-8 file encoding.
    options.isFork = true
}

/* --------------------------------- Testing ---------------------------- */
tasks.withType<Test>().configureEach {
    useJUnitPlatform() // Enable testing with JUnit 5.
}

/* ----------------------------- Auto Formatting ------------------------ */
spotless {
    java {
        eclipse().configFile("config/formatter/eclipse-java-formatter.xml") // Eclipse java formatting.
        leadingTabsToSpaces() // Convert leftover leading tabs to spaces.
        removeUnusedImports() // Remove imports that aren't being called.
    }
    kotlinGradle {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) } // JetBrains Kotlin formatting.
        target("build.gradle.kts", "settings.gradle.kts") // Gradle files to format.
    }
}

checkstyle {
    toolVersion = "10.18.1" // Declare checkstyle version to use.
    configFile = file("config/checkstyle/checkstyle.xml") // Point checkstyle to config file.
    isIgnoreFailures = true // Don't fail the build if checkstyle does not pass.
    isShowViolations = true // Show the violations in any IDE with the checkstyle plugin.
}

tasks.named("compileJava") {
    dependsOn("spotlessApply") // Run spotless before compiling with the JDK.
}

tasks.named("spotlessCheck") {
    dependsOn("spotlessApply") // Run spotless before checking if spotless ran.
}
