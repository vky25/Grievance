package org.upsmf.grievance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.AssigneeTicketAttachment;

@Repository
public interface AssigneeTicketAttachmentRepository extends CrudRepository<AssigneeTicketAttachment, Long> {
}
