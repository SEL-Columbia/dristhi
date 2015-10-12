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
-- Name: app_configuration; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE app_configuration (
    id integer NOT NULL,
    country_name integer NOT NULL,
    wife_age_min integer DEFAULT 0 NOT NULL,
    wife_age_max integer DEFAULT 0 NOT NULL,
    husband_age_min integer DEFAULT 0 NOT NULL,
    husband_age_max integer DEFAULT 0 NOT NULL,
    temperature_units character varying(20) NOT NULL,
    escalation_schedule integer NOT NULL,
    is_highrisk character varying(100)
);


ALTER TABLE app_configuration OWNER TO dhanush;

--
-- Name: config_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE config_id_seq OWNER TO dhanush;

--
-- Name: config_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE config_id_seq OWNED BY app_configuration.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY app_configuration ALTER COLUMN id SET DEFAULT nextval('config_id_seq'::regclass);


--
-- Name: app_configuration_country_name_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY app_configuration
    ADD CONSTRAINT app_configuration_country_name_key UNIQUE (country_name);


--
-- Name: app_configuration_id_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY app_configuration
    ADD CONSTRAINT app_configuration_id_key UNIQUE (id);


--
-- Name: app_configuration_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY app_configuration
    ADD CONSTRAINT app_configuration_pkey PRIMARY KEY (id);


--
-- Name: app_configuration_country_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY app_configuration
    ADD CONSTRAINT app_configuration_country_name_fkey FOREIGN KEY (country_name) REFERENCES country_tb(id);


--
-- PostgreSQL database dump complete
--

