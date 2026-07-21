package io.github.oni0nfr1.skid.client.api.events;

import io.github.oni0nfr1.skid.client.api.events.unstable.KartAttrModifierEvents;

final class JavaKartAttrEventTypeTest {
    private JavaKartAttrEventTypeTest() {
    }

    static void registerAttrEvents() {
        KartAttrEvents.UPDATE.register((saddle, base, modifiers) -> { });
        KartAttrModifierEvents.ID_ENGINE.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.ID_ENGINE_REAL.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.CTX_MAX_LAP.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.CTX_CURRENT_LAP.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.CAN_IBOOST.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.STATE_IBOOST.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.STATE_DRIFTING.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.STATE_NITRO.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.CAP_NITRO_COUNT.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.STATE_DRAFT_ACCEL.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.STATE_TEAM_NITRO_COUNT.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.CTX_PERF_LIMIT.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.ID_TIRE.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.ID_BODY_TYPE.register((saddle, previousValue, value) -> { });
        KartAttrModifierEvents.STATE_MODEL_ROTATION_ALLOWED.register((saddle, previousValue, value) -> { });
    }
}
