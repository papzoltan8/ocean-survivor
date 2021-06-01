package com.zoli.survivor.game;

import com.zoli.survivor.command.*;
import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.enumeration.ItemState;
import com.zoli.survivor.enumeration.SurfaceItem;
import com.zoli.survivor.internal.CellWithPosition;
import com.zoli.survivor.internal.ShortestPath;
import com.zoli.survivor.state.*;

import java.util.LinkedList;
import java.util.List;

public final class AI {

    private final LinkedList<Command> avoidShark = new LinkedList<>();
    private final LinkedList<Command> commands = new LinkedList<>();
    private final GameState gs;

    public AI(GameState gs) {
        this.gs = gs;
    }

    public Command getCommand() {
        if (gs.isGameOver()) {
            return null;
        }

        if (!avoidShark.isEmpty()) {
            return avoidShark.pop();
        }

        for (CellWithPosition cell : gs.getWorld().getNeighbourCells()) {
            if (cell.cell.getSurfaceType().isResource()) {
                return new UseCommand();
            }
        }

        if (!commands.isEmpty()) {
            return commands.pop();
        }

        World world = gs.getWorld();
        Player player = gs.getWorld().getPlayer();
        Inventory inventory = player.getInventory();
        int row = player.getRow();
        int col = player.getCol();
        RaftBuiltItem waterFilter = world.getBuiltItem(WaterFilter.class);
        RaftBuiltItem fireplace = world.getBuiltItem(Fireplace.class);
        boolean hasEssentials = waterFilter != null && fireplace != null;
        Cell c = world.getCellOfPlayer();
        boolean playerInWater = world.isPlayerInWater();

        List<CellWithPosition> sharkPath = ShortestPath.get(true, world, world.getShark().getRow(), world.getShark().getCol(), row, col);
        int sharkDistance = sharkPath.size();

        if (playerInWater) {
            Cell bottomCell = world.getCell(row + 1, col);
            if (sharkDistance <= 2 && null != bottomCell && bottomCell.getSurfaceType().isFixed()) {
                return new MoveCommand(1, 0);
            } else {
                int pRow = row;
                int pCol = col;
                List<CellWithPosition> pathToRaft = ShortestPath.get(true, world, pRow, pCol, World.ROWS / 2, World.COLS / 2);
                if (sharkDistance <= pathToRaft.size()) {
                    for (CellWithPosition cell : pathToRaft) {
                        avoidShark.add(new MoveCommand(cell.row - pRow, cell.col - pCol));
                        pRow = cell.row;
                        pCol = cell.col;
                    }
                    return avoidShark.pop();
                }
            }
        }

        if (hasEssentials && inventory.has(BuildNet.requirements)) {
            Cell upperCell = world.getCell(row - 1, col);
            if (null == upperCell || upperCell.getSurfaceType().isResource() && sharkDistance > 1) {
                commands.add(new MoveCommand(-1, 0));
                commands.add(new BuildNet());
                commands.add(new MoveCommand(1, 0));
                return commands.pop();
            }
            Cell upperLeftCell = world.getCell(row - 1, col - 1);
            if (null == upperLeftCell || upperLeftCell.getSurfaceType().isResource() && sharkDistance > 2) {
                commands.add(new MoveCommand(-1, 0));
                commands.add(new MoveCommand(0, -1));
                commands.add(new BuildNet());
                commands.add(new MoveCommand(1, 0));
                return commands.pop();
            }
            Cell upperRightCell = world.getCell(row - 1, col + 1);
            if (null == upperRightCell || upperRightCell.getSurfaceType().isResource() && sharkDistance > 2) {
                commands.add(new MoveCommand(-1, 0));
                commands.add(new MoveCommand(0, 1));
                commands.add(new BuildNet());
                commands.add(new MoveCommand(1, 0));
                return commands.pop();
            }
        }

        if (fireplace == null) {
            if (inventory.has(BuildFireplace.requirements)) {
                if (null != c && c.isFreeToBuild()) {
                    return new BuildFireplace();
                } else {
                    commands.addAll(moveToFreeRaftCell(world));
                    commands.add(new BuildFireplace());
                }
            }
        } else {
            ItemState state = fireplace.getState();
            if (state == ItemState.EMPTY) {
                if (inventory.has(1, InventoryItem.FISH) || inventory.has(1, InventoryItem.POTATO)) {
                    Command command = useOrGoToAndUseBuiltItem(world, player, fireplace);
                    if (null != command) {
                        return command;
                    }
                } else {
                    if (player.getHunger() >= 40.0) {
                        if (!playerInWater) {
                            Cell upperCell = world.getCell(row - 1, col);
                            if (null == upperCell || upperCell.getSurfaceType().isResource()) {
                                if (sharkDistance > 2) {
                                    commands.add(new MoveCommand(-1, 0));
                                }
                            } else if (sharkDistance > 3) {
                                commands.add(new MoveCommand(-1, 0));
                                commands.add(new MoveCommand(-1, 0));
                            }
                        }
                        if (sharkDistance > 1 && (playerInWater || !commands.isEmpty())) {
                            commands.add(new UseCommand());
                            return commands.pop();
                        }
                    }
                }
            } else if (state == ItemState.FINISHED) {
                if (player.getHunger() >= 50.0) {
                    Command command = useOrGoToAndUseBuiltItem(world, player, fireplace);
                    if (null != command) {
                        return command;
                    }
                } else if (player.getHunger() >= 30.0 && ShortestPath.getDistance(fireplace.row, fireplace.col, row, col) <= 1) {
                    return new UseCommand();
                }
            }
        }

        if (waterFilter == null) {
            if (inventory.has(BuildWaterFilter.requirements)) {
                if (null != c && c.isFreeToBuild()) {
                    return new BuildWaterFilter();
                } else {
                    commands.addAll(moveToFreeRaftCell(world));
                    commands.add(new BuildWaterFilter());
                }
            }
        } else {
            ItemState state = waterFilter.getState();
            if (state == ItemState.FINISHED) {
                if (player.getThirst() >= 50.0) {
                    Command command = useOrGoToAndUseBuiltItem(world, player, waterFilter);
                    if (null != command) {
                        return command;
                    }
                } else if (player.getThirst() >= 20.0 && ShortestPath.getDistance(waterFilter.row, waterFilter.col, row, col) <= 1) {
                    return new UseCommand();
                }
            }
        }

        Cell leftCell = world.getCell(row, col - 1);
        Cell rightCell = world.getCell(row, col + 1);

        if (hasEssentials
                && leftCell != null && leftCell.getSurfaceType() == SurfaceItem.RAFT
                && rightCell == null
                && world.getNets().size() >= world.getRafts().size() - 3
                && inventory.has(BuildRaft.requirements)
                && sharkDistance > 2
                && col + 1 < World.COLS) {
            commands.add(new MoveCommand(0, 1));
            commands.add(new BuildRaft());
            return commands.pop();
        }

        Cell upperLeftCell = world.getCell(row - 1, col - 2);
        Cell upperRightCell = world.getCell(row - 1, col + 2);
        Cell upper2LeftCell = world.getCell(row - 1, col - 3);
        Cell upper2RightCell = world.getCell(row - 1, col + 3);
        if (leftCell != null && leftCell.getSurfaceType() == SurfaceItem.RAFT &&
                ((null != upperLeftCell && upperLeftCell.getSurfaceType().isResource()) || (null != upper2LeftCell && upper2LeftCell.getSurfaceType().isResource()))) {
            return new MoveCommand(0, -1);
        }
        if (rightCell != null && rightCell.getSurfaceType() == SurfaceItem.RAFT &&
                ((null != upperRightCell && upperRightCell.getSurfaceType().isResource()) || (null != upper2RightCell && upper2RightCell.getSurfaceType().isResource()))) {
            return new MoveCommand(0, 1);
        }
        if (c != null && c.getSurfaceType() == SurfaceItem.RAFT && null != upperLeftCell && upperLeftCell.getSurfaceType().isResource() && sharkDistance > 2) {
            commands.add(new MoveCommand(0, -1));
            return commands.pop();
        }
        if (c != null && c.getSurfaceType() == SurfaceItem.RAFT && null != upperRightCell && upperRightCell.getSurfaceType().isResource() && sharkDistance > 2) {
            commands.add(new MoveCommand(0, 1));
            return commands.pop();
        }

        if (hasEssentials && player.getSharkDefence() == 0 && inventory.has(BuildSpear.requirements)) {
            return new BuildSpear();
        }

        if (rightCell != null && rightCell.getSurfaceType() == SurfaceItem.RAFT) {
            return new MoveCommand(0, 1);
        }

        return playerInWater ? new UseCommand() : new Command();
    }

