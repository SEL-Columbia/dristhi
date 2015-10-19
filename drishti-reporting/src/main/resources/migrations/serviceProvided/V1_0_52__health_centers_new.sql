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
-- Name: health_centers_new; Type: TABLE; Schema: report; Owner: dhanush; Tablespace: 
--

CREATE TABLE health_centers_new (
    id integer NOT NULL,
    hospital_name character varying(200) DEFAULT NULL::character varying NOT NULL,
    hospital_type character varying(200) DEFAULT NULL::character varying NOT NULL,
    hospital_address character varying(200) DEFAULT NULL::character varying NOT NULL,
    parent_hospital character varying(200) DEFAULT NULL::character varying,
    villages character varying(200),
    active boolean NOT NULL,
    country_name integer,
    county_name integer,
    district_name integer,
    subdistrict_name integer
);


ALTER TABLE health_centers_new OWNER TO dhanush;

--
-- Name: health_centers_new_id_seq; Type: SEQUENCE; Schema: report; Owner: dhanush
--

CREATE SEQUENCE health_centers_new_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE health_centers_new_id_seq OWNER TO dhanush;

--
-- Name: health_centers_new_id_seq; Type: SEQUENCE OWNED BY; Schema: report; Owner: dhanush
--

ALTER SEQUENCE health_centers_new_id_seq OWNED BY health_centers_new.id;


--
-- Name: id; Type: DEFAULT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY health_centers_new ALTER COLUMN id SET DEFAULT nextval('health_centers_new_id_seq'::regclass);


--
-- Name: health_centers_new_hospital_name_key; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY health_centers_new
    ADD CONSTRAINT health_centers_new_hospital_name_key UNIQUE (hospital_name);


--
-- Name: health_centers_new_id_key1; Type: CONSTRAINT; Schema: report; Owner: dhanush; Tablespace: 
--

ALTER TABLE ONLY health_centers_new
    ADD CONSTRAINT health_centers_new_id_key1 UNIQUE (id);


--
-- Name: health_centers_new_country_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY health_centers_new
    ADD CONSTRAINT health_centers_new_country_name_fkey FOREIGN KEY (country_name) REFERENCES country_tb(id);


--
-- Name: health_centers_new_county_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY health_centers_new
    ADD CONSTRAINT health_centers_new_county_name_fkey FOREIGN KEY (county_name) REFERENCES county_tb(id);


--
-- Name: health_centers_new_district_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY health_centers_new
    ADD CONSTRAINT health_centers_new_district_name_fkey FOREIGN KEY (district_name) REFERENCES district_new(id);


--
-- Name: health_centers_new_subdistrict_name_fkey; Type: FK CONSTRAINT; Schema: report; Owner: dhanush
--

ALTER TABLE ONLY health_centers_new
    ADD CONSTRAINT health_centers_new_subdistrict_name_fkey FOREIGN KEY (subdistrict_name) REFERENCES subdistrict_new(id);


--
-- PostgreSQL database dump complete
--

