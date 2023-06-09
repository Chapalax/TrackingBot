package ru.tinkoff.edu.java.scrapper.domain.jdbc.repositories;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.domain.interfaces.LinkRepository;
import ru.tinkoff.edu.java.scrapper.domain.jdbc.mappers.LinkDataRowMapper;
import ru.tinkoff.edu.java.scrapper.domain.models.Link;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {

    @Value("${check.interval}")
    private Long checkInterval;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Link> rowMapper = new LinkDataRowMapper();

    private final String SQL_ADD = "INSERT INTO links (path, last_activity, action_count) "
        + "VALUES (:path, :lastActivity, :actionCount) RETURNING *";
    private final String SQL_REMOVE = "DELETE FROM links WHERE path=:path";
    private final String SQL_FIND_ALL = "SELECT * FROM links";
    private final String SQL_IS_EXISTS = "SELECT EXISTS(SELECT 1 FROM links WHERE path=:path)";
    private final String SQL_FIND_ONE_BY_URL = "SELECT 1 FROM links WHERE path=:path";
    private final String SQL_FIND_ONE_BY_ID = "SELECT 1 FROM links WHERE id=:id";
    private final String SQL_FIND_UPDATES = "SELECT * FROM links WHERE checked_at<:time";
    private final String SQL_UPDATE_LINK = "UPDATE links SET last_activity=:lastActivity, "
        + "action_count=:actionCount, checked_at=:checkedAt WHERE id=:id";

    @Override
    @Transactional
    public Link add(@NotNull Link object) {
        return jdbcTemplate.queryForObject(SQL_ADD, new BeanPropertySqlParameterSource(object), rowMapper);
    }

    @Override
    @Transactional
    public int remove(@NotNull Link object) {
        return jdbcTemplate.update(SQL_REMOVE, Map.of("path", object.getPath()));
    }

    @Override
    @Transactional
    public List<Link> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, rowMapper);
    }

    @Override
    @Transactional
    public Boolean isExists(@NotNull Link link) {
        return jdbcTemplate.queryForObject(SQL_IS_EXISTS, Map.of("path", link.getPath()), Boolean.class);
    }

    @Override
    @Transactional
    public Link findByUrl(@NotNull Link link) {
        return jdbcTemplate.queryForObject(SQL_FIND_ONE_BY_URL, new BeanPropertySqlParameterSource(link), rowMapper);
    }

    @Override
    @Transactional
    public Link findById(@NotNull Link link) {
        return jdbcTemplate.queryForObject(SQL_FIND_ONE_BY_ID, new BeanPropertySqlParameterSource(link), rowMapper);
    }

    @Override
    @Transactional
    public List<Link> findAllToUpdate() {
        return jdbcTemplate.query(SQL_FIND_UPDATES,
                Map.of("time", Timestamp.from(Instant.now().minus(checkInterval, ChronoUnit.MINUTES))),
                rowMapper);
    }

    @Override
    @Transactional
    public void update(@NotNull Link link) {
        jdbcTemplate.update(SQL_UPDATE_LINK,
                Map.of("lastActivity", link.getLastActivity(),
                        "actionCount", link.getActionCount(),
                        "checkedAt", OffsetDateTime.now(),
                        "id", link.getId()));
    }
}
