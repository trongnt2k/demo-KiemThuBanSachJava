package com.mycompany.booksellapp;

import booksellapp.Utils;
import com.mycompany.pojo.Book;
import com.mycompany.pojo.Book_catalog;
import com.mycompany.service.BookService;
import com.mycompany.service.Book_catalogService;
import com.mycompany.service.JdbcUtils;
import com.mycompany.service.LoginService;
import java.io.IOException;
import java.math.BigDecimal;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PrimaryController implements Initializable{
    @FXML private ComboBox<Book_catalog> cbCates;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TableView<Book> tbBooks;
    @FXML private TextField txtKeyWord;
    @FXML private TextField txtAuthor;

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
        colAuthor.setCellValueFactory(new PropertyValueFactory("author"));
        
        TableColumn colAction = new TableColumn("ACTIONS");
        colAction.setCellFactory(obj -> {
            Button btn = new Button("Xóa");
            btn.setOnAction(evt -> {
                Utils.getBox("Bạn có chắc chắn xóa không?", Alert.AlertType.CONFIRMATION)
                     .showAndWait().ifPresent(b -> {
                         if (b == ButtonType.OK) {
                             Button bt = (Button) evt.getSource();
                             TableCell cell = (TableCell) bt.getParent();
                             Book q = (Book)cell.getTableRow().getItem();
                             
                             Connection conn;
                             try {
                                 conn = JdbcUtils.getConn();
                                 BookService s = new BookService(conn);
                                 
                                 if (s.deleteBook(q.getId())) {
                                     Utils.getBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                                     loadBooks("");
                                 } else
                                     Utils.getBox("FAILED", Alert.AlertType.ERROR).show();
                                 
                                 conn.close();
                             } catch (SQLException ex) {
                                 Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                             }
                         }
                    });
            });
            
            TableCell cell = new TableCell();
            cell.setGraphic(btn);
            return cell;
        });
        
        this.tbBooks.getColumns().addAll(colId, colName, colPrice, colAuthor,colAction);
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
    
     public void addHandler(ActionEvent evt) {
        try {
            Connection conn = JdbcUtils.getConn();
            BookService p = new BookService(conn);
            
            Book b = new Book();
            b.setName_book(txtName.getText());
            b.setPrice(new BigDecimal(txtPrice.getText()));
            b.setBookCatalog_id(cbCates.getSelectionModel().getSelectedItem().getId());
            b.setAuthor(txtAuthor.getText());
            
            Alert a =new Alert(Alert.AlertType.INFORMATION);
            if (p.addBook(b) == true) 
                a.setContentText("SUCCESSFUL");
            else
                a.setContentText("FAILED");
            
            a.show();
            
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     
     
     public void updateHandler(ActionEvent evt) throws SQLException {
        Book b = this.tbBooks.getSelectionModel().getSelectedItem();
        b.setName_book(txtName.getText());
        b.setPrice(new BigDecimal(txtPrice.getText()));
        b.setBookCatalog_id(this.cbCates.getSelectionModel().getSelectedItem().getId());
        b.setAuthor(txtAuthor.getText());
        
        Connection conn = JdbcUtils.getConn();
        BookService s = new BookService(conn);
        if (s.updateBook(b) == true) {
            Utils.getBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
            loadBooks("");
        } else
            Utils.getBox("FAILED", Alert.AlertType.ERROR).show();
        conn.close();
    }
}
    