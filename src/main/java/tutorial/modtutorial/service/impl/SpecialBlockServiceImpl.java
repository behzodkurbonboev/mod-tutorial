package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.Date;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.constant.SubjectsId;
import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.domain.dto.test.request.SpecialBlockDTO;
import tutorial.modtutorial.domain.dto.test.request.SpecialBlockFilter;
import tutorial.modtutorial.domain.dto.test.response.SpecialBlockProtectedDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialBlockPublicDTO;
import tutorial.modtutorial.domain.dto.test.response.GroupedByDate;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestProtectedDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestPublicDTO;
import tutorial.modtutorial.domain.entity.SpecialBlock;
import tutorial.modtutorial.domain.entity.SpecialBlockResult;
import tutorial.modtutorial.domain.entity.SpecialBlocksTests;
import tutorial.modtutorial.domain.entity.SpecialTest;
import tutorial.modtutorial.repository.SpecialBlockRepository;
import tutorial.modtutorial.repository.SpecialBlockResultRepository;
import tutorial.modtutorial.repository.SpecialBlocksTestsRepository;
import tutorial.modtutorial.service.SpecialBlockService;
import tutorial.modtutorial.service.SpecialTestService;
import tutorial.modtutorial.service.SubjectService;
import tutorial.modtutorial.service.UserService;
import tutorial.modtutorial.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;


@Service
public class SpecialBlockServiceImpl implements SpecialBlockService {
    @PersistenceContext
    private EntityManager entityManager;
    private final SpecialBlockRepository blockRepository;
    private final SpecialBlocksTestsRepository blocksTestsRepository;
    private final SpecialBlockResultRepository blockResultRepository;
    private final SpecialTestService testService;
    private final UserService userService;
    private final SubjectService subjectService;

    private static final int LIMIT = 6;
    private static final int MANDATORY_TESTS_SIZE = 30;
    private static final List<String> choices = new ArrayList<>();

    static {
        // 24 unique cases:
        choices.add("0123"); // twice
        choices.add("0132");
        choices.add("0213");
        choices.add("0231");
        choices.add("0312"); // twice
        choices.add("0321");
        choices.add("1023");
        choices.add("1032");
        choices.add("1203");
        choices.add("1230"); // twice
        choices.add("1302");
        choices.add("1320");
        choices.add("2013"); // twice
        choices.add("2031");
        choices.add("2103");
        choices.add("2130");
        choices.add("2301"); // twice
        choices.add("2310");
        choices.add("3012");
        choices.add("3021");
        choices.add("3102");
        choices.add("3120");
        choices.add("3201"); // twice
        choices.add("3210");

        // 6 repeated cases:
        choices.add("0123");
        choices.add("0312");
        choices.add("1230");
        choices.add("2013");
        choices.add("2301");
        choices.add("3201");
    }

