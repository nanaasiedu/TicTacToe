package app;

import javafx.scene.control.Button;

public class BoardSpace extends Button {
    public static double DEFAULT_HEIGHT = 200;
    public static double DEFAULT_WIDTH = 200;
    private Icon icon;
    private int row;
    private int col;

    public BoardSpace(Icon icon, int row, int col) {
        super();
        setIcon(icon);
        this.row = row;
        this.col = col;
        setPrefHeight(DEFAULT_HEIGHT);
        setPrefWidth(DEFAULT_WIDTH);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    private void setSpaceImage() {

    }
}
