package Logic;
//этот класс работает с маштабами поля, разбивая его на ячейки из картинок

import java.util.ArrayList;
import java.util.Random;

class Field {
    private static Coord sizeOfField;
    private static ArrayList<Coord> listOfAllCoords;

    Field(Coord size) {                               //конструктор
        sizeOfField = size;                          //получаем размер поля
        listOfAllCoords = new ArrayList<>();
        for (int y = 0; y < sizeOfField.y; y++)
            for (int x = 0; x < sizeOfField.x; x++) {
                listOfAllCoords.add(new Coord(x, y));  //список всех ячеек (матрицу 11 12 13 из объектов Сoord(x 1, y 1); в частоности использует для отображения поля в VIewClass
            }
    }

    Coord getSize() {
        return sizeOfField;
    }

    ArrayList<Coord> getListOfAllCoords() { //используется в MatrixOfField и в Controller
        return listOfAllCoords;
    }

    Coord randomCoord() { // метод для растановки бомб
        Random random = new Random();
        return new Coord(random.nextInt(sizeOfField.x), random.nextInt(sizeOfField.y));
    }
}
