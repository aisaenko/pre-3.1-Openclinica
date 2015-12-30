-- -----------------------------------------------------------------------------
-- Set up Oracle database environment
-- -----------------------------------------------------------------------------
--
-- Create tablespaces
--
CREATE TABLESPACE &clinica_data_ts 
  DATAFILE &clinica_data_data_file_dir SIZE 10M 
  AUTOEXTEND ON NEXT  10M 
  MAXSIZE  2048M 
	EXTENT MANAGEMENT LOCAL SEGMENT 
  SPACE MANAGEMENT  AUTO
  LOGGING
/
  
CREATE TABLESPACE &clinica_indx_ts
  DATAFILE &clinica_indx_data_file_dir SIZE 10M
  AUTOEXTEND ON NEXT 10M
  MAXSIZE 2048M
  EXTENT management LOCAL SEGMENT
  SPACE management auto
  LOGGING
/


CREATE TABLESPACE &clinica_lob_ts
  DATAFILE &clinica_lob_data_file_dir SIZE 1M
  AUTOEXTEND ON NEXT 1M
  MAXSIZE 2048M
  EXTENT management LOCAL SEGMENT
  SPACE management auto
  LOGGING
/
 
--
-- Create user and grant privileges
--
CREATE USER &clinica IDENTIFIED BY &clinicapassword 
  DEFAULT TABLESPACE &clinica_data_ts
  TEMPORARY TABLESPACE &temp_ts
  QUOTA UNLIMITED ON &clinica_data_ts
  QUOTA UNLIMITED ON &clinica_indx_ts
  QUOTA UNLIMITED ON &clinica_lob_ts
/

GRANT CONNECT TO &clinica
/

ALTER USER &clinica DEFAULT ROLE ALL
/

