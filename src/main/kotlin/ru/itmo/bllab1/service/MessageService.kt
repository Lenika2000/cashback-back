package ru.itmo.bllab1.service

import org.springframework.stereotype.Service
import ru.itmo.bllab1.model.Client

interface MessageService {
    fun sendNotificationToClient(notification: String, client: Client)
}

@Service
class MessageServiceStub : MessageService {
    override fun sendNotificationToClient(notification: String, client: Client) {}
}
