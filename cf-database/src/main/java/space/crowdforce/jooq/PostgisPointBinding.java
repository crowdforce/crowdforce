package space.crowdforce.jooq;

import java.sql.SQLException;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.DSL;
import space.crowdforce.jooq.geo.PGPoint;

public class PostgisPointBinding implements Binding<Object, PGPoint> {
    private final PostgisPointConverter pointConverter = new PostgisPointConverter();

    @Override public Converter<Object, PGPoint> converter() {
        return pointConverter;
    }

    @Override public void sql(BindingSQLContext<PGPoint> ctx) {
        ctx.render().visit(DSL.sql("?::point"));
    }

    @Override public void register(BindingRegisterContext<PGPoint> ctx) {
        throw new UnsupportedOperationException();
    }

    @Override public void set(BindingSetStatementContext<PGPoint> ctx) throws SQLException {
        ctx.statement().setObject(ctx.index(), ctx.convert(converter()).value());
    }

    @Override public void set(BindingSetSQLOutputContext<PGPoint> ctx) {
        throw new UnsupportedOperationException();
    }

    @Override public void get(BindingGetResultSetContext<PGPoint> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()));
    }

    @Override public void get(BindingGetStatementContext<PGPoint> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getObject(ctx.index()));
    }

    @Override public void get(BindingGetSQLInputContext<PGPoint> ctx) {
        throw new UnsupportedOperationException();
    }
}
