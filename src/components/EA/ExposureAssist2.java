package components.EA;

import components.map.Map;
import components.map.Map1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * {@code ExposureAssist} represented as a {@code Map<String, Double>} with
 * implementations of primary methods.
 *
 * <p>
 * The map stores exactly three entries:
 * <ul>
 * <li>{@code "aperture"} — the f-stop value</li>
 * <li>{@code "shutterSpeed"} — the exposure time in seconds</li>
 * <li>{@code "iso"} — the sensor sensitivity (stored as a double for map
 * uniformity, interpreted as an integer)</li>
 * </ul>
 *
 * <p>
 *
 * @convention <pre>
 * [this.rep contains exactly the keys "aperture", "shutterSpeed", "iso"] and
 * [0.5 <= this.rep.get("aperture") <= 64.0] and
 * [this.rep.get("shutterSpeed") > 0] and
 * [this.rep.get("iso") >= 1 and this.rep.get("iso") <= 204800]
 * </pre>
 *
 * @correspondence <pre>
 * this.aperture    = this.rep.get("aperture")
 * this.shutterSpeed = this.rep.get("shutterSpeed")
 * this.iso         = this.rep.get("iso").intValue()
 * </pre>
 */
public class ExposureAssist2 extends ExposureAssistSecondary {

    /*
     * -----------------------------------------------------------------------
     * Private representation
     * -----------------------------------------------------------------------
     */

    /**
     * Map storing all camera exposure settings.
     */
    private Map<String, Double> rep;

    /*
     * -----------------------------------------------------------------------
     * Map key constants
     * -----------------------------------------------------------------------
     */

    /** Map key for aperture. */
    private static final String KEY_APERTURE = "aperture";

    /** Map key for shutter speed. */
    private static final String KEY_SHUTTER = "shutterSpeed";

    /** Map key for ISO. */
    private static final String KEY_ISO = "iso";

    /*
     * -----------------------------------------------------------------------
     * Creator of initial representation
     * -----------------------------------------------------------------------
     */

    /**
     * Populates {@code this.rep} with the standard default values.
     *
     * @ensures <pre>
     * this.rep.get("aperture")    = 8.0
     * this.rep.get("shutterSpeed") = 1.0 / 250.0
     * this.rep.get("iso")         = 100.0
     * </pre>
     */
    private void createNewRep() {
        this.rep = new Map1L<String, Double>();
        this.rep.add(KEY_APERTURE, 8.0);
        this.rep.add(KEY_SHUTTER, 1.0 / 250.0);
        this.rep.add(KEY_ISO, 100.0);
    }

    /*
     * -----------------------------------------------------------------------
     * Constructors
     * -----------------------------------------------------------------------
     */

    /**
     * No-argument constructor.
     *
     * @ensures this = (8.0, 1/250, 100)
     */
    public ExposureAssist2() {
        this.createNewRep();
    }

    /**
     * Constructor that initializes all three exposure parameters.
     *
     * @param fStop
     *            the aperture value
     * @param shutterNum
     *            numerator of the shutter speed fraction
     * @param shutterDen
     *            denominator of the shutter speed fraction
     * @param isoVal
     *            the ISO value
     * @requires 0.5 <= fStop and fStop <= 64.0 and shutterNum > 0 and
     *           shutterDen > 0 and 1 <= isoVal and isoVal <= 204800
     * @ensures this.aperture = fStop and this.shutterSpeed = shutterNum /
     *          shutterDen and this.iso = isoVal
     */
    public ExposureAssist2(double fStop, int shutterNum, int shutterDen,
            int isoVal) {
        assert fStop >= 0.5 && fStop <= 64.0 : "Violation of: fStop range";
        assert shutterNum > 0
                && shutterDen > 0 : "Violation of: positive shutter fraction";
        assert isoVal >= 1 && isoVal <= 204800 : "Violation of: isoVal range";

        this.createNewRep();
        this.rep.replaceValue(KEY_APERTURE, fStop);
        this.rep.replaceValue(KEY_SHUTTER, (double) shutterNum / shutterDen);
        this.rep.replaceValue(KEY_ISO, (double) isoVal);
    }

    /*
     * -----------------------------------------------------------------------
     * Standard methods (used by all OSU component implementations thusfar)
     * -----------------------------------------------------------------------
     */

    @Override
    public final void clear() {
        this.createNewRep();
    }

