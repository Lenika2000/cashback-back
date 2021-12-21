package ru.itmo.bllab1.model
import javax.persistence.*

@Entity
class Client(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        @Column(name = "first_name")
        var firstName: String = "",
        @Column(name = "last_name")
        var lastName: String = "",
        var balance: Double = 0.0,
        @OneToOne(mappedBy = "client")
        var eUser: EUser = EUser()
)


data class ClientData(
        val id: Long,
        val firstName: String,
        val lastName: String,
        val login: String,
        val balance: Number
)
