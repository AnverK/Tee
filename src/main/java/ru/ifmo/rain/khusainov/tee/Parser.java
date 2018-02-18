package main.java.ru.ifmo.rain.khusainov.tee;

import org.apache.commons.cli.*;

public class Parser {
    private static final Options options = new Options();
    private static final String help = "java -jar Tee.jar [-flags...] [paths to files...]";

    private static void initArguments() {
        options.addOption("a", "append", false, "append to the given FILEs, do not overwrite");
        options.addOption("i", "ignore-interrupts", false, "ignore interrupt signals");
    }

    static Tee newTee(String[] args) {
        initArguments();

        HelpFormatter formatter = new HelpFormatter();
        if (args == null) {
            formatter.printHelp(help, options);
            return null;
        }

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException | NullPointerException e) { // NPE for null in args[i]
            formatter.printHelp(help, options);
            return null;
        }

        return new Tee(cmd.hasOption("a"), cmd.hasOption("i"), cmd.getArgList());
    }
}