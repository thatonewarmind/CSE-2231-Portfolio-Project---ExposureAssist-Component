import components.EA.ExposureAssist2;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Concrete use-case demonstrations for ExposureAssist2.
 *
 * <p>
 * Two scenarios are shown:
 * <ol>
 * <li>Bright outdoor / Sunny-16 rule — fast action in noon sun</li>
 * <li>Indoor low-light — museum / gallery, no flash allowed</li>
 * </ol>
 */
public class ExposureAssistDemo {

        /**
         * EV reference values (ISO-100) commonly cited in photographic
         * literature.
         */
        private static final int EV_BRIGHT_SUN = 15; // sunny-16 at noon
        private static final int EV_INDOOR_OFFICE = 7; // bright interior

        public static void main(String[] args) {
                SimpleWriter out = new SimpleWriter1L();

                out.println("╔══════════════════════════════════════════════════════╗");
                out.println("║          ExposureAssist2 — Concrete Use Cases        ║");
                out.println("╚══════════════════════════════════════════════════════╝");

                // -------------------------------------------------------------------
                // Use Case 1 — Sunny day, freezing a sprinting athlete
                //
                // Goal: bright sun ≈ EV 15.  Freeze motion → fast shutter.
                // Settings: f/8, 1/2000 s, ISO 200
                // -------------------------------------------------------------------
                out.println("\n━━ Use Case 1: Bright Outdoor / Freezing Motion ━━━━━━━━");
                out.println("Scene: Sprinting athlete under noon sun.");
                out.println("Priority: Freeze motion → fast shutter 1/2000 s.");

                ExposureAssist2 sports = new ExposureAssist2(8.0, 1, 2000, 200);
                printSettings(sports, out);
                sports.displayBalanceAdvice(EV_BRIGHT_SUN, out);

                // -------------------------------------------------------------------
                // Use Case 2 — Museum interior, no flash allowed
                //
                // Goal: EV ≈ 7.  Avoid camera shake (handheld) → 1/60 s minimum.
                // Settings: f/2.8, 1/60 s, ISO 1600
                // -------------------------------------------------------------------
                out.println("\n━━ Use Case 2: Indoor Low-Light (No Flash) ━━━━━━━━━━━━");
                out.println("Scene: Museum exhibit, handheld, no flash.");
                out.println("Priority: Wide aperture + high ISO to keep shutter safe.");

                ExposureAssist2 museum = new ExposureAssist2(2.8, 1, 60, 1600);
                printSettings(museum, out);
                museum.displayBalanceAdvice(EV_INDOOR_OFFICE, out);

                out.println("\nDone.");
                out.close();
        }

        // -----------------------------------------------------------------------
        // Helper
        // -----------------------------------------------------------------------

        /**
         * Prints the current exposure triangle in a human-readable format.
         *
         * @param ea
         *                the ExposureAssist2 instance to inspect
         * @param out
         *                destination writer
         */
        private static void printSettings(ExposureAssist2 ea,
                        SimpleWriter out) {
                out.println(String.format(
                                "Settings  : f/%.1f  |  1/%.0f s  |  ISO %d",
                                ea.aperture(), 1.0 / ea.shutterSpeed(),
                                ea.iso()));
                out.println(String.format("Computed EV: %.2f",
                                ea.calculateEV()));
        }
}
