package entities;

import enums.Periodicita;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "riviste")
public class Rivista extends ElementoCatalogo {

    @Enumerated(jakarta.persistence.EnumType.STRING)
    private Periodicita periodicita;

    public Rivista() {}

    public Rivista(String titolo, int annoPubblicazione, int numeroPagine, Periodicita periodicita) {
        super(titolo, annoPubblicazione, numeroPagine);
        this.periodicita = periodicita;
    }

    public Periodicita getPeriodicita() {return periodicita;}
    public void setPeriodicita(Periodicita periodicita) {this.periodicita = periodicita;}

    @Override
    public String toString() {
        return super.toString() + " | Rivista{" +
                "periodicita=" + periodicita +
                '}';
    }
}
