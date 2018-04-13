/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package streaming.test;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.junit.Test;
import static org.junit.Assert.*;
import streaming.entity.Film;

/**
 *
 * @author Formation
 */
public class ExerciceTest {
    
    @Test // 1. Vérifier que le titre du film d'id 4 est "Fargo"
    public void req1() {
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        /* Peut se décomposer en deux lignes :
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");
        EntityManager em = emf.createEntityManager();
        */
        Query q = em.createQuery("SELECT f FROM Film f WHERE f.id=4");
        Film film = (Film) q.getSingleResult();
        assertEquals("Fargo", film.getTitre());
        // System.out.println("1 : " + film.getTitre());        
    }
    
    @Test // 2. Vérifier le nombre de films
    public void req2() {      
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query query = em.createQuery("SELECT COUNT(f) FROM Film f");
        long r = (long) query.getSingleResult(); // Long ou long, à savoir : le COUNT renvoie du long
        assertEquals(4, r); // Le Long avec une majuscule ne compile pas avec assertEquals
        // System.out.println("2 : " + r);
    }
    
    @Test // 3. Année de prod mini de nos films
    public void req3() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT MIN(f.annee) FROM Film f"); // MIN renvoie le minimum d'une colonne d'une entité
        // Aller dans la classe Film pour voir que l'année renvoie de l'Integer
        // Integer ok mais le "int" fonctionne car NetBeans fait le casting automatiquement
        int i = (int) q.getSingleResult();
        assertEquals(1968, i); // int par défaut
        // System.out.println("3 : " + i); 
    }
    
    
    @Test // 4. Nombre de liens du film 'Big Lebowski (The)'
    public void req4() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT COUNT(l) FROM Lien l JOIN l.film f WHERE f.titre='Big Lebowski (The)'");
        long nbLiens = (long) q.getSingleResult();
        // System.out.println("4 : " + nbLiens);
        // deprecated quand il barre dans notre cas "Assert. " qui veut dire que la fonction n'existera peut-être plus dans Java9 
        assertEquals(1L, nbLiens); // Si je mets "10L", me renvoie l'erreur : Failed tests:   req4(streaming.test.ExerciceTest): expected:<10> but was:<1>
            // parce que nbLiens=1 (en Long) et non 10 (en Long), écrire "10L" revient à écrire "(Long) 10"
    } 
    
    @Test // 5. Nombre de films réalisés par Polanski
    public void req5() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT COUNT(f) FROM Film f JOIN f.realisateurs r WHERE r.nom='Polanski'");
        // "realisateurs" est une variable (List) de la classe Film
        long nbFilmsRealisateurPolanski = (long) q.getSingleResult();
        assertEquals(2L, nbFilmsRealisateurPolanski);
        // System.out.println("5 : " + nbFilmsP);
    }
    
    @Test // 6. Nombre de films interprétés par Polanski
    public void req6() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT COUNT(f) FROM Film f JOIN f.acteurs a WHERE a.nom='Polanski'");
        long nbFilmsActeurPolanski = (long) q.getSingleResult();
        // System.out.println("6 : " + nbFilmsActeurPolanski);
        assertEquals(1L, nbFilmsActeurPolanski);
    }
    
    @Test // 7. Nombre de films à la fois interprétés et réalisés par polanski
    public void req7() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT COUNT(f) FROM Film f JOIN f.acteurs a JOIN f.realisateurs r WHERE a.nom='Polanski' AND r.nom='Polanski'");
        long nbFilmsActRealPolanski = (long) q.getSingleResult();
        // System.out.println("7 : " + nbFilmsActRealPolanski);
        assertEquals(1L, nbFilmsActRealPolanski);
    }
    
    @Test // 8. Le titre du film d'horreur anglais réalisé par roman polanski
    public void req8() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT f FROM Film f JOIN f.genre g JOIN f.pays p JOIN f.realisateurs r WHERE g.nom='Horreur' AND p.nom='UK' AND r.nom='Polanski'");
        Film film = (Film) q.getSingleResult();
        assertEquals("Le bal des vampires", film.getTitre());
        // System.out.println(film.getTitre());
    }
    
    @Test // 9. Le nombre de films réalisés par joel coen
    public void req9() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT COUNT(f) FROM Film f JOIN f.realisateurs r WHERE r.nom='Coen' AND r.prenom='Joel'");
        long nbFilmsJCoen = (long) q.getSingleResult();
        assertEquals(2L, nbFilmsJCoen);
    }
    
    @Test // 10. Le nombre de films réalisés à la fois par les 2 frères coen
    public void req10() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        
        // Solution 1 : Liste de films avec INTERSECT et faire un SIZE sur la liste (et non un COUNT)
        Query q = em.createQuery("SELECT f FROM Film f JOIN f.realisateurs r WHERE r.nom='Coen' AND r.prenom='Ethan' INTERSECT "
                + "SELECT f FROM Film f JOIN f.realisateurs r WHERE r.nom='Coen' AND r.prenom='Joel'");
        List<Film> films = q.getResultList(); 
        assertEquals(2, films.size()); // fonctionne aussi avec 2L (size retourne du int, insertEquals(long, long))
        /* System.out.println("Nombre : " + films.size());
        for (Film film : films) {
            System.out.println("10 : " + film.getTitre());
        } */
                
        // Solution 2 : Avec COUNT et 2 JOIN f.realisateurs    
        Query q2 = em.createQuery("SELECT COUNT(f) FROM Film f JOIN f.realisateurs rParJoel JOIN f.realisateurs rParEthan "
                + "WHERE rParJoel.nom='Coen' AND rParJoel.prenom='Joel' AND rParEthan.nom='Coen' AND rParEthan.prenom='Ethan'");
        long nbFilmsJECoen = (long) q2.getSingleResult();
        assertEquals(2L, nbFilmsJECoen);  
    }
    
    @Test // 11. Le nombre de films réalisés à la fois par les 2 frères Coen, et interprétés par Steve Buscemi
    public void req11() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        // Query q = em.createQuery("");
        
        // à finir ...
    }
    
    @Test // 17. Le nombre totals de liens pour nos films d'horreur interprétés par Polanski
    public void req17() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT COUNT(l) ....");
        
        // à finir ... voir le fichier jpql
    }
    
    @Test // 21. Le nombre de films réalisés pour chaque genre ( GROUP BY )
    public void req21() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT g.nom, COUNT(f) FROM Genre g JOIN g.films f GROUP BY g"); // (ou g.id)
        List<Object[]> res = q.getResultList();
        assertEquals(3, res.size());
        /*
        System.out.println("Taille de la liste : " + res.size());
        for (Object[] tab : res) {
            // String nomGenre = (String) tab[0];
            // long nbFilms = (long) tab[1];
            // System.out.println(nomGenre + " " + nbFilms);
            System.out.println(tab[0] + " " + tab[1]);
        }
        */
    }
    
    
    @Test // 22. Le nombre de films réalisés pour chaque réalisateur, triés par ordre croissant puis par ordre alphabétique ( GROUP BY )
    public void req22() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("SELECT p.nom, COUNT(f) FROM Personne p JOIN p.filmsRealises f GROUP BY f ORDER BY p.nom");
        List<Object[]> res = q.getResultList();
        // System.out.println("Nombre : " + res.size());
        /*
        for (Object[] tab : res) {
            System.out.println(tab[0] + " " + tab[1]);
        }*/
    // assertEquals();
        
    
    
    }
    
    @Test // 25. Le nombre total d'épisodes pour chaque série, pour peu qu'il y ait plus de 5 épisodes au total. Le tout trié par nbre d'épisodes.
    public void req25() {
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        Query q = em.createQuery("");
        
    }
    
    
    @Test
    public void ajouter() {
        
        Film f = new Film();
        
        f.setTitre("Blabla");
        f.setAnnee(2018);
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        em.getTransaction().begin();
        em.persist(f);
        em.getTransaction().commit();
        
        Film f2 = em.find(Film.class, 3L); // Clé primaire du film à récupérer
        System.out.println(f2.getTitre()); // Pas besoin de faire de SELECT je peux récupérer avec le get.
        
    }
    
}    
    
    /* SOLUTIONS DANS : JPQLTest.java (alt+shift+o)
    12. Le nombre de films policiers réalisés à la fois par les 2 frères Coen, et interprétés par Steve Buscemi

13. Le nombre de saisons de la série Dexter

14. Le nombre total d'épisodes de la série Dexter

15. Le nombre d'épisodes de la saison 8 de la série Dexter

16. Le nombre total de liens pour nos films policiers américains

17. Le nombre totals de liens pour nos films d'horreur interprétés par Polanski

18. Tous les films d'horreur, sauf ceux interprétés par Polanski ( utiliser UNION ou MINUS ou INTERSECT )

19. Parmi tous les films, uniquement ceux interprétés par Polanski  ( utiliser UNION ou MINUS ou INTERSECT )

20. Tous les films interprétés par Polanski et aussi tous les films d'horreur ( utiliser UNION ou MINUS ou INTERSECT )

21. Le nombre de films réalisés pour chaque genre ( GROUP BY )

22. Le nombre de films réalisés pour chaque réalisateur, triés par ordre croissant puis par ordre alphabétique ( GROUP BY )

23. Le nombre de films réalisés pour chaque réalisateur, uniquement si >= 2 ( GROUP BY et HAVING )

24. Le nombre total de saisons pour chaque série, triés par ordre croissant de saisons, puis par ordre alphabétique.



24:
SELECT sr.titre, COUNT(sa) AS total
LEFT JOIN sr.saisons sa // Si c'est un join tout court avec 0 épisode, ça ne fera pas partie des résultats, avec le LEFT ça fera partie des résultats même s'il n'y a pas de saison 
GROUP BY sr 
ORDER BY total, sr.titre ";

INNER JOIN et JOIN, c'est la même chose
Le AS est optionnel aussi
LEFT (OUTER) JOIN ou RIGHT (OUTER) JOIN


    */