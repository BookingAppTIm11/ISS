package rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.domain.User;

@Repository
public interface IUserRepository extends JpaRepository<User,String> {

//    boolean isLoginValid(String email, String password);
}
