package xyz.rk0cc.willpub.cmd.subcmd;

import xyz.rk0cc.willpub.cmd.options.PubOption;

import javax.annotation.Nonnull;
import java.util.Set;

public non-sealed abstract class AbstractedPubSubCommand extends PubSubCommand {
    protected AbstractedPubSubCommand(@Nonnull String subCommandName, @Nonnull Set<Class<? extends PubOption>> acceptedOption) {
        super(subCommandName, acceptedOption);
    }

    @Nonnull
    @Override
    public final String buildSubCommand() {
        return subCommandProgram() + " " + subCommandOptionFlags();
    }
}
