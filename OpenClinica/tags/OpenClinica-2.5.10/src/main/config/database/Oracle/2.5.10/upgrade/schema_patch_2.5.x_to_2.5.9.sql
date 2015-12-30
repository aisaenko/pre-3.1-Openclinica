--
-- database changes
--

-- update resolution_status_id of parent note

update discrepancy_note d set d.resolution_status_id = (
				select dn_a.resolution_status_id from discrepancy_note dn, discrepancy_note dn_a
       			   where dn.parent_dn_id is null and dn_a.discrepancy_note_id =
                		(select max(dn_b.discrepancy_note_id) from discrepancy_note dn_b
                  		where dn_b.parent_dn_id = dn.discrepancy_note_id and dn_b.discrepancy_note_type_id = dn.discrepancy_note_type_id)
               	   and dn_a.parent_dn_id = d.discrepancy_note_id
                )
where d.parent_dn_id is null