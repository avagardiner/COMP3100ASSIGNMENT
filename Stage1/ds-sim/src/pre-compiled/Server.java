   package pre-compiled;
   
   public Server(int id, int cpuCount, int availableJobs, int jobLimit, double currentLoad) {
        this.id = id;
        this.cpuCount = cpuCount;
        this.availableJobs = availableJobs;
        this.jobLimit = jobLimit;
        this.currentLoad = currentLoad;
    }

    public int getServerId() {
        return id;
    }

    public int getAvailableCPUs() {
        return cpuCount;
    }

    public int getAvailableJobs() {
        return availableJobs;
    }

    public int getJobLimit() {
        return jobLimit;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public void setAvailableJobs(int availableJobs) {
        this.availableJobs = availableJobs;
    }

    public void setCurrentLoad(double currentLoad) {
        this.currentLoad = currentLoad;
    }
