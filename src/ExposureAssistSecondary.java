abstract class ExposureAssistSecondary implements ExposureAssist {
    @Override
    public double calculateEV() {
        final double standardISO = 100.0;
        return Math.log((Math.pow(this.aperture(), 2) / this.shutterSpeed())
                * (standardISO / this.iso())) / Math.log(2);
    }

    @Override
    public void displayBalanceAdvice(int targetEV, SimpleWriter out) {
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
}
