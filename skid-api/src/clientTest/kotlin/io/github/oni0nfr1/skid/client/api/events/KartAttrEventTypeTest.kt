package io.github.oni0nfr1.skid.client.api.events

import io.github.oni0nfr1.skid.client.api.events.unstable.KartAttrModifierEvents

@Suppress("unused")
private fun attrEventsCanBeRegistered() {
    KartAttrEvents.UPDATE.register { _, _, _ -> }
    KartAttrModifierEvents.ID_ENGINE.register { _, _, _ -> }
    KartAttrModifierEvents.ID_ENGINE_REAL.register { _, _, _ -> }
    KartAttrModifierEvents.CTX_MAX_LAP.register { _, _, _ -> }
    KartAttrModifierEvents.CTX_CURRENT_LAP.register { _, _, _ -> }
    KartAttrModifierEvents.CAN_IBOOST.register { _, _, _ -> }
    KartAttrModifierEvents.STATE_IBOOST.register { _, _, _ -> }
    KartAttrModifierEvents.STATE_DRIFTING.register { _, _, _ -> }
    KartAttrModifierEvents.STATE_NITRO.register { _, _, _ -> }
    KartAttrModifierEvents.CAP_NITRO_COUNT.register { _, _, _ -> }
    KartAttrModifierEvents.STATE_DRAFT_ACCEL.register { _, _, _ -> }
    KartAttrModifierEvents.STATE_TEAM_NITRO_COUNT.register { _, _, _ -> }
    KartAttrModifierEvents.CTX_PERF_LIMIT.register { _, _, _ -> }
    KartAttrModifierEvents.ID_TIRE.register { _, _, _ -> }
    KartAttrModifierEvents.ID_BODY_TYPE.register { _, _, _ -> }
    KartAttrModifierEvents.STATE_MODEL_ROTATION_ALLOWED.register { _, _, _ -> }
}
