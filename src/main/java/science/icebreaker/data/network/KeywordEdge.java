package science.icebreaker.data.network;

public class KeywordEdge extends Edge {

    private double normalizedWeight;

    private String references;

    public KeywordEdge(int node1, int node2, int weight, double normalizedWeight, String references) {
        super(node1, node2, weight);
        this.normalizedWeight = normalizedWeight;
        this.references = references;
    }

    public double getNormalizedWeight() {
        return normalizedWeight;
    }

    public void setNormalizedWeight(double normalizedWeight) {
        this.normalizedWeight = normalizedWeight;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }
}
