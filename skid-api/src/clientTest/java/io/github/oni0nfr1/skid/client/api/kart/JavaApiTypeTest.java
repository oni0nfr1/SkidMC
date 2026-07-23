package io.github.oni0nfr1.skid.client.api.kart;

import io.github.oni0nfr1.skid.client.api.engine.DSEngine;
import io.github.oni0nfr1.skid.client.api.engine.XEngine;
import io.github.oni0nfr1.skid.client.api.kart.unstable.KartRaceUtils;
import io.github.oni0nfr1.skid.client.api.tachometer.DSTachometer;
import io.github.oni0nfr1.skid.client.api.tachometer.XTachometer;
import io.github.oni0nfr1.skid.client.api.utils.KartType;
import io.github.oni0nfr1.skid.client.api.utils.Ref;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

final class JavaApiTypeTest {
    private JavaApiTypeTest() {
    }

    static void typeCheck(KartRef ref) {
        Ref<Kart<XEngine>> specified = ref.specify(KartType.X.INSTANCE);
        KartType<DSEngine> dsType = KartType.DS.INSTANCE;
        Ref<Kart<DSEngine>> specifiedDS = ref.specify(dsType);
        Optional<Kart<?>> kart = ref.get();
        List<KartType<?>> entries = KartType.entries;
        KartType<?> type = KartType.fromEngineCode(10);
        specified.get().ifPresent(value -> {
            XTachometer tachometer = value.getEngine().getTachometer();
            int currentLap = KartRaceUtils.getCurrentLap(value);
            Integer maxLap = KartRaceUtils.getMaxLap(value);
        });
        specifiedDS.get().ifPresent(value -> {
            DSTachometer tachometer = value.getEngine().getTachometer();
        });
    }

    static void accessors(Cod saddle, Player rider, LocalPlayer localPlayer) {
        KartRef directRef = new KartRef(saddle);
        KartRef kart = KartUtils.getKart(saddle);
        KartRef ridingKart = KartUtils.getRidingKart(rider);
        MountType mountStatus = KartUtils.getMountStatus(localPlayer);
    }
}
