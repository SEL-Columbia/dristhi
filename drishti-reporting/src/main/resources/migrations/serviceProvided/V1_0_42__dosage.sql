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
-- Name: dosage; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE dosage (
    id integer NOT NULL,
    dosage character varying(100) NOT NULL,
    active boolean NOT NULL
);


ALTER TABLE dosage OWNER TO dhanush;

--
-- Name: dosage_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE dosage_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dosage_id_seq OWNER TO dhanush;

--
-- Name: dosage_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE dosage_id_seq OWNED BY dosage.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY dosage ALTER COLUMN id SET DEFAULT nextval('dosage_id_seq'::regclass);


--
-- Name: dosage_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY dosage
    ADD CONSTRAINT dosage_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

