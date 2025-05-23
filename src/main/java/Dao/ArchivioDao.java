package Dao;

import entities.ElementoCatalogo;
import entities.Libro;
import entities.Rivista;
import entities.Prestito;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public class ArchivioDao {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("Name_Pratica_S3_L5_Esame_Settimanale_3");

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public boolean aggiungiLibro(Libro libro) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(libro);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean aggiungiRivista(Rivista rivista) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(rivista);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean rimuoviElemento(UUID isbn) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ElementoCatalogo elemento = em.find(ElementoCatalogo.class, isbn);
            if (elemento != null) {
                em.remove(elemento);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public ElementoCatalogo ricercaPerISBN(UUID isbn) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(ElementoCatalogo.class, isbn);
        } finally {
            em.close();
        }
    }

    public List<ElementoCatalogo> ricercaPerAnno(int anno) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ElementoCatalogo> query = em.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno", ElementoCatalogo.class);
            query.setParameter("anno", anno);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Libro> ricercaPerAutore(String autore) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery("SELECT l FROM Libro l WHERE LOWER(l.autore) LIKE LOWER(CONCAT('%', :autore, '%'))", Libro.class);
            query.setParameter("autore", autore);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ElementoCatalogo> ricercaPerTitolo(String titolo) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ElementoCatalogo> query = em.createQuery("SELECT e FROM ElementoCatalogo e WHERE LOWER(e.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))", ElementoCatalogo.class);
            query.setParameter("titolo", titolo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Prestito> ricercaPrestitiPerTessera(String numeroTessera) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Prestito> query = em.createQuery("SELECT p FROM Prestito p WHERE p.utente.numeroTessera = :numTessera", Prestito.class);
            query.setParameter("numTessera", numeroTessera);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Prestito> ricercaPrestitiScaduti() {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDate oggi = LocalDate.now();
            TypedQuery<Prestito> query = em.createQuery("SELECT p FROM Prestito p WHERE p.dataRestituzioneEffettiva IS NULL AND p.dataRestituzionePrevista < :oggi\n", Prestito.class);
            query.setParameter("oggi", oggi);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
