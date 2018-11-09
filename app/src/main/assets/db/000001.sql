CREATE TABLE UserBook(uri TEXT PRIMARY KEY NOT NULL, offset REAL NOT NULL, percent REAL NOT NULL, lastReadTime DATETIME NOT NULL);
CREATE INDEX UserBook_lastReadTime ON UserBook(lastReadTime);

CREATE TABLE Setting(key TEXT PRIMARY KEY NOT NULL, intValue INTEGER, realValue REAL, textValue TEXT);