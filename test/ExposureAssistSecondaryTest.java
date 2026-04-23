import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import components.EA.ExposureAssist2;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * JUnit tests for the secondary methods of {@code ExposureAssistSecondary},
 * exercised through the {@code ExposureAssist2} concrete implementation.
 *
 * <p>
 * Secondary methods under test:
 * <ul>
 * <li>{@code calculateEV}</li>
 * <li>{@code displayBalanceAdvice}</li>
 * <li>{@code toString}</li>
 * </ul>
 *
 * <p>
 * Kernel methods and Standard methods ({@code clear}, {@code newInstance},
 * {@code transferFrom}) are tested separately in
 * {@code ExposureAssistKernelTest}.
 */
public class ExposureAssistSecondaryTest {

    /** Tolerance to account for floating point math errors. */
    private static final double DELTA = 1e-6;

    /** Temporary file used to capture SimpleWriter output in tests. */
    private static final String TEMP_FILE = "test_output.txt";

    /**
     * Independent EV formula used as the reference in assertions.
     */
    private static double ev(double aperture, double shutterSpeed, int iso) {
        return Math.log((Math.pow(aperture, 2) / shutterSpeed) * (100.0 / iso))
                / Math.log(2);
    }

    /**
     * Reads the full contents of {@code TEMP_FILE} into a String.
     *
     * @return the captured output as a single String
     */
    private static String readTempFile() {
        SimpleReader in = new SimpleReader1L(TEMP_FILE);
        StringBuilder sb = new StringBuilder();
        while (!in.atEOS()) {
            sb.append(in.nextLine()).append('\n');
        }
        in.close();
        return sb.toString();
    }

    private ExposureAssist2 ea;

    @Before
    public void setUp() {
        this.ea = new ExposureAssist2(); // default: f/8, 1/250 s, ISO 100
    }

    // =======================================================================
    // calculateEV
    // =======================================================================

    /**
     * Default settings (f/8, 1/250, ISO 100) must match the independent formula
     * exactly.
     */
    @Test
    public void testCalculateEV_defaultSettings() {
        double expected = ev(8.0, 1.0 / 250.0, 100);
        assertEquals(expected, this.ea.calculateEV(), DELTA);
    }

    /**
     * Sunny-16 rule: f/16, 1/100 s, ISO 100.
     */
    @Test
    public void testCalculateEV_sunnyRule() {
        this.ea.setAperture(16.0);
        this.ea.setShutterSpeed(1, 100);
        this.ea.setISO(100);
        double expected = ev(16.0, 1.0 / 100.0, 100);
        assertEquals(expected, this.ea.calculateEV(), DELTA);
    }

    /**
     * Doubling ISO (one stop more sensitive) must decrease EV by exactly 1.
     */
    @Test
    public void testCalculateEV_doubleISOdecreasesEVbyOne() {
        double ev1 = this.ea.calculateEV();
        this.ea.setISO(200);
        assertEquals(ev1 - 1.0, this.ea.calculateEV(), DELTA);
    }

    /**
     * Widening aperture by one stop (dividing f-number by sqrt(2)) must
     * decrease EV by exactly 1.
     */
    @Test
    public void testCalculateEV_openApertureOneStopDecreasesEVbyOne() {
        double ev1 = this.ea.calculateEV();
        this.ea.setAperture(8.0 / Math.sqrt(2));
        assertEquals(ev1 - 1.0, this.ea.calculateEV(), DELTA);
    }

    /**
     * Halving shutter speed (twice as fast) must increase EV by exactly 1.
     */
    @Test
    public void testCalculateEV_halveShutterIncreasesEVbyOne() {
        double ev1 = this.ea.calculateEV();
        this.ea.setShutterSpeed(1, 500); // was 1/250
        assertEquals(ev1 + 1.0, this.ea.calculateEV(), DELTA);
    }

    /**
     * Parameterised constructor then calculateEV must agree with the
     * independent formula.
     */
    @Test
    public void testCalculateEV_parameterisedConstructor() {
        ExposureAssist2 ea2 = new ExposureAssist2(2.8, 1, 60, 1600);
        assertEquals(ev(2.8, 1.0 / 60.0, 1600), ea2.calculateEV(), DELTA);
    }

    // =======================================================================
    // displayBalanceAdvice
    // =======================================================================

    /**
     * When the current EV already matches the target (within 0.1 stops), the
     * advice must say "optimal".
     */
    @Test
    public void testDisplayBalanceAdvice_alreadyOptimal() {
        int targetEV = (int) Math.round(this.ea.calculateEV());
        SimpleWriter out = new SimpleWriter1L(TEMP_FILE);
        this.ea.displayBalanceAdvice(targetEV, out);
        out.close();
        assertTrue(readTempFile().contains("optimal"));
    }

    /**
     * When the target EV is higher than the current EV, the advice must say
     * "increase".
     */
    @Test
    public void testDisplayBalanceAdvice_needsIncrease() {
        this.ea.setAperture(1.4);
        this.ea.setShutterSpeed(1, 30);
        this.ea.setISO(6400);
        SimpleWriter out = new SimpleWriter1L(TEMP_FILE);
        this.ea.displayBalanceAdvice(14, out);
        out.close();
        assertTrue(readTempFile().contains("increase"));
    }

    /**
     * When the target EV is lower than the current EV, the advice must say
     * "decrease".
     */
    @Test
    public void testDisplayBalanceAdvice_needsDecrease() {
        this.ea.setAperture(22.0);
        this.ea.setShutterSpeed(1, 4000);
        this.ea.setISO(100);
        SimpleWriter out = new SimpleWriter1L(TEMP_FILE);
        this.ea.displayBalanceAdvice(6, out);
        out.close();
        assertTrue(readTempFile().contains("decrease"));
    }

    /**
     * Output must always include a reference to the current EV and the target.
     */
    @Test
    public void testDisplayBalanceAdvice_containsCurrentAndTarget() {
        SimpleWriter out = new SimpleWriter1L(TEMP_FILE);
        this.ea.displayBalanceAdvice(10, out);
        out.close();
        String output = readTempFile();
        assertTrue(output.contains("Current EV"));
        assertTrue(output.contains("10"));
    }

    /**
     * The suggested ISO printed in the advice must be mathematically consistent
     * with the stop difference between current and target EV.
     */
    @Test
    public void testDisplayBalanceAdvice_suggestedISO_mathematicallySane() {
        int targetEV = 10;
        double diff = targetEV - this.ea.calculateEV();
        int expectedISO = (int) Math.round(this.ea.iso() * Math.pow(2, diff));
        SimpleWriter out = new SimpleWriter1L(TEMP_FILE);
        this.ea.displayBalanceAdvice(targetEV, out);
        out.close();
        assertTrue(readTempFile().contains(String.valueOf(expectedISO)));
    }

    // =======================================================================
    // toString
    // =======================================================================

    @Test
    public void testToString_defaultSettings() {
        String s = this.ea.toString();
        assertTrue(s.contains("8.0"));
        assertTrue(s.contains(String.valueOf(1.0 / 250.0)));
        assertTrue(s.contains("100"));
    }

    @Test
    public void testToString_afterMutation() {
        this.ea.setAperture(2.8);
        this.ea.setShutterSpeed(1, 60);
        this.ea.setISO(1600);
        String s = this.ea.toString();
        assertTrue(s.contains("2.8"));
        assertTrue(s.contains("1600"));
    }
}
