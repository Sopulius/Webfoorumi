/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Webfoorumi.Collectors;

/**
 *
 * @author Aleksi
 */

import java.sql.*;

public interface Collector<T> {

    T collect(ResultSet rs) throws SQLException;

}

