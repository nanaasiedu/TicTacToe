package app;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BoardSpace extends Button {
    public static double DEFAULT_HEIGHT = 200;
    public static double DEFAULT_WIDTH = 200;
    private static String NOUGHT_IMG = "img/nought.jpg";
    private static String CROSS_IMG = "img/cross.jpg";
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

    @Override
    public BoardSpace clone() {
        return new BoardSpace(icon, row, col);
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
        setSpaceImage(icon);
    }

    public void setIcon(Icon icon, boolean setImage) {
        this.icon = icon;
        if (setImage) setSpaceImage(icon);
    }

    private void setSpaceImage(Icon icon) {
        if (icon == Icon.EMPTY) return;

        String imgLocation = (icon == Icon.CROSSES ? CROSS_IMG : NOUGHT_IMG);
        Image iconImage = new Image(getClass().getResourceAsStream(imgLocation));
        ImageView iconImageView = new ImageView(iconImage);

        iconImageView.setFitHeight(DEFAULT_HEIGHT);
        iconImageView.setFitWidth(DEFAULT_WIDTH);
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                setGraphic(new ImageView(iconImage));
            }
        });

    }

    public String toString() {
        return (icon == Icon.CROSSES ? "X" : (icon == Icon.NOUGHTS ? "O" : " "));
    }
}
