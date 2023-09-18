package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;
    @Column(length = RoleEnum.MAX_LEN_ROLE_NAME)
    @Enumerated(EnumType.STRING)
    private RoleEnum roleName;

    static public Role from(RoleEnum roleKey) {
        Role role = new Role();
        role.setRoleName(roleKey);
        return role;
    }

    public enum RoleEnum {
        ADMIN("Admin"), USER("User");

        public static final int MAX_LEN_ROLE_NAME = 32;

        private final String description;

        RoleEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
