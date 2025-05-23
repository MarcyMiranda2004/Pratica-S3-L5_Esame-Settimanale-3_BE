package entities;

import Dao.ArchivioDao;
import enums.Periodicita;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class BibliotecaMain {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ArchivioDao archivioDao = new ArchivioDao();

    public static void main(String[] args) {
        inserisciDatiTest();

        boolean running = true;

        while (running) {
            System.out.println("\n-- Menu Biblioteca --");
            System.out.println("1. Aggiungi un Elemento");
            System.out.println("2. Rimuovi Un Elemento per ISBN");
            System.out.println("3. Ricerca Un Elemento per ISBN");
            System.out.println("4. Ricerca Un Elemento per anno pubblicazione");
            System.out.println("5. Ricerca Un Libro per autore");
            System.out.println("6. Ricerca Un Elemento per titolo");
            System.out.println("7. Ricerca prestiti per numero tessera");
            System.out.println("8. Prestiti scaduti non restituiti");
            System.out.println("0. Esci");

            int scelta = leggiIntero(0, 8);

            switch (scelta) {
                case 1 -> aggiungiElemento();
                case 2 -> rimuoviElemento();
                case 3 -> ricercaPerISBN();
                case 4 -> ricercaPerAnno();
                case 5 -> ricercaPerAutore();
                case 6 -> ricercaPerTitolo();
                case 7 -> ricercaPrestitiPerTessera();
                case 8 -> ricercaPrestitiScaduti();
                case 0 -> running = false;
            }
        }

        System.out.println("Chiusura programma. Arrivederci!");
        scanner.close();
        archivioDao.close();
    }

    //dati test
    private static void inserisciDatiTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Name_Pratica_S3_L5_Esame_Settimanale_3");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Utente utente1 = TestData.creaUtente1();
            Utente utente2 = TestData.creaUtente2();
            em.persist(utente1);
            em.persist(utente2);

            em.flush();

            Libro libro1 = TestData.creaLibro1();
            Libro libro2 = TestData.creaLibro2();
            Rivista rivista1 = TestData.creaRivista1();
            em.persist(libro1);
            em.persist(libro2);
            em.persist(rivista1);

            // Prestito non restituito, eventualmente scaduto
            Prestito prestitoAperto = TestData.creaPrestitoAperto(utente1, libro1);
            em.persist(prestitoAperto);

            // Prestito già restituito
            Prestito prestitoConRestituzione = TestData.creaPrestitoConRestituzione(utente2, rivista1);
            em.persist(prestitoConRestituzione);

            em.getTransaction().commit();
            System.out.println("Dati di test inseriti con successo.");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println("Errore durante l'inserimento dei dati di test.");
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    private static int leggiIntero(int min, int max) {
        int input = -1;
        while (true) {
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input < min || input > max) {
                    System.out.println("Inserisci un numero valido tra " + min + " e " + max + ".");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Input non valido, inserisci un numero intero.");
            }
        }
        return input;
    }

    private static void aggiungiElemento() {
        boolean aggiungiRunning = true;

        while (aggiungiRunning) {
            System.out.println("\n-- Sotto-menu Aggiungi elemento --");
            System.out.println("1. Aggiungi Libro");
            System.out.println("2. Aggiungi Rivista");
            System.out.println("0. Torna al menu principale");

            int sottoScelta = leggiIntero(0, 2);

            switch (sottoScelta) {
                case 1 -> {
                    Libro libro = creaLibroDaInput();
                    if (libro != null) {
                        boolean ok = archivioDao.aggiungiLibro(libro);
                        System.out.println(ok ? "Libro aggiunto con successo!" : "Errore durante l'inserimento del libro.");
                    }
                }
                case 2 -> {
                    Rivista rivista = creaRivistaDaInput();
                    if (rivista != null) {
                        boolean ok = archivioDao.aggiungiRivista(rivista);
                        System.out.println(ok ? "Rivista aggiunta con successo!" : "Errore durante l'inserimento della rivista.");
                    }
                }
                case 0 -> aggiungiRunning = false;
            }
        }
    }

    private static Libro creaLibroDaInput() {
        System.out.println("Inserisci titolo:");
        String titolo = scanner.nextLine();

        System.out.println("Inserisci anno pubblicazione:");
        int anno = leggiIntero(1000, 2100);

        System.out.println("Inserisci numero pagine:");
        int pagine = leggiIntero(1, 10000);

        System.out.println("Inserisci autore:");
        String autore = scanner.nextLine();

        System.out.println("Inserisci genere:");
        String genere = scanner.nextLine();

        return new Libro(titolo, anno, pagine, autore, genere);
    }

    private static Rivista creaRivistaDaInput() {
        System.out.println("Inserisci titolo:");
        String titolo = scanner.nextLine();

        System.out.println("Inserisci anno pubblicazione:");
        int anno = leggiIntero(1000, 2100);

        System.out.println("Inserisci numero pagine:");
        int pagine = leggiIntero(1, 10000);

        System.out.println("Inserisci periodicità (SETTIMANALE, MENSILE, SEMESTRALE):");
        String periodicitaInput = scanner.nextLine().toUpperCase();

        Periodicita periodicita;

        try {
            periodicita = Periodicita.valueOf(periodicitaInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Periodicità non valida.");
            return null;
        }

        return new Rivista(titolo, anno, pagine, periodicita);
    }

    private static void rimuoviElemento() {
        System.out.println("Inserisci ISBN dell'elemento da rimuovere:");
        String isbnStr = scanner.nextLine();

        try {
            UUID isbn = UUID.fromString(isbnStr);
            boolean ok = archivioDao.rimuoviElemento(isbn);
            System.out.println(ok ? "Elemento rimosso con successo." : "Elemento non trovato.");
        } catch (IllegalArgumentException e) {
            System.out.println("ISBN non valido.");
        }
    }

    private static void ricercaPerISBN() {
        System.out.println("Inserisci ISBN da cercare:");
        String isbnStr = scanner.nextLine();

        try {
            UUID isbn = UUID.fromString(isbnStr);
            ElementoCatalogo elemento = archivioDao.ricercaPerISBN(isbn);
            if (elemento != null) {
                System.out.println(elemento);
            } else {
                System.out.println("Elemento non trovato.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ISBN non valido.");
        }
    }

    private static void ricercaPerAnno() {
        System.out.println("Inserisci anno pubblicazione da cercare:");
        int anno = leggiIntero(1000, 2100);

        List<ElementoCatalogo> risultati = archivioDao.ricercaPerAnno(anno);
        if (risultati.isEmpty()) {
            System.out.println("Nessun elemento trovato per l'anno indicato.");
        } else {
            risultati.forEach(System.out::println);
        }
    }

    private static void ricercaPerAutore() {
        System.out.println("Inserisci autore da cercare:");
        String autore = scanner.nextLine();

        List<Libro> libri = archivioDao.ricercaPerAutore(autore);
        if (libri.isEmpty()) {
            System.out.println("Nessun libro trovato per l'autore indicato.");
        } else {
            libri.forEach(System.out::println);
        }
    }

    private static void ricercaPerTitolo() {
        System.out.println("Inserisci titolo o parola chiave da cercare:");
        String titolo = scanner.nextLine();

        List<ElementoCatalogo> risultati = archivioDao.ricercaPerTitolo(titolo);
        if (risultati.isEmpty()) {
            System.out.println("Nessun elemento trovato per il titolo indicato.");
        } else {
            risultati.forEach(System.out::println);
        }
    }

    private static void ricercaPrestitiPerTessera() {
        System.out.println("Inserisci numero tessera:");
        int numero = leggiIntero(1, Integer.MAX_VALUE);
        String numeroTessera = String.valueOf(numero);

        List<Prestito> prestiti = archivioDao.ricercaPrestitiPerTessera(numeroTessera);
        if (prestiti.isEmpty()) {
            System.out.println("Nessun prestito trovato per il numero tessera inserito.");
        } else {
            prestiti.forEach(System.out::println);
        }
    }

    private static void ricercaPrestitiScaduti() {
        List<Prestito> prestiti = archivioDao.ricercaPrestitiScaduti();
        if (prestiti.isEmpty()) {
            System.out.println("Nessun prestito scaduto non restituito.");
        } else {
            prestiti.forEach(System.out::println);
        }
    }
}
