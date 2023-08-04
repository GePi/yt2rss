package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static farm.giggle.yt2rss.model.Role.RoleEnum.ADMIN;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "uname", nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Auth2ProviderEnum auth2Provider;
    @Column(nullable = false)
    private String auth2Id;
    @Column
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<Role.RoleEnum, Role> roleMap = new HashMap();
    //private List<Role> roles = new ArrayList<>();

    public Role addRole(Role role) {
        if (roleMap.containsKey(role.getRoleName())) {
            return roleMap.get(role.getRoleName());
        }
        roleMap.put(role.getRoleName(), role);
        role.setUser(this);
        return role;
    }

    public void removeRole(Role role) {
        roleMap.remove(role.getRoleName());
        role.setUser(null);
    }

    public boolean isAdmin() {
        return roleMap.containsKey(ADMIN);
    }
  /*  public Role addRole(Role role) {
        for (var existRole : roles) {
            if (existRole.getRoleName() == role.getRoleName()) {
                return existRole;
            }
        }
        roles.add(role);
        role.setUser(this);
        return role;
    }

    public void removeRole(Role role) {
        for (Iterator<Role> roleIterator = roles.iterator(); roleIterator.hasNext(); ) {
            Role existRole = roleIterator.next();
            if (existRole.getRoleName() == role.getRoleName()) {
                roleIterator.remove();
                role.setUser(null);
                return;
            }
        }
    }*/
}