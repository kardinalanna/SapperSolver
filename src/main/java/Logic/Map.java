package Logic;

import java.util.ArrayList;

class Map {
    MatrixOfField flagMatrix;
    private MatrixOfField bombMatrix;
    private int bombNumber;
    Field field;
    int closed;

    Map(int bombNumber, Coord sizeOfField) {
        field = new Field(sizeOfField);
        int limit = field.getSize().x * field.getSize().y / 2; //устанавливоем ограничение на количество бомб,если их больше, выставляем максимально возможное значение
        this.bombNumber = Math.min(bombNumber, limit);
        closed = sizeOfField.x * sizeOfField.y; //считает количество закрытых ячеек, если оно = кол-ву бомб, игрок победил
    }

    void startPlaceBomb() { //рандомно распалагаем бомбы, получая случайные координаты из Field
        bombMatrix = new MatrixOfField(ImageStorage.zero, field);
        for (int i = 0; i < bombNumber; i++) {
            while (true) {
                Coord coord = field.randomCoord();
                if (bombMatrix.getPictureOnPosition(coord) == ImageStorage.bomb)
                    continue; //чтобы бомбы не накладывались друг на друга
                bombMatrix.setPictureOnPosition(coord, ImageStorage.bomb);
                for (Coord near : around(coord)) { //окружаем бомбу цифрами, имея список коодинат вокруг, вносим в соответсвуюшие ячейки следующую по номеру картинку из
                    // хранилища(там соблюден порядок, после бомбы идет 1, а в этой ячейке уже есть цифра, берем следующую по номеру картинку 2 и т.д.
                    if (bombMatrix.getPictureOnPosition(near) != ImageStorage.bomb)
                        bombMatrix.setPictureOnPosition(near, bombMatrix.getPictureOnPosition(near).nextImage());
                }
                break;
            }
        }
    }

    ImageStorage getFromBombMap(Coord coord) {
        return bombMatrix.getPictureOnPosition(coord);
    }

    void startFlagging() { //сначала наше поля заполнено закрытыми ячейками
        flagMatrix = new MatrixOfField(ImageStorage.closed, field);//запускается из Controller и создает "верхнее" поле на основе MatrixOfField
    }

    ImageStorage getFromFlagMap(Coord coord) {
        return flagMatrix.getPictureOnPosition(coord);
    }

    void open(Coord coord) {
        flagMatrix.setPictureOnPosition(coord, ImageStorage.opened);
        closed--;
    }

    int getCountOfFlagged(Coord coord) {  //подсчитывает флаги рядом с заданной ячейкой
        int count = 0;
        for (Coord near : around(coord)) if (flagMatrix.getPictureOnPosition(near) == ImageStorage.flaged) count++;
        return count;
    }

     boolean inField(Coord coord) { //проверяем, находится ли нащ элемент в пределах поля
        return (coord.x >= 0 && coord.x < field.getSize().x) && (coord.y >= 0 && coord.y < field.getSize().y);
    }

    public ArrayList<Coord> around(Coord coord) { //возвраущает лист с координатами вокруг заданной ячейки
        Coord num;
        ArrayList<Coord> list = new ArrayList<>();
        for (int i = coord.x - 1; i <= coord.x + 1; i++) {
            for (int y = coord.y - 1; y <= coord.y + 1; y++) {
                num = new Coord(i, y);
                if (inField(num) && !num.equals(coord)) {
                    list.add(num);
                }
            }
        }
        return list;
    }
    MatrixOfField getBombMatrix(){
        return bombMatrix;
    }
}






