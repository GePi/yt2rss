package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long Id;
    @ManyToOne
    private User user;
    @Column
    @Enumerated(EnumType.STRING)
    private RoleEnum roleName;

    static public Role from(RoleEnum roleKey) {
        Role role = new Role();
        role.setRoleName(roleKey);
        return role;
    }

    public enum RoleEnum {
        ADMIN("SU"), USER("User");

        private final String description;

        RoleEnum(String description) {
            this.description = description;
        }

    }
}
