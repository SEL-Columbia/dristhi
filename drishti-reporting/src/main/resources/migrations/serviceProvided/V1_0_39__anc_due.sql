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
-- Name: anc_due; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE anc_due (
    id integer NOT NULL,
    entityid character varying(200) NOT NULL,
    patientnum character varying(200),
    anmnum character varying(200),
    visittype character varying(200),
    visitno integer,
    lmpdate character varying(200),
    womenname character varying(200),
    visitdate character varying(200),
    anmid character varying(200)
);


ALTER TABLE anc_due OWNER TO dhanush;

--
-- Name: anc_due_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE anc_due_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE anc_due_id_seq OWNER TO dhanush;

--
-- Name: anc_due_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE anc_due_id_seq OWNED BY anc_due.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY anc_due ALTER COLUMN id SET DEFAULT nextval('anc_due_id_seq'::regclass);


--
-- Name: anc_due_id_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY anc_due
    ADD CONSTRAINT anc_due_id_key UNIQUE (id);


--
-- Name: anc_due_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY anc_due
    ADD CONSTRAINT anc_due_pkey PRIMARY KEY (entityid);


--
-- PostgreSQL database dump complete
--

