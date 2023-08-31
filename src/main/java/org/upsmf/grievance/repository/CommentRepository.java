package org.upsmf.grievance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.Comments;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comments, Long> {
    List<Comments> findAllByTicketId(Long ticketId);
}
