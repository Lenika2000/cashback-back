package ru.itmo.bllab1.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import ru.itmo.bllab1.controller.CashbackChangeRequestPayload
import ru.itmo.bllab1.model.*
import ru.itmo.bllab1.repo.*
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.EntityNotFoundException

const val KAFKA_CASHBACK_TOPIC = "cashback";

@Service
class CashbackService(
        private val cashbackRepository: CashbackRepository,
        private val messageService: MessageService,
        private val clientRepository: ClientRepository,
        private val template: TransactionTemplate,
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val shopRepository: ShopRepository
) {


    // каждый день в 15.00
    @Scheduled(cron = "0 0 15 * * ?")
    fun processCashback() {
        template.execute {
            cashbackRepository.findByCreationDateLessThan(LocalDateTime.now().minusDays(60)).forEach { cashback ->
                if (!cashback.shop.name.isEmpty() && !cashback.client.firstName.isEmpty()) {
                    if (cashback.status != CashbackStatus.NEW && cashback.isPaid && cashback.isOrderCompleted && cashback.confirmPayment && cashback.cashbackSum / 0.9 <= cashback.shopPayment) {
                        cashback.status = CashbackStatus.APPROVED
                        cashback.client.balance += cashback.shopPayment * 0.9
                        clientRepository.save(cashback.client)
                        messageService.sendNotificationToClient("Кэшбек зачислен", cashback.client)
                    } else {
                        cashback.status = CashbackStatus.REJECTED
                        messageService.sendNotificationToClient("Кэшбек отклонен", cashback.client)
                    }
                    cashbackRepository.save(cashback)
                }
            }
        }
    }

    fun updateCashback(payload: CashbackChangeRequestPayload) {
        template.execute {
            val cashback = cashbackRepository.findById(payload.cashbackId).orElseThrow {
                EntityNotFoundException("Кэшбек с id ${payload.cashbackId} не найден!")
            }
            payload.isPaid?.let { cashback.isPaid = it }
            payload.isOrderCompleted?.let { cashback.isOrderCompleted = it }
            payload.payment?.let {
                cashback.confirmPayment = true
                cashback.shopPayment = it
            }
            if (cashback.status == CashbackStatus.NEW)
                cashback.status = CashbackStatus.RECEIVED_INF
            cashbackRepository.save(cashback)
        }
    }


    @KafkaListener(id = "generated_cashback", topics = [KAFKA_CASHBACK_TOPIC], containerFactory = "singleFactory")
    fun receiveCashbackFromKafka(cashback: CashbackFromGenerator) {
        println("receieve")
        // клиент с таким логином еще не существует в базе
        if (!userRepository.findByLogin(cashback.clientLogin).isPresent) {
            // создаем шаблон клиента, при регистрации он заполнится нужными данными
            val client = Client(0, "", "")
            val user = EUser(
                    0, cashback.clientLogin, "", client, null, null,
                    setOf(roleRepository.findRoleByName(ERole.ROLE_CLIENT).get())
            )
            client.eUser = user
            userRepository.save(user)
            clientRepository.save(client)
        }
        // магазин с таким именем еще не существует в базе
        if (!userRepository.findByLogin(cashback.shopLogin).isPresent) {
            // создаем шаблон магазина, при регистрации он заполнится нужными данными
            val shop = Shop(0, "")
            val user = EUser(
                    0, cashback.shopLogin, "", null, shop, null,
                    setOf(roleRepository.findRoleByName(ERole.ROLE_SHOP).get())
            )
            shop.eUser = user
            userRepository.save(user)
            shopRepository.save(shop)
        }
        val shop = userRepository.findByLogin(cashback.shopLogin).get().shop;
        val client = userRepository.findByLogin(cashback.clientLogin).get().client;
        val creationDate = LocalDateTime.ofInstant(cashback.creationDate.toInstant(), ZoneId.systemDefault());
        val cashback = Cashback(0, client = client!!, shop = shop!!, creationDate = creationDate, productName = cashback.productName, productPrice = cashback.productPrice, cashbackSum = Math.ceil(cashback.productPrice * 0.05));
        cashbackRepository.save(cashback)
    }
}
