package org.upsmf.grievance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.Comments;

@Repository
public interface CommentRepository extends CrudRepository<Comments, Long> {
}
