-- select the dates in item data table
select id.item_data_id,id.value from item_data as id, item as i, item_data_type as idt 
where 
id.item_id = i.item_id and 
i.item_data_type_id = idt.item_data_type_id and
idt.code = 'DATE'

-- Flip dates from 12/01/2008 to 01/12/2008
update item_data as id set value= 
split_part(id.value,'/',2) || '/' || split_part(id.value,'/',1) ||  '/' || split_part(id.value,'/',3)
from 
item as i, item_data_type as idt 
where 
id.item_id = i.item_id and 
i.item_data_type_id = idt.item_data_type_id and
idt.code = 'DATE' and length(id.value) = 10