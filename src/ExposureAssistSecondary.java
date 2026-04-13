interface ExposureAssistKernel {
    void setAperture(double fStop);

    void setShutterSpeed(int num, int den);

    void setISO(int isoVal);

    double aperture();

    double shutterSpeed();

    int iso();
}

interface ExposureAssist extends ExposureAssistKernel {
    double calculateEV();

    void displayBalanceAdvice(int targetEV, SimpleWriter out);
}

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

final class ExposureAssist1 extends ExposureAssistSecondary {

    private double aperture;
    private double shutterSpeed;
    private int iso;

    public ExposureAssist1() {
        this.aperture = 8.0;
        this.shutterSpeed = 1.0 / 125.0;
        this.iso = 100;
    }

    @Override
    public void setAperture(double fStop) {
        assert fStop >= 0.5 && fStop <= 64.0 : "Violation of: fStop range";
        this.aperture = fStop;
    }

    @Override
    public void setShutterSpeed(int num, int den) {
        assert num > 0 && den > 0 : "Violation of: positive fraction";
        this.shutterSpeed = (double) num / den;
    }

    @Override
    public void setISO(int isoVal) {
        assert isoVal > 0 && isoVal <= 204800 : "Violation of: isoVal > 0";
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
}

public final class ExposureAssistDemo {

    private ExposureAssistDemo() {
    }

    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        ExposureAssist ea = new ExposureAssist1();

        out.println("--- ExposureAssist v1 ---");
        out.println("Initial EV: " + ea.calculateEV());

        out.print("Enter target EV: ");
        int target = in.nextInteger();

        ea.displayBalanceAdvice(target, out);

        in.close();
        out.close();
    }
}