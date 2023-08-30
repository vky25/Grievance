package org.upsmf.grievance.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.User;
@Repository("userRepository")
public interface UserRepository extends PagingAndSortingRepository<User,Long> {

    User findByUsername(String username);
    User findByEmail(String email);
}
