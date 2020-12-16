package Logic;

import java.util.ArrayList;

public class Controller {

    Map map;
    State state;
    public int countOfBomb;

    public Controller(int columns, int rows, int bombNumber) { //передаем размеры поля из ViewClass и отсюда все понеслось...
        countOfBomb = bombNumber;
        map = new Map(bombNumber, new Coord(columns, rows));
    }

    public void start() { //ключевой метод, запускающийся в DialogWithUser! Именно с него начинается логика!
        map.startFlagging();
        map.startPlaceBomb();
        state = State.playing;//определяем состояние, которое будет playing пока не выполнится условие для winner или bombed
    }

    public ImageStorage getPictureFromImageStorage(Coord coord) { //непосредственно используется в ViewClass для отображения матрицы из картинок
        if (map.getFromFlagMap(coord) == ImageStorage.opened) //(если в "верхнем" поле ячейка открыта - отображаем соответсвую ее координатам ячейку в "нмжнем" поле)
            return map.getFromBombMap(coord);
        else
            return map.getFromFlagMap(coord);
    }

    public void presButton1(Coord coord) { //необъходимо заменит картинку верхнего поля на картинку нижнего
        if (gameOver()) return;
        openBox(coord);
        winner();
    }


    void openBox(Coord coord) { //при нажатии левой кнопкой мыши
        if (map.inField(coord)) {
            switch (map.getFromFlagMap(coord)) {
                case opened: { //если ячейка открыта и бомбы вокруг нее помечены флагами, открываем оставшие цифры и пустые ячейки
                    if (map.getFromBombMap(coord) != ImageStorage.bomb && map.getCountOfFlagged(coord) == map.getFromBombMap(coord).getNumber())
                        for (Coord near : map.around(coord)) {
                            if (map.getFromFlagMap(near) == ImageStorage.closed) openBox(near);
                        }
                }
                case flaged:
                    return; //не можем открыть флагированную ячейку
                case closed:
                    switch (map.getFromBombMap(coord)) { //если ячейка закрыта, проверяем, что находится под ней в BombMap
                        case zero: { //если пустая открываем саму ячейку и  рекурсивно все ячейки до первых бомб
                            map.open(coord);
                            for (Coord near : map.around(coord)) {
                                openBox(near);
                            }
                        }
                        break;
                        case bomb:
                            findBomb(coord);
                            break;
                        default:
                            map.open(coord);
                            break;
                    }
            }
        }
    }

    private void findBomb(Coord coord) {
        state = State.bombed;
        map.flagMatrix.setPictureOnPosition(coord, ImageStorage.bombed); //если игрок наткнулся на бомбу, отображаем взрыв в этой координате
        for (Coord point : map.field.getListOfAllCoords()) {
            if (map.getFromBombMap(point) == ImageStorage.bomb)
                map.flagMatrix.setPictureOnPosition(point, ImageStorage.opened); //открываем все ячейки с бомбами после проигрыша
            else if (map.getFromFlagMap(point) == ImageStorage.flaged)
                map.flagMatrix.setPictureOnPosition(point, ImageStorage.nobomb); //омечаем флагированные ячейки без бомб в конце ингры
        }
    }

    public void presButton3(Coord coord) {//обработка нажатия правой клавищи - постановка/снятие флага
        if (gameOver()) return;
        switch (map.flagMatrix.getPictureOnPosition(coord)) {
            case flaged: {
                map.flagMatrix.setPictureOnPosition(coord, ImageStorage.closed);
            }
            break;
            case closed: {
                map.flagMatrix.setPictureOnPosition(coord, ImageStorage.flaged);
            }
            break;
        }
    }

    public State getState() {
        return state;
    } //getter для состояния

    public boolean gameOver() {
        return state != State.playing;
    }

    private void winner() { //если кол-во закрытых ячеек, считающихся при открытии ячеек в "верхнем" поле = кол-ву бомб, игрок победил
        if (state == State.playing && map.closed == countOfBomb) {
            state = State.winner;
            System.out.println("ты победил!");
        }
    }


    public ArrayList<Coord> getListOfCoord() {
        return map.field.getListOfAllCoords();
    }

    public MatrixOfField getBombMatr() {
        return map.getBombMatrix();
    }

}

