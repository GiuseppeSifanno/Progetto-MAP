CREATE TABLE IF NOT EXISTS Atto (
    id_atto VARCHAR(3) PRIMARY KEY,
    dialogo_iniziale VARCHAR(3)
);

CREATE TABLE IF NOT EXISTS  Dialogo (
    id_dialogo VARCHAR(3) PRIMARY KEY,
    id_atto VARCHAR(3) NOT NULL,
    testo TEXT NOT NULL,
    next_id VARCHAR(3),

    FOREIGN KEY (id_atto)
        REFERENCES Atto(id_atto),

    FOREIGN KEY (next_id)
        REFERENCES Dialogo(id_dialogo)
);

CREATE TABLE IF NOT EXISTS Scelta (
    id_scelta VARCHAR(3) PRIMARY KEY,
    id_dialogo VARCHAR(3) NOT NULL,
    testo TEXT NOT NULL,
    next_id VARCHAR(3),

    FOREIGN KEY (id_dialogo)
        REFERENCES Dialogo(id_dialogo),

    FOREIGN KEY (next_id)
        REFERENCES Dialogo(id_dialogo)
);

ALTER TABLE Atto
    ADD CONSTRAINT IF NOT EXISTS fk_dialogo_iniziale
        FOREIGN KEY (dialogo_iniziale)
            REFERENCES Dialogo(id_dialogo);

CREATE TABLE IF NOT EXISTS Oggetto (
    id_oggetto VARCHAR(3) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descrizione TEXT,
    image_name VARCHAR(255) //Nome del file dell'immagine'
);

CREATE TABLE IF NOT EXISTS Puzzle (
    id_puzzle VARCHAR(2) PRIMARY KEY
);