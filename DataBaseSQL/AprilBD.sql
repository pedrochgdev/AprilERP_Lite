--------------------------------------------------------
--  File created - miércoles-febrero-26-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence DBTOOLS$EXECUTION_HISTORY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ADMIN"."DBTOOLS$EXECUTION_HISTORY_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 81 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_DEVOLUCIONES
--------------------------------------------------------

   CREATE SEQUENCE  "ADMIN"."SEQ_DEVOLUCIONES"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_PRENDAS
--------------------------------------------------------

   CREATE SEQUENCE  "ADMIN"."SEQ_PRENDAS"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 217 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SEQ_VENTAS
--------------------------------------------------------

   CREATE SEQUENCE  "ADMIN"."SEQ_VENTAS"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 13 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence USUARIOS_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ADMIN"."USUARIOS_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 4 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table CATEGORIAS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."CATEGORIAS" 
   (	"ID_CATEGORIA" NUMBER(3,0), 
	"NOMBRE" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP"
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT DELETE ON "ADMIN"."CATEGORIAS" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."CATEGORIAS" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."CATEGORIAS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."CATEGORIAS" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table CLIENTES
--------------------------------------------------------

  CREATE TABLE "ADMIN"."CLIENTES" 
   (	"DNI" VARCHAR2(15 BYTE) COLLATE "USING_NLS_COMP", 
	"NOMBRE" VARCHAR2(100 BYTE) COLLATE "USING_NLS_COMP", 
	"TELEFONO" VARCHAR2(20 BYTE) COLLATE "USING_NLS_COMP", 
	"CORREO" VARCHAR2(100 BYTE) COLLATE "USING_NLS_COMP", 
	"ID" NUMBER GENERATED ALWAYS AS IDENTITY MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE 
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT INSERT ON "ADMIN"."CLIENTES" TO "ROL_VENDEDOR";
  GRANT SELECT ON "ADMIN"."CLIENTES" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."CLIENTES" TO "ROL_VENDEDOR";
  GRANT DELETE ON "ADMIN"."CLIENTES" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."CLIENTES" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."CLIENTES" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."CLIENTES" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table DBTOOLS$EXECUTION_HISTORY
--------------------------------------------------------

  CREATE TABLE "ADMIN"."DBTOOLS$EXECUTION_HISTORY" 
   (	"ID" NUMBER, 
	"HASH" CLOB COLLATE "USING_NLS_COMP", 
	"CREATED_BY" VARCHAR2(255 BYTE) COLLATE "USING_NLS_COMP", 
	"CREATED_ON" TIMESTAMP (6) WITH TIME ZONE, 
	"UPDATED_BY" VARCHAR2(255 BYTE) COLLATE "USING_NLS_COMP", 
	"UPDATED_ON" TIMESTAMP (6) WITH TIME ZONE, 
	"STATEMENT" CLOB COLLATE "USING_NLS_COMP", 
	"TIMES" NUMBER
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" 
 LOB ("HASH") STORE AS SECUREFILE (
  TABLESPACE "DATA" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES 
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) 
 LOB ("STATEMENT") STORE AS SECUREFILE (
  TABLESPACE "DATA" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES 
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
--------------------------------------------------------
--  DDL for Table DETALLE_DEVOLUCIONES
--------------------------------------------------------

  CREATE TABLE "ADMIN"."DETALLE_DEVOLUCIONES" 
   (	"ID_DEVOLUCION" NUMBER, 
	"ID_PRENDA" VARCHAR2(8 BYTE) COLLATE "USING_NLS_COMP", 
	"CANTIDAD" NUMBER
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT INSERT ON "ADMIN"."DETALLE_DEVOLUCIONES" TO "ROL_VENDEDOR";
  GRANT SELECT ON "ADMIN"."DETALLE_DEVOLUCIONES" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."DETALLE_DEVOLUCIONES" TO "ROL_VENDEDOR";
  GRANT INSERT ON "ADMIN"."DETALLE_DEVOLUCIONES" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."DETALLE_DEVOLUCIONES" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."DETALLE_DEVOLUCIONES" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table DETALLE_VENTAS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."DETALLE_VENTAS" 
   (	"ID_VENTA" NUMBER(6,0), 
	"ID_PRENDA" VARCHAR2(8 BYTE) COLLATE "USING_NLS_COMP", 
	"CANTIDAD" NUMBER(3,0), 
	"PRECIO_UNITARIO" NUMBER(8,2), 
	"CANTIDAD_DEVUELTA" NUMBER DEFAULT 0
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT INSERT ON "ADMIN"."DETALLE_VENTAS" TO "ROL_VENDEDOR";
  GRANT SELECT ON "ADMIN"."DETALLE_VENTAS" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."DETALLE_VENTAS" TO "ROL_VENDEDOR";
  GRANT DELETE ON "ADMIN"."DETALLE_VENTAS" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."DETALLE_VENTAS" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."DETALLE_VENTAS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."DETALLE_VENTAS" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table DEVOLUCIONES
--------------------------------------------------------

  CREATE TABLE "ADMIN"."DEVOLUCIONES" 
   (	"ID_DEVOLUCION" NUMBER, 
	"ID_VENTA" NUMBER, 
	"FECHA_DEVOLUCION" TIMESTAMP (6) DEFAULT (CAST(CURRENT_TIMESTAMP AS DATE)), 
	"RAZON" VARCHAR2(500 BYTE) COLLATE "USING_NLS_COMP"
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT INSERT ON "ADMIN"."DEVOLUCIONES" TO "ROL_VENDEDOR";
  GRANT SELECT ON "ADMIN"."DEVOLUCIONES" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."DEVOLUCIONES" TO "ROL_VENDEDOR";
  GRANT INSERT ON "ADMIN"."DEVOLUCIONES" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."DEVOLUCIONES" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."DEVOLUCIONES" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table PRENDAS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."PRENDAS" 
   (	"ID_PRENDA" VARCHAR2(8 BYTE) COLLATE "USING_NLS_COMP", 
	"NOMBRE" VARCHAR2(100 BYTE) COLLATE "USING_NLS_COMP", 
	"COLOR" VARCHAR2(30 BYTE) COLLATE "USING_NLS_COMP", 
	"ID_TALLA" NUMBER(2,0), 
	"PRECIO_COMPRA" NUMBER(8,2), 
	"PRECIO_VENTA" NUMBER(8,2), 
	"STOCK" NUMBER(3,0) DEFAULT 0, 
	"ID_CATEGORIA" NUMBER(3,0), 
	"FECHA_REGISTRO" DATE DEFAULT (CAST(CURRENT_TIMESTAMP AS DATE)), 
	"ID_TIENDA" NUMBER(3,0), 
	"CANTIDAD_COMPRADA" NUMBER DEFAULT 0
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT INSERT ON "ADMIN"."PRENDAS" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."PRENDAS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."PRENDAS" TO "ROL_ADMIN";
  GRANT DELETE ON "ADMIN"."PRENDAS" TO "JEFE";
  GRANT SELECT ON "ADMIN"."PRENDAS" TO "ROL_VENDEDOR";
  GRANT DELETE ON "ADMIN"."PRENDAS" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."PRENDAS" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."PRENDAS" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Table TALLAS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."TALLAS" 
   (	"ID_TALLA" NUMBER(2,0), 
	"TALLA" CHAR(4 BYTE) COLLATE "USING_NLS_COMP"
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT DELETE ON "ADMIN"."TALLAS" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."TALLAS" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."TALLAS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."TALLAS" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table TIENDAS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."TIENDAS" 
   (	"ID_TIENDA" NUMBER(3,0), 
	"NOMBRE" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP"
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT DELETE ON "ADMIN"."TIENDAS" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."TIENDAS" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."TIENDAS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."TIENDAS" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Table USUARIOS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."USUARIOS" 
   (	"ID" NUMBER, 
	"USERNAME" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP", 
	"PASSWORD_HASH" VARCHAR2(255 BYTE) COLLATE "USING_NLS_COMP", 
	"NOMBRE_COMPLETO" VARCHAR2(100 BYTE) COLLATE "USING_NLS_COMP", 
	"EMAIL" VARCHAR2(100 BYTE) COLLATE "USING_NLS_COMP", 
	"ROL" VARCHAR2(20 BYTE) COLLATE "USING_NLS_COMP", 
	"FECHA_CREACION" TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP, 
	"ULTIMO_LOGIN" TIMESTAMP (6), 
	"DB_USER" VARCHAR2(50 BYTE) COLLATE "USING_NLS_COMP", 
	"DB_PASSWORD" VARCHAR2(500 BYTE) COLLATE "USING_NLS_COMP"
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT SELECT ON "ADMIN"."USUARIOS" TO "VERIFICADOR";
  GRANT UPDATE ON "ADMIN"."USUARIOS" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."USUARIOS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."USUARIOS" TO "VENDEDOR" WITH GRANT OPTION;
  GRANT UPDATE ON "ADMIN"."USUARIOS" TO "VERIFICADOR";
  GRANT ALTER ON "ADMIN"."USUARIOS" TO "VENDEDOR";
  GRANT SELECT ON "ADMIN"."USUARIOS" TO "VENDEDOR";
  GRANT ALTER ON "ADMIN"."USUARIOS" TO "ROL_VENDEDOR";
  GRANT SELECT ON "ADMIN"."USUARIOS" TO "ROL_VENDEDOR";
  GRANT ALTER ON "ADMIN"."USUARIOS" TO "JEFE";
  GRANT SELECT ON "ADMIN"."USUARIOS" TO "JEFE";
--------------------------------------------------------
--  DDL for Table VENTAS
--------------------------------------------------------

  CREATE TABLE "ADMIN"."VENTAS" 
   (	"ID_VENTA" NUMBER(6,0), 
	"FECHA_VENTA" DATE DEFAULT (CAST(CURRENT_TIMESTAMP AS DATE)), 
	"TOTAL_VENTA" NUMBER(10,2), 
	"ESTADO" VARCHAR2(15 BYTE) COLLATE "USING_NLS_COMP", 
	"MONTO_PAGADO" NUMBER(8,2), 
	"MONTO_RESTANTE" NUMBER(8,2), 
	"METODO_PAGO" VARCHAR2(15 BYTE) COLLATE "USING_NLS_COMP", 
	"FECHA_ENTREGA" DATE, 
	"ID_CLIENTE" NUMBER
   )  DEFAULT COLLATION "USING_NLS_COMP" SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 10 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
  GRANT INSERT ON "ADMIN"."VENTAS" TO "ROL_ADMIN";
  GRANT SELECT ON "ADMIN"."VENTAS" TO "ROL_ADMIN";
  GRANT UPDATE ON "ADMIN"."VENTAS" TO "ROL_ADMIN";
  GRANT INSERT ON "ADMIN"."VENTAS" TO "ROL_VENDEDOR";
  GRANT SELECT ON "ADMIN"."VENTAS" TO "ROL_VENDEDOR";
  GRANT UPDATE ON "ADMIN"."VENTAS" TO "ROL_VENDEDOR";
  GRANT DELETE ON "ADMIN"."VENTAS" TO "JEFE";
  GRANT DELETE ON "ADMIN"."VENTAS" TO "ROL_VENDEDOR";
  GRANT DELETE ON "ADMIN"."VENTAS" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Index SYS_C008454
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."SYS_C008454" ON "ADMIN"."PRENDAS" ("ID_PRENDA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index PK_DETALLE_VENTAS
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."PK_DETALLE_VENTAS" ON "ADMIN"."DETALLE_VENTAS" ("ID_VENTA", "ID_PRENDA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index SYS_C008420
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."SYS_C008420" ON "ADMIN"."TIENDAS" ("ID_TIENDA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index SYS_C008460
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."SYS_C008460" ON "ADMIN"."VENTAS" ("ID_VENTA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index DBTOOLS$EXECUTION_HISTORY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."DBTOOLS$EXECUTION_HISTORY_PK" ON "ADMIN"."DBTOOLS$EXECUTION_HISTORY" ("ID") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index SYS_C008443
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."SYS_C008443" ON "ADMIN"."CATEGORIAS" ("ID_CATEGORIA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index SYS_C008445
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."SYS_C008445" ON "ADMIN"."TALLAS" ("ID_TALLA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index CLIENTES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."CLIENTES_PK" ON "ADMIN"."CLIENTES" ("ID") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Index SYS_C008446
--------------------------------------------------------

  CREATE UNIQUE INDEX "ADMIN"."SYS_C008446" ON "ADMIN"."TALLAS" ("TALLA") 
  PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA" ;
--------------------------------------------------------
--  DDL for Trigger MANEJO_MONTOS_VENTAS
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "ADMIN"."MANEJO_MONTOS_VENTAS" 
BEFORE INSERT OR UPDATE ON VENTAS
FOR EACH ROW
DECLARE
    v_monto_restante_calculado NUMBER;
BEGIN
    -- Sección para INSERT
    IF INSERTING THEN
        :NEW.monto_restante := :NEW.total_venta - NVL(:NEW.monto_pagado, 0);

        -- Actualizar fecha_entrega si el estado es ENTREGADO
        IF :NEW.estado = 'ENTREGADO' THEN
            :NEW.fecha_entrega := SYSDATE;
        END IF;
    END IF;

    -- Sección para UPDATE
    IF UPDATING THEN
        IF :NEW.estado IN ('PAGADO', 'ENTREGADO') THEN
            -- Actualizar montos
            :NEW.monto_pagado := :NEW.total_venta;
            :NEW.monto_restante := 0;

            -- Actualizar fecha_entrega solo si el estado es ENTREGADO
            IF :NEW.estado = 'ENTREGADO' THEN
                :NEW.fecha_entrega := CAST(CURRENT_TIMESTAMP AS DATE);
            END IF;
        ELSE
            -- Recalcular si cambian montos base
            IF :NEW.total_venta <> :OLD.total_venta OR 
               :NEW.monto_pagado <> :OLD.monto_pagado THEN
                :NEW.monto_restante := :NEW.total_venta - NVL(:NEW.monto_pagado, 0);
            END IF;
        END IF;
    END IF;
END;
/
ALTER TRIGGER "ADMIN"."MANEJO_MONTOS_VENTAS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_GENERAR_ID_PRENDA
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "ADMIN"."TRG_GENERAR_ID_PRENDA" 
BEFORE INSERT ON prendas
FOR EACH ROW
DECLARE
    v_anio CHAR(4);
    v_secuencia VARCHAR2(4);
BEGIN
    -- Obtener año actual del registro
    v_anio := TO_CHAR(SYSDATE, 'YYYY');

    -- Formatear secuencia con ceros a la izquierda
    v_secuencia := LPAD(seq_prendas.NEXTVAL, 4, '0');

    -- Asignar ID con formato YYYYNNNN
    :NEW.id_prenda := v_anio || v_secuencia;
END;


/
ALTER TRIGGER "ADMIN"."TRG_GENERAR_ID_PRENDA" ENABLE;
--------------------------------------------------------
--  DDL for Trigger USUARIOS_TRG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "ADMIN"."USUARIOS_TRG" 
BEFORE INSERT ON usuarios
FOR EACH ROW
BEGIN
    :new.id := usuarios_seq.NEXTVAL;
END;

/
ALTER TRIGGER "ADMIN"."USUARIOS_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Procedure ACTUALIZAR_ESTADO_VENTA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."ACTUALIZAR_ESTADO_VENTA" (
    p_id_venta IN VARCHAR2,
    p_estado IN VARCHAR2
)
IS
BEGIN
    UPDATE VENTAS 
    SET ESTADO = p_estado
    WHERE ID_VENTA = p_id_venta;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;

/

  GRANT EXECUTE ON "ADMIN"."ACTUALIZAR_ESTADO_VENTA" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."ACTUALIZAR_ESTADO_VENTA" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Procedure ACTUALIZAR_INVENTARIO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."ACTUALIZAR_INVENTARIO" (
    p_id_prenda IN VARCHAR2,
    p_nombre IN VARCHAR2,
    p_color IN VARCHAR2,
    p_talla IN CHAR,
    p_precio_compra in Number,
    p_precio_venta IN NUMBER,
    p_stock IN NUMBER,
    p_cantidad IN NUMBER,
    p_categoria IN VARCHAR2,
    P_TIENDA IN VARCHAR2
) IS
    v_id_categoria NUMBER(3);
    v_id_talla NUMBER(2);
    V_ID_TIENDA NUMBER(3);
BEGIN
    -- Obtener IDs
    SELECT id_categoria INTO v_id_categoria
    FROM categorias
    WHERE UPPER(nombre) = UPPER(p_categoria);

    SELECT id_talla INTO v_id_talla
    FROM tallas
    WHERE talla = p_talla;

    SELECT ID_TIENDA INTO v_id_TIENDA
    FROM TIENDAS
    WHERE UPPER(nombre) = UPPER(P_TIENDA);

    -- Actualizar prenda
    UPDATE prendas SET
        nombre = UPPER(p_nombre),
        color = UPPER(p_color),
        id_talla = v_id_talla,
        precio_compra=P_precio_compra,
        precio_venta = p_precio_venta,
        stock = p_stock,
        cantidad_comprada = p_cantidad,
        id_categoria = v_id_categoria,
        ID_TIENDA = V_ID_TIENDA
    WHERE id_prenda = p_id_prenda;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002, 'Datos no válidos');
END ACTUALIZAR_INVENTARIO;

/

  GRANT EXECUTE ON "ADMIN"."ACTUALIZAR_INVENTARIO" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Procedure ELIMINAR_REGISTRO_COMPLETO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."ELIMINAR_REGISTRO_COMPLETO" (
    p_id_prenda IN VARCHAR2
) IS
BEGIN
    DELETE FROM prendas
    WHERE id_prenda = p_id_prenda
    AND NOT EXISTS (
        SELECT 1 FROM detalle_ventas
        WHERE id_prenda = p_id_prenda
    );

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'No se puede eliminar: existen ventas relacionadas');
    END IF;

    COMMIT;
END ELIMINAR_REGISTRO_COMPLETO;


/

  GRANT EXECUTE ON "ADMIN"."ELIMINAR_REGISTRO_COMPLETO" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Procedure FILTRAR_ORDENES
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."FILTRAR_ORDENES" (
    p_desde IN DATE,
    p_hasta IN DATE,
    p_precio_min IN NUMBER,
    p_precio_max IN NUMBER,
    p_nombre_cliente IN VARCHAR2,  -- Cambio de DNI a Nombre
    p_estado IN VARCHAR2,
    p_metodo_pago IN VARCHAR2,
    p_falta_pagar IN NUMBER,
    p_resultado OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_resultado FOR
    SELECT 
        v.ID_VENTA,
        TO_CHAR(v.FECHA_VENTA, 'DD/MM/YYYY HH24:MI') AS FECHA_FORMATEADA,
        v.TOTAL_VENTA,
        c.NOMBRE AS DNI_CLIENTE,  -- Ahora muestra el nombre del cliente
        v.ESTADO,
        v.MONTO_PAGADO,
        v.MONTO_RESTANTE,
        v.METODO_PAGO,
        NVL(TO_CHAR(v.FECHA_ENTREGA, 'DD/MM/YYYY HH24:MI'), 'SIN FECHA') AS FECHA_ENTREGA,

        --TOTALES-----
        SUM(v.MONTO_PAGADO) OVER() AS TOTAL_PAGADO,
        SUM(v.TOTAL_VENTA) OVER() AS TOTAL_VENTAS
    FROM ventas v
    LEFT JOIN clientes c ON v.id_cliente = c.id  -- JOIN agregado

    WHERE
        -- Filtro de fechas con rango completo (incluyendo horas)
        (p_desde IS NULL OR v.fecha_venta >= p_desde) AND
        (p_hasta IS NULL OR v.fecha_venta <= p_hasta) AND

        -- Resto de filtros
        (p_precio_min IS NULL OR v.total_venta >= p_precio_min) AND
        (p_precio_max IS NULL OR v.total_venta <= p_precio_max) AND
        (p_nombre_cliente IS NULL OR UPPER(c.nombre) LIKE UPPER('%' || p_nombre_cliente || '%')) AND  -- Filtra por nombre
        (p_estado IS NULL OR v.estado = p_estado) AND
        (p_metodo_pago IS NULL OR v.metodo_pago = p_metodo_pago) AND
        (
            (p_falta_pagar = 0) OR 
            (p_falta_pagar = 1 AND v.monto_restante > 0) OR 
            (p_falta_pagar = 2 AND v.monto_restante = 0)
        )
            ORDER BY v.FECHA_VENTA DESC;

END;

/

  GRANT EXECUTE ON "ADMIN"."FILTRAR_ORDENES" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."FILTRAR_ORDENES" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Procedure FILTRAR_REGISTROS
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."FILTRAR_REGISTROS" (
    p_desde IN DATE,
    p_hasta IN DATE,
    p_talla IN VARCHAR2,
    p_color IN VARCHAR2,
    p_precio IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
)
AS
    v_impuesto NUMBER := 0.07;
    v_tipo_cambio NUMBER := 3.7;
BEGIN
    OPEN p_cursor FOR 
    SELECT 
        p.id_prenda,
        p.nombre,
        p.color,
        t.talla,
        p.precio_compra,              -- Movido a posición 5
        (p.precio_compra * p.cantidad_comprada) AS subtotal,
        (p.precio_compra * p.cantidad_comprada * (1 + v_impuesto)) AS total_usd,
        (p.precio_compra * p.cantidad_comprada * (1 + v_impuesto) * v_tipo_cambio) AS total_pen,
        p.precio_venta,                -- Movido a posición 9
        p.stock,                       -- Movido a posición 10
        p.cantidad_comprada,           -- Nueva posición 11

        -- Cálculo de totales dinámicos
        SUM(p.precio_compra * p.cantidad_comprada) OVER () AS total_subtotal,
        SUM(p.precio_compra * p.cantidad_comprada * (1 + v_impuesto)) OVER () AS total_usd_gen,
        SUM(p.precio_compra * p.cantidad_comprada * (1 + v_impuesto) * v_tipo_cambio) OVER () AS total_pen_gen

    FROM prendas p
    JOIN tallas t ON p.id_talla = t.id_talla
    WHERE (p_desde IS NULL OR fecha_registro >= p_desde) AND
        (p_hasta IS NULL OR fecha_registro <= p_hasta) AND
       (p_talla IS NULL OR UPPER(t.talla) = UPPER(p_talla))
      AND (p_color IS NULL OR UPPER(p.color) = UPPER(p_color))
      AND (p_precio IS NULL OR p.precio_compra <= p_precio)
      order by
      id_prenda desc;
END;

/

  GRANT EXECUTE ON "ADMIN"."FILTRAR_REGISTROS" TO "JEFE";
  GRANT EXECUTE ON "ADMIN"."FILTRAR_REGISTROS" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Procedure INSERTAR_INVENTARIO
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."INSERTAR_INVENTARIO" (
    p_nombre IN VARCHAR2,
    p_color IN VARCHAR2,
    p_talla IN CHAR,
    p_precio_compra IN NUMBER,
    p_precio_venta IN NUMBER,
    p_stock IN NUMBER,
    p_categoria IN VARCHAR2,
    p_tienda IN VARCHAR2
) IS
    v_id_categoria NUMBER(3);
    v_id_talla NUMBER(2);
    V_ID_TIENDA NUMBER(3);
BEGIN
    -- Obtener ID de categoría
    SELECT id_categoria INTO v_id_categoria
    FROM categorias
    WHERE UPPER(nombre) = UPPER(p_categoria);

    SELECT ID_TIENDA INTO v_id_TIENDA
    FROM TIENDAS
    WHERE UPPER(nombre) = UPPER(P_TIENDA);

    -- Obtener ID de talla
    SELECT id_talla INTO v_id_talla
    FROM tallas
    WHERE talla = p_talla;

    -- Insertar en prendas (el ID se genera automáticamente)
    INSERT INTO prendas(
        nombre, color, id_talla, precio_compra, precio_venta, stock, cantidad_comprada, id_categoria, id_tienda
    ) VALUES (
        UPPER(p_nombre), 
        UPPER(p_color), 
        v_id_talla, 
        p_precio_compra, 
        p_precio_venta, 
        p_stock, 
        p_stock,  -- Aquí asignamos cantidad_comprada igual a stock
        v_id_categoria, 
        v_id_tienda
    );


    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Categoría o talla no encontrada');
END INSERTAR_INVENTARIO;

/

  GRANT EXECUTE ON "ADMIN"."INSERTAR_INVENTARIO" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Procedure REGISTRAR_CLIENTE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."REGISTRAR_CLIENTE" (
    p_dni IN VARCHAR2,
    p_nombre IN VARCHAR2,
    p_telefono IN VARCHAR2,
    p_correo IN VARCHAR2
)
AS
BEGIN
    MERGE INTO CLIENTES c
    USING (
        SELECT ID 
        FROM CLIENTES 
        WHERE (DNI = p_dni AND TELEFONO = p_telefono)
           OR (DNI = p_dni) 
           OR (TELEFONO = p_telefono)
        UNION ALL
        SELECT NULL FROM DUAL WHERE NOT EXISTS (
            SELECT 1 
            FROM CLIENTES 
            WHERE (DNI = p_dni AND TELEFONO = p_telefono)
               OR (DNI = p_dni) 
               OR (TELEFONO = p_telefono)
        )
    ) tmp
    ON (c.ID = tmp.ID)
    WHEN MATCHED THEN
        UPDATE SET 
            DNI = p_dni,   
            NOMBRE = p_nombre,
            TELEFONO = p_telefono,
            CORREO = p_correo
    WHEN NOT MATCHED THEN
        INSERT (DNI, NOMBRE, TELEFONO, CORREO)
        VALUES (p_dni, p_nombre, p_telefono, p_correo);

    COMMIT;
EXCEPTION
    WHEN VALUE_ERROR THEN
        RAISE_APPLICATION_ERROR(-20006, 'Longitud de DNI incorrecta');
    WHEN INVALID_CURSOR THEN
        RAISE_APPLICATION_ERROR(-20007, 'Error interno en registro de cliente');
END;

/

  GRANT EXECUTE ON "ADMIN"."REGISTRAR_CLIENTE" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."REGISTRAR_CLIENTE" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Procedure REGISTRAR_DETALLE_DEVOLUCION
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."REGISTRAR_DETALLE_DEVOLUCION" (
    P_ID_VENTA IN NUMBER,
    P_ID_PRENDA IN VARCHAR2,
    P_CANTIDAD IN NUMBER
) AS
    v_disponible NUMBER;
    v_total_devolucion NUMBER;
    v_total_vendido NUMBER;
    V_ID_DEVOLUCION NUMBER;
    v_nuevo_total NUMBER;
    v_monto_pagado_actual NUMBER;
BEGIN
    -- Paso 1: Validar sin COMMIT
    SELECT CANTIDAD - CANTIDAD_DEVUELTA
    INTO v_disponible
    FROM DETALLE_VENTAS
    WHERE ID_VENTA = P_ID_VENTA
    AND ID_PRENDA = P_ID_PRENDA;

    IF P_CANTIDAD > v_disponible THEN
        RAISE_APPLICATION_ERROR(-20002, 'Cantidad excede lo disponible: ' || v_disponible);
    END IF;

    -- Paso 2: Obtener ID devolución
    SELECT MAX(ID_DEVOLUCION) INTO V_ID_DEVOLUCION
    FROM DEVOLUCIONES WHERE ID_VENTA = P_ID_VENTA;

    -- Paso 3: Registrar detalle
    INSERT INTO DETALLE_DEVOLUCIONES VALUES (V_ID_DEVOLUCION, P_ID_PRENDA, P_CANTIDAD);

    -- Paso 4: Actualizar cantidad devuelta
    UPDATE DETALLE_VENTAS SET
    CANTIDAD_DEVUELTA = CANTIDAD_DEVUELTA + P_CANTIDAD
    WHERE ID_VENTA = P_ID_VENTA
    AND ID_PRENDA = P_ID_PRENDA;

    -- Paso 5: Obtener valores ACTUALIZADOS
    SELECT SUM(CANTIDAD_DEVUELTA), SUM(CANTIDAD),
           SUM((CANTIDAD - CANTIDAD_DEVUELTA) * PRECIO_UNITARIO),
           MAX(MONTO_PAGADO)
    INTO v_total_devolucion, v_total_vendido, v_nuevo_total, v_monto_pagado_actual
    FROM DETALLE_VENTAS
    JOIN VENTAS ON VENTAS.ID_VENTA = DETALLE_VENTAS.ID_VENTA
    WHERE DETALLE_VENTAS.ID_VENTA = P_ID_VENTA;

    -- Paso 6: Actualizar cabecera CORRECTAMENTE
    UPDATE VENTAS SET
    TOTAL_VENTA = v_nuevo_total,
    MONTO_PAGADO = CASE 
                    WHEN v_monto_pagado_actual > v_nuevo_total THEN v_nuevo_total
                    ELSE v_monto_pagado_actual
                   END,
    MONTO_RESTANTE = v_nuevo_total - CASE 
                                    WHEN v_monto_pagado_actual > v_nuevo_total THEN v_nuevo_total
                                    ELSE v_monto_pagado_actual
                                   END,
    ESTADO = CASE
        WHEN v_total_devolucion = v_total_vendido THEN 'DEVUELTO'
        WHEN v_total_devolucion > 0 THEN 'DEV PARCIAL'
        ELSE ESTADO
    END
    WHERE ID_VENTA = P_ID_VENTA;

    -- Paso 7: Actualizar stock
    UPDATE PRENDAS SET
    STOCK = STOCK + P_CANTIDAD
    WHERE ID_PRENDA = P_ID_PRENDA;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END REGISTRAR_DETALLE_DEVOLUCION;

/

  GRANT EXECUTE ON "ADMIN"."REGISTRAR_DETALLE_DEVOLUCION" TO "ROL_VENDEDOR";
  GRANT EXECUTE ON "ADMIN"."REGISTRAR_DETALLE_DEVOLUCION" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Procedure REGISTRAR_DETALLE_VENTA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."REGISTRAR_DETALLE_VENTA" (
    p_id_venta IN NUMBER,
    p_id_prenda IN NUMBER,
    p_cantidad IN NUMBER,
    p_precio_final IN NUMBER
)
AS
    v_stock NUMBER;
BEGIN
    -- Bloquear la fila de la prenda antes de actualizar el stock
    SELECT stock 
    INTO v_stock
    FROM PRENDAS
    WHERE id_prenda = p_id_prenda
    FOR UPDATE;

    -- Insertar detalle
    INSERT INTO DETALLE_VENTAS(
        ID_VENTA,
        ID_PRENDA,
        CANTIDAD,
        precio_unitario
    )
    VALUES(
        p_id_venta,
        p_id_prenda,
        p_cantidad,
        p_precio_final
    );

    -- Actualizar stock con hint para evitar paralelismo
    UPDATE /*+ NO_PARALLEL(PRENDAS) */ PRENDAS 
    SET stock = stock - p_cantidad 
    WHERE id_prenda = p_id_prenda;

    COMMIT;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20002, 'Prenda no existe: ' || p_id_prenda);
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Prenda ya en venta ID: ' || p_id_venta);
    WHEN OTHERS THEN
        ROLLBACK;
END;

/

  GRANT EXECUTE ON "ADMIN"."REGISTRAR_DETALLE_VENTA" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."REGISTRAR_DETALLE_VENTA" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Procedure REGISTRAR_DEVOLUCION
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."REGISTRAR_DEVOLUCION" (
    P_ID_VENTA IN NUMBER,
    P_RAZON IN VARCHAR2
) AS
    V_ID_DEVOLUCION NUMBER;
BEGIN
    -- Obtener siguiente ID de la secuencia
    SELECT SEQ_DEVOLUCIONES.NEXTVAL 
    INTO V_ID_DEVOLUCION 
    FROM DUAL;

    -- Insertar cabecera
    INSERT INTO DEVOLUCIONES(ID_DEVOLUCION, ID_VENTA, RAZON)
    VALUES (V_ID_DEVOLUCION, P_ID_VENTA, P_RAZON);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END REGISTRAR_DEVOLUCION;

/

  GRANT EXECUTE ON "ADMIN"."REGISTRAR_DEVOLUCION" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."REGISTRAR_DEVOLUCION" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Procedure REGISTRAR_VENTA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."REGISTRAR_VENTA" (
    p_dni_cliente IN VARCHAR2,
    p_telefono_cliente IN VARCHAR2,
    p_total IN NUMBER,
    p_metodo_pago IN VARCHAR2,
    p_monto_pagado IN NUMBER,
    p_estado IN VARCHAR2,
    p_id_venta OUT NUMBER
)
AS
    v_id_cliente NUMBER;
BEGIN
    -- Buscar el ID del cliente con el DNI o Teléfono
    SELECT ID INTO v_id_cliente
    FROM CLIENTES
    WHERE (DNI = p_dni_cliente AND TELEFONO = p_telefono_cliente) 
       OR (DNI = p_dni_cliente) 
       OR (TELEFONO = p_telefono_cliente)
    FETCH FIRST 1 ROWS ONLY;  -- Para evitar múltiples resultados


    -- Insertar la venta con el ID del cliente
    INSERT INTO VENTAS(
        ID_VENTA,
        FECHA_VENTA,
        TOTAL_VENTA,
        ID_CLIENTE,
        METODO_PAGO,
        MONTO_PAGADO,
        ESTADO
    )
    VALUES(
        SEQ_VENTAS.NEXTVAL,
        CAST(CURRENT_TIMESTAMP AS DATE),
        p_total,
        v_id_cliente,
        p_metodo_pago,
        p_monto_pagado,
        p_estado
    )
    RETURNING ID_VENTA INTO p_id_venta;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20008, 
            'No se encontró un cliente con ese DNI o Teléfono');
    WHEN INVALID_NUMBER THEN
        RAISE_APPLICATION_ERROR(-20004, 
            'Formato numérico inválido en monto');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20005, 
            'Error al registrar venta: ' || SQLERRM);
END;

/

  GRANT EXECUTE ON "ADMIN"."REGISTRAR_VENTA" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."REGISTRAR_VENTA" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Procedure SP_REGISTRAR_VENTA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "ADMIN"."SP_REGISTRAR_VENTA" (
    p_dni_cliente   IN VARCHAR2,  -- Cliente asociado (puede ser NULL)
    p_fecha_venta  IN DATE,      -- Fecha de la venta
    p_productos    IN SYS.ODCIVARCHAR2LIST, -- Lista de IDs de productos
    p_cantidades   IN SYS.ODCINUMBERLIST,   -- Lista de cantidades
    p_precios      IN SYS.ODCINUMBERLIST    -- Lista de precios unitarios
) AS
    v_id_venta     NUMBER;  -- ID de la venta generada
    v_id_detalle   NUMBER;  -- ID de cada detalle de venta
    v_total_venta  NUMBER := 0;
    v_stock_actual NUMBER;  -- Stock actual del producto
BEGIN
    -- Generar un nuevo ID para la venta
    SELECT NVL(MAX(ID_VENTA), 0) + 1 INTO v_id_venta FROM VENTAS;

    -- Insertar la venta
    INSERT INTO VENTAS (ID_VENTA, DNI_CLIENTE, FECHA_VENTA, TOTAL_VENTA)
    VALUES (v_id_venta, p_dni_cliente, p_fecha_venta, 0);

    -- Insertar los detalles de la venta y actualizar el stock
    FOR i IN 1 .. p_productos.COUNT LOOP
        -- Verificar stock actual
        SELECT STOCK INTO v_stock_actual FROM PRENDAS WHERE ID_PRENDA = p_productos(i);

        -- Validar si hay suficiente stock
        IF v_stock_actual < p_cantidades(i) THEN
            RAISE_APPLICATION_ERROR(-20001, 'Stock insuficiente para el producto ID: ' || p_productos(i));
        END IF;

        -- Generar un nuevo ID para el detalle de venta
        SELECT NVL(MAX(ID_DETALLE), 0) + 1 INTO v_id_detalle FROM DETALLE_VENTAS;

        -- Insertar en detalle de ventas
        INSERT INTO DETALLE_VENTAS (ID_DETALLE, ID_VENTA, ID_PRENDA, CANTIDAD, PRECIO_UNITARIO)
        VALUES (v_id_detalle, v_id_venta, p_productos(i), p_cantidades(i), p_precios(i));

        -- Restar stock del producto
        UPDATE PRENDAS
        SET STOCK = STOCK - p_cantidades(i)
        WHERE ID_PRENDA = p_productos(i);

        -- Sumar al total de la venta
        v_total_venta := v_total_venta + (p_cantidades(i) * p_precios(i));
    END LOOP;

    -- Actualizar el total de la venta
    UPDATE VENTAS SET TOTAL_VENTA = v_total_venta WHERE ID_VENTA = v_id_venta;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_REGISTRAR_VENTA;

/

  GRANT EXECUTE ON "ADMIN"."SP_REGISTRAR_VENTA" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Function CALCULAR_TOTALES_GENERALES
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "ADMIN"."CALCULAR_TOTALES_GENERALES" 
RETURN SYS_REFCURSOR 
AS
    c_totales SYS_REFCURSOR;
    v_impuesto NUMBER := 0.07; -- 7% Florida
    v_tipo_cambio NUMBER := 3.7; -- Tipo de cambio
BEGIN
    OPEN c_totales FOR 
    SELECT 
        SUM(p.precio_compra * p.cantidad_comprada) AS SUBTOTAL,
        SUM(p.precio_compra * p.cantidad_comprada * (1 + v_impuesto)) AS TOTAL_USD,
        SUM(p.precio_compra * p.cantidad_comprada * (1 + v_impuesto) * v_tipo_cambio) AS TOTAL_PEN
    FROM prendas p;

    RETURN c_totales;
END;

/

  GRANT EXECUTE ON "ADMIN"."CALCULAR_TOTALES_GENERALES" TO "JEFE";
  GRANT EXECUTE ON "ADMIN"."CALCULAR_TOTALES_GENERALES" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Function CALCULAR_TOTALES_VENTAS
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "ADMIN"."CALCULAR_TOTALES_VENTAS" 
RETURN SYS_REFCURSOR 
AS
    c_totales_ventas SYS_REFCURSOR;
BEGIN
    OPEN c_totales_ventas FOR 
    SELECT 
        SUM(v.Monto_pagado) AS TOTAL_PAGADO,
        SUM(v.Total_venta) AS TOTAL_VENTAS
    FROM ventas v;

    RETURN c_totales_ventas;
END;

/

  GRANT EXECUTE ON "ADMIN"."CALCULAR_TOTALES_VENTAS" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."CALCULAR_TOTALES_VENTAS" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Function CALCULAR_TOTAL_VENTA
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "ADMIN"."CALCULAR_TOTAL_VENTA" (P_ID_VENTA NUMBER) 
RETURN NUMBER 
IS
    V_TOTAL NUMBER;
BEGIN
    SELECT TOTAL_VENTA 
    INTO V_TOTAL
    FROM VENTAS
    WHERE ID_VENTA = P_ID_VENTA;

    RETURN NVL(V_TOTAL, 0);
END;

/

  GRANT EXECUTE ON "ADMIN"."CALCULAR_TOTAL_VENTA" TO "ROL_VENDEDOR";
  GRANT EXECUTE ON "ADMIN"."CALCULAR_TOTAL_VENTA" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Function LISTADO_TABLA
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "ADMIN"."LISTADO_TABLA" 
RETURN SYS_REFCURSOR 
AS
    c_total SYS_REFCURSOR;
    v_impuesto NUMBER := 0.07; -- 7% Florida
    v_tipo_cambio NUMBER := 3.7; -- Tipo de cambio
BEGIN
    OPEN c_total FOR 
    SELECT 
        p.id_prenda,
        p.nombre,
        p.color,
        t.talla,
        p.precio_compra,              -- Movido a posición 5
        (p.precio_compra * p.cantidad_comprada) AS subtotal,
        (p.precio_compra * p.cantidad_comprada * (1 + v_impuesto)) AS TOTAL_USD,
        (p.precio_compra * p.cantidad_comprada * (1 + v_impuesto) * v_tipo_cambio) AS TOTAL_PEN,
        p.precio_venta,                -- Movido a posición 9
        p.stock,                       -- Movido a posición 10
        p.cantidad_comprada            -- Nueva posición 11
    FROM prendas p
    JOIN tallas t ON p.id_talla = t.id_talla
    order by
    p.id_prenda desc;
    RETURN c_total;
END;

/

  GRANT EXECUTE ON "ADMIN"."LISTADO_TABLA" TO "JEFE";
  GRANT EXECUTE ON "ADMIN"."LISTADO_TABLA" TO "ROL_ADMIN";
--------------------------------------------------------
--  DDL for Function LISTAR_VENTAS
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "ADMIN"."LISTAR_VENTAS" 
RETURN SYS_REFCURSOR 
AS
    p_cursor SYS_REFCURSOR;
BEGIN
    OPEN p_cursor FOR
    SELECT 
        v.ID_VENTA,
        TO_CHAR(v.FECHA_VENTA, 'DD/MM/YYYY HH24:MI') AS FECHA_FORMATEADA,
        v.TOTAL_VENTA,
        c.NOMBRE AS DNI_CLIENTE,  -- Cambio aquí
        v.ESTADO,
        v.MONTO_PAGADO,
        v.MONTO_RESTANTE,
        v.METODO_PAGO,
        NVL(TO_CHAR(v.FECHA_ENTREGA, 'DD/MM/YYYY HH24:MI'), 'SIN FECHA') AS FECHA_ENTREGA
    FROM VENTAS v
    LEFT JOIN CLIENTES c ON v.ID_CLIENTE = c.ID  -- Join agregado
    ORDER BY v.FECHA_VENTA DESC;

    RETURN p_cursor;
END;

/

  GRANT EXECUTE ON "ADMIN"."LISTAR_VENTAS" TO "ROL_ADMIN";
  GRANT EXECUTE ON "ADMIN"."LISTAR_VENTAS" TO "ROL_VENDEDOR";
--------------------------------------------------------
--  DDL for Function OBTENER_NUEVO_ID_PRENDA
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "ADMIN"."OBTENER_NUEVO_ID_PRENDA" 
RETURN NUMBER 
IS
    nuevo_id NUMBER;
BEGIN
    SELECT NVL(MAX(ID_PRENDA), 0) + 1 INTO nuevo_id 
    FROM PRENDAS;
    RETURN nuevo_id;
END;


/

  GRANT EXECUTE ON "ADMIN"."OBTENER_NUEVO_ID_PRENDA" TO "ROL_ADMIN";
--------------------------------------------------------
--  Constraints for Table TIENDAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."TIENDAS" ADD PRIMARY KEY ("ID_TIENDA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
  ALTER TABLE "ADMIN"."TIENDAS" MODIFY ("NOMBRE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table VENTAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."VENTAS" MODIFY ("FECHA_VENTA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."VENTAS" MODIFY ("TOTAL_VENTA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."VENTAS" ADD PRIMARY KEY ("ID_VENTA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table TALLAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."TALLAS" MODIFY ("TALLA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."TALLAS" ADD PRIMARY KEY ("ID_TALLA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
  ALTER TABLE "ADMIN"."TALLAS" ADD UNIQUE ("TALLA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PRENDAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("COLOR" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("ID_TALLA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("PRECIO_COMPRA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("PRECIO_VENTA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("ID_CATEGORIA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" MODIFY ("ID_TIENDA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."PRENDAS" ADD PRIMARY KEY ("ID_PRENDA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table CATEGORIAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."CATEGORIAS" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."CATEGORIAS" ADD PRIMARY KEY ("ID_CATEGORIA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table DBTOOLS$EXECUTION_HISTORY
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DBTOOLS$EXECUTION_HISTORY" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DBTOOLS$EXECUTION_HISTORY" ADD CONSTRAINT "DBTOOLS$EXECUTION_HISTORY_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table DETALLE_DEVOLUCIONES
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DETALLE_DEVOLUCIONES" MODIFY ("ID_DEVOLUCION" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DETALLE_DEVOLUCIONES" MODIFY ("ID_PRENDA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DETALLE_DEVOLUCIONES" MODIFY ("CANTIDAD" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DETALLE_DEVOLUCIONES" ADD PRIMARY KEY ("ID_DEVOLUCION", "ID_PRENDA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table DETALLE_VENTAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DETALLE_VENTAS" ADD CONSTRAINT "PK_DETALLE_VENTAS" PRIMARY KEY ("ID_VENTA", "ID_PRENDA")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
  ALTER TABLE "ADMIN"."DETALLE_VENTAS" MODIFY ("CANTIDAD" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DETALLE_VENTAS" MODIFY ("PRECIO_UNITARIO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table CLIENTES
--------------------------------------------------------

  ALTER TABLE "ADMIN"."CLIENTES" ADD CONSTRAINT "CLIENTES_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
  ALTER TABLE "ADMIN"."CLIENTES" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table USUARIOS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."USUARIOS" MODIFY ("USERNAME" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."USUARIOS" MODIFY ("PASSWORD_HASH" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."USUARIOS" MODIFY ("NOMBRE_COMPLETO" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."USUARIOS" MODIFY ("EMAIL" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."USUARIOS" MODIFY ("ROL" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."USUARIOS" ADD CONSTRAINT "CHK_ROLES" CHECK (rol IN ('admin', 'rol_vendedor', 'rol_admin')) ENABLE;
  ALTER TABLE "ADMIN"."USUARIOS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
  ALTER TABLE "ADMIN"."USUARIOS" ADD UNIQUE ("USERNAME")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
  ALTER TABLE "ADMIN"."USUARIOS" ADD UNIQUE ("EMAIL")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Constraints for Table DEVOLUCIONES
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DEVOLUCIONES" MODIFY ("ID_VENTA" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DEVOLUCIONES" MODIFY ("RAZON" NOT NULL ENABLE);
  ALTER TABLE "ADMIN"."DEVOLUCIONES" ADD PRIMARY KEY ("ID_DEVOLUCION")
  USING INDEX PCTFREE 10 INITRANS 20 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "DATA"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETALLE_DEVOLUCIONES
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DETALLE_DEVOLUCIONES" ADD FOREIGN KEY ("ID_DEVOLUCION")
	  REFERENCES "ADMIN"."DEVOLUCIONES" ("ID_DEVOLUCION") ENABLE;
  ALTER TABLE "ADMIN"."DETALLE_DEVOLUCIONES" ADD FOREIGN KEY ("ID_PRENDA")
	  REFERENCES "ADMIN"."PRENDAS" ("ID_PRENDA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETALLE_VENTAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DETALLE_VENTAS" ADD FOREIGN KEY ("ID_VENTA")
	  REFERENCES "ADMIN"."VENTAS" ("ID_VENTA") ENABLE;
  ALTER TABLE "ADMIN"."DETALLE_VENTAS" ADD FOREIGN KEY ("ID_PRENDA")
	  REFERENCES "ADMIN"."PRENDAS" ("ID_PRENDA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DEVOLUCIONES
--------------------------------------------------------

  ALTER TABLE "ADMIN"."DEVOLUCIONES" ADD FOREIGN KEY ("ID_VENTA")
	  REFERENCES "ADMIN"."VENTAS" ("ID_VENTA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PRENDAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."PRENDAS" ADD FOREIGN KEY ("ID_TALLA")
	  REFERENCES "ADMIN"."TALLAS" ("ID_TALLA") ENABLE;
  ALTER TABLE "ADMIN"."PRENDAS" ADD FOREIGN KEY ("ID_CATEGORIA")
	  REFERENCES "ADMIN"."CATEGORIAS" ("ID_CATEGORIA") ENABLE;
  ALTER TABLE "ADMIN"."PRENDAS" ADD FOREIGN KEY ("ID_TIENDA")
	  REFERENCES "ADMIN"."TIENDAS" ("ID_TIENDA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VENTAS
--------------------------------------------------------

  ALTER TABLE "ADMIN"."VENTAS" ADD CONSTRAINT "VENTAS_FK" FOREIGN KEY ("ID_CLIENTE")
	  REFERENCES "ADMIN"."CLIENTES" ("ID") ENABLE;
      
--------------------------------------------------------
--  INSERT de prueba
--------------------------------------------------------

    INSERT INTO usuarios (
        username, 
        password_hash, 
        nombre_completo, 
        email, 
        rol, 
        db_user, 
        db_password
    ) VALUES (
        'admin', 
        '$2a$12$k9bUr82TcF2uT27PBMdCw.RQN1l6QRxEiSy5lRC9FJY2LA6g98jea', -- Hash de "admin123"
        'Administrador Principal',
        'admin@tienda.com',
        'admin',
        'ADMIN',  -- Usuario de DB
        'm5ZwvukTcV4QJQtWhzhDNQ=='  -- Contraseña cifrada @admin123456
    );
