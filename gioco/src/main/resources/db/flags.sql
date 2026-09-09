-- Migrazione idempotente per database già esistenti.
-- I flag sono oggetti tecnici usati dalle interazioni delle zone.
MERGE INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) KEY (id_oggetto) VALUES
('o10', 'Flag fuoco acceso', 'Stato tecnico: il fuoco sulla spiaggia è stato acceso.', NULL, FALSE),
('o11', 'Flag combattente pronto', 'Stato tecnico: il Combattente ha recuperato le energie.', NULL, FALSE),
('o14', 'Flag montacarichi riparato', 'Stato tecnico: il minigioco del montacarichi è stato completato.', NULL, FALSE),
('o15', 'Flag liane rimosse', 'Stato tecnico: il minigioco delle liane è stato completato.', NULL, FALSE),
('o16', 'Flag montacarichi utilizzato', 'Stato tecnico: la ciurma ha avviato il montacarichi.', NULL, FALSE),
('o17', 'Flag tesoro raggiunto', 'Stato tecnico: la ciurma ha raggiunto il tesoro.', NULL, FALSE),
('o18', 'Flag masso spostato', 'Stato tecnico: il passaggio verso la giungla è stato liberato.', NULL, FALSE),
('o22', 'Flag cespuglio raccolto', 'Stato tecnico: le foglie secche sono già state raccolte dal cespuglio.', NULL, FALSE),
('o23', 'Flag navigatrice consultata', 'Stato tecnico: la Navigatrice ha già consegnato lente e bastone.', NULL, FALSE),
('o24', 'Flag frutti raccolti', 'Stato tecnico: i frutti sono già stati fatti cadere dall''albero.', NULL, FALSE);
