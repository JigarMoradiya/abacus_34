package com.jigar.me

import com.jigar.me.utils.GrantKind
import com.jigar.me.utils.GrantedPlan
import com.jigar.me.utils.RevenueCatHelper
import com.jigar.me.utils.toDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

/** A RevenueCat promotional grant (dashboard > Customers > Grant Entitlement) shows the plan it stands for as "Assigned". */
class RevenueCatGrantTest {
    @Test fun monthlyYearlyAndLifetimeGrantsStandForAPlan() {
        assertEquals(GrantKind.MONTHLY, RevenueCatHelper.grantKindOf("rc_promo_premium_monthly"))
        assertEquals(GrantKind.YEARLY, RevenueCatHelper.grantKindOf("rc_promo_premium_yearly"))
        assertEquals(GrantKind.LIFETIME, RevenueCatHelper.grantKindOf("rc_promo_premium_lifetime"))
    }

    @Test fun otherDurationsStandForNoPlan() {
        for (id in listOf("rc_promo_premium_daily", "rc_promo_premium_three_day", "rc_promo_premium_weekly",
            "rc_promo_premium_two_month", "rc_promo_premium_three_month", "rc_promo_premium_six_month", "rc_promo_premium_custom")) {
            assertNull(id, RevenueCatHelper.grantKindOf(id))
        }
    }

    @Test fun aStoreProductIsNoGrant() {
        assertNull(RevenueCatHelper.grantKindOf("com.abacus.puzzle.1month"))
        assertNull(RevenueCatHelper.grantKindOf("com.abacus.puzzle.1year"))
        // the lifetime SKU has no "lifetime" in its id: a real purchase is never read as a grant (grants are told apart by the store, too)
        assertNull(RevenueCatHelper.grantKindOf("com.abacus.all"))
    }

    @Test fun aGrantBecomesTheDataOfAnAssignedPlanSoEveryCardShowsIt() {
        val since = Date(1_732_406_400_000L)   // 24 Nov 2024 00:00 UTC
        val year = RevenueCatHelper.assignedDataOf(GrantedPlan(GrantKind.YEARLY, since, null))
        assertEquals("com.abacus.puzzle.1year", year.google_plan_id)
        assertNull(year.google_order_id)   // an admin-assigned plan has no store order: so has a grant
        assertNotNull(year.start_date)
        // the cards read the date back with the same pattern it was written with: the same moment comes out
        assertEquals(since.time, year.start_date!!.toDate().time)
        assertEquals("com.abacus.puzzle.1month", RevenueCatHelper.assignedDataOf(GrantedPlan(GrantKind.MONTHLY, since, null)).google_plan_id)
        assertEquals("all", RevenueCatHelper.assignedDataOf(GrantedPlan(GrantKind.LIFETIME, since, null)).google_plan_id)
    }
}
