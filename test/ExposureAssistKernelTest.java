import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import components.EA.ExposureAssist;
import components.EA.ExposureAssist2;

/**
 * JUnit tests for the kernel methods of {@code ExposureAssist2}.
 *
 * <p>
 * Kernel methods under test:
 * <ul>
 * <li>{@code setAperture} / {@code aperture}</li>
 * <li>{@code setShutterSpeed} / {@code shutterSpeed}</li>
 * <li>{@code setISO} / {@code iso}</li>
 * <li>{@code clear}</li>
 * <li>{@code newInstance}</li>
 * <li>{@code transferFrom}</li>
 * </ul>
 *
 * <p>
 * Secondary methods ({@code calculateEV}, {@code displayBalanceAdvice},
 * {@code toString}) are tested separately in
 * {@code ExposureAssistSecondaryTest}.
 */
public class ExposureAssistKernelTest {

    private static final double DELTA = 1e-6;

    private ExposureAssist2 ea;

    @Before
    public void setUp() {
        this.ea = new ExposureAssist2(); // default: f/8, 1/250 s, ISO 100
    }

    // =======================================================================
    // Constructors
    // =======================================================================

    @Test
    public void testDefaultConstructor() {
        assertEquals(8.0, this.ea.aperture(), DELTA);
        assertEquals(1.0 / 250.0, this.ea.shutterSpeed(), DELTA);
        assertEquals(100, this.ea.iso());
    }

    @Test
    public void testParameterisedConstructor_typical() {
        ExposureAssist2 ea2 = new ExposureAssist2(2.8, 1, 60, 1600);
        assertEquals(2.8, ea2.aperture(), DELTA);
        assertEquals(1.0 / 60.0, ea2.shutterSpeed(), DELTA);
        assertEquals(1600, ea2.iso());
    }

    @Test
    public void testParameterisedConstructor_minAperture() {
        ExposureAssist2 ea2 = new ExposureAssist2(0.5, 1, 100, 100);
        assertEquals(0.5, ea2.aperture(), DELTA);
    }

    @Test
    public void testParameterisedConstructor_maxAperture() {
        ExposureAssist2 ea2 = new ExposureAssist2(64.0, 1, 100, 100);
        assertEquals(64.0, ea2.aperture(), DELTA);
    }

    @Test
    public void testParameterisedConstructor_maxISO() {
        ExposureAssist2 ea2 = new ExposureAssist2(8.0, 1, 250, 204800);
        assertEquals(204800, ea2.iso());
    }

    // =======================================================================
    // setAperture / aperture
    // =======================================================================

    @Test
    public void testSetAperture_typical() {
        this.ea.setAperture(5.6);
        assertEquals(5.6, this.ea.aperture(), DELTA);
    }

    @Test
    public void testSetAperture_minBoundary() {
        this.ea.setAperture(0.5);
        assertEquals(0.5, this.ea.aperture(), DELTA);
    }

    @Test
    public void testSetAperture_maxBoundary() {
        this.ea.setAperture(64.0);
        assertEquals(64.0, this.ea.aperture(), DELTA);
    }

    /** setAperture must not change shutterSpeed or ISO. */
    @Test
    public void testSetAperture_doesNotMutateOtherFields() {
        double prevShutter = this.ea.shutterSpeed();
        int prevISO = this.ea.iso();
        this.ea.setAperture(4.0);
        assertEquals(prevShutter, this.ea.shutterSpeed(), DELTA);
        assertEquals(prevISO, this.ea.iso());
    }

    @Test(expected = AssertionError.class)
    public void testSetAperture_belowMin_throws() {
        this.ea.setAperture(0.4);
    }

    @Test(expected = AssertionError.class)
    public void testSetAperture_aboveMax_throws() {
        this.ea.setAperture(65.0);
    }

    // =======================================================================
    // setShutterSpeed / shutterSpeed
    // =======================================================================

    @Test
    public void testSetShutterSpeed_typical() {
        this.ea.setShutterSpeed(1, 500);
        assertEquals(1.0 / 500.0, this.ea.shutterSpeed(), DELTA);
    }

    @Test
    public void testSetShutterSpeed_oneSecond() {
        this.ea.setShutterSpeed(1, 1);
        assertEquals(1.0, this.ea.shutterSpeed(), DELTA);
    }

