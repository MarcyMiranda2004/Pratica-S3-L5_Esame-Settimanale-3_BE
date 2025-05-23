package Dao;

import entities.ElementoCatalogo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ElementoDao {
    private final EntityManager em;

    public ElementoDao(EntityManager em) {
        this.em = em;
    }

    public void salvaElemento(ElementoCatalogo e) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(e);
        tx.commit();
    }

    public ElementoCatalogo getById(Long id) {
        return em.find(ElementoCatalogo.class, id);
    }

    public List<ElementoCatalogo> getAll() {
        return em.createQuery("SELECT e FROM ElementoCatalogo e", ElementoCatalogo.class).getResultList();
    }

    public List<ElementoCatalogo> findByTitolo(String titolo) {
        return em.createQuery("SELECT e FROM ElementoCatalogo e WHERE LOWER(e.titolo) LIKE LOWER(:titolo)", ElementoCatalogo.class)
                .setParameter("titolo", "%" + titolo + "%")
                .getResultList();
    }
}
