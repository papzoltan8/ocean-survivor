package com.zoli.survivor.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CommandFactory {

    private static final Logger logger = LogManager.getLogger(CommandFactory.class);

    public static Command getCommand(String input) {
        switch (input) {
            case "do-nothing":
                return new Command();
            case "e":
                return new UseCommand();
            case "n":
                return new BuildNet();
            case "f":
                return new BuildFireplace();
            case "w":
                return new BuildWaterFilter();
            case "s":
                return new BuildSpear();
            case "r":
                return new BuildRaft();
            case "p":
                return new PickupCommand();
            default:
                if (input.startsWith("move-")) {
                    return new MoveCommand(input);
                } else {
                    return null;
                }
        }
    }

}
