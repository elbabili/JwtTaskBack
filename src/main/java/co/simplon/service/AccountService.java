package co.simplon.service;

import co.simplon.entities.AppRole;
import co.simplon.entities.AppUser;

public interface AccountService {
    public AppUser saveUser(AppUser user);
    public AppRole saveRole(AppRole role);
    public void addRoleToUser(String username,String rolename);
    public AppUser findUserByUsername(String username);
}