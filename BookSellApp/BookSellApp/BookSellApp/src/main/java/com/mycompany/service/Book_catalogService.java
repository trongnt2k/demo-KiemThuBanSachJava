/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service;

import com.mycompany.pojo.Book_catalog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class Book_catalogService { 
    private Connection conn;
    
    public Book_catalogService(Connection conn) {
        this.conn = conn;
    }
    public List<Book_catalog> getBookcatalog() throws SQLException {
        Statement stm = conn.createStatement();
        ResultSet r = stm.executeQuery("SELECT * FROM book_catalog");
        
        List<Book_catalog> re = new ArrayList<>();
        while (r.next()) {
            Book_catalog c = new Book_catalog();
            c.setId(r.getInt("id"));
            c.setName(r.getString("name"));
            c.setNote(r.getString("note"));
            
            re.add(c);
        }
        conn.close();
        return re;
    }
    public Book_catalog getBookCatalogById(int id) throws SQLException {
        String sql = "SELECT * FROM book_catalog WHERE id=?";
        PreparedStatement stm = this.conn.prepareStatement(sql);
        stm.setInt(1, id);
        
        ResultSet rs = stm.executeQuery();
        Book_catalog c = null;
        while (rs.next()) {
            c = new Book_catalog();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));  
        }      
        return c;
    }
}            

 