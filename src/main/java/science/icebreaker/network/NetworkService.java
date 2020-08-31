package science.icebreaker.network;

import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.neo4j.driver.Values.parameters;


@Service
public class NetworkService implements AutoCloseable {

    /**
     * TODO Find a better solution to limit the size of the results.
     *
     * The number of the returned edges per request is limited since the browser is not able to process to large graphs.
     * It is not defined which edges will be ignored. For example, it might happen that the ego graph endpoint returns
     * a graph where not all nodes are connected to the ego node.
     */
    private static final int MAX_RETURNED_EDGES = 10000;

    private final Driver driver;

    private final PaperRepository paperRepository;


    public NetworkService(
            @Value("${icebreaker.neo4j.host}") String host,
            @Value("${icebreaker.neo4j.port}") String port,
            @Value("${icebreaker.neo4j.username}") String username,
            @Value("${icebreaker.neo4j.password}") String password,
            PaperRepository paperRepository
    ) {
        this.paperRepository = paperRepository;
        driver = GraphDatabase.driver("bolt://" + host + ":" + port, AuthTokens.basic(username, password));
    }


    public int countNodes() {
        try (Session session = driver.session()) {
            Result result = session.run("match (n: Topic) return count(n)");
            return result.single().get(0).asInt();
        }
    }


    public List<Node> getAllNodes() {
        try (Session session = driver.session()) {
            Result result = session.run("match (n: Topic) return n.name, n.weight");
            return result.stream()
                    .map((record) -> new Node(record.get("n.name").asString(), record.get("n.weight").asInt()))
                    .collect(Collectors.toList());
        }
    }


    public List<Edge> getEgoGraph(String nodeName) {
        try (Session session = driver.session()) {
            Result result = session.run("match (n:Topic)-[e]-(m: Topic) " +
                            "where n.name = $name " +
                            "with collect(m.name) as ms, n " +
                            "match (m1: Topic)-[e]-(m2: Topic) " +
                            "where (m1.name in ms or m1.name = n.name) and (m2.name in ms or m2.name = n.name) " +
                            "and m1.name < m2.name " +
                            "return m1.name, m2.name, e.weight, e.normalizedWeight, e.references " +
                            "limit " + MAX_RETURNED_EDGES,
                    parameters("name", nodeName));
            return result.stream()
                    .map((record) -> new Edge(
                            record.get("m1.name").asString(), record.get("m2.name").asString(),
                            record.get("e.weight").asInt(), record.get("e.normalizedWeight").asDouble(),
                            record.get("e.references").asString()
                    ))
                    .collect(Collectors.toList());
        }
    }


    public List<Edge> getShortestPathGraph(List<String> nodes) {
        try (Session session = driver.session()) {
            Result result = session.run("match (n:Topic), (m:Topic), " +
                            "p = allShortestPaths((n)-[*..]-(m)) " +
                            "where n.name in $nodes and m.name in $nodes and n.name < m.name " +
                            "unwind nodes(p) as tmp " +
                            "with collect(distinct(tmp)) as nodes_on_path " +
                            "match (n:Topic)-[e]-(m:Topic) " +
                            "where n in nodes_on_path and m in nodes_on_path and n.name < m.name " +
                            "return n.name, m.name, e.weight, e.normalizedWeight, e.references " +
                            "limit " + MAX_RETURNED_EDGES,
                    parameters("nodes", nodes));
            return result.stream()
                    .map((record) -> new Edge(
                            record.get("n.name").asString(), record.get("m.name").asString(),
                            record.get("e.weight").asInt(), record.get("e.normalizedWeight").asDouble(),
                            record.get("e.references").asString()
                    ))
                    .collect(Collectors.toList());
        }
    }


    public List<Paper> getPapers(List<Integer> ids) {
        return paperRepository.findAllByIds(ids);
    }


    @Override
    public void close() throws Exception {
        driver.close();
    }
}
