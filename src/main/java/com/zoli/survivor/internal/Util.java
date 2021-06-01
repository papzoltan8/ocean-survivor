package com.zoli.survivor.internal;

import com.zoli.survivor.state.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class Util {

    public static Optional<ButtonType> showInfoMessage(String message) {
        return showMessage(Alert.AlertType.ERROR, "Information", message);
    }

    public static Optional<ButtonType> showErrorMessage(String message) {
        return showMessage(Alert.AlertType.ERROR, "Error", message);
    }

    public static Optional<ButtonType> showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    public static void drawGridPattern(GraphicsContext gc, int x, int y) {
        int offset = 10;
        int endOffset = World.CELL_LAST_PIXEL - offset;
        gc.strokeLine(x, y + offset, x + World.CELL_LAST_PIXEL, y + offset);
        gc.strokeLine(x, y + endOffset, x + World.CELL_LAST_PIXEL, y + endOffset);
        gc.strokeLine(x + offset, y, x + offset, y + World.CELL_LAST_PIXEL);
        gc.strokeLine(x + endOffset, y, x + endOffset, y + World.CELL_LAST_PIXEL);
    }

    public static void drawStripes(GraphicsContext gc, int x, int y) {
        for (int i = 0; i < 6; i++) {
            gc.strokeLine(x + 6, y + 6 + i * 4, x + World.CELL_LAST_PIXEL - 6, y + 6 + i * 4);
        }
    }

    public static void drawPlusSign(GraphicsContext gc, int x, int y) {
        double middle = World.CELL_SIZE / 2.0;
        gc.strokeLine(x + middle, y, x + middle, y + World.CELL_LAST_PIXEL);
        gc.strokeLine(x, y + middle, x + World.CELL_LAST_PIXEL, y + middle);
    }

}
