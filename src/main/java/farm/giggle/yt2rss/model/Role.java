package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_generator")
    @SequenceGenerator(name="roles_generator", sequenceName = "roles_SEQ", allocationSize=1)
    private Long Id;
    @Column(length = RoleEnum.MAX_LEN_ROLE_NAME)
    @Enumerated(EnumType.STRING)
    private RoleEnum roleName;

    @Version
    private int version;

    public Role(RoleEnum roleName) {
        this.roleName = roleName;
    }

    @Getter
    public enum RoleEnum {
        USER_MANAGEMENT("ROLE_USER_MANAGEMENT"), ORDINARY_USER("ROLE_ORDINARY_USER");

        public static final int MAX_LEN_ROLE_NAME = 32;

        private final String roleName;

        RoleEnum(String roleName) {
            this.roleName = roleName;
        }
    }
}
