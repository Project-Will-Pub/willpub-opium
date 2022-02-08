package xyz.rk0cc.willpub.cmd;

import xyz.rk0cc.willpub.cmd.subcmd.PubSubCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A factory that build {@link Process} that running {@link PubSubCommand} to local machine.
 * <br/>
 * This factory will fetch <code>WILLPUB_OPIUM_DART_FLUTTER_EXEC</code> values when first execution of
 * {@link #getInstance()}. The environment variable should be like this:
 * <br/>
 * <code>(path_to_dart_executable);([optional] path_to_flutter_executable)</code>
 * <br/>
 * It should be done absolutely nothing when getting Flutter from
 * <code>git clone https://github.com/flutter/flutter.git -b stable</code> and set argument with above steps. Otherwise,
 * it required renaming back to <code>dart</code> and <code>flutter</code> for representing SDK function.
 * <br/>
 * To execute subcommand, please refer to {@link #buildPubProcess(File, boolean, PubSubCommand)}'s steps.
 *
 * @since 1.0.0
 */
public final class PubCommandProcessFactory {
    /**
     * A static boolean option that enable flutter version check after OPIUM execution.
     * <br/>
     * It added execution command <code>flutter --version-check</code> in
     * {@link Runtime#addShutdownHook(Thread) shutdown hook} that will be executed after OPIUM execution flows.
     */
    public static boolean RE_ENABLE_FLUTTER_VERSION_CHECK = true;

    private static PubCommandProcessFactory INSTANCE = null;
    private File dartSDKExec, flutterSDKExec = null;

    private PubCommandProcessFactory() throws IOException {
        String opiumEnvVal = System.getenv("WILLPUB_OPIUM_DART_FLUTTER_EXEC");
        String[] opiumEnv = Objects.isNull(opiumEnvVal) ? new String[]{} : opiumEnvVal.split(";");

        switch (opiumEnv.length) {
            case 2:
            default:
                // Get Flutter SDK, ignore paths which behind the second value.
                assert !opiumEnv[1].isBlank();
                flutterSDKExec = new File(opiumEnv[1]);
                if (
                        !flutterSDKExec.getName().startsWith("flutter")
                        || !flutterSDKExec.isAbsolute()
                        || !flutterSDKExec.canExecute()
                ) throw new IOException("The second 'WILLPUB_OPIUM_DART_FLUTTER_EXEC' should be absolute path of flutter executable");

                // Disable flutter check update during execution under opium
                Process disableFU = new ProcessBuilder(flutterSDKExec.getPath(), "--no-version-check")
                        .start();

                if (RE_ENABLE_FLUTTER_VERSION_CHECK)
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            new ProcessBuilder(flutterSDKExec.getPath(), "--version-check")
                                    .start()
                                    .waitFor();
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException("Fail to recover Flutter version check", e);
                        }
                    }));

                try {
                    disableFU.waitFor();
                } catch (InterruptedException e) {
                    throw new IOException("Interruption found when waiting disabling Flutter update", e);
                }
            case 1:
                // Get Dart SDK.
                assert !opiumEnv[0].isBlank();
                dartSDKExec = new File(opiumEnv[0]);
                if (
                        !dartSDKExec.getName().startsWith("dart")
                        || !dartSDKExec.isAbsolute()
                        || !dartSDKExec.canExecute()
                ) throw new IOException("The first 'WILLPUB_OPIUM_DART_FLUTTER_EXEC' should be absolute path of dart executable");
                break;
            case 0:
                // When applied with nothing.
                throw new IOException("Can not found 'WILLPUB_OPIUM_DART_FLUTTER_EXEC' in environment variable");
        }
    }

    /**
     * Get current Dart SDK executable path.
     *
     * @return Dart SDK executable path.
     */
    @Nonnull
    public String getDartSDKExecutable() {
        return dartSDKExec.getPath();
    }

    /**
     * Get current Flutter SDK executable path.
     *
     * @return Flutter SDK executable path, or <code>null</code> if not provided.
     */
    @Nullable
    public String getFlutterSDKExecutable() {
        return Objects.isNull(flutterSDKExec) ? null : flutterSDKExec.getPath();
    }

    /**
     * Build {@link Process} for executing {@link PubSubCommand} under given <code>projectDir</code>.
     * <br/><br/>
     * Returned process is {@link ProcessBuilder#start() started} already, and you may need to apply
     * {@link BufferedReader} and {@link BufferedWriter} for continue interacting between {@link PubSubCommand} and
     * Java program itself.
     * <h4>Example for building process</h4>
     * <pre>
     *     // Get factory's instance
     *     PubCommandProcessFactory f = getInstance();
     *
     *     // Apply new pub process
     *     Process p = f.buildPubProcess(
     *              new File("/path/to/project"),
     *              false, // `true` if Flutter project.
     *              new PubPublishSubCommand().addOption(new PubDryRunOption()) // Any subcommand
     *     );
     *
     *     // Buffered reader for buffer execution output of pub command.
     *     BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
     *
     *     // Buffered writer for buffer incoming input argument to pub command.
     *     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
     *
     *     // Buffered error for buffer error message output of pub command.
     *     BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
     *
     *     // Handle buffers below...
     * </pre>
     *
     * @param projectDir An absolute {@link File} that using to execute under project's directory.
     * @param runFlutter Execute <code>pub</code> with Flutter execution instead of Dart one (required Flutter
     *                   executable path provided).
     * @param subcommand Execute which {@link PubSubCommand} under this {@link Process}.
     *
     * @return A running {@link Process} that ready to attach {@link BufferedReader} and {@link BufferedWriter} for
     *         further interaction.
     *
     * @throws IOException When given project directory is not {@link File#isAbsolute() is absolute} or
     *                     {@link File#isDirectory() directory}, try to run Flutter without given Flutter executable
     *                     or I/O error which throw from {@link ProcessBuilder#start()}.
     */
    public Process buildPubProcess(@Nonnull File projectDir, boolean runFlutter, @Nonnull PubSubCommand subcommand)
            throws IOException {
        if (!projectDir.isAbsolute())
            throw new FileSystemException(projectDir.getPath(), null, "Required absolute path of project directory.");
        else if (!projectDir.isDirectory())
            throw new NotDirectoryException(projectDir.getPath());

        if (runFlutter && flutterSDKExec == null)
            throw new IOException("Run pub under Flutter without providing Flutter executable path.");

        // Spilt all needed arguments as a single field.
        ArrayList<String> cmdArgs = new ArrayList<>();
        cmdArgs.add((runFlutter ? flutterSDKExec : dartSDKExec).getPath());
        cmdArgs.addAll(Arrays.asList(subcommand.buildSubCommand().split("\\s")));

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(projectDir); // Locate target project directory.
        pb.command(cmdArgs.toArray(new String[]{})); // Reassemble to an array with every string without whitespace.
        return pb.start(); // It executes when return.
    }

    /**
     * Return instance of {@link PubCommandProcessFactory}. If no previous call, the instance will be constructed
     * automatically.
     *
     * @return Instance of {@link PubCommandProcessFactory}.
     */
    @Nonnull
    public static PubCommandProcessFactory getInstance() {
        if (Objects.isNull(INSTANCE)) {
            try {
                INSTANCE = new PubCommandProcessFactory();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return INSTANCE;
    }
}
