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
-- Name: form_fields; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE form_fields (
    id integer NOT NULL,
    form_name character varying(50),
    field1 character varying(50),
    field2 character varying(50),
    field3 character varying(50),
    field4 character varying(50),
    field5 character varying(50),
    country integer
);


ALTER TABLE form_fields OWNER TO dhanush;

--
-- Name: form_fields_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE form_fields_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE form_fields_id_seq OWNER TO dhanush;

--
-- Name: form_fields_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE form_fields_id_seq OWNED BY form_fields.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY form_fields ALTER COLUMN id SET DEFAULT nextval('form_fields_id_seq'::regclass);


--
-- Name: form_fields_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY form_fields
    ADD CONSTRAINT form_fields_pkey PRIMARY KEY (id);


--
-- Name: form_fields_country_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY form_fields
    ADD CONSTRAINT form_fields_country_fkey FOREIGN KEY (country) REFERENCES country_tb(id);


--
-- PostgreSQL database dump complete
--