    public SpecialBlockServiceImpl(
            SpecialBlockRepository blockRepository, SpecialBlockResultRepository blockResultRepository,
            SpecialTestService testService, SpecialBlocksTestsRepository blocksTestsRepository,
            UserService userService, SubjectService subjectService
    ) {
        this.blockRepository = blockRepository;
        this.blockResultRepository = blockResultRepository;
        this.testService = testService;
        this.blocksTestsRepository = blocksTestsRepository;
        this.userService = userService;
        this.subjectService = subjectService;
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    @Transactional(readOnly = true)
    public List<SpecialBlockProtectedDTO> getSpecialBlocksProtected() {
        List<SpecialBlock> blocks = blockRepository.findByOrderByDateDescCreatedDateDesc();

        return blocks.stream()
                .map(this::toSpecialBlockProtectedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialTestProtectedDTO> getSpecialBlock(String blockId) {
        throwIfNotExists(blockId);
        List<SpecialBlocksTests> blockTests = blocksTestsRepository.findAllByBlockId(blockId);

        return blockTests.stream()
                .map(blockTest -> toSpecialTestProtectedDTO(
                        testService.findById(blockTest.getTestId()),
                        blockTest.getNumber())
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createSpecialBlock(SpecialBlockDTO dto) {
        if (!SpecialUsers.contains(getCurrentUserId())) {
            throw new RuntimeException("Creating SpecialBlock is not allowed");
        }

        // check if subject and tests with this subject exists
        subjectService.throwIfNotExists(dto.getSubjectId());

        List<String> testsId = dto.getTestsId();
        if (testsId == null || testsId.size() != MANDATORY_TESTS_SIZE || new HashSet<>(testsId).size() != MANDATORY_TESTS_SIZE) {
            throw new RuntimeException("Number of SpecialTests must be " + MANDATORY_TESTS_SIZE);
        }

        List<String> existingTestIds = new ArrayList<>();
        for (int i = 0; i < MANDATORY_TESTS_SIZE; i++) {
            String testId = testService.createIfNotExistsByIdAndSubjectId(testsId.get(i), dto.getSubjectId(), i + 1);
            testService.updateUsageCount(testId, 1);
            existingTestIds.add(testId);
        }

        // create necessary entities
        SpecialBlock block = new SpecialBlock();

        LocalDateTime date = dto.getDate() != null ? dto.getDate() : LocalDateTime.now();
        block.setDate(date.toLocalDate().atStartOfDay()); // set date to start of the day (...T00:00:00)
        block.setCode(null);
        block.setSubjectId(dto.getSubjectId());
        block.setDifficulty(dto.getDifficulty());
        block.setAnalysed(false);
        block.setScoreSum(0);
        block.setSolvedCount(0);
        block.setVisible(false);

        block = blockRepository.save(block);

        // randomly shuffle 'choices'
        Collections.shuffle(choices);

        final String blockId = block.getId();
        for (int i = 0; i < MANDATORY_TESTS_SIZE; i++) {
            SpecialBlocksTests blockTest = new SpecialBlocksTests();

            blockTest.setBlockId(blockId);
            blockTest.setTestId(existingTestIds.get(i));
            blockTest.setNumber(i + 1);
            blockTest.setRule(choices.get(i));

            blocksTestsRepository.save(blockTest);
        }
    }

    @Override
    @Transactional
    public void updateSpecialBlock(String blockId, SpecialBlockDTO dto) {
        SpecialBlock block = getSpecialBlockIfAllowed(blockId);

        if (dto.getDate() != null) {
            block.setDate(dto.getDate());
        }

        if (dto.getDifficulty() != null) {
            block.setDifficulty(dto.getDifficulty());
        }

        if (dto.isAnalysed()) {
            // To make Block analysed all its Tests must be analysed
            blocksTestsRepository.findAllByBlockId(blockId)
                    .forEach(blockTest -> {
                        boolean analysed = testService.findById(blockTest.getTestId()).isAnalysed();

                        if (!analysed) {
                            throw new RuntimeException("All tests must be analysed to make block analysed.");
                        }
                    });
        }
        block.setAnalysed(dto.isAnalysed());
    }

    @Override
    @Transactional
    public void changeVisibility(String blockId) {
        SpecialBlock block = getSpecialBlockIfAllowed(blockId);

        block.setVisible(!block.isVisible());

        if (block.getAuthorId() != null) {
            int count = blockRepository.countVisibleSpecialBlocksByAuthorId(block.getAuthorId());
            userService.updateResourceCount(block.getAuthorId(),"blocks", count);
        }
    }

    @Override
    @Transactional
    public void transferSpecialBlock(String blockId, String userId) {
        userService.validate(userId);

        SpecialBlock block = getSpecialBlockIfAllowed(blockId);
        if (Objects.equals(block.getAuthorId(), userId)) {
            return;
        }

        if (block.isVisible()) {
            changeVisibility(blockId);
        }

        block.setAuthorId(userId);
    }


    // ======================== USER ZONE ========================
    @Override
    @Transactional(readOnly = true)
    public Slice<SpecialBlockPublicDTO> getSpecialBlocksPublicSliced(SpecialBlockFilter filter) {
        String userId = getCurrentUserId();

        // construct sql query:
        String sql = "SELECT * FROM special_block WHERE";

        if (notEmpty(filter.getSubjectsId())) {
            sql += " (subject_id IN :subjectsId) AND";
        }

        sql += " created_date < :minDate AND visible = true ORDER BY created_date DESC LIMIT :limit";

        Query query = entityManager.createNativeQuery(sql, SpecialBlock.class);
        setParams(query, filter);

        // get result and construct response
        List<SpecialBlock> blocks = query.getResultList();
        LocalDateTime minDate = blocks.isEmpty() ? Date.MIN_DATE : blocks.getLast().getCreatedDate();

        return Slice.of(
                blocks.stream()
                        .map(block -> toSpecialBlockPublicDTO(block, userId))
                        .collect(Collectors.toList()),
                minDate,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<SpecialBlockPublicDTO> getSpecialBlocksCreatedByUserSliced(SpecialBlockFilter filter) {
        String userId = getCurrentUserId();

        List<SpecialBlock> blocks = blockRepository.findSpecialBlocksCreatedByUserSliced(filter.getAuthorId(), filter.getMinDate(), LIMIT);
        LocalDateTime minDate = blocks.isEmpty() ? Date.MIN_DATE : blocks.getLast().getCreatedDate();

        return Slice.of(
                blocks.stream()
                        .map(block -> toSpecialBlockPublicDTO(block, userId))
                        .collect(Collectors.toList()),
                minDate,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<GroupedByDate<SpecialBlockPublicDTO>> getSpecialBlocksPublic(SpecialBlockFilter filter) {
        String userId = SecurityUtils.getCurrentUserId();

        List<SpecialBlock> blocks = blockRepository.findSpecialBlocks(filter.getMinDate(), LIMIT);
        List<GroupedByDate<SpecialBlockPublicDTO>> items = new ArrayList<>();

        blocks.stream()
                .map(block -> toSpecialBlockPublicDTO(block, userId))
                .collect(Collectors.groupingBy(
                        SpecialBlockPublicDTO::getDate,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .forEach((K, V) -> items.add(GroupedByDate.of(K, V)));

        LocalDateTime minDate = blocks.isEmpty() ? Date.MIN_DATE : blocks.getLast().getDate();

        return Slice.of(items, minDate, LIMIT);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<GroupedByDate<SpecialBlockPublicDTO>> getSpecialBlocksCreatedByUser(SpecialBlockFilter filter) {
        String userId = SecurityUtils.getCurrentUserId();

        List<SpecialBlock> blocks = blockRepository.findSpecialBlocksCreatedByUser(filter.getAuthorId(), filter.getMinDate(), LIMIT);
        List<GroupedByDate<SpecialBlockPublicDTO>> items = new ArrayList<>();

        blocks.stream()
                .map(block -> toSpecialBlockPublicDTO(block, userId))
                .collect(Collectors.groupingBy(
                        SpecialBlockPublicDTO::getDate,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .forEach((K, V) -> items.add(GroupedByDate.of(K, V)));

        LocalDateTime minDate = blocks.isEmpty() ? Date.MIN_DATE : blocks.getLast().getDate();

        return Slice.of(items, minDate, LIMIT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialTestPublicDTO> startSpecialBlock(String blockId) {
        throwIfNotExists(blockId);
        List<SpecialBlocksTests> blockTests = blocksTestsRepository.findAllByBlockId(blockId);

        return blockTests.stream()
                .map(blockTest -> toSpecialTestPublicDTO(
                        testService.findById(blockTest.getTestId()),
                        blockTest.getNumber(),
                        blockTest.getRule())
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<Integer, String> finishSpecialBlock(String blockId, Map<Integer, Integer> key) {
        throwIfNotExists(blockId);
        Map<Integer, String> map = new HashMap<>();

        int score = 0;
        List<SpecialBlocksTests> blockTests = blocksTestsRepository.findAllByBlockId(blockId);

        for (SpecialBlocksTests blockTest : blockTests) {
            int number = blockTest.getNumber();

            if (key.containsKey(number)) {
                // if there is a choice then check
                if (Objects.equals(blockTest.getRule().indexOf('0'), key.get(number))) {
                    map.put(number, "correct");
                    score++;
                } else {
                    map.put(number, "incorrect");
                }
            } else {
                // mark it as neutral
                map.put(number, "neutral");
            }
        }

        if (!blockResultRepository.existsByBlockIdAndUserId(blockId, SecurityUtils.getCurrentUserId())) {
            // save users' result
            SpecialBlockResult blockResult = new SpecialBlockResult();

            blockResult.setBlockId(blockId);
            blockResult.setKey(key);
            blockResult.setUserId(SecurityUtils.getCurrentUserId());
            blockResult.setScore(score);

            blockResultRepository.save(blockResult);

            // update blocks statistics
            blockRepository.updateScoreSumAndSolvedCount(blockId, score);
        }

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialTestPublicDTO> analiseSpecialBlock(String blockId) {
        throwIfNotExists(blockId);
        boolean analysed = blockRepository.isBlockAnalysed(blockId);

        Optional<SpecialBlockResult> blockResult = blockResultRepository.findByBlockIdAndUserId(blockId, SecurityUtils.getCurrentUserId());
        if (blockResult.isEmpty()) {
            throw new EntityNotFoundException("SpecialBlockResult for blockId = '" + blockId + "' and userId = '" + SecurityUtils.getCurrentUserId() + "' not found");
        }
        Map<Integer, Integer> userKey = blockResult.get().getKey();

        List<SpecialBlocksTests> blockTests = blocksTestsRepository.findAllByBlockId(blockId);

        return blockTests.stream()
                .map(blockTest -> {
                    SpecialTest test = testService.findById(blockTest.getTestId());

                    SpecialTestPublicDTO dto = toSpecialTestPublicDTO(test, blockTest.getNumber(), blockTest.getRule());

                    // populate additional fields
                    if (analysed) {
                        dto.setSolution(test.getSolution());
                    }
                    dto.setIndexU(userKey.get(blockTest.getNumber()));
                    dto.setIndexC(blockTest.getRule().indexOf('0'));

                    return dto;
                })
                .collect(Collectors.toList());
    }


    private void setParams(Query query, SpecialBlockFilter filter) {
        if (notEmpty(filter.getSubjectsId())) {
            query.setParameter("subjectsId", filter.getSubjectsId());
        }

        query.setParameter("minDate", filter.getMinDate());
        query.setParameter("limit", LIMIT);
    }

    private void throwIfNotExists(String blockId) {
        if (!blockRepository.existsById(blockId)) {
            throw new EntityNotFoundException("SpecialBlock with 'id'=[" + blockId + "] not found.");
        }
    }

    private SpecialTestPublicDTO toSpecialTestPublicDTO(SpecialTest test, int number, String rule) {
        SpecialTestPublicDTO dto = new SpecialTestPublicDTO();

        dto.setId(test.getId());
        dto.setNumber(number);
        dto.setQuestion(test.getQuestion());
        if (SubjectsId.contains(test.getSubjectId())) {
            dto.setText(test.getText());
        }

        // set shuffled choices
        String[] shuffle = rule.split("");
        dto.setChoice0(getAnswerByIndex(shuffle[0], test));
        dto.setChoice1(getAnswerByIndex(shuffle[1], test));
        dto.setChoice2(getAnswerByIndex(shuffle[2], test));
        dto.setChoice3(getAnswerByIndex(shuffle[3], test));

        return dto;
    }

    private String getAnswerByIndex(String idx, SpecialTest test) {
        return switch (idx) {
            case "0" -> test.getTrueAnswer();
            case "1" -> test.getFalseAnswer1();
            case "2" -> test.getFalseAnswer2();
            case "3" -> test.getFalseAnswer3();
            default -> null;
        };
    }

    private SpecialBlock getSpecialBlockIfAllowed(String blockId) {
        Optional<SpecialBlock> optionalSpecialBlock;

        if (SpecialUsers.contains(getCurrentUserId())) {
            optionalSpecialBlock = blockRepository.findById(blockId);
        } else {
            optionalSpecialBlock = blockRepository.findByCreatorIdAndId(getCurrentUserId(), blockId);
        }

        return optionalSpecialBlock.orElseThrow(() -> new EntityNotFoundException("SpecialBlock with 'id'=[" + blockId + "] not found."));
    }

    private SpecialBlockPublicDTO toSpecialBlockPublicDTO(SpecialBlock block, String userId) {
        SpecialBlockPublicDTO dto = new SpecialBlockPublicDTO();

        dto.setId(block.getId());
        dto.setDate(Date.toDate(block.getDate()));
        dto.setCode(block.getCode());
        dto.setSubject(block.getSubject().getName());
        dto.setDifficulty(block.getDifficulty());
        if (block.getAuthorId() != null) {
            dto.setAuthor(userService.toAuthorProfile(block.getAuthorId()));
        }
        if (block.getSolvedCount() != 0) {
            dto.setAverageScore(String.format("%.2f", block.getScoreSum() * 1.0 / block.getSolvedCount()));
        }
        dto.setAnalysed(block.isAnalysed());
        dto.setSolvedCount(block.getSolvedCount());
        Optional<SpecialBlockResult> optionalResult = blockResultRepository.findByBlockIdAndUserId(block.getId(), userId);
        if (optionalResult.isPresent()) {
            dto.setSolvedByUser(true);
            dto.setUserScore(optionalResult.get().getScore());
        }

        return dto;
    }

    private SpecialBlockProtectedDTO toSpecialBlockProtectedDTO(SpecialBlock block) {
        SpecialBlockProtectedDTO dto = new SpecialBlockProtectedDTO();

        dto.setId(block.getId());
        dto.setDate(block.getDate());
        dto.setCode(block.getCode());
        dto.setSubject(subjectService.toDTO(block.getSubjectId()));
        dto.setDifficulty(block.getDifficulty());
        if (block.getAuthorId() != null) {
            dto.setAuthor(userService.toAuthor(block.getAuthorId()));
        }
        dto.setAnalysed(block.isAnalysed());
        dto.setCreatedDate(block.getCreatedDate().format(Date.FORMAT));
        dto.setUpdatedDate(block.getUpdatedDate().format(Date.FORMAT));
        dto.setCreatedBy(userService.toAuthor(block.getCreatorId()));
        dto.setUpdatedBy(userService.toAuthor(block.getUpdaterId()));
        if (block.getSolvedCount() != 0) {
            dto.setAverageScore(String.format("%.2f", block.getScoreSum() * 1.0 / block.getSolvedCount()));
        }
        dto.setSolvedCount(block.getSolvedCount());
        dto.setVisible(block.isVisible());

        return dto;
    }

    private SpecialTestProtectedDTO toSpecialTestProtectedDTO(SpecialTest test, int number) {
        SpecialTestProtectedDTO dto = new SpecialTestProtectedDTO();

        dto.setId(test.getId());
        dto.setNumber(number);
        dto.setSubject(subjectService.toDTO(test.getSubjectId()));
        dto.setText(test.getText());
        dto.setQuestion(test.getQuestion());
        dto.setTrueAnswer(test.getTrueAnswer());
        dto.setFalseAnswer1(test.getFalseAnswer1());
        dto.setFalseAnswer2(test.getFalseAnswer2());
        dto.setFalseAnswer3(test.getFalseAnswer3());
        dto.setSolution(test.getSolution());
        dto.setDifficulty(test.getDifficulty());
        dto.setAnalysed(test.isAnalysed());
        dto.setCreatedDate(test.getCreatedDate().format(Date.FORMAT));
        dto.setUpdatedDate(test.getUpdatedDate().format(Date.FORMAT));
        dto.setCreatedBy(userService.toAuthor(test.getCreatorId()));
        dto.setUpdatedBy(userService.toAuthor(test.getUpdaterId()));
        dto.setUsageCount(test.getUsageCount());

        return dto;
    }

    private static boolean notEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
