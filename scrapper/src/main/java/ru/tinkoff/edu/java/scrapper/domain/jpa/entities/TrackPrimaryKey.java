package ru.tinkoff.edu.java.scrapper.domain.jpa.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackPrimaryKey implements Serializable {
    private Long chatId;
    private Long linkId;
}
