package vos1.superinnova.engine.statsummarizer.minmax;

/**
 * Created by Wachirawat on 11/25/15 AD.
 */
public class StatisticsMinMax {

    private String name;
    private String min;
    private String max;

    public StatisticsMinMax(String name, String min, String max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }


}
