module org.assignment_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.assignment_manager to javafx.fxml;
    exports org.assignment_manager;
}