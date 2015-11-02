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
-- Name: investigation; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE investigation (
    id integer NOT NULL,
    service_group_name character varying(200) NOT NULL,
    investigation_name character varying(200) NOT NULL,
    is_active boolean NOT NULL,
    CONSTRAINT "check_inv_group-name" CHECK (((((service_group_name)::text = 'procedures'::text) OR ((service_group_name)::text = 'radiology'::text)) OR ((service_group_name)::text = 'laboratory'::text)))
);


ALTER TABLE investigation OWNER TO dhanush;

--
-- Name: investigations_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE investigations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE investigations_id_seq OWNER TO dhanush;

--
-- Name: investigations_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE investigations_id_seq OWNED BY investigation.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY investigation ALTER COLUMN id SET DEFAULT nextval('investigations_id_seq'::regclass);


--
-- Name: investigations_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY investigation
    ADD CONSTRAINT investigations_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

