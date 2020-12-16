import Logic.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
    boolean start = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        int bomb = 6;
        int columns = 5;
        int rows = 5;
        controller = new Controller(columns, rows, bomb);
        controller.start();
        Bot bot = new Bot(controller.getBombMatr(), columns, rows, bomb);
        paintPicture();
        primaryStage.getIcons().add(getPicture("icon"));
        primaryStage.setTitle("Sapper");
        final GridPane root = new GridPane();
        for (Coord coord : controller.getListOfCoord()) {
            ImageView image = new ImageView(controller.getPictureFromImageStorage(coord).picture);
            root.add(image, coord.y, coord.x);
        }
        Scene scene = new Scene(root, columns * sizeOfPicture, rows * sizeOfPicture + 26);

        while (!controller.gameOver()) Solver(bot,root);

        scene.setOnMouseClicked(event -> {
            Coord coord = new Coord((int) event.getSceneY() / sizeOfPicture, (int) event.getSceneX() / sizeOfPicture);
            if (event.getButton().equals(MouseButton.PRIMARY)) return;
            if (event.getButton().equals(MouseButton.SECONDARY)) return;
            initScene(root);

        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void Solver ( Bot bot, GridPane root){
        if (start) {
            controller.presButton1(bot.randomOpen());
            initScene(root);
            start = false;
        }
        if (controller.getState() == State.bombed) {
            System.out.println("Бомба в самой первой ячейке");
            return;
        }
        bot.reliableSolution();
        for (Coord coord : bot.getCoordToOpen()) {
            controller.presButton1(coord);
            initScene(root);
        }
        for (Coord coord : bot.getCoordToFlagged()) {
            controller.presButton3(coord);
            initScene(root);
        }
        if (controller.getState() == State.bombed) {
            System.out.println("Великий каиайски рандом подвел(");
            return;
        }
        initScene(root);
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