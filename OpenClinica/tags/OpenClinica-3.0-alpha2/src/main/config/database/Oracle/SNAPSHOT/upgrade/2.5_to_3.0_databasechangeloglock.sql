
  CREATE TABLE "P_LB_TEST3"."DATABASECHANGELOGLOCK" 
   (	"ID" NUMBER(*,0) NOT NULL ENABLE, 
	"LOCKED" NUMBER(1,0) NOT NULL ENABLE, 
	"LOCKGRANTED" TIMESTAMP (6), 
	"LOCKEDBY" VARCHAR2(255 BYTE), 
	 CONSTRAINT "PK_DATABASECHANGELOGLOCK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "CLINICA25SNAPSHOT"  ENABLE
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "CLINICA25SNAPSHOT" ;
 
REM INSERTING into "P_LB_TEST3".DATABASECHANGELOGLOCK
Insert into "P_LB_TEST3".DATABASECHANGELOGLOCK (ID,LOCKED,LOCKGRANTED,LOCKEDBY) values (1,0,null,null);

