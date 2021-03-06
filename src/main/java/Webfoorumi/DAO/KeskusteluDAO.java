/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Webfoorumi.DAO;

import Webfoorumi.Database.Database;
import Webfoorumi.Dom.Alue;
import Webfoorumi.Dom.Kayttaja;
import Webfoorumi.Dom.Keskustelu;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aleksi
 */
public class KeskusteluDAO implements Dao<Keskustelu, Integer> {

    private Database database;
    private KayttajaDAO kdao;
 
    private ViestiDAO vdao;

    public KeskusteluDAO(Database db, KayttajaDAO kd,ViestiDAO vd) {
        this.database = db;
        this.kdao = kd;
       
        this.vdao = vd;
    }

    @Override
    public Keskustelu findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE Keskustelu.id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasone = rs.next();
        if (!hasone) {
            return null;
        }

        int id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String timestamp = rs.getString("timestamp");
        int alue_id = rs.getInt("alue_id");
        int aloittaja_id = rs.getInt("aloittaja_id");

        Keskustelu kesk = new Keskustelu(id, nimi, timestamp);
       
        Kayttaja aloittaja = kdao.findOne(aloittaja_id);

        kesk.setAloittaja(aloittaja);
        kesk.setAlue(alue_id);
        kesk.setViestit(vdao.keskustelunViestit(id));
        kesk.setUusinviesti(vdao.keskustelunUusinViesti(id));

        rs.close();
        stmt.close();
        connection.close();

        return kesk;

    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu");
        ResultSet rs = stmt.executeQuery();

        List<Keskustelu> keskustelut = new ArrayList<>();

        while (rs.next()) {

            int id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String timestamp = rs.getString("timestamp");
            int alue_id = rs.getInt("alue_id");
            int aloittaja_id = rs.getInt("aloittaja_id");

            Keskustelu kesk = new Keskustelu(id, nimi, timestamp);
            kesk.setAloittaja(this.kdao.findOne(aloittaja_id));
            kesk.setAlue(alue_id);
            kesk.setViestit(this.vdao.keskustelunViestit(id));
            kesk.setUusinviesti(this.vdao.keskustelunUusinViesti(id));
            keskustelut.add(kesk);

        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelut;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Keskustelu WHERE Keskustelu.id = ?");
        stmt.setObject(1, key);
        int tulos = stmt.executeUpdate();
        System.out.println(tulos);

        stmt.close();
        connection.close();
    }

    public List<Keskustelu> alueenKeskustelut(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Keskustelu.id, Max(Viesti.id) AS lkm FROM Keskustelu, Viesti WHERE Keskustelu.alue_id = ? AND Viesti.keskustelu_id = Keskustelu.id"
                + " GROUP BY Keskustelu.id ORDER BY lkm DESC");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();

        List<Keskustelu> keskustelut = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            Keskustelu kesk = this.findOne(id);
            keskustelut.add(kesk);
            
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelut;

    }

    @Override
    public Keskustelu lastInsert() throws SQLException {
        Connection c = database.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Keskustelu WHERE id = (SELECT MAX(id) FROM Keskustelu)");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int k_id = rs.getInt("id");
            rs.close();
            stmt.close();
            c.close();
            
         
            Keskustelu k = this.findOne(k_id);
            
            return k;
            
    }
    
    public void insert(String otsikko, int alue_id, int aloittaja)throws SQLException{
        database.update("INSERT INTO Keskustelu(nimi,alue_id,aloittaja_id) VALUES(?,?,?)", otsikko, alue_id, aloittaja);
    }

}
