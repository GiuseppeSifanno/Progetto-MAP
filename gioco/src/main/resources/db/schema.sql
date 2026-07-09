-- ============================================================
-- OGGETTI
-- ============================================================
CREATE TABLE IF NOT EXISTS Oggetto (
    id_oggetto VARCHAR(10) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione TEXT,
    image_name VARCHAR(255), -- Nome del file dell'immagine
    combinabile BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS Materiale (
    id_materiale VARCHAR(10) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione TEXT,
    image_name VARCHAR(255)
);

-- ============================================================
-- RICETTE (crafting)
-- ============================================================
CREATE TABLE IF NOT EXISTS Ricetta (
    id_ricetta VARCHAR(10) PRIMARY KEY,
    id_ingrediente1 VARCHAR(10) NOT NULL,
    id_ingrediente2 VARCHAR(10) NOT NULL,
    id_risultato VARCHAR(10) NOT NULL,

    FOREIGN KEY (id_ingrediente1) REFERENCES Oggetto(id_oggetto),
    FOREIGN KEY (id_ingrediente2) REFERENCES Oggetto(id_oggetto),
    FOREIGN KEY (id_risultato) REFERENCES Oggetto(id_oggetto)
);

-- ============================================================
-- PUZZLE
-- ============================================================
CREATE TABLE IF NOT EXISTS Puzzle (
    id_puzzle VARCHAR(10) PRIMARY KEY,
    soluzione VARCHAR(255) NOT NULL
);

-- ============================================================
-- SALVATAGGI
-- ============================================================
CREATE TABLE IF NOT EXISTS Salvataggio (
    id_slot INT PRIMARY KEY,
    id_atto_corrente VARCHAR(10) NOT NULL,
    data_salvataggio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS SalvataggioInventarioOggetto (
    id_slot INT NOT NULL,
    id_oggetto VARCHAR(10) NOT NULL,

    PRIMARY KEY (id_slot, id_oggetto),
    FOREIGN KEY (id_slot) REFERENCES Salvataggio(id_slot) ON DELETE CASCADE,
    FOREIGN KEY (id_oggetto) REFERENCES Oggetto(id_oggetto)
);

CREATE TABLE IF NOT EXISTS SalvataggioInventarioMateriale (
    id_slot INT NOT NULL,
    id_materiale VARCHAR(10) NOT NULL,
    quantita INT NOT NULL DEFAULT 1,

    PRIMARY KEY (id_slot, id_materiale),
    FOREIGN KEY (id_slot) REFERENCES Salvataggio(id_slot) ON DELETE CASCADE,
    FOREIGN KEY (id_materiale) REFERENCES Materiale(id_materiale)
);

CREATE TABLE IF NOT EXISTS SalvataggioPuzzleRisolti (
    id_slot INT NOT NULL,
    id_puzzle VARCHAR(10) NOT NULL,

    PRIMARY KEY (id_slot, id_puzzle),
    FOREIGN KEY (id_slot) REFERENCES Salvataggio(id_slot) ON DELETE CASCADE,
    FOREIGN KEY (id_puzzle) REFERENCES Puzzle(id_puzzle)
);

CREATE TABLE IF NOT EXISTS SalvataggioScelteEffettuate (
    id_slot INT NOT NULL,
    id_scelta VARCHAR(10) NOT NULL,
    id_dialogo VARCHAR(10) NOT NULL,
    ordine INT NOT NULL, -- per mantenere l'ordine cronologico delle scelte

    PRIMARY KEY (id_slot, ordine),
    FOREIGN KEY (id_slot) REFERENCES Salvataggio(id_slot) ON DELETE CASCADE
);