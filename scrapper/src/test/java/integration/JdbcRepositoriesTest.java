package integration;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.ScrapperApplication;
import ru.tinkoff.edu.java.scrapper.domain.mappers.LinkDataRowMapper;
import ru.tinkoff.edu.java.scrapper.domain.mappers.TrackDataRowMapper;
import ru.tinkoff.edu.java.scrapper.domain.models.Link;
import ru.tinkoff.edu.java.scrapper.domain.models.TgChat;
import ru.tinkoff.edu.java.scrapper.domain.models.Track;
import ru.tinkoff.edu.java.scrapper.domain.repositories.JdbcLinkRepository;
import ru.tinkoff.edu.java.scrapper.domain.repositories.JdbcTgChatRepository;
import ru.tinkoff.edu.java.scrapper.domain.repositories.JdbcTrackRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

// TODO: fix and create tests

@SpringBootTest(classes = ScrapperApplication.class)
public class JdbcRepositoriesTest extends IntegrationEnvironment{
    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcTgChatRepository tgChatRepository;

    @Autowired
    private JdbcTrackRepository trackRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Link> linkRowMapper = new LinkDataRowMapper();
    private final RowMapper<TgChat> tgChatRowMapper = new DataClassRowMapper<>(TgChat.class);
    private final RowMapper<Track> trackRowMapper = new TrackDataRowMapper();

    private @NotNull Link createNewLink(int param) {
        Link link = new Link();
        link.setDomain("https://github.com");
        link.setPath("/User/Repository" + param);
        link.setLastActivity(OffsetDateTime.now());
        return link;
    }

    private @NotNull TgChat createNewTgChat(Long param) {
        TgChat tgChat = new TgChat();
        tgChat.setId(param);
        return tgChat;
    }

    private @NotNull Track createNewTrack(Long tgChatId) {
        Track track = new Track();
        track.setChatId(tgChatId);
        return track;
    }

    @Test
    @Transactional
    @Rollback
    public void addTgChatTest() {
        TgChat tgChat = createNewTgChat(7777777L);

        assertThat(tgChatRepository.add(tgChat)).isEqualTo(1);
        assertThat(jdbcTemplate.query("SELECT * FROM chats", tgChatRowMapper)
                .get(0)
                .getId())
                .isEqualTo(7777777L);
    }

    @Test
    @Transactional
    @Rollback
    public void addLinkTest() {
        Link link = createNewLink(1);

        assertThat(linkRepository.add(link)).isEqualTo(1);
        assertThat(jdbcTemplate.query("SELECT * FROM links", linkRowMapper)
                .get(0)
                .getPath())
                .isEqualTo("/User/Repository1");
    }

    @Test
    @Transactional
    @Rollback
    public void addTrackTest() {
        Track track = createNewTrack(11L);
        tgChatRepository.add(createNewTgChat(11L));
        linkRepository.add(createNewLink(88));
        track.setLinkId(linkRepository.findAll().iterator().next().getId());

        assertThat(trackRepository.add(track)).isEqualTo(1);
        assertThat(jdbcTemplate.query("SELECT * FROM tracking", trackRowMapper)
                .get(0)
                .getLinkId())
                .isEqualTo(track.getLinkId());
    }

    @Test
    @Transactional
    @Rollback
    public void removeTgChatTest() {
        TgChat tgChat = createNewTgChat(222L);
        tgChatRepository.add(tgChat);

        assertThat(tgChatRepository.remove(tgChat)).isEqualTo(1);

        Throwable throwable = catchThrowable(
                () -> jdbcTemplate.query("SELECT * FROM chats", tgChatRowMapper).get(0)
        );

        assertThat(throwable).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @Transactional
    @Rollback
    public void removeLinkTest() {
        linkRepository.add(createNewLink(2));
        ArrayList<Link> links = (ArrayList<Link>) linkRepository.findAll();

        assertThat(linkRepository.remove(links.get(0))).isEqualTo(1);

        Throwable throwable = catchThrowable(
                () -> jdbcTemplate.query("SELECT * FROM links", linkRowMapper).get(0)
        );

        assertThat(throwable).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @Transactional
    @Rollback
    public void removeTrackTest() {
        Track track = createNewTrack(22L);
        tgChatRepository.add(createNewTgChat(22L));
        linkRepository.add(createNewLink(123));
        track.setLinkId(linkRepository.findAll().iterator().next().getId());
        trackRepository.add(track);

        assertThat(trackRepository.remove(track)).isEqualTo(1);

        Throwable throwable = catchThrowable(
                () -> jdbcTemplate.query("SELECT * FROM tracking", trackRowMapper).get(0)
        );

        assertThat(throwable).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @Transactional
    @Rollback
    public void findAllTgChatsTest() {
        tgChatRepository.add(createNewTgChat(555L));
        tgChatRepository.add(createNewTgChat(111L));
        tgChatRepository.add(createNewTgChat(999L));

        ArrayList<TgChat> tgChats = (ArrayList<TgChat>) tgChatRepository.findAll();

        assertThat(tgChats.size()).isEqualTo(3);
        assertThat(tgChats.get(0).getId()).isEqualTo(555L);
        assertThat(tgChats.get(1).getId()).isEqualTo(111L);
        assertThat(tgChats.get(2).getId()).isEqualTo(999L);
    }

    @Test
    @Transactional
    @Rollback
    public void findAllLinksTest() {
        linkRepository.add(createNewLink(3));
        linkRepository.add(createNewLink(4));
        linkRepository.add(createNewLink(5));

        ArrayList<Link> links = (ArrayList<Link>) linkRepository.findAll();

        assertThat(links.size()).isEqualTo(3);
        assertThat(links.get(0).getPath()).isEqualTo("/User/Repository3");
        assertThat(links.get(1).getPath()).isEqualTo("/User/Repository4");
        assertThat(links.get(2).getPath()).isEqualTo("/User/Repository5");
    }

    @Test
    @Transactional
    @Rollback
    public void findAllTrackingTest() {
        tgChatRepository.add(createNewTgChat(33L));
        tgChatRepository.add(createNewTgChat(44L));
        linkRepository.add(createNewLink(163));
        linkRepository.add(createNewLink(777));

        ArrayList<Link> links = (ArrayList<Link>) linkRepository.findAll();
        Track firstTrack = createNewTrack(33L);
        firstTrack.setLinkId(links.get(0).getId());
        Track secondTrack = createNewTrack(44L);
        secondTrack.setLinkId(links.get(0).getId());
        Track thirdTrack = createNewTrack(33L);
        thirdTrack.setLinkId(links.get(1).getId());

        trackRepository.add(firstTrack);
        trackRepository.add(secondTrack);
        trackRepository.add(thirdTrack);

        ArrayList<Track> tracks = (ArrayList<Track>) trackRepository.findAll();

        assertThat(tracks.size()).isEqualTo(3);
        assertThat(tracks.get(0).getLinkId()).isEqualTo(firstTrack.getLinkId());
        assertThat(tracks.get(1).getLinkId()).isEqualTo(secondTrack.getLinkId());
        assertThat(tracks.get(0).getLinkId()).isEqualTo(secondTrack.getLinkId());
    }
}