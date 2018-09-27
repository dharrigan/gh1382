package com.dharrigan.gh1382.stream

import com.dharrigan.gh1382.config.KafkaBindings
import com.dharrigan.gh1382.config.KafkaBindings.Companion.CONTRACT_CREATE
import com.dharrigan.gh1382.model.ContractCreate
import mu.KotlinLogging
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.StandardIntegrationFlow
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import javax.validation.Valid

private val LOGGER = KotlinLogging.logger { }

@Configuration
@EnableBinding(KafkaBindings::class)
class StreamConfig {

    @StreamListener(CONTRACT_CREATE)
    fun contractCreate(@Valid contractCreate: ContractCreate) {
        LOGGER.info { "Received [$contractCreate]." }
    }

    @Bean
    fun errorFlow(): StandardIntegrationFlow = IntegrationFlows.from("errorChannel")
        .routeByException {
            it.subFlowMapping(MethodArgumentNotValidException::class.java) { subFlow ->
                subFlow.handle { messageHandler ->
                    (messageHandler.payload as? MethodArgumentNotValidException)?.let { manve ->
                        manve.bindingResult?.fieldErrors?.forEach { fieldError ->
                            LOGGER.error { "[${fieldError.objectName}.${fieldError.field}] rejected [${fieldError.rejectedValue}] because [${fieldError.defaultMessage}]." }
                        }
                    }
                }
            }
        }.get()

}
