-- ============================================================
-- OGGETTI
-- ============================================================
CREATE TABLE IF NOT EXISTS PUBLIC.Oggetto (
    id_oggetto VARCHAR(3) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione TEXT,
    image_name VARCHAR(255), -- Nome del file dell'immagine
    combinabile BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS PUBLIC.Materiale (
    id_materiale VARCHAR(3) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione TEXT,
    image_name VARCHAR(255)
);

-- ============================================================
-- RICETTE (crafting)
-- ============================================================
CREATE TABLE IF NOT EXISTS PUBLIC.Ricetta (
    id_ricetta VARCHAR(3) PRIMARY KEY,
    id_ingrediente1 VARCHAR(3) NOT NULL,
    id_ingrediente2 VARCHAR(3) NOT NULL,
    id_risultato VARCHAR(3) NOT NULL,

    FOREIGN KEY (id_ingrediente1) REFERENCES PUBLIC.Oggetto(id_oggetto),
    FOREIGN KEY (id_ingrediente2) REFERENCES PUBLIC.Oggetto(id_oggetto),
    FOREIGN KEY (id_risultato) REFERENCES PUBLIC.Oggetto(id_oggetto)
);

-- ============================================================
-- PUZZLE
-- ============================================================
CREATE TABLE IF NOT EXISTS PUBLIC.Puzzle (
    id_puzzle VARCHAR(3) PRIMARY KEY,
    soluzione VARCHAR(255) NOT NULL
);

-- ============================================================
-- SALVATAGGI
-- ============================================================
CREATE TABLE IF NOT EXISTS PUBLIC.Salvataggio (
    id_slot INT PRIMARY KEY,
    id_atto_corrente VARCHAR(3) NOT NULL,
    id_dialogo_corrente VARCHAR(3),
    data_salvataggio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS PUBLIC.SalvataggioQuestPassiCompletati (
    id_slot INT NOT NULL,
    id_quest VARCHAR(3) NOT NULLì,
    id_passo VARCHAR(3) NOT NULL,

    PRIMARY KEY (id_slot, id_quest, id_passo),
    FOREIGN KEY (id_slot) REFERENCES PUBLIC.Salvataggio(ID_SLOT) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.SalvataggioInventarioOggetto (
    id_slot INT NOT NULL,
    id_oggetto VARCHAR(3) NOT NULL,

    PRIMARY KEY (id_slot, id_oggetto),
    FOREIGN KEY (id_slot) REFERENCES PUBLIC.Salvataggio(id_slot) ON DELETE CASCADE,
    FOREIGN KEY (id_oggetto) REFERENCES PUBLIC.Oggetto(id_oggetto)
);

CREATE TABLE IF NOT EXISTS PUBLIC.SalvataggioInventarioMateriale (
    id_slot INT NOT NULL,
    id_materiale VARCHAR(3) NOT NULL,
    quantita INT NOT NULL DEFAULT 1,

    PRIMARY KEY (id_slot, id_materiale),
    FOREIGN KEY (id_slot) REFERENCES PUBLIC.Salvataggio(id_slot) ON DELETE CASCADE,
    FOREIGN KEY (id_materiale) REFERENCES PUBLIC.Materiale(id_materiale)
);

CREATE TABLE IF NOT EXISTS PUBLIC.SalvataggioPuzzleRisolti (
    id_slot INT NOT NULL,
    id_puzzle VARCHAR(3) NOT NULL,

    PRIMARY KEY (id_slot, id_puzzle),
    FOREIGN KEY (id_slot) REFERENCES PUBLIC.Salvataggio(id_slot) ON DELETE CASCADE,
    FOREIGN KEY (id_puzzle) REFERENCES PUBLIC.Puzzle(id_puzzle)
);

CREATE TABLE IF NOT EXISTS PUBLIC.SalvataggioScelteEffettuate (
    id_slot INT NOT NULL,
    id_scelta VARCHAR(3) NOT NULL,
    id_dialogo VARCHAR(3) NOT NULL,
    ordine INT NOT NULL, -- per mantenere l'ordine cronologico delle scelte

    PRIMARY KEY (id_slot, ordine),
    FOREIGN KEY (id_slot) REFERENCES PUBLIC.Salvataggio(id_slot) ON DELETE CASCADE
);