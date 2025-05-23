package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "libri")
public class Libro extends ElementoCatalogo {
    private String autore;
    private String genere;

    public Libro() {}

    public Libro(String titolo, int annoPubblicazione, int numeroPagine, String autore, String genere) {
        super(titolo, annoPubblicazione, numeroPagine);
        this.autore = autore;
        this.genere = genere;
    }


    public String getAutore() {return autore;}
    public void setAutore(String autore) {this.autore = autore;}

    public String getGenere() {return genere;}

    public void setGenere(String genere) {this.genere = genere;}

    @Override
    public String toString() {
        return super.toString() + " | Libro{" +
                "autore='" + autore + '\'' +
                ", genere='" + genere + '\'' +
                '}';
    }
}
