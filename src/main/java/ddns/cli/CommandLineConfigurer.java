package ddns.cli;

import picocli.CommandLine;

import java.util.List;
import java.util.Set;

public class CommandLineConfigurer {

    public void configure(CommandLine cmd) {
        cmd.setHelpFactory((commandSpec, colorScheme) -> new CommandLine.Help(commandSpec, colorScheme) {
            // my solution for https://stackoverflow.com/questions/79586537/option-description-on-new-line-for-long-option-width
            @Override
            public int calcLongOptionColumnWidth(List<CommandLine.Model.OptionSpec> options,
                                                 List<CommandLine.Model.PositionalParamSpec> positionals,
                                                 ColorScheme aColorScheme) {
                return super.calcLongOptionColumnWidth(options, positionals, aColorScheme) + 5;
            }

            @Override
            public IParamLabelRenderer parameterLabelRenderer() {
                return new IParamLabelRenderer() {
                    @Override
                    public Ansi.Text renderParameterLabel(CommandLine.Model.ArgSpec argSpec,
                                                          Ansi ansi,
                                                          List<Ansi.IStyle> styles) {
                        return Ansi.OFF.text("");
                    }

                    @Override
                    public String separator() {
                        return "";
                    }
                };
            }

            @Override
            protected Ansi.Text createDetailedSynopsisGroupsText(Set<CommandLine.Model.ArgSpec> args) {
                return Ansi.OFF.text("");
            }
        });
        cmd.setExecutionExceptionHandler((ex, commandLine, fullParseResult) -> {
            String message = CommandLine.Help.Ansi.AUTO.string("@|bold,red " + ex.getMessage() + "|@");
            commandLine.getErr().println(message);
            return commandLine.getCommandSpec().exitCodeOnExecutionException();
        });
        cmd.usage(System.out);
        cmd.setUsageHelpAutoWidth(true);
    }
}
