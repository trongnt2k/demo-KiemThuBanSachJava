module com.mycompany.booksellapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 

    opens com.mycompany.booksellapp to javafx.fxml;
    exports com.mycompany.booksellapp;
    exports com.mycompany.pojo;
}
