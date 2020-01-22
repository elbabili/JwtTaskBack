package co.simplon.service;

import co.simplon.dao.RoleRepository;
import co.simplon.dao.UserRepository;
import co.simplon.entities.AppRole;
import co.simplon.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional      //spring !
public class AccountServiceImpl implements AccountService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public AppUser saveUser(AppUser user) {
        //lorsqu'on souhaite sauvegarder un utilisateur, il faut crypter le mot de pass qu'il a saisi
        String hashPW = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashPW);
        return userRepository.save(user);
    }

    @Override
    public AppRole saveRole(AppRole role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        AppRole role = roleRepository.findByRoleName(rolename);
        AppUser user = userRepository.findByUsername(username);
        user.getRoles().add(role);
        //puisque la méthode est transactionnel, des qu'il y a un commit, il y a ajout en base
    }

    @Override
    public AppUser findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
