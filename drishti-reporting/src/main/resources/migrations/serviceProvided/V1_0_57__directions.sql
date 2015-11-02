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
-- Name: directions; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE directions (
    id integer NOT NULL,
    directions character varying(200) NOT NULL,
    active boolean NOT NULL
);


ALTER TABLE directions OWNER TO dhanush;

--
-- Name: directions_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE directions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE directions_id_seq OWNER TO dhanush;

--
-- Name: directions_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE directions_id_seq OWNED BY directions.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY directions ALTER COLUMN id SET DEFAULT nextval('directions_id_seq'::regclass);


--
-- Name: directions_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY directions
    ADD CONSTRAINT directions_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

