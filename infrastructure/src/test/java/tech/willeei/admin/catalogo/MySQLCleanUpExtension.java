package tech.willeei.admin.catalogo;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tech.willeei.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import tech.willeei.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import tech.willeei.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import tech.willeei.admin.catalogo.infrastructure.video.persistence.VideoRepository;

class MySQLCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(VideoRepository.class),
                appContext.getBean(CastMemberRepository.class),
                appContext.getBean(GenreRepository.class),
                appContext.getBean(CategoryRepository.class)
        ));
    }

    @SuppressWarnings("rawtypes")
    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
