/*
 * This file is generated by jOOQ.
 */
package space.crowdforce.model.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import space.crowdforce.model.Keys;
import space.crowdforce.model.Public;
import space.crowdforce.model.tables.records.ActivityParticipantsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActivityParticipants extends TableImpl<ActivityParticipantsRecord> {

    private static final long serialVersionUID = -706540257;

    /**
     * The reference instance of <code>public.activity_participants</code>
     */
    public static final ActivityParticipants ACTIVITY_PARTICIPANTS = new ActivityParticipants();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActivityParticipantsRecord> getRecordType() {
        return ActivityParticipantsRecord.class;
    }

    /**
     * The column <code>public.activity_participants.activity_id</code>.
     */
    public final TableField<ActivityParticipantsRecord, Integer> ACTIVITY_ID = createField(DSL.name("activity_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.activity_participants.user_id</code>.
     */
    public final TableField<ActivityParticipantsRecord, Integer> USER_ID = createField(DSL.name("user_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>public.activity_participants</code> table reference
     */
    public ActivityParticipants() {
        this(DSL.name("activity_participants"), null);
    }

    /**
     * Create an aliased <code>public.activity_participants</code> table reference
     */
    public ActivityParticipants(String alias) {
        this(DSL.name(alias), ACTIVITY_PARTICIPANTS);
    }

    /**
     * Create an aliased <code>public.activity_participants</code> table reference
     */
    public ActivityParticipants(Name alias) {
        this(alias, ACTIVITY_PARTICIPANTS);
    }

    private ActivityParticipants(Name alias, Table<ActivityParticipantsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ActivityParticipants(Name alias, Table<ActivityParticipantsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ActivityParticipants(Table<O> child, ForeignKey<O, ActivityParticipantsRecord> key) {
        super(child, key, ACTIVITY_PARTICIPANTS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<UniqueKey<ActivityParticipantsRecord>> getKeys() {
        return Arrays.<UniqueKey<ActivityParticipantsRecord>>asList(Keys.ACTIVITY_PARTICIPANTS_USER_ID_ACTIVITY_ID_KEY);
    }

    @Override
    public List<ForeignKey<ActivityParticipantsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ActivityParticipantsRecord, ?>>asList(Keys.ACTIVITY_PARTICIPANTS__ACTIVITY_PARTICIPANTS_ACTIVITY_ID_FKEY, Keys.ACTIVITY_PARTICIPANTS__ACTIVITY_PARTICIPANTS_USER_ID_FKEY);
    }

    public Activities activities() {
        return new Activities(this, Keys.ACTIVITY_PARTICIPANTS__ACTIVITY_PARTICIPANTS_ACTIVITY_ID_FKEY);
    }

    public Users users() {
        return new Users(this, Keys.ACTIVITY_PARTICIPANTS__ACTIVITY_PARTICIPANTS_USER_ID_FKEY);
    }

    @Override
    public ActivityParticipants as(String alias) {
        return new ActivityParticipants(DSL.name(alias), this);
    }

    @Override
    public ActivityParticipants as(Name alias) {
        return new ActivityParticipants(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityParticipants rename(String name) {
        return new ActivityParticipants(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityParticipants rename(Name name) {
        return new ActivityParticipants(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, Integer> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}