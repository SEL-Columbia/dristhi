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
-- Name: visit_configuration; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE visit_configuration (
    id integer NOT NULL,
    anc_visit1_from_week integer,
    anc_visit1_to_week integer,
    anc_visit2_from_week integer,
    anc_visit2_to_week integer,
    anc_visit3_from_week integer,
    anc_visit3_to_week integer,
    anc_visit4_from_week integer,
    anc_visit4_to_week integer
);


ALTER TABLE visit_configuration OWNER TO dhanush;

--
-- Name: visit_configuration_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE visit_configuration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE visit_configuration_id_seq OWNER TO dhanush;

--
-- Name: visit_configuration_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE visit_configuration_id_seq OWNED BY visit_configuration.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY visit_configuration ALTER COLUMN id SET DEFAULT nextval('visit_configuration_id_seq'::regclass);


--
-- Name: visit_configuration_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY visit_configuration
    ADD CONSTRAINT visit_configuration_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

