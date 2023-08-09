package org.upsmf.grievance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.RaiserTicketAttachment;

@Repository
public interface RaiserTicketAttachmentRepository extends CrudRepository<RaiserTicketAttachment, Long> {
}
