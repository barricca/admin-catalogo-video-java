package tech.willeei.admin.catalogo.application.genre.delete;

import tech.willeei.admin.catalogo.domain.genre.GenreGateway;
import tech.willeei.admin.catalogo.domain.genre.GenreID;

import java.util.Objects;

public class DefaultDeleteGenreUseCase extends DeleteGenreUseCase {

    private final GenreGateway genreGateway;

    public DefaultDeleteGenreUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public void execute(final String anIn) {
        this.genreGateway.deleteById(GenreID.from(anIn));
    }
}
