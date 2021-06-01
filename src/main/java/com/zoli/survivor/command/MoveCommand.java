package com.zoli.survivor.command;

import com.zoli.survivor.state.GameState;

public final class MoveCommand extends Command {

    private int rowOffset;
    private int colOffset;

    public MoveCommand(String input) {
        if (input.contains("north")) {
            rowOffset = -1;
        } else if (input.contains("south")) {
            rowOffset = 1;
        }
        if (input.contains("west")) {
            colOffset = -1;
        } else if (input.contains("east")) {
            colOffset = 1;
        }
    }

    public MoveCommand(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
    }

    @Override
    public boolean execute(GameState gs) {
        if (0 != rowOffset || 0 != colOffset) {
            return gs.getWorld().getPlayer().moveWithOffset(rowOffset, colOffset);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "MoveCommand{" +
                "rowOffset=" + rowOffset +
                ", colOffset=" + colOffset +
                '}';
    }

}
