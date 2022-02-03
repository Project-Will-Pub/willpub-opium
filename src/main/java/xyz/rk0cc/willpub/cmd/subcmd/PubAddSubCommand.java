package xyz.rk0cc.willpub.cmd.subcmd;

import xyz.rk0cc.willpub.cmd.options.*;

import javax.annotation.Nonnull;
import java.util.Set;

public final class PubAddSubCommand extends PubSubCommandWithArgs {
    public PubAddSubCommand(@Nonnull String packageName) {
        super("add", Set.of(
                PubGitURLOption.class,
                PubGitPathOption.class,
                PubGitRefOption.class,
                PubHostedUrlOption.class,
                PubPathOption.class,
                PubSdkOption.class,
                PubPreCompileOption.class,
                PubDevOption.class,
                PubOfflineOption.class,
                PubDryRunOption.class
        ), packageName);
    }
}