package org.defichain.portfolio

import org.defichain.portfolio.walletinfo.OceanAPICaller
import org.junit.Assert.assertThat
import org.testng.annotations.Test
import org.hamcrest.CoreMatchers.*

class OceanAPICallerTest {
    val defichainBurnAddress = "8defichainBurnAddressXXXXXXXdRQkSm"
    @Test
    fun testAggregate(){
        val oceanAPICaller = OceanAPICaller()

        val aggregation = oceanAPICaller.getAggregation(defichainBurnAddress)

        assertThat(aggregation.id, `is`("001f2752e70680db7f28c22b9ef50a72b923e8991488a50f8b104022d92c42ebad7ee175"))
    }
}