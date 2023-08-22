package org.upsmf.grievance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
