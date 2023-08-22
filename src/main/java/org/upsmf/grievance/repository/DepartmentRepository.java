package org.upsmf.grievance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
