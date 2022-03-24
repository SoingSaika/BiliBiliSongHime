package club.sentinc.bilibili.songhime.ruankao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class RuanKaoEngine {

    private String RUANKAO_DATA_PATH = "./data/ruankao/";
    private String RUANKAO_IMG = RUANKAO_DATA_PATH + "img/";
    private String RUANKAO_SIMPLE = RUANKAO_DATA_PATH + "simple-tr.json";
    //TODO Regex get Exam
    private final String DEFAULT_EXAM_REGEX = "";
    private List<Exam> exams;

    public void loadData() throws IOException {
        String simpleJson = FileUtils.readFileToString(new File(RUANKAO_SIMPLE), StandardCharsets.UTF_8);
        exams = JSONArray.parseArray(simpleJson, Exam.class);
    }

    public Optional<Question> getRandomQuestion() {
        Optional<Exam> examOptional = getRandomExam();
        if (examOptional.isPresent()) {
            List<Question> questions = examOptional.get().questions;
            return Optional.ofNullable(questions.get((int) (Math.random() * questions.size())));
        }
        return Optional.empty();
    }

    public Optional<Question> getRandomQuestion(Exam exam) {
        return Optional.ofNullable(exam.questions.get((int) (Math.random() * exam.questions.size())));
    }

    public Optional<Exam> getRandomExam() {
        return getCurrentExamIndexAt((int) (Math.random() * exams.size()));
    }

    //TODO Regex get Exam
    public Optional<Exam> getCurrentExamUseRegex(String name, String regex) {
        return Optional.empty();
    }

    public Optional<Exam> getCurrentExamUseRegex(String name) {
        return getCurrentExamUseRegex(name, DEFAULT_EXAM_REGEX);
    }

    public Optional<Exam> getCurrentExamByName(String name) {
        for (Exam exam : exams) {
            if(exam.name.equals(name)) {
                return Optional.of(exam);
            }
        }
        return Optional.empty();
    }

    public Optional<Exam> getCurrentExamIndexAt(int index) {
        return Optional.ofNullable(exams.get(index));
    }

    public static class Exam {

        @JSONField(name = "name")
        private String name;
        @JSONField(name = "dataList")
        private List<Question> questions;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        @Override
        public String toString() {
            return "Exam{" +
                    "name='" + name + '\'' +
                    ", questions=" + questions +
                    '}';
        }
    }

    public static class Question {
        @JSONField(name = "explain")
        private String explain;
        @JSONField(name = "tanswer")
        private List<String> tanswer;
        @JSONField(name = "tcontent")
        private String tcontent;
        @JSONField(name = "ttype")
        private Integer ttype;
        @JSONField(name = "uuid")
        private String uuid;

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public List<String> getTanswer() {
            return tanswer;
        }

        public void setTanswer(List<String> tanswer) {
            this.tanswer = tanswer;
        }

        public String getTcontent() {
            return tcontent;
        }

        public void setTcontent(String tcontent) {
            this.tcontent = tcontent;
        }

        public Integer getTtype() {
            return ttype;
        }

        public void setTtype(Integer ttype) {
            this.ttype = ttype;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public String toString() {
            return "Question{" +
                    "explain='" + explain + '\'' +
                    ", tanswer=" + tanswer +
                    ", tcontent='" + tcontent + '\'' +
                    ", ttype=" + ttype +
                    ", uuid='" + uuid + '\'' +
                    '}';
        }
    }
}
