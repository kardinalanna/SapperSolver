package Logic;

import java.math.RoundingMode;
import java.util.*;
import java.math.BigDecimal;

public class Bot {

    public Bot(MatrixOfField bombMap, int width, int height, int bomb) {
        this.width = width;
        this.height = height;
        this.bombMap = bombMap;
        field = createField();
        countB = bomb;
    }
    int countB;
    MatrixOfField bombMap;
    Random random = new Random();
    int width;
    int height;
    Cell[][] field;
    HashSet<Coord> returnToOpen = new HashSet<>();
    HashSet<Coord> returnToFlagged = new HashSet<>();
    ArrayList<Group> groupsSet = new ArrayList<>();
    int OPENED = 0;
    int FLAGGED = 0;


    Cell[][] createField() {
        Cell[][] matr = new Cell[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell(i, j);
                ImageStorage im = bombMap.getPictureOnPosition(new Coord(i, j));
                switch (im) {
                    case bomb:
                        cell.countBombAround = -1;
                        break;
                    case zero:
                        cell.countBombAround = 0;
                        break;
                    case num1:
                        cell.countBombAround = 1;
                        break;
                    case num2:
                        cell.countBombAround = 2;
                        break;
                    case num3:
                        cell.countBombAround = 3;
                        break;
                    case num4:
                        cell.countBombAround = 4;
                        break;
                    case num5:
                        cell.countBombAround = 5;
                        break;
                    case num6:
                        cell.countBombAround = 6;
                        break;
                }
                matr[i][j] = cell;
            }
        return matr;
    }

