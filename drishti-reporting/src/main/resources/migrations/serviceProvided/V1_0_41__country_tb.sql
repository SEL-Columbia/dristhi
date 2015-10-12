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
-- Name: country_tb; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE country_tb (
    id integer NOT NULL,
    country_name character varying(100) NOT NULL,
    country_code character varying(10) NOT NULL,
    active boolean
);


ALTER TABLE country_tb OWNER TO dhanush;

--
-- Name: country_tb_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE country_tb_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE country_tb_id_seq OWNER TO dhanush;

--
-- Name: country_tb_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE country_tb_id_seq OWNED BY country_tb.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY country_tb ALTER COLUMN id SET DEFAULT nextval('country_tb_id_seq'::regclass);


--
-- Name: country_tb_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY country_tb
    ADD CONSTRAINT country_tb_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

