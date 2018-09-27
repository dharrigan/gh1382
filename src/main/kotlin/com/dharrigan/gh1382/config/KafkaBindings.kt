package com.dharrigan.gh1382.config

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

@Suppress("unused")
interface KafkaBindings {

    @Input(CONTRACT_CREATE)
    fun contractCreate(): SubscribableChannel

    companion object {
        const val CONTRACT_CREATE = "contractCreate"
    }

}
