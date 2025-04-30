class StudentInput {
    String id;
    String name;
    String type;
    String programCode;
    String section;

    public StudentInput(String id, String name, String type, String programCode, String section) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.programCode = programCode;
        this.section = section;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getProgramCode() {
        return programCode;
    }

    public String getSection() {
        return section;
    }
}
