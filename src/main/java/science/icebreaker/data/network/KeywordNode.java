package science.icebreaker.data.network;

public class KeywordNode extends Node {

    private int weight;

    private String[] categories;

    public KeywordNode(int id, String name, int weight, String[] categories) {
        super(id, name);
        this.weight = weight;
        this.categories = categories;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}
