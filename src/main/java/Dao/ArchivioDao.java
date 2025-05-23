package Dao;

import entities.ElementoCatalogo;
import entities.Libro;
import entities.Prestito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class ArchivioDao {

    private EntityManagerFactory emf;

    public ArchivioDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Aggiunta elemento
    public void aggiungiElemento(ElementoCatalogo elemento) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(elemento);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Rimozione per ISBN
    public void rimuoviElemento(String isbn) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ElementoCatalogo el = em.find(ElementoCatalogo.class, isbn);
            if (el != null) em.remove(el);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Ricerca per ISBN
    public ElementoCatalogo cercaPerIsbn(String isbn) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ElementoCatalogo.class, isbn);
        } finally {
            em.close();
        }
    }

    // Ricerca per anno pubblicazione
    public List<ElementoCatalogo> cercaPerAnno(int anno) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ElementoCatalogo> query = em.createQuery(
                    "SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno", ElementoCatalogo.class);
            query.setParameter("anno", anno);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Ricerca Libro per autore
    public List<Libro> cercaPerAutore(String autore) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery(
                    "SELECT l FROM Libro l WHERE l.autore = :autore", Libro.class);
            query.setParameter("autore", autore);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Ricerca per titolo
    public List<ElementoCatalogo> cercaPerTitolo(String titolo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ElementoCatalogo> query = em.createQuery(
                    "SELECT e FROM ElementoCatalogo e WHERE LOWER(e.titolo) LIKE LOWER(:titolo)", ElementoCatalogo.class);
            query.setParameter("titolo", "%" + titolo + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Ricerca elementi in prestito con il numero tessera
    public List<Prestito> cercaPrestitiPerTessera(int numeroTessera) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Prestito> query = em.createQuery(
                    "SELECT p FROM Prestito p WHERE p.utente.numeroTessera = :tessera AND p.dataRestituzioneEffettiva IS NULL", Prestito.class);
            query.setParameter("tessera", numeroTessera);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Ricerca prestiti scaduti e non restituiti
    public List<Prestito> prestitiScadutiNonRestituiti() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Prestito> query = em.createQuery(
                    "SELECT p FROM Prestito p WHERE p.dataRestituzionePrevista < :oggi AND p.dataRestituzioneEffettiva IS NULL", Prestito.class);
            query.setParameter("oggi", LocalDate.now());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // (Opzionale) Aggiorna un elemento
    public void aggiornaElemento(ElementoCatalogo elemento) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(elemento);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
