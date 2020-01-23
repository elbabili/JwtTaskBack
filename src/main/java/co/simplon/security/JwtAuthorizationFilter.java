package co.simplon.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //permet les accès de domaines différent du back
        response.setHeader("Access-Control-Allow-Origin","*");

        //Tous les headers autorisés
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, Accept, X-Requested-With, Content-Type, " +
                "Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");

        //Tous les headers exposés donc visible côté front
        response.setHeader("Access-Control-Expose-Headers",
                "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, Authorization");

        if(request.getMethod().equals("OPTIONS")){  //si la requete contient une OPTION renvoyer OK -- côté front : Authorization
            response.setStatus(HttpServletResponse.SC_OK);  //Status code (200) indicating the request succeeded normally
            System.out.println("Options");
        }
        else {
            //sinon verifier s'il y a présence d'un token
            String jwt = request.getHeader(SecurityConstants.HEADER_STRING); //Authorization

            System.out.println("+++++"+jwt);
            if(jwt == null || !jwt.startsWith(SecurityConstants.TOKEN_PREFIX)){
                filterChain.doFilter(request,response); //passe au filtre suivant, pas de token présent ici
                return;
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)    //signature avec la mienne
                    .parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX,"")) //retrait du Bearer
                    .getBody();
            String username = claims.getSubject();  //admin
            ArrayList<Map<String,String>> roles = (ArrayList<Map<String,String>>) claims.get("roles");    // "roles": [  { "authority": "ADMIN" ...
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            roles.forEach(r->{
                authorities.add(new SimpleGrantedAuthority(r.get("authority")));
            });

            //vérification du token qui a été envoyé
            UsernamePasswordAuthenticationToken authenticationUser = new UsernamePasswordAuthenticationToken(username,null, authorities);

            //chargement du contexte de sécurité de spring
            SecurityContextHolder.getContext().setAuthentication(authenticationUser);  //chargement de l'utilisateur authentifié dans le contexte de sécurité
            //manière de dire à spring, la personne qui s'est authentifié, voilà son identité, son username et ses roles...
            filterChain.doFilter(request,response);
        }
    }
}
