package ru.itmo.bllab1.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import ru.itmo.bllab1.model.CashbackFromGenerator

@Configuration
class KafkaConsumerConfig {
    @Value("\${kafka.server}")
    private val kafkaServer: String? = null

    @Value("\${kafka.group.id}")
    private val kafkaGroupId: String? = null
    @Bean
    fun batchFactory(): KafkaListenerContainerFactory<*> {
        val factory: ConcurrentKafkaListenerContainerFactory<Long, CashbackFromGenerator> = ConcurrentKafkaListenerContainerFactory<Long, CashbackFromGenerator>()
        factory.setConsumerFactory(consumerFactory())
        factory.setBatchListener(true)
        factory.setMessageConverter(BatchMessagingMessageConverter(converter()))
        return factory
    }

    @Bean
    fun singleFactory(): KafkaListenerContainerFactory<*> {
        val factory: ConcurrentKafkaListenerContainerFactory<Long, CashbackFromGenerator> = ConcurrentKafkaListenerContainerFactory<Long, CashbackFromGenerator>()
        factory.setConsumerFactory(consumerFactory())
        factory.setBatchListener(false)
        factory.setMessageConverter(StringJsonMessageConverter())
        return factory
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<Long, CashbackFromGenerator> {
        return DefaultKafkaConsumerFactory<Long, CashbackFromGenerator>(consumerConfigs())
    }

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<*> {
        return ConcurrentKafkaListenerContainerFactory<Any, Any>()
    }

    @Bean
    fun consumerConfigs(): Map<String, Any?> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServer
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = LongDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = kafkaGroupId
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
        return props
    }

    @Bean
    fun converter(): StringJsonMessageConverter {
        return StringJsonMessageConverter()
    }
}
