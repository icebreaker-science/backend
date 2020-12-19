package science.icebreaker.data.network;


public class Node {

    private int id;

    private String name;


    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
