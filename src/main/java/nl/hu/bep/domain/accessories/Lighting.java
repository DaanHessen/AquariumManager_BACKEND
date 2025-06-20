package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.utils.Validator;
import lombok.*;
import java.time.LocalTime;
import java.time.Duration;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Lighting extends Accessory {
    private boolean isLed;
    private LocalTime turnOffTime;
    private LocalTime turnOnTime;

    public Lighting(String model, String serialNumber, boolean isLed, 
                   LocalTime turnOffTime, LocalTime turnOnTime, Long ownerId) {
        super(model, serialNumber, ownerId);
        this.isLed = isLed;
        this.turnOffTime = Validator.notNull(turnOffTime, "Turn off time");
        this.turnOnTime = Validator.notNull(turnOnTime, "Turn on time");
    }

    @Override
    public String getAccessoryType() {
        return "Lighting";
    }

    public void updateProperties(boolean isLed, LocalTime turnOffTime, LocalTime turnOnTime) {
        this.isLed = isLed;
        this.turnOffTime = Validator.notNull(turnOffTime, "Turn off time");
        this.turnOnTime = Validator.notNull(turnOnTime, "Turn on time");
    }

    public boolean isCurrentlyOn() {
        LocalTime now = LocalTime.now();
        if (turnOnTime.isBefore(turnOffTime)) {
            return !now.isBefore(turnOnTime) && now.isBefore(turnOffTime);
        } else {
            return !now.isBefore(turnOnTime) || now.isBefore(turnOffTime);
        }
    }

    public Duration getDailyLightDuration() {
        if (turnOnTime.isBefore(turnOffTime)) {
            return Duration.between(turnOnTime, turnOffTime);
        } else {
            return Duration.between(turnOnTime, LocalTime.MIDNIGHT)
                    .plus(Duration.between(LocalTime.MIDNIGHT, turnOffTime));
        }
    }

    public void updateSchedule(LocalTime turnOnTime, LocalTime turnOffTime) {
        this.turnOnTime = Validator.notNull(turnOnTime, "Turn on time");
        this.turnOffTime = Validator.notNull(turnOffTime, "Turn off time");
    }

    public void updateLedType(boolean isLed) {
        this.isLed = isLed;
    }
}