    public void showField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j != width - 1) System.out.print(field[i][j].countBombAround + " ");
                else System.out.print(field[i][j].countBombAround + "\n");
            }
        }
    }

    public void showFieldClOp() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j != width - 1) System.out.print(field[i][j].open + " ");
                else System.out.print(field[i][j].open + "\n");
            }
        }
    }

    public void showFieldProb() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j != width - 1) System.out.print(field[i][j].possibility + " ");
                else System.out.print(field[i][j].possibility + "\n");
            }
        }
    }

    public void showFieldFlagg() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (j != width - 1) System.out.print(field[i][j].flag + " ");
                else System.out.print(field[i][j].flag + "\n");
            }
        }
    }

    public HashSet<Coord> getCoordToOpen() {
        return returnToOpen;
    }

    public HashSet<Coord> getCoordToFlagged() {
        return returnToFlagged;
    }

    public Coord randomOpen() {
        Cell cell = new Cell(random.nextInt(width - 1), random.nextInt(height - 1));
        field[cell.x][cell.y].openWithoutBoom();
        OPENED++;
        return cell.getCoord();
    }

    boolean confirmation(Group group, Cell[][] field) {
        HashSet<Cell> around = new HashSet<>();
        int opened = 0;
        for (Cell cell : group.getSet()) {
            int x = cell.x;
            int y = cell.y;
            for (int a = x - 1; a <= x + 1; a++)
                for (int b = y - 1; b <= y + 1; b++) {
                    if (inField(a, b)) {
                        if (!around.contains(field[a][b]) && field[a][b].open && !field[a][b].flag)
                            opened++;
                        around.add(field[a][b]);
                    }
                }
        }
        return (opened >= group.getSize());
    }

    boolean inField(int a, int b) {
        return (a >= 0 && b >= 0 && a <= width - 1 && b <= height - 1);
    }

    private Group createGroup(int i, int j, int bomb) {
        ArrayList<Cell> set = new ArrayList<>();
        for (int a = i - 1; a <= i + 1; a++)
            for (int b = j - 1; b <= j + 1; b++) {
                if (inField(a, b) && !field[a][b].open && !field[a][b].flag) set.add(field[a][b]);
            }
        if (set.isEmpty()) return null;
        else return new Group(set, bomb);
    }

    private void setGroups() {
        groupsSet.clear();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) { //создаем группы из закрытых ячеек вокруг открытых
                if (field[i][j].open && (createGroup(i, j, field[i][j].countBombAround)) != null)
                    groupsSet.add((createGroup(i, j, field[i][j].countBombAround)));
            }
        boolean repeat = true;
        while (repeat) { //повторяем, пока происходят изменения
            repeat = false;
            for (int i = 0; i < groupsSet.size(); i++) {
                Group group1 = groupsSet.get(i);
                for (int j = i + 1; j < groupsSet.size(); j++) {
                    Group group2 = groupsSet.get(j);
                    if (group1.equals(group2)) { //если группы одинаковые - оставляем одну
                        groupsSet.remove(j--);
                        continue;
                    }
                    Group bigGroup;
                    Group smallGroup;
                    if (group1.getSize() > group2.getSize()) {
                        bigGroup = group1;
                        smallGroup = group2;
                    } else {
                        bigGroup = group2;
                        smallGroup = group1;
                    }
                    if (bigGroup.contains(smallGroup)) { //если большая по размеру группа содержит меншую, то из большей удаляем меньшую
                        bigGroup.delete(smallGroup);
                        if (bigGroup.getSize() == 0) groupsSet.remove(bigGroup);
                        repeat = true;
                    }
                }
            }
        }
    }

    public void reliableSolution() { //рассматривает простые ситуации, где есть достоверное решение
        if (OPENED == width * height - countB || FLAGGED == countB) {
            System.out.println("победа");
            return;
        }
        Cell[][] copyField = field;
        boolean change = true;
        while (change) { //если изменилось кол-во открытых и/или закрытых ячееек, обновляем список групп

            setGroups();
            change = false;
            if (groupsSet.size() == 0) break;
            for (Group g : groupsSet) {
                if (g.countB == 0 && confirmation(g, copyField)) { //открываем ячейки группах, где количество бомб = 0
                    for (Cell c : g.set) {
                        c.openWithoutBoom();
                        if (c.flag) {
                            c.flag = false;
                            returnToFlagged.remove(c.getCoord());
                        }
                        c.openWithoutBoom();
                        returnToOpen.add(c.getCoord());
                        OPENED++;
                        change = true;
                    }
                }
                if (g.getSize() == g.countB && confirmation(g, copyField)) //флагируем ячейки в группах
                    for (Cell c : g.set) {
                        if (!c.open) {
                            c.flag = true;
                            returnToFlagged.add(c.getCoord());
                            FLAGGED++;
                            change = true;
                        }
                    }
            }
        }
        Cell end = probabilisticSolution(); // если изменений больше не происходит, обращаемся к вероятномтному открытию ячейки
    }

    public Cell probabilisticSolution() { //решение для неопределенных случаев, построенное на расчете вероятности;
        if (OPENED == width * height - countB || FLAGGED == countB) {
            System.out.println("победа");
            return null;
        }
        HashMap<Cell, Double> cells = new HashMap();
        for (Group group : groupsSet) {
            for (Cell c : group.getSet()) {
                Double cellProb = cells.get(c);
                if (cellProb == null)
                    cells.put(c, BigDecimal.valueOf((double) group.getCountBomb() / group.getSize()).setScale(3, RoundingMode.UP).doubleValue());
                else cells.put(c, ProbSum(cellProb, (double) group.getCountBomb() / group.getSize()));
            }
        }
        for (Group group : groupsSet) {
            for (Cell cell : group.getSet()) {
                cell.setPossibility(cells.get(cell));
            }
        }
        boolean loop = true;
        while (loop) {
            loop = false;
            for (Group group : groupsSet) {
                int i = 0;
                Double[] probList = new Double[group.getSize()];
                for (Cell cell : group.getSet()) {
                    probList[i] = cell.getPossibility();
                    i++;
                }
                Double sum = 0.0;
                for (Cell cell : group.getSet()) sum += cell.getPossibility();
                int bobs = group.getCountBomb();
                if (!(sum - bobs < 0.01)) {
                    loop = true;
                    Double correct = group.getCountBomb() / sum;
                    i = 0;
                    for (; i < group.getSize(); i++) probList[i] = probList[i] * correct;
                    for (int j = 0; j < group.getSize(); j++) {
                        Double realProb = BigDecimal.valueOf(probList[j]).setScale(3, RoundingMode.UP).doubleValue();
                        group.getSet().get(j).setPossibility(realProb);
                        cells.put(group.getSet().get(j), realProb);
                    }
                }
            }
        }
        for (Cell cell : cells.keySet()) {
            Double prob = cells.get(cell);
            if (prob > 0.99) {
                field[cell.x][cell.y].setPossibility(0.999);
                cells.put(cell, 0.999);
            }
            if (prob < 0.0) {
                field[cell.x][cell.y].setPossibility(0.000);
                cells.put(cell, 0.999);
            }
        }
        //выбираем ячейку с наименьшей вероятностью и открываем ее
        Cell cell;
        Double min = 1.0;
        ArrayList<Cell> randomOpen = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!field[i][j].open && !field[i][j].flag && field[i][j].possibility != null && field[i][j].possibility < min) {
                    min = field[i][j].possibility;
                }
            }
        }
        //открываем ячейку с наименьше вероятностью, если их несколько, открываем случайную
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!field[i][j].open && !field[i][j].flag && field[i][j].possibility != null && field[i][j].possibility.equals(min))
                    randomOpen.add(field[i][j]);
            }
        }
        if (randomOpen.size() == 1) {
            field[randomOpen.get(0).x][randomOpen.get(0).y].openWithoutBoom();
            OPENED++;
            returnToOpen.add(field[randomOpen.get(0).x][randomOpen.get(0).y].getCoord());
            cell = field[randomOpen.get(0).x][randomOpen.get(0).y];
        } else {
            if (randomOpen.size() == 0) { //последние ходы, но в ячейках не определена вероятность
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (!field[i][j].open && !field[i][j].flag) randomOpen.add(field[i][j]);
                    }
                }
                if (randomOpen.size() == 1) {
                    return randomOpen.get(0);
                } else if (randomOpen.size() == 0) return null;
                cell = randomOpen.get(random.nextInt(randomOpen.size() - 1));
                field[cell.x][cell.y].openWithoutBoom();
                OPENED++;
            } else {
                Cell cellMin = randomOpen.get(random.nextInt(randomOpen.size() - 1));
                field[cellMin.x][cellMin.y].openWithoutBoom();
                OPENED++;
                returnToOpen.add(cellMin.getCoord());
                cell = cellMin;
            }
        }
        return cell;
    }


    private Double ProbSum(Double cellProb, double thisGroupProb) {
        return BigDecimal.valueOf(1 - (1 - cellProb) * (1 - thisGroupProb)).setScale(3, RoundingMode.UP).doubleValue();
    }

    class Cell { //можно добавить список ячеек вокруг
        private Double possibility;
        private int countBombAround;
        boolean open;
        boolean bombed;
        boolean flag;
        int x;
        int y;
        Coord coord;

        Cell(int x, int y) {
            open = false;
            possibility = null;
            this.x = x;
            this.y = y;
            coord = new Coord(x, y);
        }

        public void setPossibility(Double possibility) {
            this.possibility = possibility;
        }

        boolean openWithoutBoom() {
            open = true;
            return !bombed;
        }

        public Coord getCoord() {
            return coord;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Cell) {
                Cell newCell = (Cell) obj;
                return newCell.x == this.x && newCell.y == this.y;
            }
            return false;
        }

        public int hashCode() {
            return super.hashCode();
        }

        public Double getPossibility() {
            return possibility;
        }
    }

    class Group {
        ArrayList<Cell> set;
        int countB;

        Group(ArrayList<Cell> set, int countOfB) {
            this.set = set;
            countB = countOfB;
        }

        ArrayList<Cell> crossing(Group group) {
            ArrayList<Cell> set1 = new ArrayList<>();
            for (Cell c : group.getSet()) {
                if (set.contains(c)) set1.add(c);
            }
            return set1;
        }

        void delete(Group group) {
            for (Cell c : group.set) {
                this.set.remove(c);
            }
            countB = countB - group.getCountBomb();
            if (countB < 0) countB = 0;
        }

        public boolean contains(Group group) {
            for (Cell c : group.getSet()) {
                if (!this.set.contains(c)) return false;
            }
            return true;
        }

        public int getCountBomb() {
            return countB;
        }

        int getSize() {
            return set.size();
        }

        public ArrayList<Cell> getSet() {
            return set;
        }

        @Override
        public int hashCode() {
            return set.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (getClass() == obj.getClass()) {
                Group group2 = ((Group) obj);
                return (set.equals(group2.getSet()) && countB == group2.getCountBomb());
            } else return false;
        }

        public void toSt() {
            for (Cell c : set) {
                System.out.print(c.toString() + ", ");
            }
            System.out.print("|| ");
        }
    }
}


