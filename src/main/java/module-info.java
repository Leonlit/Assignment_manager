module org.assignment_manager1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.assignment_manager1 to javafx.fxml;
    exports org.assignment_manager1;
}