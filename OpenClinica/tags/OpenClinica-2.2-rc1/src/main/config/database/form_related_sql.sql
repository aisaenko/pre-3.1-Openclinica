CREATE TABLE form_group (
    group_id serial NOT NULL,
    group_label character varying(255) NOT NULL,
    group_header character varying(255) NULL,
    group_subheader character varying(255) NULL,
    group_layout character varying(100) NULL,
    group_repeat_number numeric NULL,
    group_repeat_max numeric NULL,
    group_repeat_array character varying(255) NULL,
    group_row_start_number numeric NULL,
    CONSTRAINT pk_form_group PRIMARY KEY (group_id)
    
);

CREATE TABLE form_group_item_map (
    group_id numeric NOT NULL,
    item_id numeric NOT NULL,
    CONSTRAINT fk_item_form_group FOREIGN KEY (item_id)
      REFERENCES item (item_id),
    CONSTRAINT fk_form_group_item FOREIGN KEY (group_id)
      REFERENCES form_group (group_id)
);

alter table item_data add ordinal numeric null;
alter table form_group add ordinal numeric null;