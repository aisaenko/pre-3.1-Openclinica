-- ------------------------------------------------------------------
-- Purpose: Changes item_form_metadata.subheader column width to 4000 
-- character long
-- 
-- Date: 8/2006
--
-- Changed by: Wen
-- ------------------------------------------------------------------
ALTER TABLE item_form_metadata ALTER subheader TYPE varchar(4000);
ALTER TABLE item_form_metadata ALTER COLUMN subheader SET STATISTICS -1;
 
-- The change below is included in basecase_data_1.2.sql
-- ------------------------------------------------------------------
-- Purose:Insert new values into response_type table to support 
-- scoring
--
-- Date: 8/23/2006
--
-- Changed by: Wen
-- ------------------------------------------------------------------
INSERT INTO response_type (name, description) 
  VALUES ('calculation', 'value calculated automatically');