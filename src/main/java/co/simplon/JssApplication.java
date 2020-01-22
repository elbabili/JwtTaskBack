package co.simplon;

import co.simplon.dao.TaskRepository;
import co.simplon.entities.AppRole;
import co.simplon.entities.AppUser;
import co.simplon.entities.Task;
import co.simplon.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JssApplication implements CommandLineRunner {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(JssApplication.class, args);
	}

	@Bean   //sera executé par Spring au boot de l'appli delors on pourra l'injecter ailleurs (SecurityConfig par ex)
	public BCryptPasswordEncoder getBCryptPasswordEncoder(){
	    return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
	    accountService.saveUser(new AppUser(null,"admin","1234",null));
        accountService.saveUser(new AppUser(null,"user","1234",null));

        accountService.saveRole(new AppRole(null,"ADMIN"));
        accountService.saveRole(new AppRole(null,"USER"));

        accountService.addRoleToUser("admin","ADMIN");
        accountService.addRoleToUser("admin","USER");
        accountService.addRoleToUser("user","USER");

        taskRepository.save(new Task(null,"première"));
        taskRepository.save(new Task(null,"seconde"));
        taskRepository.save(new Task(null,"troisième"));
    }
}
