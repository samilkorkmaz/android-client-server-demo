package server;

/**
 *
 * @author sam
 */
public class MyData {

    private final String name;
    private final double[] heights;
    private final boolean isDisplayHostNames;

    public MyData(String name, double[] heights, boolean isDisplayHostNames) {
        this.name = name;
        this.heights = heights;
        this.isDisplayHostNames = isDisplayHostNames;
    }

    public double[] getHeights() {
        return heights;
    }

    public String getName() {
        return name;
    }

    public boolean isIsDisplayHostNames() {
        return isDisplayHostNames;
    }
}
