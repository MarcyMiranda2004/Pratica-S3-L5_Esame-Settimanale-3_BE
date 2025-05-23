package entities;

import enums.Periodicita;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class BibliotecaMain {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("Name_Pratica_S3_L5_Esame_Settimanale_3");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n-- Menu Biblioteca --");
            System.out.println("1. Aggiungi Libro");
            System.out.println("2. Aggiungi Rivista");
            System.out.println("3. Rimuovi elemento per ISBN");
            System.out.println("4. Ricerca per ISBN");
            System.out.println("5. Ricerca per anno pubblicazione");
            System.out.println("6. Ricerca per autore (solo libri)");
            System.out.println("7. Ricerca per titolo o parte di esso");
            System.out.println("8. Ricerca prestiti per numero tessera");
            System.out.println("9. Prestiti scaduti non restituiti");
            System.out.println("0. Esci");

            int scelta = Integer.parseInt(scanner.nextLine());

            switch (scelta) {
                case 1 -> aggiungiLibro(scanner);
                case 2 -> aggiungiRivista(scanner);
                case 3 -> rimuoviElemento(scanner);
                case 4 -> ricercaPerISBN(scanner);
                case 5 -> ricercaPerAnno(scanner);
                case 6 -> ricercaPerAutore(scanner);
                case 7 -> ricercaPerTitolo(scanner);
                case 8 -> ricercaPrestitiPerTessera(scanner);
                case 9 -> ricercaPrestitiScaduti();
                case 0 -> running = false;
                default -> System.out.println("Scelta non valida");
            }
        }
        emf.close();
    }

    private static void aggiungiLibro(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Titolo:");
            String titolo = scanner.nextLine();
            System.out.println("Anno pubblicazione:");
            int anno = Integer.parseInt(scanner.nextLine());
            System.out.println("Numero pagine:");
            int pagine = Integer.parseInt(scanner.nextLine());
            System.out.println("Autore:");
            String autore = scanner.nextLine();
            System.out.println("Genere:");
            String genere = scanner.nextLine();

            Libro libro = new Libro(titolo, anno, pagine, autore, genere);

            em.getTransaction().begin();
            em.persist(libro);
            em.getTransaction().commit();
            System.out.println("Libro aggiunto con successo!");
        } finally {
            em.close();
        }
    }

    private static void aggiungiRivista(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Titolo:");
            String titolo = scanner.nextLine();
            System.out.println("Anno pubblicazione:");
            int anno = Integer.parseInt(scanner.nextLine());
            System.out.println("Numero pagine:");
            int pagine = Integer.parseInt(scanner.nextLine());
            System.out.println("Periodicita (SETTIMANALE, MENSILE, SEMESTRALE):");
            Periodicita periodicita = Periodicita.valueOf(scanner.nextLine().toUpperCase());

            Rivista rivista = new Rivista(titolo, anno, pagine, periodicita);

            em.getTransaction().begin();
            em.persist(rivista);
            em.getTransaction().commit();
            System.out.println("Rivista aggiunta con successo!");
        } finally {
            em.close();
        }
    }

    private static void rimuoviElemento(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Inserisci ISBN:");
            String isbn = scanner.nextLine();
            ElementoCatalogo elemento = em.find(ElementoCatalogo.class, UUID.fromString(isbn));

            if (elemento != null) {
                em.getTransaction().begin();
                em.remove(elemento);
                em.getTransaction().commit();
                System.out.println("Elemento rimosso.");
            } else {
                System.out.println("Elemento non trovato.");
            }
        } finally {
            em.close();
        }
    }

    private static void ricercaPerISBN(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Inserisci ISBN:");
            String isbn = scanner.nextLine();
            ElementoCatalogo elemento = em.find(ElementoCatalogo.class, UUID.fromString(isbn));
            System.out.println(elemento != null ? elemento : "Elemento non trovato.");
        } finally {
            em.close();
        }
    }

    private static void ricercaPerAnno(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Inserisci anno:");
            int anno = Integer.parseInt(scanner.nextLine());
            List<ElementoCatalogo> risultati = em.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno", ElementoCatalogo.class)
                    .setParameter("anno", anno).getResultList();
            risultati.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    private static void ricercaPerAutore(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Inserisci autore:");
            String autore = scanner.nextLine();
            List<Libro> risultati = em.createQuery("SELECT l FROM Libro l WHERE l.autore ILIKE :autore", Libro.class)
                    .setParameter("autore", "%" + autore + "%").getResultList();
            risultati.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    private static void ricercaPerTitolo(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Inserisci titolo o parte di esso:");
            String titolo = scanner.nextLine();
            List<ElementoCatalogo> risultati = em.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.titolo ILIKE :titolo", ElementoCatalogo.class)
                    .setParameter("titolo", "%" + titolo + "%").getResultList();
            risultati.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    private static void ricercaPrestitiPerTessera(Scanner scanner) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("Inserisci numero tessera:");
            int numero = Integer.parseInt(scanner.nextLine());
            List<Prestito> prestiti = em.createQuery("SELECT p FROM Prestito p WHERE p.utente.numeroTessera = :numero", Prestito.class)
                    .setParameter("numero", numero).getResultList();
            prestiti.forEach(System.out::println);
        } finally {
            em.close();
        }
    }

    private static void ricercaPrestitiScaduti() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Prestito> scaduti = em.createQuery("SELECT p FROM Prestito p WHERE p.dataRestituzioneEffettiva IS NULL AND p.dataRestituzionePrevista < :oggi", Prestito.class)
                    .setParameter("oggi", LocalDate.now()).getResultList();
            scaduti.forEach(System.out::println);
        } finally {
            em.close();
        }
    }
}
