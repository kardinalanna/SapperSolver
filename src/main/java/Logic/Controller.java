package Logic;

import java.util.ArrayList;

public class Controller {

    Map map;
    State state;
    public int countOfBomb;
    public int countOfFlag = 0;

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

    public int countOfOpen = 0;

    void openBox(Coord coord) { //при нажатии левой кнопкой мыши
        if (map.inField(coord)) {
            countOfOpen++;
            System.out.println("OPen in real field = " + countOfOpen);
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
        countOfFlag++;
        System.out.println("countOfFlag in real field = " + countOfFlag);
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

    public static void main(String[] args) {
        Controller controller = new Controller(9, 9, 7);
        controller.start();
        Bot bot = new Bot(controller.getBombMatr(), 9, 9);
        bot.showField();

        int y = 15;
        boolean start = true;
        while (controller.state != State.bombed && y > 0) {// while (!controller.gameOver())
            if (start) {
                controller.presButton1(bot.randomOpen());
                start = false;
            }
            if (controller.state == State.bombed) {
                System.out.println("Бомба в самой первой ячейке");
                return;
            }
            bot.reliableSolution();
            if (bot.getCoordToOpen().size() != 0) for (Coord c : bot.getCoordToOpen()) controller.presButton1(c);
            if (bot.getCoordToFlagged().size() != 0) for (Coord c : bot.getCoordToFlagged()) controller.presButton3(c);

            if (controller.state == State.bombed) {
                System.out.println("Здесь взрываться не должно!!");
                return;
            }

            System.out.println("opened = " + controller.countOfOpen);
            for (Coord c : bot.getCoordToOpen()) {
                System.out.print(c.toString() + "; ");
            }
            System.out.println();
            System.out.println("flagged =" + controller.countOfFlag);
            for (Coord c : bot.getCoordToOpen()) {
                System.out.print(c.toString() + "; ");
            }
            if (controller.state == State.winner) {
                System.out.println("Можешь наслождаться сладким вкусом победы (*0*)/");
            }

            System.out.println(" y =" + y--);

            System.out.println("******END******");
        }
        if (controller.state == State.bombed) System.out.println("МЫ ВЗОРВАЛИСЬ К ЧЕРТЯМ!!!!");

    }

}


