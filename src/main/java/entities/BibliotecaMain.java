package entities;

import enums.Periodicita;
import Dao.ArchivioDao;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class BibliotecaMain {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ArchivioDao archivioDao = new ArchivioDao();

    public static void main(String[] args) {

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
        try {
            System.out.println("Titolo:");
            String titolo = scanner.nextLine();

            System.out.println("Anno pubblicazione:");
            int anno = leggiIntero(0, 3000);

            System.out.println("Numero pagine:");
            int pagine = leggiIntero(1, Integer.MAX_VALUE);

            System.out.println("Autore:");
            String autore = scanner.nextLine();

            System.out.println("Genere:");
            String genere = scanner.nextLine();

            return new Libro(titolo, anno, pagine, autore, genere);

        } catch (Exception e) {
            System.out.println("Errore durante la creazione del libro: " + e.getMessage());
            return null;
        }
    }

    private static Rivista creaRivistaDaInput() {
        try {
            System.out.println("Titolo:");
            String titolo = scanner.nextLine();

            System.out.println("Anno pubblicazione:");
            int anno = leggiIntero(0, 3000);

            System.out.println("Numero pagine:");
            int pagine = leggiIntero(1, Integer.MAX_VALUE);

            Periodicita periodicita = null;
            boolean valid = false;
            while (!valid) {
                System.out.println("Scegli la periodicità:");
                System.out.println("1. SETTIMANALE");
                System.out.println("2. MENSILE");
                System.out.println("3. SEMESTRALE");
                System.out.println("0. Annulla");

                int scelta = leggiIntero(0, 3);
                switch (scelta) {
                    case 1 -> {
                        periodicita = Periodicita.SETTIMANALE;
                        valid = true;
                    }
                    case 2 -> {
                        periodicita = Periodicita.MENSILE;
                        valid = true;
                    }
                    case 3 -> {
                        periodicita = Periodicita.SEMESTRALE;
                        valid = true;
                    }
                    case 0 -> {
                        System.out.println("Inserimento rivista annullato.");
                        return null;
                    }
                }
            }

            return new Rivista(titolo, anno, pagine, periodicita);

        } catch (Exception e) {
            System.out.println("Errore durante la creazione della rivista: " + e.getMessage());
            return null;
        }
    }

    private static void rimuoviElemento() {
        System.out.println("Inserisci ISBN:");
        String inputIsbn = scanner.nextLine();
        UUID isbn;
        try {
            isbn = UUID.fromString(inputIsbn);
        } catch (IllegalArgumentException e) {
            System.out.println("ISBN non valido.");
            return;
        }

        boolean ok = archivioDao.rimuoviElemento(isbn);
        System.out.println(ok ? "Elemento rimosso." : "Elemento non trovato o errore nella rimozione.");
    }

    private static void ricercaPerISBN() {
        System.out.println("Inserisci ISBN:");
        String inputIsbn = scanner.nextLine();
        UUID isbn;
        try {
            isbn = UUID.fromString(inputIsbn);
        } catch (IllegalArgumentException e) {
            System.out.println("ISBN non valido.");
            return;
        }

        ElementoCatalogo elemento = archivioDao.ricercaPerISBN(isbn);
        System.out.println(elemento != null ? elemento : "Elemento non trovato.");
    }

    private static void ricercaPerAnno() {
        System.out.println("Inserisci anno:");
        int anno = leggiIntero(0, 3000);

        List<ElementoCatalogo> risultati = archivioDao.ricercaPerAnno(anno);
        if (risultati.isEmpty()) {
            System.out.println("Nessun risultato trovato.");
        } else {
            risultati.forEach(System.out::println);
        }
    }

    private static void ricercaPerAutore() {
        System.out.println("Inserisci autore:");
        String autore = scanner.nextLine();

        List<Libro> risultati = archivioDao.ricercaPerAutore(autore);
        if (risultati.isEmpty()) {
            System.out.println("Nessun risultato trovato.");
        } else {
            risultati.forEach(System.out::println);
        }
    }

    private static void ricercaPerTitolo() {
        System.out.println("Inserisci titolo o parte di esso:");
        String titolo = scanner.nextLine();

        List<ElementoCatalogo> risultati = archivioDao.ricercaPerTitolo(titolo);
        if (risultati.isEmpty()) {
            System.out.println("Nessun risultato trovato.");
        } else {
            risultati.forEach(System.out::println);
        }
    }

    private static void ricercaPrestitiPerTessera() {
        System.out.println("Inserisci numero tessera:");
        int numero = leggiIntero(1, Integer.MAX_VALUE);

        List<Prestito> prestiti = archivioDao.ricercaPrestitiPerTessera(numero);
        if (prestiti.isEmpty()) {
            System.out.println("Nessun prestito trovato per il numero tessera inserito.");
        } else {
            prestiti.forEach(System.out::println);
        }
    }

    private static void ricercaPrestitiScaduti() {
        List<Prestito> scaduti = archivioDao.ricercaPrestitiScaduti();
        if (scaduti.isEmpty()) {
            System.out.println("Nessun prestito scaduto trovato.");
        } else {
            scaduti.forEach(System.out::println);
        }
    }
}
