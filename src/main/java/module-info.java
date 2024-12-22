module ru.vsu.cs.simple3dviewer {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.vsu.cs to javafx.fxml;
    exports ru.vsu.cs;
}