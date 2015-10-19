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
-- Name: icd10; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE icd10 (
    id integer NOT NULL,
    "ICD10_Chapter" character varying(200) NOT NULL,
    "ICD10_Code" character varying(100) NOT NULL,
    "ICD10_Name" character varying(100) NOT NULL,
    can_select boolean NOT NULL,
    status boolean NOT NULL
);


ALTER TABLE icd10 OWNER TO dhanush;

--
-- Name: icd10_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE icd10_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE icd10_id_seq OWNER TO dhanush;

--
-- Name: icd10_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE icd10_id_seq OWNED BY icd10.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY icd10 ALTER COLUMN id SET DEFAULT nextval('icd10_id_seq'::regclass);


--
-- Name: icd10_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY icd10
    ADD CONSTRAINT icd10_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

