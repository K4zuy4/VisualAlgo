module org.example.algovisualiser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.swing;


    opens org.example.algovisualiser to javafx.fxml;
    exports org.example.algovisualiser;
}