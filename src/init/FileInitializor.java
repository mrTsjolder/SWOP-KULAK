package init;

/**
 * This class reads input from a filereader to initialize a projectcontainer
 * with the appropriate data.
 *
 * @author Frederic, Mathias, Pieter-Jan
 */
import domain.BranchOffice;
import domain.Company;
import domain.Project;
import domain.Resource;
import domain.ResourceType;
import domain.task.Task;
import domain.time.Clock;
import domain.time.Duration;
import domain.time.Timespan;
import domain.time.WorkWeekConfiguration;
import domain.user.Developer;
import domain.user.GenericUser;
import domain.user.Role;
import exception.ConflictException;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class initializes all data structures, based on an input file
 * 
 * @author Mathias
 */
public class FileInitializor extends StreamTokenizer {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Clock clock;
    private final Company company;
    private WorkWeekConfiguration dailyAvailability;

    /**
     * Initialize this ProjectConainerFileInitializor with the given reader and
     * projectcontainer
     *
     * @param r The reader to use to read in the file
     * @param clock The clock to use
     * @param company The database to initialize
     */
    public FileInitializor(Reader r, Clock clock, Company company) {
        super(r);

        this.clock = clock;
        this.company = company;
    }

    /**
     *
     * @return The next token
     * @see java.io.StreamTokenizer#nextToken()
     */
    @Override
    public int nextToken() {
        try {
            return super.nextToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void error(String msg) {
        throw new RuntimeException("Line " + lineno() + ": " + msg);
    }

    boolean isWord(String word) {
        return ttype == TT_WORD && sval.equals(word);
    }

    void expectChar(char c) {
        if (ttype != c) {
            error("'" + c + "' expected");
        }
        nextToken();
    }

    void expectLabel(String name) {
        if (!isWord(name)) {
            error("Keyword '" + name + "' expected");
        }
        nextToken();
        expectChar(':');
    }

    LocalDateTime expectDateField(String label) {
        String date = expectStringField(label);
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    LocalTime expectTimeField(String label) {
        String date = expectStringField(label);
        return LocalTime.parse(date, timeFormatter);
    }

    String expectStringField(String label) {
        expectLabel(label);
        if (ttype != '"') {
            error("String expected");
        }
        String value = sval;
        nextToken();
        return value;
    }

    int expectInt() {
        if (ttype != TT_NUMBER || nval != (double) (int) nval) {
            error("Integer expected");
        }
        int value = (int) nval;
        nextToken();
        return value;
    }

    int expectIntField(String label) {
        expectLabel(label);
        return expectInt();
    }

    List<Integer> expectIntList() {
        ArrayList<Integer> list = new ArrayList<>();
        expectChar('[');
        while (ttype == TT_NUMBER) {
            list.add(expectInt());
            if (ttype == ',') {
                expectChar(',');
            } else if (ttype != ']') {
                error("']' (end of list) or ',' (new list item) expected");
            }
        }
        expectChar(']');
        return list;
    }

    public class IntPair {

        public int first;
        public int second;
    }

    List<IntPair> expectLabeledPairList(String first, String second) {
        ArrayList<IntPair> list = new ArrayList<>();
        expectChar('[');
        while (ttype == '{') {
            if (ttype == '{') {
                expectChar('{');
                int f = expectIntField(first);
                expectChar(',');
                int s = expectIntField(second);
                expectChar('}');
                IntPair p = new IntPair();
                p.first = f;
                p.second = s;
                list.add(p);
            }
            if (ttype == ',') {
                expectChar(',');
            } else if (ttype != ']') {
                error("']' (end of list) or ',' (new list item) expected");
            }
        }
        expectChar(']');
        return list;
    }

    /**
     * Processes the input file and inits all necessary structures
     */
    public void processFile() throws ConflictException {
        slashSlashComments(false);
        slashStarComments(false);
        ordinaryChar('/'); // otherwise "//" keeps treated as comments.
        commentChar('#');

        nextToken();

        LocalDateTime systemTime = expectDateField("systemTime");

        clock.advanceTime(systemTime);

        expectLabel("dailyAvailability");
        while (ttype == '-') {
            expectChar('-');
            LocalTime creationTime = expectTimeField("startTime");
            LocalTime dueTime = expectTimeField("endTime");
            dailyAvailability = new WorkWeekConfiguration(creationTime, dueTime);
        }

        expectLabel("offices");

        while (ttype == '-') {
            expectChar('-');
            String location = expectStringField("location");

            company.addOffice(new BranchOffice(location));
        }

        /**
         * Resourcetypes
         */
        expectLabel("resourceTypes");
        List<List<ResourceType>> reqList = new ArrayList<>();
        List<List<Integer>> reqIntList = new ArrayList<>();
        List<List<ResourceType>> conflictList = new ArrayList<>();
        List<List<Integer>> conflictIntList = new ArrayList<>();
        
        while (ttype == '-') {
            expectChar('-');
            String name = expectStringField("name");
            expectLabel("requires");
            List<Integer> requirementIds = expectIntList();
            expectLabel("conflictsWith");
            List<Integer> conflictIds = expectIntList();
            expectLabel("dailyAvailability");
            int availabilityIndex = 1;
            if (ttype == TT_NUMBER) {
                availabilityIndex = expectInt();
            }

            List<ResourceType> requirements = new ArrayList<>();
            List<ResourceType> conflicts = new ArrayList<>();
            reqList.add(requirements);
            reqIntList.add(requirementIds);
            conflictList.add(conflicts);
            conflictIntList.add(conflictIds);

            ResourceType resourceType;
            if (availabilityIndex == 0) {
                resourceType = new ResourceType(name, requirements, conflicts, dailyAvailability);
            } else {
                // always available
                resourceType = new ResourceType(name, requirements, conflicts);
            }
            company.addResourceType(resourceType);

        }

        // transform ids to objects
        // this happens afterwards so a resource type can conflict with a resource type that's lower in the init file
        for (int i = 0; i < reqList.size(); i++) {
            for (Integer j : reqIntList.get(i)) {
                reqList.get(i).add(company.getResourceTypes().get(j));
            }
            for (Integer j : conflictIntList.get(i)) {
                conflictList.get(i).add(company.getResourceTypes().get(j));
            }
        }
        
        expectLabel("resources");
        List<Resource> tempList = new ArrayList<>();
        
        while (ttype == '-') {
            expectChar('-');
            String name = expectStringField("name");
            expectLabel("type");
            int typeIndex = expectInt();
            expectLabel("office");
            int officeId = expectInt();
            // create and add resourcetype
            Resource res = company.getOffices().get(officeId).getResourceContainer().createResource(name, company.getResourceTypes().get(typeIndex));

            // add to tempory list
            tempList.add(res);
        }

        expectLabel("managers");

        while (ttype == '-') {
            expectChar('-');
            String name = expectStringField("name");
            int officeId = expectIntField("office");
            GenericUser manager = new GenericUser(name, Role.MANAGER, company.getOffices().get(officeId));
            company.getOffices().get(officeId).addUser(manager);
        }

        /**
         * Developers
         */
        expectLabel("developers");
        // new resourcetype for developers
        ArrayList<Resource> devList = new ArrayList<>();
        ResourceType devType = ResourceType.DEVELOPER;
        company.addResourceType(devType);
        
        while (ttype == '-') {
            expectChar('-');
            String name = expectStringField("name");
            int officeId = expectIntField("office");
            Developer dev = new Developer(name, company.getOffices().get(officeId));
            
            company.getOffices().get(officeId).addUser(dev);
            company.getOffices().get(officeId).getResourceContainer().addResource(dev);
            // add to temp list
            devList.add(dev);
            
            // developer is user and resource at the same time
            
            tempList.add(dev);
        }

        /**
         * Projects
         */
        expectLabel("projects");
        HashMap<Project, BranchOffice> projectOffice = new HashMap<>();
        List<Project> tempProjects = new ArrayList<>();
        
        while (ttype == '-') {
            expectChar('-');
            String name = expectStringField("name");
            String description = expectStringField("description");
            LocalDateTime creationTime = expectDateField("creationTime");
            LocalDateTime dueTime = expectDateField("dueTime");
            int officeId = expectIntField("office");

            BranchOffice office = company.getOffices().get(officeId);
            Project project = office.getProjectContainer().createProject(name, description, creationTime, dueTime);
            projectOffice.put(project, office);
            tempProjects.add(project);
        }

        /**
         * Tasks
         */
        expectLabel("tasks");
        
        while (ttype == '-') {
            expectChar('-');
            
            int projectId = expectIntField("project");
            String description = expectStringField("description");

            int estimatedDuration = expectIntField("estimatedDuration");
            int acceptableDeviation = expectIntField("acceptableDeviation");
            int alternativeFor = Project.NO_ALTERNATIVE;
            expectLabel("alternativeFor");
            if (ttype == TT_NUMBER) {
                alternativeFor = expectInt();
            }
            List<Integer> prerequisiteTasks = new ArrayList<>();
            expectLabel("prerequisiteTasks");
            if (ttype == '[') {
                prerequisiteTasks = expectIntList();
            }

            if (prerequisiteTasks.isEmpty()) {
                prerequisiteTasks = Project.NO_DEPENDENCIES;
            }

            Duration duration = new Duration(estimatedDuration);

            expectLabel("resources");
            List<IntPair> requirements = expectLabeledPairList("type", "quantity");
            HashMap<ResourceType, Integer> resourceMap = new LinkedHashMap<>();
            
            for (IntPair pair : requirements) {
                resourceMap.put(company.getResourceTypes().get(pair.first), pair.second);
            }

            Task task;
            task = tempProjects.get(projectId).createTask(description, duration, acceptableDeviation, alternativeFor, prerequisiteTasks, resourceMap);
             
            expectLabel("delegatedTo");
            if (ttype == TT_NUMBER) {
                int delegatedOfficeId = expectInt();
                projectOffice.get(task.getProject()).delegateTaskTo(task, company.getOffices().get(delegatedOfficeId));
            }
           
            int planning = expectIntField("planned");
           
            if (planning == 1) {
                LocalDateTime plannedStartTime = expectDateField("plannedStartTime");
                expectLabel("developers");
                List<Integer> developers = expectIntList();
                // transform ids to objects
                ArrayList<Resource> resources = new ArrayList<>();
                
                for (int id : developers) {
                    resources.add(devList.get(id));
                }
                
                expectLabel("resources");
                List<Integer> resourcesIds = new ArrayList<>();
                if (ttype == '[') {
                    resourcesIds = expectIntList();
                }
                // add rest of resources
                for (int id : resourcesIds) {
                    resources.add(tempList.get(id));
                }
                
                task.plan(plannedStartTime, resources, new Clock(plannedStartTime));
            }
            
            expectLabel("status");
            
            String status = "";
            if (isWord("finished")) {
                nextToken();
                
                status = "finished";
            } else if (isWord("failed")) {
                nextToken();
                status = "failed";
            }
           
            if (isWord("executing")) {
                nextToken();
                status = "executing";
            }
            if (!"".equals(status) && !(status.equalsIgnoreCase("executing"))) {
                LocalDateTime startTime = expectDateField("startTime");
                LocalDateTime endTime = expectDateField("endTime");

                if (status.equalsIgnoreCase("failed")) {
                    task.fail(new Timespan(startTime, endTime), clock.getTime());
                } else if (status.equalsIgnoreCase("finished")) {
                    task.execute(clock);
                    task.finish(new Timespan(startTime, endTime), clock.getTime());
                }

            }
        }

        if (ttype != TT_EOF) {
            error("End of file or '-' expected");
        }
    }
}
