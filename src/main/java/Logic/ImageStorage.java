package Logic;

import javafx.scene.image.Image;

import java.util.ArrayList;

//кдасс-перечисление, содержащий имена наших картинок (в логической части выступает как строка(имя), в диалоговой части реализуется в картинку из ресурсов
public enum ImageStorage {
    zero,
    num1,
    num2,
    num3,
    num4,
    num5,
    num6,
    bomb,
    opened,
    closed,
    flaged,
    bombed,
    nobomb;

    public Image picture; //объект, чтобы не привязываться к определенному типу (модель реализует лишь логику без диалога с рользователем), при запуске ViewClass.Start() заполняется картинками Image

    ImageStorage nextImage() {
        return ImageStorage.values()[this.ordinal() + 1];
    } //возвращает номер следующего элемента

    int getNumber() {
        return this.ordinal();
    }


}
