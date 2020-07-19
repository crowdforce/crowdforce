/*
 * This file is generated by jOOQ.
 */
package space.crowdforce.model.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import space.crowdforce.model.Keys;
import space.crowdforce.model.Public;
import space.crowdforce.model.tables.records.GoalsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Goals extends TableImpl<GoalsRecord> {

    private static final long serialVersionUID = -457577398;

    /**
     * The reference instance of <code>public.goals</code>
     */
    public static final Goals GOALS = new Goals();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GoalsRecord> getRecordType() {
        return GoalsRecord.class;
    }

    /**
     * The column <code>public.goals.id</code>.
     */
    public final TableField<GoalsRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('goals_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.goals.name</code>.
     */
    public final TableField<GoalsRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>public.goals.description</code>.
     */
    public final TableField<GoalsRecord, String> DESCRIPTION = createField(DSL.name("description"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.goals.creation_time</code>.
     */
    public final TableField<GoalsRecord, LocalDateTime> CREATION_TIME = createField(DSL.name("creation_time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>public.goals.project_id</code>.
     */
    public final TableField<GoalsRecord, Integer> PROJECT_ID = createField(DSL.name("project_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.goals.progress_bar</code>.
     */
    public final TableField<GoalsRecord, Integer> PROGRESS_BAR = createField(DSL.name("progress_bar"), org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * Create a <code>public.goals</code> table reference
     */
    public Goals() {
        this(DSL.name("goals"), null);
    }

    /**
     * Create an aliased <code>public.goals</code> table reference
     */
    public Goals(String alias) {
        this(DSL.name(alias), GOALS);
    }

    /**
     * Create an aliased <code>public.goals</code> table reference
     */
    public Goals(Name alias) {
        this(alias, GOALS);
    }

    private Goals(Name alias, Table<GoalsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Goals(Name alias, Table<GoalsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Goals(Table<O> child, ForeignKey<O, GoalsRecord> key) {
        super(child, key, GOALS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<GoalsRecord, Integer> getIdentity() {
        return Keys.IDENTITY_GOALS;
    }

    @Override
    public UniqueKey<GoalsRecord> getPrimaryKey() {
        return Keys.GOALS_PKEY;
    }

    @Override
    public List<UniqueKey<GoalsRecord>> getKeys() {
        return Arrays.<UniqueKey<GoalsRecord>>asList(Keys.GOALS_PKEY);
    }

    @Override
    public List<ForeignKey<GoalsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GoalsRecord, ?>>asList(Keys.GOALS__GOALS_PROJECT_ID_FKEY);
    }

    public Projects projects() {
        return new Projects(this, Keys.GOALS__GOALS_PROJECT_ID_FKEY);
    }

    @Override
    public Goals as(String alias) {
        return new Goals(DSL.name(alias), this);
    }

    @Override
    public Goals as(Name alias) {
        return new Goals(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Goals rename(String name) {
        return new Goals(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Goals rename(Name name) {
        return new Goals(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, String, String, LocalDateTime, Integer, Integer> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}