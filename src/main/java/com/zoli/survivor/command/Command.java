package com.zoli.survivor.command;

import com.zoli.survivor.state.GameState;

public class Command {

    public boolean execute(GameState gs) {
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{}";
    }

}
