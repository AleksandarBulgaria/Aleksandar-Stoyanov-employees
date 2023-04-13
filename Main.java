import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String csvFile = "input.csv";
        String line = "";
        String cvsSplitBy = ",";
        Map<String, Map<String, Project>> employeeProjects = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                String empID = data[0];
                String projectID = data[1];
                LocalDate dateFrom = LocalDate.parse(data[2]);
                LocalDate dateTo = data[3] != null && !"NULL".equals(data[3]) ? LocalDate.parse(data[3]) : LocalDate.now();
                Map<String, Project> projects = employeeProjects.getOrDefault(empID, new HashMap<>());
                if (projects.containsKey(projectID)) {
                    Project project = projects.get(projectID);
                    project.dateFrom = project.dateFrom.isBefore(dateFrom) ? project.dateFrom : dateFrom;
                    project.dateTo = project.dateTo.isAfter(dateTo) ? project.dateTo : dateTo;
                } else {
                    projects.put(projectID, new Project(dateFrom, dateTo));
                }
                employeeProjects.put(empID, projects);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String emp1 = null;
        String emp2 = null;
        long maxDuration = 0;
        for (Map.Entry<String, Map<String, Project>> entry1 : employeeProjects.entrySet()) {
            for (Map.Entry<String, Project> entry2 : entry1.getValue().entrySet()) {
                LocalDate projectDateFrom = entry2.getValue().dateFrom;
                LocalDate projectDateTo = entry2.getValue().dateTo;
                for (Map.Entry<String, Map<String, Project>> entry3 : employeeProjects.entrySet()) {
                    if (!entry1.getKey().equals(entry3.getKey())) {
                        Project project = entry3.getValue().get(entry2.getKey());
                        if (project != null) {
                            LocalDate dateFrom = project.dateFrom.isAfter(projectDateFrom) ? project.dateFrom : projectDateFrom;
                            LocalDate dateTo = project.dateTo.isBefore(projectDateTo) ? project.dateTo : projectDateTo;
                            long duration = ChronoUnit.DAYS.between(dateFrom, dateTo);
                            if (duration > maxDuration) {
                                maxDuration = duration;
                                emp1 = entry1.getKey();
                                emp2 = entry3.getKey();
                            }
                        }
                    }
                }
            }
        }

        System.out.println(emp1 + ", " + emp2 + ", " + maxDuration);
    }

    private static class Project {
        private LocalDate dateFrom;
        private LocalDate dateTo;

        public Project(LocalDate dateFrom, LocalDate dateTo) {
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
        }
    }
}
