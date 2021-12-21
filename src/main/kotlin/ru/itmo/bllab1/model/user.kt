package ru.itmo.bllab1.model
import javax.persistence.*

@Entity
class EUser(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        var login: String = "",
        var password: String = "",
        @OneToOne(cascade = [CascadeType.ALL])
        var client: Client? = null,
        @OneToOne(cascade = [CascadeType.ALL])
        var shop: Shop? = null,
        @OneToOne(cascade = [CascadeType.ALL])
        var admin: Admin? = null,
        @ManyToMany(fetch = FetchType.EAGER)
        var roles: Set<Role> = emptySet(),
        var registrationFromWebSite: Boolean = false
)

data class RegisterUserRequest(
        val login: String,
        val password: String,
        val firstName: String,
        val lastName: String
)

data class LoginRequest(
        val login: String,
        val password: String
)
