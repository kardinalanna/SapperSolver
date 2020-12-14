package Logic;

//этот кдасс непосредственно работает с заполнением ячеек картинками (в то время как Field - представлял поле как матрицу с координатами ячеек)
//он является основой "верхнего" и "нижнего" полей
public class MatrixOfField {
    private Field field;
    private ImageStorage[][] matrix;

        MatrixOfField(ImageStorage picture, Field field) {                                // в конструкторетмы заполняем матрицу поля заданной в параметре картинкой из хранилища картинок
        this.field = field;
        matrix = new ImageStorage[field.getSize().x][field.getSize().y];   // размер поля берем из Field (он равен кол-ву столбцов и строк, задаваемых в ViewClass)
        for (Coord coord : field.getListOfAllCoords())                      // перебирая ячейки в Field, заполняем матрицу
            matrix[coord.x][coord.y] = picture;
    }

   public ImageStorage getPictureOnPosition(Coord coord) {                      //можем узнать, какая картинка лежит в ячейке; используется в FlagMap и BombMap
        if (inField(coord)) return matrix[coord.x][coord.y];
        else {
            return null;
        }
    }

    private boolean inField(Coord coord) { //проверяем, находится ли нащ элемент в пределах поля
       return field.getListOfAllCoords().contains(coord);
        //return (coord.x >= 0 && coord.x < field.getSize().x) && (coord.y >= 0 && coord.y < field.getSize().y);
    }

    void setPictureOnPosition(Coord coord, ImageStorage thisPicture) {     //можем поместить катринку в нужную ячейку. с помощью этого метода отображаются все изменения на игровом поле
        if (inField(coord))
            matrix[coord.x][coord.y] = thisPicture; //на игровом поле, в нужную координате изменяем картинку, потом матрица снова отрисовывается (после mouseEvent) в ViewClass
    }



}
