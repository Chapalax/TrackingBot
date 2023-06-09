package ru.tinkoff.edu.java.scrapper.web.clients;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.tinkoff.edu.java.scrapper.web.clients.dto.GitHubResponse;
import ru.tinkoff.edu.java.scrapper.web.clients.interfaces.WebClientGitHub;

public class GitHubClient implements WebClientGitHub {

    @Value("${baseurl.github}")
    private String gitHubBaseUrl;

    private final WebClient webClient;

    public GitHubClient() {
        webClient = WebClient.create(gitHubBaseUrl);
    }

    public GitHubClient(@URL String url) {
        webClient = WebClient.create(url);
    }

    @Override
    public GitHubResponse fetchGitHubRepository(String user, String repository) {
        return webClient.get()
                .uri("/repos/{user}/{repo}", user, repository)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(RuntimeException.class)
                        .flatMap(error -> Mono.error(new RuntimeException("GitHub API Exception"))))
                .bodyToMono(GitHubResponse.class)
                .block();
    }
}
