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
-- Name: subdistrict_new; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE subdistrict_new (
    id integer NOT NULL,
    subdistrict character varying(100),
    active boolean NOT NULL,
    country integer,
    county integer,
    district integer
);


ALTER TABLE subdistrict_new OWNER TO dhanush;

--
-- Name: subdistrict_new_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE subdistrict_new_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE subdistrict_new_id_seq OWNER TO dhanush;

--
-- Name: subdistrict_new_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE subdistrict_new_id_seq OWNED BY subdistrict_new.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY subdistrict_new ALTER COLUMN id SET DEFAULT nextval('subdistrict_new_id_seq'::regclass);


--
-- Name: subdistrict_new_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY subdistrict_new
    ADD CONSTRAINT subdistrict_new_pkey PRIMARY KEY (id);


--
-- Name: subdistrict_new_subdistrict_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY subdistrict_new
    ADD CONSTRAINT subdistrict_new_subdistrict_key UNIQUE (subdistrict);


--
-- Name: subdistrict_new_country_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY subdistrict_new
    ADD CONSTRAINT subdistrict_new_country_fkey FOREIGN KEY (country) REFERENCES country_tb(id);


--
-- Name: subdistrict_new_county_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY subdistrict_new
    ADD CONSTRAINT subdistrict_new_county_fkey FOREIGN KEY (county) REFERENCES county_tb(id);


--
-- Name: subdistrict_new_district_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY subdistrict_new
    ADD CONSTRAINT subdistrict_new_district_fkey FOREIGN KEY (district) REFERENCES district_new(id);


--
-- PostgreSQL database dump complete
--

