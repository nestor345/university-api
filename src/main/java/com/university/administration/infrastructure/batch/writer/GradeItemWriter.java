package com.university.administration.infrastructure.batch.writer;

import com.university.administration.infrastructure.persistence.entity.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class GradeItemWriter implements ItemWriter<Grade> {

    private final DataSource dataSource;

    @Override
    public void write(Chunk<? extends Grade> chunk) {

        NamedParameterJdbcTemplate jdbcTemplate =
                new NamedParameterJdbcTemplate(dataSource);

        String sql = """
            INSERT INTO grades (id, enrollment_id, grade_type, grade_value, created_at)
            VALUES (:id, :enrollmentId, :gradeType, :gradeValue, NOW())
            ON CONFLICT (enrollment_id, grade_type)
            DO UPDATE SET grade_value = EXCLUDED.grade_value
            """;

        for (Grade item : chunk.getItems()) {

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", item.getId())
                    .addValue("enrollmentId", item.getEnrollment().getId())
                    .addValue("gradeType", item.getGradeType().name())
                    .addValue("gradeValue", item.getGradeValue());

            jdbcTemplate.update(sql, params);
        }
    }
}
