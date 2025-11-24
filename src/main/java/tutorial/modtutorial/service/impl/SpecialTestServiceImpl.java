package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.Date;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.constant.SubjectsId;
import tutorial.modtutorial.domain.dto.test.request.SpecialTestDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestProtectedDTO;
import tutorial.modtutorial.domain.entity.SpecialTest;
import tutorial.modtutorial.repository.SpecialTestRepository;
import tutorial.modtutorial.service.SpecialTestService;
import tutorial.modtutorial.service.SubjectService;
import tutorial.modtutorial.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;
import static tutorial.modtutorial.utils.TextFieldUtils.isNotEmpty;
import static tutorial.modtutorial.utils.TextFieldUtils.validateTextFields;


@Service
public class SpecialTestServiceImpl implements SpecialTestService {
    private final SpecialTestRepository testRepository;
    private final UserService userService;
    private final SubjectService subjectService;

    public SpecialTestServiceImpl(
            SpecialTestRepository testRepository, UserService userService,
            SubjectService subjectService
    ) {
        this.testRepository = testRepository;
        this.subjectService = subjectService;
        this.userService = userService;
    }


    // ====================== SYSTEM ZONE =======================
    @Override
    public String createIfNotExistsByIdAndSubjectId(String testId, String subjectId, int number) {
        if (testRepository.existsByIdAndSubjectId(testId, subjectId)) {
            return testId;
        }

        // Create blank SpecialTest
        SpecialTest test = new SpecialTest();

        test.setSubjectId(subjectId);
        test.setQuestion("Savol " + number);
        test.setTrueAnswer("1");
        test.setFalseAnswer1("0");
        test.setFalseAnswer2("0");
        test.setFalseAnswer3("0");
        test.setSolution(null);
        test.setAnalysed(false);

        return testRepository.save(test).getId();
    }

    @Override
    public void updateUsageCount(String testId, int change) {
        testRepository.updateUsageCount(testId, change);
    }

    @Override
    public SpecialTest findById(String testId) {
        return testRepository.findById(testId).orElseThrow(() -> new EntityNotFoundException("SpecialTest with 'id'=[" + testId + "] not found."));
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    @Transactional(readOnly = true)
    public List<SpecialTestProtectedDTO> getSpecialTestsProtected(String subjectId, int maxUsageCount) {
        return testRepository.findAll(subjectId, maxUsageCount)
                .stream().map(this::toProtectedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createSpecialTest(SpecialTestDTO dto) {
        subjectService.throwIfNotExists(dto.getSubjectId());
        validateTextFields(dto.getQuestion(), 10);
        validateTextFields(dto.getTrueAnswer(), 1);
        validateTextFields(dto.getFalseAnswer1(), 1);
        validateTextFields(dto.getFalseAnswer2(), 1);
        validateTextFields(dto.getFalseAnswer3(), 1);
        if (dto.isAnalysed()) {
            validateTextFields(dto.getSolution(), 10);
        }

        SpecialTest test = new SpecialTest();

        test.setSubjectId(dto.getSubjectId());
        if (dto.getDifficulty() != null) {
            test.setDifficulty(dto.getDifficulty());
        }
        if (SubjectsId.contains(dto.getSubjectId())) {
            test.setText(dto.getText());
        }
        test.setQuestion(dto.getQuestion());
        test.setTrueAnswer(dto.getTrueAnswer());
        test.setFalseAnswer1(dto.getFalseAnswer1());
        test.setFalseAnswer2(dto.getFalseAnswer2());
        test.setFalseAnswer3(dto.getFalseAnswer3());
        test.setSolution(dto.getSolution());
        test.setAnalysed(dto.isAnalysed());

        testRepository.save(test);
    }

    @Override
    @Transactional
    public void updateSpecialTest(String testId, SpecialTestDTO dto) {
        SpecialTest test = getSpecialTestIfAllowed(testId);

        if (dto.getDifficulty() != null) {
            test.setDifficulty(dto.getDifficulty());
        }

        if (SubjectsId.contains(test.getSubjectId())) {
            test.setText(dto.getText());
        }

        if (isNotEmpty(dto.getQuestion())) {
            validateTextFields(dto.getQuestion(), 10);
            test.setQuestion(dto.getQuestion());
        }

        if (isNotEmpty(dto.getTrueAnswer())) {
            validateTextFields(dto.getTrueAnswer(), 1);
            test.setTrueAnswer(dto.getTrueAnswer());
        }

        if (isNotEmpty(dto.getFalseAnswer1())) {
            validateTextFields(dto.getFalseAnswer1(), 1);
            test.setFalseAnswer1(dto.getFalseAnswer1());
        }

        if (isNotEmpty(dto.getFalseAnswer2())) {
            validateTextFields(dto.getFalseAnswer2(), 1);
            test.setFalseAnswer2(dto.getFalseAnswer2());
        }

        if (isNotEmpty(dto.getFalseAnswer3())) {
            validateTextFields(dto.getFalseAnswer3(), 1);
            test.setFalseAnswer3(dto.getFalseAnswer3());
        }

        if (dto.isAnalysed()) {
            validateTextFields(dto.getSolution(), 10);
        }
        test.setSolution(dto.getSolution());
        test.setAnalysed(dto.isAnalysed());
    }

    @Override
    @Transactional
    public void deleteSpecialTest(String testId) {
        SpecialTest test = getSpecialTestIfAllowed(testId);
        if (test.getUsageCount() > 0) {
            throw new RuntimeException("SpecialTest has been used in SpecialBlocks");
        }

        testRepository.setDeleted(testId);
    }

    private SpecialTest getSpecialTestIfAllowed(String testId) {
        Optional<SpecialTest> optionalTest;

        if (SpecialUsers.contains(getCurrentUserId())) {
            optionalTest = testRepository.findById(testId);
        } else {
            optionalTest = testRepository.findByCreatorIdAndId(getCurrentUserId(), testId);
        }

        if (optionalTest.isEmpty() || optionalTest.get().isDeleted()) {
            throw new EntityNotFoundException("SpecialTest with 'id'=[" + testId + "] not found.");
        }

        return optionalTest.get();
    }

    private SpecialTestProtectedDTO toProtectedDTO(SpecialTest test) {
        SpecialTestProtectedDTO dto = new SpecialTestProtectedDTO();

        dto.setId(test.getId());
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
}
