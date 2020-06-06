package space.crowdforce.dao

import org.springframework.dao.support.DataAccessUtils.singleResult
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.entities.SampleEntity
import java.sql.ResultSet

interface SampleDao {
    fun findById(id: Long): SampleEntity?
    fun insert(entity: SampleEntity): SampleEntity
}

@Repository
@Transactional(propagation = MANDATORY)
class SampleDaoImpl(private val jdbc: JdbcTemplate) : SampleDao {

    override fun findById(id: Long): SampleEntity? = singleResult(
        jdbc.query("""
            SELECT * 
            FROM cf_sample_entities 
            WHERE
                id = ?
        """.trimIndent(), arrayOf(id), SampleEntityMapper())
    )

    override fun insert(entity: SampleEntity): SampleEntity {
        val values = MapSqlParameterSource()
            .addValue("name", entity.name)

        val id = SimpleJdbcInsert(jdbc)
            .withTableName("cf_sample_entities")
            .usingGeneratedKeyColumns("id")
            .usingColumns(*values.values.keys.toTypedArray())
            .executeAndReturnKey(values)
            .toLong()

        return entity.copy(
            id = id
        )
    }
}

class SampleEntityMapper : RowMapper<SampleEntity> {
    override fun mapRow(rs: ResultSet, index: Int): SampleEntity =
        SampleEntity(
            rs.getLong("id"),
            rs.getString("name")
        )
}
