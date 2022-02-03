package xyz.rk0cc.willpub.cmd.subcmd;

import xyz.rk0cc.willpub.cmd.options.*;

import javax.annotation.Nonnull;
import java.util.Set;

public final class PubCacheSubCommand extends PubSubCommandWithArgs {
    public PubCacheSubCommand(@Nonnull String subCommandName, @Nonnull String... args) {
        super(subCommandName, Set.of(
                PubAllOption.class,
                PubVersionOption.class
        ), args);
    }
}
