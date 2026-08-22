-- Oggetti introdotti in Atto 1 (spiaggia)
INSERT INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
('o1', 'Lente',   'La lente del cannocchiale rotto della Navigatrice.', 'lente.png', FALSE),
('o2', 'Bastone', 'Il bastone da passeggio/rilevamento della Navigatrice.', 'bastone.png', FALSE),
('o3', 'Foglie',  'Foglie secche raccolte da un cespuglio ai margini della giungla.', 'foglie.png', FALSE),
('o4', 'Frutti',  'Un cesto di frutti maturi fatti cadere da un albero.', 'frutti.png', FALSE);

-- Oggetti introdotti in Atto 2 (giungla)
-- 'bastone' (o2) già inserito in spiaggia.json, non re-inserirlo
INSERT INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
('o5', 'Pergamena', 'La pergamena del Capitano Schettino trovata nella borsa di cuoio, rivela l''esistenza del Tesoro degli Abissi.', 'pergamena.png', FALSE);

-- Oggetti introdotti in Atto 3 (miniera)
-- 'bastone' (o2) già inserito in spiaggia.json, non re-inserirlo
INSERT INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) VALUES
('o6', 'Calzino',          'Un calzino del Capitano, usato come esca per la fiamma della torcia.', 'calzino.png', FALSE),
('o7', 'Pietra focaia',    'Una pietra focaia trovata tra i sassi della miniera.', 'pietra_focaia.png', FALSE),
('o8', 'Bastone spezzato', 'Il bastone della Navigatrice, spezzato in due dal Combattente.', 'bastone_spezzato.png', FALSE),
('o9', 'Fili d''acciaio',   'Fili d''acciaio di ricambio recuperati dai vecchi macchinari della miniera.', 'fili_acciaio.png', FALSE);


-- Flag tecnici usati dalle interazioni.
-- Sono oggetti di stato e non rappresentano oggetti fisici del gameplay.
MERGE INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) KEY (id_oggetto) VALUES
('o10', 'Flag fuoco acceso', 'Stato tecnico: il fuoco sulla spiaggia è stato acceso.', NULL, FALSE),
('o11', 'Flag combattente pronto', 'Stato tecnico: il Combattente ha recuperato le energie.', NULL, FALSE),
('o12', 'Flag zuppa pronta', 'Stato tecnico: il minigioco della zuppa è stato completato.', NULL, FALSE),
('o13', 'Flag tunnel illuminato', 'Stato tecnico: la torcia della miniera è stata accesa.', NULL, FALSE),
('o14', 'Flag montacarichi riparato', 'Stato tecnico: il minigioco del montacarichi è stato completato.', NULL, FALSE),
('o15', 'Flag liane rimosse', 'Stato tecnico: il minigioco delle liane è stato completato.', NULL, FALSE),
('o16', 'Flag montacarichi utilizzato', 'Stato tecnico: la ciurma ha avviato il montacarichi.', NULL, FALSE),
('o17', 'Flag tesoro raggiunto', 'Stato tecnico: la ciurma ha raggiunto il tesoro.', NULL, FALSE),
('o18', 'Flag masso spostato', 'Stato tecnico: il passaggio verso la giungla è stato liberato.', NULL, FALSE);

commit;