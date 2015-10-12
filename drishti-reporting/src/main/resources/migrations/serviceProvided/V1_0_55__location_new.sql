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
-- Name: location_new; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE location_new (
    id integer NOT NULL,
    location character varying(100),
    active boolean NOT NULL,
    country integer,
    county integer,
    district integer,
    subdistrict integer
);


ALTER TABLE location_new OWNER TO dhanush;

--
-- Name: location_new_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE location_new_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE location_new_id_seq OWNER TO dhanush;

--
-- Name: location_new_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE location_new_id_seq OWNED BY location_new.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY location_new ALTER COLUMN id SET DEFAULT nextval('location_new_id_seq'::regclass);


--
-- Name: location_new_location_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY location_new
    ADD CONSTRAINT location_new_location_key UNIQUE (location);


--
-- Name: location_new_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY location_new
    ADD CONSTRAINT location_new_pkey PRIMARY KEY (id);


--
-- Name: location_new_country_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY location_new
    ADD CONSTRAINT location_new_country_fkey FOREIGN KEY (country) REFERENCES country_tb(id);


--
-- Name: location_new_county_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY location_new
    ADD CONSTRAINT location_new_county_fkey FOREIGN KEY (county) REFERENCES county_tb(id);


--
-- Name: location_new_district_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY location_new
    ADD CONSTRAINT location_new_district_fkey FOREIGN KEY (district) REFERENCES district_new(id);


--
-- Name: location_new_subdistrict_fkey1; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY location_new
    ADD CONSTRAINT location_new_subdistrict_fkey1 FOREIGN KEY (subdistrict) REFERENCES subdistrict_new(id);


--
-- PostgreSQL database dump complete
--

