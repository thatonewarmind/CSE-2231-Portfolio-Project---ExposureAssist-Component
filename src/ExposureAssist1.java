import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

//I cannot figure out why I don't have osu components working right now. I will check with Jeremy next week or over Zoom if possible.

/**
 * Interface for the ExposureAssist Kernel. In OSU style, we define the core
 * mathematical state changes here.
 */
interface ExposureAssistKernel {
    //this interface works kinda like a prototype header in c++, where I'm just defining function headers and parameters and return types
    /**
     * @requires fStop >= 0.5 and fStop <= 64.0
     * @ensures this.aperture = fStop
     */
    void setAperture(double fStop);

    /**
     * @requires num > 0 and den > 0
     * @ensures this.shutterSpeed = num / den
     */
    void setShutterSpeed(int num, int den);

    /**
     * @requires isoVal > 0
     * @ensures this.iso = isoVal
     */
    void setISO(int isoVal);

    double aperture();

    double shutterSpeed();

    int iso();
}

/**
 * Optimal camera settings component. * @convention <pre>
 * [aperture is between 0.5 and 64.0] and
 * [shutterSpeed > 0] and [iso > 0]
 * </pre>
 */
public class ExposureAssist implements ExposureAssistKernel {

    /*
     * Private members
     */
    private double aperture;
    private double shutterSpeed; // store as double for simplicity in this POC
    private int iso;

    /**
     * Default constructor.
     *
     * @ensures this = (8.0, 1/125, 100)
     */
    public ExposureAssist() {
        this.aperture = 8.0;
        this.shutterSpeed = 1.0 / 125.0;
        this.iso = 100;
    }

    // --- Kernel Methods ---

    @Override
    public void setAperture(double fStop) {
        assert fStop >= 0.5 && fStop <= 64.0 : "Violation of: fStop range";
        //this is standard fStop range for almost all lens+body combos
        //a lens with wider than f/0.5 would cost 5 sets of kidneys:(
        this.aperture = fStop;
    }

    @Override
    public void setShutterSpeed(int num, int den) {
        assert num > 0 && den > 0 : "Violation of: positive fraction";
        this.shutterSpeed = (double) num / den;
    }

    @Override
    public void setISO(int isoVal) {
        assert isoVal > 0  && isoVal <= 204,800: "Violation of: isoVal > 0";
        //canon r6 mk 2 goes up to 204,800 ISO
        this.iso = isoVal;
    }

    @Override
    public double aperture() {
        return this.aperture;
    }

    @Override
    public double shutterSpeed() {
        return this.shutterSpeed;
    }

    @Override
    public int iso() {
        return this.iso;
    }

    // --- Secondary Methods ---

    /**
     * Calculates the Exposure Value (EV).
     *
     * @ensures calculateEV = log2((N^2 / T) * (100 / S))
     */
    public double calculateEV() {
        final double standardIso = 100.0;
        double ev = Math.log((Math.pow(this.aperture, 2) / this.shutterSpeed)
                * (standardIso / this.iso)) / Math.log(2);
        return ev;
    }

    /**
     * Reports suggestions to reach target EV via a SimpleWriter.
     *
     * @updates out.content
     */
    public void displayBalanceAdvice(int targetEV, SimpleWriter out) {
        double currentEV = this.calculateEV();
        double diff = targetEV - currentEV;

        out.print("Current EV: ");
        out.print(currentEV, 2, false);
        out.println(" | Target: " + targetEV);

        if (Math.abs(diff) > 0.1) {
            String action = (diff > 0) ? "increase" : "decrease";
            out.println("Suggestion: " + action + " exposure by "
                    + Math.abs(diff) + " stops.");

            // Math for ISO suggestion
            int suggestedIso = (int) Math.round(this.iso * Math.pow(2, diff));
            out.println(" -> Try ISO: " + suggestedIso);
        } else {
            out.println("Settings are optimal for target EV.");
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        ExposureAssist ea = new ExposureAssist();

        out.println("--- ExposureAssist v1 ---");
        out.println("Initial EV: " + ea.calculateEV());

        out.print("Enter target EV: ");
        int target = in.nextInteger();

        ea.displayBalanceAdvice(target, out);

        // Close streams (Best practice)
        in.close();
        out.close();
    }
}
