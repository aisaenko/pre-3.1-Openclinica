@REM A script to setup jobs to create the OpenClinica data warehouse daily at midnight.
@REM Verify that the location of the psql.exe and pg_data_warehouse.sql files are correct.

AT 00:00 /EVERY:SUNDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
AT 00:00 /EVERY:MONDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
AT 00:00 /EVERY:TUESDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
AT 00:00 /EVERY:WEDNESDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
AT 00:00 /EVERY:THURSDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
AT 00:00 /EVERY:FRIDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
AT 00:00 /EVERY:SATURDAY "C:\Program Files\PostgreSQL\8.1\bin\psql.exe C:\OpenClinica\OpenClinica-2.0\conf\pg_data_warehouse.sql"