    @Override
    public final void transferFrom(ExposureAssist source) {
        assert source != null : "Violation of: source is not null";
        assert source != this : "Violation of: source is not this";
        assert source instanceof ExposureAssist2 : "Violation of: source is of dynamic type ExposureAssist2";

        ExposureAssist2 localSource = (ExposureAssist2) source;
        this.rep = localSource.rep;
        localSource.createNewRep();
    }

    @Override
    public final ExposureAssist newInstance() {
        try {
            return this.getClass().getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(
                    "Cannot construct new instance of ExposureAssist2");
        }
    }

    /*
     * -----------------------------------------------------------------------
     * Kernel methods
     * -----------------------------------------------------------------------
     */

    /**
     * Sets the aperture (f-stop) for this.
     *
     * @param fStop
     *            the new aperture value
     * @requires 0.5 <= fStop and fStop <= 64.0
     * @ensures this.aperture = fStop and this.shutterSpeed = #this.shutterSpeed
     *          and this.iso = #this.iso
     */
    @Override
    public final void setAperture(double fStop) {
        assert fStop >= 0.5 && fStop <= 64.0 : "Violation of: fStop range";
        this.rep.replaceValue(KEY_APERTURE, fStop);
    }

    /**
     * Sets the shutter speed for this using a fractional representation.
     *
     * @param num
     *            numerator of the shutter speed fraction
     * @param den
     *            denominator of the shutter speed fraction
     * @requires num > 0 and den > 0
     * @ensures this.shutterSpeed = (double) num / den and this.aperture =
     *          #this.aperture and this.iso = #this.iso
     */
    @Override
    public final void setShutterSpeed(int num, int den) {
        assert num > 0 && den > 0 : "Violation of: positive shutter fraction";
        this.rep.replaceValue(KEY_SHUTTER, (double) num / den);
    }

    /**
     * Sets the ISO sensitivity for this.
     *
     * @param isoVal
     *            the new ISO value
     * @requires 1 <= isoVal and isoVal <= 204800
     * @ensures this.iso = isoVal and this.aperture = #this.aperture and
     *          this.shutterSpeed = #this.shutterSpeed
     */
    @Override
    public final void setISO(int isoVal) {
        assert isoVal >= 1 && isoVal <= 204800 : "Violation of: isoVal range";
        this.rep.replaceValue(KEY_ISO, (double) isoVal);
    }

    /**
     * Reports the aperture value of this.
     *
     * @return this.aperture
     * @ensures aperture = this.aperture
     */
    @Override
    public final double aperture() {
        return this.rep.value(KEY_APERTURE);
    }

    /**
     * Reports the shutter speed value of this.
     *
     * @return this.shutterSpeed
     * @ensures shutterSpeed = this.shutterSpeed
     */
    @Override
    public final double shutterSpeed() {
        return this.rep.value(KEY_SHUTTER);
    }

    /**
     * Reports the ISO value of this.
     *
     * @return this.iso
     * @ensures iso = this.iso
     */
    @Override
    public final int iso() {
        return this.rep.value(KEY_ISO).intValue();
    }

    /*
     * -----------------------------------------------------------------------
     * Main method (informal demonstration / smoke test)
     * -----------------------------------------------------------------------
     */

    /**
     * Exercises the component with sample inputs.
     *
     * @param args
     *            command-line arguments (not used)
     */
    public static void main(String[] args) {
        SimpleWriter out = new SimpleWriter1L();

        out.println("=== ExposureAssist2 Smoke Test ===");

        // Default construction
        ExposureAssist2 ea = new ExposureAssist2();
        out.println("Default settings: " + ea);
        out.println("Default EV: " + ea.calculateEV());

        // Parameterized construction: f/2.8, 1/60 s, ISO 1600
        ExposureAssist2 ea2 = new ExposureAssist2(2.8, 1, 60, 1600);
        out.println("\nCustom settings (f/2.8, 1/60, ISO 1600): " + ea2);
        out.println("EV: " + ea2.calculateEV());
        ea2.displayBalanceAdvice(10, out);

        // Mutation via kernel methods
        ea.setAperture(5.6);
        ea.setShutterSpeed(1, 500);
        ea.setISO(400);
        out.println("\nAfter mutation (f/5.6, 1/500, ISO 400): " + ea);
        out.println("EV: " + ea.calculateEV());

        // transferFrom
        ExposureAssist2 ea3 = new ExposureAssist2();
        ea3.transferFrom(ea2);
        out.println("\nAfter transferFrom ea2 -> ea3: " + ea3);
        out.println("ea2 after transfer (should be default): " + ea2);

        // clear
        ea3.clear();
        out.println("ea3 after clear (should be default): " + ea3);

        out.close();
    }
}