package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static farm.giggle.yt2rss.model.Role.RoleEnum.ADMIN;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "UNAME", nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            length = Auth2ProviderEnum.MAX_LEN_PROVIDER_NAME)
    private Auth2ProviderEnum auth2Provider;
    @Column(nullable = false,
            length = Auth2ProviderEnum.MAX_LEN_PROVIDER_ID)
    private String auth2Id;

    @Column(nullable = false,
            unique = true)
    private UUID uuid;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    public User() {
    }

    public User(String name, Auth2ProviderEnum auth2Provider, String auth2Id, UUID uuid) {
        this.name = name;
        this.auth2Provider = auth2Provider;
        this.auth2Id = auth2Id;
        this.uuid = uuid;
    }

    public void addRole(@NonNull Role role) {
        userRoles.add(new UserRole(this, role));
    }

    public void removeRole(Role role) {
        Iterator<UserRole> iterator = userRoles.iterator();
        while (iterator.hasNext()) {
            UserRole userRole = iterator.next();
            if ((userRole.getUser().equals(this) &&
                    (userRole.getRole().equals(role)))) {
                iterator.remove();
            }
        }
    }

    public boolean isAdmin() {
        for (UserRole userRole : userRoles) {
            if (userRole.getRole().getRoleName().equals(ADMIN)) return true;
        }
        return false;
    }
}