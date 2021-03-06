package ru.itmo.bllab1.model
import javax.persistence.*

@Entity
data class Role(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Enumerated(EnumType.STRING)
        val name: ERole = ERole.ROLE_CLIENT,
)

enum class ERole {
    ROLE_CLIENT,
    ROLE_SHOP,
    ROLE_ADMIN
}
