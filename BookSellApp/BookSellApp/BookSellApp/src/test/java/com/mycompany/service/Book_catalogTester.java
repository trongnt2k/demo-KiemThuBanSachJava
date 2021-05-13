/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service;

import com.mycompany.pojo.Book_catalog;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Admin
 */
public class Book_catalogTester {
    private static Connection CONN;
    
    @BeforeAll
    public static void setUpClass() {
        try {
            CONN = JdbcUtils.getConn();
        } catch (SQLException ex) {
            Logger.getLogger(Book_catalogTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @AfterAll
    public static void tearDownClass() {
        if (CONN != null)
            try {
                CONN.close();
            } catch (SQLException ex) {
                Logger.getLogger(Book_catalogTester.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    @Test
    public void testQuantity() {
        try {
            int expected = 3;
            Book_catalogService s = new Book_catalogService(CONN);
            List<Book_catalog> cates = s.getBookcatalog();
            
            System.out.println(cates);
            Assertions.assertTrue(cates.size() >= expected);
        } catch (SQLException ex) {
            Logger.getLogger(Book_catalogTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testUniqueName() {
         try {
            Book_catalogService s = new Book_catalogService(CONN);
            List<Book_catalog> cates = s.getBookcatalog();
            
            List<String> names = new ArrayList<>();
            cates.forEach(c -> {
                names.add(c.getName());
             });
            
            Set<String> names2 = new HashSet<>(names);
            
            Assertions.assertTrue(names.size() == names2.size());
         } catch (SQLException ex) {
            Logger.getLogger(Book_catalogTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
