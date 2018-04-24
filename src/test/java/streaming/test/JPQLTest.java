/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package streaming.test;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import junit.framework.Assert;
import org.junit.Test;
import streaming.entity.Film;
import streaming.entity.Personne;
import streaming.entity.Serie;

/**
 *
 * @author formation
 */
public class JPQLTest {

    @Test
    public void fonctionsAccesJPA() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();
        
        em.getTransaction().begin();// Démarre transaction: nécessaire si écritures
        
        em.createQuery("UPDATE FROM Personne p SET p.nom = 'coucou'").executeUpdate();
        em.persist( new Personne() );// INSERT
        
        Serie s = em.find(Serie.class, 1L);// Récup 1 entité
        em.remove(s);// Pas génial puisque nécessite un find auparavant!
        
        Serie dexter = new Serie();
        dexter.setTitre("DEXTER NOUV VERSION");
        dexter.setSynopsis("coucou");
        dexter.setId(1L);
        em.merge(dexter);
        
        em.createQuery("DELETE FROM Serie s WHERE s.id=1").executeUpdate();
        
        em.getTransaction().commit();// Valide en DB modifs
        
    }

    @Test // 25. Le nombre total d'épisodes pour chaque série, pour peu qu'il y ait plus de 5 épisodes au total. Le tout trié par nbre d'épisodes.
    public void req25() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT COUNT(e) AS total, se.titre "
                + "FROM     Serie se "
                + "         JOIN se.saisons ss "
                + "         JOIN ss.episodes e "
                + "GROUP BY se "
                + "HAVING total>5 "
                + "ORDER BY total";

        Query query = em.createQuery(sql);
        List<Object[]> resultats = query.getResultList();
        for (Object[] ligne : resultats) {

            System.out.println(String.format("%s %d", ligne[1], ligne[0]));
        }
    }

    @Test // 24. Le nombre total de saisons pour chaque série, triés par ordre croissant de saisons, puis par ordre alphabétique.
    public void req24() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT   sr.titre, COUNT(sa) AS total "
                + "FROM     Serie sr "
                + "         JOIN sr.saisons sa "
                + "GROUP BY sr "
                + "ORDER BY total, sr.titre ";

        Query query = em.createQuery(sql);
        List<Object[]> resultats = query.getResultList();
        for (Object[] ligne : resultats) {

//            System.out.println( String.format("%s %d", ligne[0], ligne[1]) );
        }
    }

    @Test // 23. Le nombre de films réalisés pour chaque réalisateur, uniquement si >= 2 ( GROUP BY et HAVING )
    public void req23() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT COUNT(f) AS nbFilms, r.nom, r.prenom "
                + "FROM Film f "
                + "     JOIN f.realisateurs r "
                + "GROUP BY r "
                + "HAVING nbFilms>=2 "
                + "ORDER BY nbFilms, r.nom DESC, r.prenom DESC";

        Query query = em.createQuery(sql);
        List<Object[]> resultats = query.getResultList();
        for (Object[] ligne : resultats) {

//            System.out.println( String.format("%s %s %d", ligne[1], ligne[2], ligne[0]) );
        }
    }

    @Test // 22. Le nombre de films réalisés pour chaque réalisateur, triés par ordre croissant puis par ordre alphabétique ( GROUP BY )
    public void req22() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT COUNT(f) AS nbFilms, r.nom, r.prenom "
                + "FROM Film f "
                + "     JOIN f.realisateurs r "
                + "GROUP BY r "
                + "ORDER BY nbFilms, r.nom DESC, r.prenom DESC";

        Query query = em.createQuery(sql);
        List<Object[]> resultats = query.getResultList();
        for (Object[] ligne : resultats) {

//            System.out.println( String.format("%s %s %d", ligne[1], ligne[2], ligne[0]) );
        }
    }

    @Test // 21. Le nombre de films réalisés pour chaque genre ( GROUP BY )
    public void req21() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT COUNT(f), g.nom "
                + "FROM Film f "
                + "     JOIN f.genre g "
                + "GROUP BY g";

        Query query = em.createQuery(sql);
        List<Object[]> resultats = query.getResultList();
        for (Object[] ligne : resultats) {

//            System.out.println( ligne[1] + " " + ligne[0] );
        }
    }

    @Test // 20. Tous les films interprétés par Polanski et aussi tous les films d'horreur ( utiliser UNION ou MINUS ou INTERSECT )
    public void req20() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.acteurs a "
                + "WHERE    a.nom='Polanski' "
                + "UNION "
                + "SELECT   f "
                + "FROM     Film f "
                + "JOIN     f.genre g "
                + "WHERE    g.nom='Horreur'";

        Query query = em.createQuery(sql);

        Assert.assertEquals(1L, query.getResultList().size());
    }

    @Test // 19. Parmi tous les films, uniquement ceux interprétés par Polanski  ( utiliser UNION ou MINUS ou INTERSECT )
    public void req19() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT   f "
                + "FROM     Film f "
                + "INTERSECT "
                + "SELECT   f "
                + "FROM     Film f "
                + "JOIN     f.acteurs a "
                + "WHERE    a.nom='Polanski'";

        Query query = em.createQuery(sql);

        Assert.assertEquals(1L, query.getResultList().size());
    }

    @Test // 18. Tous les films d'horreur, sauf ceux interprétés par Polanski
    public void req18() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.genre g "
                + "WHERE    g.nom='Horreur' "
                + "EXCEPT "
                + "SELECT   f "
                + "FROM     Film f "
                + "JOIN     f.acteurs a "
                + "WHERE    a.nom='Polanski'";

        Query query = em.createQuery(sql);

        Assert.assertEquals(0L, query.getResultList().size());
    }

    @Test // 17. Le nombre totals de liens pour nos films d'horreur interprétés par Polanski
    public void req17() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = ""
                + "SELECT COUNT(l) "
                + "FROM     Lien l "
                + "         JOIN l.film f "
                + "         JOIN f.acteurs a "
                + "         JOIN f.genre g "
                + "WHERE    g.nom='Horreur' "
                + "         AND a.nom='Polanski'";

        Query query = em.createQuery(sql);

        Assert.assertEquals(1L, query.getSingleResult());
    }

    @Test // 16. Le nombre total de liens pour nos films policiers américains
    public void req16() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        String sql = "SELECT   COUNT(l) "
                + "FROM     Lien l "
                + "         JOIN l.film f "
                + "         JOIN f.genre g "
                + "         JOIN f.pays p "
                + "WHERE    g.nom='Policier' "
                + "         AND p.nom='USA'";

