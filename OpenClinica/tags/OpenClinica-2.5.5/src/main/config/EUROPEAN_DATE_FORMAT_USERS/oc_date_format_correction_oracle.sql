-- Select all items with type date
select item_data.item_data_id,item_data.value
  from item_data, item  i, item_data_type idt 
  where 
  item_data.item_id = i.item_id and 
  i.item_data_type_id = idt.item_data_type_id and
  idt.code = 'DATE';

-- Flip dates from 12/01/2008 to 01/12/2008
update item_data  set item_data.value=
( select
substr(item_data.value,4,2) || '/' ||  
substr(item_data.value,0,2) || '/' ||
substr(item_data.value,7,4)
from 
item, item_data_type
where 
item_data.item_id = item.item_id and 
item.item_data_type_id = item_data_type.item_data_type_id and
item_data_type.code = 'DATE' ) where item_data.value is not null and length(item_data.value) = 10;