    @Test
    public void testSetShutterSpeed_30seconds() {
        this.ea.setShutterSpeed(30, 1);
        assertEquals(30.0, this.ea.shutterSpeed(), DELTA);
    }

    /** setShutterSpeed must not change aperture or ISO. */
    @Test
    public void testSetShutterSpeed_doesNotMutateOtherFields() {
        double prevAperture = this.ea.aperture();
        int prevISO = this.ea.iso();
        this.ea.setShutterSpeed(1, 4000);
        assertEquals(prevAperture, this.ea.aperture(), DELTA);
        assertEquals(prevISO, this.ea.iso());
    }

    @Test(expected = AssertionError.class)
    public void testSetShutterSpeed_zeroNumerator_throws() {
        this.ea.setShutterSpeed(0, 250);
    }

    @Test(expected = AssertionError.class)
    public void testSetShutterSpeed_zeroDenominator_throws() {
        this.ea.setShutterSpeed(1, 0);
    }

    // =======================================================================
    // setISO / iso
    // =======================================================================

    @Test
    public void testSetISO_typical() {
        this.ea.setISO(800);
        assertEquals(800, this.ea.iso());
    }

    @Test
    public void testSetISO_minBoundary() {
        this.ea.setISO(1);
        assertEquals(1, this.ea.iso());
    }

    @Test
    public void testSetISO_maxBoundary() {
        this.ea.setISO(204800);
        assertEquals(204800, this.ea.iso());
    }

    /** setISO must not change aperture or shutterSpeed. */
    @Test
    public void testSetISO_doesNotMutateOtherFields() {
        double prevAperture = this.ea.aperture();
        double prevShutter = this.ea.shutterSpeed();
        this.ea.setISO(3200);
        assertEquals(prevAperture, this.ea.aperture(), DELTA);
        assertEquals(prevShutter, this.ea.shutterSpeed(), DELTA);
    }

    @Test(expected = AssertionError.class)
    public void testSetISO_zero_throws() {
        this.ea.setISO(0);
    }

    @Test(expected = AssertionError.class)
    public void testSetISO_aboveMax_throws() {
        this.ea.setISO(204801);
    }

    // =======================================================================
    // clear
    // =======================================================================

    @Test
    public void testClear_restoresDefaults() {
        this.ea.setAperture(2.0);
        this.ea.setShutterSpeed(1, 30);
        this.ea.setISO(3200);
        this.ea.clear();
        assertEquals(8.0, this.ea.aperture(), DELTA);
        assertEquals(1.0 / 250.0, this.ea.shutterSpeed(), DELTA);
        assertEquals(100, this.ea.iso());
    }

    // =======================================================================
    // newInstance
    // =======================================================================

    @Test
    public void testNewInstance_returnsDefaultObject() {
        this.ea.setAperture(1.4);
        ExposureAssist fresh = this.ea.newInstance();
        assertEquals(8.0, fresh.aperture(), DELTA);
        assertEquals(1.0 / 250.0, fresh.shutterSpeed(), DELTA);
        assertEquals(100, fresh.iso());
        // caller must be unchanged
        assertEquals(1.4, this.ea.aperture(), DELTA);
    }

    // =======================================================================
    // transferFrom
    // =======================================================================

    @Test
    public void testTransferFrom_receiverGetsSourceState() {
        ExposureAssist2 source = new ExposureAssist2(4.0, 1, 125, 400);
        ExposureAssist2 dest = new ExposureAssist2();
        dest.transferFrom(source);
        assertEquals(4.0, dest.aperture(), DELTA);
        assertEquals(1.0 / 125.0, dest.shutterSpeed(), DELTA);
        assertEquals(400, dest.iso());
    }

    @Test
    public void testTransferFrom_sourceRevertedToDefault() {
        ExposureAssist2 source = new ExposureAssist2(4.0, 1, 125, 400);
        ExposureAssist2 dest = new ExposureAssist2();
        dest.transferFrom(source);
        assertEquals(8.0, source.aperture(), DELTA);
        assertEquals(1.0 / 250.0, source.shutterSpeed(), DELTA);
        assertEquals(100, source.iso());
    }

    @Test(expected = AssertionError.class)
    public void testTransferFrom_nullSource_throws() {
        this.ea.transferFrom(null);
    }

    @Test(expected = AssertionError.class)
    public void testTransferFrom_selfTransfer_throws() {
        this.ea.transferFrom(this.ea);
    }
}
