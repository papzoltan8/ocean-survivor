module com.zoli.survivor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires java.desktop;

    exports com.zoli.survivor.app;
    opens com.zoli.survivor.ui to javafx.fxml;
}