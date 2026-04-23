package components.EA;
//Not implementing hashCode and equals methods because it doesn't make sense

//You wouldn't need to have multiple of these objects working at a time, there's not much reason to compare them using equals or store them in a set using hashCode.
//Component is meant to be a "one and done", where the object can be discarded or wiped clean after each use, storing the values long-term isn't necessary

import components.simplewriter.SimpleWriter;

public abstract class ExposureAssistSecondary implements ExposureAssist {
    @Override
    public final double calculateEV() {
        final double standardISO = 100.0;
        return Math.log((Math.pow(this.aperture(), 2) / this.shutterSpeed())
                * (standardISO / this.iso())) / Math.log(2);
    }

    @Override
    public final void displayBalanceAdvice(int targetEV, SimpleWriter out) {
        double currentEV = this.calculateEV();
        double diff = targetEV - currentEV;

        out.print("Current EV: ");
        out.print(currentEV, 2, false);
        out.println("Target EV: " + targetEV);

        if (Math.abs(diff) > 0.1) {
            String action;
            if (diff > 0) {
                action = "increase";
            } else {
                action = "decrease";
            }

            out.println("Suggestion: " + action + " exposure by "
                    + Math.abs(diff) + " stops");

            int suggestedISO = (int) Math.round(this.iso() * Math.pow(2, diff));
            out.println("Try setting your ISO to: " + suggestedISO);

        } else {
            out.println("Settings are optimal for your target EV");
        }
    }

    @Override
    public final String toString() {
        String result = "(" + this.aperture() + ", " + this.shutterSpeed()
                + ", " + this.iso() + ")";
        return result;
    }
}
