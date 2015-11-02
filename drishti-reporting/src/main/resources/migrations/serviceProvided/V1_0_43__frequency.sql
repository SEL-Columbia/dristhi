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
-- Name: frequency; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE frequency (
    id integer NOT NULL,
    active boolean NOT NULL,
    number_of_times character varying(100) NOT NULL
);


ALTER TABLE frequency OWNER TO dhanush;

--
-- Name: frequency_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE frequency_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE frequency_id_seq OWNER TO dhanush;

--
-- Name: frequency_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE frequency_id_seq OWNED BY frequency.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY frequency ALTER COLUMN id SET DEFAULT nextval('frequency_id_seq'::regclass);


--
-- Name: frequency_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY frequency
    ADD CONSTRAINT frequency_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

