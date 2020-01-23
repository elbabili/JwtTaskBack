package co.simplon.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableWebSecurity	//active notre stratégie de sécurité ici
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //		Version d'authentification en mémoire
    //@Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      //auth.inMemoryAuthentication().withUser("admin").password("{noop}12345").roles("ADMIN","USER");
      //auth.inMemoryAuthentication().withUser("user").password("{noop}12345").roles("USER");
    //}

    //		Version service
    @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        //l'utilisateur qui souhaite s'authentifier va saisir un mot de passe qui sera crypté pour être comparé au mot de passe stocké en base
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();  //Désactiver la génération automatique du synchronized token
        //http.formLogin();       //formulaire fournit par spring

        //Désactiver l'authentification basée sur les sessions -> demander à Spring d'utiliser le mode stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers("/login/**","/register/**").permitAll();
        //liste de toutes les actions permises, en effet, ne pas permettre l'accès à login peut rendre l'appli inutile

        http.authorizeRequests().antMatchers(HttpMethod.POST,"/tasks/**").hasAuthority("ADMIN");
        //s'agissant des requetes en post, seule ceux qui sont ADMIN peuvent le réaliser

        http.authorizeRequests().anyRequest().authenticated();
        //indique que toutes les ressources de l'appli necessiste une authentification

        http.addFilter(new JwtAuthenticationFilter(authenticationManager()));
        // ajout du filtre d'authentification jwt à l'aide de l'objet principale d'authentification de spring securité

        //ajout du filtre d'authorization qui va recevoir toutes les requetes
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:4200");
    }
}
