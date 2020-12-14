import Logic.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ViewClascc extends Application {
    private Controller controller;
    private final int sizeOfPicture = 50;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        int bomb = 20;
        int columns = 9;
        int rows = 9;
        controller = new Controller(columns, rows, bomb);
        controller.start();
        Bot bot = new Bot(controller.getBombMatr(), columns, rows);
        paintPicture();
        primaryStage.getIcons().add(getPicture("icon"));
        primaryStage.setTitle("Sapper");
        final GridPane root = new GridPane();
        for (Coord coord : controller.getListOfCoord()) {
            ImageView image = new ImageView(controller.getPictureFromImageStorage(coord).picture);
            root.add(image, coord.y, coord.x);
        }
        Scene scene = new Scene(root, columns * sizeOfPicture, rows * sizeOfPicture + 26);
        initScene(root);

        int y = 15;
        boolean start = true;
        while (controller.getState() == State.playing) {// while (!controller.gameOver())
            if (start) {
                controller.presButton1(bot.randomOpen());
                start = false;
            }
            if (controller.getState() == State.bombed) {
                System.out.println("Бомба в самой первой ячейке");
                return;
            }
            bot.reliableSolution();
            //if (bot.getCoordToOpen().size() != 0) for (Coord c : bot.getCoordToOpen()) controller.presButton1(c);
            // if (bot.getCoordToFlagged().size() != 0) for (Coord c : bot.getCoordToFlagged()) controller.presButton3(c);
            if (controller.getState() == State.bombed) {
                System.out.println("Здесь взрываться не должно!!");
                return;
            }
            /*System.out.println("opened = " + controller.countOfOpen);
            for (Coord c : bot.getCoordToOpen()) {
                System.out.print(c.toString() + "; ");
            }
            System.out.println();
            System.out.println("flagged =" + controller.countOfFlag);
            for (Coord c : bot.getCoordToOpen()) {
                System.out.print(c.toString() + "; ");
            }
            if (controller.getState() == State.winner) {
                System.out.println("Можешь наслождаться сладким вкусом победы (*0*)/");
            }

            System.out.println(" y =" + y--);

            System.out.println("******END******");*/
            initScene(root);
        }
        System.out.println("вот и все ребята((");
        if (controller.getState() == State.bombed) System.out.println("МЫ ВЗОРВАЛИСЬ К ЧЕРТЯМ!!!!");

        scene.setOnMouseClicked(event -> {
            Coord coord = new Coord((int) event.getSceneY() / sizeOfPicture, (int) event.getSceneX() / sizeOfPicture);
            if (event.getButton().equals(MouseButton.PRIMARY)) return;//botPlay(bot);
            if (event.getButton().equals(MouseButton.SECONDARY)) return;
            initScene(root);

        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initScene(GridPane pane) {
        for (Coord coord : controller.getListOfCoord()) {
            ImageView image = new ImageView(controller.getPictureFromImageStorage(coord).picture);
            pane.add(image, coord.y, coord.x);
        }
    }

    private Image getPicture(String name) throws IOException {
        try (InputStream it = getClass().getResourceAsStream(name + ".png")) {
            return new Image(it);
        }
    }

    private void paintPicture() throws IOException {
        for (ImageStorage picture : ImageStorage.values()) {
            picture.picture = getPicture(picture.name()); //кажлый элемент "заполняем" картинкой
        }
    }

    private String getState() {
        switch (controller.getState()) {
            case playing:
                return "Будь осторожен";
            case bombed:
                return "Шальная бомба взорвалась. Вы проиграли :(";
            case winner:
                return "Вы победили. Ура!!!";
            default:
                return "";
        }
    }
}