//       ACHTUNG! String sql = ""
//                + "SELECT   COUNT(l) "
//                + "FROM     Lien l "
//                + "         JOIN l.film.pays p "
//                + "WHERE    l.film.genre.nom='Policier' "
//                + "         AND p.nom='USA'";
        Query query = em.createQuery(sql);

        Assert.assertEquals(3L, query.getSingleResult());
    }

    @Test // 15. Le nombre d'épisodes de la saison 8 de la série Dexter
    public void req15() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   COUNT(e) "
                + "FROM     Episode e "
                + "WHERE    e.saison.serie.titre='Dexter' "
                + "         AND e.saison.numSaison=8");

        Assert.assertEquals(12L, query.getSingleResult());
    }

    @Test // 14. Le nombre total d'épisodes de la série Dexter
    public void req14() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   COUNT(e) "
                + "FROM     Episode e "
                + "WHERE    e.saison.serie.titre='Dexter' ");

        Assert.assertEquals(96L, query.getSingleResult());
    }

    @Test // 13. Le nombre de saisons de la série Dexter
    public void req13() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   COUNT(ss) "
                + "FROM     Saison ss "
                + "         JOIN ss.serie se "
                + "WHERE    se.titre='Dexter'");

        Assert.assertEquals(8L, query.getSingleResult());
    }

    @Test // 12. Le nombre de films policiers réalisés à la fois par les 2 frères Coen, et interprétés par Steve Buscemi
    public void req12() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.genre g "
                + "WHERE    g.nom='Policier' "
                + "INTERSECT "
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.realisateurs r "
                + "WHERE    r.nom='Coen' "
                + "         AND r.prenom='Ethan' "
                + "INTERSECT "
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.realisateurs r "
                + "WHERE    r.nom='Coen' "
                + "         AND r.prenom='Joel' "
                + "INTERSECT "
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.acteurs a "
                + "WHERE    a.nom='Buscemi' "
                + "         AND a.prenom='Steve' ");

        Assert.assertEquals(1L, query.getResultList().size());
    }

    @Test // 11. Le nombre de films réalisés à la fois par les 2 frères Coen, et interprétés par Steve Buscemi
    public void req11() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   COUNT(f) "
                + "FROM     Film f "
                + "         JOIN f.realisateurs r1 "
                + "         JOIN f.realisateurs r2 "
                + "         JOIN f.acteurs a "
                + "WHERE    r1.nom='Coen' "
                + "         AND r2.nom='Coen' "
                + "         AND r1.prenom='Joel' "
                + "         AND r2.prenom='Ethan' "
                + "         AND a.nom='Buscemi' "
                + "         AND a.prenom='Steve'");

        Assert.assertEquals(2L, query.getSingleResult());
    }

    @Test // 10. Le nombre de films réalisés à la fois par les 2 frères coen
    public void req10v2() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   COUNT(f) "
                + "FROM     Film f "
                + "         JOIN f.realisateurs r "
                + "         JOIN f.realisateurs r2 "
                + "WHERE    r.nom='Coen' "
                + "         AND r.prenom='Ethan' "
                + "         AND r2.nom='Coen'"
                + "         AND r2.prenom='Joel'");

        Assert.assertEquals(2L, query.getSingleResult());
    }

    @Test
    public void req10() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.realisateurs r "
                + "WHERE    r.nom='Coen' "
                + "         AND r.prenom='Ethan' "
                + "INTERSECT "
                + "SELECT   f "
                + "FROM     Film f "
                + "         JOIN f.realisateurs r "
                + "WHERE    r.nom='Coen' "
                + "         AND r.prenom='Joel' ");

        Assert.assertEquals(2L, query.getResultList().size());
    }

    @Test // 9. Le nombre de films réalisés par joel coen
    public void req9() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   COUNT(f) "
                + "FROM Film f "
                + "     JOIN f.realisateurs r "
                + "WHERE r.nom='Coen' AND "
                + "      r.prenom='Joel'");

        Assert.assertEquals(2L, query.getSingleResult());
    }

