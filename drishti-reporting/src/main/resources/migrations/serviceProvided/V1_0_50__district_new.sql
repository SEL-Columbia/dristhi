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
-- Name: district_new; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE district_new (
    id integer NOT NULL,
    country_name integer NOT NULL,
    county_name integer NOT NULL,
    district_name character varying(100) NOT NULL,
    active boolean NOT NULL
);


ALTER TABLE district_new OWNER TO dhanush;

--
-- Name: district_new_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE district_new_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE district_new_id_seq OWNER TO dhanush;

--
-- Name: district_new_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE district_new_id_seq OWNED BY district_new.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY district_new ALTER COLUMN id SET DEFAULT nextval('district_new_id_seq'::regclass);


--
-- Name: district_new_district_name_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY district_new
    ADD CONSTRAINT district_new_district_name_key UNIQUE (district_name);


--
-- Name: district_new_pkey; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY district_new
    ADD CONSTRAINT district_new_pkey PRIMARY KEY (id);


--
-- Name: district_new_country_name; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX district_new_country_name ON district_new USING btree (country_name);


--
-- Name: district_new_county_name; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX district_new_county_name ON district_new USING btree (county_name);


--
-- Name: district_new_district_name_like; Type: INDEX; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE INDEX district_new_district_name_like ON district_new USING btree (district_name varchar_pattern_ops);


--
-- Name: district_new_country_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY district_new
    ADD CONSTRAINT district_new_country_name_fkey FOREIGN KEY (country_name) REFERENCES country_tb(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: district_new_county_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY district_new
    ADD CONSTRAINT district_new_county_name_fkey FOREIGN KEY (county_name) REFERENCES county_tb(id) DEFERRABLE INITIALLY DEFERRED;


--
-- PostgreSQL database dump complete
--

