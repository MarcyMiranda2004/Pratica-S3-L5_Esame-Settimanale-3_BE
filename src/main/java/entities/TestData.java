package entities;

import java.time.LocalDate;

public class TestData {

    public static Utente creaUtente1() {
        return new Utente("Mario", "Rossi", LocalDate.of(1990, 1, 1));
    }

    public static Utente creaUtente2() {
        return new Utente("Giulia", "Verdi", LocalDate.of(1995, 6, 15));
    }

    public static Libro creaLibro1() {
        return new Libro("Il Signore degli Anelli", 1954, 1200, "J.R.R. Tolkien", "Fantasy");
    }

    public static Libro creaLibro2() {
        return new Libro("1984", 1949, 328, "George Orwell", "Distopia");
    }

    public static Rivista creaRivista1() {
        return new Rivista("National Geographic", 2023, 90, enums.Periodicita.MENSILE);
    }

    public static Prestito creaPrestitoConRestituzione(Utente utente, ElementoCatalogo elemento) {
        LocalDate inizioPrestito = LocalDate.now().minusDays(40);
        Prestito prestito = new Prestito(utente, elemento, inizioPrestito);
        prestito.setDataRestituzioneEffettiva(LocalDate.now().minusDays(5));
        return prestito;
    }

    public static Prestito creaPrestitoAperto(Utente utente, ElementoCatalogo elemento) {
        LocalDate inizioPrestito = LocalDate.now().minusDays(40);
        return new Prestito(utente, elemento, inizioPrestito);
    }



}