//    @Test
//    public void reqTest() {
//
//        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
//        EntityManager em = factory.createEntityManager();
//
//        Query query = em.createQuery(""
//                + "SELECT  f "
//                + "FROM     Film f "
//                + "         JOIN f.genre g "
//        );
//
//        List l = query.getResultList();
//        
//        System.err.println( "***" + l.size() );
//    }
//    @Test  // 8. Le titre du film d'horreur anglais réalisé par roman polanski
    public void req8() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT   f.titre "
                + "FROM     Film f "
                + "         JOIN f.genre g "
                + "         JOIN f.pays p "
                + "         JOIN f.realisateurs r "
                + "WHERE    g.nom='Horreur' "
                + "         AND p.nom='UK' "
                + "         AND r.nom='Polanski'");

        String titre = (String) query.getSingleResult();
        Assert.assertEquals("Le bal des vampire", titre);
    }

//    @Test  // 7. Nombre de films à la fois interprétés et réalisés par polanski
    public void req7() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT COUNT(f) "
                + "FROM Film f "
                + "     JOIN f.acteurs a "
                + "     JOIN f.realisateurs r "
                + "WHERE a.nom='Polanski' "
                + "      AND r.nom='Polanski'");
        long n = (long) query.getSingleResult();

        Assert.assertEquals(1, n);
    }

//    @Test // 4. Nombre de liens du film 'Big Lebowski (The)'
    public void req4() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery(""
                + "SELECT COUNT(l) "
                + "FROM Lien l "
                + "     JOIN l.film f "
                + "WHERE f.titre='Big Lebowski'");
        long n = (long) query.getSingleResult();

        Assert.assertEquals(0, n);
    }

//    @Test  // 3. Année de prod mini de nos films
    public void req3() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery("SELECT MIN(f.annee) FROM Film f");
        int n = (int) query.getSingleResult();

        Assert.assertEquals(1968, n);
    }

//    @Test  // 2. Vérifier le nombre de films
    public void req2() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery("SELECT COUNT(f) FROM Film f");
        long n = (long) query.getSingleResult();

        Assert.assertEquals(4, n);
    }

//    @Test  // 1. Vérifier que le titre du film d'id 4 est "Fargo"
    public void req1() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("PU");
        EntityManager em = factory.createEntityManager();

        Query query = em.createQuery("SELECT f FROM Film f WHERE f.id=4");
        Film film = (Film) query.getSingleResult();

        Assert.assertEquals("Fargo", film.getTitre());
    }
}
