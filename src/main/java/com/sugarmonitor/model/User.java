package com.sugarmonitor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document("users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable, UserDetails {

  @Id private String id;

  private String username;

  private String password;

  @Field("roles")
  @JsonProperty("roles")
  private Set<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getRoles();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public boolean isAdmin() {
    if (roles != null) {
      return roles.contains(Role.ADMIN);
    }
    return false;
  }

  public boolean isUser() {
    if (roles != null) {
      return roles.contains(Role.USER);
    }
    return false;
  }
}
