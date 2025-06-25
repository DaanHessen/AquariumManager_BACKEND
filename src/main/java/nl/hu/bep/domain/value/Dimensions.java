package nl.hu.bep.domain.value;

import nl.hu.bep.domain.utils.Validator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value object representing aquarium dimensions.

 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dimensions {

    private double length;
    private double width;
    private double height;

    public Dimensions(double length, double width, double height) {
        this.length = Validator.positive(length, "Length");
        this.width = Validator.positive(width, "Width");
        this.height = Validator.positive(height, "Height");
    }

    public double getVolumeInLiters() {
        return (length * width * height) / 1000;
    }

    public double getVolume() {
        return getVolumeInLiters();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Dimensions))
            return false;
        Dimensions that = (Dimensions) o;
        return Double.compare(that.length, length) == 0 &&
                Double.compare(that.width, width) == 0 &&
                Double.compare(that.height, height) == 0;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Double.hashCode(length);
        result = 31 * result + Double.hashCode(width);
        result = 31 * result + Double.hashCode(height);
        return result;
    }

    @Override
    public String toString() {
        return "Dimensions{" +
                "length=" + length +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}