    private Command useOrGoToAndUseBuiltItem(World world, Player player, RaftBuiltItem builtItem) {
        int pRow = player.getRow();
        int pCol = player.getCol();
        if (ShortestPath.getDistance(pRow, pCol, builtItem.row, builtItem.col) <= 1) {
            return new UseCommand();
        } else {
            List<CellWithPosition> pathToBuiltItem = ShortestPath.get(true, world, pRow, pCol, builtItem.row, builtItem.col);
            if (!pathToBuiltItem.isEmpty()) {
                for (CellWithPosition cell : pathToBuiltItem) {
                    commands.add(new MoveCommand(cell.row - pRow, cell.col - pCol));
                    pRow = cell.row;
                    pCol = cell.col;
                }
                commands.add(new UseCommand());
                return commands.pop();
            }
        }

        return null;
    }

    private List<Command> moveToFreeRaftCell(World world) {
        List<Command> commands = new LinkedList<>();
        CellWithPosition raftCell = getFreeRaftCell(world);
        if (null != raftCell) {
            int pRow = world.getPlayer().getRow();
            int pCol = world.getPlayer().getCol();
            List<CellWithPosition> pathToRaft = ShortestPath.get(true, world, pRow, pCol, raftCell.row, raftCell.col);
            for (CellWithPosition c : pathToRaft) {
                commands.add(new MoveCommand(c.row - pRow, c.col - pCol));
                pRow = c.row;
                pCol = c.col;
            }
        }
        return commands;
    }

    private CellWithPosition getFreeRaftCell(World world) {
        for (CellWithPosition cell : world.getNeighbourCells()) {
            if (cell.cell.isFreeToBuild()) {
                return cell;
            }
        }
        return null;
    }

}
