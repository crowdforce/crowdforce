package space.crowdforce.jooq;

import org.jooq.Converter;
import space.crowdforce.jooq.geo.PGPoint;

public class PostgisPointConverter implements Converter<Object, PGPoint> {
    @Override public PGPoint from(Object obj) {
        if (obj instanceof PGPoint)
            return (PGPoint)obj;
        else
            return obj == null ? null : new PGPoint(obj.toString());
    }

    @Override public Object to(PGPoint point) {
        return point == null ? null : point.toSqlObject();
    }

    @Override public Class<Object> fromType() {
        return Object.class;
    }

    @Override public Class<PGPoint> toType() {
        return PGPoint.class;
    }
}