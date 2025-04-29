package examseatingsystem;

import java.io.IOException;

public class ExamSeatingSystem {
    public static void main(String[] args) {
        ExamSeatingModel model = new ExamSeatingModel();
        ExamSeatingView view = new ExamSeatingView();
        ExamSeatingController controller = new ExamSeatingController(model, view);

        try {
            String directoryPath = "C:\\Users\\Zarra\\IdeaProjects\\OOPFinalProject\\OOPFinalProject\\src\\examseatingsystem\\resources";
            model.loadData(directoryPath);
            controller.start();
        } catch (IOException e) {
            view.showError("Error loading data: " + e.getMessage());
        }
    }
}
