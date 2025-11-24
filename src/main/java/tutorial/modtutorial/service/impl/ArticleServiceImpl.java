package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.Date;
import tutorial.modtutorial.constant.FilesUrls;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.domain.dto.article.request.ArticleFilter;
import tutorial.modtutorial.domain.dto.article.request.ArticleDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticleContentDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticleProtectedDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticlePublicDTO;
import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.domain.entity.Article;
import tutorial.modtutorial.repository.ArticleRepository;
import tutorial.modtutorial.service.ArticleService;
import tutorial.modtutorial.service.FileService;
import tutorial.modtutorial.service.SubjectService;
import tutorial.modtutorial.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;
import static tutorial.modtutorial.utils.TextFieldUtils.isNotEmpty;
import static tutorial.modtutorial.utils.TextFieldUtils.validateTextFields;


@Service
public class ArticleServiceImpl implements ArticleService {
    @PersistenceContext
    private EntityManager entityManager;
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final SubjectService subjectService;
    private final FileService fileService;
    private final TagMapper tagMapper; // when mapping tagIds existence of subjectId also be verified.

    private static final int LIMIT = 4;

    public ArticleServiceImpl(
            ArticleRepository articleRepository, UserService userService,
            SubjectService subjectService, FileService fileService, TagMapper tagMapper
    ) {
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.subjectService = subjectService;
        this.fileService = fileService;
        this.tagMapper = tagMapper;
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    @Transactional(readOnly = true)
    public List<ArticleProtectedDTO> getArticlesProtected() {
        return articleRepository.findAllByOrderByUpdatedDateDesc().stream()
                .map(this::toProtectedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createArticle(ArticleDTO dto) {
        // do validation
        subjectService.throwIfNotExists(dto.getSubjectId());
        validateTextFields(dto.getTitle(), 10);
        validateTextFields(dto.getDescription(), 200);
        validateTextFields(dto.getContent(), 200);
        FilesUrls.validateUrl(dto.getShareUrl());

        Article article = new Article();

        article.setTitle(dto.getTitle());
        article.setSubjectId(dto.getSubjectId());
        article.setTags(tagMapper.toEntitiesSet(dto.getTagsId(), dto.getSubjectId()));
        article.setDescription(dto.getDescription());
        article.setContent(dto.getContent());
        article.setShareUrl(dto.getShareUrl());

        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void updateArticle(String articleId, ArticleDTO dto) {
        Article article = getArticleIfAllowed(articleId);
        String oldShareUrl = article.getShareUrl();

        if (isNotEmpty(dto.getTitle())) {
            validateTextFields(dto.getTitle(), 10);
            article.setTitle(dto.getTitle());
        }

        if (isNotEmpty(dto.getDescription())) {
            validateTextFields(dto.getDescription(), 200);
            FilesUrls.validateUrl(dto.getShareUrl());

            article.setDescription(dto.getDescription());
            article.setShareUrl(dto.getShareUrl());
        }

        if (isNotEmpty(dto.getContent())) {
            validateTextFields(dto.getContent(), 200);
            article.setContent(dto.getContent());
        }

        if (notEmpty(dto.getTagsId())) {
            article.setTags(tagMapper.toEntitiesSet(dto.getTagsId(), article.getSubjectId()));
        }

        articleRepository.save(article);

        // if everything is ok till this point, then delete old image
        if (dto.getShareUrl() != null && !Objects.equals(oldShareUrl, dto.getShareUrl())) {
            fileService.deleteByUrl(oldShareUrl);
        }
    }

    @Override
    @Transactional
    public void changeVisibility(String articleId) {
        Article article = getArticleIfAllowed(articleId);

        article.setVisible(!article.isVisible());

        int count = articleRepository.countVisibleArticlesByAuthorId(article.getCreatorId());
        userService.updateResourceCount(article.getCreatorId(), "articles", count);
    }

    @Override
    @Transactional
    public void transferArticle(String articleId, String userId) {
        userService.validate(userId);

        Article article = getArticleIfAllowed(articleId);
        if (Objects.equals(article.getCreatorId(), userId)) {
            return;
        }

        if (article.isVisible()) {
            changeVisibility(articleId);
        }

        article.setCreatorId(userId);
    }

    @Override
    @Transactional
    public void deleteArticle(String articleId) {
        Article article = getArticleIfAllowed(articleId);

        article.setVisible(false);
        article.setDeleted(true);

        int count = articleRepository.countVisibleArticlesByAuthorId(article.getCreatorId());
        userService.updateResourceCount(article.getCreatorId(), "articles", count);
    }


    // ======================== USER ZONE ========================
    @Override
    @Transactional(readOnly = true)
    public Slice<ArticlePublicDTO> getArticlesPublic(ArticleFilter filter) {
        String userId = getCurrentUserId();

        // construct sql query:
        String sql = "SELECT * FROM article WHERE";
        final String subjectsFilter = " (subject_id IN :subjectsId";
        final String tagsFilter = " id IN (SELECT at.article_id FROM articles_tags AS at WHERE tag_id IN :tagsId)";

        if (notEmpty(filter.getSubjectsId())) {
            sql += subjectsFilter;
            if (notEmpty(filter.getTagsId())) {
                sql += " OR" + tagsFilter;
            }
            sql += ") AND";
        } else if (notEmpty(filter.getTagsId())) {
            sql += tagsFilter + " AND";
        }

        sql += " created_date < :minDate AND visible = true ORDER BY created_date DESC LIMIT :limit";

        Query query = entityManager.createNativeQuery(sql, Article.class);
        setParams(query, filter);

        // get result and construct response
        List<Article> articles = query.getResultList();
        LocalDateTime minDate = articles.isEmpty() ? Date.MIN_DATE : articles.getLast().getCreatedDate();

        return Slice.of(
                articles.stream()
                        .map(article -> toPublicDTO(article, userId))
                        .collect(Collectors.toList()),
                minDate,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ArticlePublicDTO> getArticlesSavedByUser(ArticleFilter filter) {
        String userId = getCurrentUserId();

        List<Article> articles = articleRepository.findArticlesSavedByUser(userId, filter.getMinDate(), LIMIT);
        LocalDateTime minDate = articles.isEmpty() ? Date.MIN_DATE : articles.getLast().getCreatedDate();

        return Slice.of(
                articles.stream()
                        .map(article -> toPublicDTO(article, userId))
                        .collect(Collectors.toList()),
                minDate,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ArticlePublicDTO> getArticlesCreatedByUser(ArticleFilter filter) {
        String userId = getCurrentUserId();

        List<Article> articles = articleRepository.findArticlesCreatedByUser(filter.getAuthorId(), filter.getMinDate(), LIMIT);
        LocalDateTime minDate = articles.isEmpty() ? Date.MIN_DATE : articles.getLast().getCreatedDate();

        return Slice.of(
                articles.stream()
                        .map(article -> toPublicDTO(article, userId))
                        .collect(Collectors.toList()),
                minDate,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleContentDTO getArticleContent(String articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article with 'id'=[" + articleId + "] not found."));

        ArticleContentDTO dto = new ArticleContentDTO();
        dto.setArticleId(articleId);
        dto.setContent(article.getContent());

        return dto;
    }

    @Override
    @Transactional
    public void handleLike(String articleId) {
        throwIfNotExists(articleId);
        articleRepository.addOrDeleteLiked(articleId, getCurrentUserId());
    }

    @Override
    @Transactional
    public void handleSave(String articleId) {
        throwIfNotExists(articleId);
        articleRepository.addOrDeleteSaved(articleId, getCurrentUserId());
    }

    @Override
    @Transactional
    public void handleSee(String articleId) {
        throwIfNotExists(articleId);
        articleRepository.addOrNothingSeen(articleId, getCurrentUserId());
    }

    @Override
    @Transactional
    public void handleShare(String articleId) {
        throwIfNotExists(articleId);
        articleRepository.incrementShareCount(articleId);
    }


    private void setParams(Query query, ArticleFilter filter) {
        if (notEmpty(filter.getSubjectsId())) {
            query.setParameter("subjectsId", filter.getSubjectsId());
        }

        if (notEmpty(filter.getTagsId())) {
            query.setParameter("tagsId", filter.getTagsId());
        }

        query.setParameter("minDate", filter.getMinDate());
        query.setParameter("limit", LIMIT);
    }

    private Article getArticleIfAllowed(String articleId) {
        Optional<Article> optionalArticle;

        if (SpecialUsers.contains(getCurrentUserId())) {
            optionalArticle = articleRepository.findById(articleId);
        } else {
            optionalArticle = articleRepository.findByCreatorIdAndId(getCurrentUserId(), articleId);
        }

        if (optionalArticle.isEmpty() || optionalArticle.get().isDeleted()) {
            throw new EntityNotFoundException("Article with 'id'=[" + articleId + "] not found.");
        }

        return optionalArticle.get();
    }

    private void throwIfNotExists(String articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new EntityNotFoundException("Article with 'id'=[" + articleId + "] not found.");
        }
    }

    private ArticlePublicDTO toPublicDTO(Article article, String userId) {
        ArticlePublicDTO dto = new ArticlePublicDTO();

        dto.setId(article.getId());
        dto.setSubject(article.getSubject().getName());
        dto.setTags(tagMapper.toNameList(article.getTags()));
        dto.setDate(article.getCreatedDate().format(Date.FORMAT));
        dto.setAuthor(userService.toAuthor(article.getCreatorId()));
        dto.setDescription(article.getDescription());
        dto.setShareUrl(article.getShareUrl());
        dto.setLikeCount(article.getLikeCount());
        dto.setSaveCount(article.getSaveCount());
        dto.setSeenCount(article.getSeenCount());
        dto.setShareCount(article.getShareCount());
        dto.setLikedByUser(articleRepository.isLikedByUser(article.getId(), userId));
        dto.setSavedByUser(articleRepository.isSavedByUser(article.getId(), userId));
        dto.setSeenByUser(articleRepository.isSeenByUser(article.getId(), userId));
        dto.setAccessType(article.getAccessType());

        return dto;
    }

    private ArticleProtectedDTO toProtectedDTO(Article article) {

        ArticleProtectedDTO dto = new ArticleProtectedDTO();

        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setTags(tagMapper.toDTOList(article.getTags()));
        dto.setSubject(subjectService.toDTO(article.getSubjectId()));
        dto.setDescription(article.getDescription());
        dto.setShareUrl(article.getShareUrl());
        dto.setContent(article.getContent());
        dto.setAccessType(article.getAccessType());
        dto.setVisible(article.isVisible());
        dto.setLikeCount(article.getLikeCount());
        dto.setSaveCount(article.getSaveCount());
        dto.setSeenCount(article.getSeenCount());
        dto.setShareCount(article.getShareCount());
        dto.setCreatedDate(article.getCreatedDate().format(Date.FORMAT));
        dto.setUpdatedDate(article.getUpdatedDate().format(Date.FORMAT));
        dto.setCreatedBy(userService.toAuthor(article.getCreatorId()));
        dto.setUpdatedBy(userService.toAuthor(article.getUpdaterId()));
        dto.setLikedByUser(articleRepository.isLikedByUser(article.getId(), getCurrentUserId()));
        dto.setSavedByUser(articleRepository.isSavedByUser(article.getId(), getCurrentUserId()));
        dto.setSeenByUser(articleRepository.isSeenByUser(article.getId(), getCurrentUserId()));

        return dto;
    }

    private static boolean notEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
