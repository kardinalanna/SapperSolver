package Logic;

//специальный класс возращаюций пару значений - х, у; являются кординатами картинки в матрице
public class Coord {
    public int x;
    public int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coord) {
            Coord coord = (Coord) obj;
            return coord.x == x && coord.y == y;
        }
        return false;
    }
    public String toString(){
        return "{" + this.x + "; " + this.y + "}";
    }
}
