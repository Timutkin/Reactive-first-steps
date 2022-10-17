package ru.timutkin.reactivefirststeps.base;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Point {
    private int x;
    private int y;
}
