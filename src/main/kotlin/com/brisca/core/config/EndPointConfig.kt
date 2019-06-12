package com.brisca.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

open class EndPointConfig {
    open var baseUrl: String = ""
    open var svcId: String = ""
    open var svcPassword: String = ""
    open var healthCheckUrl: String = ""
    open var connectTimeoutInSecs: Int = 5
    open var readTimeoutInSecs: Int = 5
    open var appId: String = ""
    open var environment: String = ""
    open var retryCount: Long = 0
}

@ConfigurationProperties(prefix = "cosmos-client")
class CosmosConfig : EndPointConfig()
