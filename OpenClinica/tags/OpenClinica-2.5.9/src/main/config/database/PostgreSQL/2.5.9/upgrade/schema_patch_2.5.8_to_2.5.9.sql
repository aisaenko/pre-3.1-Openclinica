--
-- database changes
--

-- update resolution_status_id of parent note

update discrepancy_note set resolution_status_id = a.resolution_status_id
from ( select dn_a.parent_dn_id, dn_a.resolution_status_id from discrepancy_note dn, discrepancy_note dn_a
       where dn.parent_dn_id is null and dn_a.discrepancy_note_id =
	(select max(dn_b.discrepancy_note_id) from discrepancy_note dn_b
	where dn_b.parent_dn_id = dn.discrepancy_note_id and dn_b.discrepancy_note_type_id = dn.discrepancy_note_type_id)
) a
where discrepancy_note_id = a.parent_dn_id