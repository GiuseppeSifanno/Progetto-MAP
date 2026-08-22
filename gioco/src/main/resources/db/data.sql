INSERT INTO Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
    ('o1', 'Lente', 'Lente del cannocchiale rotto', 'lente.png', TRUE),
    ('o2', 'Foglie Secche', 'Foglie raccolte dal cespuglio', 'foglie.png', TRUE),
    ('o3', 'Fuoco Acceso', 'Flag: il fuoco è stato acceso', 'fuoco.png', FALSE);

INSERT INTO PUZZLE (ID_PUZZLE, SOLUZIONE) VALUES ( 'p01', 'risposta' );

commit;