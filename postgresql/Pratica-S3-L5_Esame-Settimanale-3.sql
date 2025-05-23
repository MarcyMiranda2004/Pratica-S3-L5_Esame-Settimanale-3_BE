/*
/* 1. Visualizza tutti gli elementi del catalogo in modo generico */
SELECT * FROM elementocatalogo;


/* 2. Visualizza tutti i libri con dettagli del catalogo */
SELECT 
    l.isbn,
    ec.titolo,
    ec.anno_pubblicazione,
    ec.numero_pagine,
    l.autore,
    l.genere
FROM libro l
JOIN elementocatalogo ec ON l.isbn = ec.isbn;


/* 3. Visualizza tutte le riviste con dettagli del catalogo */
SELECT 
    r.isbn,
    ec.titolo,
    ec.anno_pubblicazione,
    ec.numero_pagine,
    r.periodicita
FROM rivista r
JOIN elementocatalogo ec ON r.isbn = ec.isbn;


/* 4. Visualizza tutti i libri (senza dettagli catalogo) */
SELECT * FROM libro;


/* 5. Visualizza tutte le riviste (senza dettagli catalogo) */
SELECT * FROM rivista; */

select *
from prestiti





