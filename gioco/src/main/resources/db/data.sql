-- Oggetti introdotti in Atto 1 (spiaggia)
INSERT INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
('o1', 'Lente',   'La lente del cannocchiale rotto della Navigatrice.', 'lente.png', FALSE),
('o2', 'Bastone', 'Il bastone da passeggio/rilevamento della Navigatrice.', 'bastone.png', FALSE),
('o3', 'Foglie',  'Foglie secche raccolte da un cespuglio ai margini della giungla.', 'foglie.png', FALSE),
('o4', 'Frutti',  'Un cesto di frutti maturi fatti cadere da un albero.', 'frutti.png', FALSE);

-- Oggetti introdotti in Atto 2 (giungla)
-- 'bastone' (o2) già inserito in spiaggia.json, non re-inserirlo
INSERT INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
('o5', 'Pergamena', 'La pergamena del Capitano Schettino trovata nella borsa di cuoio, rivela l''esistenza del Tesoro degli Abissi.', 'pergamena.png', FALSE),
('o12', 'Zuppa', 'Una zuppa preparata con i frutti raccolti, pronta per essere offerta ai Foglianti.', 'zuppa.png', FALSE),
('o19', 'Tazza da Tè', 'Tazza da tè del Capitano', 'tazza_da_te.png', FALSE);
-- Oggetti introdotti in Atto 3 (miniera)
-- 'bastone' (o2) già inserito in spiaggia.json, non re-inserirlo
INSERT INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
('o6', 'Calzino',          'Un calzino del Capitano, usato come esca per la fiamma della torcia.', 'calzino.png', FALSE),
('o7', 'Pietra focaia',    'Una pietra focaia trovata tra i sassi della miniera.', 'pietra_focaia.png', FALSE),
('o8', 'Bastone spezzato', 'Il bastone della Navigatrice, spezzato in due dal Combattente.', 'bastone_spezzato.png', FALSE),
('o9', 'Fili d''acciaio',   'Fili d''acciaio di ricambio recuperati dai vecchi macchinari della miniera.', 'fili_acciaio.png', FALSE),
('o27', 'Cappello da Capitano',   'Il glorioso cappello del Capitano. Inspira fiducia, inspiegabilmente.', 'cappello_capitano.png', FALSE),
('o28', 'Mappa',   'Una mappa sgualcita dell''isola.', 'mappa.png', FALSE);

-- ============================================================
-- Erbe/radici raccoglibili nel minigioco della zuppa (Atto 2)
-- ============================================================
MERGE INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) KEY (id_oggetto) VALUES
('o20', 'Fiori Gialli',      'Un piccolo fiore giallo, commestibile.', 'Erba.png', TRUE),
('o21', 'Fiori Viola',       'Un fiore viola dal profumo dolce, commestibile.', 'Erba.png', TRUE),
('o22', 'Fiori Azzurri',     'Un fiore azzurro, commestibile.', 'Erba.png', TRUE),
('o23', 'Bacche Rosse',      'Un piccolo grappolo di bacche rosse, commestibili.', 'Erba.png', TRUE),
('o24', 'Funghi Chiazzati',  'Funghi dalle chiazze sospette: velenosi.', 'Erba.png', FALSE),
('o25', 'Radice Contorta',   'Una radice contorta dall''odore acre: velenosa.', 'Radici.png', FALSE),
('o26', 'Radice Nodosa',     'Una radice nodosa dal colore scuro: velenosa.', 'Radici.png', FALSE);

-- ============================================================
-- Ricetta: le 4 erbe/radici buone (o20,o21,o22,o23) -> zuppa (o12)
-- ============================================================
MERGE INTO PUBLIC.Ricetta (id_ricetta, id_risultato) KEY (id_ricetta) VALUES
('r01', 'o12');
DELETE FROM PUBLIC.Ricetta_Ingrediente WHERE id_ricetta = 'r01';
INSERT INTO PUBLIC.Ricetta_Ingrediente (id_ricetta, id_ingrediente) VALUES
('r01', 'o20'),
('r01', 'o21'),
('r01', 'o22'),
('r01', 'o23');

MERGE INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) KEY (id_oggetto) VALUES
('o13', 'Torcia', 'Una torcia accesa, pronta per illuminare il tunnel della miniera.', 'torcia.png', FALSE);

-- ============================================================
-- Ricetta: calzino (o6) + pietra focaia (o7) + bastone spezzato (o8) -> torcia (o13)
-- ============================================================
MERGE INTO PUBLIC.Ricetta (id_ricetta, id_risultato) KEY (id_ricetta) VALUES
('r02', 'o13');

DELETE FROM PUBLIC.Ricetta_Ingrediente WHERE id_ricetta = 'r02';
INSERT INTO PUBLIC.Ricetta_Ingrediente (id_ricetta, id_ingrediente) VALUES
('r02', 'o6'),
('r02', 'o7'),
('r02', 'o8');

commit;