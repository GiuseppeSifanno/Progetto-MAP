INSERT INTO PUBLIC.OGGETTO (ID_OGGETTO, NOME, DESCRIZIONE, IMAGE_NAME, COMBINABILE)
    VALUES ('o1', 'Spada', 'Spada di fuoco', 'spada.png', false),
           ('o2', 'Scudo', 'Scudo di fuoco', 'scudo.png', false),
           ('o3', 'Ciotola', 'Ciotola con zuppa dentro', 'ciotola.png', true),
           ('o4', 'Zuppa', 'Zuppa semplice', 'zuppa.png', true),
           ('o5', 'Ciotola di zuppa', 'Ciotola con zuppa', 'ciotola_zuppa.png', false);

INSERT INTO PUBLIC.RICETTA (ID_RICETTA, ID_INGREDIENTE1, ID_INGREDIENTE2, ID_RISULTATO)
    VALUES ('r1', 'o4', 'o3', 'o5');

commit;