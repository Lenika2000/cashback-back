package ru.itmo.bllab1.repo

import Client
import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface ClientRepository : CrudRepository<Client, Long>
