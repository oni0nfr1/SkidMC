package io.github.oni0nfr1.skid.client.api.kart;

import io.github.oni0nfr1.skid.client.api.engine.XEngine;
import io.github.oni0nfr1.skid.client.api.tachometer.XTachometer;
import io.github.oni0nfr1.skid.client.api.utils.KartType;
import io.github.oni0nfr1.skid.client.api.utils.Ref;

import java.util.List;
import java.util.Optional;

final class JavaApiTypeTest {
    private JavaApiTypeTest() {
    }

    static void typeCheck(KartRef ref) {
        Ref<Kart<XEngine>> specified = ref.specify(KartType.X.INSTANCE);
        Optional<Kart<?>> kart = ref.get();
        List<KartType<?>> entries = KartType.entries;
        KartType<?> type = KartType.fromEngineCode(10);
        specified.get().ifPresent(value -> {
            XTachometer tachometer = value.getEngine().getTachometer();
        });
    }
}
