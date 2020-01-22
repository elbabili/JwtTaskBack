package co.simplon.web;

import co.simplon.entities.AppUser;
import co.simplon.service.AccountService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRestController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public AppUser register(@RequestBody UserForm userForm){
        //au lieu d'utiliser AppUser, il est préférable de passer par un objet intermédiraire pour faire un certain nb de vérif
        if(!userForm.getPassword().equals(userForm.getConfirmedPassword()))
            throw new RuntimeException("you must confirm your password");

        //Il faut maintenant gérer le cas d'un utilisateur déjà présent en base
        AppUser user = accountService.findUserByUsername(userForm.getUsername());
        if(user != null)    throw new RuntimeException("User already exist");

        AppUser appUser = new AppUser();
        appUser.setUsername(userForm.getUsername());
        appUser.setPassword(userForm.getPassword());
        accountService.saveUser(appUser);
        accountService.addRoleToUser(appUser.getUsername(),"USER");
        return appUser;
    }
}

@Data
class UserForm{
    private String username;
    private String password;
    private String confirmedPassword;
}
