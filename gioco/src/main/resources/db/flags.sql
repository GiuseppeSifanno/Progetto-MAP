-- Migrazione idempotente per database già esistenti.
-- I flag sono oggetti tecnici usati dalle interazioni delle zone.
MERGE INTO PUBLIC.Oggetto (id_oggetto, nome, descrizione, image_name, combinabile) KEY (id_oggetto) VALUES
('f1', 'Flag fuoco acceso', 'Stato tecnico: il fuoco sulla spiaggia è stato acceso.', NULL, FALSE),
('f2', 'Flag combattente pronto', 'Stato tecnico: il Combattente ha recuperato le energie.', NULL, FALSE),
('f3', 'Flag tunnel illuminato', 'Stato tecnico: la torcia della miniera è stata accesa.', NULL, FALSE),
('f4', 'Flag montacarichi riparato', 'Stato tecnico: il minigioco del montacarichi è stato completato.', NULL, FALSE),
('f5', 'Flag liane rimosse', 'Stato tecnico: il minigioco delle liane è stato completato.', NULL, FALSE),
('f6', 'Flag montacarichi utilizzato', 'Stato tecnico: la ciurma ha avviato il montacarichi.', NULL, FALSE),
('f7', 'Flag tesoro raggiunto', 'Stato tecnico: la ciurma ha raggiunto il tesoro.', NULL, FALSE),
('f8', 'Flag masso spostato', 'Stato tecnico: il passaggio verso la giungla è stato liberato.', NULL, FALSE),
('f9', 'Flag cespuglio raccolto', 'Stato tecnico: le foglie secche sono già state raccolte dal cespuglio.', NULL, FALSE),
('f10', 'Flag navigatrice consultata', 'Stato tecnico: la Navigatrice ha già consegnato lente e bastone.', NULL, FALSE),
('f11', 'Flag frutti raccolti', 'Stato tecnico: i frutti sono già stati fatti cadere dall''albero.', NULL, FALSE),
('f12', 'Flag legnetti tentati', 'Stato tecnico: hai provato a raccogliere i legnetti', NULL, FALSE);