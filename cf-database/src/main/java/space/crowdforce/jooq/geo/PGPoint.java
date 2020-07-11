package space.crowdforce.jooq.geo;

import java.sql.SQLException;
import org.postgresql.util.PGobject;

public class PGPoint {
    private static final String POINT_PG_TYPE = "POINT";

    private final double x;
    private final double y;

    public PGPoint(String sqlValue) {
        String[] tokens = sqlValue.substring(1, sqlValue.length() - 1).split(",");

        this.x = Double.parseDouble(tokens[0]);
        this.y = Double.parseDouble(tokens[1]);
    }

    public PGPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public PGobject toSqlObject() {
        PGobject obj = new PGobject();

        try {
            obj.setType(POINT_PG_TYPE);
            obj.setValue(String.format("(%s, %s)", x, y));
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return obj;
    }

    @Override public String toString() {
        return toSqlObject().toString();
    }
}
