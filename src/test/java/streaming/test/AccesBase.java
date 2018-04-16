/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package streaming.test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.junit.Test;
import static org.junit.Assert.*;
import streaming.entity.Film;
import streaming.entity.Genre;
import streaming.entity.Pays;

/**
 *
 * @author Formation
 */
public class AccesBase {
    
    // @Test // Si je mets cette ligne en commentaire, le test (et donc cette fonction) ne sera pas exécuté(e)
    public void ajouter() { // Ajoute un film dans ma BDD
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        
        em.getTransaction().begin();
        
        // Récupère genre Fantastique
        Genre g = em.find(Genre.class, 3L); // La fonction "find" récupère une entité à partir d'une id et de sa classe
        // Le 3L correspond à l'id 3 pour le GENRE_ID, qui est "fantastique"
/*            // le "find" est synonyme de : "SELECT g FROM Genre g WHERE g.id=3L"
            Query q = em.createQuery("SELECT g FROM Genre g WHERE g.nom='Fantastique'"); // Choisir le (qlString)
            // Tout ce qu'on doit importer est tout ce qui n'est pas dans mon package (sauf certaines classes très utilisées comme String, Long, Integer, etc.)
            Genre g2 = (Genre) q.getSingleResult();
*/      

        // Récupère la France et les usa
        Pays france = em.find(Pays.class, 2L); // dès qu'il est managé on peut faire des set
        Pays usa = em.find(Pays.class, 3L); // variable statique .class dans la classe Object, le find il faut lui dire de quel type d'entité il faut récupérer l'instance (il ne faut pas confondre avec Pays.class)
        
        
        // Ajouter
        Film f = new Film();
        
        f.getPays().add(france);
        france.getFilmsProduits().add(f);
        
        f.getPays().add(usa);
        france.getFilmsProduits().add(f);
        
        
        // Caused by: ERROR 23505: L'instruction a été abandonnée car elle aurait entraîné une valeur de clé en double dans une contrainte de clé primaire ou unique, ou un index unique identifié par 'SQL180411163144200' défini sur 'FILM'.
        
        // f.setId(1L);
        f.setTitre("Le Cinquième Élément");
        f.setAnnee(1997);
        
        f.setGenre(g); // Si le setGenre n'est pas proposé, c'est qu'on n'avait pas les getter et setter dans Film.java
        g.getFilms().add(f); // Ces deux lignes correspondent au bidirectionnel pour la relation ManyToOne entre Film et Genre
        
        // f.setPays("France"); // Dans Film.java, c'est du ManyToMany
        f.setDuree(126);
        
        em.persist(f); // Lie mon objet à mon EntityManager (gestionnaire d'entités) pour l'inclure dans le "commit" sinon il ne se passera rien.
        
        em.getTransaction().commit();
    }
    
    // @Test
    public void ajouterGenreSF() {
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        em.getTransaction().begin();
        
        Genre g = new Genre();
        // g.setId(Long.MIN_VALUE); // setId facultatif //CTRL+SHIFT+C pour mettre les lignes en commentaires
        g.setNom("SF");
        // Pour que ça marche : j'ai enlevé ces 2 SET, ai exécuté (ai eu une erreur), ai remis les SET, ai exécuté, et ça marche ! (sans doute pb NetBeans, même chose chez les autres)
        
        em.persist(g); // pour faire un "insert"
        em.getTransaction().commit();
    }
    
    
    
    // @Test
    public void modifGenreParSet() {
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        em.getTransaction().begin();
        
        // 1. Récupère genre id 1
        Genre g = em.find(Genre.class, 1L); // g passe à l'état managé par l'EntityManager
        
        // 2. Modif via set
        g.setNom("Horreur");
        
        // en bdd ici : Horreur, Comédie, Fantastique, Policier, Historique, SF
        
        em.getTransaction().commit(); // puisque mon entité est managed, tout set sur mon objet va se transformer en update dans ma bdd
        
        
    }
    
    // @Test
    public void modifGenreParMerge() {
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        em.getTransaction().begin();
        
        Genre g = new Genre();
        g.setNom("Horreur");
        g.setId(1L);
        
        em.merge(g);
        
        em.getTransaction().commit();
    }
    
    // @Test
    public void remove1() {
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        em.getTransaction().begin();
        
        Genre g = em.find(Genre.class, 10L); // find renvoie null si ne trouve pas  // Quand on fait un "find" c'est une requête SELECT qui s'exécute
        em.remove(g); // le "find" me renvoie l'entité que je "remove" après
        
        em.getTransaction().commit();
    }
    
    @Test
    public void remove2() { // voici l'équivalent en requête sql, c'est plus performant parce qu'il n'y a qu'une requête au lieu de deux
        // Pas d'erreur ou d'exceptions en cas d'erreur si l'id n'exite pas, contrairement au "remove" vu plus haut.
        
        EntityManager em = Persistence.createEntityManagerFactory("PU").createEntityManager();
        em.getTransaction().begin();
        
        Query q = em.createQuery("DELETE FROM Genre g WHERE g.id=60"); // Le L après le nombre de d'id ne semble pas obligatoire ici.
        q.executeUpdate(); // Update pas dans le sens de update, mais dans le sens que ce n'est pas un SELECT (on l'utilise ici pour le DELETE)
        
        em.getTransaction().commit();
    }
    
    
    
}
