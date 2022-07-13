package org.defichain.portfolio

import dev.misfitlabs.kotlinguice4.KotlinModule
import org.defichain.portfolio.settings.JavaSystemWrapper
import org.defichain.portfolio.settings.SystemWrapper

class DefichainPortfolioModule : KotlinModule() {
    override fun configure() {
        bind<SystemWrapper>().to<JavaSystemWrapper>()
    }
}