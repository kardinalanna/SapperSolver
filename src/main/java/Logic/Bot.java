package Logic;

import java.math.RoundingMode;
import java.util.*;
import java.math.BigDecimal;

public class Bot {

    public Bot(MatrixOfField bombMap, int width, int height) {
        this.width = width;
        this.height = height;
        this.bombMap = bombMap;
        note = 0;
        field = createField();
    }

    MatrixOfField bombMap;
    Random random = new Random();
    int width;
    int height;
    Cell[][] field;
    int note;
    HashSet<Coord> returnToOpen = new HashSet<>();
    HashSet<Coord> returnToFlagged = new HashSet<>();
    ArrayList<Group> groupsSet = new ArrayList<>();

    Cell[][] createField() {
        Cell[][] matr = new Cell[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell();
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
        Coord coord = new Coord(random.nextInt(width - 1), random.nextInt(height - 1));
        field[coord.x][coord.y].openWithoutBoom();
        return coord;
    }

    boolean inField(int a, int b) {
        return (a >= 0 && b >= 0 && a <= width - 1 && b <= height - 1);
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
                        break;
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
                    if (bigGroup.contains(smallGroup) && bigGroup.getSize() != smallGroup.getSize()) { //если большая по размеру группа содержит меншую, то из большей удаляем меньшую
                        bigGroup.delete(smallGroup);
                        repeat = true;
                    } else if ((bigGroup.crossing(smallGroup)).size() > 0) { //если группы пересекаются, то вычмсляем
                        if (group1.getCountBomb() > group2.getCountBomb()) {
                            bigGroup = group1;
                            smallGroup = group2;
                        } else {
                            bigGroup = group2;
                            smallGroup = group1;
                        }
                        Group crossingGroup = crossingG(bigGroup, smallGroup); //группу пересечения;
                        if (crossingGroup != null) {
                            groupsSet.add(crossingGroup); //добовляем эту группу в список
                            bigGroup.delete(crossingGroup); //с учетом новой группы перобразуем другие
                            smallGroup.delete(crossingGroup);
                            repeat = true;
                        }
                    }
                }
            }
        }
        System.out.println("конец создания групп");
    }

    private Group crossingG(Group bigGroup, Group smallGroup) {
        ArrayList<Coord> crossSet = bigGroup.crossing(smallGroup);
        int bombInCrossing = bigGroup.getCountBomb() - (smallGroup.getSize() - crossSet.size());
        if (bombInCrossing != smallGroup.getCountBomb()) return null;
        return (new Group(crossSet, bombInCrossing));
    }

    private Group createGroup(int i, int j, int bomb) {
        ArrayList<Coord> set = new ArrayList<>();
        for (int a = i - 1; a <= i + 1; a++)
            for (int b = j - 1; b <= j + 1; b++) {
                if (inField(a, b) && !field[a][b].open && !field[a][b].flag) set.add(new Coord(a, b));
            }
        if (set.isEmpty()) return null;
        else return new Group(set, bomb);
    }

    public void reliableSolution() { //рассматривает простые ситуации, где есть достоверное решение
        boolean change = true;
        while (change) { //если изменилось кол-во открытых и/или закрытых ячееек, обновляем список групп
            setGroups();
            change = false;
            if (groupsSet.size() == 0) return;
            for (Group g : groupsSet) {
                if (g.countB == 0) { //открываем ячейки группах, где количество бомб = 0
                    for (Coord c : g.set) {
                        field[c.x][c.y].openWithoutBoom();
                        if (field[c.x][c.y].flag) {
                            field[c.x][c.y].flag = false;
                            returnToFlagged.remove(c);
                        }
                        returnToOpen.add(c);
                        change = true;
                    }
                }
                if (g.getSize() == g.countB) //флагируем ячейки в группах
                    for (Coord c : g.set) {
                        field[c.x][c.y].flag = true; //******* метод для изменения флага
                        returnToFlagged.add(c);
                        change = true;
                    }
            }
            for (Coord c : returnToOpen) {
                returnToFlagged.remove(c);
            }
        }
        probabilisticSolution(); // если изменений больше не происходит, обращаемся к вероятномтному открытию ячейки
        System.out.println("После простого решения: " + "\n");
        showFieldProb();
        System.out.println("\n" + "октр/закр" + "\n");
        showFieldClOp();
        System.out.println("\n" + "flaggs" + "\n");
        showFieldFlagg();
    }

    public void probabilisticSolution() { //решение для неопределенных случаев, построенное на расчете вероятности;
        for (Group group : groupsSet) {
            double prob = new BigDecimal(((double) group.countB / group.getSize())).setScale(4, RoundingMode.UP).doubleValue(); //расчитываем общ. вероятность для ячеек
            for (Coord c : group.getSet()) {
                if (field[c.x][c.y].possibility == null)
                    field[c.x][c.y].setPossibility(prob); //если вероятность не указана, назначаем общ., иначе высчитываем
                else
                    field[c.x][c.y].setPossibility(new BigDecimal(1 - (1 - field[c.x][c.y].possibility) * (1 - prob)).setScale(4, RoundingMode.UP).doubleValue());
            }
        }
        boolean loop = true;
        while (loop) { //устанавливаем сумму вероятностей в ячейках группы == кол-ву бомб в группе
            for (Group group : groupsSet) {
                Double prob;
                double sum = 0.0;
                for (Coord c : group.getSet()) {
                    sum += field[c.x][c.y].possibility;
                }
                System.out.println("\n" + "sum =" + sum);
                System.out.println("разница = " + Math.abs(new BigDecimal(group.getCountBomb() - sum).setScale(4, RoundingMode.UP).doubleValue()));
                //если разница достаточно маленькая, останавливаем вычисления
                if ((Math.abs(new BigDecimal(group.getCountBomb() - sum).setScale(4, RoundingMode.UP).doubleValue()) < 0.0100))
                    loop = false;
                if (sum != 0.0)
                    prob = (new BigDecimal(group.getCountBomb() / sum).setScale(4, RoundingMode.UP).doubleValue());
                else prob = 0.0;
                System.out.println("вероятность в ячейках = ");
                for (Coord c : group.getSet()) {
                    field[c.x][c.y].setPossibility(new BigDecimal(field[c.x][c.y].possibility * prob).setScale(4, RoundingMode.UP).doubleValue());
                    ;
                    System.out.print(field[c.x][c.y].possibility + ", ");
                    if (field[c.x][c.y].possibility > 0.99) field[c.x][c.y].setPossibility(0.9999);
                    if (field[c.x][c.y].possibility < 0.0) field[c.x][c.y].setPossibility(0.0000);
                }
            }
        }
        double min = 1.0; //выбираем ячейку с наименьшей вероятностью и открываем ее
        ArrayList<Coord> randomOpen = new ArrayList<>();
        Coord cord;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!field[i][j].open && !field[i][j].flag && field[i][j].possibility != null && field[i][j].possibility < min) {
                    min = field[i][j].possibility;
                }
            }
        } //открываем ячейку с наименьше вероятностью, если их несколько, открываем случайную
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!field[i][j].open && !field[i][j].flag && field[i][j].possibility != null && field[i][j].possibility == min)
                    randomOpen.add(new Coord(i, j));
            }
        }
        System.out.println("претенденты на минимум:" + "\n");
        for (Coord c : randomOpen) {
            System.out.println(c + "," + field[c.x][c.y].possibility + "_");
        }
        if (randomOpen.size() == 1) field[randomOpen.get(0).x][randomOpen.get(0).y].openWithoutBoom();
        else {
            Coord coord = randomOpen.get(random.nextInt(randomOpen.size() - 1));
            System.out.println("минимальная вероятность была в ячейке = " + coord.toString());
            field[coord.x][coord.y].openWithoutBoom();
            returnToOpen.add(coord);
        }

        System.out.println("группы: ");
        for (Group c : groupsSet) {
            c.toSt();
        }
        System.out.println();
        showFieldProb();
        System.out.println("\n" + "октр/закр");
        showFieldClOp();
        System.out.println("\n" + "flaggs" + "\n");
        showFieldFlagg();
    }

    class Cell { //можно добавить список ячеек вокруг
        Double possibility;
        int countBombAround;
        boolean open;
        boolean bombed;
        boolean flag;

        Cell() {
            open = false;
            possibility = null;
        }

        public void setPossibility(Double possibility) {
            this.possibility = possibility;
        }

        boolean openWithoutBoom() {
            open = true;
            return !bombed;
        }

        void inverseFlag() {
            if (!this.open)
                flag = !flag;
        }
    }

    class Group {
        ArrayList<Coord> set;
        int countB;

        Group(ArrayList<Coord> set, int countOfB) {
            this.set = set;
            countB = countOfB;
        }

        ArrayList<Coord> crossing(Group group) {
            ArrayList<Coord> set1 = new ArrayList<>();
            for (Coord c : group.getSet()) {
                if (containsInSet(c)) set1.add(c);
            }
            return set1;
        }

        void delete(Group group) {
            for (Coord c : group.set) {
                this.set.remove(c);
            }
            countB = countB - group.getCountBomb();
            if (countB < 0) countB = 0;
            System.out.println("********************количество бомб в группе после удаления = " + countB);
        }

        boolean containsInSet(Coord c) {
            for (Coord coord : set) {
                if (coord.equals(c)) return true;
            }
            return false;
        }

        public boolean contains(Group group) {
            for (Coord c : group.getSet()) {
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

        public ArrayList<Coord> getSet() {
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
            for (Coord c : set) {
                System.out.print(c.toString() + ", ");
            }
            System.out.print("|| ");
        }
    }
}
