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
-- Name: ec_reg; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE ec_reg (
    id integer NOT NULL,
    entityid character varying(200) NOT NULL,
    phonenumber character varying(200) NOT NULL
);


ALTER TABLE ec_reg OWNER TO dhanush;

--
-- Name: ec_reg_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE ec_reg_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ec_reg_id_seq OWNER TO dhanush;

--
-- Name: ec_reg_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE ec_reg_id_seq OWNED BY ec_reg.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY ec_reg ALTER COLUMN id SET DEFAULT nextval('ec_reg_id_seq'::regclass);


--
-- PostgreSQL database dump complete
--

