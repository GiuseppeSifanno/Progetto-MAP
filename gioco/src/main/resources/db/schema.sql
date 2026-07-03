CREATE TABLE Atto (
    id_atto VARCHAR(2) PRIMARY KEY,
    dialogo_iniziale VARCHAR(2)
);

CREATE TABLE Dialogo (
    id_dialogo VARCHAR(2) PRIMARY KEY,
    id_atto VARCHAR(2) NOT NULL,
    testo TEXT NOT NULL,
    next_id VARCHAR(2),

    FOREIGN KEY (id_atto)
        REFERENCES Atto(id_atto),

    FOREIGN KEY (next_id)
        REFERENCES Dialogo(id_dialogo)
);

CREATE TABLE Scelta (
    id_scelta VARCHAR(2) PRIMARY KEY,
    id_dialogo VARCHAR(2) NOT NULL,
    testo TEXT NOT NULL,
    next_id VARCHAR(2),

    FOREIGN KEY (id_dialogo)
        REFERENCES Dialogo(id_dialogo),

    FOREIGN KEY (next_id)
        REFERENCES Dialogo(id_dialogo)
);

ALTER TABLE Atto
    ADD CONSTRAINT fk_dialogo_iniziale
        FOREIGN KEY (dialogo_iniziale)
            REFERENCES Dialogo(id_dialogo);