module com.a1.a1enhanced {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens com.a1.a1enhanced to javafx.fxml;
    exports com.a1.a1enhanced;
}