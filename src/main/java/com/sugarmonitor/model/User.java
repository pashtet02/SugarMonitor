package com.sugarmonitor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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

  @NotBlank(message = "Username must be provided")
  @Size(min = 4, max = 16, message = "Username must be in range 4 and 16 characters")
  private String username;

  @NotBlank(message = "Password must be provided")
  @Size(min = 4, max = 32, message = "Password must be in range 4 and 32 characters")
  private String password;

  @Transient private String confirmPassword;

  @Field("roles")
  @JsonProperty("roles")
  private Set<Role> roles;

  private boolean enabled;

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
    return enabled;
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
