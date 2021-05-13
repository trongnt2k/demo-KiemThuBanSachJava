package com.mycompany.booksellapp;

import com.mycompany.pojo.Book;
import com.mycompany.pojo.Book_catalog;
import com.mycompany.service.BookService;
import com.mycompany.service.Book_catalogService;
import com.mycompany.service.JdbcUtils;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CustomerController implements Initializable{
    @FXML private ComboBox<Book_catalog> cbCates;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TableView<Book> tbBooks;
    @FXML private TextField txtKeyWord;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    void ChangeToLogin(ActionEvent event) throws IOException{
        Parent registerView = FXMLLoader.load(getClass().getResource("login.fxml"));
        
        Scene registerViewScene = new Scene(registerView);
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(registerViewScene);
        window.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection conn = JdbcUtils.getConn();
            Book_catalogService s = new Book_catalogService(conn);
            
            this.cbCates.setItems(FXCollections.observableList(s.getBookcatalog()));
            
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        loadColumns();
        loadBooks("");
        
        this.txtKeyWord.textProperty().addListener((obj) -> {
            loadBooks(this.txtKeyWord.getText());
        });
        
        this.tbBooks.setRowFactory(obj -> {
            TableRow r = new TableRow();
            
            r.setOnMouseClicked(evt -> {
                try {
                    Connection conn = JdbcUtils.getConn();
                    Book_catalogService s = new Book_catalogService(conn);
                    
                    Book p = this.tbBooks.getSelectionModel().getSelectedItem();
                    txtName.setText(p.getName_book());
                    txtPrice.setText(p.getPrice().toString());
                    cbCates.getSelectionModel().select(s.getBookCatalogById(p.getBookCatalog_id()));
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            return r;
        });
    }
    private void loadColumns() {
        TableColumn colId = new TableColumn("Id");
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = new TableColumn("Name");
        colName.setCellValueFactory(new PropertyValueFactory("name_book"));
        
        TableColumn colPrice = new TableColumn("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory("price"));
        
        TableColumn colAuthor = new TableColumn("Author");
        colPrice.setCellValueFactory(new PropertyValueFactory("author"));
        
        this.tbBooks.getColumns().addAll(colId, colName, colPrice, colAuthor);
    }
    
    private void loadBooks(String kw) {
        try {
            this.tbBooks.getItems().clear();
            
            Connection conn = JdbcUtils.getConn();
            BookService s = new BookService(conn);
            
            this.tbBooks.setItems(FXCollections.observableList(s.getBooks(kw)));
            
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
    