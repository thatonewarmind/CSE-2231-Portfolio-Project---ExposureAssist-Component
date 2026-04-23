package components.EA;

import components.standard.Standard;

/**
 * Kernel interface for ExposureAssist.
 *
 * @mathmodel type ExposureAssistKernel is modeled by (aperture: real,
 *            shutterSpeed: real, iso: integer)
 *
 * @constraint 0.5 <= aperture and aperture <= 64.0 and shutterSpeed > 0 and iso
 *             > 0
 *
 * @initially <pre> (8.0, 1.0/250.0, 100) </pre>
 */
public interface ExposureAssistKernel extends Standard<ExposureAssist> {

    /**
     * Sets the aperture value for this.
     *
     * @param fStop
     *            the new aperture value
     * @requires 0.5 <= fStop and fStop <= 64.0
     * @ensures this.aperture = fStop and this.shutterSpeed = #this.shutterSpeed
     *          and this.iso = #this.iso
     */
    void setAperture(double fStop);

    /**
     * Sets the shutter speed value for this using the fraction num / den.
     *
     * @param num
     *            numerator of the shutter speed fraction
     * @param den
     *            denominator of the shutter speed fraction
     * @requires num > 0 and den > 0
     * @ensures this.shutterSpeed = (double) num / den and this.aperture =
     *          #this.aperture and this.iso = #this.iso
     */
    void setShutterSpeed(int num, int den);

    /**
     * Sets the ISO value for this.
     *
     * @param isoVal
     *            the new ISO value
     * @requires isoVal > 0
     * @ensures this.iso = isoVal and this.aperture = #this.aperture and
     *          this.shutterSpeed = #this.shutterSpeed
     */
    void setISO(int isoVal);

    /**
     * Reports the aperture value of this.
     *
     * @return the current aperture value
     * @ensures aperture = this.aperture
     */
    double aperture();

    /**
     * Reports the shutter speed value of this.
     *
     * @return the current shutter speed value
     * @ensures shutterSpeed = this.shutterSpeed
     */
    double shutterSpeed();

    /**
     * Reports the ISO value of this.
     *
     * @return the current ISO value
     * @ensures iso = this.iso
     */
    int iso();
}
