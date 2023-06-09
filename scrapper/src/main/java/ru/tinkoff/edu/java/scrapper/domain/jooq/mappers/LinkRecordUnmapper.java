package ru.tinkoff.edu.java.scrapper.domain.jooq.mappers;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import ru.tinkoff.edu.java.scrapper.domain.jooq.generated.tables.Links;
import ru.tinkoff.edu.java.scrapper.domain.jooq.generated.tables.records.LinksRecord;
import ru.tinkoff.edu.java.scrapper.domain.models.Link;

@RequiredArgsConstructor
public class LinkRecordUnmapper implements RecordUnmapper<Link, LinksRecord> {

    private final DSLContext dsl;

    @Override
    public @NotNull LinksRecord unmap(Link link) throws MappingException {
        LinksRecord linksRecord = dsl.newRecord(Links.LINKS, link);
        linksRecord.setLastActivity(link.getLastActivity().toLocalDateTime());
        linksRecord.setCheckedAt(link.getCheckedAt().toLocalDateTime());
        return linksRecord;
    }
}
