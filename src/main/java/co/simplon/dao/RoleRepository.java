package co.simplon.dao;

import co.simplon.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@RepositoryRestResource
public interface RoleRepository extends JpaRepository<AppRole,Long> {
    public AppRole findByRoleName(String roleName);
}