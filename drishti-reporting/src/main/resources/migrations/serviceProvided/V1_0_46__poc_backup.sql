--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = report, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: poc_backup; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE poc_backup (
    id integer NOT NULL,
    visitentityid character varying(100),
    entityidec character varying(100),
    anmid character varying(100),
    level character varying(35),
    clientversion character varying(35),
    serverversion character varying(35),
    visittype character varying(35),
    phc character varying(100),
    docid character(100),
    poc text
);


ALTER TABLE poc_backup OWNER TO dhanush;

--
-- Name: poc_backup_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE poc_backup_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE poc_backup_id_seq OWNER TO dhanush;

--
-- Name: poc_backup_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE poc_backup_id_seq OWNED BY poc_backup.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY poc_backup ALTER COLUMN id SET DEFAULT nextval('poc_backup_id_seq'::regclass);


--
-- Name: poc_backup_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY poc_backup
    ADD CONSTRAINT poc_backup_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

