/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Webfoorumi;

import Webfoorumi.DAO.AlueDAO;
import Webfoorumi.DAO.KayttajaDAO;
import Webfoorumi.DAO.KeskusteluDAO;
import Webfoorumi.DAO.ViestiDAO;
import Webfoorumi.Database.Database;
import Webfoorumi.Dom.Kayttaja;
import Webfoorumi.Dom.Keskustelu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.*;

/**
 *
 * @author Aleksi
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        Database database = new Database("jdbc:sqlite:webfoorumi.db");

        KayttajaDAO kayttajadao = new KayttajaDAO(database);
        ViestiDAO viestidao = new ViestiDAO(database, kayttajadao);
        KeskusteluDAO keskusteludao = new KeskusteluDAO(database, kayttajadao, viestidao);
        AlueDAO aluedao = new AlueDAO(database, keskusteludao);
        HtmlChecker checker = new HtmlChecker();

        
    
        get("/", (req, res) -> {

            HashMap map = new HashMap<>();
            map.put("alueet", aluedao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

       
        post("/", (req, res) -> {
            String nimi = checker.stripHtml(req.queryParams("nimi"));
            if (!nimi.isEmpty()) {
                aluedao.insert(nimi);
            }
            res.redirect("/");
            return null;
        });

        get("/alue/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alue", aluedao.findOne(Integer.parseInt(req.params("id"))));
            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());

        post("/alue/:id", (req, res) -> {
            String otsikko = checker.stripHtml(req.queryParams("otsikko"));
            String viesti = checker.stripHtml(req.queryParams("viesti"));
            String nimi = checker.stripHtml(req.queryParams("nimi"));

            if (!otsikko.isEmpty() && !viesti.isEmpty() && !nimi.isEmpty()) {
                kayttajadao.insert(nimi);
                Kayttaja kayttaja = kayttajadao.lastInsert();

                keskusteludao.insert(otsikko, Integer.parseInt(req.params("id")), kayttaja.getId());
                Keskustelu k = keskusteludao.lastInsert();

                viestidao.insert(viesti, k.getId(), kayttaja.getId(), -1);
            }

            res.redirect("" + Integer.parseInt(req.params("id")));
            return null;
        });

        get("/keskustelu/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("keskustelu", keskusteludao.findOne(Integer.parseInt(req.params("id"))));
            map.put("viestit", viestidao.keskustelunViestit(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "keskustelu");
        }, new ThymeleafTemplateEngine());

        post("/keskustelu/:id", (req, res) -> {
            String viesti = checker.stripHtml(req.queryParams("viesti"));
            String nimi = checker.stripHtml(req.queryParams("nimi"));
            
            if(!viesti.isEmpty() && !nimi.isEmpty()){
                kayttajadao.insert(nimi);
                Kayttaja kayttaja = kayttajadao.lastInsert();

                viestidao.insert(viesti, Integer.parseInt(req.params("id")), kayttaja.getId(), -1);
            }

            

            res.redirect("" + Integer.parseInt(req.params("id")));
            return null;
        });
    }

}
