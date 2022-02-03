package xyz.rk0cc.willpub.cmd.options;

/**
 * Apply option for {@link xyz.rk0cc.willpub.cmd.subcmd.PubOutdatedSubCommand} that should override dependencies
 * included the result.
 *
 * @since 1.0.0
 *
 * @see PubDevDependenciesOption
 */
public final class PubDependencyOverridesOption extends PubDisableAllowedOption {
    /**
     * Construct option that include override dependencies or not.
     *
     * @param disableOption Ignore override dependencies if set as true.
     */
    public PubDependencyOverridesOption(boolean disableOption) {
        super("dependency-overrides", disableOption);
    }

    /**
     * Construct option with default option of including override dependencies.
     */
    public PubDependencyOverridesOption() {
        super("dependency-overrides");
    }
}
