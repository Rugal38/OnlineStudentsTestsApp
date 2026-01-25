-- ============================================================
-- Database: gestion_tests (CLEAN FINAL VERSION)
-- Matiere: JEE / SQL / NodeJS / MongoDB
-- Theme: figé (table theme)
-- Question: matiere_id + theme_id + test_id + type
-- Reponse: linked to question_id
-- + Global settings (parametre_global + test_settings)
-- ============================================================

DROP DATABASE IF EXISTS gestion_tests;
CREATE DATABASE gestion_tests CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_tests;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS admin_user;
DROP TABLE IF EXISTS resultat;
DROP TABLE IF EXISTS reponse;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS candidat;
DROP TABLE IF EXISTS creneau;
DROP TABLE IF EXISTS test_matiere;
DROP TABLE IF EXISTS theme;
DROP TABLE IF EXISTS matiere;
DROP TABLE IF EXISTS test;
DROP TABLE IF EXISTS test_settings;
DROP TABLE IF EXISTS parametre_global;

SET FOREIGN_KEY_CHECKS=1;

-- =========================
-- TABLE: test
-- =========================
CREATE TABLE test (
  id INT NOT NULL AUTO_INCREMENT,
  titre VARCHAR(100) DEFAULT NULL,
  duree INT NOT NULL DEFAULT 20, -- en minutes
  date_test DATE DEFAULT NULL,
  nb_questions INT NOT NULL DEFAULT 10,
  shuffle_questions TINYINT(1) NOT NULL DEFAULT 1,
  shuffle_reponses TINYINT(1) NOT NULL DEFAULT 1,
  score_par_question INT NOT NULL DEFAULT 1,
  seuil_reussite INT NOT NULL DEFAULT 50,   -- (%)
  afficher_resultat_fin TINYINT(1) NOT NULL DEFAULT 1,
  afficher_correction TINYINT(1) NOT NULL DEFAULT 0,
  max_tentatives INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: matiere
-- =========================
CREATE TABLE matiere (
  id INT NOT NULL AUTO_INCREMENT,
  nom VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_matiere_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: theme (figé)
-- =========================
CREATE TABLE theme (
  id INT NOT NULL AUTO_INCREMENT,
  nom VARCHAR(120) NOT NULL,
  matiere_id INT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_theme_matiere (matiere_id),
  CONSTRAINT fk_theme_matiere
    FOREIGN KEY (matiere_id) REFERENCES matiere(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: test_matiere (many-to-many)
-- =========================
CREATE TABLE test_matiere (
  test_id INT NOT NULL,
  matiere_id INT NOT NULL,
  PRIMARY KEY (test_id, matiere_id),
  CONSTRAINT fk_tm_test
    FOREIGN KEY (test_id) REFERENCES test(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_tm_matiere
    FOREIGN KEY (matiere_id) REFERENCES matiere(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: creneau
-- =========================
CREATE TABLE creneau (
  id INT NOT NULL AUTO_INCREMENT,
  date_exam DATE NOT NULL,
  heure_debut TIME NOT NULL,
  heure_fin TIME NOT NULL,
  disponible TINYINT(1) DEFAULT 1,
  test_id INT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_creneau_test (test_id),
  CONSTRAINT fk_creneau_test
    FOREIGN KEY (test_id) REFERENCES test(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: candidat
-- =========================
CREATE TABLE candidat (
  id INT NOT NULL AUTO_INCREMENT,
  nom VARCHAR(50) NOT NULL,
  prenom VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  gsm VARCHAR(20) DEFAULT NULL,
  ecole VARCHAR(100) DEFAULT NULL,
  filiere VARCHAR(100) DEFAULT NULL,
  code_session VARCHAR(20) DEFAULT NULL,
  creneau_id INT DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_candidat_email (email),
  KEY idx_candidat_creneau (creneau_id),
  CONSTRAINT fk_candidat_creneau
    FOREIGN KEY (creneau_id) REFERENCES creneau(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: question
-- NOTE: theme (texte libre) optional pour compatibilité
-- mais le lien officiel est theme_id
-- =========================
CREATE TABLE question (
  id INT NOT NULL AUTO_INCREMENT,
  libelle TEXT NOT NULL,
  theme VARCHAR(50) DEFAULT NULL,
  type VARCHAR(20) NOT NULL,          -- 'single' or 'multiple'
  test_id INT DEFAULT NULL,
  matiere_id INT DEFAULT NULL,
  theme_id INT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_question_test (test_id),
  KEY idx_question_matiere (matiere_id),
  KEY idx_question_theme (theme_id),
  CONSTRAINT fk_question_test
    FOREIGN KEY (test_id) REFERENCES test(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT fk_question_matiere
    FOREIGN KEY (matiere_id) REFERENCES matiere(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT fk_question_theme
    FOREIGN KEY (theme_id) REFERENCES theme(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: reponse
-- =========================
CREATE TABLE reponse (
  id INT NOT NULL AUTO_INCREMENT,
  libelle TEXT NOT NULL,
  correcte TINYINT(1) DEFAULT 0,
  question_id INT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_reponse_question (question_id),
  CONSTRAINT fk_reponse_question
    FOREIGN KEY (question_id) REFERENCES question(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: resultat
-- =========================
CREATE TABLE resultat (
  id INT NOT NULL AUTO_INCREMENT,
  score INT DEFAULT NULL,
  date_passage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  candidat_id INT DEFAULT NULL,
  test_id INT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_resultat_candidat (candidat_id),
  KEY idx_resultat_test (test_id),
  CONSTRAINT fk_resultat_candidat
    FOREIGN KEY (candidat_id) REFERENCES candidat(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT fk_resultat_test
    FOREIGN KEY (test_id) REFERENCES test(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- TABLE: admin_user
-- =========================
CREATE TABLE admin_user (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Admin par défaut: admin / admin123 (SHA-256)
INSERT INTO admin_user(username, password_hash)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- ============================================================
-- INSERT DATA
-- ============================================================

-- Matieres (IDs fixés)
INSERT INTO matiere (id, nom) VALUES
(1,'JEE'),
(2,'SQL'),
(3,'NodeJS'),
(4,'MongoDB');

-- Themes figés
-- JEE (matiere_id = 1)
INSERT INTO theme(nom, matiere_id) VALUES
('Servlets & JSP', 1),
('JSF', 1),
('JPA / Hibernate', 1),
('CDI / Beans', 1),
('EJB', 1),
('JDBC', 1),
('WildFly / Deployment', 1),
('Security / Auth', 1);

-- SQL (matiere_id = 2)
INSERT INTO theme(nom, matiere_id) VALUES
('SELECT / WHERE', 2),
('JOIN', 2),
('GROUP BY / HAVING', 2),
('Subqueries', 2),
('Constraints (PK/FK/UNIQUE)', 2),
('Normalisation', 2),
('Transactions', 2),
('Index', 2);

-- NodeJS (matiere_id = 3)
INSERT INTO theme(nom, matiere_id) VALUES
('Basics (modules, npm)', 3),
('Express.js', 3),
('Routing', 3),
('Middleware', 3),
('Async / Promises / Async-Await', 3),
('REST API', 3),
('JWT Auth', 3),
('JWT Auth (advanced)', 3),
('File Upload / Multer', 3);

-- MongoDB (matiere_id = 4)
INSERT INTO theme(nom, matiere_id) VALUES
('CRUD', 4),
('Query operators', 4),
('Aggregation Pipeline', 4),
('Embed vs Reference', 4),
('Indexes', 4),
('Validation Schema', 4),
('Mongoose', 4),
('Transactions', 4);

-- Tests
INSERT INTO test (id, titre, duree, date_test,
                  nb_questions, shuffle_questions, shuffle_reponses, score_par_question,
                  seuil_reussite, afficher_resultat_fin, afficher_correction, max_tentatives) VALUES
(1,'Test Général (JEE + SQL + NodeJS + MongoDB)',20,CURDATE(), 10, 1, 1, 1, 50, 1, 0, 1),
(2,'Test JEE',20,CURDATE(), 10, 1, 1, 1, 50, 1, 0, 1),
(3,'Test SQL',20,CURDATE(), 10, 1, 1, 1, 50, 1, 0, 1),
(4,'Test NodeJS',20,CURDATE(), 10, 1, 1, 1, 50, 1, 0, 1),
(5,'Test MongoDB',20,CURDATE(), 10, 1, 1, 1, 50, 1, 0, 1);

-- Link tests <-> matieres
INSERT INTO test_matiere (test_id, matiere_id) VALUES
(1,1),(1,2),(1,3),(1,4),
(2,1),
(3,2),
(4,3),
(5,4);

-- Créneaux (exemple)
INSERT INTO creneau (date_exam, heure_debut, heure_fin, disponible, test_id) VALUES
(CURDATE(),'09:00:00','10:00:00',1,1),
(CURDATE(),'11:00:00','12:00:00',1,1);

-- ============================================================
-- Questions + Réponses (jeu de données de test)
-- ============================================================

-- Q1 (JEE / JSF) - choix unique
INSERT INTO question(libelle, theme, type, test_id, matiere_id, theme_id)
VALUES (
 'JSF : Que sont les Facelets ?',
 'JSF',
 'single',
 2,
 1,
 (SELECT id FROM theme WHERE nom='JSF' AND matiere_id=1 LIMIT 1)
);
SET @Q1 = LAST_INSERT_ID();

INSERT INTO reponse(libelle, correcte, question_id) VALUES
('Le moteur de templates de JSF pour construire les vues', 1, @Q1),
('Une base de données', 0, @Q1),
('Un serveur Web', 0, @Q1);

-- Q2 (SQL / JOIN) - choix unique
INSERT INTO question(libelle, theme, type, test_id, matiere_id, theme_id)
VALUES (
 'SQL : Pourquoi utilise-t-on JOIN ?',
 'JOIN',
 'single',
 3,
 2,
 (SELECT id FROM theme WHERE nom='JOIN' AND matiere_id=2 LIMIT 1)
);
SET @Q2 = LAST_INSERT_ID();

INSERT INTO reponse(libelle, correcte, question_id) VALUES
('Pour combiner des données de deux tables selon une condition', 1, @Q2),
('Pour supprimer une table', 0, @Q2);

-- Q3 (NodeJS / REST API) - choix multiple
INSERT INTO question(libelle, theme, type, test_id, matiere_id, theme_id)
VALUES (
 'NodeJS : Quelles sont les caractéristiques d’une API REST ?',
 'REST API',
 'multiple',
 4,
 3,
 (SELECT id FROM theme WHERE nom='REST API' AND matiere_id=3 LIMIT 1)
);
SET @Q3 = LAST_INSERT_ID();

INSERT INTO reponse(libelle, correcte, question_id) VALUES
('Utilise les méthodes HTTP (GET/POST/PUT/DELETE)', 1, @Q3),
('Utilise souvent le format JSON', 1, @Q3),
('Utilise uniquement FTP', 0, @Q3);

-- Q4 (MongoDB / CRUD) - choix unique
INSERT INTO question(libelle, theme, type, test_id, matiere_id, theme_id)
VALUES (
 'MongoDB : Que signifie CREATE dans CRUD ?',
 'CRUD',
 'single',
 5,
 4,
 (SELECT id FROM theme WHERE nom='CRUD' AND matiere_id=4 LIMIT 1)
);
SET @Q4 = LAST_INSERT_ID();

INSERT INTO reponse(libelle, correcte, question_id) VALUES
('insertOne / insertMany', 1, @Q4),
('find', 0, @Q4),
('deleteOne', 0, @Q4);

SELECT 'OK - script de données de test exécuté' AS status;


-- ============================================================
-- PARAMETRES GLOBAUX (une seule table, une seule ligne)
-- ============================================================
DROP TABLE IF EXISTS parametre_global;

CREATE TABLE parametre_global (
  id INT PRIMARY KEY CHECK (id = 1),
  nb_questions_default INT NOT NULL DEFAULT 10,
  temps_question_minutes INT NOT NULL DEFAULT 2
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO parametre_global (id, nb_questions_default, temps_question_minutes)
VALUES (1, 10, 2);
