package weird.ngraph;

import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;

import org.json.JSONObject;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeoDriver {
    private Driver driver;
    public long total_nodes;
    public long total_edges;
    public List<Long> dups;
    public List<JSONObject> nodes;
    public List<JSONObject> edges;

    public NeoDriver() {
        this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "admin"));
        this.dups = new ArrayList<Long>();
        this.nodes = new ArrayList<JSONObject>();
        this.edges = new ArrayList<JSONObject>();
        this.totals();
    }

    private void node(Node n) {
        if (!this.dups.contains(n.id())) {
            JSONObject j = new JSONObject();
            JSONObject data = new JSONObject();
            this.dups.add(n.id());
            List<String> lbs = new ArrayList<String>();
            for (String k: n.keys()) {
                data.put(k, n.get(k).asString() ) ;
            }
            for (String s : n.labels()) {
                lbs.add(s);
                if (Arrays.asList(new String[] {"Activity", "Service", "Provider", "Receiver"}).contains(s)) {
                    data.put("type", s );
                }
            }
            data.put("id", n.id() );
            data.put("labels", lbs );
            if (!data.has("type")) {
                data.put("type", lbs.get(lbs.size() - 1));
            }

            j.put("data", data);
            //System.out.println(j);
            this.nodes.add(j);
        }
    }

    private void edge(Relationship r) {
        if (!this.dups.contains(r.id())) {
            JSONObject j = new JSONObject();
            JSONObject data = new JSONObject();
            this.dups.add(r.id());
            data.put("id", r.id());
            data.put("source", r.startNodeId());
            data.put("target", r.endNodeId());
            data.put("type", r.type());
            j.put("data", data);
            this.edges.add(j);
        }
    }

    private void value(Value v) {
        if (v instanceof NodeValue) {
            this.node(v.asNode());
        } else if (v instanceof RelationshipValue) {
            this.edge(v.asRelationship());
        } else if (v instanceof PathValue) {
            for (Node n : v.asPath().nodes()) {
                this.node( n );
            }
            for (Relationship r : v.asPath().relationships() ) {
                this.edge( r );
            }
        }
    }

    public void totals() {
        try ( Session session = driver.session() )
        {
            StatementResult result = session.run("MATCH p=() RETURN count(nodes(p)) as r;");
            while ( result.hasNext() )
            {
                Record record = result.next();
                this.total_nodes = record.get( "r" ).asLong();
            }
            result = session.run("MATCH p=()-->() RETURN count(relationships(p)) as r;");
            while ( result.hasNext() )
            {
                Record record = result.next();
                this.total_edges = record.get( "r" ).asLong();
            }
        }
    }

    public String query(String query) {
        try ( Session session = driver.session() ) {
            try {
                StatementResult result = session.run(query);
                while ( result.hasNext() )
                {
                    Record record = result.next();
                    for (String key : record.keys()) {
                        Value val = record.get(key);
                        if (val instanceof  ListValue) {
                            for (Value v : val.values()) {
                                this.value(v);
                            }
                        } else {
                            this.value(val);
                        }
                    }
                }
            } catch (org.neo4j.driver.v1.exceptions.ClientException ex) {
                return ex.getMessage();
            }
        }
        return "";
    }

    public void close() {
        this.driver.close();
    }

}
