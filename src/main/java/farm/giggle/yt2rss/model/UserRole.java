package farm.giggle.yt2rss.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "USER_ROLES")
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @Getter
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Getter
    @ManyToOne
    @MapsId("userId")
    private User user;
    @Getter
    @ManyToOne
    @MapsId("roleId")
    private Role role;

    @Version
    private int version;

    public UserRole() {
    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.id = new UserRoleId(user.getId(), role.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole userRole)) return false;

        return Objects.equals(user, userRole.getUser()) &&
                Objects.equals(role, userRole.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }

    @Embeddable
    public static class UserRoleId implements Serializable {
        @Column(name = "USER_ID")
        private Long userId;
        @Column(name = "ROLE_ID")
        private Long roleId;

        public UserRoleId() {
        }

        public UserRoleId(Long userId, Long roleId) {
            this.userId = userId;
            this.roleId = roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserRoleId id = (UserRoleId) o;

            if (!userId.equals(id.userId)) return false;
            return roleId.equals(id.roleId);
        }

        @Override
        public int hashCode() {
            int result = userId.hashCode();
            result = 31 * result + roleId.hashCode();
            return result;
        }
    }
}
