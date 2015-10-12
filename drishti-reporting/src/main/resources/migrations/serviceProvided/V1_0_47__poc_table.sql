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
-- Name: poc_table; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE poc_table (
    id integer NOT NULL,
    visitentityid character varying(100) NOT NULL,
    entityidec character varying(100) NOT NULL,
    anmid character varying(100) NOT NULL,
    level character varying(35) NOT NULL,
    clientversion character varying(35) NOT NULL,
    serverversion character varying(35) NOT NULL,
    visittype character varying(35) NOT NULL,
    phc character varying(100) NOT NULL,
    pending character varying(300),
    docid character varying(50) DEFAULT NULL::character varying,
    "timestamp" timestamp with time zone
);


ALTER TABLE poc_table OWNER TO dhanush;

--
-- Name: poc_table_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE poc_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE poc_table_id_seq OWNER TO dhanush;

--
-- Name: poc_table_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE poc_table_id_seq OWNED BY poc_table.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY poc_table ALTER COLUMN id SET DEFAULT nextval('poc_table_id_seq'::regclass);


--
-- Name: poc_table_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY poc_table
    ADD CONSTRAINT poc_table_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

