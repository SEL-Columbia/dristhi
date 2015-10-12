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
-- Name: county_tb; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE county_tb (
    id integer NOT NULL,
    country_name integer NOT NULL,
    county_name character varying(100),
    active boolean NOT NULL
);


ALTER TABLE county_tb OWNER TO dhanush;

--
-- Name: county_tb_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE county_tb_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE county_tb_id_seq OWNER TO dhanush;

--
-- Name: county_tb_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE county_tb_id_seq OWNED BY county_tb.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY county_tb ALTER COLUMN id SET DEFAULT nextval('county_tb_id_seq'::regclass);


--
-- Name: county_tb_county_name_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY county_tb
    ADD CONSTRAINT county_tb_county_name_key UNIQUE (county_name);


--
-- Name: county_tb_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY county_tb
    ADD CONSTRAINT county_tb_pkey PRIMARY KEY (id);


--
-- Name: county_tb_country_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY county_tb
    ADD CONSTRAINT county_tb_country_name_fkey FOREIGN KEY (country_name) REFERENCES country_tb(id);


--
-- PostgreSQL database dump complete
--

