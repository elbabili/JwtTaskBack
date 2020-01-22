package co.simplon.security;

import co.simplon.entities.AppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        super();
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        AppUser appUser = null;
        //nous souhaitons ici lire les infos d'un objet json pour les stocker dans un objet java désérialisé
        try {
            appUser = new ObjectMapper().readValue(request.getInputStream(),AppUser.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
   //     System.out.println("*************");
   //     System.out.println(appUser.getUsername());
   //     System.out.println(appUser.getPassword());
        return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(appUser.getUsername(),appUser.getPassword()));
    }

    @Override       // une fois authentifié spring security fait appel à cette méthode                //Filtre de spring security
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        User springUser = (User)authResult.getPrincipal();

        //Génération du token : HEADER +
        String jwtToken = Jwts.builder()
                .setSubject(springUser.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256,SecurityConstants.SECRET)
                .claim("roles",springUser.getAuthorities())
                .compact();

        //une fois le token crée, il faut le rajouter a response, qui sera repris par spring
        response.addHeader(SecurityConstants.HEADER_STRING,SecurityConstants.TOKEN_PREFIX + jwtToken);
        //super.successfulAuthentication(request, response, chain, authResult);
    }
}
