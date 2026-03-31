import components.simplewriter.SimpleWriter;

/**
 * Enhanced interface for ExposureAssist.
 */
public interface ExposureAssist extends ExposureAssistKernel {

    /**
     * Computes the exposure value corresponding to the current settings.
     *
     * @return the exposure value of this
     * @ensures calculateEV = log2((this.aperture * this.aperture /
     *          this.shutterSpeed) * (100 / this.iso))
     */
    double calculateEV();

    /**
     * Displays advice for adjusting this toward the given target EV.
     *
     * @param targetEV
     *            the desired exposure value
     * @param out
     *            destination for the advice output
     * @requires out /= null
     * @updates out.content
     * @ensures <out.content is appended with guidance based on this and
     *          targetEV>
     */
    void displayBalanceAdvice(int targetEV, SimpleWriter out);
}
