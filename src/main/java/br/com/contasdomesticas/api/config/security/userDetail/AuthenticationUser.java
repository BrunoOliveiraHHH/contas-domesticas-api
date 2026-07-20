package br.com.contasdomesticas.api.config.security.userDetail;

import br.com.contasdomesticas.api.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthenticationUser implements UserDetails {

    private static final long serialVersionUID = 1L;
    private final Usuario usuario;

    public AuthenticationUser(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getLogin();
    }

    public String getNomeExibicao() {
        return usuario.getNomeExibicao();
    }
}
