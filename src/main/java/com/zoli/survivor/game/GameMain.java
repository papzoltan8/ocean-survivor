package com.zoli.survivor.game;

import com.zoli.survivor.command.Command;
import com.zoli.survivor.command.CommandFactory;
import com.zoli.survivor.internal.Util;
import com.zoli.survivor.state.GameState;
import com.zoli.survivor.ui.UIUpdate;
import javafx.scene.canvas.GraphicsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public final class GameMain {

    private static final Logger logger = LogManager.getLogger(GameMain.class);

    private final UIUpdate UIUpdate;
    private GameState gs = new GameState();

    public GameMain(UIUpdate UIUpdate) {
        this.UIUpdate = UIUpdate;
    }

    public void turn(String input) {
        if (!gs.isGameOver()) {
            execute(CommandFactory.getCommand(input));
        }
    }

    public boolean execute(Command command) {
        if (null != command) {
            logger.debug(command);
            if (command.execute(gs)) {
                gs.turn();
                return true;
            }
        }

        return false;
    }

    public void draw(GraphicsContext gc) {
        gs.draw(gc);
        if (null != UIUpdate) {
            UIUpdate.update(gs);
        }
    }

    public void newGame() {
        gs = new GameState();
    }

    public void save(File file) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gs);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            logger.error(e);
            Util.showErrorMessage(e.toString());
        }
    }

    public void load(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            gs = (GameState) in.readObject();
            gs.reload();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            logger.error(e);
            Util.showErrorMessage(e.toString());
        }
    }

    public GameState getGameState() {
        return gs;
    }

}
