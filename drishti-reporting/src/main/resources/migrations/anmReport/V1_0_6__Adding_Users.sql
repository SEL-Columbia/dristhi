CREATE TABLE anm_report.users (username VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL, enabled BOOLEAN NOT NULL,
  CONSTRAINT pk_users PRIMARY KEY (username));

CREATE TABLE authorities (username VARCHAR(50) NOT NULL, authority VARCHAR(50) NOT NULL,
  CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES anm_report.users (username) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION);

INSERT INTO anm_report.users (username, password, enabled) VALUES ('c', '1', TRUE);
INSERT INTO anm_report.users (username, password, enabled) VALUES ('demo1', '1', TRUE);
INSERT INTO anm_report.users (username, password, enabled) VALUES ('demo2', '2', TRUE);
INSERT INTO anm_report.users (username, password, enabled) VALUES ('demo3', '3', TRUE);
INSERT INTO anm_report.authorities (username, authority) VALUES ('c', 'ROLE_USER');
INSERT INTO anm_report.authorities (username, authority) VALUES ('demo1', 'ROLE_USER');
INSERT INTO anm_report.authorities (username, authority) VALUES ('demo2', 'ROLE_USER');
INSERT INTO anm_report.authorities (username, authority) VALUES ('demo3', 'ROLE_USER');